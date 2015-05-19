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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ConfigurationFacadeImpl implements ConfigurationFacade
{
	private ProductConfigurationService configurationService;
	private ConfigPricing configPricing;
	private CsticTypeMapper csticTypeMapper;
	private ConfigConsistenceChecker configConsistenceChecker;

	private ProductDao productDao;

	private static final Logger LOG = Logger.getLogger(ConfigurationFacadeImpl.class);

	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Required
	public void setCsticTypeMapper(final CsticTypeMapper csticTypeMapper)
	{
		this.csticTypeMapper = csticTypeMapper;
	}

	@Required
	public void setConfigConsistenceChecker(final ConfigConsistenceChecker configConsistenceChecker)
	{
		this.configConsistenceChecker = configConsistenceChecker;
	}

	@Required
	public void setConfigPricing(final ConfigPricing configPricing)
	{
		this.configPricing = configPricing;
	}

	@Override
	public ConfigurationData getConfiguration(ConfigurationData configData)
	{
		final String configId = configData.getConfigId();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("get configuration by configId [CONFIG_ID='" + configId + "';PRODUCT_CODE='"
					+ configData.getKbKey().getProductCode() + "']");
		}

		final ConfigModel configModel = configurationService.retrieveConfigurationModel(configId);

		final List<UiGroupData> csticGroups = getCsticGroupsFromModel(configModel);
		configData.setGroups(csticGroups);

		configData.setShowLegend(isShowLegend(configData.getGroups()));

		final PricingData pricingData = configPricing.getPricingData(configModel);
		configData.setPricing(pricingData);

		configData = configConsistenceChecker.checkConfiguration(configData);

		return configData;
	}

	private List<UiGroupData> getCsticGroupsFromModel(final ConfigModel configModel)
	{
		final List<UiGroupData> csticGroups = getGroupsFromInstance(configModel.getRootInstance());

		return csticGroups;
	}


	private UiGroupData createUiGroup(final InstanceModel instance)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "']");
		}

		final UiGroupData uiGroup = new UiGroupData();

		final String groupId = UiGroupHelperImpl.generateGroupIdForInstance(instance);
		final String groupName = instance.getName();
		uiGroup.setId(groupId);
		uiGroup.setName(groupName);
		uiGroup.setDescription(instance.getLanguageDependentName());

		//retrieve (sub)instance product description from catalog if available
		final List<ProductModel> products = productDao.findProductsByCode(groupName);

		if (products != null && products.size() == 1)
		{
			final ProductModel product = products.get(0);
			final String productName = product.getName();
			if (productName != null && !productName.isEmpty())
			{
				uiGroup.setDescription(productName);
			}
			final String summaryText = product.getSummary();
			uiGroup.setSummaryText(summaryText);
		}

		// if no group (subinstance) language dependent description available at all, use the subinstance name
		if (uiGroup.getDescription() == null || uiGroup.getDescription().isEmpty())
		{
			uiGroup.setDescription("[" + groupName + "]");
		}

		uiGroup.setGroupType(GroupType.INSTANCE);

		final List<UiGroupData> subGroups = getGroupsFromInstance(instance);
		uiGroup.setSubGroups(subGroups);
		uiGroup.setCstics(new ArrayList<CsticData>());
		uiGroup.setConfigurable(isUiGroupConfigurable(subGroups));
		uiGroup.setOneConfigurableSubGroup(isOneSubGroupConfigurable(subGroups));
		return uiGroup;
	}


	private boolean isUiGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}

		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable())
			{
				return true;
			}

		}
		return false;
	}


	private boolean isOneSubGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}

		int numberOfConfigurableGroups = 0;

		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable() && ++numberOfConfigurableGroups > 1)
			{
				return false;
			}

		}

		return numberOfConfigurableGroups == 1;

	}

	private List<UiGroupData> getGroupsFromInstance(final InstanceModel instance)
	{
		final List<UiGroupData> csticGroups = new ArrayList<>();
		final List<CsticGroup> csticModelGroups = instance.retrieveCsticGroupsWithCstics();
		final String prefix = UiGroupHelperImpl.generateGroupIdForInstance(instance);
		for (final CsticGroup csticModelGroup : csticModelGroups)
		{
			final UiGroupData csticDataGroup = createCsticGroup(csticModelGroup, prefix);
			if (csticDataGroup.getCstics() == null || csticDataGroup.getCstics().size() == 0)
			{
				continue;
			}
			csticDataGroup.setConfigurable(true);
			csticGroups.add(csticDataGroup);
		}
		final List<InstanceModel> subInstances = instance.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			final UiGroupData uiGroup = createUiGroup(subInstance);
			csticGroups.add(uiGroup);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("get subgroups for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "';NUM_GROUPS='"
					+ csticGroups.size() + "']");
		}
		return csticGroups;
	}

	private UiGroupData createCsticGroup(final CsticGroup csticModelGroup, String prefix)
	{
		final UiGroupData uiGroupData = new UiGroupData();

		// cstic group name is unique (inside an instance), there is no cstic group id
		// For ui groups we can use the cstic group name as ui group id as well (additional to the ui group name)
		final String csticGroupName = csticModelGroup.getName();
		prefix = prefix + "." + csticGroupName;
		uiGroupData.setId(prefix);
		uiGroupData.setName(csticGroupName);

		uiGroupData.setDescription(csticModelGroup.getDescription());
		uiGroupData.setGroupType(GroupType.CSTIC_GROUP);

		uiGroupData.setCstics(getListOfCsticData(csticModelGroup.getCstics(), prefix));
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for csticGroup [NAME='" + csticModelGroup.getName() + "';GROUP_PREFIX='" + prefix
					+ "';CSTICS_IN_GROUP='" + uiGroupData.getCstics().size() + "']");
		}

		return uiGroupData;
	}

	@Override
	public ConfigurationData getConfiguration(final KBKeyData kbKey)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("get configuration by kbkey [PRODUCT_CODE='" + kbKey.getProductCode() + "']");
		}

		final ConfigModel configModel = configurationService.createDefaultConfiguration(new KBKeyImpl(kbKey.getProductCode(), kbKey
				.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()));

		final ConfigurationData configData = new ConfigurationData();
		configData.setKbKey(kbKey);
		configData.setConfigId(configModel.getId());

		final PricingData pricingData = configPricing.getPricingData(configModel);
		configData.setPricing(pricingData);

		final List<UiGroupData> csticGroups = getCsticGroupsFromModel(configModel);
		configData.setGroups(csticGroups);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Configuration has " + csticGroups.size() + " group(s)");
		}

		final boolean showLegend = isShowLegend(configData.getGroups());
		configData.setShowLegend(showLegend);

		return configData;
	}

	/**
	 * @return true is at least one mandatory cstic exists
	 */
	protected boolean isShowLegend(final List<UiGroupData> groups)
	{
		if (groups == null || groups.isEmpty())
		{
			return false;
		}

		for (final UiGroupData group : groups)
		{
			for (final CsticData cstic : group.getCstics())
			{
				if (cstic.isRequired())
				{
					return true;
				}
			}

			if (isShowLegend(group.getSubGroups()))
			{
				return true;
			}
		}

		return false;
	}


	private List<CsticData> getListOfCsticData(final List<CsticModel> csticModelList, final String prefix)
	{
		final List<CsticData> cstics = new ArrayList<>();
		for (final CsticModel model : csticModelList)
		{
			if (!model.isVisible())
			{
				continue;
			}

			cstics.add(csticTypeMapper.mapCsticModelToData(model, prefix));
		}
		return cstics;
	}



	@Override
	public void updateConfiguration(final ConfigurationData configContent)
	{
		final String configId = configContent.getConfigId();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("update configuration [CONFIG_ID='" + configId + "';PRODUCT_CODE='"
					+ configContent.getKbKey().getProductCode() + "']");
		}

		final ConfigModel configModel = configurationService.retrieveConfigurationModel(configId);

		final PricingData pricingData = configPricing.getPricingData(configModel);
		configContent.setPricing(pricingData);

		final InstanceModel rootInstance = configModel.getRootInstance();

		for (final UiGroupData uiGroup : configContent.getGroups())
		{
			updateUiGroup(rootInstance, uiGroup);
		}

		configurationService.updateConfiguration(configModel);
	}

	/**
	 * @param instance
	 * @param uiGroup
	 */
	private void updateUiGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{
		if (uiGroup.getGroupType() == GroupType.CSTIC_GROUP)
		{
			// cstic group
			updateCsticGroup(instance, uiGroup);
		}

		else
		{
			// (sub)instance
			final InstanceModel subInstance = retrieveRelatedInstanceModel(instance, uiGroup);
			final List<UiGroupData> uiSubGroups = uiGroup.getSubGroups();

			if (subInstance != null && uiSubGroups != null)
			{
				for (final UiGroupData uiSubGroup : uiSubGroups)
				{
					updateUiGroup(subInstance, uiSubGroup);
				}
			}
		}
	}

	/**
	 * @param instance
	 * @param uiSubGroup
	 * @return InstanceModel
	 */
	private InstanceModel retrieveRelatedInstanceModel(final InstanceModel instance, final UiGroupData uiSubGroup)
	{
		final String uiGroupId = uiSubGroup.getId();
		final String instanceId = UiGroupHelperImpl.retrieveInstanceId(uiGroupId);
		final List<InstanceModel> subInstances = instance.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			if (subInstance.getId().equals(instanceId))
			{
				return subInstance;
			}
		}
		return null;
	}

	private void updateCsticGroup(final InstanceModel instance, final UiGroupData csticGroup)
	{
		// we need this check for null, in the model the empty lists will be changed to null
		if (csticGroup != null && csticGroup.getCstics() != null)
		{
			for (final CsticData csticData : csticGroup.getCstics())
			{
				if (csticData.getType() != UiType.NOT_IMPLEMENTED)
				{
					updateCsticModelFromCsticData(instance, csticData);
				}
			}
		}
	}

	private void updateCsticModelFromCsticData(final InstanceModel instance, final CsticData csticData)
	{
		final String csticName = csticData.getName();
		final CsticModel cstic = instance.getCstic(csticName);
		if (cstic.isChangedByFrontend())
		{
			return;
		}
		csticTypeMapper.updateCsticModelValuesFromData(csticData, cstic);
	}

	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected ConfigPricing getConfigPricing()
	{
		return configPricing;
	}

	protected CsticTypeMapper getCsticTypeMapper()
	{
		return csticTypeMapper;
	}

	protected ConfigConsistenceChecker getConfigConsistenceChecker()
	{
		return configConsistenceChecker;
	}

	protected ProductDao getProductDao()
	{
		return productDao;
	}

}
