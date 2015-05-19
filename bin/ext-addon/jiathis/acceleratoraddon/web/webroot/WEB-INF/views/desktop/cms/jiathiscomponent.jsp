<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- JiaThis Button BEGIN -->
<div class="jiathis_style">
	<a class="jiathis_button_qzone"></a>
	<a class="jiathis_button_tsina"></a>
	<a class="jiathis_button_tqq"></a>
	<a class="jiathis_button_weixin"></a>
	<a class="jiathis_button_renren"></a>
	<a href="http://www.jiathis.com/share" class="jiathis jiathis_txt jtico jtico_jiathis" target="_blank"></a>
	<a class="jiathis_counter_style"></a>
</div>
<c:forEach var="metatag" items="${metatags}">
<c:if test="${metatag.name=='description'}" >
<script "text/javascript"> 
	var jiathis_config = { 
			summary:"${fn:substring(metatag.content, 0, 100)}" 
		};
</script>
</c:if>
</c:forEach>
<script type="text/javascript" src="http://v3.jiathis.com/code/jia.js?uid=1372958158178780" charset="utf-8"></script>
<!-- JiaThis Button END -->