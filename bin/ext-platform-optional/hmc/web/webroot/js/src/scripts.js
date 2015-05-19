// global variables


var ie5 = document.all && document.getElementById;

var exit = false;		// is used as a marker in wizards to prevent the user from closing the window
var theSubmitEventID;	// see checkforenter()
var tinymceIsLoaded = false;
var theEditorForm = null;	// will later on contain the main <form> element of the frame.jsp
var theBody = null;			// will later on contain the document's <body> element

/* ----------- keyboard ------------------------------------------------------- */

	var keyEventMapping = new Object();
	var keyFunctionMapping = new Object();
	var accessKeys = new Array();
	
	function keyHandler(event)
	{
		var e = event ? event : window.event;	
		var keyCode = e.keyCode;

		if( keyCode < 32 )
		{
			return;
		}
		
		if( e.altKey || (BrowserDetect.OS == "Mac" && BrowserDetect.browser == "Firefox" && e.ctrlKey ) )	// special treatment for firefox running on mac -> using <ctrl> instead of <alt>
		{		
			if( keyEventMapping[keyCode] )
			{
				setEvent(keyEventMapping[keyCode], "true");
				
				setScrollAndSubmit(); 
				return false;
			}
			else if( keyFunctionMapping[keyCode] )
			{
				keyFunctionMapping[keyCode]();
				return false;
			}
		}
	}
	
	function addKeyEvent(key, event, overrideAccessKey)
	{
		keyEventMapping[key.toUpperCase().charCodeAt(0)] = event;
		if( overrideAccessKey == null || overrideAccessKey )
		{
			accessKeys.push(key);
		}
	}
	
	function addKeyFunction(key, func, overrideAccessKey)
	{
		keyFunctionMapping[key.toUpperCase().charCodeAt(0)] = func;
		if( overrideAccessKey == null || overrideAccessKey )
		{
			accessKeys.push(key);
		}
	}

	// register key handler		
	document.onkeydown = keyHandler;
	
	// returns true if the pressed key was <enter> (does not call a submit on its own, see checkForEnter()
	function isEnter(event)
	{
		return (event ? event : window.event).keyCode == 13;
	}

	// checks for enter and optionally sets the event 'theSubmitEventID' to true and submits the form
	function checkForEnter(event)
	{
		var keycode = (event ? event : window.event).keyCode;
		
		if( keycode == 13 )
		{
			if( theSubmitEventID && theSubmitEventID.length > 0 )
			{
				// set submit event value to 'true' and submit the form
				setEvent(theSubmitEventID, "true");
			}
			document.editorForm.onsubmit();
			document.editorForm.submit();
			return false;
		}
		else
		{
			return true;
		}
	}

	// returns true if the pressed key was F6
	function checkForF6(event)
	{
		return (event ? event : window.event).keyCode == 117;
	}

	// returns true if the presses key was F5
	function checkForF5(event)
	{
		return (event ? event : window.event).keyCode == 116;
	}

/* --------------- positioning ------------------------------------------------- */
	
	function getResultListHeight(percent)
	{
		return ((ie5 ? document.body.offsetHeight : window.innerHeight) * percent) + "px";
	}

	function getResultListHeightAbsolute(abs, min, max)
	{
		var height = (ie5 ? document.body.offsetHeight : window.innerHeight) - abs;
		
		if( height < min )
		{
			height = min;
		}
		if( height > max )
		{
			height = max;
		}
		
		return height + "px";
	}

/* --------------- wait layer (hourglass effect) ------------------------------------------------- */

	var waitOpacity = 0;
	
	function showWaitIcon()
	{	
		y_disableClicking();

		window.setTimeout(showRunningClock, 2000);
	}

	function showRunningClock()
	{
		var waitSymbol = document.createElement("div");
		var waitStyle = waitSymbol.style;

		waitStyle.visibility = "hidden";
		waitStyle.position = "absolute";
		waitStyle.top = -1000;
		waitStyle.left = -1000;
		waitStyle.zIndex = "1001";
				
		document.body.appendChild(waitSymbol);

		waitSymbol.innerHTML = "<img class=\"hmc_waiticon\" src=\"images/icons/waiticon.gif\"/>";

		waitStyle.left = ((ie5 ? document.body.clientWidth : window.innerWidth) - waitSymbol.offsetWidth) / 2;
		waitStyle.top = ((ie5 ? document.body.clientHeight : window.innerHeight) - waitSymbol.offsetHeight) / 2;

		waitStyle.visibility = "visible";
	}

