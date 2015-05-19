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
package de.hybris.platform.core.customresource.order;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.resource.link.LinkResource;
import de.hybris.platform.core.resource.order.AbstractOrderEntryResource;
import de.hybris.platform.core.resource.order.CartEntriesResource;
import de.hybris.platform.core.resource.order.CartEntryResource;
import de.hybris.platform.core.resource.order.OrderEntryResource;
import de.hybris.platform.servicelayer.internal.resource.order.InMemoryCartEntryResource;
import de.hybris.platform.webservices.AbstractYResource;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 * Generated resource class for type Cart first defined at extension core
 */
@SuppressWarnings("all")
public class CartResource extends AbstractYResource<CartModel>
{
	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public CartResource()
	{
		super("Cart");
	}


	/**
	 * Generated HTTP method for covering DELETE requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@DELETE
	public Response deleteCart()
	{
		return createDeleteResponse().build();
	}

	/**
	 * Generated getter for sub resource of type {@link AbstractOrderEntryResource} for current root resource
	 */
	@Path("/abstractorderentries/{abstractorderentry}")
	public AbstractYResource getAbstractOrderEntryResource(@PathParam("abstractorderentry") final String resourceKey)
	{
		final AbstractOrderEntryResource resource = resourceCtx.getResource(AbstractOrderEntryResource.class);
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
	public Response getCart()
	{
		return createGetResponse().build();
	}

	/**
	 * Generated getter for sub resource of type {@link CartEntryResource} for current root resource
	 */
	@Path("/cartentries/{cartentry}")
	public AbstractYResource getCartEntryResource(@PathParam("cartentry") final String resourceKey)
	{
		final CartEntryResource resource = resourceCtx.getResource(CartEntryResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	@Path("/cartentries")
	public CartEntriesResource getCartEntriesResource()
	{
		//generic part
		final CartEntriesResource result = resourceCtx.getResource(CartEntriesResource.class);
		result.setParentResource(this);

		// TODO: how can this be generated
		final CartModel cart = this.getResourceValue();
		result.setResourceValue((Collection) cart.getEntries());

		return result;
	}

	/**
	 * Convenience method which just delegates to {@link #getResourceValue()}
	 */
	public CartModel getCartModel()
	{
		return super.getResourceValue();
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
	 * Generated getter for sub resource of type {@link InMemoryCartEntryResource} for current root resource
	 */
	@Path("/inmemorycartentries/{inmemorycartentry}")
	public AbstractYResource getInMemoryCartEntryResource(@PathParam("inmemorycartentry") final String resourceKey)
	{
		final InMemoryCartEntryResource resource = resourceCtx.getResource(InMemoryCartEntryResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

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
	 * Generated getter for sub resource of type {@link OrderEntryResource} for current root resource
	 */
	@Path("/orderentries/{orderentry}")
	public AbstractYResource getOrderEntryResource(@PathParam("orderentry") final String resourceKey)
	{
		final OrderEntryResource resource = resourceCtx.getResource(OrderEntryResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	/**
	 * Generated HTTP method for covering PUT requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@PUT
	public Response putCart(final CartDTO dto)
	{
		return createPutResponse(dto).build();
	}

	@POST
	public Response postCart(final CartDTO dto)
	{
		return createPostResponse(dto).build();
	}

	/**
	 * Gets the {@link CartModel} resource which is addressed by current resource request.
	 * 
	 * @see de.hybris.platform.webservices.AbstractYResource#readResource(String)
	 */
	@Override
	protected CartModel readResource(final String resourceId) throws Exception
	{
		final CartModel model = new CartModel();
		model.setCode(resourceId);
		return (CartModel) readResourceInternal(model);
		//		return serviceLocator.getModelService().get(PK.parse(resourceId));
	}

	@Override
	public void afterPut(final Object dto, final CartModel entity, final boolean modelWasCreated)
	{
		// recalculate cart
		serviceLocator.getCartService().calculateCart(entity);
	}

	/**
	 * Convenience method which just delegates to {@link #setResourceValue(CartModel)}
	 */
	public void setCartModel(final CartModel value)
	{
		super.setResourceValue(value);
	}

}
