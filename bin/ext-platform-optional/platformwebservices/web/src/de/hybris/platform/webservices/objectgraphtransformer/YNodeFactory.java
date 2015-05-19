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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class YNodeFactory<S, T extends ItemModel> implements NodeFactory<S, T>
{
	private static final Set<Class> silentExceptions = new HashSet<Class>(Arrays.asList( //
			UnknownIdentifierException.class, //
			PKException.class, //
			ModelLoadingException.class, //
			JaloItemNotFoundException.class, //
			ClassCastException.class, //
			IllegalArgumentException.class, //
			ModelNotFoundException.class));

	// Exceptions which are thrown because of invalid dto (xml)
	private static final Set<Class> badRequestExceptions = new HashSet<Class>(Arrays.asList( //
			AmbiguousIdentifierException.class));


	@Override
	public T getValue(final NodeContext ctx, final S dto)
	{
		T result = null;
		final YObjectGraphContext yCtx = (YObjectGraphContext) ctx.getGraphContext();
		try
		{
			result = getModel(yCtx, ctx, dto);
		}
		catch (final RuntimeException e)
		{
			if (silentExceptions.contains(e.getClass()))
			{ //NOPMD
			  // silent catch
			  // these are exceptions which (unluckily) are used to trigger business logic
			  // it's the only way to detect whether a Model is available under given DTO properties
			  // (servicelayer is very restrictive when requesting models which can't be found) 
			}
			else
			{
				if (badRequestExceptions.contains(e.getClass()))
				{
					throw new BadRequestException("Error getting entity for current request body", e);
				}
				else
				{
					// the problematic exceptions like NPE etc. are just delegated
					throw e;
				}
			}
		}

		// set a flag when root node has to be created 
		if (ctx.getRealDistance() == 0)
		{
			yCtx.setGraphWasNewlyCreated(Boolean.valueOf(result == null));
		}


		return result;
	}

	protected abstract T getModel(YObjectGraphContext ctx, NodeContext nodeCtx, S dto);

}
