<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>OCI Extension</title>
<link href="docu.css" rel="stylesheet" type="text/css">
<meta content="DocBook XSL Stylesheets V1.68.1" name="generator">
</head>

<body bgcolor="white" text="black" link="#0000FF" vlink="#840084" alink="#0000FF">
<div class="article" lang="en">

<div class="titlepage">
	<div>
		<div>
		 	<h1 class="title"><a name="N10001"></a>hybris OCI Extension</h1>
		</div>
	</div>
	<a href=".."> back </a>
	<hr>
</div>

<h3>A short introduction on using the OCI extension</h3>
<p>On this page, you'll find a short introduction into the OCI
 extension and on how to install, configure and use it.</p>
<p><a href="sap.jsp">Shortcut to an example of an integration of the
 OCI extension into a sample shop</a></p>
<p>If you see this page, you have successfully installed the OCI extension
 into your hybris platform.
	You will find a sample application of a small example shop and the
	 OCI extension in the directory
	of this file (<b>index.jsp</b>, in the OCI web module).<br>
This example consists of three parts:</p>

<P><B>SAP SRM simulator</B><BR />
The OCI extension needs an SAP Server, but if you want to test your 
shop application with the OCI extension you can use <a href="sap.jsp">sap.jsp</a>
and <b>sap-data-interpretation.jsp</b> (no link to sap-data-interpretation.jsp
since there would be quite a lot of parameters
to hand over).<br><br>
<div class="note" style="margin-left: 0.5in; margin-right: 0.5in;">
<b>sap.jsp</b> simulates a POST request by an SAP SRM server trying to browse an
external catalog.<br><br>
<b>sap-data-interpretation.jsp</b> shows the data which is sent back from the
 catalog to the SAP server in raw data and decoded xml format.<br>
</div>
<P/>
<p><b>OCI extension entrypoint</b><BR />
The SAP server itself does not call the shop. It needs to use the OCI extension.
Therefore, the SAP server needs to call <a href="entrypoint.jsp">entrypoint.jsp</a>
which itself provides all necessary methods for communicating with the SAP server. <br>
Note that if you call this file without mandatory parameters you'll receive an 
error message.
<br>

<b>IMPORTANT:</b> If you build your own entrypoint.jsp file, <b>do not</b> use any HTML
code in this file. In fact, you can use this example file but you need to implement
your own interface CatalogLoginPerformer. The sample class DefaulCatalogLoginPerformer
is an implementation for the sample shop application and can be found in the folder 
<i>/doc/resources/samplesrc/</i>.</p>
<p><b>Sample shop application</b><br>
This shop should give you an idea of how you can implement the OCI extension in a shop
of your own. The example shop consists of a shop entry page 
(<a href="shop-start.jsp">shop-start.jsp</a>), a product page (products.jsp),
a cart (cart.jsp), a product detail page (productdetails.jsp) 
and a product search page (productsearch.jsp).<br>
Remember: by calling the shop directly without using the OCI extension, you'll receive
an error message when using the OCI button in the cart.</p>

<hr />

<h3>Setup SAP server</h3>
<p>In your SAP server you need to specify two parameters for the OCI extension. <br><br>
<div class="note" style="margin-left: 0.5in; margin-right: 0.5in;">
Firstly, the link to your shop which is entrypoint.jsp in our example
(<b>note:</b> it is not the starting page of your shop).<br>
<br>
Secondly, you need to set the field HOOK_URL (the parameter name may differ, see your
SAP documentation for more information) with the URL where the SAP server expects the
data from the shop to send to. In our example it is <b>sap-auswertung.jsp</b>, a file
that only shows all data from the OCI extension in encoded and decoded format without
further processing it.
</div>
<p/>

<h3>Setup OCI extension</h3>
<p>
For your own shop you can basically use the file <b>entrypoint.jsp</b>, but you need
to implement the interfaces with your own logic. The default implementations
in the folder <i>/doc/resources/samplesrc/</i> are supposed to give you an idea how 
you could implement it. <br>
If you want to use a .JSP page of your own as an entry point, please make sure that you call
 <code class="literal">OciManager.ociLogin(..., ..., ...);</code>
first and you do not use any HTML code in the respective .JSP file.
</p>
<img src="schema.gif"></img><br>
</div>
</body>
</html>
