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
package de.hybris.platform.btg.condition.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.btg.condition.operand.valueproviders.MediaScriptOperandValueProvider;
import de.hybris.platform.btg.condition.operand.valueproviders.ScriptOperandException;
import de.hybris.platform.btg.condition.operand.valueproviders.StringScriptOperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGMediaScriptOperandModel;
import de.hybris.platform.btg.model.BTGStringScriptOperandModel;
import de.hybris.platform.btg.model.ScriptMediaModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;


/**
 *
 */
public class ScriptOperandServiceTest extends BTGIntegrationTest
{
	@Resource
	private MediaScriptOperandValueProvider mediaScriptOperandValueProvider;
	@Resource
	private StringScriptOperandValueProvider stringScriptOperandValueProvider;
	@Resource
	private MediaService mediaService;


	@Test
	public void testScript() throws ScriptException
	{
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByName("groovy");

		if (engine == null)
		{
			throw new ScriptOperandException("Cannot find script engine for groovy");
		}
		final InputStream stream = ServicelayerTest.class.getResourceAsStream("/test/test.groovy");
		final InputStreamReader reader = new InputStreamReader(stream);

		engine.put("applicationContext", Registry.getApplicationContext());
		engine.put("user", userService.getUser(USER_A));
		final Object result = engine.eval(reader);
		assertEquals(result, USER_A);
	}


	@Test
	public void testBTGMediaScriptOperandModelGetValue() throws FileNotFoundException
	{

		final ScriptMediaModel media = new ScriptMediaModel();
		media.setScriptLanguage("groovy");
		media.setCode("testScript");
		media.setCatalogVersion(online);

		final InputStream stream = ServicelayerTest.class.getResourceAsStream("/test/test.groovy");
		modelService.save(media);
		mediaService.setDataStreamForMedia(media, new DataInputStream(stream));
		modelService.save(media);

		final BTGMediaScriptOperandModel operand = new BTGMediaScriptOperandModel();
		operand.setScriptMedia(media);
		operand.setCollection(false);
		operand.setReturnType(String.class);
		operand.setUid("scriptOperand");
		operand.setCatalogVersion(online);
		modelService.save(operand);
		assertEquals(
				mediaScriptOperandValueProvider.getValue(operand, userService.getUser(USER_A), BTGConditionEvaluationScope.ONLINE),
				USER_A);
	}


	@Test
	public void testBTGStringScriptOperandModelGetValue() throws FileNotFoundException
	{
		final BTGStringScriptOperandModel operand = new BTGStringScriptOperandModel();
		operand.setScript("return user.getUid()");
		operand.setCollection(false);
		operand.setScriptLanguage("groovy");
		operand.setReturnType(String.class);
		operand.setUid("scriptOperand");
		operand.setCatalogVersion(online);
		modelService.save(operand);
		assertEquals(
				stringScriptOperandValueProvider.getValue(operand, userService.getUser(USER_A), BTGConditionEvaluationScope.ONLINE),
				USER_A);
	}

}