/* --------------- general ------------------------------------------------- */

	function swapImage(id, img)
	{
		$(id).src = img;
	}

	function open_window(URL, target, windowtitle)
	{
		fenster1 = window.open(URL, target);
		fenster1.focus();
	
		// following is a little hack to prevent javascript failures due to bug #3318
		call = "try { if( fenster1 ) { fenster1.document.title = \"" + windowtitle + "\"; } } catch(e) {}";
		setTimeout( call, 1000 ); //1 second
	}

	function preloadImages()
	{
		document.Preload = new Array();
		
		if( document.images )
		{
			for(var i = 0; i < preloadImages.arguments.length; i++)
			{
				document.Preload[i] = new Image();
				document.Preload[i].src = preloadImages.arguments[i];
			}
		}
	}

	function setEvent(eventName, value)
	{
		var inputElement;
		
		if( value == null )
		{
			value = "true";
		}
		
		if( document.editorForm.elements[eventName] )
		{
			// there is already an input element for the event, use it
			inputElement = document.editorForm.elements[eventName];
		}
		else
		{
			// create a new hidden input element for the event and set its value
			inputElement = document.createElement("input");
		
			inputElement.name = eventName;
			inputElement.type = "hidden";
			
			document.editorForm.appendChild(inputElement);
		}
		
		inputElement.value = value;
	}

	function getParentByTagName(element, name)
	{
		if( element && element.nodeName != name )
		{
			return getParentByTagName(element.parentNode, name);
		}
		
		return element;
	}

	function y_doubleClickEvent(element)
	{	
		var eventID = y_getEventIDFromClassNames(element);
		if( eventID != null )
		{
			setEvent(eventID);
			setScrollAndSubmit();
			return false;
		}
		return false;
	}
	
	function y_disableTinyMCEEditor(inst)
	{
		try
		{
			var element = document.getElementById(inst.editorId + "_parent").getElementsByTagName("table")[0];
			
			var div = document.createElement("div");
			
			div.style.filter = "alpha(opacity=50)";
			div.style.opacity = "0.5";
			div.style.position = "absolute";		
			div.style.left = Position.cumulativeOffset(element)[0];
			div.style.top = Position.cumulativeOffset(element)[1];
			div.style.width = Element.getDimensions(element).width;
			div.style.height = Element.getDimensions(element).height;
			div.style.backgroundColor = "#ffffff";
	
			document.body.appendChild(div);

			element.onfocus = function()
									{
										this.blur();
									};
		}
		catch(e) {}
	}
	
	function y_getEventIDFromClassNames(element)
	{
		return Element.classNames(element).detect(function(value) { return value.substr(0, 2) == "MC"; });
	}

	function y_openURL(element)
	{
		// find url
		var url = element.name;
		var title = "Preview";
		if( element.title )
		{
			title = element.title;
		}
		
		open_window(url, '_blank', title);
		return false;
	};

	function y_chipEvent(element)
	{
		var useStructureEditorHack = false;
	
		// find the appropriate chip event value which must be given as an element with classname 'event-value' within the <a> element
		var valueElement = domQuery(".event-value", element)[0];
		var value = (valueElement != null && valueElement.firstChild != null && valueElement.firstChild.nodeValue.strip().length > 0) ? valueElement.firstChild.nodeValue.strip() : "true"; 
		
		// collect confirmation messages, if there are any
		var confirmNodes = domQuery(".confirm-message", element);
		
		for( i = 0; i < confirmNodes.length; i++ )
		{
			if(	confirmNodes[i].firstChild != null 
				&& confirmNodes[i].firstChild.nodeValue != null
				&& confirmNodes[i].firstChild.nodeValue.strip().length > 0
				&& !confirm(decodeURI(confirmNodes[i].firstChild.nodeValue.strip())) )
			{
				// return if there is a message which is not confirmed
				return false;
			}
		}
	
		// look for structure editor hack (content of the structure editor's giant textarea is deleted before 
		// submitting the form to reduce response time. if the hack is used, it is not deleted. is used only by the
		// buttons within the structure editor)
		if( domQuery(".structure_editor_hack", element).length > 0 )
		{
			// hack is found
			useStructureEditorHack = true;
		}
	
		// add hidden input element, set its value to the chip event value and submit the form
		setEvent(element.name, value);
		setScrollAndSubmit(useStructureEditorHack);
	
		return false;
	}
	
	function y_languageToggle(element)
	{
		var chipID = element.name;
		var updateElement = $(chipID + "_updater").parentNode;
		var tenantID = domQuery("div.tenantIDStr", element)[0].firstChild;
		var tenantIDStr =  tenantID != null ? tenantID.nodeValue.strip() : "";
							 
		// find all <input> elements beneath updateelement
		var inputElements = domQuery("input,textarea,select", updateElement);
		var thePostBody = "chip-id=" + chipID + "&toggle=true";
		var l = inputElements.length;
		for( i = 0; i < l; i++ )
		{
			var el = inputElements[i];
			var nodeName = el.nodeName.toLowerCase();
			var name, value;

			if( nodeName == "input" )
			{
				var type = el.type.toLowerCase();

				if( type == "text" || type == "password" || type == "hidden" 		// normal text elements
					|| ((type == "radio" || type == "checkbox") && el.checked) )	// boolean elements
				{
					name = el.name;
					value = el.value;
				}
			}
			else if( nodeName == "select" || nodeName == "textarea" )
			{
				name = el.name;
				value = el.value;
			}

			if( name != null && value != null )
			{
				thePostBody += "&" + encodeURIComponent(name) + "=" + encodeURIComponent(value);
			}
		}

		var options = { evalScripts: true,
							 onComplete: function() { Behaviour.applyFromElement(updateElement); },
							 postBody : thePostBody };

		new Ajax.Updater(updateElement, 'prototype'+tenantIDStr, options);
		return false;
	}

	function y_addContextMenuToDisabledSelect(selectElement)
	{
		if( element.nodeName.toLowerCase() == "select" && element.oncontextmenu != null )												
		{
			var div = document.createElement("div");
			div.style.cssText = "position:absolute; width:" 
										+ Element.getWidth(element) + ";height:" 
										+ Element.getHeight(element) + "; left:" 
										+ Position.cumulativeOffset(element)[0] 
										+ ";top:" + Position.cumulativeOffset(element)[1] + ";";
			div.oncontextmenu = element.oncontextmenu;
			document.body.appendChild(div);
		}		
	}

	function y_disableClicking()
	{
		var innerWidth = document.body.scrollWidth;
		var innerHeight = document.body.scrollHeight;

		var unclickableDiv = document.createElement("div");
		var unclickableDivStyle = unclickableDiv.style;
		unclickableDiv.setAttribute("id", "y_unclickablediv");

		unclickableDivStyle.cssText = "position:absolute; top:0; left:0; zIndex:1000; width:" + innerWidth + "; height:" + innerHeight + ";filter = alpha(opacity=0);opacity:0;";
		unclickableDiv.innerHTML = "<div style='width:100%; height:100%; filter = alpha(opacity=0); opacity:0;'>&nbsp;</div>";

		document.body.appendChild(unclickableDiv);
		unclickableDivStyle.backgroundColor = "#e1e1e1";
	}
	
	function y_enableClicking()
	{
		Element.remove("y_unclickablediv");
	}

