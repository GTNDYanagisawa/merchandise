<%@include file="../../head.inc"%>
<%
	ListFillChip theChip = (ListFillChip) request.getAttribute(AbstractChip.CHIP_KEY);
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
					<%= localized("list.fill.no.columns") %>
				</span>
<%
			}
			else
			{
%>			
				<select name="<%=theChip.getEventID(ListOperationChip.CURRENT_COLUMN)%>" onchange="editorForm.submit()" id="<%= theChip.getUniqueName() %>_select">
<%
						for (final Iterator it = theChip.getSearchableColumns().iterator(); it.hasNext(); )
						{
							String column = (String)it.next();
%>
								<option<%=(column.equals(theChip.getCurrentColumn())?" selected":"")%> value="<%=column%>"><%=theChip.getAttributeDescriptorName(column)%></option>
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
	<tr>
		<td><%=localized("mode")%>:</td>
		<td>
			<input type="radio" 
					name="<%=theChip.getEventID(ListOperationChip.SELECTED_ONLY)%>" 
					value="<%=AbstractChip.FALSE%>" 
					<%=(theChip.doesOnlySelected()?"":" checked")%> 
					id="<%= theChip.getUniqueName() %>_allvisible"					
					<%= noColumns ? "disabled" : "" %>>
				<%=localized("allvisible")%>
			&nbsp;&nbsp;
			<input type="radio" 
					name="<%=theChip.getEventID(ListOperationChip.SELECTED_ONLY)%>" 
					value="<%=AbstractChip.TRUE%>" 
					<%=(theChip.doesOnlySelected()?" checked":"")%> 
					id="<%= theChip.getUniqueName() %>_selectedonly"
					<%= noColumns ? "disabled" : "" %>>
				<%=localized("selectedonly")%>
		</td>
	</tr>
	<tr>
		<td>
			<div class="xp-button<%= !noColumns ? " chip-event" : "-disabled" %>">
				<a style="width:130px;" href="#" title="<%= localized("list.fill.perform") %>" name="<%= theChip.getCommandID(ListFillChip.PERFORM) %>" hidefocus="true">
					<span>
						<span class="label" id="<%= theChip.getUniqueName() %>_perform">
							<%= localized("list.fill.perform") %>
						</span>
					</span>
				</a>
			</div>
		</td>
	</tr>
</table>
		
