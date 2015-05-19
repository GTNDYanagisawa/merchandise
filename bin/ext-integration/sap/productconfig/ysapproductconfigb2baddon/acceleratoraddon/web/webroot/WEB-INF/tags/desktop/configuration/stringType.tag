<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld" %>

<label id="${cstic.key}.label" for="${cstic.key}.input" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>
<config:longText cstic="${cstic}" pathPrefix="${pathPrefix}"/>
<div>
	<c:choose>
		<c:when test="${cstic.maxlength > 0}">
			<form:input id="${cstic.key}.input" class="${cssConf:valueStyleClass(cstic)}" type="text" 
				path="${pathPrefix}value" maxlength="${cstic.maxlength}" size="${cstic.maxlength*1.8}"/>
		</c:when>
		<c:otherwise>
		<form:input id="${cstic.key}.input" class="${cssConf:valueStyleClass(cstic)}" type="text" 
				path="${pathPrefix}value" />
		</c:otherwise>
	</c:choose>
</div>
<config:csticErrorMessages bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value"/>	
