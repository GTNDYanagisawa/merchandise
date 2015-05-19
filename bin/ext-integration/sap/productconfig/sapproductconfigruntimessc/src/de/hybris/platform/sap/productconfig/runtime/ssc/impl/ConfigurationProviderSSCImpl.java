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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticsGroups;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IPriceData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;


/**
 * @deprecated Use the CommonConfigurationProviderSSCImpl class instead.
 */
@Deprecated
public class ConfigurationProviderSSCImpl extends BaseConfigurationProviderSSCImpl
{
	private static final Logger LOG = Logger.getLogger(ConfigurationProviderSSCImpl.class);

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

			// Retrieve instances
			final Map<String, List<IInstanceData>> instanceMap = retrieveInstances(session, configId);

			//Root instance
			final IInstanceData rootInstanceData = instanceMap.get(BaseConfigurationProviderSSCImpl.CONFIG).get(0);

			// Prepare external cstic value authors
			final Map<String, String> csticValueAuthorMap = prepareExternalCsticValueAuthors(session, configId);

			// Prepare instances (starting with root instance)
			final InstanceModel rootInstanceModel = prepareInstanceModel(session, configModel, instanceMap, rootInstanceData,
					csticValueAuthorMap);
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

	protected CsticModel createCsticModel(final IConfigSession session, final String configId, final ICsticData csticData,
			final ICsticHeader csticHeader) throws IpcCommandException
	{
		final CsticModel csticModel = getConfigModelFactory().createInstanceOfCsticModel();
		csticModel.setName(csticHeader.getCsticName());
		csticModel.setLanguageDependentName(csticHeader.getCsticLname());

		csticModel.setComplete(csticData.getCsticComplete().booleanValue());
		csticModel.setConsistent(csticData.getCsticConsistent().booleanValue());
		csticModel.setConstrained(csticHeader.getCsticConstrained().booleanValue());
		csticModel.setMultivalued(csticHeader.getCsticMulti().booleanValue());
		csticModel.setAllowsAdditionalValues(csticHeader.getCsticAdd().booleanValue());
		csticModel.setEntryFieldMask(csticHeader.getCsticEntryFieldMask());
		csticModel.setIntervalInDomain(csticData.getCsticDomainIsInterval().booleanValue());
		csticModel.setReadonly(csticData.getCsticReadonly().booleanValue());
		csticModel.setRequired(csticData.getCsticRequired().booleanValue());
		csticModel.setVisible(csticData.getCsticVisible().booleanValue());
		csticModel.setAuthor(csticData.getCsticAuthor());

		csticModel.setValueType(csticHeader.getCsticValueType().intValue());
		csticModel.setTypeLength(csticHeader.getCsticTypeLength().intValue());
		if (csticHeader.getCsticNumberScale() == null)
		{
			csticModel.setNumberScale(0);
		}
		else
		{
			csticModel.setNumberScale(csticHeader.getCsticNumberScale().intValue());
		}

		csticModel.setStaticDomainLength(calculateStaticDomainLength(session, configId, csticData.getInstanceId(),
				csticHeader.getCsticName()));

		return csticModel;
	}

	protected List<CsticModel> createCstics(final IConfigSession session, final String configId, final ICsticData[] csticsData,
			final Map<String, String> csticValueAuthorMap) throws IpcCommandException
	{
		final List<CsticModel> csticModels = new ArrayList<CsticModel>();

		if (csticsData != null)
		{
			for (int ii = 0; ii < csticsData.length; ii++)
			{
				final ICsticData csticData = csticsData[ii];
				final ICsticHeader csticHeader = csticData.getCsticHeader();

				final CsticModel csticModel = createCsticModel(session, configId, csticData, csticHeader);
				createCsticValues(csticData, csticModel, csticValueAuthorMap);

				csticModels.add(csticModel);
			}
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("created CsticModel from ICsticData: " + csticModels);
		}

		return csticModels;
	}

