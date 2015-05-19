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

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for marking tests inherited from {@link SapcoreJCoRecJUnitTest} for recording.
 */
@Target(
{ java.lang.annotation.ElementType.TYPE })
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface JCoRecording
{
	/**
	 * Recording mode.
	 */
	JCoRecMode mode();

	/**
	 * Defines extension for storing or reading test data xml files. <br/>
	 * In case of mode is recording or playback the value must be set.
	 * */
	String recordingExtensionName();

	/**
	 * Relative recording directory within extension directory (optional). <br>
	 * Must not start with a slash and must end with a slash.
	 * 
	 */
	String relativeRecordingDirectory() default "";
}
