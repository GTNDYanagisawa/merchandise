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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.print.jalo.Chapter;
import de.hybris.platform.print.jalo.CometConfiguration;
import de.hybris.platform.print.jalo.Grid;
import de.hybris.platform.print.jalo.GridElement;
import de.hybris.platform.print.jalo.ItemPlacement;
import de.hybris.platform.print.jalo.Page;
import de.hybris.platform.print.jalo.Placement;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.jalo.Publication;
import de.hybris.platform.print.model.ItemPlacementModel;
import de.hybris.platform.printcockpit.constants.PrintCockpitConstants;
import de.hybris.platform.printcockpit.model.layout.LayoutService;
import de.hybris.platform.printcockpit.model.layout.impl.DynamicModel;
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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;


/**
 * Tests dynamic model - add, delete, move of placements and products etc.
 * @author Jacek
 */
public class DynamicModelTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(DynamicModelTest.class);
	private ApplicationContext applicationContext;
	private TypedObject typedPage;
	private List<Product> products;
	private TypeService typeService;
	private ModelService modelService;
	private Catalog catalog;
	private DynamicModel dynamicModel;

	@Before
	public void setUp() throws Exception
	{
		initApplicationContext();
		initDummyZkAppContext();
		createCoreData();
		createDefaultCatalog();
		createSamplePublication();
	}

	/**
	 * Just more products...
	 */
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
		final Product product = (Product) ProductManager.getInstance().getProductsByCode("testProduct0").iterator().next();
		Assert.assertNotNull(product);
		Assert.assertEquals(category, CategoryManager.getInstance().getCategoriesByProduct(product).iterator().next());


		LOG.info("Finished creating test catalog in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	/**
	 * 
	 */
	private void initDummyZkAppContext() throws Exception
	{
		ExecutionsCtrl.setCurrent(new DummyExecution(applicationContext));
		//Executions.activate(new DummyDesktop());//getCurrent().getDesktop();
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

	protected void createSamplePublication()
	{
		long nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals(0, nrpub);

		//publication
		final Map attributeValues = new HashMap<String, Object>();
		attributeValues.put(Publication.CODE, "testPub");
		attributeValues.put(Publication.TITLE, "testTitle");
		attributeValues.put(Publication.DESCRIPTION, "testDescription");
		final Publication samplePublication = PrintManager.getInstance().createPublication(attributeValues);

		//chapters
		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "chapter1");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		final Chapter sampleChapter1 = PrintManager.getInstance().createChapter(attributeValues);

		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "chapter2");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		final Chapter sampleChapter2 = PrintManager.getInstance().createChapter(attributeValues);

		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "chapter3");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		PrintManager.getInstance().createChapter(attributeValues);


		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "subchapter1");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
		PrintManager.getInstance().createChapter(attributeValues);

		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "subchapter2");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
		PrintManager.getInstance().createChapter(attributeValues);

		attributeValues.clear();
		attributeValues.put(Chapter.CODE, "subchapter3");
		attributeValues.put(Chapter.PUBLICATION, samplePublication);
		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
		PrintManager.getInstance().createChapter(attributeValues);

		//comet config
		attributeValues.clear();
		attributeValues.put(CometConfiguration.CODE, "testCometConfig");
		final CometConfiguration cometConfiguration = PrintManager.getInstance().createCometConfiguration(attributeValues);

		//grids
		final List<Grid> grids = new ArrayList<Grid>();
		//grid - left
		attributeValues.clear();
		attributeValues.put(Grid.NAME, "testLeftGrid");
		attributeValues.put(Grid.ID, Integer.valueOf(1));
		attributeValues.put(Grid.XSIZE, Double.valueOf(PrintCockpitConstants.A4.WIDTH.getValue()));
		attributeValues.put(Grid.YSIZE, Double.valueOf(PrintCockpitConstants.A4.HEIGHT.getValue()));
		attributeValues.put(Grid.COMETCONFIG, cometConfiguration);
		attributeValues.put(
				Grid.SPREADPOSITION,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.SPREADPOSITION,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.SpreadPosition.LEFT));
		final Grid leftGrid = PrintManager.getInstance().createGrid(attributeValues);

		//left grid elements
		for (int i = 1; i <= 4; i++)
		{
			attributeValues.clear();
			attributeValues.put(GridElement.ID, Integer.valueOf(i));
			attributeValues.put(GridElement.XPOS, Double.valueOf(10));
			attributeValues.put(GridElement.YPOS, Double.valueOf(i * 10));
			attributeValues.put(GridElement.XSIZE, Double.valueOf(5));
			attributeValues.put(GridElement.YSIZE, Double.valueOf(5));
			attributeValues.put(GridElement.GRID, leftGrid);
			PrintManager.getInstance().createGridElement(attributeValues);
		}
		grids.add(leftGrid);
		assertEquals(4, leftGrid.getElements().size());

		//grid - right
		attributeValues.clear();
		attributeValues.put(Grid.NAME, "testRightGrid");
		attributeValues.put(Grid.ID, Integer.valueOf(2));
		attributeValues.put(Grid.XSIZE, Double.valueOf(PrintCockpitConstants.A4.WIDTH.getValue()));
		attributeValues.put(Grid.YSIZE, Double.valueOf(PrintCockpitConstants.A4.HEIGHT.getValue()));
		attributeValues.put(Grid.COMETCONFIG, cometConfiguration);
		attributeValues.put(
				Grid.SPREADPOSITION,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.SPREADPOSITION,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.SpreadPosition.RIGHT));
		final Grid rightGrid = PrintManager.getInstance().createGrid(attributeValues);

		//right grid elements
		for (int i = 1; i <= 4; i++)
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
		assertEquals(4, rightGrid.getElements().size());

		leftGrid.setRightGridId(rightGrid.getId());
		rightGrid.setLeftGridId(leftGrid.getId());

		//page
		attributeValues.clear();
		attributeValues.put(Page.CODE, "page1");
		attributeValues.put(
				Page.ALIGNMENT,
				EnumerationManager.getInstance().getEnumerationValue(
						de.hybris.platform.print.constants.PrintConstants.TC.PAGEALIGNMENT,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.PageAlignment.RIGHT));
		attributeValues.put(Page.PUBLICATION, samplePublication);
		attributeValues.put(Page.CHAPTER, sampleChapter2);
		attributeValues.put(Page.GRIDID, leftGrid.getId());
		attributeValues.put(
				Page.GRIDMODE,
				EnumerationManager.getInstance().getEnumerationValue(de.hybris.platform.print.constants.PrintConstants.TC.GRIDMODE,
						de.hybris.platform.print.constants.PrintConstants.Enumerations.GridMode.FIXED));
		final Page samplePage = PrintManager.getInstance().createPage(attributeValues);
		assertEquals("Problem with creating first test page for test chapter", 1, sampleChapter2.getPages().size());

		//connect grids to configuration, configuration to publication
		cometConfiguration.setGrids(grids);
		samplePublication.setConfiguration(cometConfiguration);

		assertEquals("Problem with setting grid in page.", Integer.valueOf(1), samplePage.getGridId());

		attributeValues.clear();
		attributeValues.put(Page.CODE, "page2");
		attributeValues.put(Page.PUBLICATION, samplePublication);
		attributeValues.put(Page.CHAPTER, sampleChapter2);
		PrintManager.getInstance().createPage(attributeValues);
		assertEquals("Problem with creating second test page for test chapter", 2, sampleChapter2.getPages().size());
		//item placements:
		catalog = CatalogManager.getInstance().getCatalog("testCatalog");
		products = new ArrayList<Product>(catalog.getAllProducts());
		assertNotNull("None product was created", products);

		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct0"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		PrintManager.getInstance().createItemPlacement(attributeValues);
		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct1"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		PrintManager.getInstance().createItemPlacement(attributeValues);
		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct2"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		PrintManager.getInstance().createItemPlacement(attributeValues);
		assertEquals(3, samplePage.getPlacements().size());

		//Test if publication structure was build correctly
		nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals(1, nrpub);

		final int nrchap = samplePublication.getRootChapters().size();
		assertEquals(3, nrchap);
		typeService = (TypeService) applicationContext.getBean(PrintCockpitConstants.TYPE_SERVICE_BEAN_ID);
		typedPage = typeService.wrapItem(samplePage);
		modelService = (ModelService) applicationContext.getBean(PrintCockpitConstants.MODEL_SERVICE_BEAN_ID);
	}

	/**
	 * Test fixed model: create, d'n'd, move
	 */
	@Test
	public void testDynamicModel() throws Exception
	{
		testCreateDynamicModel();
		testAddNewProductsAtTheEnd();
		testAddNewProductsOverExistingPlacement();
		//testMovePlacementsFurther();
		//testMovePlacementsOverExistingPlacement();
	}

	/**
	 * Test fixed model creation with 3 placements
	 */
	private void testCreateDynamicModel() throws Exception
	{
		/* 1.Create */
		dynamicModel = new DynamicModel(typedPage)
		{
			@Override
			protected void setNotification(final Notification notification)
			{
				//do nothing it's not a web context :)
			}
		};
		dynamicModel.setModelHelper((ModelHelper) applicationContext.getBean(PrintCockpitConstants.MODEL_HELPER_BEAN_ID));
		dynamicModel.setModelService((ModelService) applicationContext.getBean("modelService"));
		dynamicModel.setPrintcockpitService((PrintcockpitService) applicationContext
				.getBean(PrintCockpitConstants.PRINTCOCKPIT_SERVICE_BEAN_ID));
		dynamicModel.setPublicationService((PublicationService) applicationContext
				.getBean(PrintCockpitConstants.PUBLICATION_SERVICE_BEAN_ID));
		dynamicModel.setLayoutService((LayoutService) applicationContext.getBean(PrintCockpitConstants.LAYOUT_SERVICE_BEAN_ID));
		dynamicModel.setCockpitTypeService((TypeService) applicationContext.getBean("cockpitTypeService"));

		dynamicModel.initModel();
		//there should be only 1 page since grid has 4 slots and we have 3 placements
		assertEquals(2, dynamicModel.getPreviewPages().size());
	}

	/**
	 * Test adding new products
	 */
	private void testAddNewProductsAtTheEnd() throws Exception
	{
		/* 2.Add (drop) 5 products - no overriding -> should get 2 full pages */
		final Collection productsToAdd = new ArrayList();
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct3").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct4").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct5").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct6").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct7").getPK()));
		final PreviewPage pageToDropInto = dynamicModel.getPreviewPages().get(0);
		final PreviewSlot slotToDropInto = pageToDropInto.getSlots().get(3);//4th - last.
		assertEquals(true, slotToDropInto.isEmpty());//it should be empty since there are only 3 first slots occupied
		dynamicModel.addPlacements(productsToAdd, slotToDropInto, false);
		final List<PreviewPage> previewPages = dynamicModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 2, previewPages.size());//NOPMD
		for (int i = 0; i < 2; i++)
		{
			final PreviewPage page = previewPages.get(i);
			assertTrue(page.isComplete());
			for (int j = 0; j < page.getSlots().size(); j++)
			{
				final PreviewSlot slot = page.getSlots().get(j);
				final String code = ((ProductModel) ((ItemPlacementModel) slot.getPlacement().getObject()).getItem()).getCode();
				assertEquals("testProduct" + ((i * 4) + j), code);//products should be placed exactly one by the other
			}
		}
	}

	/**
	 * Test adding new products over exiting placement
	 */
	private void testAddNewProductsOverExistingPlacement() throws Exception
	{
		/*
		 * 3. Drop 3 new products at 2nd slot of first page (0,1) -> 2 full pages,3rd with 3: 0,8,9,10;1,2,3,4;5,6,7,;
		 */
		//YTODO check why preview pages order is different
		final Collection productsToAdd = new ArrayList();
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct8").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct9").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct10").getPK()));
		final PreviewPage page2DropInto = dynamicModel.getPreviewPages().get(0);
		final PreviewSlot slot2DropInto = page2DropInto.getSlots().get(1);//2nd
		dynamicModel.addPlacements(productsToAdd, slot2DropInto, false);
		final List<PreviewPage> previewPages = dynamicModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 4, previewPages.size());//NOPMD
		for (int i = 0; i < 4; i++)
		{
			final PreviewPage page = previewPages.get(i);
			final List<PreviewSlot> slots = page.getSlots();
			switch (i)
			{
				case 0:
					assertTrue(page.isComplete());
					final String code0 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct0", code0);
					final String code8 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct8", code8);
					final String code9 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct9", code9);
					final String code10 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct10", code10);
					break;
				case 1:
					assertTrue(page.isComplete());
					final String code1 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct1", code1);
					final String code2 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct2", code2);
					final String code3 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct3", code3);
					final String code4 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct4", code4);
					break;
				case 2:
					final String code5 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct5", code5);
					final String code6 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct6", code6);
					final String code7 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("testProduct7", code7);
					break;
				default:
					assertTrue(page.isEmpty());
					break;
			}
		}

	}
}
