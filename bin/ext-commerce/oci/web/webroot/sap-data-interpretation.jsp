<%@ page import="java.util.*, de.hybris.platform.util.*" %>


<html>

<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<TITLE>Ausgabe des HttpServletRequest </TITLE>
<link href="docu.css" rel="stylesheet" type="text/css">
<meta content="DocBook XSL Stylesheets V1.68.1" name="generator">
</HEAD>

<body>
<h3>ParameterMap</h3>
<table border="1">
<tr><th>Name</th><th>Value</th></tr>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String   sName     = parameterList.nextElement().toString();
    String[] sMultiple = request.getParameterValues( sName );
    
    if( 1 >= sMultiple.length )
    {
      out.println("<tr><td>"+ sName +"</td><td>");
      if (request.getParameter( sName ).length() > 55)
      {
	      out.println(request.getParameter( sName ).substring(0,50) + " ...  <i>(first 50 chars of the generated xml file as Base64 encoded string)</i></td></tr>" );
      }
      else
      {
    	  out.println(request.getParameter( sName ) + "</td></tr>" );
      }       
    }
    
    
    
    else
      for( int i=0; i<sMultiple.length; i++ )
        out.println("<tr><td>"+ sName + "[" + i + "] </td><td>" + sMultiple[i] + "</td></tr>" );
  
  
  }      
%>
</table>
<%
	String x = request.getParameter("~xmlDocument");
	if (x != null)
	{
		if (x.length() < 2)
		{
			x = "";
		}
		else
		{	
			x = new String( Base64.decode(x) );
		}
		out.println("<br>content of the generated XML File:<br><textarea name='xmldocument' cols='130' rows='60'>" + de.hybris.platform.util.Utilities.escapeHTML(x) + "</textarea>" );
	}
	else
	{
		out.println("<br>Es wurde obrige Formulardaten empfangen");
	}
%>
</body>
</html>
