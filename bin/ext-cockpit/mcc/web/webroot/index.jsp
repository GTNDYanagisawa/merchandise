<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
		 "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">	 
<%@include file="head.jspf"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de">
<head>
	<title>hybris Multichannel Cockpit</title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="imagetoolbar" content="no">
	<meta name="description" content="">
	<meta name="keywords" content="">
	<link rel="icon" href="resources/image/favicon.ico" type="image/ico">
	<link rel="stylesheet" type="text/css" href="resources/css/screen.css" />
	<link rel="stylesheet" type="text/css" media="print" href="resources/css/print.css" />
	<script type="text/javascript" src="resources/js/jquery-latest.js"></script>
	<script language="JavaScript" type="text/JavaScript">
	<!--
	
		function setInfoText( text )
		{
			//alert( headline + " // " + text );
			var info = document.getElementById("infoCorner");
			info.innerHTML = text;
		}
	
	$(document).ready(function() {
	
		$("ul.zugeklappt").hide();

		$("a.adminWeb").click(function () {
			$("ul.adminWebList").toggle("fast");
		});    

		$("a.productCockpit").click(function () {
			$("ul.productCockpitList").toggle("fast");
		});    

		$("a.websites").click(function () {
			$("ul.websitesList").toggle("fast");
		});    

		$("a.mobileWebsites").click(function () {
			$("ul.mobileWebsitesList").toggle("fast");
		});    
	
		$("a.applications").click(function () {
			$("ul.applicationsList").toggle("fast");
		});    
	
	});
	//-->
	</script>
