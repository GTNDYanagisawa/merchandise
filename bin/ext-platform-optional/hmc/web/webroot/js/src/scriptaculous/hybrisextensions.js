
/* ------------------ patching scriptaculous' Autocompleter to allow showing select box on any keypress (e.g. cursor down) ---------------------------------- */



Object.extend(Ajax.Autocompleter.prototype, {
												onObserverEvent : function() 
																{
																	this.changed = false;   
																	this.startIndicator();
																	this.getUpdatedChoices();
																}
											  })
