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
package com.hybris.cockpitng.editor.onpremise;

import com.hybris.cockpitng.editor.onpremise.DefaultPKEditor;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;


public class DefaultPKEditorTest extends AbstractCockpitEditorRendererUnitTest<Object, DefaultPKEditor>
{
	private final DefaultPKEditor editor = new DefaultPKEditor();

	@Override
	public DefaultPKEditor getEditorInstance()
	{
		return editor;
	}
}
