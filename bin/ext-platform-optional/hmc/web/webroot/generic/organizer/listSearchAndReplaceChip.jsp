<%@include file="../../head.inc"%>
<%
	ListSearchAndReplaceChip theChip = (ListSearchAndReplaceChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean noColumns = theChip.getSearchableColumns().isEmpty();
%>
<table style="height:100%;">
	<tr>
		<td><%=localized("column")%>:</td>
		<td>
<%
			if( noColumns )
			{
%>
				<span style="color:red;">
					<%= localized("list.searchreplace.no.columns") %>
				</span>
<%
			}
			else
			{
%>			
				<select name="<%= theChip.getEventID(ListSearchAndReplaceChip.CURRENT_COLUMN )%>" onchange="editorForm.submit()" id="<%= theChip.getUniqueName() %>_select">
<%
						for (final Iterator it = theChip.getSearchableColumns().iterator(); it.hasNext(); )
						{
							String column = (String)it.next();
%>
								<option<%= (column.equals(theChip.getCurrentColumn()) ? " selected" : "") %> value="<%= column %>"><%= theChip.getAttributeDescriptorName(column) %></option>
<%
						}
%>
				</select>
<%
			}
%>
		</td>
	</tr>
	<tr>
		<td><%=localized("oldvalue")%>:</td>
<%
		if( noColumns )
		{
%>
			<td><%= localized("notdefined") %></td>
<%
		}
		else
		{
%>
			<td><%theChip.getOldValueEditor().render(pageContext);%></td>
<%
		}
%>
	</tr>
	<tr>
		<td><%=localized("newvalue")%>:</td>
<%
		if( noColumns )
		{
%>
			<td><%= localized("notdefined") %></td>
<%
		}
		else
		{
%>
			<td><%theChip.getNewValueEditor().render(pageContext);%></td>
<%
		}
%>
	</tr>
	<%
		if (theChip.isStringColumn())
		{
			%>
				<tr>
					<input type="hidden" name="<%= theChip.getEventID(ListSearchAndReplaceChip.SUBSTRING) %>" value="false">
					<td><%= localized("substring") %>:</td>
					<td>
						<input type="checkbox" 
								 name="<%= theChip.getEventID(ListSearchAndReplaceChip.SUBSTRING) %>"<%=(theChip.isUsingSubstrings()?" checked":"")%> 
								 value="true" 
								 <%= noColumns ? "disabled" : "" %> 
								 id="<%= theChip.getUniqueName() %>_substring"></td>
				</tr>
			<%
		}
	%>
	<tr>
		<td><%=localized("mode")%>:</td>
		<td>
			<input type="radio" 
					name="<%=theChip.getEventID(ListOperationChip.SELECTED_ONLY)%>" 
					value="<%=AbstractChip.FALSE%>"<%=(theChip.doesOnlySelected()?"":" checked")%> 
					id="<%= theChip.getUniqueName() %>_allvisible"
					<%= noColumns ? "disabled" : "" %>><%=localized("allvisible")%>
			&nbsp;&nbsp;
			<input type="radio" 
					name="<%=theChip.getEventID(ListOperationChip.SELECTED_ONLY)%>" 
					value="<%=AbstractChip.TRUE%>"
					<%=(theChip.doesOnlySelected()?" checked":"")%> 
					id="<%= theChip.getUniqueName() %>_selectedonly"
					<%= noColumns ? "disabled" : "" %>><%=localized("selectedonly")%>
		</td>
	</tr>
	<tr>
		<td>
			<div class="xp-button<%= !noColumns ? " chip-event" : "-disabled" %>">
				<a style="width:130px;" href="#" title="<%= localized("list.searchreplace.perform") %>" name="<%= theChip.getCommandID(ListSearchAndReplaceChip.PERFORM) %>" hidefocus="true">
					<span>
						<span class="label" id="<%= theChip.getUniqueName() %>_perform">
							<%= localized("list.searchreplace.perform") %>
						</span>
					</span>
				</a>
			</div>
		</td>
	</tr>
</table>
		
