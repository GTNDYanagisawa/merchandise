<%@include file="../head.inc"%>
<%
	GenericMapTypeListEntryChip theChip= (GenericMapTypeListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<td class="gmtlecEntry">
<%
	if( theChip.getKeyEditor() != null )
	{
		theChip.getKeyEditor().render( pageContext );
	}
%>
</td>

<td style="width:5px;"><div style="width:5px;"> </div></td>

<td class="gmtlecEntry2">
<%
	if( theChip.getValueEditor() != null )
	{
		theChip.getValueEditor().render( pageContext );
	}
%>
</td>
