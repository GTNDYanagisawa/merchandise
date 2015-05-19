var currentMainMenu = null;
var idCounter = 0;
var menus = [];
var menu_timeout = null;

document.onclick = menu_hide;
document.oncontextmenu = menu_hide;
   
function menu_hide()
{
	if( currentMainMenu )
	{
		currentMainMenu.hide();
	}
}

function menu_removeTimeout()
{
	menu_timeout = null;
}

function y_getEventName(chipEvent)
{
	if( chipEvent != null && chipEvent.length > 0 && chipEvent.indexOf("_") > -1 )
	{
		return chipEvent.substr(chipEvent.indexOf("_") + 1);
	}
	else
	{
		return chipEvent;
	}
}

function y_getCoordinates(sourceElement, sourceEvent, menuElement, rightAlign)
{
	var left = 0;
	var top = 0;
	
	if( sourceElement )
	{
		// position relative to the given element (right below the element, used for dropdown menus)
		if( rightAlign )
		{
			// right-align the menu
			left = (Position.cumulativeOffset(sourceElement)[0] + Element.getDimensions(sourceElement).width) - Element.getDimensions(menuElement).width;
		}
		else
		{
			// left-align the menu
			left = Position.cumulativeOffset(sourceElement)[0];
		}
		
		top = Position.cumulativeOffset(sourceElement)[1];

		top = top + Element.getHeight(sourceElement) - 1;
	}
	else if( sourceEvent )
	{
		left = ie5 ? event.clientX + document.body.scrollLeft : sourceEvent.clientX + window.pageXOffset;
		top = ie5 ? event.clientY + document.body.scrollTop : sourceEvent.clientY + window.pageYOffset;
	}
	
	return { top: top, left: left };
}
   
