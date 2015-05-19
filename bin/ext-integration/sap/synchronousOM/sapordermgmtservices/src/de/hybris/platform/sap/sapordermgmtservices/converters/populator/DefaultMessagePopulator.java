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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.sapordermgmtservices.constants.SapordermgmtservicesConstants;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;


/**
 * 
 */
public class DefaultMessagePopulator implements Populator<Message, CartModificationData>
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderPopulator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Message source, final CartModificationData target) throws ConversionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("populate from: " + source);
		}
		target.setStatusCode(SapordermgmtservicesConstants.STATUS_SAP_ERROR);
		final OrderEntryData entry = new OrderEntryData();
		final ProductData product = new ProductData();
		product.setName(source.getMessageText());
		entry.setProduct(product);
		target.setStatusMessage(source.getMessageText());
		target.setEntry(entry);
	}
}
