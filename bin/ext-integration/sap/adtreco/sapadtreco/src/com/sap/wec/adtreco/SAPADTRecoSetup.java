package com.sap.wec.adtreco;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.sap.wec.adtreco.constants.SapadtrecoConstants;
import com.sap.wec.adtreco.model.BTGSAPInitiativeOperandModel;

import de.hybris.platform.btg.enums.BTGRuleType;
import de.hybris.platform.btg.model.BTGConfigModel;
import de.hybris.platform.btg.services.BTGConfigurationService;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;


@SystemSetup(extension = SapadtrecoConstants.EXTENSIONNAME)
public class SAPADTRecoSetup
{
	private BTGConfigurationService btgConfigurationService;
	private ModelService modelService;
	private TypeService typeService;

	@SystemSetup(type = SystemSetup.Type.ALL, process = SystemSetup.Process.ALL)
	public void configureBTG(final SystemSetupContext context)
	{
		final BTGConfigModel config = getBtgConfigurationService().getConfig();
		final Map<BTGRuleType, Collection<ComposedTypeModel>> operandMappings = new LinkedHashMap<BTGRuleType, Collection<ComposedTypeModel>>(
				config.getOperandMapping());
		Collection<ComposedTypeModel> operands = operandMappings.get(BTGRuleType.USER);
		if (operands == null)
		{
			operands = new LinkedHashSet<ComposedTypeModel>();
		}
		else
		{
			operands = new LinkedHashSet<ComposedTypeModel>(operands);
		}
		operandMappings.put(BTGRuleType.USER, operands);

		final ComposedTypeModel operand = getTypeService().getComposedTypeForClass(BTGSAPInitiativeOperandModel.class);
		if (!operands.contains(operand))
		{
			operands.add(operand);
		}
		config.setOperandMapping(operandMappings);
		getModelService().save(config);

	}

	public BTGConfigurationService getBtgConfigurationService()
	{
		return btgConfigurationService;
	}

	@Required
	public void setBtgConfigurationService(final BTGConfigurationService btgConfigurationService)
	{
		this.btgConfigurationService = btgConfigurationService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(TypeService typeService)
	{
		this.typeService = typeService;
	}
}
