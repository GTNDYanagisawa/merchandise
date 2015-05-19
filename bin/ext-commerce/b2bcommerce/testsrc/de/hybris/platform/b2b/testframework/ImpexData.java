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
package de.hybris.platform.b2b.testframework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Ignore;


/**
 * Test method annotation listing <i>ImpEx</i> scripts used to load test data required for the test. Annotate methods
 * before which impex data needs to be loaded.
 * 
 * @ImpexData("/impex/essentialdata_localcontracts.impex")
 */
@Ignore
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ImpexData
{
	/**
	 * Relaxed mode.
	 * 
	 * @return true, if impex are loaded in relaxed mode
	 */
	boolean relaxedMode() default true;

	/**
	 * List of <i>ImpEx</i> scripts provided as class loader resources.
	 * 
	 * @return the string[]
	 */
	String[] value();
}
