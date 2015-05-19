<%@include file="head.inc"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.io.*"%>
<%@page import="de.hybris.platform.hmc.webchips.*"%>
<%@ page import="de.hybris.platform.licence.Licence" %>
<%
	final de.hybris.platform.licence.Licence licence = Licence.getDefaultLicence();

  /**
   * test method for http session replication
   */	
    /*
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject( (Serializable)theDisplayState );
		byte[] data = baos.toByteArray();
		oos.close();
		baos.close();
		System.out.println( "sessiondatasize: "+data.length );
		ByteArrayInputStream bais = new ByteArrayInputStream( bais );
		ObjectInputStream ois = new ObjectInputStream( bais );
		ois.readObject();
    ois.close();
    bais.close();
    //session.setAttribute( MasterServlet.STATE, theDisplayState );
    */
	
%>

<table class="defaultContent copyrightTable">
	<tr>
		<td valign="bottom">	
			<table>
				<tr>
					<td colspan="2" class="headline">
						Copyright (c) 2000-2013 hybris AG - <a target="_hybris" href="http://www.hybris.de/">hybris</a>
					</td>
					<td>
						&nbsp;
					</td>
				</tr>

				<tr>
					<td class="padding"><%=localized("licence.version")%>:</td> 
					<td class="padding"><%=Config.getParameter("build.description")%></td>
				</tr>
				<tr>
					<td>&nbsp;</td><td><%=Config.getParameter("build.version")%></td>
				</tr>
				<tr>
					<td>&nbsp;</td><td><%=localized("licence.lastcompile")%>: <%=Config.getParameter("build.builddate")%></td>
				</tr>
				<tr>
					<td>&nbsp;</td><td><%=localized("licence.releasedate")%>: <%=Config.getParameter("build.releasedate")%></td>
				</tr>

				<tr>
					<td class="padding"><%=localized("licence.server")%>:</td> 
					<td class="padding"><%=request.getServerName()+":"+request.getServerPort()%></td>
				</tr>
				<tr>
					<td><%=localized("licence.locale")%>:</td> 
					<td><%=JaloSession.hasCurrentSession()?JaloSession.getCurrentSession().getSessionContext().getLocale().getDisplayName(JaloSession.getCurrentSession().getSessionContext().getLocale())+" - "+JaloSession.getCurrentSession().getSessionContext().getLocale():"n/a"%></td>
				</tr>
				<tr>
					<td><%=localized("licence.timezone")%>:</td> 
					<td><%=JaloSession.hasCurrentSession()?JaloSession.getCurrentSession().getSessionContext().getTimeZone().getDisplayName(JaloSession.getCurrentSession().getSessionContext().getLocale())+" - "+JaloSession.getCurrentSession().getSessionContext().getTimeZone().getID():"n/a"%></td>
				</tr>
				<tr>
					<td colspan="2" class="headline"><%=localized("licence.information")%>:</td>
				</tr>
				<tr>
					<td><%=localized("licence.id")%>:</td><td><%= licence.getID()%></td>
				</tr>
				<tr>
					<td><%=localized("licence.name")%>:</td><td><%= licence.getName()%></td>
				</tr>
				<%
					Date expdate = licence.getExpirationDate();
					String expdateS;
					if(expdate==null) 
					{
						expdateS=localized("licence.expirationdate.unlimited");
					}
					else
					{
						expdateS=expdate.toString();
					}
				%>
				<tr>
					<td><%=localized("licence.expirationdate")%>:</td><td><%=expdateS%></td>
				</tr>
			</table>
<%
	if (de.hybris.platform.licence.Licence.isUnrestrictedForAllExtensions(licence))
   {
			out.println("<br><table><tr><td style=\"width=80%;\">");
			out.println(localized("licence.thisisademo.text")); 
			out.println("</td><td>&nbsp;</td></tr></table>");
	}
%>

		</td>
	</tr>
</table>
