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

import java.lang.reflect.Method;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.InternalServerErrorException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


@GraphPropertyInterceptor(typecheck = false)
public class StringToHybrisEnumValueConverter implements PropertyInterceptor<String, HybrisEnumValue>
{

	@Override
	public HybrisEnumValue intercept(final PropertyContext ctx, final String source)
	{
		// code snippet will work for both enum-types, however, needs DB access and performs slower 
		//		final TypeService typeService = ((YObjectGraphContext) ctx).getServices().getTypeService();
		//		final EnumerationValueModel model = typeService.getEnumerationValue(type, code);
		//		final ModelService modelService = ((YObjectGraphContext) ctx).getServices().getModelService();
		//		final HybrisEnumValue enumValue = modelService.getSource(model);

		HybrisEnumValue result = null;
		if (source != null)
		{
			final PropertyMapping pCfg = ctx.getPropertyMapping();

			final Class<?> enumType = pCfg.getTargetConfig().getWriteMethod().getParameterTypes()[0];

			result = this.getEnumValue(enumType, source);

		}
		return result;
	}

	public HybrisEnumValue getEnumValue(final Class enumType, final String enumId)
	{
		HybrisEnumValue result = null;
		if (enumType.isEnum())
		{
			try
			{
				result = (HybrisEnumValue) Enum.valueOf(enumType, enumId.toUpperCase());
			}
			catch (final IllegalArgumentException e)
			{
				throw new BadRequestException("Eumeration value '" + enumId + "' for type " + enumType.getSimpleName()
						+ " is not valid", e);
			}
		}
		else
		{
			// HybrisEnumValue as old fashioned enum-implementation needs an invocation 'valueOf'
			try
			{
				final Method method = enumType.getMethod("valueOf", String.class);
				result = (HybrisEnumValue) method.invoke(null, enumId);
			}
			catch (final Exception e)
			{
				throw new InternalServerErrorException("Error getting a " + HybrisEnumValue.class.getSimpleName() + " from value '"
						+ enumId + "'", e);
			}
		}
		return result;

	}
}
