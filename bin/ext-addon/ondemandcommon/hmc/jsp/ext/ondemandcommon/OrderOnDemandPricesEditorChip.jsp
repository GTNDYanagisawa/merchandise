<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.ondemandcommon.hmc.editors.OrderOnDemandPricesEditorChip"%>
<%@page import="de.hybris.platform.ondemandcommon.hmc.editors.OrderEntryInfo"%>

<%
OrderOnDemandPricesEditorChip.bindLocalizingMap(request, theDisplayState, "messageService");
OrderOnDemandPricesEditorChip chip = (OrderOnDemandPricesEditorChip)request.getAttribute("theChip");
%>

<!--
  ~ [y] hybris Platform
  ~
  ~ Copyright (c) 2000-2013 hybris AG
  ~ All rights reserved.
  ~
  ~ This software is the confidential and proprietary information of hybris
  ~ ("Confidential Information"). You shall not disclose such Confidential
  ~ Information and shall use it only in accordance with the terms of the
  ~ license agreement you entered into with hybris.
  -->

<table cellspacing="20" cellpadding="0" title="onDemandPrices" class="attributeChip">
	<tbody>
	<tr>
		<td class="arrowButton">
			<div class="arrowButton">&nbsp;</div>
		</td>
		<!-- label -->
		<td class="label" style="width:100px;overflow:hidden;">
			<div class="" style="width:100px;overflow:hidden;">
				${messageService['msg.orderEntries']}
			</div>
		</td>
		<!-- attribute editor -->
		<td style="width:550px;">
			<table cellspacing="0" cellpadding="0" class="listtable selecttable">
				<tbody>
				<tr>
					<th>
						<div title="Product" style="width:150px;" class="gilcEntry">${messageService['msg.product']}</div>
					</th>
					<th>
						<div title="UnitPrice" style="width:100px;" class="gilcEntry">${messageService['msg.unitprice']}</div>
					</th>
					<th>
						<div title="EntryPrice" style="width:130px;" class="gilcEntry">${messageService['msg.entryprice']}</div>
					</th>
					<th>
						<div title="UnitTax" style="width:100px;" class="gilcEntry">${messageService['msg.unittax']}</div>
					</th>
					<th>
						<div title="EntryTax" style="width:100px;" class="gilcEntry">${messageService['msg.entrytax']}</div>
					</th>
					<th>
						<div title="EntryTotalWithTax" style="width:130px;" class="gilcEntry">${messageService['msg.entrypricetotal']}</div>
					</th>
				</tr>

				<%
				for(OrderEntryInfo orderEntry : chip.getOrderEntryList())
				{
				%>
				<tr>
					<td style="width:150px; ">
						<div style="width:150px; " class="gilcEntry"><%= orderEntry.getEntryDesc() %></div>
					</td>
					<td style="width:100px; ">
						<div style="width:100px; " class="gilcEntry"><%= orderEntry.getUnitPrice() %></div>
					</td>
					<td style="width:100px; ">
						<div style="width:100px; " class="gilcEntry"><%= orderEntry.getEntryPrice() %></div>
					</td>
					<td style="width:100px; ">
						<div style="width:100px; " class="gilcEntry"><%= orderEntry.getUnitTax() %></div>
					</td>
					<td style="width:100px; ">
						<div style="width:100px; " class="gilcEntry"><%= orderEntry.getEntryTax() %></div>
					</td>
					<td style="width:130px; ">
						<div style="width:100px; " class="gilcEntry"><%= orderEntry.getEntryTotal() %></div>
					</td>
				</tr>
				<%
				}
				%>
				</tbody>
			</table>
		</td>
	</tr>
	<tr>
		<td class="arrowButton">
			<div class="arrowButton">&nbsp;</div>
		</td>
		<!-- label -->
		<td class="label" style="width:100px;overflow:hidden;">
			<div class="" style="width:100px;overflow:hidden;">
				${messageService['msg.order']}
			</div>
		</td>
		<!-- attribute editor -->
		<td style="width:300px;">
			<table cellspacing="0" cellpadding="0" class="listtable selecttable">
				<tbody>
				<tr>
					<th>
						<div title="Total" style="width:150px;" class="gilcEntry">${messageService['msg.orderprice']}</div>
					</th>

					<th>
						<div title="TotalTax" style="width:150px;" class="gilcEntry">${messageService['msg.ordertotaltax']}</div>
					</th>

					<th>
						<div title="TotalWithTax" style="width:150px;" class="gilcEntry">${messageService['msg.orderpricetotal']}</div>
					</th>
				</tr>

				<tr>
					<td style="width:150px; ">
						<div style="width:150px; " class="gilcEntry"><%= chip.getTotalPrice() %></div>
					</td>
					<td style="width:150px; ">
						<div style="width:150px; " class="gilcEntry"><%= chip.getTotalTax() %></div>
					</td>
					<td style="width:150px; ">
						<div style="width:150px; " class="gilcEntry"><%= chip.getTotalWithTax() %></div>
					</td>
				</tr>
				</tbody>
			</table>
		</td>
	</tr>
	</tbody>
</table>
