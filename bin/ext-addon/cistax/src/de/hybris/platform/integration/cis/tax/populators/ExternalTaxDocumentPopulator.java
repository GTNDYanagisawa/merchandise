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

import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.api.tax.model.CisTaxLine;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import de.hybris.platform.integration.cis.tax.service.TaxValueConversionService;
import de.hybris.platform.integration.cis.tax.strategies.ShippingIncludedStrategy;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;


public class ExternalTaxDocumentPopulator implements Populator<CisTaxDocOrder, ExternalTaxDocument>
{
	private TaxValueConversionService taxValueConversionService;
	private ShippingIncludedStrategy shippingIncludedStrategy;

	@Override
	public void populate(final CisTaxDocOrder source, final ExternalTaxDocument target) throws ConversionException
	{
		if (source == null || source.getTaxDoc() == null)
		{
			throw new ConversionException("No Tax Document supplied for conversion");
		}
		final boolean shippingIncluded = getShippingIncludedStrategy().isShippingIncluded(source);
		target.setShippingCostTaxes(getTaxValueConversionService().getShippingTaxes(source.getTaxDoc().getLineItems(),
				source.getAbstractOrder().getCurrency().getIsocode(), shippingIncluded));

		for (final CisTaxLine cisTaxLine : source.getTaxDoc().getLineItems())
		{
			if (shouldConvertLine(source.getTaxDoc(), Integer.parseInt(cisTaxLine.getId()), shippingIncluded))
			{
				target.setTaxesForOrderEntry(
						Integer.parseInt(cisTaxLine.getId()),
						getTaxValueConversionService().getLineTaxValues(cisTaxLine.getTaxValues(),
								source.getAbstractOrder().getCurrency().getIsocode()));
			}
		}
	}

	protected boolean shouldConvertLine(final CisTaxDoc cisTaxDoc, final int index, final boolean shippingIncluded)
	{
		if (!shippingIncluded)
		{
			return true;
		}

		return index < (cisTaxDoc.getLineItems().size() - 1);
	}

	protected TaxValueConversionService getTaxValueConversionService()
	{
		return taxValueConversionService;
	}

	@Required
	public void setTaxValueConversionService(final TaxValueConversionService taxValueConversionService)
	{
		this.taxValueConversionService = taxValueConversionService;
	}


	protected ShippingIncludedStrategy getShippingIncludedStrategy()
	{
		return shippingIncludedStrategy;
	}

	@Required
	public void setShippingIncludedStrategy(final ShippingIncludedStrategy shippingIncludedStrategy)
	{
		this.shippingIncludedStrategy = shippingIncludedStrategy;
	}
}
