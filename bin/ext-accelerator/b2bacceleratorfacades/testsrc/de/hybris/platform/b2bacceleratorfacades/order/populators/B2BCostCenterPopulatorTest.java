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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 */
@UnitTest
public class B2BCostCenterPopulatorTest
{
	private static final String CUR_ISOCODE = "currIsoCode";
	private static final String MODEL_CODE = "code";
	@Mock
	private Converter<B2BBudgetModel, B2BBudgetData> b2bBudgetConverter; //NOPMD
	@Mock
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter; //NOPMD
	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private Converter<AddressModel, AddressData> addressConverter;
	@InjectMocks
	private final B2BCostCenterPopulator b2BCostCenterPopulator = new B2BCostCenterPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testConvert()
	{
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final B2BUnitModel unitModel = mock(B2BUnitModel.class);
		final B2BBudgetModel budgetModel = mock(B2BBudgetModel.class);
		final CurrencyData curData = new CurrencyData();
		final AddressModel addressModel = mock(AddressModel.class);
		curData.setIsocode(CUR_ISOCODE);

		given(costCenter.getBudgets()).willReturn(Collections.singleton(budgetModel));
		given(costCenter.getUnit()).willReturn(unitModel);
		given(unitModel.getAddresses()).willReturn(Collections.singleton(addressModel));
		given(costCenter.getCode()).willReturn(MODEL_CODE);
		given(costCenter.getCurrency()).willReturn(currencyModel);
		given(costCenter.getUnit()).willReturn(unitModel);
		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		given(currencyConverter.convert(currencyModel)).willReturn(curData);

		//TODO Convert to populate method
		final B2BCostCenterData costCenterData = new B2BCostCenterData();
		b2BCostCenterPopulator.populate(costCenter, costCenterData);
		Assert.assertEquals(MODEL_CODE, costCenterData.getCode());
		Assert.assertEquals(costCenter.getName(), costCenterData.getName());
		Assert.assertEquals(CUR_ISOCODE, costCenterData.getCurrency().getIsocode());
		Assert.assertEquals(costCenter.getActive(), Boolean.valueOf(costCenterData.isActive()));
		Assert.assertTrue(costCenterData.getUnit().getAddresses().size() == 1);
	}
}
