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
package de.hybris.platform.oci.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import de.hybris.platform.oci.jalo.exception.OciException;
import de.hybris.platform.oci.jalo.utils.OutboundSection;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.HashMap;

import org.junit.Test;


/**
 * testing all public methods of oci class Outboundsection
 * 
 * 
 * 
 */
public class OutboundsectionTest extends HybrisJUnit4TransactionalTest
{

	//----------------- nun die tests ------------------------------------------------
	@Test
	public void testOutboundSection() throws Exception
	{
		final String hookurltest = "http://localhost:9001/sap-auswertung.jsp";
		final String hookurlfieldnametest = "hOoK_UrL";

		final HashMap parametermaptest = new HashMap();
		parametermaptest.put(hookurlfieldnametest, hookurltest);
		parametermaptest.put("klein1", "kleiner text");

		final OutboundSection obs = new OutboundSection(parametermaptest, hookurlfieldnametest);

		//test field constants
		assertEquals(OutboundSection.Fields.CALLER, "~Caller");
		assertEquals(OutboundSection.Fields.OK_CODE, "~OkCode");
		assertEquals(OutboundSection.Fields.TARGET, "~Target");
		//end - test field constants

		//test hook_url feld name
		assertEquals(obs.getHookURLFieldName(), "HOOK_URL"); // all keys are in upper case
		//end - test hook_url feld name

		//test parametermap, methode getField
		assertNull(obs.getField("a")); //Feld existiert nicht 
		assertEquals(obs.getField("klein1"), "kleiner text"); //feld existiert
		assertEquals(obs.getField("KLEIN1"), "kleiner text"); //key gross/kleinschreibung ist egal, da alles gross, zahlen bleiben so
		assertNull(obs.getField(null)); //return null wenn key null ist
		assertEquals(obs.getField("HOOK_URL"), hookurltest); //mal ein echter wert, siehe constructor aufruf
		//ende - test parametermap, methode getField

		//Exceptiontest
		try
		{
			new OutboundSection(parametermaptest, " ");
			fail();
		}
		catch (final OciException e)
		{
			assertEquals(e.getErrorCode(), OciException.NO_HOOK_URL);
		}

		try
		{
			new OutboundSection(parametermaptest, null);
			fail();
		}
		catch (final OciException e)
		{
			assertEquals(e.getErrorCode(), OciException.NO_HOOK_URL);
		}
		//end - exception tests		
	}
}
