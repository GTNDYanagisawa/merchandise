<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ attribute name="group" required="true"
	type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>


<div class="product-config-group-hidden">

	<form:input type="hidden" id="${group.id}_collapsed" 
		path="${pathPrefix}collapsed" />
	<form:input type="hidden" id="node_${group.id}_collapsedInSpec"
		path="${pathPrefix}collapsedInSpecificationTree" />
	<form:input type="hidden" path="${pathPrefix}name" />
	<form:input type="hidden" path="${pathPrefix}id" />
	<form:input type="hidden" path="${pathPrefix}groupType" />

	<c:forEach var="group" items="${group.subGroups}" varStatus="groupStatus">
		<c:set value="${pathPrefix}subGroups[${groupStatus.index}]." var="path"/>
		<config:hiddenGroup group="${group}" pathPrefix="${path}"/>
	</c:forEach>
</div>
