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

package de.hybris.platform.print.model;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.jalo.table.CellEntry;
import de.hybris.platform.print.jalo.table.ColumnEntry;
import de.hybris.platform.print.jalo.table.RowEntry;
import de.hybris.platform.print.jalo.table.TableBlock;
import de.hybris.platform.print.model.table.CellEntryModel;
import de.hybris.platform.print.model.table.ColumnEntryModel;
import de.hybris.platform.print.model.table.RowEntryModel;
import de.hybris.platform.print.model.table.TableBlockModel;
import de.hybris.platform.print.tests.AbstractPrintServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.Map;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

@IntegrationTest
public class CellEntryConsistencyIntegrationTest extends AbstractPrintServicelayerTest
{

	@Test
	public void testCellEntryTableBlockCVConsistency()
	{
		final CatalogModel catalogOne = prepareCatalog("One");
		final CatalogVersionModel oneStaged = prepareCatalogVersion(catalogOne, "Staged", false);

		final CatalogModel catalogTwo = prepareCatalog("Two");
		final CatalogVersionModel twoStaged = prepareCatalogVersion(catalogTwo, "Staged", false);

		getModelService().saveAll(catalogOne, catalogTwo, oneStaged, twoStaged);

		final TableBlockModel tableBlock = prepareTableBlock(oneStaged);

		final CellEntryModel cellEntry = prepareCellEntry(twoStaged, tableBlock);

		try
		{
			getModelService().saveAll();
			Assert.fail("Interceptor should have thrown exception...");
		}
		catch (final ModelSavingException mse)
		{
			//Expected
		}

		cellEntry.setCatalogVersion(oneStaged);
		cellEntry.getColumn().setCatalogVersion(oneStaged);
		cellEntry.getRow().setCatalogVersion(oneStaged);
		getModelService().saveAll(tableBlock, cellEntry);

		getModelService().refresh(tableBlock);
		getModelService().refresh(cellEntry);

		Assertions.assertThat(tableBlock.getCatalogVersion()).isSameAs(oneStaged);
		Assertions.assertThat(tableBlock.getCatalogVersion()).isSameAs(cellEntry.getCatalogVersion());

		tableBlock.setCatalogVersion(twoStaged);
		try
		{
			getModelService().save(tableBlock);
			Assert.fail("Interceptor should have thrown exception...");
		}
		catch (final ModelSavingException mse)
		{
			//Expected
		}
	}

	@Test
	public void testJaloCreationAndModelLoadOfCellEntryAndTableBlock()
	{
		final TableBlockModel tableModel = getModelService().get(prepareInconsistentTableBlockJalo().getPK());
		for (final CellEntryModel cell : tableModel.getCells())
		{
			Assertions.assertThat(cell.getCatalogVersion()).isSameAs(tableModel.getCatalogVersion());
		}

		final TableBlock tableBlock = prepareInconsistentTableBlockJalo();
		final PK cvPK = tableBlock.getCatalogVersion().getPK();
		for (final CellEntry cellEntry : tableBlock.getCells())
		{
			final CellEntryModel cellEntryModel = getModelService().get(cellEntry.getPK());
			Assertions.assertThat(cellEntryModel.getCatalogVersion().getPk()).isEqualTo(cvPK);
		}

	}

