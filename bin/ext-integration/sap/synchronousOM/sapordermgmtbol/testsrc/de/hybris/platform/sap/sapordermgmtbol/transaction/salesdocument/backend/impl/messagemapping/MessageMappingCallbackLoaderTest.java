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

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Map;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class MessageMappingCallbackLoaderTest extends SapordermanagmentBolSpringJunitTest
{

	public MessageMappingCallbackLoader classUnderTest;


	@Test
	public void test()
	{

		classUnderTest = genericFactory.getBean("sapOrdermgmtMessageMappingCallbackLoader");

		final Map<String, MessageMappingCallbackProcessor> callbackMap = classUnderTest.loadCallbacks();

		assertTrue(!callbackMap.isEmpty());

		callbackMap.get(TestImplementationMessageMappingCallback.TEST_MESSAGE_MAPPING_CALLBACK_ID).process(null);


	}



}
