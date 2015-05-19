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
package de.hybris.platform.integration.cis.tax.populators;

import com.hybris.cis.api.model.CisLineItem;
import de.hybris.platform.commerceservices.externaltax.TaxCodeStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.constants.CistaxConstants;
import de.hybris.platform.integration.cis.tax.strategies.ShippingItemCodeStrategy;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CisLineItem} for Delivery Cost tax calculation
 */
public class DeliveryCisLineItemPopulator implements Populator<AbstractOrderModel, CisLineItem>
{
	private TaxCodeStrategy taxCodeStrategy;
	private ShippingItemCodeStrategy shippingItemCodeStrategy;

	@Override
	public void populate(final AbstractOrderModel source, final CisLineItem target) throws ConversionException
	{
		if (source == null || target == null)
		{
			throw new ConversionException("AbstractOrderModel and target have to be specified");
		}

		target.setId(getShippingItemCodeStrategy().getShippingItemCode(source));
		target.setItemCode(CistaxConstants.EXTERNALTAX_DELIVERY_LINEITEM_ID);
		target.setProductDescription(CistaxConstants.EXTERNALTAX_DELIVERY_DESCRIPTION);
		target.setQuantity(Integer.valueOf(1));
		target.setUnitPrice(BigDecimal.valueOf(source.getDeliveryCost() == null ? 0d : source.getDeliveryCost().doubleValue()));
		target.setTaxCode(getTaxCodeStrategy().getTaxCodeForCodeAndOrder(source.getDeliveryMode().getCode(), source));
	}

	protected TaxCodeStrategy getTaxCodeStrategy()
	{
		return taxCodeStrategy;
	}

	@Required
	public void setTaxCodeStrategy(final TaxCodeStrategy taxCodeStrategy)
	{
		this.taxCodeStrategy = taxCodeStrategy;
	}


	protected ShippingItemCodeStrategy getShippingItemCodeStrategy()
	{
		return shippingItemCodeStrategy;
	}

	@Required
	public void setShippingItemCodeStrategy(final ShippingItemCodeStrategy shippingItemCodeStrategy)
	{
		this.shippingItemCodeStrategy = shippingItemCodeStrategy;
	}

}
