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
package de.hybris.platform.cms2.systemsetup;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CMS2SystemSetupTest
{
	@InjectMocks
	private CMS2SystemSetup cms2SystemSetup;
	@Mock
	private PersistentKeyGenerator keyGen;

	@Before
	public void setUp() throws Exception
	{
		cms2SystemSetup = new CMS2SystemSetup();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.systemsetup.CMS2SystemSetup#createNumberSeriesForTypes()}.
	 */
	@Test
	public void shouldCreateNumberSeriesForCms2Models() // NOPMD Assert not needed when using verify
	{
		// nothing given

		// when
		cms2SystemSetup.createNumberSeriesForTypes();

		// then
		verify(keyGen, times(1)).setKey(CMSItemModel._TYPECODE);
		verify(keyGen, times(1)).setKey(AbstractPageModel._TYPECODE);
		verify(keyGen, times(1)).setKey(ContentSlotModel._TYPECODE);
		verify(keyGen, times(1)).setKey(AbstractCMSComponentModel._TYPECODE);
		verify(keyGen, times(1)).setKey(ContentSlotForTemplateModel._TYPECODE);
		verify(keyGen, times(1)).setKey(ContentSlotForPageModel._TYPECODE);
		verify(keyGen, times(1)).setKey(CMSSiteModel._TYPECODE);
		verify(keyGen, times(1)).setKey(ContentCatalogModel._TYPECODE);
		verify(keyGen, times(1)).setKey(AbstractRestrictionModel._TYPECODE);
		verify(keyGen, times(1)).setKey(CMSNavigationNodeModel._TYPECODE);
		//verify(keyGen, times(10)).init();
	}
}