/*
	The Menu object represents the "outer hull" of one menu instance.
	It contains menu and sub menu entries which will be rendered in this menu.
	So the Menu object is responsible for the actual rendering of all the DIVs, TABLEs etc. and the correct
	(absolute) positioning the whole lot.
	Also you can control the appearance of the menu (colors etc.) by changing some of the Menu member variables.
	
	Parameters:
	
	menuEntries		-	An array containing all the entries for this menu.
	sourceEvent 	-	For all non-IE browsers this is the means to provide the event that triggered the menu creation. In IE this would
							be undefined because it provides a global 'event' variable.
	sourceElement 	-	This may contain the html element which has been clicked to show this menu. If 'sourceElement' contains an element
							it will be used for a relative positioning of this menu below the given element. In essence this results in
							a drop down menu.
	title				-	If title contains a string it will be shown as the menu's title.
	options			-	An associative array which may contain one or more parameter with which you can alter the menu's look.
*/
function Menu(menuEntries, sourceEvent, sourceElement, title, options)
{
	if( options == null )
	{
		// create an empty array to make things easier during the following parameter settings
		options = { };
	}
	
	// options
	this.width = options.width != null ? options.width : "150px";														// width of the menu
	this.rightAlign = options.rightAlign != null ? options.rightAlign : false;										// set this to true to get a right-aligned menu (default is left-aligned)
	this.className = options.className != null ? options.className : "dropdown-main";							// name of the css class to use for this menu
	this.uniqueName = options.uniqueName;																						// the uniqueName (of the parent's webhchip) should help for selenium recordings (making the clickable entries uniquely identifyable)
		
	this.menuEntries = menuEntries;			// these are the menu entry objects
	this.title = title;							// the title of this menu (may be null or empty)
														
	this.theMenuElement = null;				// this will eventually contain the actual <div> element containing the menu which is later shown on screen
	this.theIframeElement = null;				// this will contain the iframe element which is used as a hack to show this menu on top of select boxes
	this.canRemove = false;						// this is used to decide if the menu may be removed in hide() because the onclick event handler is used
														// to remove the menu, but it is also invoked when showing the menu initially

	this.subMenus = new Array();				// will contain all sub menu objects
	this.ID = idCounter++;						// is used to put all (sub-)menus into an array to have a unique reference using an index

	
	this.sourceEvent = sourceEvent;			// contains the source event, see parameter description above
	this.sourceElement = sourceElement;		// contains the source element, see parameter description above

	/*
		createMenu() creates the actual div which contains the menu shown on the sceen.
		
		Parameters:
		
		sourceEvent 	-	For all non-IE browsers this is the means to provide the event that triggered the menu creation. In IE this would
								be undefined because it provides a global 'event' variable.
		sourceElement 	-	This may contain the html element which has been clicked to show this menu. If 'sourceElement' contains an element
								it will be used for a relative positioning of this menu below the given element. In essence this results in
								a drop down menu.
	*/
	this.createMenu = function(sourceEvent, sourceElement)
		{
			var menuElement;
			var menuHTML = "";
			var entries = this.menuEntries;
				
			menuHTML += "<table style=\"width: " + this.width + ";\">";
			if( this.title && this.title != "" )
			{
				// show menu title
				menuHTML += "<tr><td class=\"title\">" + this.title + "</td></tr>";
			}
		
			for( var i = 0; i < entries.length; i++ )
			{
				var isSplitter = entries[i].isSplitter;
				var showIcon = entries[i].hasIcon();
				var icon = showIcon ? entries[i].icon : null;
				var disabled = !entries[i].enabled;
				var hasSubMenu = entries[i].hasSubMenu();
				var bottomAlign = entries[i].isSubMenuBottomAlign;
				var hasConfirmMessage = entries[i].hasConfirmMessage();
				var confirmMessage = entries[i].confirmMessage;
				var entryEvent = entries[i].entryEvent;
				var entryValue = entries[i].entryValue;
				var entryName = entries[i].name;
				var elementID = (this.uniqueName != null ? this.uniqueName : "") + "_" + y_getEventName(entryEvent) + "_" + entryValue;

				// create sub menu if necessary
				var subMenuID = -1;
				if( hasSubMenu )
				{
					var subMenu = this.createSubMenu(entries[i].menuEntries, bottomAlign);
					subMenuID = subMenu.ID;
				}

				if( !disabled )
				{
					menuHTML += "<tr";
					menuHTML += " id=\"" + elementID + "_tr\"";
					menuHTML += " onmouseover=\"Element.addClassName(this, 'highlight');";
					if( hasSubMenu )
					{
						// show the selected submenu, SubMenu.show() will implicitly hide all other submenus of this menu
						menuHTML += "menus[" + subMenuID + "].show(this);";
					}
					else
					{
						// attempt to hide all submenus of this menu
						menuHTML += "menus[" + this.ID + "].hideSubMenus();";
					}
					menuHTML += "\"";
					menuHTML += " onmouseout=\"Element.removeClassName(this, 'highlight');\"";
					if( !hasSubMenu )
					{
						if( !hasConfirmMessage )
						{
							menuHTML += " onclick=\"document.editorForm.elements['" + entryEvent + "'].value='" + entryValue + "'; setScrollAndSubmit(); return false;\"";
						}
						else
						{
							// show confirmation message
							menuHTML += " onclick=\"if( confirm('" + confirmMessage + "') ) { document.editorForm.elements['" + entryEvent + "'].value='" + entryValue + "'; setScrollAndSubmit(); return false;}\"";
						}
					}
					else
					{
						// add empty onclick handler for selenium tests
						menuHTML += " onclick=\"\" ";
					}

					menuHTML += ">";
				}
				else
				{
					menuHTML += "<tr >";
				}

				if( isSplitter )
				{
					menuHTML += "<td colspan=\"3\">" + entryName + "</td>";
				}
				else
				{
					menuHTML += "<td class=\"icon\" id=\"" + elementID + "_icon_td\">";
					if( showIcon )
					{
						menuHTML += "<img src=\"" + icon + "\" id=\"" + elementID + "_img\">";
					}
					if( !disabled )
					{
						if( !document.editorForm.elements[entryEvent] )
						{
							inputElement = document.createElement("div");
							inputElement.innerHTML = "<input type=\"hidden\" name=\"" + entryEvent + "\" value=\"\">";
							document.editorForm.appendChild(inputElement);
						}
						
					}
					
					menuHTML += "</td>";
					menuHTML += "<td ";
					menuHTML += "class=\"name " + (disabled ? "disabled" : "") + "\" ";
					menuHTML += "style=\"text-align:" + (this.rightAlign ? "right" : "left") + ";\" id=\"" + elementID + "_label\">";
					menuHTML += entryName;
					menuHTML += "</td>";
					menuHTML += "<td>";
					
					if( hasSubMenu )
					{
						if( !disabled )
						{
							menuHTML += "<div style=\"width:8px;\"><img src=\"images/icons/submenu_arrow.gif\" /></div>";
						}
						else
						{
							menuHTML += "<div style=\"width:8px;\"><img src=\"images/icons/submenu_arrow_disabled.gif\" /></div>";
						}
					}
					
					menuHTML += "</td>";
				}

				menuHTML += "</tr>";				
			}

			menuHTML += "</table>";
			
			menuElement = document.createElement("div");
			menuElement.className = this.className;

			menuElement.style.visibility = "hidden";
			menuElement.style.position = "absolute";
			menuElement.style.top = -1000;				// though setting visibility to 'hidden' mozilla tends to show the menu for a fraction of a second
			menuElement.style.left = -1000;				// before placing it in the correct position. With these values for top and left
																	// the menu flicker will appear beyond the visible area and can no longer bother anyone.
			menuElement.style.zIndex = "20";

			document.getElementsByTagName("body")[0].appendChild(menuElement);

			menuElement.innerHTML = menuHTML;
			
			// find correct top-left coordinates
			var coordinates = y_getCoordinates(sourceElement, sourceEvent, menuElement, this.rightAlign);
			
			menuElement.style.top = coordinates.top;
			menuElement.style.left = coordinates.left;

			// create iframe which will later be shown behind the menu element to force the ie to show the menu on top of select boxes
			this.theIframeElement = y_createUnderlyingIframe(menuElement);				

			return menuElement;
		}

	/*
		Menu.show() will show this menu (which is already rendered when creating the menu but set invisible).
		All the submenus are also rendered during the creation of this menu but they will not visible until the appropriate
		menu entry is highlighted.
	*/
	this.show = function(event)
		{
			if( this.theMenuElement == null )
			{
				// create the hidden div containing the menu and create all submenus
				this.theMenuElement = this.createMenu(event != null ? event : this.sourceEvent, this.sourceElement);
			}

			if( menu_timeout != null)
			{
				// prevent multiple menus from opening at the same time (could happen, if an element with a contextmenu contains another element with a contextmenu, for example)
				return;
			}

			// remove possible previous menus
			menu_hide();

			if( event != null )
			{
				// find correct top-left coordinates
				var coordinates = y_getCoordinates(null, event, this.theMenuElement, this.rightAlign);
				
				this.theMenuElement.style.top = coordinates.top;
				this.theMenuElement.style.left = coordinates.left;

				if( this.theIframeElement != null )
				{
					this.theIframeElement.style.top = coordinates.top;
					this.theIframeElement.style.left = coordinates.left;
				}
			}

			this.theMenuElement.style.visibility = "visible";
			
			if( this.theIframeElement != null )
			{
				this.theIframeElement.style.visibility = "visible";
			}
			
			currentMainMenu = this;
			this.canRemove = false;
			
			menu_timeout = setTimeout(menu_removeTimeout, 300);			
		}
					
	/*
		Menu.hide() will hide this menu and all its containing submenus.
	*/
	this.hide = function()
	{
		if( this.canRemove )
		{
			// hide this menu element
			if( this.theMenuElement )
			{
				this.theMenuElement.style.visibility = "hidden";
				currentMainMenu = null;
				this.hideSubMenus();
			}
			
			if( this.theIframeElement != null )
			{
				this.theIframeElement.style.visibility = "hidden";
			}
		}
		else
		{
			// hide() was called by the initial onclick event which has just created the menu. the menu can be removed the next time hide() is called.
			this.canRemove = true;
		}
	}
					
	this.hideSubMenus = function()
	{
		for( var x = 0; x < this.subMenus.length; x++ )
		{
			this.subMenus[x].hide();
		}
	}				
	
	this.createSubMenu = function(menuEntries, bottomAlign)
	{
		return new SubMenu(menuEntries, this, bottomAlign, this.uniqueName);
	}
	
	/*
		Menu.setOpacity() sets the opacity of this menu.
		This is just a convenience method to set the two values 'filter' and 'opacity' which are responsible for the opacity 
		in the IE or in Mozilla browsers respectively.
		
		Parameter:
		
		opacityInPercent - the opacity in percent, i.e. using '100' will yield a menu which is fully opaque and '50' will be fifty percent transparent etc.
	*/
	this.setOpacity = function(opacityInPercent)
		{
			this.filter = "alpha(opacity=" + opacityInPercent + ")";
			this.opacity = opacityInPercent / 100;
		}

		
// ----- 'constructor' of Menu
	
	// store this menu in the global menu array
	menus[this.ID] = this;	
}

