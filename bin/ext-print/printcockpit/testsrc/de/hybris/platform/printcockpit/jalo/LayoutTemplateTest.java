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
package de.hybris.platform.printcockpit.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.classextension.EasyMock.createMock;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.cockpit.components.notifier.Notification;
import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.zk.mock.DummyExecution;
import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.print.jalo.Chapter;
import de.hybris.platform.print.jalo.CometConfiguration;
import de.hybris.platform.print.jalo.Grid;
import de.hybris.platform.print.jalo.GridElement;
import de.hybris.platform.print.jalo.LayoutTemplate;
import de.hybris.platform.print.jalo.Page;
import de.hybris.platform.print.jalo.PlaceholderTemplate;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.jalo.Publication;
import de.hybris.platform.print.model.ItemPlacementModel;
import de.hybris.platform.printcockpit.constants.PrintCockpitConstants;
import de.hybris.platform.printcockpit.model.layout.LayoutService;
import de.hybris.platform.printcockpit.model.layout.impl.FixedModel;
import de.hybris.platform.printcockpit.model.publication.PublicationService;
import de.hybris.platform.printcockpit.view.layouts.grid.PreviewPage;
import de.hybris.platform.printcockpit.view.layouts.grid.PreviewSlot;
import de.hybris.platform.printcockpitnew.services.PrintcockpitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;


/**
 * This test verifies if "default layout template" assigned to particular grid is really used when using fixed model.<br>
 * <b>Preconditions:</b><br>
 * There are two grids (left and right) both has different layout templates assigned.<br>
 * These layout templates are apply-able for product item.<br>
 * <b>Scenario:</b><br>
 * Three times few products are dropped at different slots - see methods comments for details.
 * @author Jacek
 */
public class LayoutTemplateTest extends ServicelayerTransactionalTest
{
	private static final String TEST_PAGE = "page1";
	private static final String TEST_PUB = "testPub";
	private static final Logger LOG = Logger.getLogger(LayoutTemplateTest.class);
	@Resource
	private ModelService modelService;
	private GenericApplicationContext applicationContext;
	private Catalog catalog;
	private FixedModel fxModel;
	private Page samplePage;

	@Before
	public void setUp() throws Exception
	{
		initApplicationContext();
		initDummyZkAppContext();
		createCoreData();
		createDefaultCatalog();
		createPublication();
		createFixedModel();
	}

