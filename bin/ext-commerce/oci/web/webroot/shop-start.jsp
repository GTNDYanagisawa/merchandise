<%@include file="head.inc"%>

<%	//sample shop application
	//start page of shop
%>

<html>
<title> sample shop application </title>
<body>
<a href="index.jsp">back to oci index page</a><hr><br>
<a href="products.jsp">to the products</a><br><br>
<a href="cart.jsp">to the cart</a><br><br><br>
<form action="productsearch.jsp" method="get">
<b>Productsearch</b><br><br>
searchterm: <input type="text" name="searchterm">&nbsp;&nbsp;<input type="submit" value="search">
</form>


<%@include file="tail.inc"%>
