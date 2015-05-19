<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
<c:param name="twowshared" value="true"/>
</c:import>

<h1>2Ways Messaging test page</h1>
<p>Please introduce a message input and click on Send</p>
<form action="<c:url value='/view/test'/>" method="get">
<p>Country Iso Code:<input name="country" value="UK" type="text"></p>
<input name="action" value="mo" type="hidden">
<p>Shortcode:<input name="shortcode" value="000102" type="text"></p>
<p>Phone number:
<input name="phone" type="text"></p>
<p>Text message:
<input name="text" type="text"></p>

<!-- insert -->
<p class="info">
	Write "KEYWORD" followed by optional commands. Keywords are PRODUCT, CATEGORY, PAGE, PROMOTION, CATEGORY, PAGE
	<br />
	Example: KEYWORD product, 
	<%-- ACC-2202 Remove voucher support
	KEYWORD voucher
	--%>
	, etc
</p>
<!-- /insert -->

<p><input name="Send" value="Send" type="submit"></p>
</form> 
 <c:import url="_footer.jsp"/> 