</head>
<body>
	<div class="canvas">
		<div id="innerCanvas">
			<div id="logo"><img src="resources/image/logo.jpg" /></div>
			<!-- contentContainer START - -->
			<div class="contentContainer">
				<div class="table">
					<div class="infoCorner" id="infoCorner"></div>
					<div class="emptyColumn column">&nbsp;</div>
					<div class="platform column">
						<div class="text">
							<h2>Platform</h2>
							<ul>
								<% if( isInstalled("reportcockpit")) {%><li onmouseover="setInfoText('<h3>hybris Report Cockpit</h3>Business Intelligence with hybris');" onmouseout="setInfoText('');" ><a href="<%=getWebRoot(response, "reportcockpit" )%>">Report Cockpit</a></li>
								<li class="divider">&nbsp;</li>
								<%}%>
								
								<% if( isInstalled("hmc")) {%><li onmouseover="setInfoText('<h3>hybris Management Console (hmc)</h3>Manage all kind of business objects in the hybris Management Console.');" onmouseout="setInfoText('');" >
									<a href="<%=getWebRoot(response, "hmc" )%>hybris">hybris Management Console (hmc)</a></li><%}%>
								<% if( isInstalled("adminweb")) {%><li onmouseover="setInfoText('<h3>hybris Administration Console (adminweb)</h3>For system administrators. Monitor the system, watch memory settings, check the cache and much more.');" onmouseout="setInfoText('');">
									<a href="<%=getWebRoot(response, "adminweb" )%>">System Administration</a></li><%}%>
							</ul>
						</div>
					</div>
					<div class="content column">
						<div class="text">
							<h2>Content</h2>
							<ul>
							<% int cockpits = 0; %>
							<% if( isInstalled("productcockpit")) {cockpits++;%><li onmouseover="setInfoText('<h3>hybris Product Cockpit</h3>Management of all catalog data (categories, products, variants, prices, ..)');" onmouseout="setInfoText('');">
								<a href="<%=getWebRoot(response, "productcockpit" )%>">Product Cockpit</a></li><%}%>
							<% if( isInstalled("importcockit")) { cockpits++;%><li onmouseover="setInfoText('<h3>hybris Import Cockpit</h3>Import data into hybris.');" onmouseout="setInfoText('');">
								<a href="<%=getWebRoot(response, "importcockpit" )%>">Import Cockpit</a></li><%}%>
							<% if( cockpits>0 ) {%><li class="divider">&nbsp;</li><%}%>
							<% if( isInstalled("hmc")) {%><li onmouseover="setInfoText('<h3>User/Group Management</h3>Manage users and groups from within the hybris Management Console');" onmouseout="setInfoText('');" >
								<a href="<%=getWebRoot(response, "hmc" )%>hybris?select=user">User/Group Management (hmc)</a></li><%}%>
							<% if( isInstalled("mam")) {%><li onmouseover="setInfoText('<h3>hybris DAM Server</h3>If installed, go directly to the hybris Digital Asset Management Server, based on Celum');" onmouseout="setInfoText('');">
								<a href="<%=Config.getParameter("celum.server.url")+":"+Config.getParameter("celum.server.port")%>">hybris DAM Server (Celum)</a></li><%}%>
							</ul>
						</div>
					</div>
					<div class="commerce column">
						<div class="text">
							<h2>Commerce</h2>
							<ul>
								<% if( isInstalled("marketingcockpit")) {%><li onmouseover="setInfoText('<h3>Marketing Cockpit</h3>THE Cockpit for all kind of marketing activities.');" onmouseout="setInfoText('');">
									<a href="<%=getWebRoot(response, "marketingcockpit" )%>">Marketing Cockpit</a></li>
								<li class="divider">&nbsp;</li>
								<%}%>
								<% if( isInstalled("hmc")) {%><li onmouseover="setInfoText('<h3>Marketing Tools</h3>Including promotions, voucher managed via hmc.');" onmouseout="setInfoText('');" >
									<a href="<%=getWebRoot(response, "hmc" )%>hybris?select=marketing">Marketing Tools incl. Promotions (hmc)</a></li><%}%>
								<% if( isInstalled("hmc")) {%><li onmouseover="setInfoText('<h3>Order Management</h3>Orders, Payment and Delivery costs');" onmouseout="setInfoText('');" >
									<a href="<%=getWebRoot(response, "hmc" )%>hybris?select=order">Order Management (hmc)</a></li><%}%>
							</ul>
						</div>
					</div>
					<div class="channel column">
						<div class="text">
							<h2>Channel</h2>
							<ul>
								<% cockpits = 0; %>
								<% if( isInstalled("cmscockpit")) {cockpits++;%><li onmouseover="setInfoText('<h3>WCMS Cockpit</h3>Integrated Multisite capable Web Content Management');" onmouseout="setInfoText('');">
									<a href="<%=getWebRoot(response, "cmscockpit" )%>">WCMS Cockpit</a></li><%}%>
								<% if( isInstalled("csmcockpit")) {cockpits++;%><li onmouseover="setInfoText('<h3>CustomerService Cockpit</h3>Call Center and Issue Management');" onmouseout="setInfoText('');">
									<a href="<%=getWebRoot(response, "csmcockpit" )%>">CustomerService Cockpit</a></li><%}%>
								<% if( isInstalled("printcockpit")) {cockpits++;%><li onmouseover="setInfoText('<h3>Print Cockpit</h3>Cockpit for creating and editing print publications. Output your documents to Adobe InDesign over werkII iQUEST.comet plugin.');" onmouseout="setInfoText('');">
									<a href="<%=getWebRoot(response, "printcockpit" )%>">Print Cockpit</a></li><%}%>
								<% if( cockpits>0 ) {%><li class="divider">&nbsp;</li><%}%>
								<li><span class="websites">Web Frontends:</span>
									<ul class="websitesList">
										<% int websites = 0; %>
										<% if( isInstalled("storetemplate")) {websites++;%><li onmouseover="setInfoText('<h3>hybris StoreTemplate</h3>technology demonstration of various hybris components and modules.');" onmouseout="setInfoText('');">
											<a href="<%=getWebRoot(response, "storetemplate" )%>">StoreTemplate</a></li><%}%>
										<% if( isInstalled("mobiletemplate")) {websites++;%><li onmouseover="setInfoText('<h3>hybris MobileTemplate</h3>Mobile Frontend, please view with your mobile browser');" onmouseout="setInfoText('');">
											<a href="<%=getWebRoot(response, "mobiletemplate" )%>">MobileTemplate</a></li><%}%>
										<% if(websites==0) {%>none installed<% } %>
									</ul>
									
								</li>
							</ul>
						</div>
					</div>
				</div>

			</div>
			<!-- contentContainer END - -->

		</div> 
		<!-- innerCanvas End -->
				
	</div>
	<!-- canvas End -->
	
</body>
</html>
<%@include file="tail.jspf"%>