	protected void createCsticValues(final ICsticData csticData, final CsticModel csticModel,
			final Map<String, String> csticValueAuthorMap)
	{
		final ICsticValueData[] csticValuesData = csticData.getCsticValues();
		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();

		boolean containesValueSetByUser = false;

		for (final ICsticValueData csticValueData : csticValuesData)
		{
			final CsticValueModel csticValueModel = createModelValue(csticValueData);

			// enrich value model with external author
			final String csticValueAutorKey = getValueMapKey(csticData.getInstanceId(), csticData.getCsticHeader().getCsticName(),
					csticValueModel.getName());
			final String authorExternal = csticValueAuthorMap.get(csticValueAutorKey);
			csticValueModel.setAuthorExternal(authorExternal);

			if (csticValueData.getValueAssigned().booleanValue())
			{
				assignedValues.add(csticValueModel);
				if (csticValueData.getValueAuthor() != null
						&& !csticValueData.getValueAuthor().equalsIgnoreCase(CsticValueModel.AUTHOR_SYSTEM)
						&& !csticValueData.getValueDefault().booleanValue())
				{
					containesValueSetByUser = true;
				}
			}

			if ((csticValueModel.isDomainValue() || csticValueData.getValueAssigned().booleanValue())
					&& csticData.getCsticHeader().getCsticConstrained().booleanValue())
			{
				assignableValues.add(csticValueModel);
			}

		}

		csticModel.setAssignedValuesWithoutCheckForChange(assignedValues);
		csticModel.setAssignableValues(assignableValues);

		adjustCsticAuthor(csticModel, containesValueSetByUser);
	}

	protected InstanceModel createInstance(final IInstanceData instanceData)
	{
		final String instanceId = instanceData.getInstId();
		final InstanceModel instanceModel = getConfigModelFactory().createInstanceOfInstanceModel();
		instanceModel.setId(instanceId);
		instanceModel.setName(instanceData.getInstName());
		instanceModel.setLanguageDependentName(instanceData.getInstLname());
		instanceModel.setPosition(instanceData.getInstPosition());
		instanceModel.setConsistent(instanceData.isInstConsistent().booleanValue());
		instanceModel.setComplete(instanceData.isInstComplete().booleanValue());
		instanceModel.setRootInstance(instanceData.getIsRootInstance().booleanValue());

		if (LOG.isTraceEnabled())
		{
			LOG.trace("created InstanceModel from IInstanceDatal: " + instanceModel.toString());
		}

		return instanceModel;
	}

	protected CsticValueModel createModelValue(final ICsticValueData csticValueData)
	{
		final CsticValueModel csticValueModel = getConfigModelFactory().createInstanceOfCsticValueModel();
		csticValueModel.setName(csticValueData.getValueName());
		csticValueModel.setLanguageDependentName(csticValueData.getValueLname());
		csticValueModel.setAuthor(csticValueData.getValueAuthor());
		csticValueModel.setDomainValue(csticValueData.getValueIsDomainValue().booleanValue());
		final String selectable = csticValueData.getValueSelectable();
		csticValueModel.setSelectable(CsticValueModel.TRUE.equals(selectable));

		final PriceModel deltaPrice;
		final IPriceData priceValue = csticValueData.getDeltaPrice();
		if (priceValue != null && priceValue.getPricingRate() != null && priceValue.getPricingUnit() != null
				&& !priceValue.getPricingUnit().isEmpty())
		{
			deltaPrice = getConfigModelFactory().createInstanceOfPriceModel();
			deltaPrice.setPriceValue(priceValue.getPricingRate());
			deltaPrice.setCurrency(priceValue.getPricingUnit());
		}
		else
		{
			deltaPrice = getConfigModelFactory().getZeroPriceModel();
		}
		csticValueModel.setDeltaPrice(deltaPrice);

		return csticValueModel;
	}

