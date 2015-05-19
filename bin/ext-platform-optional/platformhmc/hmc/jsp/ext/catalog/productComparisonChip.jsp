<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.jalo.CatalogManager"%>
<%@page import="de.hybris.platform.catalog.jalo.CatalogVersion"%>
<%@page import="de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment"%>
<%@page import="de.hybris.platform.jalo.type.TypeManager"%>
<%@page import="de.hybris.platform.jalo.product.Product"%>
<%@page import="de.hybris.platform.jalo.product.ProductManager"%>
<%@page import="de.hybris.platform.hmc.webchips.AbstractChip"%>
<%@page import="de.hybris.platform.catalog.hmc.ProductComparisonChip" %>

<%
	final ProductComparisonChip theChip = (ProductComparisonChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String backgroundColor;
%>

<div>
<table class="listtable productComparisonChip">
 	<tr>
 		<th>&nbsp;</th>
<%
		for( Product product : theChip.getProducts() )
		{
%>
			<th>
				<div>
					<a href="#" 
						onclick="setEvent('<%= theChip.getCommandID(ProductComparisonChip.OPEN_PRODUCT) %>', '<%= product.getPK().toString() %>'); setScrollAndSubmit(); return false;"
					>
						<%= product.getName() %>
					</a>
				</div>
			</th>
<%
		}
%>
 	</tr>
 	<tr>
 		<th>
			<div>
				<%= localized( "productcomparison" ) %>
			</div>
 		</th>
<%
		for( Product product : theChip.getProducts() )
		{
%>
			<td class="pccCompIcon">
				<div>
					<a href="#" 
						onclick="setEvent('<%= theChip.getCommandID(ProductComparisonChip.SET_MASTER_PRODUCT) %>', '<%= product.getPK().toString() %>'); setScrollAndSubmit(); return false;"
					>
						<img src="images/ext/catalog/product_comparison_master_16x16.gif">
					</a>
				</div>
			</td>
<%
		}
%>
	</tr>
<%
	Product masterProduct = theChip.getMasterProduct();
	String fontcolor = "";
	String masterValue = "";
	String value = "";
	
	// product code

%>
		<tr>
			<th>
				<div>
					<%= TypeManager.getInstance().getComposedType(Product.class).getAttributeDescriptor( "code" ).getName() %>
				</div>
			</th>
<%
	for( Product product : theChip.getProducts() )
	{
%>
			<td class="pccProductCode">
				<div>
					<%= product.getCode() %>
				</div>
			</td>
<%
	}

%>
		</tr>
<%

	// product catalog version
%>
		<tr>
			<th>
				<div>
					<%= TypeManager.getInstance().getComposedType(Product.class).getAttributeDescriptor( "catalogVersion" ).getName() %>
				</div>
			</th>
<%
	for( Product product : theChip.getProducts() )
	{
			if( masterProduct != null )
			{
				masterValue = CatalogManager.getInstance().getCatalogVersion(masterProduct).toString();
				value = CatalogManager.getInstance().getCatalogVersion(product).toString();
				fontcolor = ( value.equals( masterValue ) ) ? "" : "color:red";
			}
%>
			<td style="<%= fontcolor %>;">
				<div>
					<%= CatalogManager.getInstance().getCatalogVersion(product).getCatalog().getName() %>
					<br>(<%= CatalogManager.getInstance().getCatalogVersion(product).getVersion() %>)
				</div>
			</td>
<%
	}
%>
		</tr>

<%
	// the classification attributes
	int counter = 0;
	for( ClassAttributeAssignment assignment : theChip.getClassAssignments() )
	{
		if( counter % 2 == 0 ) backgroundColor = "#e1e1e1";
		else backgroundColor = "white";
%>
		<tr>
			<th>
				<div>
					<%= theChip.getAttributeName(assignment) %>
				</div>
			</th>
<%
		for( Product product : theChip.getProducts() )
		{
			if( masterProduct != null )
			{
				masterValue = theChip.getValue(assignment, masterProduct );
				value = theChip.getValue(assignment, product);
				fontcolor = ( value.equals( masterValue ) ) ? "" : "color:red";
			}
%>
	 		<td style="background-color:<%= backgroundColor %>;<%= fontcolor %>;">
	 			<div>
	 				<%= theChip.getValue(assignment, product) %>
	 			</div>
	 		</td>
<%
		}
		counter++;
%>			
		</tr>
<%
	}
%>
</table>
 
</div>
