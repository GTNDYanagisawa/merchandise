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
package de.hybris.platform.btgcockpit.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.btg.model.BTGAssignToGroupDefinitionModel;
import de.hybris.platform.btg.model.BTGOutputActionDefinitionModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.model.CmsRestrictionActionDefinitionModel;
import de.hybris.platform.btg.model.restrictions.BtgSegmentRestrictionModel;
import de.hybris.platform.btgcockpit.service.context.RestrictionActionCreateContext;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.jalo.Cms2Manager;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;


/**
 * @author karol.walczak
 */
public class BTGCockpitServiceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(BTGCockpitServiceTest.class);
	private ApplicationContext applicationContex;

	@Resource
	private ModelService modelService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private CMSComponentService cmsComponentService;
	@Resource
	private CMSPageService cmsPageService;
	@Resource
	private CMSAdminPageService cmsAdminPageService;
	@Resource
	private CMSAdminSiteService cmsAdminSiteService;
	@Resource
	private PersistentKeyGenerator processCodeGenerator;


	private TypeService cockpitTypeService;
	private BTGCockpitService btgCockpitService;


	private AbstractCMSComponentModel componentModel;
	private AbstractCMSComponentModel altComponentModel;
	private ContentPageModel pageModel;
	private BTGSegmentModel segmentModel;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating btg test data ..");
		initApplicationContext();
		cockpitTypeService = (TypeService) applicationContex.getBean("cockpitTypeService");

		createNumberSeries(BTGSegmentModel._TYPECODE);
		createNumberSeries(BTGRuleModel._TYPECODE);
		createNumberSeries(BTGAssignToGroupDefinitionModel._TYPECODE);
		createNumberSeries(BTGOutputActionDefinitionModel._TYPECODE);
		createNumberSeries(AbstractRestrictionModel._TYPECODE);
		createNumberSeries(AbstractPageModel._TYPECODE);

		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		Cms2Manager.getInstance().createEssentialData(Collections.EMPTY_MAP, null);

		importCsv("/test/btgCockpitTestData.csv", "utf-8");
		LOG.info("Finished creating btg test data " + (System.currentTimeMillis() - startTime) + "ms");

		btgCockpitService = (BTGCockpitService) applicationContex.getBean("btgCockpitService");

		final CatalogVersionModel catalogVersionModel = getSampleCatalogVersion();
		segmentModel = new BTGSegmentModel();
		segmentModel.setCatalogVersion(catalogVersionModel);
		segmentModel.setName("BTG Sample Segment");
		segmentModel.setUid(RandomStringUtils.randomAlphanumeric(10));
		modelService.save(segmentModel);
		cmsAdminSiteService.setActiveCatalogVersion(catalogVersionModel);
		componentModel = cmsComponentService.getAbstractCMSComponent("test_component1",
				Collections.singletonList(catalogVersionModel));
		altComponentModel = cmsComponentService.getAbstractCMSComponent("test_component2",
				Collections.singletonList(catalogVersionModel));
		catalogService.setSessionCatalogVersions(Collections.singleton(catalogVersionModel));
		pageModel = cmsPageService.getPageByLabelOrId("test_homepage_default");


	}

	private void createNumberSeries(final String key)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Creating NumberSeries for " + key);
		}
		processCodeGenerator.setKey(key);
		processCodeGenerator.generate();
	}

	protected CatalogVersionModel getSampleCatalogVersion()
	{
		return catalogService.getCatalogVersion("sampleCatalog", "Staged");
	}

	private CmsRestrictionActionDefinitionModel extractActionDefinition(final TypedObject typedObject)
	{

		CmsRestrictionActionDefinitionModel ret = null;
		if (typedObject == null)
		{
			return ret;
		}
		if (typedObject.getObject() instanceof CmsRestrictionActionDefinitionModel)
		{
			ret = (CmsRestrictionActionDefinitionModel) typedObject.getObject();
		}
		return ret;
	}

	@Test
	public void testCreateShowItemRestrictionForComponentsNotInverted()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForComponent(false, null, false);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Component should contains segment restriction!", isContainsRestriction(componentModel.getRestrictions(), false));
		assertTrue("Output action shouldn't be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForComponentsInverted()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForComponent(true, null, false);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Component should contains segment restriction!", isContainsRestriction(componentModel.getRestrictions(), false));
		assertTrue("Output action should be inverted!",
				Boolean.TRUE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForComponentsNotInvertedWithAltCmp()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForComponent(false,
				altComponentModel, false);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Component should contains segment restriction!", isContainsRestriction(componentModel.getRestrictions(), false));
		assertTrue("Alternative Component should contains invert restriction!",
				isContainsRestriction(altComponentModel.getRestrictions(), true));
		assertTrue("Output action shouldn't be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForComponentsInvertedWithAltCmp()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForComponent(false,
				altComponentModel, false);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Component should contains segment restriction!", isContainsRestriction(componentModel.getRestrictions(), false));
		assertTrue("Alternative Component should contains invert restriction!",
				isContainsRestriction(altComponentModel.getRestrictions(), true));
		assertTrue("Output action should be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForDefaultPage()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForPage(false, false, false, 2, 2);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertFalse("Personalized Page must exist!", getPersonalizedPages(pageModel).isEmpty());
		assertTrue("Personalized Page should contains segment restriction!",
				isContainsRestriction(getPersonalizedPages(pageModel).iterator().next().getRestrictions(), false));
		assertTrue("Output action shouldn't  be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);

	}

	@Test
	public void testCreateShowItemRestrictionForDefaultPageInverted()
	{
		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForPage(true, false, false, 2, 2);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertFalse("Personalized Page must exist!", getPersonalizedPages(pageModel).isEmpty());
		assertTrue("Personalized Page should contains segment restriction!",
				isContainsRestriction(getPersonalizedPages(pageModel).iterator().next().getRestrictions(), false));
		assertTrue("Output action should be inverted!",
				Boolean.TRUE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}


	@Test
	public void testCreateShowItemRestrictionForNonDefaultPageWithoutRestriction()
	{
		pageModel.setDefaultPage(Boolean.FALSE);
		modelService.save(pageModel);

		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForPage(false, false, false, 1, 0);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		modelService.refresh(pageModel);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Personalized Page must not exist!", getPersonalizedPages(pageModel).isEmpty());
		assertTrue("Current page should contains segment restriction!", isContainsRestriction(pageModel.getRestrictions(), false));
		assertTrue("Output action shouldn't be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForNonDefaultPageWithRestrictionPersonalized()
	{
		final CMSTimeRestrictionModel timeRestrictionModel = new CMSTimeRestrictionModel();
		timeRestrictionModel.setUid(UUID.randomUUID().toString());
		timeRestrictionModel.setCatalogVersion(getSampleCatalogVersion());
		pageModel.setDefaultPage(Boolean.FALSE);
		pageModel.setLabel("withRestrictionPersonalized");
		pageModel.setRestrictions((List) Collections.singletonList(timeRestrictionModel));
		modelService.save(pageModel);

		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForPage(false, false, false, 1, 2);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		modelService.refresh(pageModel);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertFalse("Personalized Page must exist!", getPersonalizedPages(pageModel).isEmpty());
		assertTrue("Personalized page should contains segment restriction!",
				isContainsRestriction(getPersonalizedPages(pageModel).iterator().next().getRestrictions(), false));
		assertTrue("Output action shouldn't be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateShowItemRestrictionForNonDefaultPageWithRestrictionUseExistingRestriction()
	{
		final CMSTimeRestrictionModel timeRestrictionModel = new CMSTimeRestrictionModel();
		timeRestrictionModel.setUid(UUID.randomUUID().toString());
		timeRestrictionModel.setCatalogVersion(getSampleCatalogVersion());
		pageModel.setDefaultPage(Boolean.FALSE);
		pageModel.setLabel("withRestrictionAddToExisting");
		pageModel.setRestrictions((List) Collections.singletonList(timeRestrictionModel));
		modelService.save(pageModel);

		final RestrictionActionCreateContext mockedContext = prepareRestrictionActionCreateContextForPage(false, true, false, 1, 0);
		final TypedObject wrappedCmsOutputAction = btgCockpitService.createShowItemOutputAction(mockedContext);
		modelService.refresh(pageModel);
		assertTrue("Cms Output Action Definition is created not correctly!", extractActionDefinition(wrappedCmsOutputAction)
				.getSegment().equals(segmentModel));
		assertNotNull("Segment restriciton should exist!", extractActionDefinition(wrappedCmsOutputAction).getSegmentRestrictions()
				.iterator().next());
		assertTrue("Personalized Page must not exist!", getPersonalizedPages(pageModel).isEmpty());
		assertTrue("Current page should contains segment restriction!", isContainsRestriction(pageModel.getRestrictions(), false));
		assertTrue("Output action shouldn't be inverted!",
				Boolean.FALSE.equals(extractActionDefinition(wrappedCmsOutputAction).getInverted()));
		verify(mockedContext);
	}

	@Test
	public void testCreateSegmentFromCms()
	{
		//Create segment
		final CatalogVersionModel sampleCatalogVersion = getSampleCatalogVersion();
		final TypedObject btgSegment = btgCockpitService.createBtgSegment("testSegment",
				cockpitTypeService.wrapItem(sampleCatalogVersion));
		assertTrue(btgSegment.getObject() instanceof BTGSegmentModel);


		//Create restriction action and assign to segment
		final TypedObject cmsRestrictionAction = btgCockpitService.createCmsRestrictionAction(btgSegment,
				cockpitTypeService.wrapItem(componentModel), false);

		final CmsRestrictionActionDefinitionModel actionModel = (CmsRestrictionActionDefinitionModel) cmsRestrictionAction
				.getObject();
		assertTrue(Boolean.FALSE.equals(actionModel.getInverted()));
		assertTrue(actionModel.getCatalogVersion().equals(sampleCatalogVersion));
		assertTrue(actionModel.getSegment().equals(btgSegment.getObject()));
		assertTrue(actionModel.getSegmentRestrictions().size() == 1);
		assertTrue(actionModel.getSegmentRestrictions().iterator().next().getComponents().iterator().next().equals(componentModel));
		assertTrue(componentModel.getRestrictions().size() == 1);


		//Create second restriction action and assign to segment
		final TypedObject createCmsRestrictionActionInv = btgCockpitService.createCmsRestrictionAction(btgSegment,
				cockpitTypeService.wrapItem(componentModel), true);
		final CmsRestrictionActionDefinitionModel actionModelInv = (CmsRestrictionActionDefinitionModel) createCmsRestrictionActionInv
				.getObject();
		assertTrue(Boolean.TRUE.equals(actionModelInv.getInverted()));
		modelService.refresh(componentModel);
		final List<AbstractRestrictionModel> restrictions = componentModel.getRestrictions();
		assertTrue("Expected 2 restrictions, got " + restrictions.size(), restrictions.size() == 2);


		final BTGSegmentModel segmentModel = (BTGSegmentModel) btgSegment.getObject();
		modelService.remove(segmentModel);

		assertTrue(modelService.isRemoved(actionModel));
		assertTrue(modelService.isRemoved(actionModelInv));
		assertTrue(modelService.isRemoved(segmentModel));
		modelService.refresh(componentModel);
		assertTrue(componentModel.getRestrictions().size() == 0);
		for (final AbstractRestrictionModel abstractRestrictionModel : restrictions)
		{
			assertTrue(modelService.isRemoved(abstractRestrictionModel));
		}
	}

	private List<AbstractPageModel> getPersonalizedPages(final AbstractPageModel sourcePageModel)
	{
		final List<AbstractPageModel> ret = new ArrayList<AbstractPageModel>();

		final Collection<AbstractPageModel> allPagesWithSameType = cmsAdminPageService.getAllPagesByType(sourcePageModel
				.getItemtype());

		final String labelOrId = ((ContentPageModel) sourcePageModel).getLabelOrId();

		if (labelOrId != null)
		{
			for (final AbstractPageModel abstractPageModel : allPagesWithSameType)
			{
				if (!sourcePageModel.equals(abstractPageModel))
				{
					if (labelOrId.equals(((ContentPageModel) abstractPageModel).getLabelOrId()))
					{
						ret.add(abstractPageModel);
					}
				}
			}
		}
		return ret;
	}

	private boolean isContainsRestriction(final Collection<AbstractRestrictionModel> inputCollection, final boolean inverse)
	{
		boolean ret = false;
		for (final AbstractRestrictionModel restriction : inputCollection)
		{

			if (inverse && (restriction instanceof CMSInverseRestrictionModel))
			{
				ret = true;
				break;
			}
			else if (restriction instanceof BtgSegmentRestrictionModel)
			{
				ret = true;
				break;
			}
		}
		return ret;
	}


	private RestrictionActionCreateContext prepareRestrictionActionCreateContextForComponent(final boolean inverted,
			final AbstractCMSComponentModel altComponent, final boolean showAltComponent)
	{
		final RestrictionActionCreateContext mockedContext = createMock(RestrictionActionCreateContext.class);
		expect(Boolean.valueOf(mockedContext.isAttachedToComponent())).andReturn(Boolean.TRUE);
		expect(mockedContext.getComponentReference()).andReturn(cockpitTypeService.wrapItem(componentModel));
		expect(mockedContext.getCurrentSegment()).andReturn(cockpitTypeService.wrapItem(segmentModel)).times(2);
		expect(Boolean.valueOf(mockedContext.isInverted())).andReturn(Boolean.valueOf(inverted));
		expect(mockedContext.getAltComponent()).andReturn(cockpitTypeService.wrapItem(altComponent));
		if (altComponent == null)
		{
			expect(Boolean.valueOf(mockedContext.isShowAltComponent())).andReturn(Boolean.valueOf(showAltComponent));
		}
		replay(mockedContext);
		return mockedContext;
	}

	private RestrictionActionCreateContext prepareRestrictionActionCreateContextForPage(final boolean inverted,
			final boolean adddSegmentRestrToPage, final boolean useExistingRestrictions, final int addHowMany, final int useHowMany)
	{
		final RestrictionActionCreateContext mockedContext = createMock(RestrictionActionCreateContext.class);
		expect(Boolean.valueOf(mockedContext.isAttachedToComponent())).andReturn(Boolean.FALSE);
		if (addHowMany > 0)
		{
			expect(Boolean.valueOf(mockedContext.isAddSegmentRestrToPage())).andReturn(Boolean.valueOf(adddSegmentRestrToPage))
					.times(addHowMany);
		}
		if (useHowMany > 0)
		{
			expect(Boolean.valueOf(mockedContext.isUseExistingRestrictions())).andReturn(Boolean.valueOf(useExistingRestrictions))
					.times(useHowMany);
		}
		expect(mockedContext.getPageReference()).andReturn(cockpitTypeService.wrapItem(pageModel));
		expect(mockedContext.getCurrentSegment()).andReturn(cockpitTypeService.wrapItem(segmentModel)).atLeastOnce();
		expect(Boolean.valueOf(mockedContext.isInverted())).andReturn(Boolean.valueOf(inverted));
		replay(mockedContext);
		return mockedContext;
	}

	public void initApplicationContext()
	{
		final GenericApplicationContext context = new GenericApplicationContext();
		context.setResourceLoader(new DefaultResourceLoader(Registry.class.getClassLoader()));
		context.setClassLoader(Registry.class.getClassLoader());
		context.getBeanFactory().setBeanClassLoader(Registry.class.getClassLoader());
		context.setParent(Registry.getGlobalApplicationContext());
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
		xmlReader.setDocumentReaderClass(ScopeTenantIgnoreDocReader.class);
		xmlReader.setBeanClassLoader(Registry.class.getClassLoader());
		xmlReader.loadBeanDefinitions(getSpringConfigurationLocations());
		context.refresh();
		final AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		this.applicationContex = context;
	}

	protected String[] getSpringConfigurationLocations()
	{
		return new String[]
		{ "classpath:/cockpit/cockpit-spring-wrappers.xml", // 
				"classpath:/cockpit/cockpit-spring-services.xml", //
				"classpath:/cockpit/cockpit-junit-spring.xml", //
				"classpath:/btgcockpit/btgcockpit-spring-services.xml", //
				"classpath:/cmscockpit/cmscockpit-spring-services.xml" //
		}; // 
	}
}
