<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/template" %>
<%@ taglib prefix="addoncart" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/cart"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
	<script  type="text/javascript">
		/*<![CDATA[*/
		$(document).ready(function() {
			$('#procurementForm').submit();
		});
		/*]]>*/
		</script>
	</jsp:attribute>
	<jsp:body>
	<common:globalMessages/>
	<form id="procurementForm" id="procurementForm" method="post" action="${browseFormPostUrl}">
		<input type="hidden" name="cxml-base64" value="${orderAsCXML}">
	</form>
	</jsp:body>
</template:page>
