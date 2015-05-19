var myrules = 
{
	'div.chip-event > a' :					function(element)
													{
														// add onclick eventhandler
														element.onclick = function()
																				{
																					return y_chipEvent(element);
																				}													
													},

	'div.open-url a' : function(element)
												{
													element.onclick = function()
																			{
																				y_openURL(element);
																			};
												},
	'table.selecttable':						function(table)
													{
														st_addSelecttable(table);
													},

	'table.selecttable > tbody > tr' : 	function(tr)
													{													
														// add onclick eventhandler (for left clicks)
														element.onclick = function(event)
																				{
																					st_click(tr, event);
																				};
	
														// add oncontextmenu eventhandler (for right clicks)
														element.oncontextmenu = function(event)
																						{
																							st_click(tr, event, true);
																						};
	
														element.onselectstart = function() { 
															if((document.activeElement.nodeName == "INPUT" && document.activeElement.type=="text" ) || document.activeElement.nodeName=="TEXTAREA")
															{
																return true;
															}
														return false; }		// prevents built-in select feature in ie													
													},

	'tr.doubleclick-event':					function(element)
													{	
														// add ondblclick eventhandler
														element.ondblclick = function(event)
																					{
																						return y_doubleClickEvent(element);
																					};
													},

	'div.tree-toggle':						function(element)
													{
														element.onclick = function()
																				{
																					y_toggleTreeNode(element);
																				}
											},
											
	'div.language-toggle a':		function(element)
											{
												element.onclick = function()
																		{							
																			y_languageToggle(element);
																		}
											},
										
	'select.disabled':				function(element)
											{
												if( BrowserDetect.browser == "Firefox" )
												{
													y_addContextMenuToDisabledSelect(element);
												}
											}
};

Behaviour.register(myrules);
