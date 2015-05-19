<%@include file="../head.inc"%>
<%
	final PasswordEditorChip theChip = (PasswordEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final boolean canChange = theChip.canChange();

	if( theChip.canRead() )
	{
%>
		<table class="passwordEditorChip">
			<tr>
				<td class="arrowButton">&nbsp;</td>
		 		<td class="attrLabel" <%= canChange ? "" : " style=\"color:silver;\" "%>><%=localized("newpassword")%>:</td>
		 		<td>
		 			<input type="password" 
		 					 name="<%=theChip.getEventID(PasswordEditorChip.SET_NEW_PASSWORD)%>" 
		 					 value="<%=theChip.getNewPassword()%>"
	 					    <%= canChange ? "" : "disabled=\"disabled\"" %> 
		 					 >
		 		</td>
			</tr>
				<tr>
				<td class="arrowButton">&nbsp;</td>
		 		<td class="attrLabel" <%= canChange ? "" : " style=\"color:silver;\" "%>><%=localized("repeatpassword")%>:</td>
		 		<td>
		 			<input type="password" 
		 					 name="<%=theChip.getEventID(PasswordEditorChip.REPEAT_NEW)%>" 
		 					 value="<%=theChip.getRepeat()%>"
	 					    <%= canChange ? "" : "disabled=\"disabled\"" %> 
		 					 >
		 		</td>
			</tr>
		</table>
<%
	}
%>
