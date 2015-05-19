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
package de.hybris.platform.printcockpit;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.services.values.ValueHandlerPermissionException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.print.jalo.Chapter;
import de.hybris.platform.print.jalo.Publication;
import de.hybris.platform.print.model.ChapterModel;
import de.hybris.platform.print.model.ItemPlacementModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.print.model.PublicationModel;
import de.hybris.platform.printcockpit.services.impl.DefaultPublicationCloningService;
import de.hybris.platform.printcockpit.services.impl.PrintModelCloningContext;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * UnitTest to test the behaviour of the {@link de.hybris.platform.printcockpit.services.PublicationCloningService
 * PublicationCloningService} methods.
 */
public class PublicationCloningServiceUnitTest
{
	private PublicationModel sourcePublication = null;

	private final Map<String, String> idMap = new HashMap<String, String>();

	private ChapterModel sourceChapter = null;
	private PublicationModel targetPublication = null;

	private PageModel sourcePage = null;
	private ChapterModel targetChapter = null;

	private ItemPlacementModel sourcePlacement = null;
	private PageModel targetPage = null;

	private static final String SOURCE_CODE = "original";
	private static final String TARGET_PUBLICATION = "target_publication";
	private static final String TARGET_CHAPTER = "target_chapter";
	private static final String TARGET_PAGE = "target_page";

	private static final String COPY1_CODE = "original_copy";
	private static final String COPY2_CODE = "original_copy_1";

	private final StubLocaleProvider localeProvider = new StubLocaleProvider(new Locale("en"));

	@Resource
	private DefaultPublicationCloningService cloningService;

	private CommonI18NService commonI18NService;
	private FlexibleSearchService flexibleSearchService;
	private ModelHelper modelHelper;
	private ModelService modelService;
	private PrintModelCloningContext modelCloningContext;

	private PublicationModel createPublication(final List<ChapterModel> chapters, final String code)
	{
		final PublicationModel publication = new PublicationModel()
		{
			/**
			 * Overwrite "jalo" method
			 */
			@Override
			public Collection<ChapterModel> getRootChapters()
			{
				return chapters;
			}
		};
		publication.setCode(code);

		// Manually set the local provider. I guess in a real environment this
		// would be handled via the model service
		getContext(publication).setLocaleProvider(localeProvider);

		// Set correct publication
		for (final ChapterModel chapter : chapters)
		{
			chapter.setPublication(publication);
			for (final PageModel page : chapter.getPages())
			{
				page.setPublication(publication);
				for (final PlacementModel placement : page.getPlacements())
				{
					placement.setPublication(publication);
				}
			}
		}

		return publication;
	}

