<%@include file="head.inc"%>
<%@include file="xp_button.inc"%>
<%
	ItemChip theChip = (ItemChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<p class="itemChipNotValid">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="itemChipNotValid">
				<%=localized("itemchipnotvalid")%>
			</td>

		</tr>
		<tr>
			<td style="icnvOkButton">
					<%= xpButtonWithCondition("<span style=\"width:60px; text-align:center;\">" + localized("ok") + "</span>",
													  "", localized("ok"), false, "window.close()") %>
					
<!--				<input id="okbutton" type="image" src="images/icons/e_ok.gif" onclick="window.close()"/>-->
			</td>
		</tr>
	</table>
</p>
