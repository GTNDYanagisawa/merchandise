<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
<c:param name="onewlink" value="true"/>
</c:import>
<h1>1Way link Message</h1>
<p>Please introduce a message input and click on Send</p>
<form action="<c:url value='/view/test'/>" method="get">
<p>phone country isocode:<input name="phoneCountry" value="GB" type="text"></p>
<input name="action" value="link" type="hidden">
<p>Phone number:
<input name="phone" type="text"></p>
<p>Subject:

<!-- insert -->
<textarea name="subject" type="text" class="textarea"></textarea>
<!-- /insert -->

</p>
<p>Url:
<input name="text" type="text"></p>
<p><input name="Send" value="Send" type="submit"></p>
</form> 

 <c:import url="_footer.jsp"/> 
