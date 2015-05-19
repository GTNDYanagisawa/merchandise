<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration/sapproductconfig.tld"%>

<c:set var="multiGroups" value="${fn:length(config.groups) gt 1}" />
<c:set var="collapsed" value="${config.specificationTreeCollapsed}" />
<c:if test="${multiGroups}">
	<c:set var="bindResult"
		value="${requestScope['org.springframework.validation.BindingResult.config']}" />

	<jsp:useBean id="cons"
		class="de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler"
		scope="session" />
	<div id="specificationTree" class="product-config-specification product-config-side-comp">
		<div id="specificationTitle" class="product-config-side-comp-header  product-config-specification-header ${cssConf:sideCompStyleClasses(collapsed, bindResult)}">
			<div class="product-config-group-title">
				<spring:message code="sapproductconfig.specification.title"
					text="Specification (Default)" />
			</div>
		</div>
		<div id="specificationContent" <c:if test="${collapsed}">style="display:none"</c:if>
			class="product-config-group product-config-side-comp-border product-config-specification-content">
			<div class="scrollbar-inner product-config-specification-content-scroll-wrapper">
				<c:forEach var="node" items="${config.groups}" varStatus="groupStatus">
					<c:set value="${fn:length(config.groups)-1 == groupStatus.index}" var="isLastNode"/>
					<config:specificationNode node="${node}"
						showNodeTitle="${!node.oneConfigurableSubGroup || groupStartLevel != 0}" isFirstLevelNode="true"
						isLastNode="${isLastNode}"/>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>