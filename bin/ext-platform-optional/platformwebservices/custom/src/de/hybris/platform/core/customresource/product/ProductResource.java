/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.core.customresource.product;

import de.hybris.platform.catalog.resource.ProductCatalogVersionDifferenceResource;
import de.hybris.platform.catalog.resource.ProductFeatureResource;
import de.hybris.platform.catalog.resource.ProductReferenceResource;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.resource.link.LinkResource;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.resource.PriceResource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 * Generated resource class for type Product first defined at extension core
 */
@SuppressWarnings("all")
public class ProductResource extends AbstractYResource<ProductModel>
{
	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public ProductResource()
	{
		super("Product");
	}


	/**
	 * Generated HTTP method for covering DELETE requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@DELETE
	public Response deleteProduct()
	{
		return createDeleteResponse().build();
	}

	/*
	*//**
	 * Generated getter for sub resource of type {@link HMCHistoryEntryResource} for current root resource
	 */
	/*
	 * @Path("/hmchistoryentries/{hmchistoryentry}") public AbstractYResource
	 * getHMCHistoryEntryResource(@PathParam("hmchistoryentry") final String resourceKey) { final HMCHistoryEntryResource
	 * resource = resourceCtx.getResource(HMCHistoryEntryResource.class); resource.setResourceId(resourceKey);
	 * resource.setParentResource(this); passUniqueMember(resource); return resource; }
	 */
	/**
	 * Generated getter for sub resource of type {@link LinkResource} for current root resource
	 */
	@Path("/links/{link}")
	public AbstractYResource getLinkResource(@PathParam("link") final String resourceKey)
	{
		final LinkResource resource = resourceCtx.getResource(LinkResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	/**
	 * Generated HTTP method for covering GET requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@GET
	public Response getProduct()
	{
		return createGetResponse().build();
	}

	/**
	 * Generated getter for sub resource of type {@link ProductCatalogVersionDifferenceResource} for current root
	 * resource
	 */
	@Path("/productcatalogversiondifferences/{productcatalogversiondifference}")
	public AbstractYResource getProductCatalogVersionDifferenceResource(
			@PathParam("productcatalogversiondifference") final String resourceKey)
	{
		final ProductCatalogVersionDifferenceResource resource = resourceCtx
				.getResource(ProductCatalogVersionDifferenceResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	/**
	 * Generated getter for sub resource of type {@link ProductFeatureResource} for current root resource
	 */
	@Path("/productfeatures/{productfeature}")
	public AbstractYResource getProductFeatureResource(@PathParam("productfeature") final String resourceKey)
	{
		final ProductFeatureResource resource = resourceCtx.getResource(ProductFeatureResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	/**
	 * Convenience method which just delegates to {@link #getResourceValue()}
	 */
	public ProductModel getProductModel()
	{
		return super.getResourceValue();
	}

	/**
	 * Generated getter for sub resource of type {@link ProductReferenceResource} for current root resource
	 */
	@Path("/productreferences/{productreference}")
	public AbstractYResource getProductReferenceResource(@PathParam("productreference") final String resourceKey)
	{
		final ProductReferenceResource resource = resourceCtx.getResource(ProductReferenceResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	@Path("defaultprice")
	public AbstractResource getDefaultPrice()
	{
		final ProductModel product = this.getResourceValue();
		AbstractResource result = this;
		if (product != null)
		{
			result = resourceCtx.getResource(PriceResource.class);
			result.setParentResource(this);
			((PriceResource) result).setPricingStrategy(((PriceResource) result).new DefaultPricingStrategy());
		}
		return result;
	}

	@Path("lowestquantityprice")
	public AbstractResource getLowestQuantityPrice()
	{
		final ProductModel product = this.getResourceValue();
		AbstractResource result = this;
		if (product != null)
		{
			result = resourceCtx.getResource(PriceResource.class);
			result.setParentResource(this);
			((PriceResource) result).setPricingStrategy(((PriceResource) result).new LowestQuantityPricingStrategy());
		}
		return result;
	}

	@Path("bestvalueprice")
	public AbstractResource getBestValuePrice()
	{
		final ProductModel product = this.getResourceValue();
		AbstractResource result = this;
		if (product != null)
		{
			result = resourceCtx.getResource(PriceResource.class);
			result.setParentResource(this);
			((PriceResource) result).setPricingStrategy(((PriceResource) result).new BestValuePricingStrategy());
		}
		return result;
	}

	/**
	 * Generated HTTP method for covering PUT requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@PUT
	public Response putProduct(final ProductDTO dto)
	{
		return createPutResponse(dto).build();
	}

	/**
	 * Gets the {@link ProductModel} resource which is addressed by current resource request.
	 * 
	 * @see de.hybris.platform.webservices.AbstractYResource#readResource(String)
	 */
	@Override
	protected ProductModel readResource(final String resourceId) throws Exception
	{
		final ProductModel model = new ProductModel();
		model.setCode(resourceId);
		return (ProductModel) readResourceInternal(model);
	}

	/**
	 * Convenience method which just delegates to {@link #setResourceValue(ProductModel)}
	 */
	public void setProductModel(final ProductModel value)
	{
		super.setResourceValue(value);
	}

}
