<% 
	if( DEBUG_COMMENTS ) { %><!-- start: js/eventHandler.inc --><% }
	
	for( final Iterator events = theDisplayState.getWindowEvents().iterator(); events.hasNext(); )
	{
		WindowEvent event = (WindowEvent) events.next();
		if( event instanceof WindowOpenEvent )
		{
			WindowOpenEvent woe = (WindowOpenEvent) event;
			String name = woe.getWindowName();
			String id = woe.getFrame().getID();
			String strToolbar = "";
						
			if( woe.isShowToolbar() )
			{
				strToolbar = ",toolbar=yes";
			}
			if( woe.isMaximized() )
			{
%>
				<script language = "JavaScript1.2">
					var width = screen.availWidth;
					var height = screen.availHeight;
					var x = 0;
					var y = 0;
					var newWindow = window.open("<%= getWindowRequestURL(id) %>","<%= name %>", "height=" + height + ",width=" + width + ",status=yes,dependent=no,scrollbars=yes<%= strToolbar %>,resizable=yes,directories=no,screenX=" + x + ",screenY=" + y);
					newWindow.focus();					
					newWindow.moveTo(0,0);
					newWindow.resizeTo(screen.availWidth,screen.availHeight);
				</script>
<%
			}
			else
			{
%>
				<script language = "JavaScript1.2">

						if( <%= woe.isPercentage() %> )
						{
							// interpret 'width' as percentage
							var height = screen.availHeight * <%= woe.getWidth() %> / 100;
							var width = screen.availWidth * <%= woe.getWidth() %> / 100;
						}
						else
						{
							var height = <%= woe.getHeight() %>;
							var width = <%= woe.getWidth() %>;
						}						
<%
						if( woe.getXOffset() > -1 || woe.getYOffset() > -1 )
						{
							// relative window positioning
							final int xOffset = woe.getXOffset() > -1 ? woe.getXOffset() : 0;
							final int yOffset = woe.getYOffset() > -1 ? woe.getYOffset() : 0;
%>
							var currentX, currentY;		// get current window position
							if( ie5 )
							{
								// little trick to get current window position in ie
								x0 = window.screenLeft;
								y0 = window.screenTop;
								window.moveTo(0,0);
								currentX = x0 - window.screenLeft;
								currentY = y0 - window.screenTop;
								window.moveTo(currentX, currentY);
							}
							else
							{
								// easier in mozilla etc.
								currentX = window.screenX;
								currentY = window.screenY;
							}
							
							x = currentX + <%= xOffset %>;
							y = currentY + <%= yOffset %>;
<%
						}
						else
						{
							// center window positioning
%>
							x = (screen.width / 2) - (width / 2);
							y = (screen.height / 2) - (height / 2);
<%
						}
%>
					var newWindow = window.open("<%= getWindowRequestURL(id) %>","<%= name %>", "height=" + height +",width=" + width +",status=yes,dependent=no,scrollbars=yes<%= strToolbar %>,resizable=yes,directories=no,left=" + x + ",top=" + y, true);
					newWindow.focus();
				</script>
<%
			}
			events.remove();
		}
		if( event instanceof WindowRedirectEvent )
		{
			WindowRedirectEvent wre = (WindowRedirectEvent) event;
			if( wre.getFrame() == frame )
			{
%>
				<script language="JavaScript1.2">
					window.location.href = "<%=response.encodeRedirectURL(wre.getRedirectURL())%>";
				</script>
<%
				events.remove();
				return;
			}
		}
		if( event instanceof WindowRefreshEvent )
		{
			WindowRefreshEvent wre = (WindowRefreshEvent) event;
			String name = wre.getWindowName();
			String id = wre.getFrame().getID();
%>
			<script language = "JavaScript1.2">
				window.open("<%= getWindowRequestURL(id) %>", "<%= name %>", "");
			</script>
<%
			events.remove();
		}
		if( event instanceof FrameRefreshEvent )
		{
			FrameRefreshEvent fre = (FrameRefreshEvent) event;
			String name = fre.getWindowName();
			String id = fre.getFrame().getID();
%>
				<script language = "JavaScript1.2">
					<%= name %>.frames['<%= id %>'].document.location="<%= getWindowRequestURL(id) %>";
				</script>
<%
			events.remove();
		}
		if( event instanceof ExternalFrameEvent )
		{
			ExternalFrameEvent efe = (ExternalFrameEvent) event;
			String name = efe.getFrameName();
			String id = efe.getFrame().getID();
			String url = efe.getURL();
%>
			<script language = "JavaScript1.2">
				<%= name %>.frames['<%= id %>'].document.location="<%= url %>";
			</script>
<%
			events.remove();
		}
		if( event instanceof WindowCloseEvent )
		{
			WindowCloseEvent wce = (WindowCloseEvent) event;
			if( wce.getFrame() == frame )
			{
%>
				<script language = "JavaScript1.2">
					window.close();
				</script>
<%
			}
			events.remove();
			return;
		}
	}

	if( DEBUG_COMMENTS ) { %><!-- end: js/eventHandler.inc --><% }
%>




