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

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.TextConverter;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigPartOf;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConflictContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IFactData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IJustificationData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKbProfilesData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigPartOf;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.custdev.projects.fbs.slc.pricing.spc.api.SPCConstants.DataModel;


public abstract class BaseConfigurationProviderSSCImpl implements ConfigurationProvider
{
	private static final Logger LOG = Logger.getLogger(BaseConfigurationProviderSSCImpl.class);

	protected static final String CONFIG = "CONFIG";

	private final Map<String, IConfigSession> sessionMap = new Hashtable<String, IConfigSession>();

	private final SSCTimer timer = new SSCTimer();

	private ConfigurationContextAndPricingWrapper contextAndPricingWrapper;
	private ConfigModelFactory configModelFactory;

	@Autowired
	private I18NService i18NService;


	private TextConverter textConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider#createDefaultConfiguration(com.sap.hybris
	 * . productconfig.runtime.interf.KBKey)
	 */
	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		ConfigModel configModel;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Init new config session for:" + kbKey.getProductCode());
		}

		final String configId = initializeDefaultConfiguration(kbKey);
		configModel = fillConfigModel(configId);

		return configModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider#updateConfiguration(de.hybris.platform
	 * .sap .runtime.interf.model.ConfigModel)
	 */
	@Override
	public boolean updateConfiguration(final ConfigModel configModel)
	{

		final String qualifiedId = configModel.getId();
		final InstanceModel rootInstanceModel = configModel.getRootInstance();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Updating config with id: " + configModel.getId());
		}

		final boolean sscUpdated;
		try
		{
			sscUpdated = updateInstance(qualifiedId, rootInstanceModel);
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Could not update instance", e);
		}

		if (LOG.isDebugEnabled())
		{
			if (sscUpdated)
			{
				LOG.debug("Update for config with id: " + configModel.getId() + " executed");
			}
			else
			{
				LOG.debug("there was nothing to update for config with id: " + configModel.getId());
			}
		}
		return sscUpdated;
	}


	/**
	 * @param qualifiedId
	 * @param instanceModel
	 * @throws IpcCommandException
	 */
	protected boolean updateInstance(final String qualifiedId, final InstanceModel instanceModel) throws IpcCommandException
	{
		if (LOG.isTraceEnabled())
		{
			LOG.trace("Update instance : InstanceID = " + instanceModel.getId());
		}
		boolean instanceUpdated = false;

		final String rootInstanceId = instanceModel.getId();
		final List<CsticModel> csticModels = instanceModel.getCstics();

		final List<ICsticData> csticDataList = new ArrayList<ICsticData>();
		final List<ICsticData> csticDataListToClear = new ArrayList<ICsticData>();

		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		final String configId = retrievePlainConfigId(qualifiedId);

		for (final CsticModel csticModel : csticModels)
		{
			if (!csticModel.isChangedByFrontend())
			{
				continue;
			}

			if (LOG.isTraceEnabled())
			{
				LOG.trace("Cstic changed by Frontend: " + csticModel.toString());
			}

			final String csticName = csticModel.getName();
			final boolean withoutDescription = true;


			timer.start("getCstic");
			final ICsticData csticData = session.getCstic(rootInstanceId, csticName, withoutDescription, configId);
			timer.stop();

			final List<String> newValues = getValuesToBeAssigned(csticModel);
			final List<String> oldValues = getValuesPreviouslyAssigned(csticData);

			final ICsticValueData[] valuesToSet = determineValuesToSet(newValues, oldValues);
			if (valuesToSet.length > 0)
			{
				csticData.setCsticValues(valuesToSet);
				csticDataList.add(csticData);
			}

			final ICsticValueData[] valuesToDelete = determineValuesToDelete(newValues, oldValues);
			if (valuesToDelete.length > 0)
			{
				timer.start("getCstic");
				final ICsticData csticDataToClear = session.getCstic(rootInstanceId, csticName, withoutDescription, configId);
				timer.stop();

				csticDataToClear.setCsticValues(valuesToDelete);
				csticDataListToClear.add(csticDataToClear);
			}
		}

		final boolean doDeleteCstics = !csticDataListToClear.isEmpty();
		if (doDeleteCstics)
		{
			deleteCsticValues(qualifiedId, rootInstanceId, csticDataListToClear);
			instanceUpdated = true;
		}

		final boolean doSetCstics = !csticDataList.isEmpty();
		if (doSetCstics)
		{
			setCsticValues(qualifiedId, rootInstanceId, csticDataList);
			instanceUpdated = true;
		}

		final boolean subInstanceUpdated = updateSubInstances(qualifiedId, instanceModel);
		return instanceUpdated || subInstanceUpdated;
	}

	/**
	 * @param qualifiedId
	 * @param instanceModel
	 * @throws IpcCommandException
	 */
	protected boolean updateSubInstances(final String qualifiedId, final InstanceModel instanceModel) throws IpcCommandException
	{

		boolean subInstanceUpdated = false;
		final List<InstanceModel> subInstances = instanceModel.getSubInstances();
		for (final InstanceModel subInstanceModel : subInstances)
		{
			final boolean instanceUpdated = updateInstance(qualifiedId, subInstanceModel);
			subInstanceUpdated = subInstanceUpdated || instanceUpdated;
		}
		return subInstanceUpdated;
	}

	/**
	 * @param qualifiedId
	 * @param rootInstanceId
	 * @param csticDataList
	 * @throws IpcCommandException
	 */
	protected void setCsticValues(final String qualifiedId, final String rootInstanceId, final List<ICsticData> csticDataList)
			throws IpcCommandException
	{
		final ICsticData[] csticDataArray = csticDataList.toArray(new ICsticData[csticDataList.size()]);
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		final String configId = retrievePlainConfigId(qualifiedId);

		timer.start("setCsticsValues");
		session.setCsticsValues(rootInstanceId, configId, false, csticDataArray);
		timer.stop();
	}

	/**
	 * @param qualifiedId
	 * @param rootInstanceId
	 * @param csticDataListToClear
	 * @throws IpcCommandException
	 */
	protected void deleteCsticValues(final String qualifiedId, final String rootInstanceId,
			final List<ICsticData> csticDataListToClear) throws IpcCommandException
	{
		final ICsticData[] csticDataArray = csticDataListToClear.toArray(new ICsticData[csticDataListToClear.size()]);
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		final String configId = retrievePlainConfigId(qualifiedId);

		if (LOG.isTraceEnabled())
		{
			LOG.trace("Delete Cstic values : InstanceID = " + rootInstanceId + " / csticDataList = " + csticDataListToClear);
		}

		timer.start("deleteCsticValues");
		session.deleteCsticValues(rootInstanceId, "false", csticDataArray, configId);
		timer.stop();
	}


	protected List<String> getValuesToBeAssigned(final CsticModel csticModel)
	{
		final List<String> valuesToBeAssigned = new ArrayList<String>();
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		for (final CsticValueModel valueModel : assignedValues)
		{
			valuesToBeAssigned.add(valueModel.getName());
		}
		return valuesToBeAssigned;
	}


	protected List<String> getValuesPreviouslyAssigned(final ICsticData csticData)
	{
		final List<String> valuesPreviouslyAssigned = new ArrayList<String>();
		final ICsticValueData[] valueDataArray = csticData.getCsticValues();
		for (final ICsticValueData valueData : valueDataArray)
		{
			if (valueData.getValueAssigned().booleanValue())
			{
				valuesPreviouslyAssigned.add(valueData.getValueName());
			}
		}
		return valuesPreviouslyAssigned;
	}


	protected ICsticValueData[] determineValuesToSet(final List<String> newValues, final List<String> oldValues)
	{
		final List<ICsticValueData> csticValueDataList = new ArrayList<ICsticValueData>();

		if (LOG.isTraceEnabled())
		{
			LOG.trace("Cstic values to set newValues/oldValues: " + newValues + " / " + oldValues);
		}

		for (final String value : newValues)
		{
			if (!oldValues.contains(value))
			{
				final ICsticValueData valueData = new CsticValueData();
				valueData.setValueName(value);
				csticValueDataList.add(valueData);
			}
		}

		final ICsticValueData[] csticValueDataArray = csticValueDataList.toArray(new ICsticValueData[csticValueDataList.size()]);

		return csticValueDataArray;
	}


	protected ICsticValueData[] determineValuesToDelete(final List<String> newValues, final List<String> oldValues)
	{
		final List<ICsticValueData> csticValueDataList = new ArrayList<ICsticValueData>();

		if (LOG.isTraceEnabled())
		{
			LOG.trace("Cstic values to delete newValues/oldValues: " + newValues + " / " + oldValues);
		}

		for (final String value : oldValues)
		{
			if (!newValues.contains(value))
			{
				final ICsticValueData valueData = new CsticValueData();
				valueData.setValueName(value);
				csticValueDataList.add(valueData);
			}
		}

		final ICsticValueData[] csticValueDataArray = csticValueDataList.toArray(new ICsticValueData[csticValueDataList.size()]);

		return csticValueDataArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider#retrieveConfigurationModel(java.lang
	 * .String)
	 */
	@Override
	public ConfigModel retrieveConfigurationModel(final String qualifiedConfigId)
	{
		final ConfigModel configModel;

		configModel = fillConfigModel(qualifiedConfigId);

		return configModel;
	}


	/**
	 * @param qualifiedId
	 * @return String external configuration as XML
	 */
	@Override
	public String retrieveExternalConfiguration(final String qualifiedId)
	{
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		final String configId = retrievePlainConfigId(qualifiedId);
		String configItemInfoXML;
		try
		{
			timer.start("getConfigItemInfoXML");
			configItemInfoXML = session.getConfigItemInfoXML(configId, false);
			timer.stop();
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot retrieve external configuration XML", e);
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("Created XML configuration :" + configItemInfoXML);
		}

		return configItemInfoXML;
	}


	protected String initializeDefaultConfiguration(final KBKey kbKey)
	{
		IConfigSession session = null;
		String configId = null;

		try
		{
			session = createSession(kbKey);
			configId = createConfig(kbKey, session);
			preparePricingContext(session, configId, kbKey);
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot initialize default configuration", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot initialize pricing context", e);
		}

		final String sessionId = session.getSessionId();
		final String qualifiedId = retrieveQualifiedId(sessionId, configId);

		holdConfigSession(qualifiedId, session);

		return qualifiedId;
	}

	protected String createConfig(final KBKey kbKey, final IConfigSession session) throws IpcCommandException
	{
		String configId;

		IKbProfilesData[] profiles;

		String kbProfile = null;

		String productId = null;
		String productType = null;

		String kbLogsys = kbKey.getKbLogsys();
		String kbName = kbKey.getKbName();
		String kbVersion = kbKey.getKbVersion();

		if (kbName != null && kbVersion != null && kbLogsys != null)
		{
			timer.start("getProfilesOfKB");
			profiles = session.getProfilesOfKB(kbLogsys, kbName, kbVersion);
			timer.stop();
			kbProfile = profiles[0].getKbProfile();
		}
		else
		{
			productId = kbKey.getProductCode();
			productType = SapproductconfigruntimesscConstants.PRODUCT_TYPE_MARA;
			kbName = null;
			kbVersion = null;
			kbLogsys = null;
		}

		Hashtable<String, String> context = null;
		if (contextAndPricingWrapper != null)
		{
			context = contextAndPricingWrapper.retrieveConfigurationContext(kbKey);
		}
		final boolean setRichConfigId = true;

		final String rfcConfigId = null; //let the configurator create the id
		final Integer kbIdInt = null;
		final String kbDateStr = null;
		final String kbBuild = null;
		final String useTraceStr = null;

		// Instantiate product configuration
		timer.start("createConfig");
		configId = session.createConfig(rfcConfigId, productId, productType, kbLogsys, kbName, kbVersion, kbProfile, kbIdInt,
				kbDateStr, kbBuild, useTraceStr, context, setRichConfigId);
		timer.stop();

		return configId;
	}

	protected abstract ConfigModel fillConfigModel(final String qualifiedId);

	protected int calculateStaticDomainLength(final IConfigSession session, final String configId, final String instanceId,
			final String csticName) throws IpcCommandException
	{
		int length = 0;
		final OrchestratedInstance oInstance = session.getInstanceLocal(configId, instanceId);
		final OrchestratedCstic oCstic = oInstance.getCstic(csticName);
		final String[] staticDomain = oCstic.getStaticDomain();
		if (staticDomain != null)
		{
			length = staticDomain.length;
		}
		return length;
	}

	/**
	 * @param csticModel
	 * @param containesValueSetByUser
	 */
	protected void adjustCsticAuthor(final CsticModel csticModel, final boolean containesValueSetByUser)
	{
		if (csticModel.getAuthor().equalsIgnoreCase(CsticModel.AUTHOR_USER))
		{
			if (csticModel.getAssignedValues().size() == 0)
			{
				csticModel.setAuthor(CsticModel.AUTHOR_NOAUTHOR);
			}
			else if (!containesValueSetByUser)
			{
				csticModel.setAuthor(CsticModel.AUTHOR_DEFAULT);
			}
		}
	}

	protected String retrieveQualifiedId(final String sessionId, final String configId)
	{
		return sessionId + "@" + configId;
	}

	protected String retrievePlainConfigId(final String qualifiedId)
	{
		final String plainConfigId = qualifiedId.split("@")[1];
		return plainConfigId;
	}

	protected String retrieveSessionId(final String qualifiedId)
	{
		final String sessionId = qualifiedId.split("@")[0];
		return sessionId;
	}

	protected void holdConfigSession(final String qualifiedId, final IConfigSession configSession)
	{
		sessionMap.put(qualifiedId, configSession);
	}

	protected IConfigSession retrieveConfigSession(final String qualifiedId)
	{
		final IConfigSession configSession = sessionMap.get(qualifiedId);

		if (configSession == null)
		{
			throw new IllegalStateException("Session does not exist and could not be retrieved");
		}

		return configSession;
	}

	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	@Required
	public void setContextAndPricingWrapper(final ConfigurationContextAndPricingWrapper contextAndPricingWrapper)
	{
		this.contextAndPricingWrapper = contextAndPricingWrapper;
	}

	protected void retrieveConfigurationConflicts(final String configId, final InstanceModel instance, final IConfigSession session)
			throws IpcCommandException
	{
		final String instanceId = instance.getId();

		timer.start("getConflicts");
		final IConflictContainer[] conflictContainer = session.getConflicts(configId, instanceId);
		timer.stop();

		if (conflictContainer != null && conflictContainer.length > 0)
		{
			for (final IConflictContainer conflict : conflictContainer)
			{
				final String conflictText = retriveveConflictText(conflict);
				final List<CsticModel> cstics = retriveveConflictCstics(conflict, instance);

				if (cstics.size() > 0)
				{
					for (final CsticModel cstic : cstics)
					{
						final ConflictModel conflictModel = configModelFactory.createInstanceOfConflictModel();
						conflictModel.setText(conflictText);
						addConflictToCstic(cstic, conflictModel);
					}
				}
				else
				{
					throw new IllegalStateException("'" + conflictText + "' cannot be assigned to a characteristic");
				}
			}
		}
	}


	protected List<CsticModel> retriveveConflictCstics(final IConflictContainer conflict, final InstanceModel instance)
	{
		final List<CsticModel> conflictingCsics = new ArrayList<CsticModel>();

		final IJustificationData[] justifications = conflict.getJustifications();
		for (final IJustificationData justification : justifications)
		{
			final IFactData[] facts = justification.getTriggerFacts();

			for (final IFactData fact : facts)
			{
				addConflictingCstic(instance, conflictingCsics, fact);
			}
		}
		return conflictingCsics;
	}


	protected void addConflictingCstic(final InstanceModel instance, final List<CsticModel> conflictingCsics, final IFactData fact)
	{
		final String csticName = fact.getObsName();
		final CsticModel cstic = instance.getCstic(csticName);
		// cstic may belong to a different instance, thats why have to check for null here
		// conflict will be returned again, if we read conflicts for other instances
		final boolean conflictingCtsicApplicable = cstic != null && !conflictingCsics.contains(cstic);
		if (conflictingCtsicApplicable)
		{
			conflictingCsics.add(cstic);
		}
	}


	protected String retriveveConflictText(final IConflictContainer conflict)
	{
		String text = null;
		final StringBuffer textBuffer = new StringBuffer();

		final ITextDescription[] textDescriptionArray = conflict.getConflictLongText();

		if (textDescriptionArray != null && textDescriptionArray.length > 0)
		{
			for (final ITextDescription textDescriptionLine : textDescriptionArray)
			{
				final String textLine = textDescriptionLine.getTextLine();
				textBuffer.append(textLine);
			}
		}

		if (textBuffer.length() > 0)
		{
			text = getExplanation(textBuffer.toString());
		}
		return text;
	}

	protected String getExplanation(final String text)
	{

		String explanation = null;

		if (text == null || text.isEmpty())
		{
			explanation = "";
		}
		else
		{
			int start = 0;
			if (text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION) > -1)
			{
				start = text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION)
						+ SapproductconfigruntimesscConstants.EXPLANATION.length();
			}
			int end = text.length();
			if (text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION) > -1)
			{
				end = text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION);
			}
			explanation = text.substring(start, end);
		}

		return explanation;
	}

	/**
	 * @param cstic
	 * @param conflictModel
	 */
	protected void addConflictToCstic(final CsticModel cstic, final ConflictModel conflictModel)
	{
		final String textOfNewConflict = conflictModel.getText();

		final List<ConflictModel> assignedConflicts = cstic.getConflicts();

		boolean addToCstic = true;
		for (final ConflictModel assignedConflict : assignedConflicts)
		{
			final String textOfExistingConflict = assignedConflict.getText();

			if ((textOfNewConflict == null && textOfExistingConflict == null)
					|| (textOfNewConflict != null && textOfNewConflict.equals(textOfExistingConflict)))
			{
				addToCstic = false;
				break;
			}
		}
		if (addToCstic)
		{
			cstic.addConflict(conflictModel);
		}
	}

	protected void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException
	{
		if (contextAndPricingWrapper != null)
		{
			contextAndPricingWrapper.preparePricingContext(session, configId, kbKey);
		}
	}

	protected void retrievePrice(final IConfigSession session, final ConfigModel configModel) throws InteractivePricingException
	{
		if (contextAndPricingWrapper != null)
		{
			final String configId = retrievePlainConfigId(configModel.getId());
			contextAndPricingWrapper.processPrice(session, configId, configModel);
		}
	}

	protected IConfigSession createSession(final KBKey kbKey) throws IpcCommandException
	{
		final String runtimEnv = System.getProperty(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT);
		if (runtimEnv == null)
		{
			System.getProperties().put(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT,
					SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT_HYBRIS);
		}

		final IConfigSession session = createSSCSession();

		return session;
	}

	protected IConfigSession createSSCSession() throws IpcCommandException
	{
		IConfigSession session = null;

		String sessionId = null;

		session = new ConfigSessionImpl();

		final String language = i18NService.getCurrentLocale().getLanguage().toUpperCase();
		sessionId = UUID.randomUUID().toString();
		timer.start("createSession");
		session.createSession("true", sessionId, SapproductconfigruntimesscConstants.DATA_SOURCE_CRM, false, true, language);
		timer.stop();
		session.setPricingDatamodel(DataModel.CRM);

		return session;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		ConfigModel configModel;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Init new config session for:" + extConfig.getKbKey().getProductCode());
		}

		final String configId = initializeConfigurationFromExternalSource(extConfig);
		configModel = fillConfigModel(configId);

		if (LOG.isTraceEnabled())
		{
			LOG.trace("created configID : " + configModel.getId());
		}

		return configModel;
	}

	protected String initializeConfigurationFromExternalSource(final Configuration extConfig)
	{
		IConfigSession session = null;
		String configId = null;

		try
		{
			final KBKey kbKey = extConfig.getKbKey();
			session = createSession(kbKey);

			setExternalContext(session, extConfig);

			configId = createConfigFromExternalSource(session, extConfig);

			preparePricingContext(session, configId, kbKey);
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot initialize configuration from external source", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot initialize pricing context", e);
		}

		final String sessionId = session.getSessionId();
		final String qualifiedId = retrieveQualifiedId(sessionId, configId);

		holdConfigSession(qualifiedId, session);

		return qualifiedId;
	}


	protected void setExternalContext(final IConfigSession session, final Configuration extConfig) throws IpcCommandException
	{
		final Hashtable<String, String> context = new Hashtable<String, String>();
		for (final ContextAttribute contextAttribute : extConfig.getContextAttributes())
		{
			context.put(contextAttribute.getName(), contextAttribute.getValue());
		}
		session.setContext(context);
	}

	protected String createConfigFromExternalSource(final IConfigSession session, final Configuration extConfig)
			throws IpcCommandException
	{

		final KBKey kbKey = extConfig.getKbKey();

		String kbName = kbKey.getKbName();
		String kbVersion = kbKey.getKbVersion();
		String kbLogsys = kbKey.getKbLogsys();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// retrieve kb name, version, logsys (if not provided) for desired date
		if ((kbName == null || kbName.isEmpty() || kbVersion == null || kbVersion.isEmpty() || kbLogsys == null || kbLogsys
				.isEmpty()) && !sdf.format(kbKey.getDate()).equalsIgnoreCase(sdf.format(new Date())))
		{
			final IKnowledgeBaseData[] kbData = session.findKnowledgeBases(extConfig.getRootInstance().getObjectType(),
					kbKey.getProductCode(), null, sdf.format(kbKey.getDate()), null, null, null, null, true);
			if (kbData != null)
			{
				if (kbData.length > 1)
				{
					LOG.warn("Create configuration from external Source: More than one knowledgebase was found for product "
							+ kbKey.getProductCode() + " and date " + kbKey.getDate());
				}
				kbName = kbData[0].getKbName();
				kbVersion = kbData[0].getKbVersion();
				kbLogsys = kbData[0].getKbLogsys();
			}
		}

		final String language = i18NService.getCurrentLocale().getLanguage().toUpperCase();

		// prepare config container
		final IConfigContainer configContainer = new ConfigContainer();
		final IConfigHeader configHeader = new ConfigHeader();
		configHeader.setRootId(extConfig.getRootInstance().getId());

		configHeader.setKbName(kbName);
		configHeader.setKbVersion(kbVersion);
		configHeader.setKbLanguage(language);

		configContainer.setConfigHeader(configHeader);
		configContainer.setProductId(kbKey.getProductCode());
		configContainer.setProductLogSys(kbLogsys);
		configContainer.setProductType(extConfig.getRootInstance().getObjectType());

		// parts of
		int i = 0;
		final IConfigPartOf[] partOfs = new ConfigPartOf[extConfig.getPartOfRelations().size()];
		for (final PartOfRelation extPartOf : extConfig.getPartOfRelations())
		{
			partOfs[i] = new ConfigPartOf();
			partOfs[i].setParentId(extPartOf.getParentInstId());
			partOfs[i].setInstId(extPartOf.getInstId());
			partOfs[i].setPosNr(extPartOf.getPosNr());
			partOfs[i].setObjType(extPartOf.getObjectType());
			partOfs[i].setClassType(extPartOf.getClassType());
			partOfs[i].setObjKey(extPartOf.getObjectKey());
			partOfs[i].setAuthor(extPartOf.getAuthor());
			//partOfs[i].setSalesRelevant(extPartOf.);
			i++;
		}
		configContainer.setArrConfigPartOf(partOfs);

		// instances
		i = 0;
		final InstanceData[] instances = new InstanceData[extConfig.getInstances().size()];
		for (final Instance extInstance : extConfig.getInstances())
		{
			instances[i] = new InstanceData();
			instances[i].setInstId(extInstance.getId());
			instances[i].setObjType(extInstance.getObjectType());
			instances[i].setClassType(extInstance.getClassType());
			instances[i].setObjKey(extInstance.getObjectKey());
			instances[i].setObjTxt(extInstance.getObjectText());
			instances[i].setInstAuthor(extInstance.getAuthor());
			instances[i].setSalesQty(extInstance.getQuantity());
			instances[i].setSalesQtyUnit(extInstance.getQuantityUnit());
			instances[i].setIsInstConsistent(Boolean.valueOf(extInstance.isConsistent()));
			instances[i].setIsInstComplete(Boolean.valueOf(extInstance.isComplete()));
			i++;
		}
		configContainer.setArrInstanceContainer(instances);

		// cstics / values
		final ICsticData[] cstics = prepareCsticDataArrayFromExternalSource(extConfig);
		configContainer.setArrCsticContainer(cstics);

		// recreate configuration
		timer.start("createConfig");
		final String configId = session.recreateConfig(configContainer);
		timer.stop();

		return configId;
	}


	protected ICsticData[] prepareCsticDataArrayFromExternalSource(final Configuration extConfig)
	{
		final Map<String, Map<String, List<CharacteristicValue>>> instCsticValueMap = new HashMap<String, Map<String, List<CharacteristicValue>>>();

		for (final CharacteristicValue extCsticValue : extConfig.getCharacteristicValues())
		{
			final String instId = extCsticValue.getInstId();
			final String csticName = extCsticValue.getCharacteristic();

			Map<String, List<CharacteristicValue>> csticValueMap = instCsticValueMap.get(instId);
			if (csticValueMap == null)
			{
				csticValueMap = new HashMap<String, List<CharacteristicValue>>();
				instCsticValueMap.put(instId, csticValueMap);
			}

			List<CharacteristicValue> valueList = csticValueMap.get(csticName);
			if (valueList == null)
			{
				valueList = new ArrayList<CharacteristicValue>();
				csticValueMap.put(csticName, valueList);
			}
			valueList.add(extCsticValue);
		}

		final List<ICsticData> csticList = new ArrayList<ICsticData>();

		for (final Map.Entry<String, Map<String, List<CharacteristicValue>>> entryInst : instCsticValueMap.entrySet())
		{
			final String instId = entryInst.getKey();
			final Map<String, List<CharacteristicValue>> csticValueMap = entryInst.getValue();
			for (final Map.Entry<String, List<CharacteristicValue>> entryCstic : csticValueMap.entrySet())
			{
				final String csticName = entryCstic.getKey();
				final List<CharacteristicValue> valueList = entryCstic.getValue();

				final ICsticData csticData = new CsticData();
				csticList.add(csticData);
				csticData.setInstanceId(instId);
				final ICsticHeader csticHeader = new CsticHeader();
				csticData.setCsticHeader(csticHeader);
				csticHeader.setCsticName(csticName);

				final ICsticValueData[] csticValues = new ICsticValueData[valueList.size()];
				csticData.setCsticValues(csticValues);

				int i = 0;
				for (final CharacteristicValue value : valueList)
				{
					if (i == 0)
					{
						csticHeader.setCsticLname(value.getCharacteristicText());
					}
					final ICsticValueData csticValueData = new CsticValueData();
					csticValueData.setValueName(value.getValue());
					csticValueData.setValueLname(value.getValueText());
					csticValueData.setValueAuthor(value.getAuthor());
					csticValues[i] = csticValueData;
					i++;
				}
			}
		}

		final ICsticData[] cstics = new ICsticData[csticList.size()];
		csticList.toArray(cstics);

		return cstics;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		ConfigModel configModel;

		try
		{
			final IConfigSession session = createSession(kbKey);

			if (contextAndPricingWrapper != null)
			{
				final Hashtable<String, String> context = contextAndPricingWrapper.retrieveConfigurationContext(kbKey);
				session.setContext(context);
			}

			final String configId = session.recreateConfigFromXml(extConfig);
			preparePricingContext(session, configId, kbKey);

			final String qualifiedId = retrieveQualifiedId(session.getSessionId(), configId);

			holdConfigSession(qualifiedId, session);
			configModel = fillConfigModel(qualifiedId);

			return configModel;
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Could not create configuration from external representation", e);
		}
		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot initialize pricing context", e);
		}
	}

	@Override
	public void releaseSession(final String sessionId)
	{
		sessionMap.remove(sessionId);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("SSC Configuration session released, sessionID: " + sessionId);
		}
	}

	Map<String, IConfigSession> getSessionMap()
	{
		return sessionMap;
	}

	protected ConfigurationContextAndPricingWrapper getContextAndPricingWrapper()
	{
		return contextAndPricingWrapper;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}

	protected TextConverter getTextConverter()
	{
		return textConverter;
	}

	@Required
	public void setTextConverter(final TextConverter textConverter)
	{
		this.textConverter = textConverter;
	}

	protected SSCTimer getTimer()
	{
		return timer;
	}

}
