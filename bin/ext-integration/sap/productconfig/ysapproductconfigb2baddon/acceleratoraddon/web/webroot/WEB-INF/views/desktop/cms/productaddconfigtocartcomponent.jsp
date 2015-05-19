<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<div <c:if test="${showOnlyForLongConfigurations}">style="display:none"</c:if> class="product-config-addtocart<c:if test="${showOnlyForLongConfigurations}"> product-config-addtocart-showonly</c:if>">
	<config:addToCartButton config="${config}" product="${product}" />
</div>



