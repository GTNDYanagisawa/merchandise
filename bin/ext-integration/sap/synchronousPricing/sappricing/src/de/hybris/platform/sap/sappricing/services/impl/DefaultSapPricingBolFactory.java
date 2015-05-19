package de.hybris.platform.sap.sappricing.services.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sappricing.services.SapPricingBolFactory;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;


public class DefaultSapPricingBolFactory implements SapPricingBolFactory
{


	private GenericFactory genericFactory;

	/**
	 * 
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * 
	 * @param genericFactory
	 */
	public void setGenericFactory(GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	@Override
	public SapPricing getSapPricing() {
		return (SapPricing) genericFactory.getBean(SappricingbolConstants.SAP_PRICING_BO);
	}
}