	@Before
	public void setUp()
	{
		commonI18NService = createMock(CommonI18NService.class);
		flexibleSearchService = createMock(FlexibleSearchService.class);
		modelHelper = createMock(ModelHelper.class);
		modelService = createMock(ModelService.class);
		modelCloningContext = new PrintModelCloningContext();

		cloningService = new DefaultPublicationCloningService();
		cloningService.setCommonI18NService(commonI18NService);
		cloningService.setFlexibleSearchService(flexibleSearchService);
		cloningService.setModelHelper(modelHelper);
		cloningService.setModelService(modelService);
		cloningService.setModelCloningContext(modelCloningContext);

		idMap.clear();

		final List<ChapterModel> sourceChapters = new ArrayList<ChapterModel>();
		for (int i = 0; i < 2; i++)
		{
			final ChapterModel chapter = new ChapterModel();
			chapter.setPublication(sourcePublication);
			chapter.setCode("source_chapter_" + i);

			// Manually set the locale provider. I guess in a real environment this
			// would be handled via the model service
			getContext(chapter).setLocaleProvider(localeProvider);
			chapter.setId(PK.createUUIDPK(0).toString());

			idMap.put(chapter.getCode(), chapter.getId());
			if (sourceChapter == null)
			{
				sourceChapter = chapter;
			}

			final List<PageModel> pages = new ArrayList<PageModel>();
			for (int j = 0; j < 2; j++)
			{
				final PageModel page = new PageModel();
				page.setChapter(chapter);
				page.setPublication(sourcePublication);
				page.setCode("source_page_" + i + "_" + j);
				page.setId(PK.createUUIDPK(0).toString());
				pages.add(page);

				// Manually set the locale provider. I guess in a real environment this
				// would be handled via the model service
				getContext(page).setLocaleProvider(localeProvider);

				idMap.put(page.getCode(), page.getId());
				if (sourcePage == null)
				{
					sourcePage = page;
				}

				final List<PlacementModel> placements = new ArrayList<PlacementModel>();
				for (int k = 0; k < 2; k++)
				{
					final ItemPlacementModel placement = new ItemPlacementModel();
					placement.setPage(page);
					placement.setPublication(sourcePublication);
					placement.setId(PK.createUUIDPK(0).toString());
					placements.add(placement);

					// Manually set the local provider. I guess in a real environment this
					// would be handled via the model service
					getContext(placement).setLocaleProvider(localeProvider);

					final ProductModel dummyProduct = new ProductModel();
					dummyProduct.setCode("source_product_" + i + "_" + j + "_" + k);

					placement.setItem(dummyProduct);

					idMap.put("source_placement_" + i + "_" + j + "_" + k, placement.getId());
					if (sourcePlacement == null)
					{
						sourcePlacement = placement;
					}
				}
				page.setPlacements(placements);
			}
			chapter.setPages(pages);

			// Setting empty list. In a real environment this would be handled via relations.
			chapter.setSubChapters(Collections.EMPTY_LIST);
			sourceChapters.add(chapter);
		}

		sourcePublication = createPublication(sourceChapters, SOURCE_CODE);

		final List<ChapterModel> targetChapters = new ArrayList<ChapterModel>();
		for (int i = 0; i < 2; i++)
		{
			final ChapterModel chapter = new ChapterModel();
			chapter.setPublication(targetPublication);
			chapter.setCode("target_chapter_" + i);

			// Manually set the locale provider. I guess in a real environment this
			// would be handled via the model service
			getContext(chapter).setLocaleProvider(localeProvider);
			chapter.setId(PK.createUUIDPK(0).toString());

			if (targetChapter == null)
			{
				targetChapter = chapter;
				targetChapter.setCode(TARGET_CHAPTER);
			}

			final List<PageModel> pages = new ArrayList<PageModel>();
			for (int j = 0; j < 2; j++)
			{
				final PageModel page = new PageModel();
				page.setChapter(chapter);
				page.setPublication(targetPublication);
				page.setCode("target_page_" + i + "_" + j);
				page.setId(PK.createUUIDPK(0).toString());
				pages.add(page);

				// Manually set the locale provider. I guess in a real environment this
				// would be handled via the model service
				getContext(page).setLocaleProvider(localeProvider);

				if (targetPage == null)
				{
					targetPage = page;
					targetPage.setCode(TARGET_PAGE);
				}

				final List<PlacementModel> placements = new ArrayList<PlacementModel>();
				for (int k = 0; k < 2; k++)
				{
					final ItemPlacementModel placement = new ItemPlacementModel();
					placement.setPage(page);
					placement.setPublication(targetPublication);
					placement.setId(PK.createUUIDPK(0).toString());
					placements.add(placement);

					// Manually set the local provider. I guess in a real environment this
					// would be handled via the model service
					getContext(placement).setLocaleProvider(localeProvider);

					final ProductModel dummyProduct = new ProductModel();
					dummyProduct.setCode("target_product_" + i + "_" + j + "_" + k);

					placement.setItem(dummyProduct);
				}
				page.setPlacements(placements);
			}
			chapter.setPages(pages);

			// Setting empty list. In a real environment this would be handled via relations.
			chapter.setSubChapters(Collections.EMPTY_LIST);
			targetChapters.add(chapter);
		}

		targetPublication = createPublication(targetChapters, TARGET_PUBLICATION);
	}