/*
	The SubMenu object represents a submenu within a menu.
	It is a 'subclass' of Menu and thus does almost the same things as Menu does.
	Main differences are:
	- In show() and hide() the SubMenu does not touch the global 'currentMainMenu' variable.
	- The SubMenu does not have a title.
	- There is no source event which can have triggered the SubMenu so it won't be considered when calculating the position.
	- There is always a source element which triggered the SubMenu and this will be used to calculate the Position (next to the 
	  source element which of course is a super menu entry which has been highlighted).
	
	As like Menu SubMenu contains menu entries which will be rendered in this menu.
	
	Parameters:
	
	menuEntries		-	An array containing all the entries for this menu.
*/
function SubMenu(menuEntries, parentMenu, bottomAlign, uniqueName)
{
	this.superClass = Menu;
	this.superClass(menuEntries);
	
	this.parentMenu = parentMenu;
	this.bottomAlign = bottomAlign != null ? bottomAlign : false;
	this.uniqueName = uniqueName;
	parentMenu.subMenus.push(this);

	/*
		Overriding Menu.show() to skip the 'currentMainMenu' handling and to allow a super menuentry element to be given to calculate the correct position.
	*/
	this.show = function(parentEntry)
		{
			if( this.theMenuElement == null )
			{
				// create the hidden div containing the menu and create all submenus
				this.theMenuElement = this.createMenu(this.sourceEvent, this.sourceElement);
			}

			if( parentEntry )
			{
				var parentDiv = getParentByTagName(parentEntry, "DIV");
				
				var offset = parentDiv != null ? (Element.getDimensions(parentDiv).width - Element.getDimensions(parentEntry).width) / 2 : 4;

				this.theMenuElement.style.left = Position.cumulativeOffset(parentEntry)[0] + Element.getDimensions(parentEntry).width + offset - 1;
				this.theMenuElement.style.top = Position.cumulativeOffset(parentEntry)[1] - (this.bottomAlign ? (Element.getDimensions(this.theMenuElement).height - Element.getDimensions(parentEntry).height - offset) : offset);

				// position the iframe at the same position as the submenu element
				if( this.theIframeElement != null)
				{
					this.theIframeElement.style.left = this.theMenuElement.style.left;
					this.theIframeElement.style.top = this.theMenuElement.style.top;
				}
			}
			if( parentMenu )
			{
				// hide all other sub menus (= my 'sibling' menus) of my parentmenu
				for(var x = 0; x < parentMenu.subMenus.length; x++ )
				{
					parentMenu.hideSubMenus();
				}
			}
			this.theMenuElement.style.visibility = "visible";
			if( this.theIframeElement != null )
			{
				this.theIframeElement.style.visibility = "visible";
			}
		}
					
	/*
		Overriding Menu.hide() to skip the 'currentMainMenu' handling.
	*/
	this.hide = function()
		{
			if( this.theMenuElement )
			{
				this.theMenuElement.style.visibility = "hidden";
			}
			if( this.theIframeElement != null )
			{
				this.theIframeElement.style.visibility = "hidden";
			}
			this.hideSubMenus();
		}

}



