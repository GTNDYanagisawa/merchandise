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

import com.hybris.cis.api.avs.model.AvsResult;
import com.hybris.cis.api.avs.model.CisFieldError;
import com.hybris.cis.api.model.CisAddress;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.strategies.ShowSuggestedAddressesStrategy;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class CisAddressVerificationResultPopulator implements Populator<AvsResult, AddressVerificationResultData>
{
	private Converter<List<CisAddress>, List<AddressModel>> cisAvsReverseAddressesConverter;
	private Converter<List<CisFieldError>, List<AddressFieldErrorData>> cisAvsFieldErrorConverter;
	private ShowSuggestedAddressesStrategy showSuggestedAddressesStrategy;

	@Override
	public void populate(final AvsResult source, final AddressVerificationResultData target) throws ConversionException
	{
		Assert.assertNotNull("No CIS AVS result supplied", source);

		// add decision
		target.setDecision(AddressVerificationDecision.lookup(source.getDecision().toString().toLowerCase()));

		// add suggested addresses
		if (getShowSuggestedAddressesStrategy().shouldAddressSuggestionsBeShown())
		{
			final List<AddressModel> addresses = getCisAvsReverseAddressesConverter().convert(source.getSuggestedAddresses());
			target.setSuggestedAddresses(addresses);
		}

		final List<AddressFieldErrorData> errorList = getCisAvsFieldErrorConverter().convert(source.getFieldErrors());

		if (target.getDecision().equals(AddressVerificationDecision.REJECT) && CollectionUtils.isEmpty(source.getFieldErrors())
				&& CollectionUtils.isEmpty(source.getSuggestedAddresses()))
		{
			target.setDecision(AddressVerificationDecision.UNKNOWN);
			final List<AddressFieldErrorData> tempList = new ArrayList<AddressFieldErrorData>();
			final AddressFieldErrorData data = new AddressFieldErrorData();
			data.setFieldType(AddressFieldType.UNKNOWN);
			tempList.add(data);
			target.setFieldErrors(tempList);
		}
		else
		{
			target.setFieldErrors(errorList);
		}
	}

	protected ShowSuggestedAddressesStrategy getShowSuggestedAddressesStrategy()
	{
		return showSuggestedAddressesStrategy;
	}

	@Required
	public void setShowSuggestedAddressesStrategy(final ShowSuggestedAddressesStrategy showSuggestedAddressesStrategy)
	{
		this.showSuggestedAddressesStrategy = showSuggestedAddressesStrategy;
	}

	protected Converter<List<CisAddress>, List<AddressModel>> getCisAvsReverseAddressesConverter()
	{
		return cisAvsReverseAddressesConverter;
	}

	@Required
	public void setCisAvsReverseAddressesConverter(
			final Converter<List<CisAddress>, List<AddressModel>> cisAvsReverseAddressesConverter)
	{
		this.cisAvsReverseAddressesConverter = cisAvsReverseAddressesConverter;
	}

	protected Converter<List<CisFieldError>, List<AddressFieldErrorData>> getCisAvsFieldErrorConverter()
	{
		return cisAvsFieldErrorConverter;
	}

	@Required
	public void setCisAvsFieldErrorConverter(
			final Converter<List<CisFieldError>, List<AddressFieldErrorData>> cisAvsFieldErrorConverter)
	{
		this.cisAvsFieldErrorConverter = cisAvsFieldErrorConverter;
	}
}
