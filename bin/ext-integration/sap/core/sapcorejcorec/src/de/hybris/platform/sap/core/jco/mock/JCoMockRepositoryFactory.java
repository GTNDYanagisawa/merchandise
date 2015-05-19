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
/**
 * 
 */
package de.hybris.platform.sap.core.jco.mock;

import java.io.File;


/**
 * Factory providing parsing of JCo mock data files.
 */
public interface JCoMockRepositoryFactory
{
	/**
	 * Returns a mocked JCo Repository filled with data from a xml file.
	 * 
	 * @param file
	 *           file with mocking data
	 * @return mocked JCo Repository.
	 */
	public JCoMockRepository getMockRepository(File file);
}
