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
 */

package de.hybris.platform.printcockpit.model.table.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.print.model.table.CellEntryModel;
import de.hybris.platform.print.model.table.ColumnEntryModel;
import de.hybris.platform.print.model.table.RowEntryModel;
import de.hybris.platform.print.model.table.TableBlockModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class TableServiceImplTest extends ServicelayerTransactionalTest
{

	private TableServiceImpl tableService = new TableServiceImpl();

	@Mock
	private ModelHelper mockModelHelper;

	@Resource
	private ModelService modelService;

	@Mock
	private ModelService mockModelService;

	@Resource
	private CatalogService catalogService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(mockModelService.create(CellEntryModel.class)).thenReturn(new CellEntryModel());
		tableService.setModelService(mockModelService);
		tableService.setModelHelper(mockModelHelper);
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testCreateCell() throws ValueHandlerException
	{
		final CatalogVersionModel inactiveCatalogVersion = findInactiveCatalogVersion();
		final TableBlockModel table = prepareTableBlock(inactiveCatalogVersion);
		tableService.createCell(table, prepareColumn(inactiveCatalogVersion, table),
				prepareRow(inactiveCatalogVersion, table));
		Mockito.verify(mockModelHelper).saveModel(Matchers.argThat(new BaseMatcher<ItemModel>()
		{

			@Override
			public boolean matches(final Object entry)
			{
				if (entry instanceof CellEntryModel)
				{
					return ObjectUtils.equals(((CellEntryModel) entry).getCatalogVersion(), inactiveCatalogVersion);
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				//NOPMD
			}

		}), Matchers.anyBoolean(), Matchers.anyBoolean());
	}

	private CatalogVersionModel findInactiveCatalogVersion()
	{
		return catalogService.getDefaultCatalog().getActiveCatalogVersion();
//
//		final CatalogVersionModel activeCatalogVersion = catalogService.getDefaultCatalog().getActiveCatalogVersion();
//		for (final CatalogVersionModel catalogVersionModel : catalogService.getDefaultCatalog().getCatalogVersions())
//		{
//			if (catalogVersionModel != activeCatalogVersion)
//			{
//				return catalogVersionModel;
//			}
//		}
//		throw new IllegalStateException("At least one inactive catalog version expected");
	}

	private TableBlockModel prepareTableBlock(final CatalogVersionModel oneStaged)
	{
		final TableBlockModel tableBlock = modelService.create(TableBlockModel.class);
		tableBlock.setCode("table-block-" + System.nanoTime());
		tableBlock.setCatalogVersion(oneStaged);
		return tableBlock;
	}

	private CellEntryModel prepareCellEntry(final CatalogVersionModel twoStaged, final TableBlockModel tableBlock)
	{
		final CellEntryModel cellEntry = modelService.create(CellEntryModel.class);
		cellEntry.setTable(tableBlock);
		cellEntry.setCatalogVersion(twoStaged);
		cellEntry.setColumn(prepareColumn(twoStaged, tableBlock));
		cellEntry.setRow(prepareRow(twoStaged, tableBlock));
		return cellEntry;
	}

	private RowEntryModel prepareRow(final CatalogVersionModel catalogVersion, final TableBlockModel table)
	{
		final RowEntryModel row = modelService.create(RowEntryModel.class);
		row.setCatalogVersion(catalogVersion);
		row.setId("row-" + System.nanoTime());
		row.setTable(table);
		return row;
	}

	private ColumnEntryModel prepareColumn(final CatalogVersionModel catalogVersion, final TableBlockModel table)
	{
		final ColumnEntryModel column = modelService.create(ColumnEntryModel.class);
		column.setCatalogVersion(catalogVersion);
		column.setId("column-" + System.nanoTime());
		column.setTable(table);
		return column;
	}
}
