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
package de.hybris.platform.printcockpit.pagemanagment;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.ChapterModel;
import de.hybris.platform.print.model.CometConfigurationModel;
import de.hybris.platform.print.model.GridElementModel;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.ItemPlacementModel;
import de.hybris.platform.print.model.LayoutTemplateModel;
import de.hybris.platform.print.model.PageFormatModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.print.model.PublicationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;


@Ignore
public abstract class AbstractPagePlannerIntegrationTest extends ServicelayerTransactionalTest
{

	protected int uniqueCodeCounter = 0;

	protected LayoutTemplateModel defaultTemplate = null;

	@Resource
	protected PagePlannerRegistry pagePlannerRegistry;

	@Resource
	protected ModelService modelService;

	@Resource
	protected UserService userService;

	@Resource
	protected CatalogVersionService catalogVersionService;
	@Resource
	protected CatalogService catalogService;

	protected CatalogVersionModel catalogVersion;
	protected CometConfigurationModel cometConfig;
	protected ChapterModel chapter;
	protected PublicationModel publication;
	protected PageFormatModel pageFormat;

	@Before
	public void setUp() throws Exception
	{
		createCometConfiguration();
		createLayoutTemplate();
		createCatalogVersion();
		createPageFormat();
		createPublication();
		createChapter();
	}


	protected GridModel prepareGrid(final int gridElementsCount, final int id)
	{
		final GridModel grid = modelService.create(GridModel.class);

		grid.setCometConfig(cometConfig);
		grid.setId(Integer.valueOf(id));

		final List<GridElementModel> gridElements = new ArrayList<GridElementModel>(gridElementsCount);

		for (int i = 0; i < gridElementsCount; i++)
		{
			final GridElementModel gridElement = modelService.create(GridElementModel.class);
			gridElement.setDefaultLayoutTemplate(defaultTemplate);
			gridElement.setId(Integer.valueOf(i));
			gridElement.setGrid(grid);
			modelService.save(gridElement);
			gridElements.add(gridElement);
		}

		grid.setElements(gridElements);
		modelService.save(grid);
		return grid;
	}

	protected void fillAllSlotsWithPlacements(final PageModel page)
	{
		final GridModel grid = page.getGrid();
		final int elementsCnt = grid.getElements().size();

		int pages = 1;
		if (page.getPageMode() == PageMode.DOUBLE)
		{
			pages = 2;
		}
		else if (page.getPageMode() == PageMode.SEQUENCE)
		{
			pages = 3;
		}
		final List<PlacementModel> placements = new ArrayList<PlacementModel>();
		page.setPlacements(placements);
		for (int pageNo = 0; pageNo < pages; pageNo++)
		{
			for (int gridId = 1; gridId <= elementsCnt; gridId++)
			{
				final PlacementModel placement = preparePlacement();
				placement.setPage(page);
				placement.setPageNumber(Integer.valueOf(pageNo));
				placement.setGridElementId(Integer.valueOf(gridId));
				placements.add(placement);
			}
		}
	}

	protected PageModel preparePage(final PageMode pageMode, final GridModel grid)
	{
		return preparePage(pageMode, grid, getDefaultGridModeForTest());
	}

	protected GridMode getDefaultGridModeForTest()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Be sure to implement getDefaultGridModeForTest(). Otherwise UnsupportedOperationException will be thrown.
	 * @see de.hybris.platform.printcockpit.pagemanagment.AbstractPagePlannerIntegrationTest.getDefaultGridModeForTest()
	 */
	protected PageModel preparePage(final PageMode pageMode, final GridModel grid, final GridMode defaultGridMode)
	{
		final PageModel page = modelService.create(PageModel.class);
		page.setPageMode(pageMode);
		page.setGrid(grid);
		page.setGridMode(defaultGridMode);
		page.setPlacements(new ArrayList<PlacementModel>());

		page.setCode("TestPage" + System.currentTimeMillis());
		page.setChapter(chapter);
		page.setPublication(publication);
		page.setId("TestPageID" + System.currentTimeMillis());

		modelService.save(page);
		return page;
	}

