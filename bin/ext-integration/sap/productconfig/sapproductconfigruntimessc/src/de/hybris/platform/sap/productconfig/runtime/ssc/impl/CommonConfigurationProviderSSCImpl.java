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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.sce.front.base.DecompItem;
import com.sap.sce.front.base.PricingConditionRate;


public class CommonConfigurationProviderSSCImpl extends BaseConfigurationProviderSSCImpl
{

	private static final Logger LOG = Logger.getLogger(CommonConfigurationProviderSSCImpl.class);

	@Override
	protected ConfigModel fillConfigModel(final String qualifiedId)
	{
		final ConfigModel configModel = getConfigModelFactory().createInstanceOfConfigModel();

		try
		{
			final IConfigSession session = retrieveConfigSession(qualifiedId);

			// Configuration Model
			configModel.setId(qualifiedId);
			final String configId = retrievePlainConfigId(qualifiedId);

			IConfigInfoData configInfo;

			getTimer().start("getConfigInfo");
			configInfo = session.getConfigInfo(configId, false);
			getTimer().stop();

			configModel.setName(configInfo.getConfigName());
			configModel.setConsistent(configInfo.isConsistent());
			configModel.setComplete(configInfo.isComplete());

			//Root instance
			final OrchestratedInstance rootOrchestratedInstance = session.getRootInstanceLocal(configId);

			// Prepare instances (starting with root instance)
			final InstanceModel rootInstanceModel = prepareInstanceModel(session, configModel, rootOrchestratedInstance);

			configModel.setRootInstance(rootInstanceModel);

			// Retrieve root instance price
			retrievePrice(session, configModel);
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot fill configuration model", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot fill retrieve price", e);
		}

