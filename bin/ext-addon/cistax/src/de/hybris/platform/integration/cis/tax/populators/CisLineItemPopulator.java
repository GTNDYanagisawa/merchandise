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
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class CisLineItemPopulator implements Populator<OndemandDiscountedOrderEntry, CisLineItem>
{
	private TaxCodeStrategy taxCodeStrategy;

	@Override
	public void populate(final OndemandDiscountedOrderEntry source, final CisLineItem target) throws ConversionException
	{
		if (source == null || source.getOrderEntry() == null || target == null)
		{
			throw new ConversionException("AbstractOrderEntry and target have to be specified");
		}

		target.setId(source.getOrderEntry().getEntryNumber());
		target.setItemCode(source.getOrderEntry().getProduct().getCode());
		// currently for avalara the limit of this field is 255
		target.setProductDescription(StringUtils.substring(source.getOrderEntry().getProduct().getName(), 0, 255));
		target.setQuantity(Integer.valueOf(source.getOrderEntry().getQuantity().intValue()));
		target.setUnitPrice(source.getDiscountedUnitPrice());
		target.setTaxCode(getTaxCodeStrategy().getTaxCodeForCodeAndOrder(source.getOrderEntry().getProduct().getCode(),
				source.getOrderEntry().getOrder()));
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
}
