<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ attribute name="node" required="true"
	type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="showNodeTitle" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="isFirstLevelNode" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="isLastNode" required="false"
	type="java.lang.Boolean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld"%>

<jsp:useBean id="cons"
	class="de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler"
	scope="session" />

<c:set var="nodeStyle"
	value="${cssConf:nodeStyleClasses(node, isFirstLevelNode, showNodeTitle)}" />
<c:set var="isNodeExpandable"
	value="${fn:contains(nodeStyle, cons.getExpandeableNodeClass())}" />


<c:if test="${showNodeTitle}">
	<div id="node_${node.id}_title" class="product-config-specification-node">
		<div
			class="product-config-specification-node-title <c:if test="${node.configurable}">product-config-specification-node-title-link</c:if>">
			<c:choose>
				<c:when test="${node.name eq cons.getGeneralGroupName()}">
					<spring:message code="sapproductconfig.group.general.title"
						text="General (Default)" />
				</c:when>
				<c:otherwise>
					<c:out value="${node.description}" />
				</c:otherwise>
			</c:choose>
		</div>
		<div class="${nodeStyle}"></div>
</c:if>


<c:set var="atLeastOneSubGroup"
	value="${fn:length(node.subGroups) gt 0}" />


<c:if
	test="${atLeastOneSubGroup && node.configurable && (!node.collapsedInSpecificationTree || !isFirstLevelNode)}">
	<div class="product-config-specification-node-sub">
		<c:forEach var="subNode" items="${node.subGroups}"
			varStatus="groupStatus">

			<c:set value="${pathPrefix}subGroups[${groupStatus.index}]."
				var="path" />

			<config:specificationNode node="${subNode}"
				showNodeTitle="${!node.oneConfigurableSubGroup || !subNode.configurable}"
				isFirstLevelNode="false" />

		</c:forEach>
		<c:if test="${isNodeExpandable && not isLastNode}">
			<hr class="product-config-specification-node-hr" />
		</c:if>
	</div>
</c:if>
<c:if test="${showNodeTitle}">
	</div>
</c:if>