/* ----------- error and info messages -------------------------- */

	function y_showPopUpMessage(message, title, buttonName, type)
	{
		y_disableClicking();
		var messageDiv = document.createElement("div");
		messageDiv.setAttribute("id", "y_popupmessage");
		Element.addClassName(messageDiv, "hidden");
		Element.addClassName(messageDiv, "popupmessage-" + type);
		document.body.appendChild(messageDiv);

		var messageHTML = "<table cellspacing='0' cellpadding='0'>";
		messageHTML += "<tr><th colspan='2' class='title'>" + title + "</th></tr>";
		messageHTML += "<tr>";
		messageHTML += "<td class='icon'><img src='images/icons/" + type + "_32x32.gif'/></td>";
		messageHTML += "<td class='text'>" + message + "</td></tr>";
		messageHTML += "<tr><td colspan='2' class='button'>";
		messageHTML += "<div class='xp-button' style='padding-left:210px;'>";
		messageHTML += "<a href='#' hidefocus='true' id='y_popupmessage_ok_a' onclick='y_removePopUpMessage()'>";
		messageHTML += "<span class='label' id='y_popupmessage_ok_label'>" + buttonName + "</span>";
		messageHTML += "</a></div></td>";
		messageHTML += "</tr></table>";
		
		messageDiv.innerHTML = messageHTML;

		var messageStyle = messageDiv.style;
		//messageStyle.zIndex = "10";

		new Draggable(messageDiv, { starteffect: '', endeffect: '', handle: 'title' });
				
		Element.removeClassName(messageDiv, "hidden");

		messageStyle.left = ((ie5 ? document.body.clientWidth : window.innerWidth) - messageDiv.offsetWidth) / 2;
		messageStyle.top = ((ie5 ? document.body.clientHeight : window.innerHeight) - messageDiv.offsetHeight) / 2;		

		// create underlying iframe (if necessary) to overcome the 'select box is on top of all other elements' bug in the ie6
		var iframe = y_createUnderlyingIframe(messageDiv);
		if( iframe != null )
		{
			iframe.setAttribute("id", "y_popupmessage_iframe");
			iframe.style.visibility = "visible";
		}
		
		$("y_popupmessage_ok_a").focus();
	}

	function y_removePopUpMessage()
	{
		Element.remove("y_popupmessage");
		if( $("y_popupmessage_iframe") != null )
		{
			Element.remove("y_popupmessage_iframe");
		}
		
		y_enableClicking();
	}

	function y_showErrorMessage(message, title, buttonName)
	{
		if( title == null )
		{
			title = "Error";
		}
		
		if( buttonName == null )
		{
			buttonName = "OK";
		}
		
		y_showPopUpMessage(message, title, buttonName, "error");
	}

	function y_showInfoMessage(message, title, buttonName)
	{
		if( title == null )
		{
			title = "Info";
		}

		if( buttonName == null )
		{
			buttonName = "OK";
		}
		
		y_showPopUpMessage(message, title, buttonName, "info");
	}

