<%@include file="../head.inc"%>
<%
	final EncryptedPasswordEditorChip theChip = (EncryptedPasswordEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final boolean canChange = theChip.canChange();

	if( theChip.canRead() )
	{
%>
		<table class="encryptedPasswordEditorChip">
			<tr>
				<td class="arrowButton">&nbsp;</td>
		 		<td class="attrLabel" <%= canChange ? "" : " style=\"color:silver;\" "%> colspan="2"><%= localized("encryption") %>:</td>
		 		<td align="left">
		 			<select name="<%= theChip.getEventID(EncryptedPasswordEditorChip.SET_ENCRYPTION) %>" 
		 					  id="<%= theChip.getUniqueName() %>_select" 
		 					  <%= canChange ? "" : "disabled=\"disabled\"" %> 
		 					  >
<%
		 				for( String encoding : theChip.getInstalledEncryptionMethods() )
		 				{
%>
		 				<option value="<%=encoding%>" <%=(encoding.equals(theChip.getEncryptionMethod())?"selected":"")%>><%=localized("encrypt_"+encoding)%></option>
<%
		 				}
%>
		 			</select>
		 		</td>
			</tr>
			<tr>
				<td class="arrowButton">&nbsp;</td>
		 		<td class="attrLabel" <%= canChange ? "" : " style=\"color:silver;\" "%> colspan="2"><%= localized("newpassword") %>:</td>
		 		<td>
		 			<input 
		 				type="password"
		 				name="<%= theChip.getEventID(EncryptedPasswordEditorChip.SET_NEW_PASSWORD) %>"
		 				value="<%= theChip.getNewPassword() %>"
		 				id="<%= theChip.getUniqueName() %>_new"
 					   <%= canChange ? "" : "disabled=\"disabled\"" %> 
		 				>
		 		</td>
			</tr>
			<tr>
				<td class="arrowButton">&nbsp;</td>
		 		<td class="attrLabel" <%= canChange ? "" : " style=\"color:silver;\" "%> colspan="2"><%= localized("repeatpassword") %>:</td>
		 		<td>
		 			<input 
		 				type="password"
		 				name="<%= theChip.getEventID(EncryptedPasswordEditorChip.REPEAT_NEW) %>"
		 				value="<%= theChip.getRepeat() %>"
		 				id="<%= theChip.getUniqueName() %>_repeat"
		 				<%= canChange ? "" : "disabled=\"disabled\"" %> 
		 				>
		 		</td>
			</tr>
		</table>
<%
	}
%>
