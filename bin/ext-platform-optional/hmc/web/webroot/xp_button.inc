<% if( DEBUG_COMMENTS ) { %><!-- start: xp_button.inc --><% } %>

<%@ page import="java.io.IOException" %>
<script language="JavaScript" type="text/JavaScript">
	var selectedID = "";
	
	function mOver(id)
	{
		<%= swapButton("btn_hover") %>
	}
	
	function mOut(id)
	{
		if( id != selectedID )
		{				
			<%= swapButton("btn_norm") %>
		}
		else 
		{
			<%= swapButton("btn_selected") %>
		}
	}

	function mClick(id)
	{
		<%= swapButton("btn_clicked") %>
	}

	function mFocus(id)
	{
		<%= swapButton("btn_selected") %>				
		selectedID = id;
	}
	
	function mBlur(id)
	{
		selectedID = "";
		mOut(id);
	}
	
	function mSubmit(id, event, scroll, formElement)
	{
		mClick(id);
		formElement.elements[event].value='true';
//		setScroll();
		exit = false;
		if( formElement.onsubmit )
		{
			formElement.onsubmit();
		}
		showWaitIcon();
		formElement.submit();
	}
	
	function getCurrentForm(currentElement)
	{
		if( (currentElement.nodeName == "body") || (currentElement.nodeName == "BODY") )
		{
			return;
		}
		
		if( (currentElement.nodeName == "form") || (currentElement.nodeName == "FORM") )
		{
			return currentElement;
		}

		return getCurrentForm(currentElement.parentNode);
	}
</script>