/* ----------- blinking ----------------------------------------- */

	var blinkElements = new Array();
	
	function doBlink()
	{
		for( x in blinkElements )
		{
			if( blinkElements[x].style )
			{
				blinkElements[x].style.visibility = blinkElements[x].style.visibility == "" ? "hidden" : "";
			}
		}
	
		setTimeout(doBlink, 1000);
	}
	
	function addBlink(element)
	{
		blinkElements.push(element);
	}
	
	
	// start 'blinking process'
	doBlink();
	

/* ----------- select box clipping ----------------------------------------- */
	window.onresize = repositionClips;

	clipCount = 0;
	clipSizes = new Array();

	// clip given element by given size (the element had to be marked previously with markClipStart() and markClipEnd()
	function clip(element, size)
	{
		p = element.parentNode;

		width = element.offsetWidth;
		height = element.offsetHeight;
		x = Position.cumulativeOffset(element)[0];
		y = Position.cumulativeOffset(element)[1];

		parentWidth = p.offsetWidth;
		parentHeight = p.offsetHeight;

		element.style.position = "absolute";
		element.style.top = y;
		element.style.left = x;
		element.style.clip = "rect(" + size + "px " + (width - size) + "px " + (height - size) + "px " + size + "px)";

		p.style.width = parentWidth;
		p.style.height = parentHeight;
	}

	// puts a starting mark to the current position within the html document, the element will be clipped by 'size' points,
	// don't forget the ending mark!!
	function markClipStart(size)
	{
		document.write('<div id="clipthis' + clipCount + '" style="white-space:nowrap;">');
		clipSizes[clipCount] = size;
		clipCount++;
	}

	// puts an ending mark to the current position within the html document
	function markClipEnd()
	{
		document.write('</div>');
	}

	// clips all marked elements by the appropriate sizes
	function clipAll()
	{
		if( !ie5 ) 
		{
			return;
		}
	
		for( i = 0; i < clipCount; i++ )
		{
			element = document.getElementById("clipthis" + i);
			clip(element.firstChild, clipSizes[i]);
		}
	}

	// repositions the given (marked) element, is necessary after a window resize since the clipped elements are positioned absolute
	function reposition(element)
	{
		x = Position.cumulativeOffset(element)[0];
		y = Position.cumulativeOffset(element)[1];

		element.firstChild.style.left = x;
		element.firstChild.style.top = y;
	}

	// repositions all (marked) elements, is necessary after a window resize since the clipped elements are positioned absolute
	function repositionClips()
	{
		if( !ie5 ) 
		{
			return;
		}

		for( i = 0; i < clipCount; i++ )
		{
			element = document.getElementById("clipthis" + i);
			reposition(element);
		}
	}

