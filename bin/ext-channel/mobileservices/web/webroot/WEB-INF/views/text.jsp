<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
<c:param name="onewtext" value="true"/>
</c:import>

<h1>1Way text Message</h1>
<p>Please introduce a message input and click on Send</p>
<form action="<c:url value='/view/test'/>" method="get">
<input name="action" value="text" type="hidden">
<!-- 
<p>specific shortcode to route to:<input name="shortcode" value="" type="text"></p>
<p>shared shortcode keyoword :<input name="shortcodeKeyword" value="" type="text"></p>
-->
<p>phone country isocode:<input name="phoneCountry" value="GB" type="text"></p>
<p>Phone number:
<input name="phone" type="text"></p>
<p>Text message:
<input name="text" type="text"></p>
<p><input name="Send" value="Send" type="submit"></p>
</form> 
 <c:import url="_footer.jsp"/> 