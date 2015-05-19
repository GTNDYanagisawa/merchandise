<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ attribute name="group" required="true"
	type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="showGroupTitle" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="showProductSummary" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="parentIsTab" required="true"
	type="java.lang.Boolean"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld"%>


<c:if test="${showGroupTitle}">
	<jsp:useBean id="cons"
		class="de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler"
		scope="session" />

	<div tabindex="0" id="${group.id}_title"
		class="${cssConf:groupStyleClasses(group)}">
		<div class="product-config-group-title">
			<c:choose>
				<c:when test="${group.name eq cons.getGeneralGroupName()}">
					<spring:message code="sapproductconfig.group.general.title"
						text="General (Default)" />
				</c:when>
				<c:otherwise>
					<c:out value="${group.description}" />
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>



<c:choose>
	<c:when test="${showGroupTitle}">
		<c:set var="groupStyle"
			value="product-config-group product-config-group-border" />
	</c:when>
	<c:otherwise>
		<c:set var="groupStyle" value="product-config-group-hidden" />
	</c:otherwise>
</c:choose>

<div <c:if test="${group.collapsed}">style="display:none"</c:if>
	class="${groupStyle}">
	<c:if
		test="${showProductSummary && (showGroupTitle ||  parentIsTab)  && not empty group.summaryText}">
		<div class="product-config-summary-text product-config-cstic">
			<c:out value="${group.summaryText}" />
		</div>
	</c:if>

	<c:set var="bindResult"
		value="${requestScope['org.springframework.validation.BindingResult.config']}" />
	<config:headErrorMessages bindResult="${bindResult}"
		groupPath="${pathPrefix}" />

	<c:forEach var="cstic" items="${group.cstics}" varStatus="csticStatus">
		<c:set value="${pathPrefix}cstics[${csticStatus.index}]." var="path" />
		<config:cstics cstic="${cstic}" pathPrefix="${path}" />
	</c:forEach>

	<form:input type="hidden" id="${group.id}_collapsed"
		path="${pathPrefix}collapsed" />
	<form:input type="hidden"
		id="node_${group.id}_collapsedInSpec"
		path="${pathPrefix}collapsedInSpecificationTree" />
	<form:input type="hidden" path="${pathPrefix}name" />
	<form:input type="hidden" path="${pathPrefix}id" />
	<form:input type="hidden" path="${pathPrefix}groupType" />
	<c:forEach var="subgroup" items="${group.subGroups}"
		varStatus="groupStatus">
		<c:set value="${pathPrefix}subGroups[${groupStatus.index}]."
			var="path" />
		<c:choose>
			<c:when test="${subgroup.configurable}">
				<config:group group="${subgroup}" pathPrefix="${path}"
					showGroupTitle="${!group.oneConfigurableSubGroup}"
					showProductSummary="${showProductSummary}" parentIsTab="false" />
			</c:when>
			<c:otherwise>
				<config:hiddenGroup group="${subgroup}" pathPrefix="${path}" />
			</c:otherwise>
		</c:choose>
	</c:forEach>
</div>