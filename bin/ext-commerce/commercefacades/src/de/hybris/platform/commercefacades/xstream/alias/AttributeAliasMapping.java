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
 * Abstraction for the attribute aliasing. Provides a mapping which is used for marshaling/unmarshaling a type of data
 * object.
 * 
 * <pre>
 * {@code}
 * final XStream xstream  = .... ;
 * xstream.aliasAttribute({@link #getAliasedClass()},{@link #getAttributeName()},{@link #getAlias()});
 * {@code}
 * </pre>
 * 
 * Such spring configuration
 * 
 * <pre>
 *  {@code
 * <bean class="de.hybris.platform.commercefacades.xstream.alias.AttributeAliasMapping">
 *         <property name="alias" value="inlinecode" />
 *         <property name="attributeName" value="code" />
 *         <property name="aliasedClass"    value="de.hybris.platform.commercefacades.product.data.RootData" />
 *     </bean>
 *  </bean>
 *  }
 * </pre>
 * 
 * ,results which such response
 * 
 * <pre>
 * {@code
 * <de.hybris.platform.commercefacades.product.data.RootData inlinecode="...">
 * ...
 * </de.hybris.platform.commercefacades.product.data.RootData>
 * }
 * </pre>
 * 
 * instead of
 * 
 * ,
 * 
 * <pre>
 * {@code
 * <de.hybris.platform.commercefacades.product.data.RootData>
 * ...
 * <code>
 * 	...
 * </code>
 * </de.hybris.platform.commercefacades.product.data.RootData>
 * }
 * </pre>
 */
public class AttributeAliasMapping extends TypeAliasMapping
{
	private String attributeName;


	public void setAttributeName(final String attributeName)
	{
		this.attributeName = attributeName;
	}


	public String getAttributeName()
	{
		return attributeName;
	}


}
