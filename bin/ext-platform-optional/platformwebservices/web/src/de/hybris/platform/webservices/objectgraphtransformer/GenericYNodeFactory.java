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

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.InternalServerErrorException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class GenericYNodeFactory extends YNodeFactory<Object, ItemModel>
{

	@Override
	protected ItemModel getModel(final YObjectGraphContext ctx, final NodeContext nodeCtx, final Object dto)
	{
		// get all properties which are flagged 'unique' (GraphNode annotation; property: uidproperties) 
		final PropertyConfig[] propCfgList = nodeCtx.getNodeMapping().getSourceConfig().getUidProperties();

		// fetch DTO getter
		final Method dtoGetter = propCfgList[0].getReadMethod();
		final Object value;
		try
		{
			// invoke getter; get property value 
			value = dtoGetter.invoke(dto, (Object[]) null);
		}
		catch (final Exception e)
		{
			// exception handling should be reviewed
			throw new InternalServerErrorException(e);
		}
		//POST method
		if (value == null)
		{
			return null;
		}

		final ItemModel result;

		// get already computed unique information from URL for created resource
		final ItemModel notsavedModelForCreate = ((AbstractYResource) ctx.getRequestResource()).getNotsavedModelForCreate();
		if (notsavedModelForCreate != null && isRootInputDto((AbstractYResource) ctx.getRequestResource(), dto, value))
		{
			result = ctx.getServices().getFlexibleSearchService().getModelByExample(notsavedModelForCreate);
		}
		// for all nested unique resources included within input dto
		else
		{
			//null, model or ModelNotFoundException
			result = getModelFromDto(ctx, dto);
		}
		return result;
	}

	private boolean isRootInputDto(final AbstractYResource resource, final Object dto, final Object dtoUniqueMemberValue)
	{
		if (dto.getClass().isAnnotationPresent(GraphNode.class))
		{
			final GraphNode graphNode = dto.getClass().getAnnotation(GraphNode.class);
			final Class modelClass = graphNode.target();
			if (resource.getNotsavedModelForCreate().getClass().equals(modelClass)
					&& resource.getResourceId().equals(dtoUniqueMemberValue))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Copy all unique members from dto to empty model in order to pass the model to the getModelByExample method and
	 * return null, saved model or ModelNotFoundException.
	 * 
	 * @return null, saved model or ModelNotFoundException.
	 */
	private ItemModel getModelFromDto(final YObjectGraphContext ctx, final Object dto)
	{
		ItemModel exampleModel = null;
		final FlexibleSearchService flexibleSearchService = ctx.getServices().getFlexibleSearchService();

		//if dto with GraphNode annotation
		if (dto.getClass().isAnnotationPresent(GraphNode.class))
		{
			final TypeService typeService = ctx.getServices().getTypeService();
			final ModelService modelService = ctx.getServices().getModelService();

			// get annotation with node configurations
			final GraphNode graphNode = dto.getClass().getAnnotation(GraphNode.class);
			final Class modelClass = graphNode.target();

			//part1 set primitive unique value
			final List<String> uniqueIdList = Arrays.asList(graphNode.uidProperties().split(","));
			for (final String uniqueID : uniqueIdList)
			{
				String uniqueIdPrepared = uniqueID.substring(0, 1).toUpperCase();
				uniqueIdPrepared += uniqueID.substring(1);
				final Object value;
				try
				{
					final Method getter = dto.getClass().getMethod("get" + uniqueIdPrepared);
					value = getter.invoke(dto, (Object[]) null);
					exampleModel = (ItemModel) modelClass.newInstance();
				}
				catch (final Exception e)
				{
					// exception handling should be reviewed
					throw new InternalServerErrorException(e);
				}
				if ("pk".equals(uniqueID))
				{
					exampleModel = modelService.get(PK.fromLong(((Long) value).longValue()));
					return exampleModel;
				}
				modelService.setAttributeValue(exampleModel, uniqueID, value);
			}

			//part2 set non-primitive unique values
			final ComposedTypeModel composedTypeModel = typeService.getComposedType(exampleModel.getClass());
			final Set<String> uMembersString = typeService.getUniqueAttributes(composedTypeModel.getCode());
			for (final String string : uMembersString)
			{
				final AttributeDescriptorModel attrDescModel = typeService.getAttributeDescriptor(composedTypeModel, string);
				if (attrDescModel.getUnique().booleanValue()
						&& attrDescModel.getAttributeType().getClass().equals(ComposedTypeModel.class))
				{
					try
					{
						String uniqueIdPrepared = string.substring(0, 1).toUpperCase();
						uniqueIdPrepared += string.substring(1);
						final Method getter = dto.getClass().getMethod("get" + uniqueIdPrepared);
						final Object value = getter.invoke(dto, (Object[]) null);
						if (value != null)
						{
							final ItemModel subModel = getModelFromDto(ctx, value);
							modelService.setAttributeValue(exampleModel, string, subModel);
						}
					}
					catch (final Exception e)
					{
						// exception handling should be reviewed
						throw new InternalServerErrorException(e);
					}
				}
			}
		}
		if (exampleModel == null)
		{
			return null;
		}
		return flexibleSearchService.getModelByExample(exampleModel);
	}
}
