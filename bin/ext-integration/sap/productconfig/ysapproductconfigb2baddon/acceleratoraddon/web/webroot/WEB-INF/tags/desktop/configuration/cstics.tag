<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
	
<c:if test="${cstic.visible}">
	<div class="product-config-cstic">
		<c:choose>
			<c:when test="${cstic.type == 'STRING'}">
				<config:stringType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'DROPDOWN'  || cstic.type == 'DROPDOWN_ADDITIONAL_INPUT'}">
				<config:dropDownType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'RADIO_BUTTON' || cstic.type == 'RADIO_BUTTON_ADDITIONAL_INPUT'}">
				<config:radioButtonType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'CHECK_BOX'}">
				<config:checkBoxType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'CHECK_BOX_LIST'}">
				<config:checkBoxListType pathPrefix="${pathPrefix}"
					cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'NUMERIC'}">
				<config:numericType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:when test="${cstic.type == 'READ_ONLY'}">
				<config:readOnlyType pathPrefix="${pathPrefix}" cstic="${cstic}" />
			</c:when>
			<c:otherwise>
				<config:notImplementedType pathPrefix="${pathPrefix}"
					cstic="${cstic}" />
			</c:otherwise>
		</c:choose>
		<form:input type="hidden"  id="${cstic.key}.key" 
			path="${pathPrefix}key" />
		<form:input type="hidden"  id="${cstic.key}.type"
			path="${pathPrefix}type" />
		<form:input type="hidden"  id="${cstic.key}.validationType"
			path="${pathPrefix}validationType" />
		<form:input type="hidden"  id="${cstic.key}.visible"
			path="${pathPrefix}visible" />
		<form:input type="hidden"  id="${cstic.key}.langdepname"
			path="${pathPrefix}langdepname" />
		<form:input type="hidden"  id="${cstic.key}.name" 
			path="${pathPrefix}name" />
		<form:input type="hidden"  id="${cstic.key}.lastValidInput" 
			path="${pathPrefix}lastValidValue" />
	</div>
</c:if>
