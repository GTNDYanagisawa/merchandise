package de.hybris.platform.sap.sappricing.services.impl;

import org.springframework.beans.factory.annotation.Required;

import sap.hybris.integration.models.constants.SapmodelConstants;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricing.services.SapPartnerService;
import de.hybris.platform.sap.sappricingbol.businessobject.impl.SapPricingPartnerFunctionImpl;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class DefaultSapPartnerService implements SapPartnerService {
	
	private CommonI18NService commonI18NService;
	private ModuleConfigurationAccess moduleConfigurationAccess;
	
	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}
	
	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}

	@Required
	public void setModuleConfigurationAccess(
			ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}
	

	@Override
	public SapPricingPartnerFunction getPartnerFunction() {
		
		    SapPricingPartnerFunction partnerFuntion = new SapPricingPartnerFunctionImpl();
			
			partnerFuntion.setLanguage(getCommonI18NService().getCurrentLanguage().getIsocode().toUpperCase());
			partnerFuntion.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode().toUpperCase());
			partnerFuntion.setSoldTo((String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_REFERENCE_CUSTOMER));
			
			return partnerFuntion;
	}

}
