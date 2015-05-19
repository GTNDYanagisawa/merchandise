<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.voucher.hmc.VoucherEditorChip"%>
<%
	final VoucherEditorChip theChip = (VoucherEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean active = theChip.isActive();
%>
<table class="attribute" cellspacing="0" cellpadding="0">
	<tr>
		<td width="16px">&nbsp;</td>
		<td width="100px">&nbsp;</td>
		<td width="30px">&nbsp;</td>
		<td>
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<input type="text" name="<%=theChip.getEventID(VoucherEditorChip.VOUCHERCODE)%>" id="<%= theChip.getUniqueName() %>_input" <%= (active ? "" : "disabled")%>>&nbsp;
					</td>
					<td>&nbsp;</td>
					<td>
						<div class="xp-button<%= active ? " chip-event" : "-disabled" %>">
							<a style="width:130px;" href="#" title="<%= localized("btn.redeem.voucher") %>" name="<%= theChip.getEventID(VoucherEditorChip.REDEEM) %>" hidefocus="true">
								<span>
									<span class="label" id="<%= theChip.getUniqueName() %>_redeem">
										<%= localized("btn.redeem.voucher") %>
									</span>
								</span>
							</a>
						</div>
					</td>
					<td>&nbsp;</td>
					<td>
						<div class="xp-button<%= active ? " chip-event" : "-disabled" %>">
							<a style="width:130px;" href="#" title="<%= localized("btn.release.voucher") %>" name="<%= theChip.getEventID(VoucherEditorChip.RELEASE) %>" hidefocus="true">
								<span>
									<span class="label" id="<%= theChip.getUniqueName() %>_release">
										<%= localized("btn.release.voucher") %>
									</span>
								</span>
							</a>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
<% 
	if( active )
	{ 
%>
	<tr>
		<td width="16px">&nbsp;</td>
		<td width="100px">
		   <div style="width: 100px; white-space: normal;">
		   <%= localized( "voucher.assigned" ) %>:
		   </div>
		</td>
		<td width="30px">&nbsp;</td>
		<td><% theChip.getDiscountList().render(pageContext); %></td>
	</tr>
<%	
	}
%>
</table>