	@Test
	public void testCopyPublicationWithNoCopies() throws ValueHandlerPermissionException, ValueHandlerException
	{
		// getPublicationCopy
		final PublicationModel dummy = new PublicationModel();
		getContext(dummy).setLocaleProvider(localeProvider);

		final PublicationModel example = new PublicationModel();
		getContext(example).setLocaleProvider(localeProvider);

		final Publication pubMock = createMock(Publication.class);
		final Chapter chapMock = createMock(Chapter.class);

		expect(modelService.clone(sourcePublication, modelCloningContext)).andReturn(dummy);
		expect(modelService.create(sourcePublication.getClass())).andReturn(example);
		expect(flexibleSearchService.getModelByExample(example)).andThrow(new ModelNotFoundException(""));

		final List<ChapterModel> rootChapterCopies = new ArrayList<ChapterModel>();
		for (final ChapterModel rootChapter : sourcePublication.getRootChapters())
		{
			// getChapterCopy
			final ChapterModel chapter = new ChapterModel();
			getContext(chapter).setLocaleProvider(localeProvider);

			expect(modelService.clone(rootChapter, modelCloningContext)).andReturn(chapter);
			expect(modelService.getSource(dummy)).andReturn(pubMock);

			// We don't want the PrintManager to be fetched...
			expect(pubMock.getPublicationElementById(idMap.get(rootChapter.getId()))).andReturn(null);

			for (final PageModel pageModel : rootChapter.getPages())
			{
				// getPageCopy
				final PageModel page = new PageModel();
				getContext(page).setLocaleProvider(localeProvider);

				expect(modelService.clone(pageModel, modelCloningContext)).andReturn(page);
				expect(modelService.getSource(dummy)).andReturn(pubMock);

				// We don't want the PrintManager to be fetched...
				expect(pubMock.getPublicationElementById(idMap.get(pageModel.getId()))).andReturn(null);

				for (final PlacementModel placementModel : pageModel.getPlacements())
				{
					// getPlacementCopy
					expect(modelService.clone(placementModel, modelCloningContext)).andReturn(new ItemPlacementModel());
					expect(modelService.getSource(dummy)).andReturn(pubMock);

					// We don't want the PrintManager to be fetched...
					expect(pubMock.getPublicationElementById(idMap.get(placementModel.getId()))).andReturn(null);
				}
			}

			rootChapterCopies.add(chapter);
		}

		for (final ChapterModel rootchapterCopy : rootChapterCopies)
		{
			// getPublicationCopy
			expect(modelService.getSource(rootchapterCopy)).andReturn(chapMock);
		}

		replay(flexibleSearchService);
		replay(modelService);
		replay(pubMock);

		final PublicationModel result = cloningService.getPublicationCopy(sourcePublication);
		// We then verify that the result returned from the service is suffixed with a copy
		assertEquals("Should have '" + SOURCE_CODE + "_copy" + "' as code", SOURCE_CODE + "_copy", result.getCode());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(flexibleSearchService);
		verify(modelService);
		verify(pubMock);
	}

	@Test
	public void testCopyPublicationWithOneCopy() throws ValueHandlerPermissionException, ValueHandlerException
	{
		// getPublicationCopy
		final PublicationModel dummy = new PublicationModel();
		getContext(dummy).setLocaleProvider(localeProvider);

		final PublicationModel example = new PublicationModel();
		getContext(example).setLocaleProvider(localeProvider);

		final Publication pubMock = createMock(Publication.class);
		final Chapter chapMock = createMock(Chapter.class);

		final PublicationModel found = new PublicationModel();
		getContext(found).setLocaleProvider(localeProvider);
		found.setCode(COPY1_CODE);

		expect(modelService.clone(sourcePublication, modelCloningContext)).andReturn(dummy);
		expect(modelService.create(sourcePublication.getClass())).andReturn(example);
		expect(flexibleSearchService.getModelByExample(example)).andReturn(found);
		expect(flexibleSearchService.getModelByExample(example)).andThrow(new ModelNotFoundException(""));

		final List<ChapterModel> rootChapterCopies = new ArrayList<ChapterModel>();
		for (final ChapterModel rootChapter : sourcePublication.getRootChapters())
		{
			// getChapterCopy
			final ChapterModel chapter = new ChapterModel();
			getContext(chapter).setLocaleProvider(localeProvider);

			expect(modelService.clone(rootChapter, modelCloningContext)).andReturn(chapter);
			expect(modelService.getSource(dummy)).andReturn(pubMock);

			// We don't want the PrintManager to be fetched...
			expect(pubMock.getPublicationElementById(idMap.get(rootChapter.getId()))).andReturn(null);

			for (final PageModel pageModel : rootChapter.getPages())
			{
				// getPageCopy
				final PageModel page = new PageModel();
				getContext(page).setLocaleProvider(localeProvider);

				expect(modelService.clone(pageModel, modelCloningContext)).andReturn(page);
				expect(modelService.getSource(dummy)).andReturn(pubMock);

				// We don't want the PrintManager to be fetched...
				expect(pubMock.getPublicationElementById(idMap.get(pageModel.getId()))).andReturn(null);

				for (final PlacementModel placementModel : pageModel.getPlacements())
				{
					// getPlacementCopy
					expect(modelService.clone(placementModel, modelCloningContext)).andReturn(new ItemPlacementModel());
					expect(modelService.getSource(dummy)).andReturn(pubMock);

					// We don't want the PrintManager to be fetched...
					expect(pubMock.getPublicationElementById(idMap.get(placementModel.getId()))).andReturn(null);
				}
			}

			rootChapterCopies.add(chapter);
		}

		for (final ChapterModel rootchapterCopy : rootChapterCopies)
		{
			// getPublicationCopy
			expect(modelService.getSource(rootchapterCopy)).andReturn(chapMock);
		}

		replay(flexibleSearchService);
		replay(modelService);
		replay(pubMock);

		final PublicationModel result = cloningService.getPublicationCopy(sourcePublication);
		// We then verify that the result returned from the service is suffixed with a copy
		assertEquals("Should have '" + SOURCE_CODE + "_copy" + "_1' as code", SOURCE_CODE + "_copy_1", result.getCode());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(flexibleSearchService);
		verify(modelService);
		verify(pubMock);
	}

