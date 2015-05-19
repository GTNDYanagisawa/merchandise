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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.stock.StockService;

import org.apache.commons.lang.StringUtils;


/**
 * Translator for updating the stock via {@link StockService}.
 */
public class StockTranslator extends AbstractSpecialValueTranslator
{
	private static final String MODIFIER_NAME_ADAPTER = "adapter";
	private static final String DEFAULT_IMPORT_ADAPTER_NAME = "defaultStockImportAdapter";
	private StockImportAdapter stockImportAdapter;

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor)
	{
		String beanName = columnDescriptor.getDescriptorData().getModifier(MODIFIER_NAME_ADAPTER);
		if (StringUtils.isBlank(beanName))
		{
			beanName = DEFAULT_IMPORT_ADAPTER_NAME;
		}
		stockImportAdapter = (StockImportAdapter) Registry.getApplicationContext().getBean(beanName);
	}

	@Override
	public void performImport(final String cellValue, final Item processedItem)
	{
		stockImportAdapter.performImport(cellValue, processedItem);
	}

	/**
	 * @param stockImportAdapter
	 *           the stockImportAdapter to set
	 */
	public void setStockImportAdapter(final StockImportAdapter stockImportAdapter)
	{
		this.stockImportAdapter = stockImportAdapter;
	}
}
