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
package de.hybris.platform.mobileservices.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.mobileservices.enums.EnginesType;
import de.hybris.platform.mobileservices.enums.MessageType;
import de.hybris.platform.mobileservices.enums.MobileActionAssignmentStateType;
import de.hybris.platform.mobileservices.enums.MobileKeywordType;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileActionAssignmentModel;
import de.hybris.platform.mobileservices.model.text.MobileActionKeywordModel;
import de.hybris.platform.mobileservices.model.text.MobileAggregatorModel;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.model.text.MobileReceiveAndSendTextActionModel;
import de.hybris.platform.mobileservices.model.text.MobileShortcodeModel;
import de.hybris.platform.mobileservices.text.processing.IncomingMessageRoutingStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class KeywordMatchingTest extends ServicelayerTest
{
	@Resource
	I18NService i18nService;

	@Resource
	ModelService modelService;

	@Resource
	IncomingMessageRoutingStrategy incomingMessageRoutingStrategy;

	CountryModel cDE;

	MobileAggregatorModel aggregator;
	MobileShortcodeModel shortcode;
	MobileReceiveAndSendTextActionModel action;
	MobileActionAssignmentModel assignment;

	MobileActionKeywordModel keyword_plain;
	MobileActionKeywordModel keyword_regexp;
	MobileActionKeywordModel keyword_regexp_multiword;

	int msgCounter = 0;

	@Before
	public void setUp()
	{
		try
		{
			cDE = i18nService.getCountry("DE");
		}
		catch (final UnknownIdentifierException e)
		{
			cDE = new CountryModel();
			cDE.setIsocode("DE");
			modelService.save(cDE);
		}
		assertNotNull(cDE);

		this.aggregator = modelService.create(MobileAggregatorModel.class);
		aggregator.setCode("testAggregator");
		aggregator.setEngine(EnginesType.TESTSENDSMSENGINE);

		this.shortcode = modelService.create(MobileShortcodeModel.class);
		shortcode.setAggregator(aggregator);
		shortcode.setCode("5555");
		shortcode.setCountry(cDE);

		this.action = modelService.create(MobileReceiveAndSendTextActionModel.class);
		action.setCode("testSendTextAction");
		action.setContent("foo bar");

		// matches HELLO, hello etc.
		this.keyword_plain = modelService.create(MobileActionKeywordModel.class);
		keyword_plain.setKeyword("HELLO");

		// matches MOBILE-<number>
		this.keyword_regexp = modelService.create(MobileActionKeywordModel.class);
		keyword_regexp.setKeyword("MOBILE-\\d+");
		keyword_regexp.setType(MobileKeywordType.REGEX);

		this.keyword_regexp_multiword = modelService.create(MobileActionKeywordModel.class);
		keyword_regexp_multiword.setKeyword("HI (THERE|AXEL) \\d+");
		keyword_regexp_multiword.setType(MobileKeywordType.REGEX);

		this.assignment = modelService.create(MobileActionAssignmentModel.class);
		assignment.setShortcode(shortcode);
		assignment.setAction(action);
		assignment.setKeyword(keyword_plain);
		assignment.setState(MobileActionAssignmentStateType.ACTIVE);

		modelService.saveAll();
	}

	@Test
	public void testKeywordMatching()
	{
		// after setUp() keyword_plain is assigned !!!

		MobileMessageContextModel msg = createIncomingMessage("HELLO Axel How are you?", "491771234567");
		assertEquals(this.assignment, incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		msg = createIncomingMessage("Hi Axel How are you?", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// switch to keyword_regexp -> matches MOBILE-<number>
		assignment.setKeyword(keyword_regexp);
		modelService.save(assignment);

		// test success
		msg = createIncomingMessage("MOBILE-123456 foo bar shall be ignored", "491771234567");
		assertEquals(this.assignment, incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test failure
		msg = createIncomingMessage("MOBILE 123456 foo bar shall be ignored", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test failure
		msg = createIncomingMessage("MOBILE-123456x foo bar shall be ignored", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// switch to keyword_regexp_multiword -> matches HI (THERE|AXEL) <number>
		assignment.setKeyword(keyword_regexp_multiword);
		modelService.save(assignment);

		// test success
		msg = createIncomingMessage("HI there 12345 foo bar", "491771234567");
		assertEquals(this.assignment, incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test success
		msg = createIncomingMessage("HI Axel 12345", "491771234567");
		assertEquals(this.assignment, incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test failure
		msg = createIncomingMessage("HI HO", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test failure
		msg = createIncomingMessage("HI foo 12345", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));

		// test failure
		msg = createIncomingMessage("HI THERE 12345x", "491771234567");
		assertNull(incomingMessageRoutingStrategy.matchAction(msg, shortcode));
	}

	protected MobileMessageContextModel createIncomingMessage(final String text, final String number)
	{
		final MobileMessageContextModel msg = modelService.create(MobileMessageContextModel.class);
		msg.setIncomingMessageId(Integer.toString(msgCounter++));
		msg.setStatus(MobileMessageStatus.RECEIVED);
		msg.setType(MessageType.INCOMING);
		msg.setIncomingText(text);
		msg.setPhoneNumber(number);
		msg.setShortcode(shortcode.getCode());
		msg.setCountryIsoCode(shortcode.getCountry().getIsocode());

		return msg;
	}

}
