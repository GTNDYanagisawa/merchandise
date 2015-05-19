package de.hybris.platform.sap.b2bsappricing.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.sap.sappricing.services.impl.DefaultSapPartnerService;
import de.hybris.platform.sap.sappricingbol.businessobject.impl.SapPricingPartnerFunctionImpl;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

import org.springframework.beans.factory.annotation.Required;

import sap.hybris.integration.models.constants.SapmodelConstants;


/**
 * SAP B2B Partner Service
 */
public class DefaultB2BSapPartnerService extends DefaultSapPartnerService
{

	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	/**
	 * @return B2BCustomerService<B2BCustomerModel, B2BUnitModel>
	 */
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	/**
	 * @param b2bCustomerService
	 */
	@Required
	public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	/**
	 * @return B2BUnitService<B2BUnitModel, B2BCustomerModel>
	 */
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 */
	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Override
	public SapPricingPartnerFunction getPartnerFunction()
	{

		final SapPricingPartnerFunction partnerFuntion = new SapPricingPartnerFunctionImpl();

		partnerFuntion.setLanguage(getCommonI18NService().getCurrentLanguage().getIsocode().toUpperCase());
		partnerFuntion.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode().toUpperCase());
		partnerFuntion.setSoldTo(getCurrentSapCustomerId());

		return partnerFuntion;
	}

	protected String getCurrentSapCustomerId()
	{

		final B2BUnitModel root = determineB2BUnitOfCurrentB2BCustomer();

		if (root != null)
		{
			return root.getUid();
		}

		// Return SAP reference customer
		return (String) getModuleConfigurationAccess().getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_REFERENCE_CUSTOMER);

	}

	/**
	 * @return the root B2B unit of the current B2B customer
	 */
	protected B2BUnitModel determineB2BUnitOfCurrentB2BCustomer()
	{
		final B2BCustomerModel b2bCustomer = b2bCustomerService.getCurrentB2BCustomer();
		final B2BUnitModel parent = b2bUnitService.getParent(b2bCustomer);
		final B2BUnitModel root = b2bUnitService.getRootUnit(parent);
		return root;

	}

}
