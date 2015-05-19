<%@include file="../head.inc"%>
<%
	GenericListLayoutChip theChip= (GenericListLayoutChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table class="genericListLayoutChip" cellpadding="0" cellspacing="0" 
		 <%=(theChip.getHeight()!=null ? "height=\""+theChip.getHeight()+"\"px" : "") %>
		 <%=(theChip.getWidth()!=null ? "width=\""+theChip.getWidth()+"\"" : "width=\"100%\"")%>
		 colspan="<%=theChip.getColSpan()%>"
		 rowspan="<%=theChip.getRowSpan()%>" >
<%
		int i = 0;
		for (final Iterator it = theChip.getChildren().iterator(); it.hasNext(); i++ )
		{
			final Chip chip = (Chip)it.next();
%>
	<tr class="glcTR">
		<td>
			<div class="glcDIV">
<%
				chip.render(pageContext);
%>
			</div>
		</td>
	</tr>
<%
		}
%>
</table>
