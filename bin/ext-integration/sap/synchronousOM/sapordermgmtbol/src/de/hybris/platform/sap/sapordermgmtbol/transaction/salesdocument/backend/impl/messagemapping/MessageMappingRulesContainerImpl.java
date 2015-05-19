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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.TransactionConfigurationBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingRulesContainer;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingRulesLoader;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.xml.sax.SAXException;

import com.sap.tc.logging.Severity;


/**
 * Contains all rules for message mapping. Refer to messages.xml
 */
public class MessageMappingRulesContainerImpl implements MessageMappingRulesContainer
{


	/**
	 * Allows to access configuration settings
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess;
	/**
	 * Loads call backs (change of messages on application side)
	 */
	protected MessageMappingCallbackLoader messageMappingCallbackLoader;

	/**
	 * Key Level 1. It is used to match message class, number and severity into a hashMap.
	 */
	static public class Key
	{
		/**
		 * Message ID
		 */
		protected final String id;
		/**
		 * Message number
		 */
		protected final String number;
		/**
		 * Severity
		 */
		protected final String severity;

		/**
		 * @param id
		 * @param num
		 * @param sev
		 */
		public Key(final String id, final String num, final String sev)
		{
			this.id = id;
			this.number = num;
			this.severity = sev;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}

			if (!(obj instanceof Key))
			{
				return false;
			}

			final Key o = (Key) obj;
			return MessageMappingRule.equalsField(id, o.id) //
					&& MessageMappingRule.equalsField(number, o.number) //
					&& MessageMappingRule.equalsField(severity, o.severity);
		}

		@Override
		public int hashCode()
		{
			return 1 + MessageMappingRule.hashCodeField(id) * 0x1F + MessageMappingRule.hashCodeField(number) + 0x7
					* MessageMappingRule.hashCodeField(severity);
		}

