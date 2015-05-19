<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty productReferences}">
			<div class="title">${title}</div>
			<ul id="recommendationUL" class="carousel jcarousel-skin">
				<c:forEach end="5" items="${productReferences}" var="productReference">
				<li>
					<c:url value="${productReference.target.url}" var="productQuickViewUrl"/>
						<a href="${productQuickViewUrl}" class="popup scrollerProduct">
							<div class="thumb">
								<product:productPrimaryImage product="${productReference.target}" format="product"/>
							</div>
							<div class="priceContainer"><format:fromPrice priceData="${productReference.target.price}"/></div>
							<div class="details">${productReference.target.name}</div>
						</a>
				</li>
				</c:forEach>
			</ul>
	</c:when>

	
	<c:otherwise>
	</c:otherwise>
</c:choose>

