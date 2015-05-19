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
package de.hybris.platform.sap.core.jco.rec;

import java.io.File;


/**
 * This interface helps the {@link RepositoryPlaybackFactory} decide which {@link RepositoryPlayback} implementation
 * should get instantiated.
 */
public interface VersionReader
{

	/**
	 * Searches the given file for a RepositoryVersion tag-name.
	 * 
	 * @param file
	 *           the content of this file will be examined.
	 * @return Returns the version number contained in the file or {@code null} if no version is found.
	 */
	public String getVersion(File file);
}