		return configModel;
	}

	protected InstanceModel prepareInstanceModel(final IConfigSession session, final ConfigModel configModel,
			final OrchestratedInstance orchestratedInstance) throws IpcCommandException
	{
		final String configId = retrievePlainConfigId(configModel.getId());

		final InstanceModel instanceModel = createInstance(orchestratedInstance);

		final List<CsticGroupModel> goupModelList = prepareCsticGroups(orchestratedInstance);
		instanceModel.setCsticGroups(goupModelList);

		final List<CsticModel> csticModels = createCstics(orchestratedInstance);
		instanceModel.setCstics(csticModels);

		retrieveConfigurationConflicts(configId, instanceModel, session);

		// Prepare subinstances
		final List<InstanceModel> subInstanceModelList = new ArrayList<InstanceModel>();

		final OrchestratedInstance[] orchestratedSubInstances = orchestratedInstance.getPartInstances();

		if (orchestratedSubInstances != null && orchestratedSubInstances.length > 0)
		{
			for (final OrchestratedInstance orchestratedSubInstance : orchestratedSubInstances)
			{
				final InstanceModel subInstanceModel = prepareInstanceModel(session, configModel, orchestratedSubInstance);
				subInstanceModelList.add(subInstanceModel);
			}
		}
		instanceModel.setSubInstances(subInstanceModelList);

		return instanceModel;
	}

	protected InstanceModel createInstance(final OrchestratedInstance orchestratedInstance)
	{
		final InstanceModel instanceModel = getConfigModelFactory().createInstanceOfInstanceModel();

		final String instanceId = String.valueOf(orchestratedInstance.getFirstSharedInstance().getUid());
		instanceModel.setId(instanceId);
		instanceModel.setName(orchestratedInstance.getFirstSharedInstance().getType().getName());

		final String ldn = orchestratedInstance.getFirstSharedInstance().getLangDepName();
		final int sepPos = ldn.indexOf('-');
		instanceModel.setLanguageDependentName(ldn.substring(sepPos + 1));

		String position = "";
		final DecompItem dItem = orchestratedInstance.getDecompItem();
		if (dItem != null)
		{
			position = dItem.getPosition();
		}
		instanceModel.setPosition(position);

		instanceModel.setConsistent(!orchestratedInstance.isConflicting());
		instanceModel.setComplete(orchestratedInstance.isComplete());
		instanceModel.setRootInstance(orchestratedInstance.isRootInstance());

		if (LOG.isTraceEnabled())
		{
			LOG.trace("created InstanceModel from IInstanceDatal: " + instanceModel.toString());
		}

		return instanceModel;
	}

	protected List<CsticModel> createCstics(final OrchestratedInstance orchestratedInstance) throws IpcCommandException
	{
		final List<CsticModel> csticModels = new ArrayList<CsticModel>();

		final OrchestratedCstic[] orchestratedCstics = orchestratedInstance.getCstics();

		if (orchestratedCstics != null)
		{
			for (int ii = 0; ii < orchestratedCstics.length; ii++)
			{
				final OrchestratedCstic orchestratedCstic = orchestratedCstics[ii];

				final CsticModel csticModel = createCsticModel(orchestratedCstic);
				createCsticValues(orchestratedCstic, csticModel);

				csticModels.add(csticModel);
			}
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("created CsticModel from ICsticData: " + csticModels);
		}

		return csticModels;
	}

	protected CsticModel createCsticModel(final OrchestratedCstic orchestratedCstic) throws IpcCommandException
	{
		final CsticModel csticModel = getConfigModelFactory().createInstanceOfCsticModel();

		csticModel.setName(orchestratedCstic.getName());
		csticModel.setLanguageDependentName(orchestratedCstic.getLangDependentName());
		String description = orchestratedCstic.getDescription();
		description = getTextConverter().convertLongText(description);
		csticModel.setLongText(description);

		csticModel.setComplete(!orchestratedCstic.isRequired() || orchestratedCstic.hasValues());

		csticModel.setConsistent(!orchestratedCstic.isConflicting());
		csticModel.setConstrained(orchestratedCstic.getType().isDomainConstrained());
		csticModel.setMultivalued(orchestratedCstic.isMultiValued());
		csticModel.setAllowsAdditionalValues(orchestratedCstic.getType().isAdditionalValuesAllowed());
		csticModel.setEntryFieldMask(orchestratedCstic.getType().getEntryFieldMask());
		csticModel.setIntervalInDomain(orchestratedCstic.isDomainAnInterval());
		csticModel.setReadonly(orchestratedCstic.isReadOnly());
		csticModel.setRequired(orchestratedCstic.isRequired());
		csticModel.setVisible(!orchestratedCstic.isInvisible());

		final String csticAuthor = orchestratedCstic.isUserOwned() ? CsticModel.AUTHOR_USER : CsticModel.AUTHOR_SYSTEM;
		csticModel.setAuthor(csticAuthor);

		csticModel.setValueType(orchestratedCstic.getType().getValueType());
		csticModel.setTypeLength(orchestratedCstic.getType().getTypeLength().intValue());

		final Integer numberScaleInt = orchestratedCstic.getType().getNumberScale();
		final int numberScale = (numberScaleInt != null) ? numberScaleInt.intValue() : 0;
		csticModel.setNumberScale(numberScale);

		final String[] staticDomain = orchestratedCstic.getStaticDomain();
		final int staticDomainLength = (staticDomain != null) ? staticDomain.length : 0;
		csticModel.setStaticDomainLength(staticDomainLength);

		return csticModel;
	}

	protected void createCsticValues(final OrchestratedCstic orchestratedCstic, final CsticModel csticModel)
	{
		boolean containesValueSetByUser = false;

		// Prepare the list of relevant values
		final String[] valuesAssigned = orchestratedCstic.getValues();
		final String[] domain = getDomainValues(orchestratedCstic, csticModel);
		final List<String> valueNames = getValueNames(valuesAssigned, domain);

		// Retrieve delta price map
		final Map<String, PricingConditionRate> deltaPriceMap = orchestratedCstic.getFirstSharedCstic().getDeltaPrices();

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();

		// Process values
		for (final String valueName : valueNames)
		{
			final boolean isDomainValue = isValueContained(valueName, domain);
			final boolean isValueAssigned = isValueContained(valueName, valuesAssigned);

			final boolean isValueSelectable = true;

			PricingConditionRate pricingConditionRate = null;
			if (deltaPriceMap != null)
			{
				pricingConditionRate = deltaPriceMap.get(valueName);
			}
			final CsticValueModel csticValueModel = createModelValue(orchestratedCstic, valueName, isDomainValue, isValueSelectable,
					pricingConditionRate);

			final String authorExternal = orchestratedCstic.getFirstSharedCstic().getValueDBAuthor(valueName);
			csticValueModel.setAuthorExternal(authorExternal);

			if (isValueAssigned)
			{
				assignedValues.add(csticValueModel);
				if (csticValueModel.getAuthor() != null
						&& !csticValueModel.getAuthor().equalsIgnoreCase(CsticValueModel.AUTHOR_SYSTEM)
						&& !orchestratedCstic.isValueDefault(valueName))
				{
					containesValueSetByUser = true;
				}
			}

			if (csticModel.isConstrained() || csticModel.isAllowsAdditionalValues())
			{
				assignableValues.add(csticValueModel);
			}
		}

		csticModel.setAssignedValuesWithoutCheckForChange(assignedValues);
		csticModel.setAssignableValues(assignableValues);

		adjustCsticAuthor(csticModel, containesValueSetByUser);
	}

	private List<String> getValueNames(final String[] valuesAssigned, final String[] domain)
	{
		final List<String> valueNames = new ArrayList<>();

		if (domain != null && domain.length > 0)
		{
			for (final String valueName : domain)
			{
				valueNames.add(valueName);
			}
		}

		if (valuesAssigned != null && valuesAssigned.length > 0)
		{
			for (final String valueName : valuesAssigned)
			{
				if (!valueName.trim().isEmpty() && !valueNames.contains(valueName))
				{
					valueNames.add(valueName);
				}
			}
		}

		return valueNames;
	}

	private String[] getDomainValues(final OrchestratedCstic orchestratedCstic, final CsticModel csticModel)
	{
		final String[] domain;
		if (csticModel.isAllowsAdditionalValues())
		{
			domain = orchestratedCstic.getTypicalDomain();
		}
		else
		{
			domain = orchestratedCstic.getDynamicDomain();
		}
		return domain;
	}


	protected CsticValueModel createModelValue(final OrchestratedCstic orchestratedCstic, final String valueName,
			final boolean isDomainValue, final boolean isValueSelectable, final PricingConditionRate pricingConditionRate)
	{
		final CsticValueModel csticValueModel = getConfigModelFactory().createInstanceOfCsticValueModel();

		csticValueModel.setName(valueName);
		csticValueModel.setLanguageDependentName(orchestratedCstic.getValueLangDependentName(valueName));

		final String csticValueAuthor = orchestratedCstic.isValueUserOwned(valueName) ? CsticValueModel.AUTHOR_USER
				: CsticValueModel.AUTHOR_SYSTEM;
		csticValueModel.setAuthor(csticValueAuthor);

		csticValueModel.setDomainValue(isDomainValue);
		csticValueModel.setSelectable(isValueSelectable);

		// Delta price
		final PriceModel deltaPrice;

		BigDecimal deltaPriceValue = null;
		String deltaPriceUnit = null;
		if (pricingConditionRate != null)
		{
			deltaPriceValue = pricingConditionRate.getConditionRateValue();
			deltaPriceUnit = pricingConditionRate.getConditionRateUnitName();
		}

		if (pricingConditionRate != null && deltaPriceValue != null && deltaPriceUnit != null && !deltaPriceUnit.isEmpty())
		{
			deltaPrice = getConfigModelFactory().createInstanceOfPriceModel();
			deltaPrice.setPriceValue(deltaPriceValue);
			deltaPrice.setCurrency(deltaPriceUnit);
		}
		else
		{
			deltaPrice = getConfigModelFactory().getZeroPriceModel();
		}

		csticValueModel.setDeltaPrice(deltaPrice);

		return csticValueModel;
	}

	private boolean isValueContained(final String valueName, final String[] values)
	{
		boolean isValueContained = false;
		for (final String value : values)
		{
			if (valueName.equals(value))
			{
				isValueContained = true;
				break;
			}
		}
		return isValueContained;
	}

	protected List<CsticGroupModel> prepareCsticGroups(final OrchestratedInstance orchestratedInstance) throws IpcCommandException
	{
		// Group name
		final String[] groupNames = orchestratedInstance.getCsticGroups(false);
		// Group descriptions
		final String[] groupLanguageDependentNames = orchestratedInstance.getCsticGroups(true);

		// All cstics in instance
		final OrchestratedCstic[] orchastratedCstics = orchestratedInstance.getCstics();
		final List<String> csticNamesInInstance = new ArrayList<String>();
		for (final OrchestratedCstic orchastratedCstic : orchastratedCstics)
		{
			csticNamesInInstance.add(orchastratedCstic.getName());
		}

		// Initialize cstic groups
		final List<CsticGroupModel> csticGroupModelList = new ArrayList<CsticGroupModel>();

		for (int i = 0; i < groupNames.length; i++)
		{
			final CsticGroupModel csticGroupModel = getConfigModelFactory().createInstanceOfCsticGroupModel();
			csticGroupModel.setName(groupNames[i]);
			csticGroupModel.setDescription(groupLanguageDependentNames[i]);
			csticGroupModel.setCsticNames(new ArrayList<String>());
			csticGroupModelList.add(csticGroupModel);

			final OrchestratedCstic[] orchastratedCsticsInGroup = orchestratedInstance.getCstics(groupNames[i]);
			final List<String> csticList = new ArrayList<String>();

			for (final OrchestratedCstic orchastratedCstic : orchastratedCsticsInGroup)
			{
				final String csticName = orchastratedCstic.getName();
				csticList.add(csticName);

				if (csticNamesInInstance.contains(csticName))
				{
					csticNamesInInstance.remove(csticName);
				}
			}
			csticGroupModel.setCsticNames(csticList);
		}

		// Add default group
		if (csticNamesInInstance.size() > 0)
		{
			final CsticGroupModel defaultGroup = getConfigModelFactory().createInstanceOfCsticGroupModel();
			defaultGroup.setName(InstanceModel.GENERAL_GROUP_NAME);
			defaultGroup.setCsticNames(csticNamesInInstance);
			csticGroupModelList.add(0, defaultGroup);
		}

		return csticGroupModelList;
	}
}
