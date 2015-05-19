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
package de.hybris.platform.commercefacades.address.impl;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationErrorField;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.AddressVerificationService;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.commerceservices.address.util.AddressVerificationResultUtils;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of AddressVerificationFacade.
 */
public class DefaultAddressVerificationFacade implements AddressVerificationFacade
{
	private ModelService modelService;
	private AddressVerificationService<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> addressVerificationService;

	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<AddressData, AddressModel> addressReverseConverter;

	@Override
	public AddressVerificationResult<AddressVerificationDecision> verifyAddressData(final AddressData addressData)
	{
		final AddressModel addressModel = getModelService().create(AddressModel.class);
		getAddressReverseConverter().convert(addressData, addressModel);

		// Pass the addressModel to the verification service and process the resulting address errors and suggestions
		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = getAddressVerificationService()
				.verifyAddress(addressModel);

		final List<AddressData> suggestedAddresses = new ArrayList<AddressData>();
		if (AddressVerificationResultUtils.hasSuggestedAddresses(result))
		{
			for (final AddressModel suggestedAddressModel : result.getSuggestedAddresses())
			{
				suggestedAddresses.add(getAddressConverter().convert(suggestedAddressModel));
			}
		}

		final Map<String, AddressVerificationErrorField> verificationResultErrorMap = new HashMap<String, AddressVerificationErrorField>();
		if (AddressVerificationResultUtils.requiresErrorHandling(result))
		{
			verificationResultErrorMap.putAll(generateVerificationResultErrorMap(result));
		}

		final AddressVerificationResult<AddressVerificationDecision> avsResult = new AddressVerificationResult<AddressVerificationDecision>();
		avsResult.setDecision(result.getDecision());
		avsResult.setSuggestedAddresses(suggestedAddresses);
		avsResult.setErrors(verificationResultErrorMap);

		return avsResult;
	}

	@Override
	public boolean isCustomerAllowedToIgnoreAddressSuggestions()
	{
		return getAddressVerificationService().isCustomerAllowedToIgnoreSuggestions();
	}

	protected Map<String, AddressVerificationErrorField> generateVerificationResultErrorMap(
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result)
	{
		final Map<String, AddressVerificationErrorField> verificationResultErrorMap = new HashMap<String, AddressVerificationErrorField>();
		if (result != null && result.getFieldErrors() != null && !result.getFieldErrors().isEmpty())
		{
			for (final AddressFieldErrorData<AddressFieldType, AddressErrorCode> errorData : result.getFieldErrors())
			{
				final AddressVerificationErrorField errorField = new AddressVerificationErrorField();
				final String typeString = errorData.getFieldType().getTypeString();
				errorField.setName(typeString);
				verificationResultErrorMap.put(typeString, errorField);

				switch (errorData.getErrorCode() != null ? errorData.getErrorCode() : AddressErrorCode.UNKNOWN)
				{
					case MISSING:
						errorField.setMissing(true);
						errorField.setInvalid(false);
						break;
					case INVALID:
						errorField.setInvalid(true);
						errorField.setMissing(false);
						break;
					default:
						errorField.setInvalid(true);
						errorField.setMissing(false);
						break;
				}
			}
		}
		return verificationResultErrorMap;
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

	protected AddressVerificationService<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> getAddressVerificationService()
	{
		return addressVerificationService;
	}

	@Required
	public void setAddressVerificationService(
			final AddressVerificationService<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> addressVerificationService)
	{
		this.addressVerificationService = addressVerificationService;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<AddressData, AddressModel> getAddressReverseConverter()
	{
		return addressReverseConverter;
	}

	@Required
	public void setAddressReverseConverter(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}
}
