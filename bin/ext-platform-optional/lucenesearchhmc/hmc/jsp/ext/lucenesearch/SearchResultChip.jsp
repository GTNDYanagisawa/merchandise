<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultChip"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultListChip"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultNavigationChip"%>

<%
	SearchResultChip theChip = (SearchResultChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%@page import="de.hybris.platform.hmc.HMCHelper"%>
<%@page import="de.hybris.platform.jalo.JaloSession"%>
<table width="100%">
	<tr>
<%
		if ( theChip.getFailureMessage()!=null )
		{
%>
			<div align="center" style="font: bold"><%= localized("lucenesearch.result.searchfailed") %></div>
			<div align="center"><%= theChip.getFailureMessage() %></div>
<%
		}
		else
		{
			if ( theChip.getResultListChips().isEmpty() )
			{
%>
			<div align="center" style="font: bold"><%= theChip.getLocalizedString("lucenesearch.result.empty",new Object[]{"\""+ HMCHelper.getXSSFilter("img").filter( theChip.getSearchPattern() )+"\"",JaloSession.getCurrentSession().getSessionContext().getLanguage().getName()}) %></div>
<%
			}
			else
			{
%>
				<div align="center" style="font: bold"><%= theChip.getLocalizedString("lucenesearch.result.full",new Object[]{"\""+ HMCHelper.getXSSFilter("img").filter( theChip.getEncodedFacetedSearchPattern() )+"\"",JaloSession.getCurrentSession().getSessionContext().getLanguage().getName()}) %></div>
<%
			}
		}
%>
	</tr>
<% 
	if ( theChip.getFailureMessage()==null && !theChip.getResultListChips().isEmpty() )
	{
%>
		<tr>
			<td valign="top">
<% 
				for(int i=0;i<theChip.getResultListChips().size();i++)
				{
					SearchResultListChip resultChip=theChip.getResultListChips().get(i);
					resultChip.render( pageContext );
					if( i!=(theChip.getResultListChips().size()-1))
					{
%>
						<div style="height: 10px"></div>
<%
					}
				}
			
%>
				</td>
<%
				if(theChip.getNavigationChips().size()!=0)
				{
%>
					<td width="1%"></td>
					<td width="20%" valign="top">
<%
					for(int i=0;i<theChip.getNavigationChips().size();i++)
					{
						SearchResultNavigationChip navigation = theChip.getNavigationChips().get(i);
						navigation.render(pageContext);
						if( i!=(theChip.getNavigationChips().size()-1))
						{
%>
							<div style="height: 10px"></div>
<%
						}
					}
				}
%>
		</td>
	</tr>
<%
	}
%>
</table>
