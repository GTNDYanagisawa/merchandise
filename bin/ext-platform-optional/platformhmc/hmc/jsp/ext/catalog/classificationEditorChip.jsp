<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.ClassificationEditorChip,
					 de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment,
					 de.hybris.platform.catalog.jalo.classification.ClassificationClass,
					 de.hybris.platform.catalog.hmc.ClassificationAttributeChip" %>
	
<%
	final ClassificationEditorChip theChip = (ClassificationEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	// this tab is only active if there is a product
	if( theChip.getProduct() != null )
	{
		for( final ClassificationClass cclass : theChip.getClassificationClasses() )
		{
%>
			<div class="classificationheading"><%= theChip.getClassificationName(cclass) %> </div>
			<div class="classificationattributes">
				<table border="0" cellspacing="0" cellpadding="0">
<%
					for( final ClassificationAttributeChip attributeChip : theChip.getClassificationAttributeChips(cclass) )
					{
%>
						<tr>
							<td>
								<% attributeChip.render(pageContext);  %>
							</td>
						</tr>
<%
					}
%>
				</table>
			</div>
<%
		}
	}
%>









<%--
		if( !theChip.getUnboundFeatures().isEmpty() )
		{
%>
			<!-- unbound productfeatures -->
			<table class="unboundFeaturesHeader">
				<tr>
					<td><div class="spacer"/> </td>
					<td class="sectionheader"><%= localized("section.catalog.unboundproductfeatures") %></td>
				</tr>
			</table>

			<table class="unboundFeaturesList" cellpadding="0" cellspacing="0">
				<tr>
					<th class="classCode"><div class="classCode"><%= localized("tab.catalog.classification.code") %></div></th>
					<th class="className"><div class="className"><%= localized("tab.catalog.classification.value") %></div></th>
					<th class="classDescr"><div class="classDescr"><%= localized("tab.catalog.classification.description") %></div></th>
				</tr>
<%
				for( UntypedFeature uf : theChip.getUnboundFeatures() )
				{
					final String description = uf.getAllDescriptions();
%>
					<tr>
						<td class="classCode"><div class="classCode"><%= uf.getName() %></div></td>
						<td class="className"><div class="className"><%= uf.getValuesFormatted() %></div></td>
						<td class="classDescr"><div class="classDescr"><%= description != null ? description : localized("notdefined") %></div></td>
					</tr>
<%
				}
%>
			</table>
<%
		}
	}
--%>
<div class="cecSpacer">&nbsp;</div>
