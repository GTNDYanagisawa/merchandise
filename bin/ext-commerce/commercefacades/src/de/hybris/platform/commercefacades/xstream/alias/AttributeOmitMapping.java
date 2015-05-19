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
 *
 *  
 */
package de.hybris.platform.commercefacades.xstream.alias;

/**
 * Abstraction for the omitting specified field. Provides a mapping which is used for marshaling/unmarshaling a field of
 * type for data object.
 * 
 * <pre>
 * {@code}
 * final XStream xstream  = .... ;
 * xstream.omitField({@link #getAliasedClass()},{@link #getAttributeName()});
 * {@code}
 * </pre>
 * 
 * Such spring configuration
 * 
 * <pre>
 * {@code
 * <bean class="de.hybris.platform.commercefacades.xstream.alias.AttributeOmitMapping">
 * <property name="attributeName"  value="someAttribute" />
 * <property name="aliasedClass"  value="de.hybris.platform.commercefacades.product.data.SomeData" />
 * </bean>
 * }
 * </pre>
 * 
 * ,results which such response
 * 
 * <pre>
 * {@code
 * <de.hybris.platform.commercefacades.product.data.SomeData>
 * ...
 * </de.hybris.platform.commercefacades.product.data.SomeData>
 * }
 * </pre>
 * 
 * instead of
 * 
 * ,
 * 
 * <pre>
 * {@code
 * <de.hybris.platform.commercefacades.product.data.SomeData>
 * ...
 * 	<someAttribute>
 *  	....
 * 	</someAttribute> 
 * </de.hybris.platform.commercefacades.product.data.SomeData>
 * }
 * </pre>
 * 
 * 
 */
public class AttributeOmitMapping
{
	private String attributeName;

	private Class aliasedClass;

	public String getAttributeName()
	{
		return attributeName;
	}

	public void setAttributeName(final String attributeName)
	{
		this.attributeName = attributeName;
	}

	public void setAliasedClass(final Class aliasedClass)
	{
		this.aliasedClass = aliasedClass;
	}

	public Class getAliasedClass()
	{
		return aliasedClass;
	}
}
