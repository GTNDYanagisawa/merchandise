<%@include file="head.inc"%>
<a href="index.jsp">back to oci index page</a><hr><br>
<%

	//sample shop application
	//page to show the product details
	//no enhancements for oci needed here
	
	Collection products = JaloSession.getCurrentSession().getProductManager().getProductsByCode(request.getParameter("productid"));
	SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		
	if( !products.isEmpty() )
	{
		Product p = ((Product)products.iterator().next());
		
		out.println("<b>Code:</b> " + p.getCode() + "<br>");
		out.println("<b>Name:</b> " + p.getName() + "<br>");     
		out.println("<b>Description:</b> " + p.getDescription() + "<br>"); 
		if( p.getPicture() != null )
		{
			out.println("<img src='"+p.getPicture().getURL()+"'>" );
		}
		else
		{
			out.println("no picture");
		}
		
		out.println("<a href='cart.jsp?productid="+p.getCode()+"'>add to cart</a>"); 
	}
%>

<%@include file="tail.inc"%>