<%! 	
	int buttonID = 0;

	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 * If scrolldown is true, then the window will be scrolled down to the position of this button (if possible).
	 * In condition you can provide a java script condition text which has to return true if the event is to be fired 
	 * after the button is pressed.
	 */
	String xpButtonWithCondition(String label, String event, String tooltip, boolean scrolldown, String condition)
	{
		String buttonIDString = "xpBtn_" + event + buttonID;
		String conditionText = (condition != null && condition.trim().length() > 0 ? "if("+condition+")" : "");
		
		// store current form element in currentForm (dirty, i guess)
		String output = "<script id=\"" + buttonIDString + "\" language=\"JavaScript\" type=\"text/JavaScript\">"
							 + "currentForm = getCurrentForm(document.getElementById(\"" + buttonIDString +"\"));"
							 + "</script>";

		output += "<input type=\"hidden\" name=\"" + event + "\" value=\"" + AbstractChip.FALSE + "\" />\n"
				 + "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" title=\"" + tooltip + "\">\n"
				 + "	<tr>\n"
				 + "		<td>\n"
				 + "			<a href=\"#\" hideFocus=\"true\" style=\"text-decoration:none; \" onMouseover=\"window.status='" + tooltip + "'; mOver('" + buttonIDString + "'); return true;\" " + 
					   			"onMouseout=\"window.status=''; mOut('" + buttonIDString + "'); return true;\" onMousedown=\"mClick('" + buttonIDString +"'); return true;\" " +
                  			"onMouseup=\"mOver('" + buttonIDString + "'); return true;\" onFocus=\"mFocus('" + buttonIDString + "'); return true;\" "+
					   			"onBlur=\"mBlur('" + buttonIDString + "'); return true;\" onClick=\"" + conditionText + "{mSubmit('" + buttonIDString + "', '" + event + "', " + scrolldown + ", currentForm ); return true;}\">\n"
		       + "  			<table height=\"23\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
		       + "    				<tr>\n"
		       + "      				<td id=\"" + buttonIDString + "_left\" style=\"width:4px;\" background=\"images/btn_norm_left.gif\">&nbsp;</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_middle\" style=\"white-space:nowrap; color:#333333; vertical-align:middle;\" background=\"images/btn_norm_middle.gif\" style=\"vertical-align: middle;\">\n"
       	    + "							&nbsp; &nbsp; " + label + " &nbsp; &nbsp; \n"
		       + "						</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_right\" style=\"width:4px\" background=\"images/btn_norm_right.gif\">&nbsp;</td>\n"
		       + "    				</tr>\n"
		       + "				</table>\n"
		       + "			</a>\n"
		       + "		</td>\n"
		       + "		<td>\n"
		       + "			<input type=\"image\" src=\"images/transparent.gif\" hideFocus=\"true\" onClick=\"mClick('" + buttonIDString + "');\">\n"
		       + "		</td>\n"
		       + "	</tr>\n"
		       + "</table>\n";

		buttonID++;
		return output;
 	}

	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 * If scrolldown is true, then the window will be scrolled down to the position of this button (if possible).
	 * In confirmMessage you can provide a message text which has to be confirmed if the button is pressed.
	 */
	String xpButton(String label, String event, String tooltip, boolean scrolldown, String confirmMessage1, String confirmMessage2)
	{
		String buttonIDString = "xpBtn_" + event + buttonID;
		
		String cm = "";
		boolean message1NotEmpty = ((confirmMessage1 != null) && (confirmMessage1.length() > 0));
		boolean message2NotEmpty = ((confirmMessage2 != null) && (confirmMessage2.length() > 0));
		
		if( message1NotEmpty )
		{
			cm = "confirm('" + confirmMessage1 + "')";
		}
		if( message2NotEmpty )
		{
			if( message1NotEmpty )
				cm += " && ";
			cm += "confirm('" + confirmMessage2 + "')";
		}
		
		return xpButtonWithCondition( label, event, tooltip, scrolldown, cm );
	}

	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 * If scrolldown is true, then the window will be scrolled down to the position of this button (if possible).
	 * In confirmMessage you can provide a message text which has to be confirmed if the button is pressed.
	 */
	String xpButton(String label, String event, String tooltip, boolean scrolldown, String confirmMessage)
	{
		return xpButton(label, event, tooltip, scrolldown, confirmMessage, null);
	}
	
	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 * If scrolldown is true, then the window will be scrolled down to the position of this button (if possible).
	 */
	String xpButton(String label, String event, String tooltip, boolean scrolldown)
	{
		return xpButton(label, event, tooltip, scrolldown, null);
	}
	
	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 */
	String xpButton(String label, String event)
	{
		return xpButton(label, event, "", false);
	}
	
	/**
	 * Creates an xp-style clickbutton with the given label which triggers the given event.
	 * The label string may contain html-tags, including <img> e.g.
	 */
	String xpButton(String label, String event, String tooltip)
	{
		return xpButton(label, event, tooltip, false);
	}

	/**
	 * Creates a disabled xp-style button with the given label.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 */
	String xpButtonDisabled(String label, String tooltip)
	{
		String output = "";
		output = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
				 + "	<tr>\n"
				 + "		<td>\n"
		       + "  		<table height=\"23\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" title=\"" + tooltip + "\" "
		       + "				onMouseover=\"window.status='" + tooltip + "'; return true;\" onMouseout=\"window.status=''; return true;\">\n"
		       + "    			<tr>\n"
		       + "      			<td style=\"width:4px\" background=\"images/btn_disabled_left.gif\">&nbsp;</td>\n"
		       + "					<td style=\"white-space:nowrap;\" background=\"images/btn_disabled_middle.gif\" style=\"vertical-align: middle; color:#a1a192;\">\n"
       	    + "      				&nbsp; &nbsp; " + label + " &nbsp; &nbsp; \n"
		       + "					</td>\n"
		       + "      			<td style=\"width:4px\" background=\"images/btn_disabled_right.gif\">&nbsp;</td>\n"
		       + "    			</tr>\n"
		       + "			</table>\n"
		       + "		</td>\n"
		       + "	</tr>\n"
		       + "</table>\n";

		return output;
 	}
	
	/**
	 * Creates a disabled xp-style button with the given label.
	 * The label string may contain html-tags, including <img> e.g.
	 */
	String xpButtonDisabled(String label)
	{
		return xpButtonDisabled(label, "");
	}

	/**
	 * Creates an xp-style clickbutton with the given label which uses the given url string to create an appropriate link.
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 */
	String xpButtonURL(String label, String url, String tooltip)
	{
		String buttonIDString = "xpBtn_" + buttonID;
		String output = "";
		output = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" title=\"" + tooltip + "\">\n"
				 + "	<tr>\n"
				 + "		<td>\n"
				 + "			<a href=\"#\" hideFocus=\"true\" style=\"text-decoration:none; \" onMouseover=\"window.status='" + tooltip + "'; mOver('" + buttonIDString + "'); return true;\" " + 
					   			"onMouseout=\"window.status=''; mOut('" + buttonIDString + "'); return true;\" onMousedown=\"mClick('" + buttonIDString +"'); return true;\" " +
                  			"onMouseup=\"mOver('" + buttonIDString + "'); return true;\" onFocus=\"mFocus('" + buttonIDString + "'); return true;\" "+
					   			"onBlur=\"mBlur('" + buttonIDString + "'); return true;\" onClick=\"open_window('" + url + "', '_blank', '" + tooltip + "'); return true;\">\n"
		       + "  			<table height=\"23\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
		       + "    				<tr>\n"
		       + "      				<td id=\"" + buttonIDString + "_left\" style=\"width:4px\" background=\"images/btn_norm_left.gif\">&nbsp;</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_middle\" style=\"white-space:nowrap;\" background=\"images/btn_norm_middle.gif\" style=\"vertical-align: middle;\">\n"
       	    + "							&nbsp; &nbsp; " + label + " &nbsp; &nbsp; \n"
		       + "						</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_right\" style=\"width:4px\" background=\"images/btn_norm_right.gif\">&nbsp;</td>\n"
		       + "    				</tr>\n"
		       + "				</table>\n"
		       + "			</a>\n"
		       + "		</td>\n"
		       + "	</tr>\n"
		       + "</table>\n";

		buttonID++;
		return output;
 	}

	/**
	 * Creates an xp-style clickbutton with the given label which uses the given parameters string to create an appropriate link.
	 * (parameters must be something like "&open=xyzPK" or "&searchtype=product&autoperform=true" etc.).
	 * The label string may contain html-tags, including <img> e.g.
	 * You can also provide a tooltip for this button.
	 */
	String xpButtonLocalLink(String label, String parameters, String tooltip)
	{
		String buttonIDString = "xpBtn_" + buttonID;
		String output = "";
		output = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" title=\"" + tooltip + "\">\n"
				 + "	<tr>\n"
				 + "		<td>\n"
				 + "			<a href=\"" + "#" + "\" hideFocus=\"true\" onMouseover=\"window.status='" + tooltip + "'; mOver('" + buttonIDString + "'); return true;\" " + 
					   			"onMouseout=\"window.status=''; mOut('" + buttonIDString + "'); return true;\" onMousedown=\"mClick('" + buttonIDString +"'); return true;\" " +
                  			"onMouseup=\"mOver('" + buttonIDString + "'); return true;\" onFocus=\"mFocus('" + buttonIDString + "'); return true;\" "+
					   			"onBlur=\"mBlur('" + buttonIDString + "'); return true;\" onClick=\"window.location.href=window.location.href+'" + parameters + "'; return true;\">\n"
		       + "  			<table height=\"23\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
		       + "    				<tr>\n"
		       + "      				<td id=\"" + buttonIDString + "_left\" style=\"width:4px\" background=\"images/btn_norm_left.gif\">&nbsp;</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_middle\" style=\"white-space:nowrap;\" background=\"images/btn_norm_middle.gif\" style=\"vertical-align: middle;\">\n"
       	    + "							&nbsp; &nbsp; " + label + " &nbsp; &nbsp; \n"
		       + "						</td>\n"
		       + "      				<td id=\"" + buttonIDString + "_right\" style=\"width:4px\" background=\"images/btn_norm_right.gif\">&nbsp;</td>\n"
		       + "    				</tr>\n"
		       + "				</table>\n"
		       + "			</a>\n"
		       + "		</td>\n"
		       + "	</tr>\n"
		       + "</table>\n";

		buttonID++;
		return output;
 	}

	String swapButton(String buttonName)
	{
		String output = "";
		
		output = "if(document.getElementById)\n"
			   + "{\n"
			   + "  document.getElementById(id + '_left').style.backgroundImage = 'url(images/" + buttonName + "_left.gif)';\n"
			   + "  document.getElementById(id + '_middle').style.backgroundImage = 'url(images/" + buttonName + "_middle.gif)';\n"
			   + "  document.getElementById(id + '_right').style.backgroundImage = 'url(images/" + buttonName + "_right.gif)';\n"
			   + "}\n"
			   + "else if(document.all)\n"
			   + "{\n"
			   + "  document.all.item(id + '_left').background = \"images/" + buttonName + "_left.gif\";\n"
			   + "  document.all.item(id + '_middle').background = \"images/" + buttonName + "_middle.gif\";\n"
			   + "  document.all.item(id + '_right').background = \"images/" + buttonName + "_right.gif\";\n"
			   + "}\n";
			   
		return output;
	}
%>

<% if( DEBUG_COMMENTS ) { %><!-- end: xp_button.inc --><% } %>
