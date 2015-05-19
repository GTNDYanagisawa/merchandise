<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>


	<div class="scroller" id = "reco"/>
	</div>	
	<script type="text/javascript">
	   function loadData() {
		   retrieveRecommendations('${title}','${recommendationModel}','${productCode}','${itemType}','${includeCart}');
	   }
		window.onload = loadData;
	</script>

	
	