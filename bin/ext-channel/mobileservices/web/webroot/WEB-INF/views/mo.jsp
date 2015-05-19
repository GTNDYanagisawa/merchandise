<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
<c:param name="twowdedicated" value="true"/>
</c:import>
<h1>2Ways Messaging test page</h1>
<p>Please introduce a message input and click on Send</p>
<form action="<c:url value='/view/test'/>" method="get">
<input name="action" value="mo" type="hidden">
<p>Shortcode country isocode:<input name="shortcodeCountry" value="GB" type="text"></p>
<p>Shortcode number:<input name="shortcode" type="text" value="000101" ></p>
<p>Phone country isocode:<input name="phoneCountry" value="GB" type="text"></p>
<p>Phone number: <input name="phone" type="text" ></p>
<p>Text message:
<input name="text" type="text" value="walmart offer"></p>

<!-- insert -->
<p class="info">Walmart jeans, walmart offers,...</p>
<!-- /insert -->

<p><input name="Send" value="Send" type="submit"></p>
</form> 
 <c:import url="_footer.jsp"/> 
