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
package de.hybris.platform.webservices.resources.methods;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.resource.order.CartEntriesResource;
import de.hybris.platform.core.resource.order.CartResource;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.webservices.HttpPostResponseBuilder;


public class PostCartEntryToCartEntries extends HttpPostResponseBuilder<CartEntryModel, CartEntryDTO>
{

	private final KeyGenerator keyGenerator;

	public PostCartEntryToCartEntries()
	{
		super();
		keyGenerator = (KeyGenerator) ServicelayerUtils.getApplicationContext().getBean("cartEntryCodeGenerator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.HttpPostResponseBuilder#afterProcessing(java.lang.Object, java.lang.Object,
	 * boolean)
	 */
	@Override
	public void afterProcessing(final CartEntryDTO dto, final CartEntryModel model, final boolean wasNewlyCreated)
	{
		final AbstractOrderModel cart = model.getOrder();
		if (model.getQuantity().longValue() <= 0)
		{
			getServiceLocator().getModelService().remove(model);
		}
		// recalculates cart, you can move it elsewhere, but DO NOT remove!
		getServiceLocator().getCartService().calculateCart((CartModel) cart);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.HttpPostResponseBuilder#beforeProcessing(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void beforeProcessing(final CartEntryDTO dto, final CartEntryModel model)
	{
		final CartModel cart = this.getCart();

		if (dto.getPk() == null)
		{

			// intelligent auto complete of missing values
			if (dto.getOrder() == null)
			{
				final CartDTO cartDto = new CartDTO();
				cartDto.setPk(Long.valueOf(cart.getPk().getLongValue()));
				cartDto.setCode(cart.getCode());
				dto.setOrder(cartDto);
			}

			if (dto.getEntryNumber() == null)
			{
				dto.setEntryNumber(Integer.valueOf((String) keyGenerator.generate()));
			}

			if (dto.getQuantity() == null)
			{
				dto.setQuantity(Long.valueOf(1));
			}

			if (dto.getProduct() != null)
			{
				final ProductDTO pDto = dto.getProduct();
				if (model != null)
				{
					if (pDto.getCatalogVersion() == null)
					{
						final CatalogVersionDTO catalogVersionDto = new CatalogVersionDTO();
						catalogVersionDto.setVersion(model.getProduct().getCatalogVersion().getVersion());
						pDto.setCatalogVersion(catalogVersionDto);
					}

					if (pDto.getCatalogVersion().getCatalog() == null)
					{
						final CatalogDTO catalogDto = new CatalogDTO();
						catalogDto.setId(model.getProduct().getCatalogVersion().getCatalog().getId());
						pDto.getCatalogVersion().setCatalog(catalogDto);
					}
				}
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.HttpPostResponseBuilder#readResource(java.lang.Object)
	 */
	@Override
	public CartEntryModel readResource(final CartEntryDTO dto) throws Exception
	{
		CartEntryModel result = null;
		final CartModel cart = this.getCart();

		// if no PK is available, it can (not must) mean: create new entry
		if (dto.getPk() == null)
		{
			// instead of PK try a lookup via product code of available entries
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (entry.getProduct().getCode().equals(dto.getProduct().getCode()))
				{
					// "magical" quantity check
					// when DTO brings a quantity, it means: update cart quantity with passed dto quantity
					// when DTO brings no quantity, it means: add 'one' to already existing cart quantity
					if (dto.getQuantity() == null)
					{
						dto.setQuantity(Long.valueOf(entry.getQuantity().longValue() + 1));
					}

					if (dto.getEntryNumber() == null)
					{
						dto.setEntryNumber(Integer.valueOf(cart.getEntries().size()));
					}
					result = (CartEntryModel) entry;
					break;
				}
			}
		}
		else
		{
			result = getServiceLocator().getModelService().get(PK.fromLong(dto.getPk().longValue()));
		}
		return result;
	}

	private CartModel getCart()
	{
		//return ((CartEntriesResource) super.getResource()).getCart();
		final CartModel result = ((CartResource) ((CartEntriesResource) getResource()).getParentResource()).getResourceValue();
		return result;
	}

}
