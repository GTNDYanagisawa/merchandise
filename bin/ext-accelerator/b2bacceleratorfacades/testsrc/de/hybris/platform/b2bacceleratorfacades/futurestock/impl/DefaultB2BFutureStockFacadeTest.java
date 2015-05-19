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
package de.hybris.platform.b2bacceleratorfacades.futurestock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.futurestock.FutureStockService;
import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for {@link DefaultB2BFutureStockFacade}
 */
@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class DefaultB2BFutureStockFacadeTest
{
	private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");

	private DefaultB2BFutureStockFacade b2bFutureStockFacade;

	@Mock
	private FutureStockService futureStockService;
	private ConfigurationService configurationService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	LanguageModel languageModel;


	@Before
	public void setUp()
	{
		b2bFutureStockFacade = new DefaultB2BFutureStockFacade();
		b2bFutureStockFacade.setFutureStockService(futureStockService);
		b2bFutureStockFacade.setCommerceCommonI18NService(commerceCommonI18NService);

		configurationService = new StubConfigurationService();

		b2bFutureStockFacade.setConfigurationService(configurationService);
	}

	private Map<String, Map<Date, Integer>> getFutureMap(final String productCode)
	{
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();
		final Map<Date, Integer> futureMap = new HashMap<Date, Integer>();
		try
		{
			futureMap.put(dateformat.parse("20130505"), 1);
			futureMap.put(dateformat.parse("20130303"), 2);
			futureMap.put(dateformat.parse("20130304"), 3);
			futureMap.put(dateformat.parse("20130101"), 4);
			futureMap.put(dateformat.parse("20130102"), 5);
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}
		productsMap.put(productCode, futureMap);
		return productsMap;
	}

	@Test
	public void testGetFutureAvailability()
	{
		final String productCode = "sku01";
		Mockito.when(futureStockService.getFutureAvailability(Mockito.any(List.class))).thenReturn(getFutureMap(productCode));
		Mockito.when(languageModel.getIsocode()).thenReturn("en");
		Mockito.when(commerceCommonI18NService.getDefaultLanguage()).thenReturn(languageModel);
		Mockito.when(commerceCommonI18NService.getLocaleForLanguage(languageModel)).thenReturn(Locale.ENGLISH);

		final ProductModel product = new ProductModel();
		product.setCode(productCode);
		final List<FutureStockData> orderedFutureStock = b2bFutureStockFacade.getFutureAvailability(product);
		Assert.assertNotNull(orderedFutureStock);
		Assert.assertEquals(5, orderedFutureStock.size());
		FutureStockData fsdOld = orderedFutureStock.get(0);
		Assert.assertNotNull(fsdOld);
		// check if returned list is ordered
		for (int i = 1; i < orderedFutureStock.size(); i++)
		{
			final FutureStockData fsd = orderedFutureStock.get(i);
			Assert.assertNotNull(fsd);
			// current element should have a date that is newer or equal to last element

			final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

			try
			{
				final Date newDate = dateFormat.parse(fsd.getFormattedDate());
				final Date oldDate = dateFormat.parse(fsdOld.getFormattedDate());

				Assert.assertTrue(newDate.compareTo(oldDate) > 0);
				fsdOld = fsd;
			}
			catch (final ParseException pe)
			{
				Assert.assertTrue(pe.getMessage(), false);
			}

		}
	}
}

class StubConfigurationService implements ConfigurationService
{

	@Override
	public Configuration getConfiguration()
	{
		return new Configuration()
		{

			@Override
			public Configuration subset(final String prefix)
			{
				return null;
			}

			@Override
			public void setProperty(final String key, final Object value)
			{
				// empty block
			}

			@Override
			public boolean isEmpty()
			{
				return false;
			}

			@Override
			public String[] getStringArray(final String key)
			{
				return null;
			}

			@Override
			public String getString(final String key, final String defaultValue)
			{
				return null;
			}

			@Override
			public String getString(final String key)
			{
				return null;
			}

			@Override
			public Short getShort(final String key, final Short defaultValue)
			{
				return null;
			}

			@Override
			public short getShort(final String key, final short defaultValue)
			{
				return 0;
			}

			@Override
			public short getShort(final String key)
			{
				return 0;
			}

			@Override
			public Object getProperty(final String key)
			{
				return null;
			}

			@Override
			public Properties getProperties(final String key)
			{
				return null;
			}

			@Override
			public Long getLong(final String key, final Long defaultValue)
			{
				return null;
			}

			@Override
			public long getLong(final String key, final long defaultValue)
			{
				return 0;
			}

			@Override
			public long getLong(final String key)
			{
				return 0;
			}

			@Override
			public List getList(final String key, final List defaultValue)
			{
				return null;
			}

			@Override
			public List getList(final String key)
			{
				return null;
			}

			@Override
			public Iterator getKeys(final String prefix)
			{
				return null;
			}

			@Override
			public Iterator getKeys()
			{
				return null;
			}

			@Override
			public Integer getInteger(final String key, final Integer defaultValue)
			{
				return null;
			}

			@Override
			public int getInt(final String key, final int defaultValue)
			{
				return 0;
			}

			@Override
			public int getInt(final String key)
			{
				return 0;
			}

			@Override
			public Float getFloat(final String key, final Float defaultValue)
			{
				return null;
			}

			@Override
			public float getFloat(final String key, final float defaultValue)
			{
				return 0;
			}

			@Override
			public float getFloat(final String key)
			{
				return 0;
			}

			@Override
			public Double getDouble(final String key, final Double defaultValue)
			{
				return null;
			}

			@Override
			public double getDouble(final String key, final double defaultValue)
			{
				return 0;
			}

			@Override
			public double getDouble(final String key)
			{
				return 0;
			}

			@Override
			public Byte getByte(final String key, final Byte defaultValue)
			{
				return null;
			}

			@Override
			public byte getByte(final String key, final byte defaultValue)
			{
				return 0;
			}

			@Override
			public byte getByte(final String key)
			{
				return 0;
			}

			@Override
			public Boolean getBoolean(final String key, final Boolean defaultValue)
			{
				return null;
			}

			@Override
			public boolean getBoolean(final String key, final boolean defaultValue)
			{
				return false;
			}

			@Override
			public boolean getBoolean(final String key)
			{
				return false;
			}

			@Override
			public BigInteger getBigInteger(final String key, final BigInteger defaultValue)
			{
				return null;
			}

			@Override
			public BigInteger getBigInteger(final String key)
			{
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue)
			{
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(final String key)
			{
				return null;
			}

			@Override
			public boolean containsKey(final String key)
			{
				return false;
			}

			@Override
			public void clearProperty(final String key)
			{
				// empty block
			}

			@Override
			public void clear()
			{
				// empty block
			}

			@Override
			public void addProperty(final String key, final Object value)
			{
				// empty block
			}
		};
	}
}
