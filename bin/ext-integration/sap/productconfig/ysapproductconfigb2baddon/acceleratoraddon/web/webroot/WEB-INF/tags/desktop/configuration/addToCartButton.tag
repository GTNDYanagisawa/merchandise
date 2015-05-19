<%@ tag language="java" pageEncoding="ISO-8859-1"
	trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cssConf"
	uri="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration/sapproductconfig.tld"%>

<%@ attribute name="config" required="true"
	type="de.hybris.platform.sap.productconfig.facades.ConfigurationData"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>


<c:set var="buttonType">button</c:set>
<c:if
	test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
	<c:set var="buttonType">submit</c:set>
</c:if>

<c:if
	test="${product.variantType ne null and product.purchasable eq false and product.stock.stockLevelStatus.code ne 'outOfStock'}">
	<c:set var="buttonType">submit</c:set>
</c:if>

<spring:theme code="text.addToCart" var="addToCartText" />
<button type="${buttonType}"
	class="add_to_cart_button<c:if test="${fn:contains(buttonType, 'button')}"> out-of-stock</c:if>">
	<spring:theme code="text.addToCart" var="addToCartText" />
	<spring:theme code="basket.add.to.basket" />
</button>


