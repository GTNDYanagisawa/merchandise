<%

	//SAP have to call this method
	//you need to implement the three interfaces

	try
	{
		//this is the only method which is called by SAP
		//request and response are always the same
		//but as third parameter use here your own implemented interface
		
		//if (request.getAttribute("useHtml")
		
		//de.hybris.platform.oci.jalo.OciManager.ociLogin(request, response, new de.hybris.platform.oci.jalo.interfaces.DefaultCatalogLoginPerformer());
		
		//wenn useHtml=true dann:
		String useHtml = (String)request.getParameter("useHtml");
		if (useHtml != null && useHtml.equals("useHtml"))
		{
			de.hybris.platform.oci.jalo.OciManager.ociLogin(request, response, new de.hybris.platform.oci.jalo.interfaces.DefaultCatalogLoginPerformer(), false, true);
		}
		else
		{
			de.hybris.platform.oci.jalo.OciManager.ociLogin(request, response, new de.hybris.platform.oci.jalo.interfaces.DefaultCatalogLoginPerformer());
		}
		
	}
	catch( Throwable e )
	{
		out.println("Error occured: " + e);
		e.printStackTrace();
	}
%>
