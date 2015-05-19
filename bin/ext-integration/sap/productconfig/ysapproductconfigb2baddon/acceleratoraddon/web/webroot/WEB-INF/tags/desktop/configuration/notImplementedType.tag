<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<%@ taglib prefix="cssConf" uri="sapproductconfig.tld" %>

<label id="${cstic.key}.label" for="${cstic.key}.text" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>
<config:longText cstic="${cstic}" pathPrefix="${pathPrefix}"/>
 
<div id="${cstic.key}.text" class="${cssConf:valueStyleClass(cstic)}">
	<i><spring:message code="sapproductconfig.cstic.type.notimplemented" text="Not implemented" /></i>
</div>
