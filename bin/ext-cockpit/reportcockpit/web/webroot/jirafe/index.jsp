<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<link rel="stylesheet"  type="text/css" media="screen" href="https://test.jirafe.com/dashboard/css/hybris_ui.css" />
		<script type="text/javascript" src="https://test.jirafe.com/dashboard/js/hybris_ui.js"></script>
	</head>
<body>
	<div id="container" style="background: white; margin:auto;width: 1170px;"> 
	<div id="jirafe"></div>
	</div>
	<script type="text/javascript">
		if (typeof jQuery != 'undefined') { 
			(function($) {
				 $('#jirafe').jirafe({
				    api_url:    '${param.jirafeApiUrl}',
				    api_token:  '${param.jirafeApiToken}',
		            app_id:     '${param.jirafeAppId}',
				    version:    '${param.jirafeVersion}'
				 });
			})(jQuery);
		}
		setTimeout(function() {
			if ($('mod-jirafe') == undefined){
				$('messages').insert ("<ul class=\"messages\"><li class=\"error-msg\">We're unable to connect with the Jirafe service for the moment. Please wait a few minutes and refresh this page later.</li></ul>");        
			}        
		}, 2000);
	</script>
</body>
</html>
