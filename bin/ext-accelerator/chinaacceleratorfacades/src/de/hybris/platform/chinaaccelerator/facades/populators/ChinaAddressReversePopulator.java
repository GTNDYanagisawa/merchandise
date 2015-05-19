/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;

import de.hybris.platform.chinaaccelerator.services.location.CityService;
import de.hybris.platform.chinaaccelerator.services.location.DistrictService;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ChinaAddressReversePopulator extends AddressReversePopulator
{

	private static final Logger LOG = Logger.getLogger(ChinaAddressReversePopulator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator#populate(de.hybris.platform
	 * .commercefacades.user.data.AddressData, de.hybris.platform.core.model.user.AddressModel)
	 */
	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		// YTODO Auto-generated method stub
		super.populate(addressData, addressModel);


		// set city
		if (addressData.getCityData() != null)
		{
			final CityModel city = this.getCityService().getCityForCode(addressData.getCityData().getCode());
			addressModel.setCity(city);
		}

		// set district
		if (addressData.getCityDistrictData() != null)
		{
			final DistrictModel district = this.getDistrictService().getDistrictByCode(addressData.getCityDistrictData().getCode());
			addressModel.setCityDistrict(district);
		}

		// set landline phone number
		if (addressData.getLandlinePhonePart1() != null || addressData.getLandlinePhonePart2() != null
				|| addressData.getLandlinePhonePart3() != null)
		{
			final StringBuilder builder = new StringBuilder(20);
			builder.append(addressData.getLandlinePhonePart1() != null ? addressData.getLandlinePhonePart1().trim() : "");
			if (builder.length() > 0)
			{
				builder.append('-');
			}
			builder.append(addressData.getLandlinePhonePart2() != null ? addressData.getLandlinePhonePart2().trim() : "");
			if (addressData.getLandlinePhonePart2() != null && addressData.getLandlinePhonePart2().trim().length() > 0
					&& (addressData.getLandlinePhonePart3() != null && addressData.getLandlinePhonePart3().trim().length() > 0))
			{
				builder.append('-');
				builder.append(addressData.getLandlinePhonePart3().trim());
			}
			addressModel.setPhone1(builder.toString());
		}

		// set cellphone
		if (addressData.getCellphone() != null)
		{
			addressModel.setCellphone(addressData.getCellphone());
		}



	}

	private CityService cityService;
	private DistrictService districtService;

	protected CityService getCityService()
	{
		return cityService;
	}

	@Required
	public void setCityService(final CityService cityService)
	{
		this.cityService = cityService;
	}

	protected DistrictService getDistrictService()
	{
		return districtService;
	}

	@Required
	public void setDistrictService(final DistrictService districtService)
	{
		this.districtService = districtService;
	}


}
