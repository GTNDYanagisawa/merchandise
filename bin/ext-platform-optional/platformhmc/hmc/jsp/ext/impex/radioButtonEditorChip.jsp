<%@include file="../../head.inc"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>

<%@page import="de.hybris.platform.impex.hmc.export.*"%>

<%
	final RadioButtonEditorChip theChip = (RadioButtonEditorChip) request.getAttribute( AbstractChip.CHIP_KEY );
	final String name = theChip.getEventID( StringLayoutChip.SET_VALUE );
%>
<table>
	<%
	Map choices = theChip.getRadioChoices();
	Iterator iter=choices.entrySet().iterator();
	while(iter.hasNext()){
		Map.Entry entry=(Map.Entry) iter.next();
		String key=(String) entry.getKey();
		String value=(String) entry.getValue();
		boolean checked = theChip.getValue().toString().equals(key);
	%>
	<tr>
		<td>
			<input type="radio" <%= checked ? "checked=\"checked\"" : "" %> name="<%= name %>" id="<%= theChip.getID()+"_"+key %>" value="<%=key%>"/>
		</td>
		<td onclick="document.getElementById('<%= theChip.getID() + "_" + key %>').checked = true;">
			<%=localized( value )%>
		</td>
	</tr>
	<%
	}
	%>
</table>
