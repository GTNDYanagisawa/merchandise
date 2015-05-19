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



/**
 * This factory creates new instances of the {@link RepositoryRecording} implementations.
 */
public interface RepositoryRecordingFactory
{


	/**
	 * The actual factory method.
	 * 
	 * @return Returns a new instance of the {@link RepositoryRecording} implementation.
	 */
	public RepositoryRecording createRepositoryRecording();
}
