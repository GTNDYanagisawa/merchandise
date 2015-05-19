<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout/single" %>
<%@ taglib prefix="addon" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/desktop/checkout/single" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<spring:url value="/checkout/single/summary/getCheckoutCart.json" var="getCheckoutCartUrl" />

<script type="text/javascript"> // set vars
	 var getCheckoutCartUrl = '${getCheckoutCartUrl}';
</script>



<div id="checkoutContentPanel" data-checkout-cart-url="${getCheckoutCartUrl}" class="clearfix summaryFlow">
	<addon:summaryFlowPaymentType cartData="${cartData}" />
	<addon:summaryFlowDeliveryAddress deliveryAddress="${deliveryAddress}" costCenter="${costCenter}"/>
	<checkout:summaryFlowPayment />
	<addon:summaryFlowDeliveryMode deliveryMode="${deliveryMode}" />
</div>