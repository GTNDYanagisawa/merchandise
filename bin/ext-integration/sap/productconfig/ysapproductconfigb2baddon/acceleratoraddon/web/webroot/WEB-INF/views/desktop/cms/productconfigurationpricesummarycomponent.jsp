<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cssConf"
	uri="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration/sapproductconfig.tld"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
	
<c:set var="collapsed" value="${config.priceSummaryCollapsed}" />

<div id="priceSummary"
	class="product-config-price-summary product-config-side-comp">
	<div id="priceSummaryTitle"
		class="product-config-side-comp-header product-config-price-summary-header ${cssConf:sideCompStyleClasses(collapsed, null)}">
		<div class="product-config-group-title">
			<spring:message code="sapproductconfig.pricesummary.title"
				text="Summary (Default)" />
		</div>
	</div>
	<div id="priceSummaryContent"
		class="product-config-group product-config-side-comp-border product-config-price-summary-content">
		<c:if test="${showPriceDetails}">
			<div id="priceSummarySubContent"
				<c:if test="${collapsed}">style="display:none"</c:if>>
				<div class="product-config-price-summary-sub-price-label">
					<spring:theme code="sapproductconfig.pricesummary.label.baseprice"
						text="Base Price (Default)" />
				</div>
				<div id="basePriceValue"
					class="product-config-price-summary-sub-price-value">
					${config.pricing.basePrice.formattedValue}
				</div>
				<div class="product-config-price-summary-sub-price-label">
					<spring:theme
						code="sapproductconfig.pricesummary.label.selectedoptionsprice"
						text="Selected Options (Default)" />
				</div>
				<div id="selectedOptionsValue"
					class="product-config-price-summary-sub-price-value">
					${config.pricing.selectedOptions.formattedValue}
				</div>
			</div>
		</c:if>
		<div class="product-config-price-summary-total-price-label">
			<spring:theme code="sapproductconfig.pricesummary.label.totalprice"
				text="Current Total (Default)" />
		</div>
		<div id="currentTotalValue"
			class="product-config-price-summary-total-price-value">
				${config.pricing.currentTotal.formattedValue}
		</div>

		<div class="product-config-price-summary-other-label"><spring:theme code="sapproductconfig.pricesummary.label.other"
						text="" /></div>

		<div class="product-config-addtocart">
			<config:addToCartButton config="${config}" product="${product}" />
		</div>
	</div>
</div>