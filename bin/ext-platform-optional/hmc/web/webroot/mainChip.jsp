<%@include file="head.inc"%>
<%
	MainChip theChip = (MainChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String login = null;
	String password = null;
	
	LoginToken token = UserManager.getInstance().getLoginToken( request );
	
	if( token != null )
	{
	    //token = new CookieBasedLoginToken( cookie );
		theChip.setAutoLogin(true);
	}
	else
	{
	    login = theChip.getUser();
		password = theChip.getPassword();	
	}
	
	// "forward" all given url parameters
	Map extraParams = theChip.getExtraParams();
	for( Iterator iter = extraParams.keySet().iterator(); iter.hasNext(); )
	{
		String paramName = (String) iter.next();
		String paramValue = (String) ((List) extraParams.get(paramName)).get(0);
%>
		<input type="hidden" name="<%= escapeHTML(paramName) %>" value="<%= escapeHTML(paramValue) %>">
<%
	}
%>

<style type="text/css">
	body {
		background-image: none;
		background-repeat: repeat-x;
		font-size: 14px;
		line-height: 110%;
		color: #fff;
		margin: 0;
		background-color: #162c5e;
		background: -moz-linear-gradient(top, #1d346b, #162c5f) #162c5e;
		background: -ms-linear-gradient(top, #1d346b, #162c5f) #162c5e;
		background: -o-linear-gradient(top, #1d346b, #162c5f) #162c5e;
		background: -webkit-gradient(linear, center top, center bottom, from(#1d346b), to(#162c5f) ) #162c5e;
		background: -webkit-linear-gradient(top, #1d346b, #162c5f) #162c5e;
		background: linear-gradient(top, #1d346b, #162c5f) #162c5e;
		filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#1d346b, endColorstr=#162c5f);               
		-ms-filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#1d346b, endColorstr#162c5f);
		font-family: 'Roboto', Arial, sans-serif;
		font-weight: 200;
		font-style: normal;
	}	
</style>

<table height="100%" width="100%"  class="login_screen">
	<tr>
		<td align="center" valign="middle">
			<div class="login_screen_hmc">
				<table align="right"  class="loginClass">
					<tr>
						<td align="left">
							Login:
						</td>
						<td >
							<input type="text" name="<%= theChip.getEventID(MainChip.SET_LOGIN) %>"
									 id="<%= theChip.getUniqueName() %>_user"
									 value="<%= (login != null ? login : "") %>" style="width:150px"
									 onkeypress="return checkForEnter(event);"
									 onfocus="this.select();">
						</td>
					</tr>
					<tr>
						<td align="left">
							Password:
						</td>
						<td align="right">
							<input type="password" name="<%= theChip.getEventID(MainChip.SET_PASSWORD) %>"
									 id="<%= theChip.getUniqueName() %>_password"
									 value="<%= (password != null ? password : "") %>" style="width:150px"
									 onkeypress="return checkForEnter(event);"
									 onfocus="this.select();" onblur="this.value=this.value;">
						</td>
					</tr>
					<tr>
						<td align="left">
							Remember:
						</td>
						<td align="left">
							<input type="checkbox" name="<%= theChip.getEventID(MainChip.SET_REMEMBERME) %>" class="checkbox"
									 id="<%= theChip.getUniqueName() %>_rememberme"
									 onkeypress="return checkForEnter(event);"
									 onfocus="this.select();" onblur="this.value=this.value;">

						</td>
					</tr>
					<tr>
						<td style="width:73px;">
						</td>
						<td>
							<div class="xp-button chip-event">
								<a href="#" title="<%= localized("login") %>" name="login" id="<%= theChip.getUniqueName() %>_a" hidefocus="true">
									<span class="label" id="<%= theChip.getUniqueName() %>_label">
										<%= localized("login") %>
									</span>
								</a>
							</div>		
						</td>
					</tr>
				</table>
				<script language="JavaScript1.2">
<%
					if( (login != null) && (password == null) )
					{
						// user name was set, password not, focus in password field
%>
						$("<%= theChip.getUniqueName() %>_password").focus();
<%
					}
					else
					{
						// focus in login field
%>
						$("<%= theChip.getUniqueName() %>_user").focus();
						$("<%= theChip.getUniqueName() %>_user").select();
<%
					}
%>
				</script>
				<noscript>
					<small><i>
					For the HMC to function you have to enable JavaScript.
					On your machine JavaScript is turned off. To enable JavaScript please consult the online help.
					</i></small>
				</noscript>
			</div>
		</td>
	</tr>
</table>
<% 
	if( theChip.isAutoLogin() || theChip.isLoggedIn() )
	{
%>
		<script language = "JavaScript1.2">
			document.editorForm.submit();
		</script>
<%
	}
%>