	/**
	 * @param configId
	 * @param instanceId
	 * @param session
	 * @param csticsData
	 * @throws IpcCommandException
	 */
	protected List<CsticGroupModel> prepareCsticGroups(final String configId, final String instanceId,
			final IConfigSession session, final ICsticData[] csticsData) throws IpcCommandException
	{
		if (csticsData == null)
		{
			return new ArrayList<CsticGroupModel>(0);
		}

		final int csticCount = csticsData.length;

		// retrive groups of the instance
		getTimer().start("getCsticGroupss");
		final ICsticsGroups[] csticGroups = session.getCsticGroups(configId, instanceId);
		getTimer().stop();

		// initialize cstic groups
		final List<CsticGroupModel> csticGroupModelList = new ArrayList<CsticGroupModel>(csticGroups.length);

		final Map<String, CsticGroupModel> csticGroupModelMap = new HashMap<String, CsticGroupModel>();
		final Map<String, Map<Integer, String>> csticOrderInGroupMap = new HashMap<String, Map<Integer, String>>();

		for (final ICsticsGroups csticGroup : csticGroups)
		{
			final CsticGroupModel csticGroupModel = getConfigModelFactory().createInstanceOfCsticGroupModel();
			csticGroupModel.setName(csticGroup.getGroupName());
			csticGroupModel.setDescription(csticGroup.getGroupDescription());
			csticGroupModel.setCsticNames(new ArrayList<String>());
			csticGroupModelList.add(csticGroupModel);

			csticGroupModelMap.put(csticGroupModel.getName(), csticGroupModel);
			csticOrderInGroupMap.put(csticGroupModel.getName(), new HashMap<Integer, String>());
		}

		// add cstic to groups
		for (final ICsticData csticData : csticsData)
		{
			final String csticName = csticData.getCsticHeader().getCsticName();
			final Integer positionInFirstGroup = csticData.getCsticGroupPosition();
			final Integer positionInInstance = csticData.getCsticInstPosition();

			final String[] groupNames = csticData.getCsticHeader().getCsticGroups();

			if (groupNames == null || groupNames.length == 0)
			{
				// no group assignment for the cstic -> Default group
				CsticGroupModel defaultGroup = csticGroupModelMap.get(InstanceModel.GENERAL_GROUP_NAME);
				if (defaultGroup == null)
				{
					defaultGroup = getConfigModelFactory().createInstanceOfCsticGroupModel();
					defaultGroup.setName(InstanceModel.GENERAL_GROUP_NAME);
					csticGroupModelList.add(0, defaultGroup);
					csticGroupModelMap.put(InstanceModel.GENERAL_GROUP_NAME, defaultGroup);
					csticOrderInGroupMap.put(InstanceModel.GENERAL_GROUP_NAME, new HashMap<Integer, String>());
				}
				csticOrderInGroupMap.get(InstanceModel.GENERAL_GROUP_NAME).put(positionInInstance, csticName);
			}

			else
			{
				boolean firstGroup = true;
				for (final String groupName : groupNames)
				{
					final CsticGroupModel csticGroupModel = csticGroupModelMap.get(groupName);
					// if a cstic is assigned to more than one group, ssc api provide only the cstic position in the first group,
					// therefore we use the cstic instance position to place such cstic in the group; we place them at the end of the group
					final Integer position = firstGroup ? positionInFirstGroup : Integer.valueOf(positionInInstance.intValue()
							+ csticCount);

					csticOrderInGroupMap.get(csticGroupModel.getName()).put(position, csticName);
					firstGroup = false;
				}
			}
		}

		// generate cstic lists for cstic groups
		for (final CsticGroupModel csticGroupModel : csticGroupModelList)
		{
			final String groupName = csticGroupModel.getName();
			final Map<Integer, String> csticMap = csticOrderInGroupMap.get(groupName);
			final Map<Integer, String> treeMap = new TreeMap<Integer, String>(csticMap);
			final List<String> csticList = new ArrayList<String>(treeMap.values());
			csticGroupModel.setCsticNames(csticList);
		}

		return csticGroupModelList;
	}

