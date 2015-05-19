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
package de.hybris.platform.servicelayer.internal.converter.impl;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.converter.ConverterRegistry;
import de.hybris.platform.servicelayer.internal.converter.ModelConverter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ItemModelConverterTest extends ServicelayerBaseTest
{

	private DiscountModel discountModel;

	@Resource
	private ModelService modelService;

	@Resource
	private ConverterRegistry converterRegistry;

	@Resource
	private I18NService i18NService;

	@Before
	public void setUp()
	{
		createAndPrepareDiscount();
	}

	private void createAndPrepareDiscount()
	{
		final CurrencyModel currencyModel = modelService.create(CurrencyModel.class);
		currencyModel.setIsocode("EUR");

		discountModel = modelService.create(DiscountModel.class);
		discountModel.setCode("discount1");
		discountModel.setCurrency(currencyModel);

		modelService.saveAll(currencyModel, discountModel);
	}

	// PLA-10433
	@Test
	public void testJaloAttrInModelRefOldValue()
	{
		final String discountStringBefore = discountModel.getDiscountString();

		final CurrencyModel newCurrencyModel = modelService.create(CurrencyModel.class);
		newCurrencyModel.setIsocode("USD");
		modelService.save(newCurrencyModel);

		discountModel.setCurrency(newCurrencyModel);
		modelService.save(discountModel);

		final String discountStringAfter = discountModel.getDiscountString();

		assertFalse("The discountString values for two different currencies should be different too!",
				discountStringBefore.equals(discountStringAfter));
	}

	@Test
	public void testIsAttributeModifiedLocalized()
	{
		i18NService.setCurrentLocale(Locale.UK);
		final ModelConverter converter = converterRegistry.getModelConverterByModelType(TitleModel.class);

		final TitleModel model = modelService.create(TitleModel.class);
		model.setCode("test");
		model.setName("test1");
		modelService.save(model);

		assertFalse(converter.isModified(model, TitleModel.NAME));
		model.setName("test2");
		assertTrue("Attribute not marked as modified!", converter.isModified(model, TitleModel.NAME));
		modelService.save(model);

		i18NService.setCurrentLocale(Locale.UK);
		assertFalse(converter.isModified(model, TitleModel.NAME));
		model.setName("test2");
		assertTrue("Attribute not marked as modified!", converter.isModified(model, TitleModel.NAME));
		modelService.save(model);
	}

	@Test
	public void testUnchangedAttributeNotBeingDirtyAfterSave()
	{
		final TitleModel m = new TitleModel();
		m.setCode("t" + System.nanoTime());

		assertTrue(modelService.isNew(m));

		modelService.save(m);

		assertFalse(modelService.isNew(m));
		assertFalse(modelService.isModified(m));
		assertTrue(modelService.isUpToDate(m));

		// setter called but value not changed
		m.setCode(m.getCode());
		assertTrue(modelService.isModified(m));
		// another setter called - this time there is a change
		m.setName("name");
		assertTrue(modelService.isModified(m));

		modelService.save(m);

		assertFalse(modelService.isModified(m));
		assertTrue(modelService.isUpToDate(m));
	}

	@Test
	public void testModifiedTimeAlwaysReloaded() throws InterruptedException
	{
		final TitleModel m = new TitleModel();
		m.setCode("t" + System.nanoTime());
		modelService.save(m);
		m.setName("foo");
		modelService.save(m);

		final Date modTime1 = m.getModifiedtime();

		Thread.sleep(3 * 1000);

		m.setName("bar");
		modelService.save(m);

		final Date modTime2 = m.getModifiedtime();

		assertFalse(modTime1.equals(modTime2));
	}
}
