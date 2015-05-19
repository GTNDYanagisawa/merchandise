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

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Test;


/**
 * JUnit Tests for the Printcockpit extension
 */
public class PrintCockpitTest extends HybrisJUnit4TransactionalTest
{
	//	@SuppressWarnings("unused")
	//	private static final Logger LOG = Logger.getLogger(PrintCockpitTest.class.getName());
	//	private static final String iso = "de_de";
	//
	//	public static final String BEAN_ID_PUBLICATION_SERVICE = "publicationService";
	//
	//	private GenericApplicationContext appctx = null;
	//
	//	@Before
	//	public void setUp()
	//	{
	//		assertNull(appctx);
	//
	//		final GenericApplicationContext context = new GenericApplicationContext();
	//		context.setResourceLoader(new DefaultResourceLoader(Registry.class.getClassLoader()));
	//		context.setClassLoader(Registry.class.getClassLoader());
	//		context.getBeanFactory().setBeanClassLoader(Registry.class.getClassLoader());
	//
	//		context.setParent(Registry.getGlobalApplicationContext());
	//
	//		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
	//		xmlReader.loadBeanDefinitions(new ClassPathResource("/printcockpit/printcockpit-junit-spring.xml", Registry.class
	//				.getClassLoader()));
	//		xmlReader.loadBeanDefinitions(new ClassPathResource("/printcockpit/printcockpit-spring-services.xml", Registry.class
	//				.getClassLoader()));
	//		xmlReader.loadBeanDefinitions(new ClassPathResource("/printcockpit/printcockpit-spring-wrappers.xml", Registry.class
	//				.getClassLoader()));
	//
	//		context.refresh();
	//
	//		appctx = context;
	//
	//		assertNotNull(appctx);
	//
	//
	//		try
	//		{
	//			C2LManager.getInstance().createLanguage(iso);
	//		}
	//		catch (final ConsistencyCheckException e)
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	@After
	//	public void tearDown()
	//	{
	//		if (appctx != null)
	//		{
	//			appctx.close();
	//			appctx = null;
	//		}
	//	}
	//
	//
	//	protected List queryPKsByCode(final String from, final String code)
	//	{
	//		final String query = "SELECT {PK} FROM {" + from + "} WHERE {Code} LIKE '" + code + "' ";
	//		return JaloSession.getCurrentSession().getFlexibleSearch().search(query, Collections.EMPTY_MAP, PK.class).getResult();
	//	}
	//
	//	protected void printBeanNames()
	//	{
	//		final String[] names = appctx.getBeanDefinitionNames();
	//		String outputString = "Names of registered beans (" + appctx.getBeanDefinitionCount() + "): ";
	//		for (int i = 0; i < names.length; i++)
	//		{
	//			outputString += names[i] + ", ";
	//		}
	//		LOG.info(outputString);
	//	}
	//
	//
	//	protected void createSamplePublication()
	//	{
	//		long nrpub = PrintManager.getInstance().getAllPublications().size();
	//		assertEquals(0, nrpub);
	//
	//
	//		final Map attributeValues = new HashMap<String, Object>();
	//		attributeValues.put(Publication.CODE, "testPub");
	//		attributeValues.put(Publication.TITLE, "testTitle");
	//		attributeValues.put(Publication.DESCRIPTION, "testDescription");
	//		final Publication samplePublication = PrintManager.getInstance().createPublication(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "chapter1");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		final Chapter sampleChapter1 = PrintManager.getInstance().createChapter(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "chapter2");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		final Chapter sampleChapter2 = PrintManager.getInstance().createChapter(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "chapter3");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		PrintManager.getInstance().createChapter(attributeValues);
	//
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "subchapter1");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
	//		PrintManager.getInstance().createChapter(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "subchapter2");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
	//		PrintManager.getInstance().createChapter(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Chapter.CODE, "subchapter3");
	//		attributeValues.put(Chapter.PUBLICATION, samplePublication);
	//		attributeValues.put(Chapter.SUPERCHAPTER, sampleChapter1);
	//		PrintManager.getInstance().createChapter(attributeValues);
	//
	//		attributeValues.clear();
	//		attributeValues.put(Page.CODE, "page1");
	//		attributeValues.put(Page.PUBLICATION, samplePublication);
	//		attributeValues.put(Page.CHAPTER, sampleChapter2);
	//		final Page samplePage = PrintManager.getInstance().createPage(attributeValues);
	//		assertEquals(1, sampleChapter2.getPages().size());
	//
	//		attributeValues.clear();
	//		attributeValues.put(Page.CODE, "page2");
	//		attributeValues.put(Page.PUBLICATION, samplePublication);
	//		attributeValues.put(Page.CHAPTER, sampleChapter2);
	//		PrintManager.getInstance().createPage(attributeValues);
	//		assertEquals(2, sampleChapter2.getPages().size());
	//
	//
	//		attributeValues.clear();
	//		attributeValues.put(Placement.PUBLICATION, samplePublication);
	//		attributeValues.put(Placement.PAGE, samplePage);
	//		PrintManager.getInstance().createItemPlacement(attributeValues);
	//		attributeValues.clear();
	//		attributeValues.put(Placement.PUBLICATION, samplePublication);
	//		attributeValues.put(Placement.PAGE, samplePage);
	//		PrintManager.getInstance().createItemPlacement(attributeValues);
	//		attributeValues.clear();
	//		attributeValues.put(Placement.PUBLICATION, samplePublication);
	//		attributeValues.put(Placement.PAGE, samplePage);
	//		PrintManager.getInstance().createItemPlacement(attributeValues);
	//
	//
	//		//Test if publication structure was build correctly
	//		nrpub = PrintManager.getInstance().getAllPublications().size();
	//		assertEquals(1, nrpub);
	//
	//		final int nrchap = samplePublication.getRootChapters().size();
	//		assertEquals(3, nrchap);
	//
	//
	//	}
	//
	//
	@Test
	public void testCreatePlacement()
	{
		//		createSamplePublication();
		//		/////////////////////////////////
		//		assertNotNull(appctx);
		//		final PublicationService psi = (PublicationService) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
		//
		//		assertNotNull(psi);
		//
		//		final List<PublicationModel> pubList = psi.getExistingPublications();
		//		assertEquals(1, pubList.size());
		//
		//		final PublicationModel firstPubLst = pubList.get(0);
		//		LOG.info(firstPubLst.getCode());
		//
		//		final PublicationModel pub = psi.loadPublication(firstPubLst);
		//
		//		assertEquals(0, (pub).getUnassignedPages().size());
		//
		//		final PlacementModel placement = psi.createPlacement(pub);
		//
		//		assertEquals(3, pub.getRootChapters().size());
		//
		//
		//		PageModel insertPage = null;
		//
		//		for (final ChapterModel chap : pub.getRootChapters())
		//		{
		//			if ("chapter2".equals(chap.getCode()))
		//			{
		//				insertPage = chap.getPages().get(0);
		//				break;
		//			}
		//		}
		//
		//		assertEquals(3, insertPage.getPlacements().size());
		//
		//		placement.moveTo(insertPage, 0);
		//
		//		assertEquals(4, insertPage.getPlacements().size());
		//
		//		insertPage.getPlacements().get(0).moveTo(insertPage.getChapter().getPages().get(1), 0);
		//		insertPage.getPlacements().get(0).moveTo(insertPage.getChapter().getPages().get(1), 0);
		//
		//		//Change some values
		//		insertPage.setCode("changed code");
		//
		//		//now save the publication
		//		psi.savePublication(pub);
		//
		//
		//		assertEquals(2, PrintManager.getInstance().getPage(insertPage.getCode()).getPlacements().size());
		//
		//
		//		assertEquals("changed code", ((Page) JaloSession.getCurrentSession().getItem(insertPage.getPK())).getCode());
		//
	}
	//
	//
	//	@Test
	//	public void luceneSearchTest() throws Exception
	//	{
	//		if (Config.getBoolean("mode.dump", false))
	//		{
	//			if (LOG.isInfoEnabled())
	//			{
	//				LOG.info("Skipping PrintCockpitTest.luceneSearchTest");
	//			}
	//			return;
	//		}
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//		final LucenePublicationSearcher searcher = (LucenePublicationSearcher) pubservice.getSearcher();
	//		searcher.publicationChanged(null, publication);
	//		while (!searcher.isSearchAvailable())
	//		{
	//			Thread.sleep(500);
	//		}
	//
	//		SearchResultModel result = pubservice.queryElements(publication, "page", Collections.EMPTY_LIST, Collections.EMPTY_SET);
	//		assertEquals(2, result.getResult().size());
	//
	//
	//		result = pubservice.queryElements(publication, "Page*", Collections.EMPTY_LIST, Collections.EMPTY_SET);
	//		assertEquals(2, result.getResult().size());
	//
	//		result = pubservice.queryElements(publication, "sub*", Collections.EMPTY_LIST, Collections.EMPTY_SET);
	//		assertEquals(3, result.getResult().size());
	//		assertNotNull(result.getResult().get(0));
	//
	//		//printBeanNames();
	//	}
	//
	//	@Test
	//	public void saveConflictTestSameAttributes()
	//	{
	//		final String langiso = JaloSession.getCurrentSession().getSessionContext().getLanguage().getIsoCode();
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//		final ChapterModel chapter = publication.getRootChapters().get(0);
	//		final Chapter realChapter = JaloSession.getCurrentSession().getItem(chapter.getPK());
	//
	//		//change the same attribute should result in a conflict
	//		chapter.setCode("chapter1_ui_changed");
	//		realChapter.setCode("chapter1_changed");
	//		List<StoreConflictModel> conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(1, conflicts.size());
	//		conflicts.clear();
	//
	//		//setting the attribute in ui to the changed value in jalo should resolve that conflict
	//		chapter.setCode("chapter1_changed");
	//		conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(0, conflicts.size());
	//		conflicts.clear();
	//
	//		//now the same for a localized attribute
	//		chapter.setDescription(langiso, "chapter1_ui_description");
	//		realChapter.setDescription("chapter1_description");
	//		conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(1, conflicts.size());
	//		conflicts.clear();
	//		chapter.setDescription(langiso, "chapter1_description");
	//		conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(0, conflicts.size());
	//
	//	}
	//
	//	@Test
	//	public void saveConflictTestDifferentAttributes()
	//	{
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//		final ChapterModel chapter = publication.getRootChapters().get(0);
	//		final Chapter realChapter = JaloSession.getCurrentSession().getItem(chapter.getPK());
	//
	//		//change different attributes should not cause any conflicts
	//		chapter.setCode("chapter1_new_code");
	//		realChapter.setDescription("chapter1_new_description");
	//		final List<StoreConflictModel> conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(0, conflicts.size());
	//		conflicts.clear();
	//	}
	//
	//	@Test
	//	public void saveConflictTestStructureChanged()
	//	{
	//		createSamplePublication();
	//
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//		ChapterModel subchapter1 = null;
	//
	//		for (final ChapterModel chap : publication.getRootChapters())
	//		{
	//			if ("chapter1".equals(chap.getCode()))
	//			{
	//				subchapter1 = chap.getSubChapters().get(0);
	//			}
	//		}
	//
	//		final Chapter c = JaloSession.getCurrentSession().getItem(subchapter1.getPK());
	//		c.setSuperChapter(null);
	//		final List<StoreConflictModel> conflicts = pubservice.checkForConflicts(publication);
	//		assertEquals(1, conflicts.size());
	//		assertEquals(StoreConflictModel.ORIGINAL_MOVED, conflicts.get(0).getType());
	//	}
	//
	//	@Test
	//	public void saveAsTest()
	//	{
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//		pubservice.savePublicationAs(publication, "publication2");
	//
	//		final List<Publication> publications = PrintManager.getInstance().getAllPublications();
	//		assertEquals(2, publications.size());
	//
	//		final Publication p1 = publications.get(0);
	//		final Publication p2 = publications.get(1);
	//		assertEquals(p1.getComponents().size(), p2.getComponents().size());
	//		assertEquals(p1.getRootChapters().size(), p2.getRootChapters().size());
	//
	//		List res = queryPKsByCode("Chapter", "chapter1");
	//		assertEquals(res.size(), 2);
	//		assertFalse(res.get(0).equals(res.get(1)));
	//
	//		res = queryPKsByCode("Chapter", "subchapter1");
	//		assertEquals(res.size(), 2);
	//		assertFalse(res.get(0).equals(res.get(1)));
	//
	//		res = queryPKsByCode("Page", "page1");
	//		assertEquals(res.size(), 2);
	//		assertFalse(res.get(0).equals(res.get(1)));
	//	}
	//
	//	@Test
	//	public void deleteTest()
	//	{
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//
	//		List res = queryPKsByCode("Page", "page1");
	//		assertEquals(res.size(), 1);
	//
	//		res = queryPKsByCode("Publication", "testPub");
	//		assertEquals(res.size(), 1);
	//
	//		pubservice.deletePublication(publication);
	//
	//		res = queryPKsByCode("Page", "page1");
	//		assertEquals(res.size(), 0);
	//		res = queryPKsByCode("Publication", "testPub");
	//		assertEquals(res.size(), 0);
	//	}
	//
	//	@Test
	//	public void cloneTest()
	//	{
	//		createSamplePublication();
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//		final PublicationModel publication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//
	//		final ChapterImplModel chapter = (ChapterImplModel) publication.getRootChapters().get(0);
	//		final ChapterImplModel dolly = (ChapterImplModel) pubservice.cloneChapter(publication, chapter, true);
	//
	//		final Set<String> modAtt = ((EditableAttributeContainerImpl) dolly.getAttributeContainer()).getModifiedAttributes();
	//
	//		for (final String string : modAtt)
	//		{
	//			final Object o1 = dolly.getAttribute(string);
	//			final Object o2 = chapter.getAttribute(string);
	//
	//			if (o1 instanceof Collection)
	//			{
	//				assertNotSame(o1, o2);
	//			}
	//
	//			if (o1 != null
	//					&& (string.equals(Chapter.ID) || string.equals(Chapter.CODE.toLowerCase())
	//							|| string.equals(Chapter.CREATION_TIME.toLowerCase()) || string.equals(Chapter.PK.toLowerCase())))
	//			{
	//				assertFalse(o1.equals(o2));
	//			}
	//			else if (string.equals(Chapter.SUBCHAPTERS.toLowerCase()))
	//			{
	//				if (dolly.getSubChapters().size() > 0)
	//				{
	//					assertFalse(o1.equals(o2));
	//				}
	//			}
	//			else
	//			{
	//				assertEquals(o1, o2);
	//			}
	//		}
	//	}
	//
	//
	//	protected void compareAttributeContainers(final PublicationElementModel item1, final PublicationElementModel item2)
	//	{
	//
	//		final Set<String> ad1 = ((EditableAttributeContainer) ((PublicationElementImplModel) item1).getAttributeContainer())
	//				.getAllAttributeDescriptors();
	//		final Set<String> ad2 = ((EditableAttributeContainer) ((PublicationElementImplModel) item2).getAttributeContainer())
	//				.getAllAttributeDescriptors();
	//
	//		assertEquals(ad1.size(), ad2.size());
	//		final List l = new ArrayList(ad1);
	//
	//		for (final Object object : l)
	//		{
	//			Object o1 = null;
	//			Object o2 = null;
	//
	//			final String attr = object.toString();
	//			if (((EditableAttributeContainerImpl) ((PublicationElementImplModel) item1).getAttributeContainer())
	//					.isAttributeLocalized(attr))
	//			{
	//				o1 = ((PublicationElementImplModel) item1).getLocalizedAttribute(iso, attr);
	//				o2 = ((PublicationElementImplModel) item2).getLocalizedAttribute(iso, attr);
	//			}
	//			else
	//			{
	//				o1 = ((PublicationElementImplModel) item1).getAttribute(attr);
	//				o2 = ((PublicationElementImplModel) item2).getAttribute(attr);
	//			}
	//
	//			if (!(o1 == null || o1 instanceof Date))
	//			{
	//				assertEquals(o1, o2);
	//			}
	//		}
	//	}
	//
	//	@Test
	//	public void createStoreReloadTest()
	//	{
	//		final PublicationServiceImpl pubservice = (PublicationServiceImpl) appctx.getBean(BEAN_ID_PUBLICATION_SERVICE);
	//		assertEquals(0, pubservice.getExistingPublications().size());
	//
	//		//Create a PublicationModel
	//		final PublicationModel publication = pubservice.createPublication();
	//		publication.setSubTitle2(iso, "subtitle2");
	//
	//		//publication.setCode("createdPublication");
	//
	//		final ChapterModel chapter = pubservice.createChapter(publication);
	//		chapter.setCode("createdChapter");
	//		chapter.setComment("comment");
	//		chapter.setSubTitle(iso, "subtitle");
	//		chapter.moveTo(null, 0);
	//
	//		final PageModel page = pubservice.createPage(publication, chapter);
	//		page.setCode("createdPage");
	//		page.setFileName("filename");
	//		page.setComment("comment");
	//		page.setDescription(iso, "description");
	//		page.setChapter(chapter);
	//		page.moveTo(chapter, 0);
	//
	//		final PlacementModel placement = pubservice.createPlacement(publication);
	//		placement.moveTo(page, 0);
	//
	//		//Store it
	//		pubservice.savePublication(publication);
	//		assertEquals(1, pubservice.getExistingPublications().size());
	//
	//		//reload it
	//		final PublicationModel reloadedPublication = pubservice.loadPublication(pubservice.getExistingPublications().get(0));
	//		final ChapterModel reloadedChapter = reloadedPublication.getRootChapters().get(0);
	//		final PageModel reloadedPage = reloadedChapter.getPages().get(0);
	//		final PlacementModel reloadedPlacement = reloadedPage.getPlacements().get(0);
	//
	//		compareAttributeContainers(publication, reloadedPublication);
	//		compareAttributeContainers(chapter, reloadedChapter);
	//		compareAttributeContainers(page, reloadedPage);
	//		compareAttributeContainers(placement, reloadedPlacement);
	//	}
}
