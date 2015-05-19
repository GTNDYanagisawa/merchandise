// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.user.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import org.apache.log4j.Logger;


public class ChinaUserFacade extends DefaultUserFacade
{

	private static final Logger LOG = Logger.getLogger(ChinaUserFacade.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.user.impl.DefaultUserFacade#editAddress(de.hybris.platform.commercefacades.
	 * user.data.AddressData)
	 */
	@Override
	public void editAddress(final AddressData addressData)
	{
		//super.editAddress(addressData);

		validateParameterNotNullStandardMessage("addressData", addressData);
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
		addressModel.setRegion(null);

		// CHINAACC_START
		// impl bases on DefaultUserFacade, but need to make sure that City and District are null-ed as well
		addressModel.setCity(null);
		addressModel.setCityDistrict(null);
		// CHINAACC_END

		getAddressReversePopulator().populate(addressData, addressModel);
		getCustomerAccountService().saveAddressEntry(currentCustomer, addressModel);
		if (addressData.isDefaultAddress() && !addressModel.equals(currentCustomer.getDefaultShipmentAddress()))
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
		}
	}
}
