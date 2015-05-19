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
package com.hybris.backoffice.cockpitng.search;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchOperatorService;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


public class DefaultAdvancedSearchOperatorServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private AdvancedSearchOperatorService defaultAdvancedSearchOperatorService;
	private final String BEANS_DEFINITION = "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
			+ "\t\t xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "\t\t xmlns:util=\"http://www.springframework.org/schema/util\"\n"
			+ "\t\t xmlns:c=\"http://www.springframework.org/schema/c\"\n"
			+ "\t\t xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework"
			+ ".org/schema/beans/spring-beans-3.1.xsd\n"
			+ "\t\thttp://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd\">\n"
			+ "\t<alias alias=\"advancedSearchOperators\" name=\"defaultAdvancedSearchOperators\"/>\n"
			+ "\t<bean id=\"defaultAdvancedSearchOperatorService\"\n"
			+ "\t\t\tclass=\"com.hybris.backoffice.cockpitng.search.DefaultAdvancedSearchOperatorService\">\n"
			+ "\t\t<property name=\"advancedSearchOperators\" ref=\"advancedSearchOperators\"/>\n" + "\t</bean>"
			+ "\t<util:map id=\"defaultAdvancedSearchOperators\">\n" + "\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).LITERAL}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.EQUALS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.STARTS_WITH\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.ENDS_WITH\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.LIKE\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.CONTAINS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_EMPTY\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_NOT_EMPTY\"/>\n"
			+ "\t\t\t</list>\n" + "\t\t</entry>\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).LOGICAL}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.EQUALS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.UNEQUAL\"/>\n"
			+ "\t\t\t</list>\n" + "\t\t</entry>\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).NUMERIC}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.EQUALS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.GREATER\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator"
			+ ".GREATER_OR_EQUAL\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.LESS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator"
			+ ".LESS_OR_EQUAL\"/>\n" + "\t\t\t</list>\n" + "\t\t</entry>\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).REFERENCE}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.EQUALS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_EMPTY\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_NOT_EMPTY\"/>\n"
			+ "\t\t\t</list>\n" + "\t\t</entry>\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).MULTIREFERENCE}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.CONTAINS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.DOES_NOT_CONTAIN\n"
			+ "\t\t\t\t\"/>\n" + "\t\t\t</list>\n" + "\t\t</entry>\n"
			+ "\t\t<entry key=\"#{T(com.hybris.backoffice.cockpitng.search.AdvancedSearchOperatorType).DATE}\">\n"
			+ "\t\t\t<list>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.EQUALS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.GREATER\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator"
			+ ".GREATER_OR_EQUAL\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.LESS\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator"
			+ ".LESS_OR_EQUAL\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_EMPTY\"/>\n"
			+ "\t\t\t\t<util:constant static-field=\"com.hybris.cockpitng.search.data.ValueComparisonOperator.IS_NOT_EMPTY\"/>\n"
			+ "\t\t\t</list>\n" + "\t\t</entry>\n" + "\t</util:map>" + "</beans>\n";


	@Override
	public void prepareApplicationContextAndSession()
	{
		final ApplicationContext parentApplicationContext = getApplicationContext();
		final GenericApplicationContext applicationContext = new GenericApplicationContext(parentApplicationContext);
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(applicationContext);
		xmlReader.loadBeanDefinitions(new ByteArrayResource(BEANS_DEFINITION.getBytes()));
		applicationContext.refresh();

		autowireProperties(applicationContext);
	}

	@Test
	public void testAvailableOperatorsForString()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockStringDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForString());
	}

	@Test
	public void testAvailableOperatorsForBoolean()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockBooleanDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForBoolean());
	}

	@Test
	public void testAvailableOperatorsForDate()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockDateDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForDate());
	}

	@Test
	public void testAvailableOperatorsForDouble()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockDoubleDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForDouble());
	}

	@Test
	public void testAvailableOperatorsForInteger()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockIntegerDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForInteger());
	}

	@Test
	public void testAvailableOperatorsForEnum()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockEnumDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForEnum());
	}

	@Test
	public void testAvailableOperatorsForRelation1To1()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMock1To1RelationDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForRelation1To1());
	}


	@Test
	public void testAvailableOperatorsForRelation1ToN()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMock1ToNRelationDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForRelation1ToN());
	}

	@Test
	public void testAvailableOperatorsForRelationMToN()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockNToMRelationDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForRelationNToM());
	}

	@Test
	public void testAvailableOperatorsForRelationNTo1()
	{
		final Collection<ValueComparisonOperator> availableOperators = defaultAdvancedSearchOperatorService
				.getAvailableOperators(createMockNTo1RelationDataAttribute());
		Assertions.assertThat(availableOperators).containsOnly((Object[]) getExpectedOperatorsForRelationNTo1());
	}

	protected DataAttribute createMockDataAttributeTemplate(final boolean isAtomic, final Class valueTypeClass,
			final DataAttribute.AttributeType attributeType)
	{
		final DataAttribute dataAttribute = Mockito.mock(DataAttribute.class);
		final DataType valueType = Mockito.mock(DataType.class);

		Mockito.when(BooleanUtils.toBooleanObject(valueType.isAtomic())).thenReturn(BooleanUtils.toBooleanObject(isAtomic));
		Mockito.when(valueType.getClazz()).thenReturn(valueTypeClass);
		Mockito.when(dataAttribute.getValueType()).thenReturn(valueType);
		Mockito.when(dataAttribute.getAttributeType()).thenReturn(attributeType);

		Assertions.assertThat(dataAttribute.getValueType().isAtomic()).isEqualTo(isAtomic);
		Assertions.assertThat(dataAttribute.getValueType().getClazz()).isEqualTo(valueTypeClass);
		Assertions.assertThat(dataAttribute.getAttributeType()).isEqualTo(attributeType);
		return dataAttribute;
	}

	protected DataAttribute createMockStringDataAttribute()
	{
		return createMockDataAttributeTemplate(true, String.class, DataAttribute.AttributeType.SINGLE);
	}

	protected DataAttribute createMockBooleanDataAttribute()
	{
		return createMockDataAttributeTemplate(true, Boolean.class, DataAttribute.AttributeType.SINGLE);
	}

	protected DataAttribute createMockDateDataAttribute()
	{
		return createMockDataAttributeTemplate(true, Date.class, DataAttribute.AttributeType.SINGLE);
	}

	protected DataAttribute createMockDoubleDataAttribute()
	{
		return createMockDataAttributeTemplate(true, Double.class, DataAttribute.AttributeType.SINGLE);
	}

	protected DataAttribute createMockIntegerDataAttribute()
	{
		return createMockDataAttributeTemplate(true, Integer.class, DataAttribute.AttributeType.SINGLE);
	}

	protected DataAttribute createMockEnumDataAttribute()
	{
		return createMockDataAttributeTemplate(false, WorkflowActionStatus.class, DataAttribute.AttributeType.SINGLE);
	}

	// mock Product - Catalog realtion
	protected DataAttribute createMock1To1RelationDataAttribute()
	{
		return createMockDataAttributeTemplate(false, CatalogModel.class, DataAttribute.AttributeType.SINGLE);
	}

	// mock Product - Product Reference relation
	protected DataAttribute createMock1ToNRelationDataAttribute()
	{
		return createMockDataAttributeTemplate(false, ProductReferenceModel.class, DataAttribute.AttributeType.COLLECTION);
	}

	// mock Product - Keyword relation
	protected DataAttribute createMockNToMRelationDataAttribute()
	{
		return createMockDataAttributeTemplate(false, KeywordModel.class, DataAttribute.AttributeType.COLLECTION);
	}

	// mock ProductReference - Product relation
	protected DataAttribute createMockNTo1RelationDataAttribute()
	{
		return createMockDataAttributeTemplate(false, ProductModel.class, DataAttribute.AttributeType.SINGLE);
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForString()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.CONTAINS, ValueComparisonOperator.EQUALS, ValueComparisonOperator.LIKE,
				ValueComparisonOperator.STARTS_WITH, ValueComparisonOperator.ENDS_WITH, ValueComparisonOperator.IS_EMPTY,
				ValueComparisonOperator.IS_NOT_EMPTY };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForBoolean()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.UNEQUAL };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForDate()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.GREATER, ValueComparisonOperator.GREATER_OR_EQUAL,
				ValueComparisonOperator.LESS, ValueComparisonOperator.LESS_OR_EQUAL, ValueComparisonOperator.IS_EMPTY,
				ValueComparisonOperator.IS_NOT_EMPTY };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForDouble()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.GREATER, ValueComparisonOperator.LESS,
				ValueComparisonOperator.GREATER_OR_EQUAL, ValueComparisonOperator.LESS_OR_EQUAL }; // TODO
																															  // diffs
																															  // between
																															  // hmc
																															  // and
																															  // hmc2
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForInteger()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.GREATER, ValueComparisonOperator.LESS,
				ValueComparisonOperator.GREATER_OR_EQUAL, ValueComparisonOperator.LESS_OR_EQUAL };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForEnum()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.IS_EMPTY, ValueComparisonOperator.IS_NOT_EMPTY };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForRelation1To1()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.IS_EMPTY, ValueComparisonOperator.IS_NOT_EMPTY };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForRelation1ToN()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.CONTAINS, ValueComparisonOperator.DOES_NOT_CONTAIN };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForRelationNTo1()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.EQUALS, ValueComparisonOperator.IS_EMPTY, ValueComparisonOperator.IS_NOT_EMPTY };
	}

	protected ValueComparisonOperator[] getExpectedOperatorsForRelationNToM()
	{
		return new ValueComparisonOperator[]
		{ ValueComparisonOperator.CONTAINS, ValueComparisonOperator.DOES_NOT_CONTAIN };
	}
}
