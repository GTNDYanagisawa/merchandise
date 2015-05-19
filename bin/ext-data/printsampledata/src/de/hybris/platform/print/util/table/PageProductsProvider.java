/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.print.util.table;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.print.constants.PrintConstants;
import de.hybris.platform.print.jalo.ItemPlacement;
import de.hybris.platform.print.jalo.Page;
import de.hybris.platform.print.jalo.Placement;
import de.hybris.platform.print.jalo.table.AxisEntry;
import de.hybris.platform.print.jalo.table.TableBlock;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DataProvider that provides all Products that are placed on a Page that is given in the global table parameter
 *
 * @author  rri
 */
public class PageProductsProvider implements AxisDataProvider
{

	public class Param
	{
		public static final String PAGE = "page";
		public static final String PRODUCTS_ONLY = "productsOnly";
	}

	private List<Item> data;
	private List<ParameterDescriptor> params;


	@Override
	public Class getDataType()
	{
		return Item.class;
	}

	@Override
	public String getDisplayName()
	{
		return Localization.getLocalizedString("print.axisdataprovider.pageproductsprovider");
	}

	@Override
	public List<ParameterDescriptor> getParameters()
	{
		if (this.params == null)
		{
			this.params = new ArrayList<ParameterDescriptor>(1);
			this.params.add(new ParameterDescriptor(Param.PAGE, "print.pageproduct.param.page", PrintConstants.TC.PAGE, ParameterDescriptor.Scope.GLOBAL));
			this.params.add(new ParameterDescriptor(Param.PRODUCTS_ONLY, "print.pageproduct.param.productsonly", "java.lang.Boolean", ParameterDescriptor.Scope.LOCAL));
		}
		return this.params;
	}

	@Override
	public void init(final TableBlock table, final AxisEntry axis, final Map<String, Object> params)
	{
		// make sure, parameter PRODUCTS_ONLY is TRUE by default
//		if( !params.containsKey(Param.PRODUCTS_ONLY) )
//		{
//			params.put(Param.PRODUCTS_ONLY, Boolean.TRUE);
//		}

		final Page page = (Page) params.get(Param.PAGE);
		if (page == null)
		{
			this.data = new ArrayList<Item>();
		}
		else
		{
			final ComposedType itemPlacementType = TypeManager.getInstance().getComposedType("ItemPlacement");
			final ComposedType productType = TypeManager.getInstance().getComposedType("Product");

			final List<Placement> placements = page.getPlacements();
			this.data = new ArrayList<Item>(placements.size());

			for (final Placement placement : placements)
			{
				if( itemPlacementType.isAssignableFrom(placement.getComposedType()) )
				{
					final Item item = ((ItemPlacement)placement).getItem();
					if( ((Boolean)params.get(Param.PRODUCTS_ONLY)).booleanValue()
							&&  productType.isAssignableFrom(item.getComposedType()) )
					{
						// add the item only if it is a product
						this.data.add(item);
					}
					else
					{
						// add the item in any case
						this.data.add(item);
					}
				}
			}
		}
	}

	@Override
	public int getSize()
	{
		return this.data == null ? 0 : this.data.size();
	}

	@Override
	public Object getDataAt(final int index)
	{
		return ((this.data != null) && (index < this.data.size()) ? this.data.get(index) : null);
	}

}