/* ------------------ tinyMCE editor callbacks / 'is changed' handling ------- */

	var mce_isChangedElements = new Object();

	function mce_addIsChangedElement(editorID, isChangedElement)
	{
		mce_isChangedElements[editorID] = isChangedElement;
	}

	function mce_myExecCommandHandler(editor_id, elm, command)
	{
		var inst = tinyMCE.get(editor_id);
		var isChangedElement = mce_isChangedElements[inst.id];
		
		if( isChangedElement )
		{
			switch (command)
			{
				case "Undo":
					if( !inst.undoManager.hasUndo )
					{
						isChangedElement.value = false;
					}
					return false;
	
				case "Redo":
					isChangedElement.value = true;
					return false;
			}
		}
	
		return false; // Pass to next handler in chain
	}

	function mce_myOnChangeHandler(inst)
	{
		var isChangedElement = mce_isChangedElements[inst.id];
		if( isChangedElement && inst.isDirty() )
		{
			isChangedElement.value = "true";
		}
	}

/* ------------------ ajax components ---------------------------------- */

	function AutocompleterToolbarActionCallback(chipID, parameterName, tenantIDStr )
	{
		this.callback = function(inputElement, selectedListItem, autocompleteElement, sync )
		{
			var options = {	method: 'post',
									asynchronous:sync?false:true,
									postBody: 'chip-id=' + chipID + '&' + parameterName + '=' + domQuery('span.hidden', selectedListItem)[0].firstChild.nodeValue,
									onSuccess: function(t) 
													{
														// add a flag div to the given autocomplete element as to 
														// indicate that the selected value has been set (necessary for selenium tests!)
														if( autocompleteElement != null )
														{
															var flagDiv = document.createElement("div");
															flagDiv.style.display="none";
															flagDiv.setAttribute("id", autocompleteElement.getAttribute("id") + "_flag");
															autocompleteElement.appendChild(flagDiv);
														}
													},
									on404: function(t) 
													{
													   alert('Error 404: location "' + t.statusText + '" was not found.');
													},
									onFailure: function(t)
													{
														alert('Error ' + t.status + ' -- ' + t.statusText);
													}
								};
			
			new Ajax.Request('prototype'+tenantIDStr, options);
		}
	}

/* ------------------ popup editor ------------------------------------- */

	function y_togglePopupEditor(element)
	{
		// find pop-up element
		var popupElement = domQuery(".popup-element", element.parentNode)[0];
	
		if( popupElement.style.display == "block" )
		{
			// hide popup-element
			popupElement.style.display = "none";
		}
		else
		{
			// show popup-element
			popupElement.style.display = "block";
		
			// position element (next to the button, vertically aligned)
			var left = Position.cumulativeOffset(element)[0] + Element.getDimensions(element).width + 2;
			var top = Position.cumulativeOffset(element)[1] + (Element.getDimensions(element).height / 2) - (Element.getDimensions(popupElement).height / 2);
		
			popupElement.style.left = left;
			popupElement.style.top = top;
		}
	}

