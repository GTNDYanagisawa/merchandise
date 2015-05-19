<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Transaction Tracing</title>

<script type="text/javascript"
	src="<c:url value="/static/js/history.js"/>"></script>
	
	<link rel="stylesheet" href="<c:url value="/static/css/monitoring/dynatrace.css"/>" type="text/css" media="screen, projection">
</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft">
			<h2>dynaTrace Transaction Tracing<img style="float:right; margin-right: 10px;" src="<c:url value="/static/img/dynatrace/dynatrace.png"/>"/></h2>
			<br>
			<p> 
				By adding dynaTrace software to your hybris Platform, you will get deeper insights into your application. 
				The <a href="${wikiDynatraceInstallation}" target="_blank" class="quiet">installation</a> takes only a few minutes and
				provides many benefits:
			</p>
			
			<div class="dashboard_no_highlight">
				<h4>Deep Visibility into Transactions</h4>
				<img style="float:left; margin-right: 10px;" src="<c:url value="/static/img/dynatrace/Single_Transaction_Performance.png"/>"/>
				<p class="desc">Ability to see failures, errors and problems within your application in real-time, without the need to get notified by a customer or to dig through log files.</p>			
			</div> 			
			
			<div class="dashboard_no_highlight">
				<h4>Breakdown Performance Contributors</h4>
				<img style="float:left; margin-right: 10px;" src="<c:url value="/static/img/dynatrace/Search_Transaction_Performance.png"/>"/>
				<p class="desc">Identify with ease which system components or even 3rd party services affect performance, the corresponding service levels and have an impact on business.</p>			
			</div>
			
			 <div class="dashboard_no_highlight">
				<h4>System Utilization</h4>
				<img style="float:left; margin-right: 10px;" src="<c:url value="/static/img/dynatrace/System_Utilization.png"/>"/>
				<p class="desc">See what is going on in your application. The advanced monitoring capabilities provide deep visibility into the JVM memory. CPU usage and database performance in one single tool.</p>			
			</div> 

		</div>
	</div>
	<div class="span-6 last" id="sidebar">
		<div class="prepend-top" id="recent-reviews">
			<h3 class="caps">About dynaTrace</h3>
			<div class="box">
				<div class="quiet">
					dynaTrace software, a division of Compuware is the technology leader in Application Performance Monitoring. Together with hybris, dynaTrace created a free hybris Edition. It is designed for the hybris Multichannel Accelerator and gives you access to many performance metrics of your running hybris instances.
				</div>
				<hr/>
				<div class=quiet>
					<a href="${wikiDynatrace}" target="_blank" class="quiet" ><img style="margin:0" src="<c:url value="/static/img/dynatrace/video.png"/>"/>
					Watch the dynaTrace introduction video</a>
				</div>
			</div>
			<h3 class="caps">See also in the hybris Wiki</h3>
			<div class="box">
				<ul>
					<li> <a href="${wikiDynatrace}" target="_blank" class="quiet" >dynaTrace Application Performance Management</a> </li>
					<li> <a href="${wikiDynatraceInstallation}" target="_blank" class="quiet" >dynaTrace hybris Edition Installation Guide</a> </li>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>

