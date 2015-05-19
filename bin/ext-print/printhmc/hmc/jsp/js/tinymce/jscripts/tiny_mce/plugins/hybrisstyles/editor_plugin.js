/**
 * @author rr | hybris GmbH 
 * @copyright Copyright (c) 2000-2010, hybris AG, All rights reserved.
 * 
 * Useful links:
 * http://wiki.moxiecode.com/index.php/TinyMCE:API/tinymce.ui.ListBox
 * http://wiki.moxiecode.com/index.php/TinyMCE:API/tinymce.Editor
 */

/* Import plugin specific language pack */
tinymce.PluginManager.requireLangPack( 'hybrisstyles' );

(function() {
	tinymce.create( 'tinymce.plugins.HybrisStyles', {

		/**
		 * Returns information about the plugin as a name/value array.
		 * The current keys are longname, author, authorurl, infourl and version.
		 *
		 * @returns Name/value array containing information about the plugin.
		 * @type Array 
		 */
		getInfo : function() 
		{
			return {
				longname : 'hybris print styles plugin',
				author : 'hybris GmbH | rr',
				authorurl : 'http://www.hybris.de',
				version : "1.1"
			};
		},

		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed 		Editor instance that the plugin is initialized in.
		 * @param {string} url 				Absolute URL to where the plugin is located.
		 */
		init : function( ed, url )
		{
			var thisPlugin = this;

			// Add command for processing character style changes
			ed.addCommand( 'hySetCharacterStyle', function(ui, value) {
				thisPlugin.processCommand( 'hySetCharacterStyle', value );
			});

			// Add command for processing character style changes
			ed.addCommand( 'hySetParagraphStyle', function(ui, value) {
				thisPlugin.processCommand( 'hySetParagraphStyle', value );
			});

			ed.onNodeChange.add( function(ed, cm, node, co){
				var cStyleListbox = tinyMCE.activeEditor.controlManager.get( 'hybrisprintcharacterstyle' );
				
				// Select class in CharStyle listbox that is assigned to the active node
				// TODO: Search next parent SPAN element instead of iterating over parents
				// var parentSpan = inst.dom.getParent( node, "span" );
				if( cStyleListbox )
				{
					var isFoundStyle = false;
					var foundStyle = '';
					do {
						if( node  &&  node.className )
						{
							for( var i=0; i<cStyleListbox.items.length; i++ )
							{
								if( cStyleListbox.items[i].value == node.className )
								{
									isFoundStyle = true;
									foundStyle = cStyleListbox.items[i].value;
									break;
								}
							}
						}
					} while ( !isFoundStyle  &&  node != null  &&  (node = node.parentNode) != null );
					
					cStyleListbox.select( foundStyle );
				}
			});
			
			ed.onNodeChange.add( function(ed, cm, node, co){
				var pStyleListbox = tinyMCE.activeEditor.controlManager.get( 'hybrisprintparagraphstyle' );
	
				// Select class in ParaStyle listbox that is assigned to the active node
				// TODO: Search next parent DIV or P element instead of iterating over parents
				if( pStyleListbox )
				{
					var isFoundStyle = false;
					var foundStyle = '';
					do {
						if( node  &&  node.className )
						{
							for( var i=0; i<pStyleListbox.items.length; i++ )
							{
								if( pStyleListbox.items[i].value == node.className )
								{
									isFoundStyle = true;
									foundStyle = pStyleListbox.items[i].value;
									break;
								}
							}
						}
					} while ( !isFoundStyle  &&  node != null  &&  (node = node.parentNode) != null );
					
					pStyleListbox.select( foundStyle );
				}
			});			
		},
		
		/**
		 * Creates a control by name.
		 * When a control is created it will automatically add it to the control collection. 
		 * It first asks all plugins for the specified control if the plugins didn't return 
		 * a control then the default behavior will be used.
		 *
		 * @param {string} control_name			The name of the control that shall be created
		 * @param {tinymce.ControlManager} cm	The tinyMCE's control manager
		 */
		createControl: function( control_name, cm )
		{
			switch( control_name )
			{
				// Create a listbox with the names of the available character styles
				case 'hybrisprintcharacterstyle':
					var charStyleListBox = cm.createListBox( 'hybrisprintcharacterstyle', {
						 title : 'Character styles',		// TODO: localize title
						 onselect : function( selectedValue ) {
							 // tinyMCE.activeEditor.windowManager.alert( 'Value selected:' + selectedValue );
							 tinyMCE.execCommand( 'hySetCharacterStyle', false, selectedValue );
						 }
					});

					// get available character styles	
					var cstyles = tinyMCE.activeEditor.getParam( "hybris_print_characterstyles", "", false ).split(';');
					var emptycharacterstyle = tinyMCE.activeEditor.getParam( "emptycharacterstyle", "---", false );

					// allow empty style (if style shall not be specified)
					charStyleListBox.add( emptycharacterstyle, '' );

					// Add available character styles to the list box
					for( var i = 0; i < cstyles.length; i++ )
					{
						var code = cstyles[i].split('=')[0];
						var name = cstyles[i].split('=')[1];
						charStyleListBox.add( code, name );
					}
	
					// Return the new listbox instance
					return charStyleListBox;

				// Create a listbox with the names of the available paragraph styles
				case 'hybrisprintparagraphstyle':
					var paraStyleListBox = cm.createListBox( 'hybrisprintparagraphstyle', {
						 title : 'Paragraph styles',		// TODO: localize title
						 onselect : function( selectedValue ) {
							 tinyMCE.execCommand( 'hySetParagraphStyle', false, selectedValue );
						 }
					});

					// get available character styles	
					var pstyles = tinyMCE.activeEditor.getParam( "hybris_print_paragraphstyles", "", false ).split(';');
					var emptyparagraphstyle = tinyMCE.activeEditor.getParam( "emptyparagraphstyle", "---", false );

					// allow empty style (if style shall not be specified)
					paraStyleListBox.add( emptyparagraphstyle, '' );

					// Add available paragraph styles to the list box
					for( var i = 0; i < pstyles.length; i++ )
					{
						var code = pstyles[i].split('=')[0];
						var name = pstyles[i].split('=')[1];
						paraStyleListBox.add( code, name );
					}

					// Return the new listbox instance
					return paraStyleListBox;
			}
	
			return null;
		},
		
		
		/**
		 * Executes a style command
		 *
		 * @param {string} command 			Command name to be executed
		 * @param {string} inst 			TinyMCE editor instance
		 * @param {mixed} value 			Custom value argument, can be anything.
		 */
		processCommand : function( command, value )
		{
			switch( command )
			{
				case "hySetCharacterStyle":
					var inst = tinyMCE.activeEditor;
					var select = inst.selection;
					var elm = select.getNode();
					var parent = inst.dom.getParent( elm, "span" );

					// Check if CharStyle has been choosen or not
					if( value )
					{
						// Check if a text is selected
						if( select.getContent() )
						{
							// INFO: if there is already a SPAN surrounding this selection, the CharStyle will be assigned to this complete SPAN instead to the selection only
							// PROBLEM: InDesign does not support nested CharStyles, so prevent nested SPANs.
							// If there is a surrounding SPAN, split it and set the current selection as a separate SPAN in the middle of the splitted surrounding SPAN whose two parts now surround the new SPAN.
							this.doSetStyleInfo( parent, {command : "setattrib", name : "class", value : value, wrapper : "span"}, inst );
						}
						else 
						{
							// No text is selected, the cursor is somewhere in the text.
							// Find surrounding SPAN
							if( parent != null )
							{
								this.doSetStyleInfo( parent, {command : "setattrib", name : "class", value : value, wrapper : "span"}, inst );
							}
							else 
							{
								// There is no surrounding SPAN to which to assign the CharStyle. Do nothing
							}
						}
					}
					else 
					{
						// No character style is selected. This means: remove the character style from the current element
						
						// check if a text is selected
						if( select.getContent() )
						{
							// This removes the character style from the complete SPAN, not only from the selection
							this.doSetStyleInfo( parent, {command : "removeformat", name : "class", value : value, wrapper : "span"}, inst );
						}
						else 
						{
							if( parent != null )
							{
								this.doSetStyleInfo( parent, {command : "removeformat", name : "class", value : value, wrapper : "span"}, inst );
							}
							else 
							{
								// There is no surrounding SPAN from where to remove the CharStyle. Do nothing
							}
						}
					}
					break;
	
								
				case "hySetParagraphStyle":
					var inst = tinyMCE.activeEditor;
					var select = inst.selection;
					var elm = select.getNode();
					var parent = inst.dom.getParent( elm, "div,p" );
	
					// Check if ParaStyle has been choosen or not
					if( value )
					{
						// Check if a text is selected
						if( select.getContent() )
						{
							// INFO: if there is already a DIV surrounding this selection, the ParaStyle will be assigned to this complete DIV instead to the selection only
							this.doSetStyleInfo( parent, {command : "setattrib", name : "class", value : value, wrapper : "div,p"}, inst );
						}
						else 
						{
							// No text is selected. The cursor is somewhere in the text.
							// Find surrounding DIV
							if( parent != null )
							{
								this.doSetStyleInfo( parent, {command : "setattrib", name : "class", value : value, wrapper : "div,p"}, inst );
							}
							else 
							{
								// There is no surrounding DIV to which to assign the ParaStyle. Do nothing
							}
						}
					}
					else 
					{
						// No paragraph style is selected. This means: remove the paragraph style from the current element
						
						// check if a text is selected
						if( select.getContent() )
						{
							// This removes the character style from the complete DIV, not only from the selection
							this.doSetStyleInfo( parent, {command : "removeformat", name : "class", value : value, wrapper : "div,p"}, inst );
						}
						else 
						{
							if( parent != null )
							{
								this.doSetStyleInfo( parent, {command : "removeformat", name : "class", value : value, wrapper : "div,p"}, inst );
							}
							else 
							{
								// There is no surrounding DIV from where to remove the ParaStyle. Do nothing
							}
						}
					}
					break;
			}
		},

		
		/* 
		 * Assingns a given class information to the specified (parent) element.
		 * (Due to some smaller adaptions, this is a modified copy of the tinyMCE's original
		 * mceSetStyleInfo method)
		 * 
		 * @param {HTMLElement} parent 		The element to which to assign the class information
		 * @param {mixed} value 			Custom value argument, can be anything.
		 * @param {TinyMCE} instance 		An instance of a tinyMCE editor
		 * @type void
		 */	
		doSetStyleInfo : function( parent, value, instance )
		{
			 //alert( "doSetStyleInfo: command[" + value.command + "], name[" + value.name + "], value[" + value.value + "], wrapper[" + value.wrapper + "]" );
			var v = value;
			
			var t = this;
			var ed = instance;
			var d = ed.getDoc();
			var dom = ed.dom;
			var e;
			var b;
			var s = ed.selection;
			var nn = new Array ('');
			if(v.wrapper)
				nn = v.wrapper.split(',');
			var b = s.getBookmark();
			var re;
			var mySpan = null;



		/**
		 * function called recursively to create array of parents for a given node
		 * up to highest span in parent hierarchy
		 *
		 * @returns array of parents for span (with this span and with parent that is not span)
		 * @param {Node} instance 						node that is currently consider in this loop
		 * @param (Array) currentNodesStructure			all parrents starting from span node until this one considered in this loop
		 * @param (Array) nodesStructure 				part of currentNodesStructure which would be returned which start from given span and finish on node above highest span element in hierarchy
		 */
		function searchSpanParent(node, currentNodesStructure, nodesStructure){
			
			// when body is reached or there is no parent than finish and return result
			if(node==null || node.nodeName.toLowerCase()=='body')
				return(nodesStructure);

			// span was found so copy current structure to what we want return from function 
			// we are interested in changing span and it's children
			if(node.nodeName.toLowerCase()=='span'){
					nodesStructure=currentNodesStructure;
				}
			
			// add current node to current structure
			if(currentNodesStructure.length)
				currentNodesStructure[currentNodesStructure.length]=node;
			else
				currentNodesStructure[0]=node;

			// do the same for parent
			return searchSpanParent(node.parentNode,currentNodesStructure, nodesStructure);		
		}



		function isInArray(myString, myArray){
			if(myArray.length>0){
				for(m=0;m<myArray.length;m++){
					if(myString==myArray[m].toLowerCase())
						return true;
						}
			}
			return false;
		}

		/**
		* function that prevent creating nested spans, pass span and search if it has span in parent hierarchy
		* if it has than split them on two and finish one part before and one part after given span
		*
		* @param {Node} myElement     span that should be checked
		*/
		function separateSpan(myElement){

		// prepare variables
		var currentNodesStructure = new Array();
		currentNodesStructure = new Array();
		
		// search for all parents that should be considered
		var myArray = searchSpanParent(myElement, currentNodesStructure, new Array()); 
		if(myArray[myArray.length-1].nodeName.toLowerCase()=='span')
			myArray[myArray.length]=myArray[myArray.length-1].parentNode;
		
			
		// go through all parents and split span elemenets
		for(i=1;i<myArray.length-1;i++){
						
						//if it is first or last then there colne is not needed
						if((myArray[i].firstChild==myArray[i-1])||(myArray[i-1].lastChild==myArray[i-1])){
						
							if(myArray[i].firstChild==myArray[i-1]){
								myArray[i+1].insertBefore(myArray[i-1],myArray[i]);
							}
							else{
								myArray[i+1].appendChild(myArray[i-1]);
							}
							myArray[i]=myArray[i-1];
						}
						// need to clone parent then remove all sub nodes after child from first 
						// and all remove all sub nodes before child from second
						// and move child one level up
						else
						{
							//TODO: is this a problem that nodes id would not be unique?
							var nodeClone = myArray[i].cloneNode(true);
							var beforeNode = false;
							for(j=myArray[i].childNodes.length-1;j>=0;j--){
							// when reach child finish copy nodes
								if(myArray[i].childNodes[j]!=myArray[i-1]){
									if(beforeNode){
											myArray[i].removeChild(myArray[i].childNodes[j]);
										}
										else
										{
										 	nodeClone.removeChild(nodeClone.childNodes[j]);
										}								
								}
								else{
									nodeClone.removeChild(nodeClone.childNodes[j]);
									//add only if it is not empty
									myArray[i+1].insertBefore(nodeClone,myArray[i]);				
									myArray[i+1].insertBefore(myArray[i-1],myArray[i]);
									//if there are no children for myArray then remove 
									beforeNode = true;
									
								}
							}
							myArray[i]=myArray[i-1];
						}
		}

	}




			function set(n, e) 
			{
				if ( n.nodeType == 1  &&  isInArray(n.nodeName.toLowerCase(),nn) ) 
				{	
						// check if the node's name is equyls to the wrapper name
					switch (v.command) 
					{
						case 'setattrib':
							return dom.setAttrib(n, v.name, v.value);
			
						case 'setstyle':
							return dom.setStyle(n, v.name, v.value);
			
						case 'removeformat':
							return dom.setAttrib(n, 'class', '');
					}
				}
			};
			
			// Setup regexp
			re = ed.settings.merge_styles_invalid_parents;
			if (tinymce.is(re, 'string'))
				re = new RegExp(re, 'i');
			
			// Set style info on selected element
			e = t._getSelectedElement(instance);
			
			// id = "tinymce" is id of wysiwyg editor element 
			// in case if it is returned it means that there was no element in edited text
			
			if(e!=null && e.id=="tinymce")
					e = null;
			if ( e && isInArray(e.nodeName.toLowerCase(),nn))	// (e = s.getNode() )
			{
				set(e, 1);
				mySpan = e;
			}
			else 
			{
				// Generate wrappers and set styles on them
				d.execCommand('FontName', false, '__');
				tinymce.each( tinymce.isWebKit ? dom.select('span') : dom.select('font'), function(n) 
				{
					var sp, e;
			
					if (dom.getAttrib(n, 'face') == '__' || n.style.fontFamily == '__') 
					{
						sp = dom.create(nn[0], {mce_new : '1'});

						set(sp);
						mySpan = sp;
						tinymce.each (n.childNodes, function(n) 
						{
							sp.appendChild(n.cloneNode(true));
						});
			
						dom.replace(sp, n);
					}
				});
			}
			
			// Remove wrappers inside new ones
			tinymce.each( dom.select(nn[0]).reverse(), function(n) 
			{
				var p = n.parentNode;
			
				dom.setAttrib(n, 'mce_new', '');
			
				// Check if it's an old span in a new wrapper
				if (!dom.getAttrib(n, 'mce_new')) 
				{
					// Find new wrapper
					p = dom.getParent(n, function(n) 
					{
						return n.nodeType == 1 && dom.getAttrib(n, 'mce_new');
					});
			
					if (p)
						dom.remove(n, 1);
				}
			});
			
			// Merge wrappers with parent wrappers
			tinymce.each(dom.select(nn[0]).reverse(), function(n) 
			{
				var p = n.parentNode;
			
				if (!p)
					return;
			
				// Has parent of the same type and only child
				if (isInArray(p.nodeName.toLowerCase(),nn) && p.childNodes.length == 1){
					
					return dom.remove(p, 1);
					}
			
				// Has parent that is more suitable to have the class and only child
				if (n.nodeType == 1 && (!re || !re.test(p.nodeName)) && p.childNodes.length == 1) 
				//parent can't be a be a body which id is 'tinymce'
					if(p.id!='tinymce')
						//character style from span should not be pased to p tag
						if(n.nodeName.toLowerCase()!='span' || n.nodeName.toLowerCase()=='p')
				{	
					set(p); // Set style info on parent instead
					dom.setAttrib(n, 'class', '');
				}
			});
			
			// Remove empty wrappers
			tinymce.each(dom.select(nn[0]).reverse(), function(n) 
			{
				if (!dom.getAttrib(n, 'class') && !dom.getAttrib(n, 'style'))
					return dom.remove(n, 1);
			});
			
			s.moveToBookmark(b);
			//call method that will prevent creating nested spans
			if(mySpan!=null && mySpan.nodeName.toLowerCase()=='span')
				separateSpan(mySpan);
		},

		
		/* 
		 * Since the tinyMCE's original getSelectedElement method is not accessable from outside
		 * we use this local copy that is only modified slightly to run properly (no functional changes)
		 * 
		 * @param {TinyMCE} instance 		An instance of a tinyMCE editor
		 * @type <node>
		 */	
		_getSelectedElement : function( instance ) 
		{
			var t = this;
			var ed = instance;
			var dom = ed.dom;
			var se = ed.selection;
			var r = se.getRng();
			var r1, r2, sc, ec, so, eo, e, sp, ep, re;
			
			if (se.isCollapsed() || r.item)
				return se.getNode();

			// Setup regexp
			re = ed.settings.merge_styles_invalid_parents;
			if (tinymce.is(re, 'string'))
				re = new RegExp(re, 'i');

			if (tinymce.isIE) 
			{
				r1 = r.duplicate();
				r1.collapse(true);
				sc = r1.parentElement();

				r2 = r.duplicate();
				r2.collapse(false);
				ec = r2.parentElement();

				if (sc != ec)
				{
					r1.move('character', 1);
					sc = r1.parentElement();
				}

				if (sc == ec) 
				{
					r1 = r.duplicate();
					r1.moveToElementText(sc);

					if (r1.compareEndPoints('StartToStart', r) == 0 && r1.compareEndPoints('EndToEnd', r) == 0)
						return re && re.test(sc.nodeName) ? null : sc;
				}
			} 
			else 
			{
				function getParent(n) {
					return dom.getParent(n, function(n) {return n.nodeType == 1;});
				};

				sc = r.startContainer;
				ec = r.endContainer;
				so = r.startOffset;
				eo = r.endOffset;

				if (!r.collapsed) 
				{
					if (sc == ec) 
					{
						if (so - eo < 2) 
						{
							if (sc.hasChildNodes()) 
							{
								sp = sc.childNodes[so];
								return re && re.test(sp.nodeName) ? null : sp;
							}
						}
					}
				}

				if (sc.nodeType != 3 || ec.nodeType != 3)
					return null;

				if (so == 0) 
				{
					sp = getParent(sc);

					if (sp && sp.firstChild != sc)
						sp = null;
				}

				if (so == sc.nodeValue.length) 
				{
					e = sc.nextSibling;

					if (e && e.nodeType == 1)
						sp = sc.nextSibling;
				}

				if (eo == 0) 
				{
					e = ec.previousSibling;

					if (e && e.nodeType == 1)
						ep = e;
				}

				if (eo == ec.nodeValue.length) 
				{
					ep = getParent(ec);

					if (ep && ep.lastChild != ec)
						ep = null;
				}

				// Same element
				if (sp == ep)
					return re && sp && re.test(sp.nodeName) ? null : sp;
			}

			return null;
		}
				
		
	});
	// Adds the plugin class to the list of available TinyMCE plugins
	tinymce.PluginManager.add( 'hybrisstyles', tinymce.plugins.HybrisStyles );
})();