/*
	The MenuEntry object represents one single menu entry.
	It generally encapsulates information like the (display)name of the entry, the event id, an optional icon,
	optional submenu entries etc.
	
	Parameters:
	
	name					-	This is the name of this menu entry which you will see in the menu
	entryEvent			-	This is the event which will be triggered if you click on this menu entry. Also see entryValue! 
								This may be empty if this entry contains a submenu.
	entryValue			-	The value which the event will contain. This may be for example a simple 'true' if there is just one single
								event to be triggered (like 'delete' etc.). 
								But it also may contain any string value, which is necessary for more complex 
								operations like a create menu in which you can select the concrete element to create. In this case you
								could use the same event for several menu entries but always use another value which contains information
								about which element to create.
								Just like the entryEvent the entryValue may be empty if this entry contains a submenu, for example.
	enabled				-	if true this entry is enabled, if false it is disabled (greyed out and not clickable)
	menuEntries			-	An array containing other MenuEntry objects. If this is empty or null then this menu entry is just a normal
								entry which can be clicked etc.
								If menuEntries contains one or more other MenuEntries then these will be used to create a submenu which
								is shown if this MenuEntry is highlighted.
	icon					-	Filename of an icon which should be placed next to the menu entry. May be null to indicate there is no icon.
*/
function MenuEntry(name, entryEvent, entryValue, enabled, icon, menuEntries, confirmMessage, subMenuBottomAlign)
{
	this.name = name;							// the name of this entry which you will see in the menu
	this.entryEvent = entryEvent;			// the event which will be triggered if you click on this entry
	this.entryValue = entryValue;			// the value which will be assigned to the entryEvent if you click on this entry
	this.enabled = enabled;					// if true this entry is enabled, if false it is disabled (greyed out and not clickable)
	this.icon = icon;							// filename of an icon which should be placed next to the menu entry
	this.menuEntries = menuEntries;		// an array containing other MenuEntry objects which are used to create a submenu
	this.confirmMessage = confirmMessage;	// confirmation message wich - if not empty - will be shown before the actual event is triggered
	this.isSplitter = false;				// set to true if this entry represents a splitter/divider element
	this.isSubMenuBottomAlign = subMenuBottomAlign != null ? subMenuBottomAlign : false;	// if this is true the respective submenu would be bottom-aligned rather than the default top-aligned
	
	/*
		Convenience method to find out if this entry provides an icon.
	*/
	this.hasIcon = function()
		{
			return icon && icon != null && icon != "";
		}

	/*
		Convenience method to find out if this entry has a submenu.
	*/
	this.hasSubMenu = function()
		{
			return menuEntries && menuEntries != null && menuEntries.length > 0;
		}		

	/*
		Convenience method to find out if this entry has a confirmation message.
	*/
	this.hasConfirmMessage = function()
		{
			return confirmMessage && confirmMessage != null && confirmMessage.length > 0;
		}		
}

function MenuSplitter(name)
{
	this.superClass = MenuEntry;
	this.superClass(name, null, null, false, null, null);
	this.isSplitter = true;
}
