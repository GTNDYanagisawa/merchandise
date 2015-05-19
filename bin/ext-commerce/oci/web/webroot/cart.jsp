<%@ page import="de.hybris.platform.jalo.JaloSession" %>
<%@ page import="de.hybris.platform.oci.jalo.OciManager" %>
<%@ page import="de.hybris.platform.oci.jalo.utils.OutboundSection" %>
<%@include file="head.inc"%>
<a href="index.jsp">back to oci index page</a><hr><br>
<a href="products.jsp">back to the products</a><br><br>

<%

	//sample shop application
	//shop cart
	//implement here the OCI Byer Button
	//additional oci comments are below
	
	OrderManager orderMg = JaloSession.getCurrentSession().getOrderManager();
	Cart cart = null;

	if( request.getParameter("delproduct") != null )
	{
		cart = JaloSession.getCurrentSession().getCart();
		int entrynumber = Integer.parseInt(request.getParameter("delproduct"));
		AbstractOrderEntry entry = cart.getEntry(entrynumber);
		cart.removeEntry(entry);
	}

	if( request.getParameter("productid") != null )
	{
		cart = JaloSession.getCurrentSession().getCart();
		Collection products = JaloSession.getCurrentSession().getProductManager().getProductsByCode(request.getParameter("productid"));
		SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		
		if( !products.isEmpty() )
		{
			Product p = ((Product)products.iterator().next());
			cart.addNewEntry(p, 1, p.getUnit(), true);
		}
	}

	boolean leer = false;
	cart = JaloSession.getCurrentSession().getCart();
	List entries = cart.getAllEntries();
	int size = entries.size();
	if( size == 0 )
	{
		out.println( "<br> The cart is empty!<br>" );
		leer = true;
	}
	else
	{ 
		out.println("<table border='1'>");
		out.println("<tr><th>Cart Entry Number</th><th>Info</th><th>Count</th><th></th><tr>");
		Iterator i = cart.getAllEntries().iterator();
		while ( i.hasNext() )
		{
			CartEntry entry = (CartEntry)i.next();
			out.println("<tr><td>"+(entry.getEntryNumber()+1)+"</td><td>"+entry.getInfo()+"</td><td>"+entry.getQuantity() +"</td><td><a href='cart.jsp?delproduct="+entry.getEntryNumber()+"'>delete</td></tr><br>");
		}
		out.println("</table>");
		leer = false;
	}
  
	//here the html code for the oci buyer button is created
	if( !leer )
	{
		try
		{
			OutboundSection obs = (OutboundSection) JaloSession.getCurrentSession().getAttribute("OUTBOUND_SECTION_DATA");
			String useHtml = obs.getField("useHtml");
			if (useHtml != null && useHtml.equals("useHtml"))
			{
				out.println(OciManager.createOciBuyerButton(new DefaultSAPProductList(cart), true));
			}
			else
			{
				out.println(OciManager.createOciBuyerButton(new DefaultSAPProductList(cart)));
			}
			//you can here control the layout of the button
			//use therefor: 
			//String myButton = "my own html code for the button"
			//out.println(OciManager.createOciBuyerButton(new DefaultSAPProductList(cart), myButton));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
%>

<%@include file="tail.inc"%>
