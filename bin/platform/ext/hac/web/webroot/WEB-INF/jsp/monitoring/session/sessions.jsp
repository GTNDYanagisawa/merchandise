<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld" %> 
<html>
<head>
<title>Sessions</title>

<script type="text/javascript" src="<c:url value="/static/js/history.js"/>"></script>
<script type="text/javascript" src="<c:url value="/static/js/monitoring/sessions.js"/>"></script>

</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft">
			<div id="sessionsContainter" data-closeSessionsUrl="<c:url value="/monitoring/sessions/close/"/>" data-updateSessionUrl="<c:url value="/monitoring/sessions/data/"/>">
				<h3>hybris Sessions</h3>
				<dl>
					<dt>Session timeout</dt>
					<dd id="sessionTimeout"></dd>
					<dt>Check interval</dt>
					<dd id="sessionCheckInterval"></dd>
					<dt>Currently instantiated / expired</dt>
					<dd>
						<span id="sessionsInstantiated"></span> / <span id="sessionsExpired"></span>
					</dd>
				</dl>

				<button id="closeSessions" style="float:left;">Close hybris sessions</button>
				<button id="closeSessionsAndHttp" style="float:left;">Close hybris and HTTP sessions</button>
			</div>
		</div>
	</div>
	<div class="span-6 last" id="sidebar">
		<div class="prepend-top" id="recent-reviews">
			<h3 class="caps">Page description</h3>
				<div class="box">
					<div class="quiet">
						This page provides details about the hybris sessions and a possibility to: <br><br>
							<ul>
								<li>Cancel all active hybris sessions without reconfirmation.</li>
								<li>Cancel all active hybris sessions including assigned HTTP sessions without reconfirmation.</li>
							</ul>			
						<hr />			
						<hac:note>
							These actions interrupt cron jobs and all current user sessions including for example, their carts.						
						</hac:note>
						<hr />
						<hac:tip>
							Closing hybris sessions can be useful prior to initializing the hybris Multichannel Suite. It can be used for testing live system.				
						</hac:tip>
					</div>
				</div>
		</div>
	</div>
</body>
</html>

