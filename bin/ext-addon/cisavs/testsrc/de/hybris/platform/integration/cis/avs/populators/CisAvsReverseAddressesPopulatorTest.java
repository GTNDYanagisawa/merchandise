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
package de.hybris.platform.integration.cis.avs.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.strategies.SuggestedAddressesAmountStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.model.CisAddress;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAvsReverseAddressesPopulatorTest
{
	private CisAvsReverseAddressesPopulator populator;

	@Mock
	private ModelService modelService;

	@Mock
	private CisAvsReverseAddressPopulator reverseCisAddressePopulator;

	@Mock
	private SuggestedAddressesAmountStrategy suggestedAddressesAmountStrategy;


	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this.getClass());
		populator = new CisAvsReverseAddressesPopulator();
		populator.setModelService(modelService);
		populator.setCisAvsReverseAddressPopulator(reverseCisAddressePopulator);
		populator.setSuggestedAddressesAmountStrategy(suggestedAddressesAmountStrategy);

		Mockito.when(modelService.create(AddressModel.class)).thenReturn(new AddressModel());
		Mockito.when(Integer.valueOf(suggestedAddressesAmountStrategy.getSuggestedAddressesAmountToDisplay())).thenReturn(
				Integer.valueOf(1));
	}

	@Test
	public void shouldPopulate()
	{
		final List<CisAddress> source = new ArrayList<CisAddress>();
		source.add(new CisAddress());
		final List<AddressModel> target = new ArrayList<AddressModel>();
		populator.populate(source, target);

		Assert.assertEquals(1, target.size());

	}
}
