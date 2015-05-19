<%--
	[y] hybris E-Business Platform

	Copyright (c) 2000-2004 hybris
	All rights reserved.

	This software is the confidential and proprietary information of hybris
	("Confidential Information").  You shall not disclose such Confidential
	Information and shall use it only in accordance with the terms of the
	license agreement you entered into with hybris.

 	version $Revision: 12561 $

--%>
<%@page import="de.hybris.platform.advancedsavedquery.hmc.*"%>
<%@include file="../../head.inc"%>
<%
	AbstractGenericConditionalSearchChip theChip= (AbstractGenericConditionalSearchChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean showQuery = ConfigConstants.getInstance().SHOWQUERY;
%>
<%@page import="de.hybris.platform.advancedsavedquery.jalo.*"%>
<table class="agcsChip" cellspacing="0" cellpadding="0">
<%
	if( theChip.getSearchType() instanceof ViewType 
		 && ((ViewType) theChip.getSearchType()).getDescription() != null
		 && !((ViewType) theChip.getSearchType()).getDescription().equals("") )
	{
%>
		<tr>
			<td class="description" colspan="2">
				<%=((ViewType) theChip.getSearchType()).getDescription()%>
			</td>
		</tr>
<%
	}
%>
	<tr class="pageContext">
		<td colspan="2">
<%
	theChip.getToolbar().render( pageContext );
%>
		</td>
	</tr>
	<tr class="flexibleSearch">
		<td class="flexibleSearch" colspan="2">
			<table class="listtable">
<%
	int span = 2;
		if( theChip.isFlexibleSearchMode() )
		{
			
			SavedQuery savedQuery = theChip.getSavedQuery();
			String query = localized("search.flexible.notselected");
			String description = "";
			if( savedQuery != null )
			{
				if(savedQuery instanceof AdvancedSavedQuery)
					span = 3;
				if( showQuery )
				{
					query = savedQuery.getQuery();
				}
	
				if( savedQuery.getLocalizedProperty("description") != null )
				{
					description = (String) savedQuery.getLocalizedProperty("description");
				}
			}
%>
					<tr class="row">
						<th class="header">
							<div class="header"><%= localized("search.header.query") %></div>
						</th>
						<th class="text" colspan="<%=span%>">
<%
							if( (description != null) && !description.equals("") )
							{
%>
								<div class="text"><%= description %></div>
<%
							}
%>
						</th>
					</tr>
<%
					if( showQuery )
					{
%>
					<tr class="row">
						<td class="header">
							<div class="header">&nbsp;</div>
						</td>
						<td class="text" colspan="<%=span%>">
							<div class="text"><%= query %></div>
						</td>
					</tr>
<%
					}
				}
				if( theChip.getListEntryCount() > 0 )
				{
%>				
					<tr class="row">
						<th class="attribute">
							<div class="attribute"><%=localized("search.header.attribute")%></div>
						</th>
<%
							if( !theChip.isFlexibleSearchMode())
							{
%>
								<th class="locale">
									<div class="locale"><%=localized("search.header.locale")%></div>
								</th>
								<th class="comparator">
									<div class="comparator"><%=localized("search.header.comparator")%></div>
								</th>
<%
							}
							else if(span != 2)
							{
%>
								<th class="locale">
									<div class="locale">&nbsp;</div>
								</th>
								<th class="comparator">
									<div class="comparator">&nbsp;</div>
								</th>
<%
								
							}
%>
						<th class="condition">
							<div class="condition"><%=localized("search.header.condition")%></div>
						</th>
					</tr>
<%
					for (final Iterator theChips = theChip.getListEntries().iterator(); theChips.hasNext();)
					{
						FlexibleConditionChip chip = (FlexibleConditionChip)theChips.next();
%>
						<tr class="row">
<%
							chip.render(pageContext);
%>
						</tr>
<%
					}
				}
%>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<%@include file="../../emptyFooter.inc"%>
		</td>
	</tr>

	<tr class="agcscButton">
		<td class="outer" colspan="2">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("search.button") %>" name="<%= theChip.getCommandID(AbstractGenericConditionalSearchChip.SEARCH) %>" hidefocus="true">
								<span class="label" id="<%= theChip.getUniqueName() %>_searchbutton">
									<%= localized("search.button") %>
								</span>
							</a>
						</div>
						<!-- following is necessary for checkforenter function -->
						<script type="text/javascript">
							theSubmitEventID = "<%= theChip.getCommandID(AbstractGenericConditionalSearchChip.SEARCH) %>";
						</script>
					</td>
<%
					if( !theChip.isFlexibleSearchMode() && !(theChip.getSearchType() instanceof ViewType) && theChip.hasSubtypes() )
					{
%>
						<td class="agcscCheckBox">
							<input type="hidden" name="<%= theChip.getEventID(AbstractGenericConditionalSearchChip.INCLUDE_SUBTYPES) %>" 
									 value="<%= theChip.isIncludeSubtypes() %>"
							/>
							<input type="checkbox" <%= theChip.isIncludeSubtypes() ? "checked" : "" %> 
									 id="<%= theChip.getUniqueName() %>_subtypebox"
									 onclick="document.editorForm.elements['<%= theChip.getEventID(AbstractGenericConditionalSearchChip.INCLUDE_SUBTYPES) %>'].value='<%= !theChip.isIncludeSubtypes()%>';"
									 <%= theChip.getSearchType().isAbstract() ? " disabled" : "" %>
							/>
						</td>
						<td>
							<%= localized("search.include.subtypes") %>
						</td>
<%
					}
%>	
				</tr>
			</table>
		</td>
	</tr>
</table>
