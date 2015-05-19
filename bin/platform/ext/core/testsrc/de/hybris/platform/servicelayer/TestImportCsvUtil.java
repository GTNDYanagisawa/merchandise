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
package de.hybris.platform.servicelayer;




/**
 * The interface used to lookup bean used to run import during tests. The purpose of providing this interface is to
 * enable using impex in service layer tests. Default implementation is
 * {@link de.hybris.platform.servicelayer.impex.DefaultTestImportCsvUtil} and it is configured in
 * servicelayer-spring.xml
 * <p>
 * The usage is to lookup default implementation using Registry.getApplicationContext().getBean("testImportCsvUtil");<br>
 * See {@link de.hybris.platform.servicelayer.type.daos.impl.DefaultTypeDaoTest}
 */
public interface TestImportCsvUtil
{
	void importCsv(final String csvFile, final String encoding);
}