	@Test
	public void testCopyPublicationWithTwoCopies() throws ValueHandlerPermissionException, ValueHandlerException
	{
		// getPublicationCopy
		final PublicationModel dummy = new PublicationModel();
		getContext(dummy).setLocaleProvider(localeProvider);

		final PublicationModel example = new PublicationModel();
		getContext(example).setLocaleProvider(localeProvider);

		final Publication pubMock = createMock(Publication.class);
		final Chapter chapMock = createMock(Chapter.class);

		final PublicationModel found1 = new PublicationModel();
		getContext(found1).setLocaleProvider(localeProvider);
		found1.setCode(COPY1_CODE);

		final PublicationModel found2 = new PublicationModel();
		getContext(found2).setLocaleProvider(localeProvider);
		found2.setCode(COPY2_CODE);

		expect(modelService.clone(sourcePublication, modelCloningContext)).andReturn(dummy);
		expect(modelService.create(sourcePublication.getClass())).andReturn(example);
		expect(flexibleSearchService.getModelByExample(example)).andReturn(found1);
		expect(flexibleSearchService.getModelByExample(example)).andReturn(found2);
		expect(flexibleSearchService.getModelByExample(example)).andThrow(new ModelNotFoundException(""));

		final List<ChapterModel> rootChapterCopies = new ArrayList<ChapterModel>();
		for (final ChapterModel rootChapter : sourcePublication.getRootChapters())
		{
			// getChapterCopy
			final ChapterModel chapter = new ChapterModel();
			getContext(chapter).setLocaleProvider(localeProvider);

			expect(modelService.clone(rootChapter, modelCloningContext)).andReturn(chapter);
			expect(modelService.getSource(dummy)).andReturn(pubMock);

			// We don't want the PrintManager to be fetched...
			expect(pubMock.getPublicationElementById(idMap.get(rootChapter.getId()))).andReturn(null);

			for (final PageModel pageModel : rootChapter.getPages())
			{
				// getPageCopy
				final PageModel page = new PageModel();
				getContext(page).setLocaleProvider(localeProvider);

				expect(modelService.clone(pageModel, modelCloningContext)).andReturn(page);
				expect(modelService.getSource(dummy)).andReturn(pubMock);

				// We don't want the PrintManager to be fetched...
				expect(pubMock.getPublicationElementById(idMap.get(pageModel.getId()))).andReturn(null);

				for (final PlacementModel placementModel : pageModel.getPlacements())
				{
					// getPlacementCopy
					expect(modelService.clone(placementModel, modelCloningContext)).andReturn(new ItemPlacementModel());
					expect(modelService.getSource(dummy)).andReturn(pubMock);

					// We don't want the PrintManager to be fetched...
					expect(pubMock.getPublicationElementById(idMap.get(placementModel.getId()))).andReturn(null);
				}
			}

			rootChapterCopies.add(chapter);
		}

		for (final ChapterModel rootchapterCopy : rootChapterCopies)
		{
			// getPublicationCopy
			expect(modelService.getSource(rootchapterCopy)).andReturn(chapMock);
		}

		replay(flexibleSearchService);
		replay(modelService);
		replay(pubMock);

		final PublicationModel result = cloningService.getPublicationCopy(sourcePublication);
		// We then verify that the result returned from the service is suffixed with a copy
		assertEquals("Should have '" + SOURCE_CODE + "_copy" + "_2' as code", SOURCE_CODE + "_copy_2", result.getCode());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(flexibleSearchService);
		verify(modelService);
		verify(pubMock);
	}

