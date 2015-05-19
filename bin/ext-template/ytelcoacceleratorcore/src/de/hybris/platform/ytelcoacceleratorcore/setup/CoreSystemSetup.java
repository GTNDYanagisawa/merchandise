/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.ytelcoacceleratorcore.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ytelcoacceleratorcore.constants.YtelcoacceleratorCoreConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = YtelcoacceleratorCoreConstants.EXTENSIONNAME)
public class CoreSystemSetup extends AbstractSystemSetup
{
	public static final String IMPORT_ACCESS_RIGHTS = "accessRights";

	private TypeService typeService;
	private ModelService modelService;
	private UserService userService;

	/**
	 * This method will be called by system creator during initialization and system update. Be sure that this method can
	 * be called repeatedly.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		importImpexFile(context, "/ytelcoacceleratorcore/import/common/essential-data.impex");
		importImpexFile(context, "/ytelcoacceleratorcore/import/common/countries.impex");
		importImpexFile(context, "/ytelcoacceleratorcore/import/common/delivery-modes.impex");

		importImpexFile(context, "/ytelcoacceleratorcore/import/common/themes.impex");
		importImpexFile(context, "/ytelcoacceleratorcore/import/common/user-groups.impex");

		// add SearchRestriction for customergroup on BundleTemplate/catalogVersion
		final ComposedTypeModel restrictedType = getTypeService().getComposedTypeForClass(BundleTemplateModel.class);
		final PrincipalModel principal = getUserService().getUserGroupForUID("customergroup");
		final SearchRestrictionModel searchRestriction = getModelService().create(SearchRestrictionModel.class);
		searchRestriction.setCode("Frontend_BundleTemplate");
		searchRestriction.setActive(Boolean.TRUE);
		searchRestriction.setQuery("{catalogVersion} IN ( ?session.catalogversions ) OR  {catalogVersion} IS NULL");
		searchRestriction.setRestrictedType(restrictedType);
		searchRestriction.setPrincipal(principal);
		searchRestriction.setGenerate(Boolean.TRUE);
		try
		{
			getModelService().save(searchRestriction);
		}
		catch (final ModelSavingException e)
		{
			// if the function is called repeatedly the Search restriction already exists
			logInfo(context, "Cannot add SearchRestriction for customergroup on BundleTemplate/catalogVersion as it already exists");
		}
	}

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", true));

		return params;
	}

	/**
	 * This method will be called during the system initialization.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final boolean importAccessRights = getBooleanSystemSetupParameter(context, IMPORT_ACCESS_RIGHTS);

		final List<String> extensionNames = getExtensionNames();

		if (importAccessRights && extensionNames.contains("cmscockpit"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cmscockpit/cmscockpit-users.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cmscockpit/cmscockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("btgcockpit"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cmscockpit/btgcockpit-users.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cmscockpit/btgcockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("productcockpit"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/productcockpit/productcockpit-users.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/productcockpit/productcockpit-access-rights.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/productcockpit/productcockpit-constraints.impex");
		}

		if (importAccessRights && extensionNames.contains("cscockpit"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cscockpit/cscockpit-users.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/cscockpit/cscockpit-access-rights.impex");
		}

		if (importAccessRights && extensionNames.contains("reportcockpit"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/reportcockpit/reportcockpit-users.impex");
			importImpexFile(context, "/ytelcoacceleratorcore/import/cockpits/reportcockpit/reportcockpit-access-rights.impex");
		}

		if (extensionNames.contains("mcc"))
		{
			importImpexFile(context, "/ytelcoacceleratorcore/import/common/mcc-sites-links.impex");
		}
	}

	protected List<String> getExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	protected <T> T getBeanForName(final String name)
	{
		return (T) Registry.getApplicationContext().getBean(name);
	}

	/**
	 * @return the typeService
	 */
	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
