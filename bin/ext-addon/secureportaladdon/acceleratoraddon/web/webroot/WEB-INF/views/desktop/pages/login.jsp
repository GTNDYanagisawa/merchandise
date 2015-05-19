<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sptemplate" tagdir="/WEB-INF/tags/addons/secureportaladdon/desktop/sptemplate" %>
<%@ taglib prefix="spuser" tagdir="/WEB-INF/tags/addons/secureportaladdon/desktop/spuser" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>


<sptemplate:page pageTitle="${pageTitle}">

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-24 last login_container">
		<div class="span-9 last login-panel">
			<c:url value="/j_spring_security_check" var="loginActionUrl"/>
			<spuser:login actionNameKey="login.login" action="${loginActionUrl}"/>
		</div>
	</div>
	
</sptemplate:page>
