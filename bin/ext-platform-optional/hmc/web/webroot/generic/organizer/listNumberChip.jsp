<%@include file="../../head.inc"%>
<%
	ListNumberChip theChip = (ListNumberChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean noColumns = theChip.getNumberColumns().isEmpty();
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
					<%= localized("list.number.no.columns") %>
				</span>
<%
			}
			else
			{
%>			
				<select name="<%=theChip.getEventID(ListOperationChip.CURRENT_COLUMN)%>" onchange="editorForm.submit()" id="<%= theChip.getUniqueName() %>_select">
					<%
						for (final Iterator it = theChip.getNumberColumns().iterator(); it.hasNext(); )
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
		<td><%= localized("startrow") %>:</td>
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
			<td><% theChip.getStartRowNumberEditor().render(pageContext); %></td>
<%
		}
%>
	</tr>
	<tr>
		<td><%= localized("startvalue") %>:</td>
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
			<td><% theChip.getStartNumberEditor().render(pageContext); %></td>
<%
		}
%>
	</tr>
	<tr>
		<td><%= localized("increment") %>:</td>
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
			<td><% theChip.getIncrementEditor().render(pageContext); %></td>
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
					id="<%= theChip.getUniqueName() %>_allvisible"					
					<%=(theChip.doesOnlySelected()?"":" checked")%> 
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
				<a style="width:130px;" href="#" title="<%= localized("list.number.perform") %>" name="<%= theChip.getCommandID(ListNumberChip.PERFORM) %>" hidefocus="true">
					<span>
						<span class="label" id="<%= theChip.getUniqueName() %>_perform">
							<%= localized("list.number.perform") %>
						</span>
					</span>
				</a>
			</div>
<%--
			if( noColumns )
			{
%>
				<%= xpButtonDisabled( localized( "list.number.perform")) %>
<%
			}
			else
			{
%>
				<%= xpButton( localized( "list.number.perform" ), theChip.getCommandID(ListNumberChip.PERFORM), localized("list.number.perform")) %>
<%
			}
--%>
		</td>
	</tr>
</table>
		