		@Override
		public String toString()
		{
			return MessageFormat.format("key {0}/{1}/{2} ", new Object[]
			{ id, number, severity });
		}

	}

	/**
	 * Compares degrees of search patterns
	 */
	public static class PatternDegreeDescComparator implements Comparator<MessageMappingRule>
	{
		@Override
		public int compare(final MessageMappingRule a1, final MessageMappingRule a2)
		{
			// (minus) reverse order
			return -(a1.getPattern().attrDergee() - a2.getPattern().attrDergee());
		}
	}


	/**
	 * Cache access for storing the rules which were passed from messages.xml
	 */
	@Resource(name = SapordermgmtbolConstants.BEAN_ID_CACHE_MESSAGE_MAPPING)
	protected CacheAccess messageMappingCacheAccess;

	/**
	 * Access to SAP session beans
	 */
	protected GenericFactory genericFactory;

	private static final String CACHEKEY_MESSAGE_MAPPING_RULES = "MESSAGE_MAPPING_RULES";



	private static Log4JWrapper sapLogger = Log4JWrapper.getInstance(MessageMappingRulesContainerImpl.class.getName());

	/**
	 * Do we hide warning and info messages?
	 */
	protected boolean hideNonErrorMsg = false;

	/**
	 * Map of rules
	 */
	// null indicated uninitialized
	protected Map<Key, List<MessageMappingRule>> rules = null;
	/**
	 * Map of callbacks
	 */
	protected Map<String, MessageMappingCallbackProcessor> callbacks = null;

	/**
	 * Allows to load message mapping rules
	 */
	protected MessageMappingRulesLoader messageMappingRulesLoader = null;


	/**
	 * Initialization of container
	 */
	public void init()
	{
		getMessageMappingRulesLoader();
		initMessageMappingRulesContainer();

		callbacks = messageMappingCallbackLoader.loadCallbacks();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void initMessageMappingRulesContainer()
	{

		// the config name identifies uniquely the application
		final String rfcMsgRulesCacheKey = CACHEKEY_MESSAGE_MAPPING_RULES + moduleConfigurationAccess.getSAPConfigurationName();

		// sync access to cache
		synchronized (TransactionConfigurationBase.class)
		{

			final Object obj = messageMappingCacheAccess.get(rfcMsgRulesCacheKey);



			if (obj != null)
			{
				rules = (Map<Key, List<MessageMappingRule>>) obj;
			}
			else
			{
				try
				{
					rules = messageMappingRulesLoader.loadRules();
					hideNonErrorMsg = messageMappingRulesLoader.isHideNonErrorMsg();
				}
				catch (BackendRuntimeException | SAXException | IOException e1)
				{
					throw new BackendRuntimeException("Cannot load message mapping rules from file.");
				}


				try
				{
					messageMappingCacheAccess.put(rfcMsgRulesCacheKey, rules);
				}
				catch (final SAPHybrisCacheException e)
				{
					throw new BackendRuntimeException("Issue during cache access.");
				}

			}
		}

	}

	/**
	 * @return Message mapping rules loader
	 */
	protected MessageMappingRulesLoader getMessageMappingRulesLoader()
	{
		if (messageMappingRulesLoader == null)
		{
			messageMappingRulesLoader = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_LOADER);
		}
		return messageMappingRulesLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.
	 * MessgeMappingRulesContainer #isHideNonErrorMsg()
	 */
	@Override
	public boolean isHideNonErrorMsg()
	{
		return hideNonErrorMsg;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.
	 * MessgeMappingRulesContainer #
	 * mostNarrow(de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl
	 * .messagemapping.MessageMappingRule .Pattern)
	 */
	@Override
	public MessageMappingRule mostNarrow(final MessageMappingRule.Pattern beMes)
	{
		sapLogger.entering("mostNarrow(String beClass, String beNumber, String beSeverity)");

		if (rules == null)
		{
			final IllegalStateException ex = new IllegalStateException("Not loaded");
			sapLogger.throwing(ex);
			throw ex;
		}

		// find solution starting from the most specific rule
		final Key patternSequence[] =
		{
				// specific
				new Key(beMes.getBeClass(), beMes.getBeNumber(), beMes.getBeSeverity()),
				// any severity
				new Key(beMes.getBeClass(), beMes.getBeNumber(), null),
				// any number or severity
				new Key(beMes.getBeClass(), null, null) };

		for (final Key pattern : patternSequence)
		{
			final List<MessageMappingRule> rulesList = rules.get(pattern);
			if (rulesList != null)
			{
				for (final MessageMappingRule rule : rulesList)
				{
					if (rule.getPattern().match(beMes.getBeClass(), beMes.getBeNumber(), beMes.getBeSeverity(), beMes.getBeV1(),
							beMes.getBeV2(), beMes.getBeV3(), beMes.getBeV4()))
					{

						if (sapLogger.isDebugEnabled())
						{
							sapLogger.debug("found rule " + rule + " for " + beMes.getBeClass() + " , " + beMes.getBeNumber() + " , "
									+ beMes.getBeSeverity());
						}
						sapLogger.exiting();
						return rule;
					}
				}
				return null;
			} // else continue
		}

		sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS,
				"Found no message mapping rule for backend message: " + beMes.getBeClass() + " , " + beMes.getBeNumber() + " , "
						+ beMes.getBeSeverity() + ", " + beMes.getBeV1() + ", " + beMes.getBeV2() + ", " + beMes.getBeV3());

		sapLogger.exiting();
		return null;
	}


	/**
	 * @param messageMappingRulesLoader
	 */
	public void setMessageMappingRulesLoader(final MessageMappingRulesLoader messageMappingRulesLoader)
	{
		this.messageMappingRulesLoader = messageMappingRulesLoader;
	}

	/**
	 * @param genericFactory
	 *           Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * @param moduleConfigurationAccess
	 */
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * @param messageMappingCallbackLoader
	 *           the messageMappingCallbackLoader to set
	 */
	public void setMessageMappingCallbackLoader(final MessageMappingCallbackLoader messageMappingCallbackLoader)
	{
		this.messageMappingCallbackLoader = messageMappingCallbackLoader;
	}

	/**
	 * @return the callbacks
	 */
	@Override
	public Map<String, MessageMappingCallbackProcessor> getCallbacks()
	{
		return callbacks;
	}





}
