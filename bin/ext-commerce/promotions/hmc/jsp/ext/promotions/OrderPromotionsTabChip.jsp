<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.promotions.hmc.OrderPromotionsTabChip"%>
<%@page import="de.hybris.platform.promotions.jalo.PromotionResult"%>
<%@page import="de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed"%>
<%@page import="de.hybris.platform.promotions.result.WrappedOrderEntry"%>

<%
	OrderPromotionsTabChip.bindLocalizingMap(request, theDisplayState, "message");
	OrderPromotionsTabChip chip = (OrderPromotionsTabChip)request.getAttribute("theChip");
%>

<!-- Start Hybris header -->
<table cellspacing="0" cellpadding="0" width="100%" style="height:100%;">
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" class="propertytable" width="100%">
				<tr class="propertyrow">
					<td class="sectionheader">${message['section.abstractorder.promotions']}</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table cellspacing="0" cellpadding="20" width="100%" height="10px">
				<tr nowrap>
					<td width="100%">
						<div id="promo-abstractorderPromotionsTab">
<!-- End Hybris header -->
							<h2>${message['msg.poe.firedProductPromotions']}</h2>
							
							<% if (chip.getPromotionResults().getFiredProductPromotions() != null && !chip.getPromotionResults().getFiredProductPromotions().isEmpty()) { %> 
								<ul class="promo-firedProductPromotions">
									<% for (PromotionResult result: chip.getPromotionResults().getFiredProductPromotions()) { %>
										<li class="fireProductPromotion <%=(result.isApplied()?"'promo-applied'":"'promo-notApplied'") %>">
											<div class="promo-heading">
												<span class="promo-result">
													<% if (result.isApplied()) { %>
														${message['msg.poe.applied']}
													<% } else { %>
														${message['msg.poe.notApplied']}
													<% } %>
												</span>
												<span class="promo-code">[<%= result.getPromotion().getCode() %>]</span>
												<span class="promo-description"><%= result.getDescription() %></span>
											</div>
											<table cellspacing="0" cellpadding="0" class="promo-orderEntries" border="0">
												<thead>
													<tr>
														<td>${message['msg.poe.header.quantity']}</td>
														<td>${message['msg.poe.header.unit']}</td>
														<td>${message['msg.poe.header.product']}</td>
														<td>${message['msg.poe.header.basePrice']}</td>
														<td>${message['msg.poe.header.totalPrice']}</td>
														<td>${message['msg.poe.header.adjustedBasePrice']}</td>
														<td>${message['msg.poe.header.adjustedTotalPrice']}</td>
													</tr>
												</thead>
												<tbody>
													<% for (PromotionOrderEntryConsumed c : result.getConsumedEntries()) {
														out.println("<tr><td>" + c.getQuantity() + "</td>");
														out.println("<td>" + c.getUnit().getName() + "</td>");
														out.println("<td>" + c.getProduct().getCode() + "</td>");
														out.println("<td>" + c.getUnitPrice() + "</td>");
														out.println("<td>" + c.getEntryPrice() + "</td>");
														out.println("<td>" + c.getAdjustedUnitPrice() + "</td>");
														out.println("<td>" + c.getAdjustedEntryPrice() + "</td></tr>");														
													}%>
												</tbody>
											</table>
										</li>
									<% } %>
								</ul>
							<% } %>
							<h2>${message['msg.poe.potentialProductPromotions']}</h2>
							<% if (chip.getPromotionResults().getPotentialProductPromotions() != null && !chip.getPromotionResults().getPotentialProductPromotions().isEmpty()) { %>
								<ul class="promo-potentialProductPromoList">
									<% for (WrappedOrderEntry entry: chip.getPromotionResults().getEntriesWithPotentialPromotions()) { %>
										<li class="promo-potentialProductPromo">
											<div class="promo-heading">
												<span class="promo-description"><%= entry.getProduct().getCode()%></span>
											</div>
											<dl class="promo-productInfo">
												<dt>${message['msg.poe.header.quantity']}</dt><dd><%= entry.getQuantity()%></dd>
												<dt>${message['msg.poe.header.unit']}</dt><dd><%= entry.getUnit().getName()%></dd>
												<dt>${message['msg.poe.header.basePrice']}</dt><dd><%= entry.getUnitPrice()%></dd>
												<dt>${message['msg.poe.header.totalPrice']}</dt><dd><%= entry.getEntryPrice()%></dd>
											</dl>
											<ul class="promo-potentialPromotionList">
												<% for (PromotionResult result : entry.getPromotionResults()) { %>
													<li>
														<span class="promo-code">[<%= result.getPromotion().getCode()%>]</span>
														<span class="promo-description"><%= result.getDescription()%></span>
														<span class="promo-certainty">${message['msg.poe.certainty']} <%= result.getCertainty()%></span>
													</li>
												<% } %>
											</ul>
										</li>
									<% } %>
								</ul>
							<% } %>
							
							<h2>${message['msg.poe.productsWithoutPromotions']}</h2>
							<% if (chip.getPromotionResults().getEntriesNotInPromotions() != null && !chip.getPromotionResults().getEntriesNotInPromotions().isEmpty()) { %>
								<table cellspacing="0" cellpadding="0" class="promo-orderEntries" border="0">
									<thead>
										<tr>
											<td>${message['msg.poe.header.quantity']}</td>
											<td>${message['msg.poe.header.unit']}</td>
											<td>${message['msg.poe.header.product']}</td>
											<td>${message['msg.poe.header.basePrice']}</td>
											<td>${message['msg.poe.header.totalPrice']}</td>
										</tr>
									</thead>
									<tbody>
										<% for (WrappedOrderEntry promotionOrderEntry: chip.getPromotionResults().getEntriesNotInPromotions()) { %>
											<tr>
												<td>
													<%= promotionOrderEntry.getQuantity() %>
												</td>
												<td>
													<%= promotionOrderEntry.getUnit().getName() %>
												</td>
												<td>
													<%= promotionOrderEntry.getProduct().getCode() %>
												</td>
												<td>
													<%= promotionOrderEntry.getUnitPrice() %>
												</td>
												<td>
													<%= promotionOrderEntry.getEntryPrice() %>
												</td>
											</tr>
										<% } %>
									</tbody>
								</table>
							<% } %>
							
							<h2>${message['msg.poe.firedOrderPromotions']}</h2>
							<% if (chip.getFiredOrderPromotions() != null && !chip.getFiredOrderPromotions().isEmpty()) { %>
								<ul class="promo-firedOrderPromotions">
									<% for (PromotionResult result : chip.getFiredOrderPromotions()) { %>
										<li class="<%= (result.isApplied()?"'promo-applied'":"'promo-notApplied'") %>">
											<div class="promo-heading">
												<span>
													<% if (result.isApplied()) { %>
														${message['msg.poe.applied']}
													<% } else { %>
														${message['msg.poe.notApplied']}
													<% } %>
												</span>
												<span class="promo-code">[<%= result.getPromotion().getCode() %>]</span>
												<span class="promo-description"><%= result.getDescription() %></span>
											</div>
										</li>
									<% } %>
								</ul>
							<% } %>
							
							<h2>${message['msg.poe.potentialOrderPromotions']}</h2>
							<% if (chip.getPotentialOrderPromotions() != null && !chip.getPotentialOrderPromotions().isEmpty()) { %>
								<ul class="promo-potentialOrderPromotions">
									<% for (PromotionResult result : chip.getPotentialOrderPromotions()) { %> 
										<li>
											<div class="promo-heading">
												<span>${message['msg.poe.potentialPromotion']}</span>
												<span class="promo-code">[<%= result.getPromotion().getCode() %>]</span>
												<span class="promo-description"><%= result.getDescription() %></span>
												<span class="promo-certainty">${message['msg.poe.certainty']} <%= result.getCertainty()%></span>
											</div>
										</li>
									<% } %>
								</ul>
							<% } %>
<!-- Start Hybris footer -->
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<!-- End hybris footer -->