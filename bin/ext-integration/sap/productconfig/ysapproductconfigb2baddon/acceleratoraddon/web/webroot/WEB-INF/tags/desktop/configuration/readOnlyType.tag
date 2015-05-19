<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld"%>

<label id="${cstic.key}.label" for="${cstic.key}.readOnly"
	class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>
<config:longText cstic="${cstic}" pathPrefix="${pathPrefix}"/>

<div id="${cstic.key}.readOnly" class="${cssConf:valueStyleClass(cstic)}">
	<c:choose>
		<c:when test="${fn:length(cstic.domainvalues) gt 0}">
			<c:forEach var="value" items="${cstic.domainvalues}">
				<c:if test="${value.selected}">
					<div class="product-config-csticValueLabelWithoutSelect">
						<c:out value="${value.langdepname}" />
					</div>
				</c:if>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="product-config-csticValueLabelWithoutSelect">
				<c:out value="${cstic.value}" />
			</div>
		</c:otherwise>
	</c:choose> 
</div>
	<config:csticErrorMessages
		bindResult="${requestScope['org.springframework.validation.BindingResult.config']}"
		path="${pathPrefix}value" />
