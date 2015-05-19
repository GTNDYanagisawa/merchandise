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

import com.hybris.cis.api.avs.model.CisFieldError;
import com.hybris.cis.api.avs.model.CisFieldErrorCode;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.List;


public class CisAvsFieldErrorPopulator implements Populator<List<CisFieldError>, List<AddressFieldErrorData>>
{
	@Override
	public void populate(final List<CisFieldError> source, final List<AddressFieldErrorData> target) throws ConversionException
	{
		boolean streetCheck = false;

		if (source != null)
		{
			for (final CisFieldError error : source)
			{
				final AddressFieldErrorData localError = new AddressFieldErrorData();

				if (!error.getErrorCode().equals(CisFieldErrorCode.CORRECTED))
				{
					switch (error.getField())
					{
						case ADDRESS_LINES:

							if (!streetCheck)
							{
								localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
								localError.setFieldType(AddressFieldType.ADDRESS_LINE1);
								target.add(localError);
								localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
								localError.setFieldType(AddressFieldType.ADDRESS_LINE2);
								target.add(localError);
								streetCheck = true;
							}
							break;

						case STATE:

							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.REGION);
							target.add(localError);
							break;

						case ZIP_CODE:

							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.ZIP_CODE);
							target.add(localError);
							break;

						case COUNTRY:
							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.COUNTRY);
							target.add(localError);
							break;

						case CITY:
							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.CITY);
							target.add(localError);
							break;

						case UNKNOWN:

							if (!streetCheck)
							{
								localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
								localError.setFieldType(AddressFieldType.ADDRESS_LINE1);
								target.add(localError);
								localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
								localError.setFieldType(AddressFieldType.ADDRESS_LINE2);
								target.add(localError);
								streetCheck = true;
							}

							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.REGION);
							target.add(localError);
							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.ZIP_CODE);
							target.add(localError);
							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.COUNTRY);
							target.add(localError);
							localError.setErrorCode(AddressErrorCode.lookup(error.getErrorCode().toString().toLowerCase()));
							localError.setFieldType(AddressFieldType.CITY);
							target.add(localError);
							break;

						default:
							break;
					}
				}
			}
		}
	}
}