/* --------------------- selenium tests ------------------------------- */

	function y_findElement(uniqueName, eventName, theDocument)
	{
		if( !theDocument.getElementsByName )
		{
			theDocument = document;
		}
		
		// find <div> with the given unique name
		var uniqueNameDiv = theDocument.getElementsByName(uniqueName)[0];
	
		// get chip id from the enclosing div's name attribute
		var chipID = uniqueNameDiv.parentNode.getAttribute("name");
		
		// chipID + underscore + event name
		var result = theDocument.getElementsByName(chipID + "_" + eventName)[0];
		
		return result;
	}

/* ------------------ (explorer) tree ---------------------------------- */

	function y_toggleTreeNode(element)
	{
		var iconElement = domQuery("div.icon", element)[0];
		var chipID = domQuery("div.chip-id", element)[0].firstChild.nodeValue.strip();
		var tenantID = domQuery("div.tenantIDStr", element)[0].firstChild;
		var tenantIDStr =  tenantID != null ? tenantID.nodeValue.strip() : "";
		var fadeIn = false;
		var updateElement = $(chipID + "_updater");
		var behaviourUpdateElement = getParentByTagName(updateElement, "TR");

		if( updateElement != null )
		{
			if( updateElement.hasClassName("update-parent") )
			{
				updateElement = updateElement.parentNode;
			}

			if( iconElement.className.indexOf("plus") > -1 )
			{
				// show wait message
				updateElement.innerHTML += "<div class=\"tree-wait-message\">"
																  + "loading..."
																  + "</div>";
			}

			new Ajax.Updater(updateElement, 'prototype'+tenantIDStr+'?chip-id=' + chipID + '&toggle=true', { evalScripts: true, onComplete: function() { y_displayTreeElement(iconElement, behaviourUpdateElement); } });
		}
	}
	
	function y_displayTreeElement(iconElement, behaviourUpdateElement) 		//, chipID)
	{	
		y_toggleTreeIcon(iconElement);

		Behaviour.applyFromElement(behaviourUpdateElement);
	}


	function y_toggleTreeIcon(iconElement)
	{
			if( $(iconElement).hasClassName("plus") )
			{
				$(iconElement).removeClassName("plus");
				$(iconElement).addClassName("minus");
			}
			else if( $(iconElement).hasClassName("plus-begin") )
			{
				$(iconElement).removeClassName("plus-begin");
				$(iconElement).addClassName("minus-begin");
			}
			else if( $(iconElement).hasClassName("plus-end") )
			{
				$(iconElement).removeClassName("plus-end");
				$(iconElement).addClassName("minus-end");
			}
			else if( $(iconElement).hasClassName("minus") )
			{
				$(iconElement).removeClassName("minus");
				$(iconElement).addClassName("plus");
			}
			else if( $(iconElement).hasClassName("minus-begin") )
			{
				$(iconElement).removeClassName("minus-begin");
				$(iconElement).addClassName("plus-begin");
			}
			else if( $(iconElement).hasClassName("minus-end") )
			{
				$(iconElement).removeClassName("minus-end");
				$(iconElement).addClassName("plus-end");
			}
	}

