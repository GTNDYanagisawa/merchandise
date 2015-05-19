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
package de.hybris.platform.commercefacades.xstream.conv;

import com.thoughtworks.xstream.converters.Converter;




/**
 * Abstraction for the attribute converter mapping. Mapping entry which is being used to find a {@link Converter} while
 * marshaling/unmarshaling attribute {@link #getAttributeName()} of data object.
 * 
 * <pre>
 * {@code}
 * final XStream xstream  = .... ;
 * xstream.registerLocalConverter({@link #getAliasedClass()},{@link #getAttributeName()},{@link #getConverter()});
 * {@code}
 * </pre>
 * 
 * Such spring configuration
 * 
 * <pre>
 * {@code
 * 
 *    <bean class="de.hybris.platform.commercefacades.xstream.conv.AttributeConverterMapping" >
 *       <property name="aliasedClass" value="de.hybris.platform.commercefacades.product.data.SomeData" />
 *          <property name="converter" >
 *             <bean class="de.hybris.platform.commercewebservices.conv.SomeConverter" />
 *          </property>
 *       <property name="attributeName" value="date" />
 *    </bean>
 * }
 * </pre>
 * 
 * , determines that a de.hybris.platform.commercewebservices.conv.SomeConverter will be used for
 * marshaling/unmarshaling SomeData's date attribute.
 * 
 * 
 */
public class AttributeConverterMapping extends TypeConverterMapping
{

	private String attributeName;


	public void setAttributeName(final String name)
	{
		this.attributeName = name;
	}

	public String getAttributeName()
	{
		return attributeName;
	}

}
