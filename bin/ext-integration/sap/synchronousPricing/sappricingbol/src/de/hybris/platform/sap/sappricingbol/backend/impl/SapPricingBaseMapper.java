package de.hybris.platform.sap.sappricingbol.backend.impl;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.sap.sappricingbol.enums.PricingProceduresSubtotal;

import org.springframework.beans.factory.annotation.Required;

import sap.hybris.integration.models.constants.SapmodelConstants;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;


/**
 * SapPricingBaseMapper
 */
public class SapPricingBaseMapper
{
	
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SapPricingBaseMapper.class.getName());
	private ModuleConfigurationAccess moduleConfigurationAccess = null;

	/**
	 * @return ModuleConfigurationAccess
	 */
	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}

	/**
	 * @param moduleConfigurationAccess
	 */
	@Required
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * write import parameters to jco structures
	 * 
	 * @param importParameters
	 */
	public void fillImportParameters(final JCoParameterList importParameters)
	{

		// set control attributes
		final JCoStructure isControl = importParameters.getStructure("IS_CONTROL");
		isControl.setValue("EXTERNAL_FORMAT", SappricingbolConstants.NO);
		isControl.setValue("GROUP_PROCESSING", SappricingbolConstants.YES); 
		isControl.setValue("PRICE_DETAILS", SappricingbolConstants.YES);
		isControl.setValue("KALSM_VARIANT", SappricingbolConstants.NO);

		// set pricing control attributes
		final JCoStructure isPricingControl = isControl.getStructure("PRICING_CONTROL");
		isPricingControl.setValue("GET_SCALE_LEVELS", SappricingbolConstants.NO);
		isPricingControl.setValue("MAX_SCALE_LEVELS", SappricingbolConstants.NO);
		isPricingControl.setValue("PRIC_DETAIL_VAR", SappricingbolConstants.NO);

		isControl.setValue("PRICING_CONTROL", isPricingControl);
		importParameters.setValue("IS_CONTROL", isControl);


		// set global attributes
		final JCoStructure isGlobal = importParameters.getStructure("IS_GLOBAL");
		isGlobal.setValue("PRSDT", SappricingbolConstants.NO);
		isGlobal.setValue("AUART", getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE));
		isGlobal.setValue("VKORG", getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG));
		isGlobal.setValue("VTWEG", getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL));
		isGlobal.setValue("SPART", getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION));
		importParameters.setValue("IS_GLOBAL", isGlobal);
		

		isGlobal.setValue("CALLER_DATA", SappricingbolConstants.NO);

		importParameters.setValue("IV_CALLER_ID", SappricingbolConstants.CALLER_ID);

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("sappricingbol: RFC Call Global Parameters");
			sapLogger.debug(isGlobal.toString());
			
			sapLogger.debug("sappricingbol: RFC Call Control Parameters");
			sapLogger.debug(isControl.toString());
		}
	}

	protected String getProperty(final String name)
	{
		final Object propertyValue = getModuleConfigurationAccess().getProperty(name);

		//some configuration attributes are read as enumeration types, we need to convert them to String.
		if (propertyValue instanceof PricingProceduresSubtotal)
		{
			return ((PricingProceduresSubtotal) propertyValue).getCode();
		}

		return (String) propertyValue;
	}

}
