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


import com.hybris.cis.api.model.CisAddress;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.strategies.SuggestedAddressesAmountStrategy;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;
import org.springframework.beans.factory.annotation.Required;


public class CisAvsReverseAddressesPopulator implements Populator<List<CisAddress>, List<AddressModel>>
{
	private Populator<CisAddress, AddressModel> cisAvsReverseAddressPopulator;
	private ModelService modelService;
	private SuggestedAddressesAmountStrategy suggestedAddressesAmountStrategy;

	@Override
	public void populate(final List<CisAddress> source, final List<AddressModel> target) throws ConversionException
	{
		if (source != null)
		{
			final int addressAmount = suggestedAddressesAmountStrategy.getSuggestedAddressesAmountToDisplay();
			for (int i = 0; i < source.size(); i++)
			{
				if (i < addressAmount)
				{
					final CisAddress sourceAddy = source.get(i);
					final AddressModel targetAddy = modelService.create(AddressModel.class);
					cisAvsReverseAddressPopulator.populate(sourceAddy, targetAddy);
					target.add(targetAddy);
				}
			}
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected SuggestedAddressesAmountStrategy getSuggestedAddressesAmountStrategy()
	{
		return suggestedAddressesAmountStrategy;
	}

	@Required
	public void setSuggestedAddressesAmountStrategy(final SuggestedAddressesAmountStrategy suggestedAddressesAmountStrategy)
	{
		this.suggestedAddressesAmountStrategy = suggestedAddressesAmountStrategy;
	}

	protected Populator<CisAddress, AddressModel> getCisAvsReverseAddressPopulator()
	{
		return cisAvsReverseAddressPopulator;
	}

	@Required
	public void setCisAvsReverseAddressPopulator(final Populator<CisAddress, AddressModel> cisAvsReverseAddressPopulator)
	{
		this.cisAvsReverseAddressPopulator = cisAvsReverseAddressPopulator;
	}
}
