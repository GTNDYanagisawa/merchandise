<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
<c:param name="onewaction" value="true"/>
</c:import>

<h1>2Ways Messaging test page</h1>
<p>Please introduce a message input and click on Send</p>
<form action="<c:url value='/view/test'/>" method="get">
<p>Country Iso Code:<input name="country" value="GB" type="text"></p>
<input name="action" value="mtaction" type="hidden">
<p>Phone number:
<input name="phone" type="text"></p>
<p>Action code:
<input name="serviceCode" type="text"></p>

<!-- insert -->
<p class="info">
	Some valid action codes in the default configuration are:
	<br /> 
	 <%-- ACC-2202 Remove voucher support
	 voucherConf,
	 --%> 
	 productConfiguration, categoryConfiguration, promotionConfiguration, pageConfiguration and catalogConfiguration
</p>
<!-- /insert -->

<p><input name="Send" value="Send" type="submit"></p>
</form> 
 <c:import url="_footer.jsp"/> 
