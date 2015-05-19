/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;

import java.math.BigDecimal;


/**
 * This callback is referenced in messages.xml. It is used to replace the internal SAP number as part of a back end
 * message with the readable product description (from the hybris product model)
 */
public class DefaultInternalNumberReplacementMsgMappingCallback implements MessageMappingCallbackProcessor
{

	/**
	 * 
	 */
	public static final String SAP_INTERNAL_NUMBER_REPLACEMENT_CALLBACK_ID = "sapInternalNumberReplacement";
	private ProductService productService;
	private GenericFactory genericFactory;


	@Override
	public boolean process(final BackendMessage message)
	{
		final String[] vars = message.getVars();
		final String internalNumber = vars[2];

		final Item item = findItem(getCart().getItemList(), internalNumber);
		if (item == null)
		{
			//this means the item was not yet read. We cannot determine the product ID, return and remove message
			return false;
		}
		final String productIdFromBackend = item.getProductId();

		final ProductModel productModel = productService.getProductForCode(productIdFromBackend);

		if (productModel != null)
		{
			final String productDescription = productModel.getName(LocaleUtil.getLocale());
			if (productDescription != null && !productDescription.equals(""))
			{
				vars[2] = productDescription;
			}
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.
	 * AbstractMessageMappingCallbackProcessor#getId()
	 */
	@Override
	public String getId()
	{
		return SAP_INTERNAL_NUMBER_REPLACEMENT_CALLBACK_ID;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	Basket getCart()
	{
		return genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
	}


	Item findItem(final ItemList items, final String posnr)
	{

		final int internalNumber = new BigDecimal(posnr).intValue();
		for (final Item item : items)
		{
			if (item.getNumberInt() == internalNumber)
			{
				return item;
			}
		}
		return null;
	}


}
