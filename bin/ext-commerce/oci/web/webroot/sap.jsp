<% 

	de.hybris.platform.util.WebSessionFunctions.clearSession(session);
	//SAP SRM server simulating page
	//all requests from SAP server are send by http post
	//so you can simulate here by filling the parameter fields and submit them to the 
	//jsp file where OCI method 'login' is located
%>

<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<TITLE>SAP SRM server simulating page</TITLE>
<link href="docu.css" rel="stylesheet" type="text/css">
<meta content="DocBook XSL Stylesheets V1.68.1" name="generator">
</HEAD>

<BODY>
<BR>

<H1>Parameter Map for OCI 1.x bis 4.x </H1>
<FORM action="entrypoint.jsp" method="post">
<INPUT type="submit" value="Enter Shop" id="submit1" name="submit1"><br><br>
<TABLE border="1" cellpadding="5" frame="box">
	<TR>
		<TH colspan="3">Special catalog parameters - define your own parameters here</TH>
	</TR>
	<TR>
		<TD>USERNAME</TD>
		<TD><INPUT type="text" name="USERNAME" value="admin" /></TD>
		<TD>Field USERNAME which contains the username. Needed for login to hybris catalog.</TD>
	</TR>
	<TR>
		<TD>PASSWORD</TD>
		<TD><INPUT type="password" name="PASSWORD" value="nimda" /></TD>
		<TD>Field PASSWORD which contains the password. Needed for login to hybris catalog.</TD>
	</TR>
	<TR>
		<TH colspan="3">Mandatory parameters</TH>
	</TR>
	<TR>
		<TD>HOOK_URL</TD>
		<TD><INPUT type="text" name="HOOK_URL" value="sap-data-interpretation.jsp" /></TD>
		<TD>The URL used to return to the Enterprise Buyer application from
		the catalog application.</TD>
	</TR>
	<TR>
		<TD>~OkCode</TD>
		<TD><INPUT type="text" name="~OkCode" value="ADDI" /></TD>
		<TD>Contains the transaction code indicating that the function Add
		Items to SAP shopping basket is to be performed. Must be set to ADDI
		for Enterprise Buyer. Has no influence on the OCI extention.</TD>
	</TR>
	<TR>
		<TD>~TARGET</TD>
		<TD><INPUT type="text" name="~TARGET" value="_top" /></TD>
		<TD>Specifies the frame to which a catalog is to return in a
		frame-based environment. If this field is not set, the catalog
		application must provide a default target of _top.</TD>
	</TR>
	<TR>
		<TD>~CALLER</TD>
		<TD><INPUT type="text" name="~CALLER" value="CTLG" /></TD>
		<TD>Indicates that the data was sent by an external catalog. Content
		must be set to CTLG. Has no influence on the OCI extention.</TD>
	</TR>
	<TR>
		<TD>OCI_VERSION</TD>
		<TD><INPUT type="text" name="OCI_VERSION" value="4.0" /></TD>
		<TD>Interface version OCI. Valid entries are "2.x" "3.x" "4.x". OCI 1.x will result in a "not supported"
		OciException</TD>
	</TR>
	<TR>
		<TD>OPI_VERSION</TD>
		<TD><INPUT type="text" name="OPI_VERSION" value="1.0" /></TD>
		<TD>Interface version OPI. Can be "1.0" Other values are unknown. Has no influence on the OCI extention.</TD>
	</TR>
	<TR>
		<TD>http_content_charset</TD>
		<TD><INPUT type="text" name="http_content_charset" value="iso-8859-1" /></TD>
		<TD>Character set of SRM Server application (OCI 3.x). Has no influence on the OCI extention.</TD>
	</TR>
	<TR>
		<TD>returntarget</TD>
		<TD><INPUT type="text" name="returntarget" value="_parent" /></TD>
		<TD>Target (HTML target) for return to the SRM Server application. Has no influence on the OCI extention.</TD>
	</TR>
	<TR>
		<TH colspan="3">Parameters to trigger additional functions in the
		Product Catalog (OCI 2.x and later)</TH>
	</TR>
	<TR>
		<TD>FUNCTION</TD>
		<TD><INPUT type="text" name="FUNCTION" value=""/></TD>
		<TD>possible Inputs are <B>DETAIL</B> (OCI 2.x and later), <B>VALIDATE</B> (OCI 2.x and later), 
		<B>SOURCING</B> (OCI 4.x only, for OCI 2.x and 3.x use <B>VALIDATE</B>) and <B>BACKGROUND_SEARCH</B> (OCI 4.x)</TD>
	</TR>
	<TR>
		<TD>PRODUCTID</TD>
		<TD><INPUT type="text" name="PRODUCTID" value=""/></TD>
		<TD>Database key for the product in the catalog. Must be filled when
		FUNCTION={DETAIL|VALIDATE}. (Use the product code for this sample shop application.)</TD>
	</TR>
	<TR>
		<TD>QUANTITY</TD>
		<TD><INPUT type="text" name="QUANTITY" value=""/></TD>
		<TD>Current purchase order quantity. Must be filled when
		FUNCTION=VALIDATE</TD>
	</TR>
	<TR>
		<TD>VENDOR</TD>
		<TD><INPUT type="text" name="VENDOR" /></TD>
		<TD>Business partner number in EBP. Must be filled when
		FUNCTION=SOURCING</TD>
	</TR>
	<TR>
		<TD>SEARCHSTRING</TD>
		<TD><INPUT type="text" name="SEARCHSTRING" /></TD>
		<TD>Search term. Must be filled when
		FUNCTION={SOURCING|BACKGROUND_SEARCH}</TD>
	</TR>
	<TR>
		<TH colspan="3">Testing Parameters</TH>
	</TR>
	<TR>
		<TD>use html</TD>
		<TD><INPUT type="checkbox" name="useHtml" value="useHtml"></TD>
		<TD>Check this box if data in the form should be plain html. The default export is xml data and this box is unchecked.</TD>
	</TR>
</TABLE>
<br>
<INPUT type="submit" value="Enter Shop" id="submit1" name="submit1"></FORM>
<BR>


</BODY>
</HTML>