	protected PlacementModel preparePlacement()
	{
		final ItemPlacementModel placement = modelService.create(ItemPlacementModel.class);
		placement.setItem(prepareProduct());
		placement.setLayoutTemplate(defaultTemplate);
		placement.setPublication(publication);
		modelService.save(placement);
		return placement;
	}

	protected ProductModel prepareProduct()
	{
		final ProductModel res = new ProductModel();
		res.setCode("testProductPlacements_" + uniqueCodeCounter++);
		res.setCatalogVersion(catalogVersion);
		modelService.save(res);
		return res;
	}

	protected void createChapter()
	{
		chapter = modelService.create(ChapterModel.class);
		chapter.setCode("TestChapter");
		chapter.setPublication(publication);
		modelService.save(chapter);
	}

	protected void createPublication()
	{
		publication = modelService.create(PublicationModel.class);
		publication.setCode("TestPublication");
		publication.setPageFormat(pageFormat);
		publication.setConfiguration(cometConfig);
		modelService.save(publication);
	}

	protected void createPageFormat()
	{
		pageFormat = modelService.create(PageFormatModel.class);
		pageFormat.setName("TestPageFormat");
		pageFormat.setWidth(Double.valueOf(10d));
		pageFormat.setHeight(Double.valueOf(10d));
		pageFormat.setQualifier("testQualifier");
		modelService.save(pageFormat);
	}

	protected void createCatalogVersion()
	{
		CatalogModel catalog = null;
		try
		{
			catalog = catalogService.getCatalogForId("testCatalog");
		}
		catch (final UnknownIdentifierException e)
		{
			catalog = modelService.create(CatalogModel.class);
			catalog.setId("testCatalog");
			modelService.save(catalog);
		}
		catch (final AmbiguousIdentifierException e1)
		{
			throw new JaloSystemException(e1);
		}

		try
		{
			catalogVersionService.getCatalogVersion("testCatalog", "Demo");
		}
		catch (final UnknownIdentifierException e)
		{
			catalogVersion = modelService.create(CatalogVersionModel.class);
			catalogVersion.setCatalog(catalog);
			catalogVersion.setActive(Boolean.TRUE);
			catalogVersion.setVersion("Demo");
			modelService.save(catalogVersion);
		}
		catch (final AmbiguousIdentifierException e1)
		{
			throw new JaloSystemException(e1);
		}
	}

	protected void createLayoutTemplate()
	{
		defaultTemplate = modelService.create(LayoutTemplateModel.class);
		defaultTemplate.setCode("TestTemplate");
		defaultTemplate.setCometConfig(cometConfig);
		defaultTemplate.setId(Integer.valueOf(1));
	}

	protected void createCometConfiguration()
	{
		cometConfig = modelService.create(CometConfigurationModel.class);
		cometConfig.setCode("TestCometConfiguration");
		modelService.save(cometConfig);
	}

	protected CommentModel prepareComment(final String code, final String subject, final int pageIndex, final int x, final int y)
	{
		final CommentModel comment = modelService.create(CommentModel.class);
		comment.setCode(code);
		comment.setText(subject + "_text");

		final DomainModel domain = modelService.<DomainModel> create(DomainModel.class);
		domain.setCode(code + "_domain");

		final ComponentModel component = modelService.<ComponentModel> create(ComponentModel.class);
		component.setCode(code + "_component");
		component.setDomain(domain);

		final CommentTypeModel type = modelService.<CommentTypeModel> create(CommentTypeModel.class);
		type.setCode(code + "_commentType");
		type.setComments(Collections.singletonList(comment));
		type.setDomain(domain);

		comment.setCommentType(type);
		comment.setSubject(subject);
		comment.setOwner(userService.getCurrentUser());
		comment.setAuthor(userService.getCurrentUser());
		comment.setComponent(component);

		final CommentMetadataModel meta = modelService.create(CommentMetadataModel.class);
		meta.setPageIndex(Integer.valueOf(pageIndex));
		meta.setX(Integer.valueOf(x));
		meta.setY(Integer.valueOf(y));
		meta.setComment(comment);

		comment.setCommentMetadata(Collections.singleton(meta));

		return comment;
	}
}
