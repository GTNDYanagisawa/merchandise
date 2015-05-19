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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRulesContainerImpl.Key;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRulesContainerImpl.PatternDegreeDescComparator;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingRulesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sap.tc.logging.Severity;


/**
 * 
 */
public class MessageMappingRulesLoaderImpl implements MessageMappingRulesLoader
{

	/** Registration id for RFC messages */
	protected static final String RFC_MESSAGES = "rfc_messages";


	private final static Log4JWrapper sapLogger = Log4JWrapper.getInstance(MessageMappingRulesLoaderImpl.class.getName());
	private boolean hideNonErrorMsg;


	private InputStream messageMappingFileInputStream;

	@Override
	public Map<Key, List<MessageMappingRule>> loadRules() throws SAXException, IOException, BackendRuntimeException
	{
		sapLogger.entering("loadRules(InputStream is)");

		if (messageMappingFileInputStream == null)
		{
			messageMappingFileInputStream = loadMessageMappingRulesContainerFile();
		}

		Map<Key, List<MessageMappingRule>> rules;

		final XMLReader parser = XMLReaderFactory.createXMLReader();

		final MessageMappingRulesParserImpl msgHandler = getMessageMappingRulesParser();
		parser.setContentHandler(msgHandler);
		//parser.setProperty("http://xml.org/sax/features/validation", new Boolean(true));
		parser.parse(new InputSource(messageMappingFileInputStream));

		{
			sapLogger.log(Severity.DEBUG, LogCategories.APPS_BUSINESS_LOGIC,
					MessageFormat.format("Message Mapping Rules: {0}", new Object[]
					{ msgHandler }));
		}
		final StringBuilder errorsCollector = new StringBuilder();

		hideNonErrorMsg = msgHandler.hideNonErrorMsg;

		final MessageMappingRule[] tmpRules = msgHandler.rulesList.toArray(new MessageMappingRule[msgHandler.rulesList.size()]);

		/*
		 * Arrange rules by attribute degree, so they are added into list by degree. Most specific will be tested first.
		 * <p> Stable sort.
		 */
		Arrays.sort(tmpRules, new PatternDegreeDescComparator());

		rules = new HashMap<Key, List<MessageMappingRule>>(tmpRules.length);
		for (final MessageMappingRule rule : tmpRules)
		{
			final MessageMappingRule.Pattern pattern = rule.getPattern();

			final Key keyL1 = new Key(pattern.getBeClass(), pattern.getBeNumber(), pattern.getBeSeverity());
			List<MessageMappingRule> rulesList = rules.get(keyL1);
			if (rulesList == null)
			{
				rulesList = new ArrayList<MessageMappingRule>(1);
				rules.put(keyL1, rulesList);
			}

			// check for duplicate patterns
			for (final MessageMappingRule dublicated : rulesList)
			{
				if (rule.getPattern().equals(dublicated.getPattern()))
				{
					final String MSG_PATTERN = "Rule {0} has the same pattern as rule {1}, it will be ignored";
					final Object[] args = new Object[]
					{ rule, dublicated };

					sapLogger.trace(LogSeverity.WARNING, MSG_PATTERN, args);

					final String errMsg = MessageFormat.format(MSG_PATTERN, args);
					errorsCollector.append(errMsg).append('\n');
				}
			}

			rulesList.add(rule);
		}

		if (errorsCollector.length() > 0)
		{
			final BackendRuntimeException ex = new BackendRuntimeException(errorsCollector.toString());
			sapLogger.throwing(ex);
			throw ex;
		}

		sapLogger.exiting();
		return rules;
	}


	/**
	 * @return Parser
	 */
	protected MessageMappingRulesParserImpl getMessageMappingRulesParser()
	{
		return new MessageMappingRulesParserImpl();
	}


	/**
	 * Load messages.xml file as input stream
	 * 
	 * @return Rules in as messages.xml
	 * @throws BackendRuntimeException
	 */
	protected InputStream loadMessageMappingRulesContainerFile() throws BackendRuntimeException
	{
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream("sapmessagemapping/messages.xml");
		if (is != null)
		{
			return is;
		}
		else
		{
			throw new BackendRuntimeException("File \"" + RFC_MESSAGES + "\" can not be opened");
		}
	}


	/**
	 * @param messageMappingFileInputStream
	 */
	protected void setMessageMappingFileInputStream(final InputStream messageMappingFileInputStream)
	{
		this.messageMappingFileInputStream = messageMappingFileInputStream;
	}


	@Override
	public boolean isHideNonErrorMsg()
	{
		return hideNonErrorMsg;
	}






}
