<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Transaction Tracing</title>

<script type="text/javascript" src="<c:url value="/static/js/history.js"/>"></script>
<script type="text/javascript" src="<c:url value="/static/js/monitoring/dynatrace.js"/>"></script>

<link rel="stylesheet" href="<c:url value="/static/css/monitoring/dynatrace.css"/>" type="text/css" media="screen, projection">

</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft marginBottom">
			<h2>dynaTrace Transaction Tracing</h2>
			<p id="waiting">
				All available dynaTrace dashboards are being loaded... If dashboards are not displaying, then check the dynaTrace configuration as the communication with the dynaTrace server might be incorrectly setup. 
			</p>
				
			<div id="dashboards" data-url="<c:url value="/monitoring/dynatrace/data/"/>" data-base="<c:url value="/static/img/dynatrace/"/>"></div>

		</div>
	</div>
	<div class="span-6 last" id="sidebar">
		<div class="prepend-top" id="recent-reviews">
			<h3 class="caps">Page description</h3>
			<div class="box">
				<div class="quiet">
					This page provides links to all configured dashboards of your dynaTrace hybris Edition server. Click any dashboard to open the dynaTrace Client using Java Web Start. 
				</div>
			</div>
			<h3 class="caps">See also in the hybris Wiki</h3>
			<div class="box">
				<ul>
					<li> <a href="${wikiDynatrace}" target="_blank" class="quiet" >dynaTrace hybris Edition</a> </li>
					<li> <a href="${wikiDynatraceInstallation}" target="_blank" class="quiet" >dynaTrace hybris Edition Installation Guide</a> </li>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>

