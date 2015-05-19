<%@include file="head.inc"%>
<a href="index.jsp">back to oci index page</a><hr><br>

<%

	//sample shop application 
	//page for searching products
	//no enhancements for oci needed here
	
	SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
	
	String searchTerm = (String)request.getParameter("searchterm");
	out.println("searching for: "+searchTerm+"<br>");
	
	Collection products = searchProducts(searchTerm, 100);
	out.println("<b>" + products.size() + "</b> Produkte gefunden!<br>");
	
	out.println("<table border='1'>\n<tr><th>ProduktID</th><th>Produktname</th><th>Details</th><th>Description</th></tr>\n");
	for( Iterator it = products.iterator(); it.hasNext(); )
	{
		Product product = ((Product)it.next());
		out.println("<tr><td>"+product.getCode()+"</td><td>"+product.getName()+"</td>" );
		
		if( product.getThumbnail() != null )
		{
			out.println("<td><a href='productdetails.jsp?productid="+product.getCode()+"'><img src='"+product.getThumbnail().getURL()+"'></a></td>\n");
		}
		else
		{
			out.println("<td><a href='productdetails.jsp?productid="+product.getCode()+"'>Produktdetails</a></td>\n");
		}
		
		out.println("<td>" + product.getDescription(ctx) + "</td>");
		out.println("</tr>");		
	}
	
	out.println("</table>");
%>

<%@include file="tail.inc"%>
