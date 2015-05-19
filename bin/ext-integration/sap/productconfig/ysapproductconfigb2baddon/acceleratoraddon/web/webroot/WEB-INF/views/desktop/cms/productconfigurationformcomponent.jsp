<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<c:set var="bindResult"
	value="${requestScope['org.springframework.validation.BindingResult.config']}" />
<c:set var="multiGroups" value="${fn:length(config.groups) gt 1}" />


<div id="configurationForm" class="product-config-form">

	<form:form id="configform" method="POST" modelAttribute="config">
		<c:if test="${not empty addedToCart}">
			<config:addedToCartPopup config="${config}" product="${product}"
				bindResult="${bindResult}" addMode="${addedToCart}" />
		</c:if>

		<div class="product-config-groups">
			<c:forEach var="group" items="${config.groups}"
				varStatus="groupStatus">
				<c:set value="groups[${groupStatus.index}]." var="path" />
				<c:choose>
					<c:when test="${groupStartLevel == 0}">
						<c:choose>
							<c:when test="${group.configurable}">
								<config:group group="${group}" pathPrefix="${path}"
									showGroupTitle="${multiGroups}"
									showProductSummary="${showSummary}" parentIsTab="false" />
							</c:when>
							<c:otherwise>
								<config:hiddenGroup group="${group}" pathPrefix="${path}" />
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${group.collapsed}">
								<config:hiddenGroup group="${group}" pathPrefix="${path}" />
							</c:when>
							<c:otherwise>
								<config:group group="${group}" pathPrefix="${path}"
									showGroupTitle="false" showProductSummary="${showSummary}"
									parentIsTab="true" />
							</c:otherwise>
						</c:choose>

					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<form:input type="hidden" value="${config.kbKey.productCode}"
			path="kbKey.productCode" />
		<form:input type="hidden" value="${config.kbKey.kbName}"
			path="kbKey.kbName" />
		<form:input type="hidden" value="${config.kbKey.kbLogsys}"
			path="kbKey.kbLogsys" />
		<form:input type="hidden" value="${config.kbKey.kbVersion}"
			path="kbKey.kbVersion" />
		<form:input type="hidden" value="${config.configId}" path="configId" />
		<form:input type="hidden" value="${config.selectedGroup}"
			path="selectedGroup" />
		<form:input type="hidden" value="${config.cartItemPK}"
			path="cartItemPK" />
		<form:input type="hidden" value="${autoExpand}" path="autoExpand" />
		<form:input type="hidden" value="${focusId}" path="focusId" />
		<form:input type="hidden" value="${config.specificationTreeCollapsed}" path="specificationTreeCollapsed" />
		<form:input type="hidden" value="${config.priceSummaryCollapsed}" path="priceSummaryCollapsed" />
	</form:form>
	<config:legend visible="${config.showLegend}" />
</div>
