<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.voucher.hmc.VoucherCodeGeneratorChip"%>
<%@page import="de.hybris.platform.voucher.jalo.Voucher"%>
<%
	final VoucherCodeGeneratorChip theChip = (VoucherCodeGeneratorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean active = theChip.isActive();
%>
	<table border="0" cellpadding="0" cellspacing="0" style="padding-top:10px;">
		<tr>
			<td style="padding-left:20px;padding-right:5px;" width="120px" >
				<%= localized("text.serialvoucher.setquantity.label" )%>
			</td>
			<td>
				<input class="editorform" size="6" type="text" 
						 id="<%= theChip.getUniqueName() %>_input"
						 name="<%= theChip.getEventID(VoucherCodeGeneratorChip.QUANTITY) %>" value="1" <%= (active ? "" : "disabled") %>&nbsp;
			</td>
			<td>&nbsp;</td>
			<td>
				<div class="xp-button<%= active ? " chip-event" : "-disabled" %>">
					<a style="width:100px;" href="#" title="<%= localized("btn.generate.voucher.codes") %>" name="<%= theChip.getEventID(VoucherCodeGeneratorChip.GENERATE) %>" hidefocus="true">
						<span>
							<span class="label" id="<%= theChip.getUniqueName() %>_div">
								<%= localized("btn.generate.voucher.codes") %>
							</span>
						</span>
					</a>
				</div>
			</td>
		</tr>
	</table>
	
