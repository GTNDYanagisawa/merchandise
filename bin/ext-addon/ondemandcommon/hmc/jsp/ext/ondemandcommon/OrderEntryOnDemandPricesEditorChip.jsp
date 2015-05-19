<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.ondemandcommon.hmc.editors.OrderEntryOnDemandPricesEditorChip"%>

<%
OrderEntryOnDemandPricesEditorChip.bindLocalizingMap(request, theDisplayState, "messageService");
OrderEntryOnDemandPricesEditorChip chip = (OrderEntryOnDemandPricesEditorChip)request.getAttribute("theChip");
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

<table cellspacing="0" cellpadding="0" title="onDemandPrices" class="attributeChip">
	<tbody>
	<tr>
		<td class="arrowButton">
			<div class="arrowButton">&nbsp;</div>
		</td>
		<!-- attribute editor -->
		<td style="width:400px;">
			<table cellspacing="0" cellpadding="0" class="listtable selecttable">
				<tbody>
				<tr>
					<th>
						<div title="UnitPrice" style="width:130px;" class="gilcEntry">${messageService['msg.unitprice']}</div>
					</th>

					<th>
						<div title="UnitTax" style="width:130px;" class="gilcEntry">${messageService['msg.unittax']}</div>
					</th>
					<th>
						<div title="EntryPrice" style="width:130px;" class="gilcEntry">${messageService['msg.entryprice']}</div>
					</th>

					<th>
						<div title="EntryTax" style="width:130px;" class="gilcEntry">${messageService['msg.entrytax']}</div>
					</th>
					<th>
						<div title="EntryTotalWithTax" style="width:130px;" class="gilcEntry">${messageService['msg.entrypricetotal']}</div>
					</th>
				</tr>

				<tr>
					<td style="width:130px; ">
						<div style="width:130px; " class="gilcEntry"><%= chip.getEntryInfo().getUnitPrice() %></div>
					</td>
					<td style="width:130px; ">
						<div style="width:130px; " class="gilcEntry"><%= chip.getEntryInfo().getUnitTax() %></div>
					</td>
					<td style="width:130px; ">
						<div style="width:130px; " class="gilcEntry"><%= chip.getEntryInfo().getEntryPrice() %></div>
					</td>
					<td style="width:130px; ">
						<div style="width:130px; " class="gilcEntry"><%= chip.getEntryInfo().getEntryTax() %></div>
					</td>
					<td style="width:130px; ">
						<div style="width:130px; " class="gilcEntry"><%= chip.getEntryInfo().getEntryTotal() %></div>
					</td>
				</tr>
				</tbody>
			</table>
		</td>
	</tr>
	</tbody>
</table>