/* ----------------- browser detection ---------------------- */

	var BrowserDetect = 
	{
		init: function()
				{
					this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
					this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "an unknown version";
					this.OS = this.searchString(this.dataOS) || "an unknown OS";
				},
		
		searchString: function(data)
							{
								for( var i=0; i < data.length; i++ )
								{
									var datai = data[i];
									var dataString = datai.string;
									var dataProp = datai.prop;
									
									this.versionSearchString = datai.versionSearch || datai.identity;
									
									if( dataString )
									{
										if( dataString.indexOf(datai.subString) != -1 )
										{
											return datai.identity;
										}
									}
									else if( dataProp )
									{
										return datai.identity;
									}
								}
							},
							
		searchVersion: function(dataString)
							{
								var index = dataString.indexOf(this.versionSearchString);
								if( index == -1 )
								{
									return;
								}
								return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
							},
		
		dataBrowser: 
		[
			{ 	
				string: navigator.userAgent,
				subString: "OmniWeb",
				versionSearch: "OmniWeb/",
				identity: "OmniWeb"
			},
			{
				string: navigator.vendor,
				subString: "Apple",
				identity: "Safari"
			},
			{
				prop: window.opera,
				identity: "Opera"
			},
			{
				string: navigator.vendor,
				subString: "iCab",
				identity: "iCab"
			},
			{
				string: navigator.vendor,
				subString: "KDE",
				identity: "Konqueror"
			},
			{
				string: navigator.userAgent,
				subString: "Firefox",
				identity: "Firefox"
			},
			{
				string: navigator.vendor,
				subString: "Camino",
				identity: "Camino"
			},
			{		// for newer Netscapes (6+)
				string: navigator.userAgent,
				subString: "Netscape",
				identity: "Netscape"
			},
			{
				string: navigator.userAgent,
				subString: "MSIE",
				identity: "Explorer",
				versionSearch: "MSIE"
			},
			{
				string: navigator.userAgent,
				subString: "Gecko",
				identity: "Mozilla",
				versionSearch: "rv"
			},
			{ 		// for older Netscapes (4-)
				string: navigator.userAgent,
				subString: "Mozilla",
				identity: "Netscape",
				versionSearch: "Mozilla"
			}
		],
		
		dataOS : 
		[
			{
				string: navigator.platform,
				subString: "Win",
				identity: "Windows"
			},
			{
				string: navigator.platform,
				subString: "Mac",
				identity: "Mac"
			},
			{
				string: navigator.platform,
				subString: "Linux",
				identity: "Linux"
			}
		]
	
	};

	BrowserDetect.init();

/* -------------------------------- positioning stuff, currently used by the item history chip --------------------------------------------- */

	/*
		y_ajaxNotifyPosition() sends the upper left coordinate of the given element as an ajax request to the server.
		The request will be send to the chip with the given chip id with two parameters named 'left' and 'top'.
	*/	
	function y_ajaxNotifyPosition(element, chipID, tenantIDStr )
	{
		var options = {	method: 'post',
								postBody: 'chip-id=' + chipID + '&left=' + Position.cumulativeOffset(element)[0] + '&top=' + Position.cumulativeOffset(element)[1],
								onSuccess: function(t) 
												{
													// do nothing, this is a one-way request for now
												},
								on404: function(t) 
												{
												   alert('E' + 'rror 404: location "' + t.statusText + '" was not found.');
												},
								onFailure: function(t)
												{
													alert('E' + 'rror ' + t.status + ' -- ' + t.statusText);
												}
							};
		
		new Ajax.Request('prototype'+tenantIDStr, options);		
	}
	
	
	/*
		y_repositionElement() takes the coordinates from the element with the ID <sourceID>, 
		adds the given x- and yOffsets to them and sets these new coordinates to the element with the ID <elementID>.
		The element <elementID> better has its 'position' style set to 'absolute', of course.
		Also, if <elementID> has the classname 'hidden' set, this will be removed to make the element visible.
	*/

	function y_repositionElement(repositionElement, sourceElement, xOffset, yOffset)
	{
		var elementStyle = repositionElement.style;
		
		elementStyle.left = Position.cumulativeOffset(sourceElement)[0] + xOffset;
		elementStyle.top = Position.cumulativeOffset(sourceElement)[1] + yOffset;	

		// remove 'hidden' class to show the element
		repositionElement.removeClassName("hidden");
	}

	/**
		For the given element this function creates an (empty) iframe element which is
		exactly the same size and position as the given element.
		
		The iframe element will have a lower z-index than the given element (you might want to
		set the given element's z-index accordingly to some higher value) so that the iframe
		will be rendered below (layer-wise) the given element.
		
		The new element will be initially hidden, so you can reveal it later on together with
		your element.
		
		Background:
		The purpose of this whole procedure is to fix an IE bug (versions below 7.0 only)
		which prevents overlaying divs (or 'layers') to cover up select boxes. I.e. a select box
		would normally always be rendered on top of any other element which has been placed
		in front of the select box.
		By putting an iframe behind the overlaying element forces the IE to render the iframe
		and the overlaying element on top of the select box ;-)
		
		Because of the reasons for this hack it is only necessary to use this function for the
		IE, so if you use this for any non-IE browser, you will get 'null' as a result!  
	*/
	function y_createUnderlyingIframe(element)
	{
		var result = null;
		
		if( BrowserDetect.browser == "Explorer" && BrowserDetect.version < "7" )
		{
			if( element != null )
			{
				result = document.createElement("iframe");
				var resultStyle = result.style; 
				
				resultStyle.visibility = "hidden";
				resultStyle.filter = "alpha(opacity=0)";
			
				// exactly the same size and position as the given element
				resultStyle.position = "absolute";
				resultStyle.width = element.offsetWidth;
				resultStyle.height = element.offsetHeight;
				resultStyle.left = element.style.left;
				resultStyle.top = element.style.top;
			
			
				// behind the menu element
				if( element.style.zIndex == null || element.style.zIndex < "2")
				{
					element.style.zIndex = "1";
				}
				
				resultStyle.zIndex = element.style.zIndex - 1;
				
				// empty source file
				result.src = "js/empty_iframe.html";
				
				document.getElementsByTagName("body")[0].appendChild(result);
			}
		}
		
		return result;
	}