	@Test
	public void testCopyChapter()
	{
		final Publication pubMock = createMock(Publication.class);

		// getChapterCopy
		final ChapterModel chapter = new ChapterModel();
		chapter.setCode(sourceChapter.getCode());
		getContext(chapter).setLocaleProvider(localeProvider);

		expect(modelService.clone(sourceChapter, modelCloningContext)).andReturn(chapter);
		expect(modelService.getSource(targetPublication)).andReturn(pubMock);

		// We don't want the PrintManager to be fetched...
		expect(pubMock.getPublicationElementById(idMap.get(sourceChapter.getId()))).andReturn(null);

		for (final PageModel pageModel : sourceChapter.getPages())
		{
			// getPageCopy
			final PageModel page = new PageModel();
			getContext(page).setLocaleProvider(localeProvider);

			expect(modelService.clone(pageModel, modelCloningContext)).andReturn(page);
			expect(modelService.getSource(targetPublication)).andReturn(pubMock);

			// We don't want the PrintManager to be fetched...
			expect(pubMock.getPublicationElementById(idMap.get(pageModel.getId()))).andReturn(null);

			for (final PlacementModel placementModel : pageModel.getPlacements())
			{
				// getPlacementCopy
				expect(modelService.clone(placementModel, modelCloningContext)).andReturn(new ItemPlacementModel());
				expect(modelService.getSource(targetPublication)).andReturn(pubMock);

				// We don't want the PrintManager to be fetched...
				expect(pubMock.getPublicationElementById(idMap.get(placementModel.getId()))).andReturn(null);
			}
		}

		replay(modelService);
		replay(pubMock);

		// Make the call to PublicationCloningService's getChapterCopy
		final ChapterModel result = cloningService.getChapterCopy(sourceChapter, targetPublication);

		// Then verify that the result has the same code as the source chapter
		assertEquals("Both chapters should have the same code", sourceChapter.getCode(), result.getCode());
		// .. and it is attached to the target publication
		assertEquals("Copy of source chapter should now belong to the target publication", targetPublication,
				result.getPublication());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(modelService);
		verify(pubMock);
	}

	@Test
	public void testCopyPage()
	{
		final Publication pubMock = createMock(Publication.class);

		// getPageCopy
		final PageModel page = new PageModel();
		page.setCode(sourcePage.getCode());
		getContext(page).setLocaleProvider(localeProvider);

		expect(modelService.clone(sourcePage, modelCloningContext)).andReturn(page);
		expect(modelService.getSource(targetPublication)).andReturn(pubMock);

		// We don't want the PrintManager to be fetched...
		expect(pubMock.getPublicationElementById(idMap.get(sourcePage.getId()))).andReturn(null);

		for (final PlacementModel placementModel : sourcePage.getPlacements())
		{
			// getPlacementCopy
			expect(modelService.clone(placementModel, modelCloningContext)).andReturn(new ItemPlacementModel());
			expect(modelService.getSource(targetPublication)).andReturn(pubMock);

			// We don't want the PrintManager to be fetched...
			expect(pubMock.getPublicationElementById(idMap.get(placementModel.getId()))).andReturn(null);
		}

		replay(modelService);
		replay(pubMock);

		// Make the call to PublicationCloningService's getPageCopy
		final PageModel result = cloningService.getPageCopy(sourcePage, targetChapter);

		// Then verify that the result has the same code as the source page
		assertEquals("Both pages should have the same code", sourcePage.getCode(), result.getCode());
		// .. and it is attached to the target chapter
		assertEquals("Copy of source page should now belong to the target chapter", targetChapter, result.getChapter());
		// .. and the target publication
		assertEquals("Copy of source page should now belong to the target publication", targetChapter.getPublication(),
				result.getPublication());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(modelService);
		verify(pubMock);
	}

	@Test
	public void testCopyPlacement()
	{
		final Publication pubMock = createMock(Publication.class);

		// getPlacementCopy
		expect(modelService.clone(sourcePlacement, modelCloningContext)).andReturn(new ItemPlacementModel());
		expect(modelService.getSource(targetPublication)).andReturn(pubMock);

		// We don't want the PrintManager to be fetched...
		expect(pubMock.getPublicationElementById(idMap.get(sourcePlacement.getId()))).andReturn(null);

		replay(modelService);
		replay(pubMock);

		// Make the call to PublicationCloningService's getPlacementCopy
		final PlacementModel result = cloningService.getPlacementCopy(sourcePlacement, targetPage);

		// Then verify the result is an instance of the same type as the source placement
		assertTrue(result instanceof ItemPlacementModel);

		if (result instanceof ItemPlacementModel)
		{
			// .. and has the same item
			assertEquals("Both placements should have the same item", sourcePlacement.getItem(),
					((ItemPlacementModel) result).getItem());
		}

		// .. and it is attached to the target page
		assertEquals("Copy of source placement should now belong to the target page", targetPage, result.getPage());
		// .. and the target chapter
		assertEquals("Copy of source placement should now belong to the target chapter", targetPage.getChapter(), result.getPage()
				.getChapter());
		// .. and the target publication
		assertEquals("Copy of source placement should now belong to the target publication", targetPage.getPublication(),
				result.getPublication());

		// Finally we ask EasyMock to confirm that a call to the modelService's create was made somewhere above
		verify(modelService);
		verify(pubMock);
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}

}