	public void initApplicationContext() throws Exception
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
		this.applicationContext = context;
	}

	protected String[] getSpringConfigurationLocations()
	{
		return new String[]
		{ "classpath:/cockpit/cockpit-spring-wrappers.xml", // 
				"classpath:/cockpit/cockpit-spring-services.xml", //
				"classpath:/cockpit/cockpit-spring-services-test.xml", // 
				"classpath:/cockpit/cockpit-junit-spring.xml", // 
				"classpath:/printcockpit/printcockpit-spring-beans.xml", //
				"classpath:/printcockpit/printcockpit-junit-spring.xml", //
				"classpath:/printcockpit/printcockpit-spring-services.xml", // 
				"classpath:/printcockpit/printcockpit-spring-security.xml" };
	}

	private void initDummyZkAppContext() throws Exception
	{
		ExecutionsCtrl.setCurrent(new DummyExecution(applicationContext));
		//Executions.activate(new DummyDesktop());//getCurrent().getDesktop();
	}

	public static void createCoreData() throws Exception
	{
		LOG.info("Creating essential data for core ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/servicelayer/test/testBasics.csv", "windows-1252");

		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public static void createDefaultCatalog() throws Exception
	{
		LOG.info("Creating test catalog ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();

		importCsv("/printcockpit/test/testCatalog.csv", "windows-1252");

		// checking imported stuff
		final CatalogVersion version = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
		Assert.assertNotNull(version);
		JaloSession.getCurrentSession().getSessionContext()
				.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Collections.singletonList(version));
		//setting catalog to session and admin user
		final Category category = CategoryManager.getInstance().getCategoriesByCode("testCategory0").iterator().next();
		Assert.assertNotNull(category);
		for (int i = 0; i < 15; i++)
		{
			final Product product = (Product) ProductManager.getInstance().getProductsByCode("testProduct" + i).iterator().next();
			Assert.assertNotNull(product);
			Assert.assertEquals(category, CategoryManager.getInstance().getCategoriesByProduct(product).iterator().next());
		}
		LOG.info("Finished creating test catalog in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	protected void createPublication()
	{
		long nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals("There are some publications already ", 0, nrpub);

		//publication
		final Map attributeValues = new HashMap<String, Object>();
		attributeValues.put(Publication.CODE, TEST_PUB);
		attributeValues.put(Publication.TITLE, "testTitle");
		attributeValues.put(Publication.DESCRIPTION, "testDescription");
		final Publication samplePublication = PrintManager.getInstance().createPublication(attributeValues);

		//chapter
		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "chapter1");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		final Chapter sampleChapter1 = PrintManager.getInstance().createChapter(attributeValues);

		//comet config
		attributeValues.clear();
		attributeValues.put(CometConfiguration.CODE, "testCometConfig");
		final CometConfiguration cometConfiguration = PrintManager.getInstance().createCometConfiguration(attributeValues);

		//layout templates
		final List<LayoutTemplate> layouts = new ArrayList<LayoutTemplate>();
		//template - left
		attributeValues.clear();
		attributeValues.put(LayoutTemplate.CODE, "leftLayTmpl");
		attributeValues.put(LayoutTemplate.ID, Integer.valueOf(1));
		final LayoutTemplate leftLayTmpl = PrintManager.getInstance().createLayoutTemplate(attributeValues);
		layouts.add(leftLayTmpl);
		//template - right
		attributeValues.clear();
		attributeValues.put(LayoutTemplate.CODE, "rightLayTmpl");
		attributeValues.put(LayoutTemplate.ID, Integer.valueOf(2));
		final LayoutTemplate rightLayTmpl = PrintManager.getInstance().createLayoutTemplate(attributeValues);
		layouts.add(rightLayTmpl);

		//placeholder group
		final ComposedType ctx = TypeManager.getInstance().getComposedType(CoreConstants.TC.PRODUCT);
		attributeValues.clear();
		attributeValues.put(PlaceholderTemplate.CODE, "holderGrp");
		attributeValues.put(PlaceholderTemplate.COMETCONFIG, cometConfiguration);
		attributeValues.put(PlaceholderTemplate.TYPE, ctx);
		attributeValues.put(PlaceholderTemplate.LAYOUTTEMPLATES, layouts);
		PrintManager.getInstance().createPlaceholderTemplate(attributeValues);

		//grids
		final List<Grid> grids = new ArrayList<Grid>();
		//grid - left
		attributeValues.clear();
		attributeValues.put(Grid.NAME, "testLeftGrid");
		attributeValues.put(Grid.ID, Integer.valueOf(1));
		attributeValues.put(Grid.XSIZE, Double.valueOf(PrintCockpitConstants.A4.WIDTH.getValue()));
		attributeValues.put(Grid.YSIZE, Double.valueOf(PrintCockpitConstants.A4.HEIGHT.getValue()));
		attributeValues.put(Grid.DEFAULTLAYOUTTEMPLATE, leftLayTmpl);
		attributeValues.put(Grid.COMETCONFIG, cometConfiguration);
		attributeValues.put(
				Grid.SPREADPOSITION,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.SPREADPOSITION,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.SpreadPosition.LEFT));
		final Grid leftGrid = PrintManager.getInstance().createGrid(attributeValues);

		//left grid elements
		for (int i = 1; i <= 2; i++)
		{
			attributeValues.clear();
			attributeValues.put(GridElement.ID, Integer.valueOf(i));
			attributeValues.put(GridElement.XPOS, Double.valueOf(10));
			attributeValues.put(GridElement.YPOS, Double.valueOf(i * 10));
			attributeValues.put(GridElement.XSIZE, Double.valueOf(5));
			attributeValues.put(GridElement.GRID, leftGrid);
			PrintManager.getInstance().createGridElement(attributeValues);
		}
		grids.add(leftGrid);
		assertEquals("Grid has wrong number of slots", 2, leftGrid.getElements().size());
		assertEquals("Wrong template assigned", Integer.valueOf(1), leftGrid.getDefaultLayoutTemplate().getId());

		//grid - right
		attributeValues.clear();
		attributeValues.put(Grid.NAME, "testRightGrid");
		attributeValues.put(Grid.ID, Integer.valueOf(2));
		attributeValues.put(Grid.XSIZE, Double.valueOf(PrintCockpitConstants.A4.WIDTH.getValue()));
		attributeValues.put(Grid.YSIZE, Double.valueOf(PrintCockpitConstants.A4.HEIGHT.getValue()));
		attributeValues.put(Grid.DEFAULTLAYOUTTEMPLATE, rightLayTmpl);
		attributeValues.put(Grid.COMETCONFIG, cometConfiguration);
		attributeValues.put(
				Grid.SPREADPOSITION,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.SPREADPOSITION,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.SpreadPosition.RIGHT));
		final Grid rightGrid = PrintManager.getInstance().createGrid(attributeValues);

		//right grid elements
		for (int i = 1; i <= 2; i++)
		{
			attributeValues.clear();
			attributeValues.put(GridElement.ID, Integer.valueOf(i));
			attributeValues.put(GridElement.XPOS, Double.valueOf(10));
			attributeValues.put(GridElement.YPOS, Double.valueOf(i * 10));
			attributeValues.put(GridElement.XSIZE, Double.valueOf(5));
			attributeValues.put(GridElement.YSIZE, Double.valueOf(5));
			attributeValues.put(GridElement.GRID, rightGrid);
			PrintManager.getInstance().createGridElement(attributeValues);
		}
		grids.add(rightGrid);
		assertEquals("Grid has wrong number of slots", 2, rightGrid.getElements().size());
		assertEquals("Wrong template assigned", Integer.valueOf(2), rightGrid.getDefaultLayoutTemplate().getId());

		leftGrid.setRightGridId(rightGrid.getId());
		leftGrid.setRightGrid(rightGrid);
		rightGrid.setLeftGridId(leftGrid.getId());
		rightGrid.setLeftGrid(leftGrid);

		//page
		attributeValues.clear();
		attributeValues.put(Page.CODE, TEST_PAGE);
		attributeValues.put(
				Page.ALIGNMENT,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.PAGEALIGNMENT,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.PageAlignment.RIGHT));
		attributeValues.put(Page.PUBLICATION, samplePublication);
		attributeValues.put(Page.CHAPTER, sampleChapter1);
		attributeValues.put(Page.GRIDID, leftGrid.getId());
		attributeValues.put(
				Page.GRIDMODE,
				EnumerationManager.getInstance().getEnumerationValue(de.hybris.platform.print.constants.PrintConstants.TC.GRIDMODE,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.GridMode.FIXED));
		samplePage = PrintManager.getInstance().createPage(attributeValues);
		assertEquals("Problem with creating first test page for test chapter", 1, sampleChapter1.getPages().size());

		//connect grids to configuration, configuration to publication
		cometConfiguration.setGrids(grids);
		samplePublication.setConfiguration(cometConfiguration);

		assertEquals("Problem with setting grid in page.", Integer.valueOf(1), samplePage.getGridId());

		//Test if publication structure was build correctly
		nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals("Wrong number of publications ", 1, nrpub);

		final int nrchap = samplePublication.getRootChapters().size();
		assertEquals("Wrong number of root chapters", 1, nrchap);

		catalog = CatalogManager.getInstance().getCatalog("testCatalog");
	}

	/**
	 * Test fixed model creation with 3 placements
	 */
	private void createFixedModel() throws Exception
	{
		final TypedObject typedPage = createMock(TypedObject.class);//modelService.get(samplePage));
		expect(typedPage.getObject()).andReturn(modelService.get(samplePage)).atLeastOnce();
		replay(typedPage);
		/* 1.Create */
		fxModel = new FixedModel(typedPage)
		{
			@Override
			protected void setNotification(final Notification notification)
			{
				//do nothing it's not a web context :)
			}
		};
		fxModel.setModelHelper((ModelHelper) applicationContext.getBean(PrintCockpitConstants.MODEL_HELPER_BEAN_ID));
		fxModel.setModelService((ModelService) applicationContext.getBean("modelService"));
		fxModel.setPrintcockpitService((PrintcockpitService) applicationContext
				.getBean(PrintCockpitConstants.PRINTCOCKPIT_SERVICE_BEAN_ID));
		fxModel.setPublicationService((PublicationService) applicationContext
				.getBean(PrintCockpitConstants.PUBLICATION_SERVICE_BEAN_ID));
		fxModel.setLayoutService((LayoutService) applicationContext.getBean(PrintCockpitConstants.LAYOUT_SERVICE_BEAN_ID));
		fxModel.setCockpitTypeService((TypeService) applicationContext.getBean("cockpitTypeService"));
		fxModel.initModel();
		fxModel.addNewEmptyPreviewPage(0);
		//there should be only 1 page
		assertEquals(1, fxModel.getPreviewPages().size());
	}

	/**
	 * See sub steps comments
	 */
	@Test
	public void test()
	{
		step1();
		step2();
		step3();
	}

	/**
	 * 1. Add (drop) 3 products - no overriding -> should get 2 pages, 1st and 2nd placement should have same layout template 3rd
	 * another
	 */
	private void step1()
	{
		final Collection productsToAdd = new ArrayList();
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct0").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct1").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct2").getPK()));
		final PreviewPage pageToDropInto = fxModel.getPreviewPages().get(0);
		final PreviewSlot slotToDropInto = pageToDropInto.getSlots().get(0);//1st slot.
		assertEquals("Slot should be empty", true, slotToDropInto.isEmpty());
		//test:
		fxModel.addPlacements(productsToAdd, slotToDropInto, false);
		final List<PreviewPage> previewPages = fxModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 2, previewPages.size());
		//check page: 1st
		verify1stPage();
		//check page: 2nd
		verify2ndPage();
	}

	private void verify1stPage()
	{
		final PreviewPage page1 = fxModel.getPreviewPages().get(0);
		assertTrue("Preview page should be full.", page1.isComplete());
		//check slots:
		final PreviewSlot slot0 = page1.getSlots().get(0);
		final ItemPlacementModel placement0 = (ItemPlacementModel) slot0.getPlacement().getObject();
		final String code0 = ((ProductModel) placement0.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct0", code0);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(1), placement0.getLayoutTemplate().getId());
		final PreviewSlot slot1 = page1.getSlots().get(1);
		final ItemPlacementModel placement1 = (ItemPlacementModel) slot1.getPlacement().getObject();
		final String code1 = ((ProductModel) placement1.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct1", code1);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(1), placement1.getLayoutTemplate().getId());
	}

	private void verify2ndPage()
	{
		final PreviewPage page2 = fxModel.getPreviewPages().get(1);
		assertTrue("Preview page should not be full.", !page2.isComplete() && !page2.isEmpty());
		//check slots:
		final PreviewSlot slot2 = page2.getSlots().get(0);
		final ItemPlacementModel placement2 = (ItemPlacementModel) slot2.getPlacement().getObject();
		final String code2 = ((ProductModel) placement2.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct2", code2);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(2), placement2.getLayoutTemplate().getId());
	}

	/**
	 * 2. Add (drop) 1 product on 4th page - created placement must have layout template of right grid
	 */
	private void step2()
	{
		final Collection productToAdd = new ArrayList();
		productToAdd.add(modelService.get(catalog.getProduct("testProduct6").getPK()));
		fxModel.addNewEmptyPreviewPage(2);
		fxModel.addNewEmptyPreviewPage(3);
		final PreviewPage pageToDropInto = fxModel.getPreviewPages().get(3);
		final PreviewSlot slotToDropInto = pageToDropInto.getSlots().get(0);//1st slot.
		assertEquals("Slot should be empty", true, slotToDropInto.isEmpty());
		//test:
		fxModel.addPlacements(productToAdd, slotToDropInto, false);
		final List<PreviewPage> previewPages = fxModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 4, previewPages.size());
		//check page: 4th
		verify4thPage();
	}

	private void verify4thPage()
	{
		final PreviewPage page3 = fxModel.getPreviewPages().get(3);
		assertTrue("Preview page should not be full nor empty.", !page3.isComplete() && !page3.isEmpty());
		//check slots:
		final PreviewSlot slot6 = page3.getSlots().get(0);
		final ItemPlacementModel placement6 = (ItemPlacementModel) slot6.getPlacement().getObject();
		final String code6 = ((ProductModel) placement6.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct6", code6);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(2), placement6.getLayoutTemplate().getId());
	}

	/**
	 * 3. Add (drop) 2 products on 3th page - created placements must have layout template of left grid. no other placement shallbe
	 * moved
	 */
	private void step3()
	{
		final Collection productsToAdd = new ArrayList();
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct4").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct5").getPK()));
		final PreviewPage pageToDropInto = fxModel.getPreviewPages().get(2);
		final PreviewSlot slotToDropInto = pageToDropInto.getSlots().get(0);//1st slot.
		assertEquals("Slot should be empty", true, slotToDropInto.isEmpty());
		//test:
		fxModel.addPlacements(productsToAdd, slotToDropInto, false);
		final List<PreviewPage> previewPages = fxModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 4, previewPages.size());
		//check pages:
		verify1stPage();
		verify2ndPage();
		verify3thPage();
		verify4thPage();
	}

	private void verify3thPage()
	{
		final PreviewPage page2 = fxModel.getPreviewPages().get(2);
		assertTrue("Preview page should be full.", page2.isComplete());
		//check slots:
		final PreviewSlot slot4 = page2.getSlots().get(0);
		final ItemPlacementModel placement4 = (ItemPlacementModel) slot4.getPlacement().getObject();
		final String code4 = ((ProductModel) placement4.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct4", code4);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(1), placement4.getLayoutTemplate().getId());
		final PreviewSlot slot5 = page2.getSlots().get(1);
		final ItemPlacementModel placement5 = (ItemPlacementModel) slot5.getPlacement().getObject();
		final String code5 = ((ProductModel) placement5.getItem()).getCode();
		assertEquals("Wrong product at this slot", "testProduct5", code5);
		assertEquals("Wrong layout template at this placemnt", Integer.valueOf(1), placement5.getLayoutTemplate().getId());
	}
}
