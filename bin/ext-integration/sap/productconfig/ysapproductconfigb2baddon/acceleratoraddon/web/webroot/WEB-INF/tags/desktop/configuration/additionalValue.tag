<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<%@ taglib prefix="cssConf" uri="sapproductconfig.tld"%>


<div class="product-config-additionalValue ${cssConf:valueStyleClass(cstic)}">
	<c:choose>
	<c:when test="${cstic.validationType == 'NUMERIC'}">
		<c:set var="size"
			value="${cstic.maxlength*1.3}" />
			
		<form:input type="hidden" id="${cstic.key}.typeLength"
			path="${pathPrefix}typeLength" />
		<form:input type="hidden" id="${cstic.key}.numberScale"
			path="${pathPrefix}numberScale" />
		<form:input type="hidden" id="${cstic.key}.entryFieldMask"
			path="${pathPrefix}entryFieldMask" />
	</c:when>
	<c:otherwise>
		<c:set var="size" value="${cstic.maxlength*1.8}" />
	</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${cstic.maxlength > 0}">
			<form:input id="${cstic.key}.input"
				class="${cssConf:valueStyleClass(cstic)}" type="text"
				path="${pathPrefix}additionalValue" maxlength="${cstic.maxlength}"
				size="${size}" />
		</c:when>
		<c:otherwise>
			<form:input id="${cstic.key}.input"
				class="${cssConf:valueStyleClass(cstic)}" type="text"
				path="${pathPrefix}additionalValue" />
		</c:otherwise>
	</c:choose>
</div>

<config:csticErrorMessages
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}"
	path="${pathPrefix}additionalValue" />
