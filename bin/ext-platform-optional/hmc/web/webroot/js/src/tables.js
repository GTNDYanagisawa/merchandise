/* ------------------ selecttable---------------------------------- */

	var st_tables = new Object();

	function st_Entry(tr, index)
	{
		this.tr = tr;
		this.index = index;
	}

	function st_Table(table)
	{
		this.isSingleSelect = Element.hasClassName(table, "singleselect") ;
		this.selected = new Object();		// contains all currently selected st_Entry objects
		this.lowestSelectedIndex = -1;	// index of the currently lowest selected entry
		this.entries = new Object();
		this.entriesByIndex = null;
// mr: seems to be obsolete...?
//		this.selectionChangedElement = null;
		
		this.add = function(tr, index)
		{
			var newEntry = new st_Entry(tr, index);
			this.addEntry(newEntry);
			
			if( st_isChecked(tr) )
			{
				this.addSelectedEntry(newEntry);
			}
			return newEntry;
		};

		this.select = function(tr, exclusive)
		{
			if( exclusive )
			{
				// de-select all other selected entries
				var trID = tr.getAttribute("id");
				
				for( selectedID in this.selected )
				{
					if( selectedID != trID )
					{
						this.deselect(this.selected[selectedID].tr);
					}
				}
			}
			
			// only if tr is not already selected
			if( !this.isSelected(tr) )
			{
				this.addSelectedEntry(this.getEntryByTR(tr));
			}
		};
		
		this.deselect = function(tr)
		{
			// only if tr is currently selected
			if( this.isSelected(tr) )
			{
				this.removeSelectedEntry(this.getEntryByTR(tr));
			}
		};
		
		this.toggle = function(tr)
		{
			if( this.isSelected(tr) )
			{
				// deselect
				this.removeSelectedEntry(this.getEntryByTR(tr));
			}
			else
			{
				this.addSelectedEntry(this.getEntryByTR(tr));
			}
		};
		
		this.selectAll = function()
		{
			for( trID in this.entries )
			{
				this.select(this.entries[trID].tr);
			}
		};
		
		this.isSelected = function(tr)
		{
			return this.selected[tr.getAttribute("id")] != null;
		};

		this.addEntry = function(entry)
		{
			this.entries[entry.tr.getAttribute("id")] = entry;
		};
		
		this.getEntryByTR = function(tr)
		{
			return this.entries[tr.getAttribute("id")];
		};
		
		this.addSelectedEntry = function(entry)
										{
											this.selected[entry.tr.getAttribute("id")] = entry;
											st_check(entry.tr);
											Element.addClassName(entry.tr, "selected");
											
											// adjust lowestSelectedIndex if necessary
											if( this.lowestSelectedIndex == -1 || entry.index < this.lowestSelectedIndex)
											{
												this.lowestSelectedIndex = entry.index;
											}
// mr: seems to be obsolete...?
//											this.setSelectionChanged();
										};
		
		this.removeSelectedEntry = function(entry)
										{
											delete this.selected[entry.tr.getAttribute("id")];
											st_uncheck(entry.tr);
											Element.removeClassName(entry.tr, "selected");

											if( entry.index == this.lowestSelectedIndex )
											{
												// re-adjust lowest selected index
												this.adjustLowestSelectedIndex();
											}
// mr: seems to be obsolete...?
//											this.setSelectionChanged();
										};
		
		this.selectRange = function(tr)
										{
											if( this.lowestSelectedIndex == -1 )
											{
												// nothing selected yet, just do the normal selection
												this.select(tr);
											}
											else
											{
												// range selection, select all entries between current tr's index and lowest selected index
												var startIndex = this.lowestSelectedIndex <= this.getIndex(tr) ? this.lowestSelectedIndex : this.getIndex(tr);
												var endIndex = this.lowestSelectedIndex >= this.getIndex(tr) ? this.lowestSelectedIndex : this.getIndex(tr);
												
												// deselect all which are not in the selection range
												var entriesToRemove = new Array();
												for( entryID in this.selected )
												{
													var entry = this.selected[entryID];
													if( entry.index < startIndex || entry.index > endIndex )
													{
														entriesToRemove.push(entry);
													}
												}
												for( var i = 0; i < entriesToRemove.length; i++ )
												{
													this.removeSelectedEntry(entriesToRemove[i]);
												}
												
												// select all within the selection range
												for( var i = startIndex; i <= endIndex; i++ )
												{
													var entry = this.entriesByIndex[i];
													if( !this.isSelected(entry.tr) )
													{
														this.addSelectedEntry(entry);
													}
												}
											}
										};
										
		this.getIndex = function(tr)
										{
											return this.entries[tr.getAttribute("id")].index;
										};
		
		this.adjustLowestSelectedIndex = function()
										{
											// stop marker, no need to go lower than previous lowest index +1
											var stopAtIndex = this.lowestSelectedIndex + 1;

											// reset lowest selected index
											this.lowestSelectedIndex = -1;
											
											// loop through all selected entries searching for the lowest index (if 'selected' is empty, this loop won't even be run once)
											for( entryID in this.selected )
											{
												if( this.lowestSelectedIndex == -1 || this.selected[entryID].index < this.lowestSelectedIndex )
												{
													this.lowestSelectedIndex = this.selected[entryID].index;
								
													// stop index reached?
													if( this.lowestSelectedIndex == stopAtIndex )
													{
														break;
													}
												}
											}
										};
		
		// fill entries with all tr nodes
		var trNodes = domQuery("tr", table);
		
		this.entriesByIndex = new Array(trNodes.length);
		for( var i = 0; i < trNodes.length; i++ )
		{
			this.entriesByIndex[i] = this.add(trNodes[i], i);
		}		
	}
	
	function st_addSelecttable(table)
	{
		st_tables[table.getAttribute("id")] = new st_Table(table);
	}
	
	function st_getSelecttable(element)
	{
		if( element.nodeName == "TABLE" )
		{
			return st_tables[element.getAttribute("id")];
		}
		else
		{
			// find parent table and its id
			return st_tables[getParentByTagName(element, "TABLE").getAttribute("id")];
		}
	}
	
	// st_isChecked() returns true if the current row is selected (i.e. if the hidden checkbox is checked).
	function st_isChecked(tr)
	{
		return tr.getElementsByTagName("INPUT")[0] && tr.getElementsByTagName("INPUT")[0].checked;
	}
	
	function st_click(tr, event, isRightClick)
	{
		if( !event )
		{
	   	event = window.event;
	   }
	   
	   var table = st_getSelecttable(tr);

		if( table.isSingleSelect )
		{
			// no matter what additional key may have been pressed, if the table is 'singleselect' then exactly one row should be selected
			table.select(tr, true);
		}
		else if( event && ( event.ctrlKey || BrowserDetect.OS == "Mac" && BrowserDetect.browser == "Firefox" && event.metaKey ) )
		{
			if( isRightClick )
			{
				table.select(tr);
			}
			else
			{
				table.toggle(tr);
			}
		}
		else if( event && event.shiftKey )
		{
			table.selectRange(tr);
		}
		else
		{
			// if using left click or if using rightclick on an unselected tr -> select the tr exclusively
			if( !isRightClick || !table.isSelected(tr) )
			{
				table.select(tr, true);
			}
		}
	}
	
	function st_selectAll(table)
	{
		st_getSelecttable(table).selectAll();
	}

	function st_check(tr)
	{
		// find appropriate <input> element
		var inputElement = domQuery(".checkbox input", tr)[0];

		if( inputElement )
		{
			// set specified value or toggle current checkbox state
			inputElement.checked = true;
		}
	}

	function st_uncheck(tr)
	{
		// find appropriate <input> element
		var inputElement = domQuery(".checkbox input", tr)[0];

		if( inputElement )
		{
			// set specified value or toggle current checkbox state
			inputElement.checked = false;
		}
	}

/*
	// st_hightlight() highlights the given <tr> by assigning the appropriate 'hover' (and 'hoverselected') 
	// classes to it.
	function st_highlight(tr)
	{		
		Element.addClassName(tr, "hover");
		if( Element.hasClassName(tr, "selected") )
		{
			Element.addClassName(tr, "hoverselected");
			Element.removeClassName(tr, "hover");
		}
		else
		{
			Element.addClassName(tr, "hover");
			Element.removeClassName(tr, "hoverselected");
		}	
	}

	// st_unhighlight() removes 'hover' (and 'hoverselected') classes previously assigned via st_highlight.
	function st_unhighlight(tr)
	{
		Element.removeClassName(tr, "hover");
		Element.removeClassName(tr, "hoverselected");
	}
*/

