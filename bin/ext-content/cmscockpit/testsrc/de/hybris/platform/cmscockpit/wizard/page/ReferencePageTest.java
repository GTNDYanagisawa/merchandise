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
package de.hybris.platform.cmscockpit.wizard.page;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmscockpit.services.config.ContentElementConfiguration;
import de.hybris.platform.cmscockpit.services.config.impl.DefaultContentElementConfiguration;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.wizards.Wizard;
import de.hybris.platform.core.model.media.MediaModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * This class will test the implementation written for ReferencePage.java
 */
@UnitTest
public class ReferencePageTest
{

	private static final String URL = "imageURL";
	private static final String DESCRIPTION = "description";
	private static final String TEST_NAME = "testConfig";
	private static final String URL_CONFIG = "configurl";
	@Mock
	private static TypedObject object;
	@Mock
	private ReferencePage referencepage;
	@Mock
	private Wizard testwizard;
	@Mock
	private ObjectType testObject;
	@Mock
	private DefaultContentElementConfiguration configuartionfromobjecttype;
	@Mock
	private PageTemplateModel pagetemplate;
	@Mock
	private AbstractPageModel page;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		referencepage = new ReferencePage("myTest", testwizard);

		final MediaModel mediaModel = mock(MediaModel.class);
		pagetemplate = mock(PageTemplateModel.class);
		page = mock(AbstractPageModel.class);


		when(pagetemplate.getPreviewIcon()).thenReturn(mediaModel);

		when(mediaModel.getURL()).thenReturn(URL);

		when(configuartionfromobjecttype.getDescription()).thenReturn(DESCRIPTION);
		when(configuartionfromobjecttype.getName()).thenReturn(TEST_NAME);
		when(configuartionfromobjecttype.getType()).thenReturn(testObject);
		when(configuartionfromobjecttype.getImage()).thenReturn(URL_CONFIG);
	}

	/**
	 * This Test case will check the element configuration for PageTemplate typed object
	 * 
	 * @return returnConfig with previewIcon image for PageTemplate
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void testElementConfigurationForPageTemplateTypedObject() throws Exception
	{

		when(object.getObject()).thenReturn(pagetemplate);

		final ContentElementConfiguration returnConfig = referencepage.getElementConfigurationForTypedObject(object,
				configuartionfromobjecttype);

		Assert.assertNotNull(configuartionfromobjecttype.getImage());
		Assert.assertNotNull(configuartionfromobjecttype);
		Assert.assertEquals(returnConfig.getImage(), pagetemplate.getPreviewIcon().getURL());
		Assert.assertEquals(returnConfig.getName(), configuartionfromobjecttype.getName());
		Assert.assertEquals(returnConfig.getDescription(), configuartionfromobjecttype.getDescription());

	}

	/**
	 * This Test case will check the element configuration for any Page typed object
	 * 
	 * @return returnConfig with configurationFromoOjectType with default value for icon image
	 * 
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void testElementConfigurationForTypedObject() throws Exception
	{

		when(object.getObject()).thenReturn(page);

		final ContentElementConfiguration returnConfig = referencepage.getElementConfigurationForTypedObject(object,
				configuartionfromobjecttype);

		Assert.assertNotNull(configuartionfromobjecttype.getImage());
		Assert.assertNotNull(configuartionfromobjecttype);
		Assert.assertEquals(returnConfig.getImage(), configuartionfromobjecttype.getImage());


	}

	/**
	 * This Test case will check the element configuration when configurationFromoOjectType is kept null
	 * 
	 * @return returnConfig
	 * 
	 */
	@SuppressWarnings("javadoc")
	@Test(expected = IllegalArgumentException.class)
	public void testElementConfigurationForTypedObjectWithNullConfiguration() throws Exception
	{

		when(object.getObject()).thenReturn(pagetemplate);

		referencepage.getElementConfigurationForTypedObject(object, null);

	}

	/**
	 * This Test case will check the element configuration when Typed object is kept null
	 * 
	 * @return returnConfig with null value
	 * 
	 */
	@SuppressWarnings("javadoc")
	@Test(expected = IllegalArgumentException.class)
	public void testElementConfigurationForTypedObjectWithNull() throws Exception
	{

		referencepage.getElementConfigurationForTypedObject(null, configuartionfromobjecttype);

	}
}