	protected Map<String, String> prepareExternalCsticValueAuthors(final IConfigSession session, final String configId)
	{
		final Map<String, String> csticValueAuthorMap = new HashMap<String, String>();
		try
		{
			final IConfigContainer configContainer = session.getConfigItemInfo(configId);
			final ICsticData[] csticDataAray = configContainer.getArrCsticContainer();

			for (final ICsticData csticData : csticDataAray)
			{
				final String instanceId = csticData.getInstanceId();
				final String csticName = csticData.getCsticHeader().getCsticName();
				final ICsticValueData[] csticValueDataArray = csticData.getCsticValues();

				for (final ICsticValueData csticValueData : csticValueDataArray)
				{
					final String csticValueName = csticValueData.getValueName();
					final String csticAuthor = csticValueData.getValueAuthor();
					final String valueMapKey = getValueMapKey(instanceId, csticName, csticValueName);
					csticValueAuthorMap.put(valueMapKey, csticAuthor);
				}

			}
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot retrieve external cstic value authors", e);
		}

		return csticValueAuthorMap;
	}

	protected InstanceModel prepareInstanceModel(final IConfigSession session, final ConfigModel configModel,
			final Map<String, List<IInstanceData>> instanceMap, final IInstanceData instanceData,
			final Map<String, String> csticValueAuthorMap) throws IpcCommandException
	{

		final String configId = retrievePlainConfigId(configModel.getId());
		final String instanceId = instanceData.getInstId();
		final InstanceModel instanceModel = createInstance(instanceData);

		getTimer().start("getCstics");
		final ICsticData[] csticsData = session.getCstics(configId, instanceId);
		getTimer().stop();

		final List<CsticGroupModel> goupModelList = prepareCsticGroups(configId, instanceId, session, csticsData);
		instanceModel.setCsticGroups(goupModelList);

		final List<CsticModel> csticModels = createCstics(session, configId, csticsData, csticValueAuthorMap);
		instanceModel.setCstics(csticModels);

		retrieveConfigurationConflicts(configId, instanceModel, session);

		//  prepare subinstances
		final List<InstanceModel> subInstanceModelList = new ArrayList<InstanceModel>();
		final List<IInstanceData> subInstances = instanceMap.get(instanceId);
		if (subInstances != null)
		{
			for (final IInstanceData subInstanceData : subInstances)
			{
				final InstanceModel subInstanceModel = prepareInstanceModel(session, configModel, instanceMap, subInstanceData,
						csticValueAuthorMap);
				subInstanceModelList.add(subInstanceModel);
			}
		}
		instanceModel.setSubInstances(subInstanceModelList);

		return instanceModel;
	}

	protected Map<String, List<IInstanceData>> retrieveInstances(final IConfigSession session, final String configId)
			throws IpcCommandException
	{
		final Map<String, List<IInstanceData>> instanceMap = new HashMap<String, List<IInstanceData>>();

		getTimer().start("getInstances");
		final IInstanceData[] instances = session.getInstances(configId, false);
		getTimer().stop();

		for (int i = 0; i < instances.length; i++)
		{
			final IInstanceData instanceData = instances[i];
			String pId = instanceData.getInstPid();

			if (instanceData.getIsRootInstance().booleanValue())
			// root instance
			{
				pId = CONFIG;
			}
			List<IInstanceData> subInstances;
			if (instanceMap.containsKey(pId))
			{
				subInstances = instanceMap.get(pId);
			}
			else
			{
				subInstances = new ArrayList<IInstanceData>();
				instanceMap.put(pId, subInstances);
			}
			subInstances.add(instanceData);
		}

		return instanceMap;
	}

	protected String getValueMapKey(final String instanceId, final String csticName, final String csticValueName)
	{
		return instanceId + "." + csticName + "." + csticValueName;
	}


}
