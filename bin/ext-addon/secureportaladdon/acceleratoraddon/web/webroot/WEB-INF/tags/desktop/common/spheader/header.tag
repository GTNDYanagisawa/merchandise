<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<div id="header">

	<span id="Branding"></span>
	<div class="siteLogo">
	  <cms:pageSlot position="SiteLogo" var="logo" limit="1">
	   <cms:component component="${logo}" />
	  </cms:pageSlot>
	 </div>

	<div class="clear"></div>
	
</div>