/* ------------------ submitting and scrolling -------------------------------------------- */

	function setScrollAndSubmit(useStructureEditorHack)
	{
		exit = false;
		setScroll();
		if( !useStructureEditorHack )
		{
			var elements = domQuery(".js_clearonsubmit");
			for( x in elements )
			{
				elements[x].value = "";
			}
		}		
				
		document.editorForm.onsubmit();
		document.editorForm.submit();

		showWaitIcon();
	}
	
	function setScroll()
	{
		setEvent("scrollx", document.body.scrollLeft);
		setEvent("scrolly", document.body.scrollTop);
	}

/* ------------------ adding onload events ---------------------------------- */

	Behaviour.addLoadEvent(function() { clipAll(); theEditorForm = document.editorForm; theBody = document.body; });
	
/* ------------------- out textbox content to iframe ------------------------ */
	function y_initIFrame(textarea, iframe)
	{
		textarea.style.display = "none";
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.write(textarea.value);
		doc.close();
	}

/* -------------- drag'n'drop test ---------------------------- */

	function y_dragListItem(draggable)
	{
		var options = {	method: 'post',
						postBody: 'chip-id=' + draggable.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.id + '&order=' + y_serializeDraggableItemList(draggable.id),
						onSuccess: function(t) 
										{
//											alert(t.responseText);
										},
						on404: function(t) 
										{
										   alert('E' + 'rror 404: location "' + t.statusText + '" was not found.');
										},
						onFailure: function(t)
										{
											alert('E' + 'rror ' + t.status + ' -- ' + t.statusText);
										}
					};
		
		new Ajax.Request('prototype', options);		
	}
		
  function y_serializeDraggableItemList(element) 
  {
    element = $(element);
    var options = Object.extend(Sortable.options(element), arguments[1] || {});
    var name = encodeURIComponent(
      (arguments[1] && arguments[1].name) ? arguments[1].name : element.id);
    
    if (options.tree) {
      return Sortable.tree(element, arguments[1]).children.map( function (item) {
        return [name + Sortable._constructIndex(item) + "=" + 
                encodeURIComponent(item.id)].concat(item.children.map(arguments.callee));
      }).flatten().join('&');
    } else {
      return Sortable.sequence(element, arguments[1]).map( function(item) {
        return encodeURIComponent(item);
      }).join(',');
    }
  }