	private TableBlock prepareInconsistentTableBlockJalo()
	{
		final Map<String, Object> cParams = Maps.newHashMap();
		cParams.put(Catalog.ID, "Some");
		final Catalog someCatalog = CatalogManager.getInstance().createCatalog(cParams);

		Map<String, Object> cvParams = Maps.newHashMap();
		cvParams.put(CatalogVersion.CATALOG, someCatalog);
		cvParams.put(CatalogVersion.VERSION, "Staged");
		final CatalogVersion oneStaged = CatalogManager.getInstance().createCatalogVersion(cvParams);

		cvParams = Maps.newHashMap();
		cvParams.put(CatalogVersion.CATALOG, someCatalog);
		cvParams.put(CatalogVersion.VERSION, "Online");
		final CatalogVersion oneOnline = CatalogManager.getInstance().createCatalogVersion(cvParams);

		final Map<String, Object> tbParams = Maps.newHashMap();
		tbParams.put(TableBlock.CODE, generateRandomCode("table-block-jalo"));
		tbParams.put(TableBlock.CATALOGVERSION, oneStaged);
		final TableBlock tableBlock = PrintManager.getInstance().createTableBlock(tbParams);

		final Map<String, Object> colParams = Maps.newHashMap();
		colParams.put(ColumnEntry.CATALOGVERSION, oneStaged);
		colParams.put(ColumnEntry.TABLE, tableBlock);
		colParams.put(ColumnEntry.ID, generateRandomCode("column"));
		final ColumnEntry column = PrintManager.getInstance().createColumnEntry(colParams);

		final Map<String, Object> rowParams = Maps.newHashMap();
		rowParams.put(ColumnEntry.CATALOGVERSION, oneStaged);
		rowParams.put(ColumnEntry.TABLE, tableBlock);
		rowParams.put(ColumnEntry.ID, generateRandomCode("row"));
		final RowEntry row = PrintManager.getInstance().createRowEntry(rowParams);

		final Map<String, Object> ceParams = Maps.newHashMap();
		ceParams.put(CellEntry.TABLE, tableBlock);
		ceParams.put(CellEntry.CATALOGVERSION, oneOnline);
		ceParams.put(CellEntry.COLUMN, column);
		ceParams.put(CellEntry.ROW, row);
		PrintManager.getInstance().createCellEntry(ceParams);

		return tableBlock;
	}

	private TableBlockModel prepareTableBlock(final CatalogVersionModel oneStaged)
	{
		final TableBlockModel tableBlock = getModelService().create(TableBlockModel.class);
		tableBlock.setCode(generateRandomCode("table-block"));
		tableBlock.setCatalogVersion(oneStaged);
		return tableBlock;
	}

	private CellEntryModel prepareCellEntry(final CatalogVersionModel twoStaged, final TableBlockModel tableBlock)
	{
		final CellEntryModel cellEntry = getModelService().create(CellEntryModel.class);
		cellEntry.setTable(tableBlock);
		cellEntry.setCatalogVersion(twoStaged);
		cellEntry.setColumn(prepareColumn(twoStaged, tableBlock));
		cellEntry.setRow(prepareRow(twoStaged, tableBlock));
		return cellEntry;
	}

	private RowEntryModel prepareRow(final CatalogVersionModel catalogVersion, final TableBlockModel table)
	{
		final RowEntryModel row = getModelService().create(RowEntryModel.class);
		row.setCatalogVersion(catalogVersion);
		row.setId(generateRandomCode("row"));
		row.setTable(table);
		return row;
	}

	private ColumnEntryModel prepareColumn(final CatalogVersionModel catalogVersion, final TableBlockModel table)
	{
		final ColumnEntryModel column = getModelService().create(ColumnEntryModel.class);
		column.setCatalogVersion(catalogVersion);
		column.setId(generateRandomCode("column"));
		column.setTable(table);
		return column;
	}

	private String generateRandomCode(final String prefix)
	{
		return String.format("%s-%s", prefix, Long.toString(System.nanoTime(), 24));
	}

	private CatalogVersionModel prepareCatalogVersion(final CatalogModel catalog, final String version, final boolean active)
	{
		final CatalogVersionModel oneStaged = getModelService().create(CatalogVersionModel.class);
		oneStaged.setActive(active);
		oneStaged.setCatalog(catalog);
		oneStaged.setVersion(version);
		return oneStaged;
	}

	private CatalogModel prepareCatalog(final String id)
	{
		final CatalogModel catalogOne = getModelService().create(CatalogModel.class);
		catalogOne.setId(id);
		return catalogOne;
	}
}