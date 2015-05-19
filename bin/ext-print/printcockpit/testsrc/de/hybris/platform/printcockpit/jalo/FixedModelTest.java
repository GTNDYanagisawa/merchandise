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
import de.hybris.platform.jalo.JaloBusinessException;
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
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
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
 * Tests fixed model - add, delete, move of placements and products etc.
 * @author Jacek
 * @see <b>debug info:</b> de.hybris.platform.printcockpit.jalo.FixedModelTest.printPreviewPages(String, FixedModel)
 */
public class FixedModelTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(FixedModelTest.class);
	private ApplicationContext applicationContext;
	private TypedObject typedPage;
	private ModelService modelService;
	private Catalog catalog;
	private FixedModel fixedModel;
	private Publication samplePublication;
	private Page samplePage;

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
	public static void createDefaultCatalog() throws JaloBusinessException
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

	private void initDummyZkAppContext()
	{
		ExecutionsCtrl.setCurrent(new DummyExecution(applicationContext));
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

	protected void createSamplePublication() //NOPMD: simple test initialization
	{
		long nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals("", 0, nrpub);

		//publication
		final Map attributeValues = new HashMap<String, Object>();
		attributeValues.put(Publication.CODE, "testPub");
		attributeValues.put(Publication.TITLE, "testTitle");
		attributeValues.put(Publication.DESCRIPTION, "testDescription");
		samplePublication = PrintManager.getInstance().createPublication(attributeValues);

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
		assertEquals("", 4, leftGrid.getElements().size());

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
		assertEquals("", 4, rightGrid.getElements().size());

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
		samplePage = PrintManager.getInstance().createPage(attributeValues);
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
		assertNotNull("None product was created", new ArrayList<Product>(catalog.getAllProducts()));

		final List<Placement> placements = new ArrayList<Placement>();

		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct0"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		placements.add(PrintManager.getInstance().createItemPlacement(attributeValues));
		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct1"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		placements.add(PrintManager.getInstance().createItemPlacement(attributeValues));
		attributeValues.clear();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct2"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(-1));
		placements.add(PrintManager.getInstance().createItemPlacement(attributeValues));


		samplePage.setPlacements(placements);


		assertEquals("", 3, samplePage.getPlacements().size());

		//Test if publication structure was build correctly
		nrpub = PrintManager.getInstance().getAllPublications().size();
		assertEquals("", 1, nrpub);

		final int nrchap = samplePublication.getRootChapters().size();
		assertEquals("", 3, nrchap);
		final TypeService typeService = (TypeService) applicationContext.getBean(PrintCockpitConstants.TYPE_SERVICE_BEAN_ID);
		typedPage = typeService.wrapItem(samplePage);
		modelService = (ModelService) applicationContext.getBean(PrintCockpitConstants.MODEL_SERVICE_BEAN_ID);
	}

	/**
	 * Test fixed model: create, d'n'd, move
	 */
	@Test
	public void testFixedModel() throws Exception //NOPMD: tests split into several methods
	{
		testCreateFixedModel();
		testPlacements("Create Fixed Model");
		testAddNewProductsAtTheEnd();
		testPlacements("Add New Products At The End");
		testAddNewProductsOverExistingPlacement();
		testPlacements("Add New Products Over Existing Placement");
		testMovePlacementsFurther();
		testPlacements("Move Placements Further");
		testMovePlacements1PageFurther();
		testPlacements("Move Placements 1 Page Further");
	}

	/**
	 * Test case for PRIC-552
	 */
	@Test
	public void testPlacementMisConfiguration() throws Exception
	{
		final Map<String, Object> attributeValues = new HashMap<String, Object>();
		attributeValues.put(Placement.PUBLICATION, samplePublication);
		attributeValues.put(Placement.PAGE, samplePage);
		attributeValues.put(ItemPlacement.ITEM, catalog.getProduct("testProduct0"));
		attributeValues.put(Placement.PAGENUMBER, Integer.valueOf(-1));
		//the situation as described in PRIC-552 : a placement is misconfigured (4 slots on the grid, the placement is configured to be in 5th)
		attributeValues.put(Placement.GRIDELEMENTID, Integer.valueOf(5));
		PrintManager.getInstance().createItemPlacement(attributeValues);
		testCreateFixedModel();
		fixedModel.initModel();
	}

	/**
	 * Moves all placements from page 3 to page 4. A blank page should be introduced.
	 * {@code 
	 * 	- - | - 1 | 0  8 | 4 5  =>  - - | - 1 | - - | 0  8 | 4 5  
	 *    - - | 2 3 | 9 10 | 6 7  =>  - - | 2 3 | - - | 9 10 | 6 7
 	 * }
	 */
	private void testMovePlacements1PageFurther()
	{
		final List<PreviewPage> previewPages = fixedModel.getPreviewPages();
		final Collection placementsToMove = new ArrayList<PlacementModel>();
		for (final PreviewSlot dragSlot : fixedModel.getPreviewPages().get(2).getSlots())
		{
			placementsToMove.add(dragSlot.getPlacement().getObject());
		}
		final PreviewPage dropPage = fixedModel.getPreviewPages().get(3);
		final PreviewSlot dropSlot = dropPage.getSlots().get(0);
		fixedModel.movePlacements(placementsToMove, dropSlot, false);
		assertEquals("Not enough/too many preview pages", 5, previewPages.size());//NOPMD
		for (int i = 0; i < 5; i++)
		{
			final PreviewPage page = previewPages.get(i);
			switch (i)
			{
				case 0:
					assertTrue("", page.isEmpty());
					break;
				case 1:
					final List<PreviewSlot> slots2 = page.getSlots();
					assertTrue("", slots2.get(0).isEmpty());
					final String code1 = ((ProductModel) ((ItemPlacementModel) slots2.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct1", code1);
					final String code2 = ((ProductModel) ((ItemPlacementModel) slots2.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct2", code2);
					final String code3 = ((ProductModel) ((ItemPlacementModel) slots2.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct3", code3);
					break;
				case 2:
					assertTrue("", page.isEmpty());
					break;
				case 3:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots = page.getSlots();
					final String code0 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct0", code0);
					final String code8 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct8", code8);
					final String code9 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct9", code9);
					final String code10 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct10", code10);
					break;
				case 4:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots3 = page.getSlots();
					final String code4 = ((ProductModel) ((ItemPlacementModel) slots3.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct4", code4);
					final String code5 = ((ProductModel) ((ItemPlacementModel) slots3.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct5", code5);
					final String code6 = ((ProductModel) ((ItemPlacementModel) slots3.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct6", code6);
					final String code7 = ((ProductModel) ((ItemPlacementModel) slots3.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct7", code7);
					break;
			}
		}

	}

	private void testPlacements(final String iteration)
	{
		final PageModel page = (PageModel) typedPage.getObject();
		final List<PlacementModel> placements = page.getPlacements();
		if (!placements.isEmpty())
		{
			int prevPageNo = -1;
			int prevGridElId = -1;
			int prevPosition = -1;
			for (final PlacementModel placement : placements)
			{
				assertNotNull(iteration, placement);
				assertNotNull(iteration, placement.getPageNumber());
				assertNotNull(iteration, placement.getGridElementId());
				assertNotNull(iteration, placement.getPosition());
				final int currentPageNo = placement.getPageNumber().intValue();
				final int currentGridElId = placement.getGridElementId().intValue();
				final int currentPosition = placement.getPosition().intValue();
				assertTrue(iteration, prevPageNo <= currentPageNo);
				assertTrue(iteration + "Position: prev: " + prevPosition + "\tcurrent: " + currentPosition,
						prevPosition < currentPosition);
				assertTrue(iteration + "Grid pos: prev: " + prevGridElId + "\tcurrent: " + currentGridElId,
						prevGridElId < currentGridElId || prevPageNo != currentPageNo);
				prevPageNo = currentPageNo;
				prevPosition = currentPosition;
				prevGridElId = currentGridElId;

			}
		}
	}

	/**
	 * Test fixed model creation with 3 placements
	 */
	private void testCreateFixedModel() throws Exception
	{
		/* 1.Create */
		fixedModel = new FixedModel(typedPage)
		{
			@Override
			protected void setNotification(final Notification notification)
			{
				//do nothing it's not a web context :)
			}
		};
		fixedModel.setModelHelper((ModelHelper) applicationContext.getBean(PrintCockpitConstants.MODEL_HELPER_BEAN_ID));
		fixedModel.setModelService((ModelService) applicationContext.getBean("modelService"));
		fixedModel.setPrintcockpitService((PrintcockpitService) applicationContext
				.getBean(PrintCockpitConstants.PRINTCOCKPIT_SERVICE_BEAN_ID));
		fixedModel.setPublicationService((PublicationService) applicationContext
				.getBean(PrintCockpitConstants.PUBLICATION_SERVICE_BEAN_ID));
		fixedModel.setLayoutService((LayoutService) applicationContext.getBean(PrintCockpitConstants.LAYOUT_SERVICE_BEAN_ID));
		fixedModel.setCockpitTypeService((TypeService) applicationContext.getBean("cockpitTypeService"));
		fixedModel.initModel();
		//there should be only 1 page since grid has 4 slots and we have 3 placements
		assertEquals("", 1, fixedModel.getPreviewPages().size());
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
		final PreviewPage pageToDropInto = fixedModel.getPreviewPages().get(0);
		final PreviewSlot slotToDropInto = pageToDropInto.getSlots().get(3);//4th - last.
		assertEquals("", true, slotToDropInto.isEmpty());//it should be empty since there are only 3 first slots occupied
		fixedModel.addPlacements(productsToAdd, slotToDropInto, false);
		final List<PreviewPage> previewPages = fixedModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 2, previewPages.size());//NOPMD
		for (int i = 0; i < 2; i++)
		{
			final PreviewPage page = previewPages.get(i);
			assertTrue("", page.isComplete());
			final int pageSize = page.getSlots().size();
			for (int j = 0; j < pageSize; j++)
			{
				final PreviewSlot slot = page.getSlots().get(j);
				final String code = ((ProductModel) ((ItemPlacementModel) slot.getPlacement().getObject()).getItem()).getCode();
				/* here 0 instead of 2 */assertEquals("products should be placed exactly one by the other!", code, "testProduct"
						+ ((i * pageSize) + j));
			}
		}
	}

	/**
	 * Test adding new products over exiting placement
	 */
	private void testAddNewProductsOverExistingPlacement() throws Exception
	{
		/*
		 * 3. Drop 3 new products at 2nd slot of first page (0,1) -> 1st page is full, 2nd is empty, 3rd has 1st slot empty, 4th is
		 * full
		 */
		final Collection productsToAdd = new ArrayList();
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct8").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct9").getPK()));
		productsToAdd.add(modelService.get(catalog.getProduct("testProduct10").getPK()));
		final PreviewPage page2DropInto = fixedModel.getPreviewPages().get(0);
		final PreviewSlot slot2DropInto = page2DropInto.getSlots().get(1);//2nd
		fixedModel.addPlacements(productsToAdd, slot2DropInto, false);
		final List<PreviewPage> previewPages = fixedModel.getPreviewPages();
		assertEquals("Not enough/too many preview pages", 3, previewPages.size());//NOPMD
		for (int i = 0; i < 3; i++)
		{
			final PreviewPage page = previewPages.get(i);
			switch (i)
			{
				case 0:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots = page.getSlots();
					final String code0 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct0", code0);
					final String code8 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct8", code8);
					final String code9 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct9", code9);
					final String code10 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct10", code10);
					break;
				/*
				 * case 1: assertTrue(page.isEmpty()); break;
				 */
				case 1:
					final List<PreviewSlot> slots2 = page.getSlots();
					assertTrue("", slots2.get(0).isEmpty());
					final String code1 = ((ProductModel) ((ItemPlacementModel) slots2.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct1", code1);
					final String code2 = ((ProductModel) ((ItemPlacementModel) slots2.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct2", code2);
					final String code3 = ((ProductModel) ((ItemPlacementModel) slots2.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct3", code3);
					break;
				case 2:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots3 = page.getSlots();
					final String code4 = ((ProductModel) ((ItemPlacementModel) slots3.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct4", code4);
					final String code5 = ((ProductModel) ((ItemPlacementModel) slots3.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct5", code5);
					final String code6 = ((ProductModel) ((ItemPlacementModel) slots3.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct6", code6);
					final String code7 = ((ProductModel) ((ItemPlacementModel) slots3.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct7", code7);
					break;
			}
		}
	}

	/**
	 * Since it is very hard to debug the code under test in this test class one may use this method to get detailed description of
	 * page status.
	 */
	@SuppressWarnings("unused")
	private void printPreviewPages(final String infoId, final FixedModel fixedModel)
	{
		LOG.debug("\nTesting:" + infoId + "\n");
		for (final PreviewPage pp : fixedModel.getPreviewPages())
		{
			LOG.debug("page idx:   " + pp.getPageIndex());
			LOG.debug("page slots: " + pp.getSlots().size());
			for (final PreviewSlot ps : pp.getSlots())
			{
				LOG.debug("\tidx:       " + ps.getGridElementID());
				LOG.debug("\tempty:     " + ps.isEmpty());
				if (!ps.isEmpty())
				{
					final ItemPlacementModel placement = (ItemPlacementModel) ps.getPlacement().getObject();
					LOG.debug("\tplacement: " + ((ProductModel) placement.getItem()).getCode());
					LOG.debug("\tpageNo:    " + placement.getPageNumber());
					LOG.debug("\tposition:  " + placement.getPosition());
				}
				LOG.debug("\n");
			}
		}
	}

	/**
	 * Move all placements from first page [0,1] to first slot at 3rd page (it's empty) [2,1], expected result is: 1st, 2nd page
	 * are empty 3rd page has all moved placements, 4th is empty, 5th and 6th contains placements which were at 3rd and 4th page
	 * before move.
	 */
	private void testMovePlacementsFurther()
	{
		final List<PreviewPage> previewPages = fixedModel.getPreviewPages();
		final Collection placementsToMove = new ArrayList<PlacementModel>();
		for (final PreviewSlot dragSlot : fixedModel.getPreviewPages().get(0).getSlots())
		{
			placementsToMove.add(dragSlot.getPlacement().getObject());
		}
		final PreviewPage dropPage = fixedModel.getPreviewPages().get(2);
		final PreviewSlot dropSlot = dropPage.getSlots().get(0);
		fixedModel.movePlacements(placementsToMove, dropSlot, false);
		assertEquals("Not enough/too many preview pages", 4, previewPages.size());//NOPMD
		for (int i = 0; i < 4; i++)
		{
			final PreviewPage page = previewPages.get(i);
			switch (i)
			{
				case 0:
					assertTrue("", page.isEmpty());
					break;
				case 1:
					final List<PreviewSlot> slots2 = page.getSlots();
					assertTrue("", slots2.get(0).isEmpty());
					final String code1 = ((ProductModel) ((ItemPlacementModel) slots2.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct1", code1);
					final String code2 = ((ProductModel) ((ItemPlacementModel) slots2.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct2", code2);
					final String code3 = ((ProductModel) ((ItemPlacementModel) slots2.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct3", code3);
					break;
				case 2:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots = page.getSlots();
					final String code0 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct0", code0);
					final String code8 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct8", code8);
					final String code9 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct9", code9);
					final String code10 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct10", code10);
					break;
				case 3:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots3 = page.getSlots();
					final String code4 = ((ProductModel) ((ItemPlacementModel) slots3.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct4", code4);
					final String code5 = ((ProductModel) ((ItemPlacementModel) slots3.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct5", code5);
					final String code6 = ((ProductModel) ((ItemPlacementModel) slots3.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct6", code6);
					final String code7 = ((ProductModel) ((ItemPlacementModel) slots3.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct7", code7);
					break;
			}
		}
	}

	/**
	 * Move placements from of pages: 5th, 6th over 2nd slot at 3rd page [2,2] which contains 8th placement. Expected result: 3rd
	 * and 4th page contains placement 0,1,...7. Other pages are empty, placements 8,9,10 are overwritten.
	 */
	@SuppressWarnings("unused")
	private void testMovePlacementsOverExistingPlacement()
	{
		//YTODO - check whats wrong that some placements (testProduct8) has no page assigned
		final List<PreviewPage> previewPages = fixedModel.getPreviewPages();
		final Collection placementsToMove = new ArrayList<PlacementModel>();
		final List<PreviewSlot> dragSlots = fixedModel.getPreviewPages().get(4).getSlots();
		/* drag 7 placements */
		placementsToMove.add(dragSlots.get(1).getPlacement().getObject());
		placementsToMove.add(dragSlots.get(2).getPlacement().getObject());
		placementsToMove.add(dragSlots.get(3).getPlacement().getObject());
		for (final PreviewSlot dragSlot : fixedModel.getPreviewPages().get(5).getSlots())
		{
			placementsToMove.add(dragSlot.getPlacement().getObject());
		}
		/* drop at [2,2] */
		final PreviewPage dropPage = fixedModel.getPreviewPages().get(2);
		final PreviewSlot dropSlot = dropPage.getSlots().get(1);
		fixedModel.movePlacements(placementsToMove, dropSlot, true);
		assertEquals("Not enough/too many preview pages", 5, previewPages.size());//NOPMD
		for (int i = 0; i < 5; i++)
		{
			final PreviewPage page = previewPages.get(i);
			switch (i)
			{
				case 0:
					assertTrue("", page.isEmpty());
					break;
				case 1:
					assertTrue("", page.isEmpty());
					break;
				case 2:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots = page.getSlots();
					final String code0 = ((ProductModel) ((ItemPlacementModel) slots.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct0", code0);
					final String code1 = ((ProductModel) ((ItemPlacementModel) slots.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct1", code1);
					final String code2 = ((ProductModel) ((ItemPlacementModel) slots.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct2", code2);
					final String code3 = ((ProductModel) ((ItemPlacementModel) slots.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct3", code3);
					break;
				case 3:
					assertTrue("", page.isComplete());
					final List<PreviewSlot> slots3 = page.getSlots();
					final String code4 = ((ProductModel) ((ItemPlacementModel) slots3.get(0).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct4", code4);
					final String code5 = ((ProductModel) ((ItemPlacementModel) slots3.get(1).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct5", code5);
					final String code6 = ((ProductModel) ((ItemPlacementModel) slots3.get(2).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct6", code6);
					final String code7 = ((ProductModel) ((ItemPlacementModel) slots3.get(3).getPlacement().getObject()).getItem())
							.getCode();
					assertEquals("", "testProduct7", code7);
					break;
				case 4:
					assertTrue("", page.isEmpty());
					break;
				case 5:
					assertTrue("", page.isEmpty());
					break;
			}
		}
	}
}
