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
package de.hybris.platform.commerceservices.util;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.c2l.CountryModel;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class AbstractComparatorTest
{
	private AbstractComparator abstractComparator;

	@Before
	public void setUp()
	{
		abstractComparator = new TestAbstractComparator();
	}

	@Test
	public void testCompare()
	{
		final CountryModel model1 = mock(CountryModel.class);
		final CountryModel model2 = mock(CountryModel.class);
		final CountryModel model3 = mock(CountryModel.class);
		final CountryModel model4 = mock(CountryModel.class);
		given(model1.getName()).willReturn("Aname");
		given(model2.getName()).willReturn("Bname");
		given(model3.getName()).willReturn("Bname");
		given(model1.getIsocode()).willReturn("Acode");
		given(model2.getIsocode()).willReturn("Bcode");
		given(model3.getIsocode()).willReturn("Bcode");
		given(model4.getName()).willReturn(null);
		int result = abstractComparator.compare(model1, model1);
		Assert.assertEquals(AbstractComparator.EQUAL, result);

		result = abstractComparator.compare(null, model2);
		Assert.assertEquals(AbstractComparator.BEFORE, result);

		result = abstractComparator.compare(model1, null);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compare(model2, model3);
		Assert.assertEquals(AbstractComparator.EQUAL, result);

		result = abstractComparator.compare(model1, model2);
		Assert.assertEquals(AbstractComparator.BEFORE, result);

		result = abstractComparator.compare(model2, model1);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compare(model4, model1);
		Assert.assertEquals(AbstractComparator.BEFORE, result);
	}

	@Test
	public void testCompareIntValues()
	{
		int result = abstractComparator.compareValues(2, 10);
		Assert.assertEquals(AbstractComparator.BEFORE, result);

		result = abstractComparator.compareValues(10, 2);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compareValues(2, 2);
		Assert.assertEquals(AbstractComparator.EQUAL, result);
	}

	@Test
	public void testCompareDoubleValues()
	{
		int result = abstractComparator.compareValues(2.12, 2.13);
		Assert.assertEquals(AbstractComparator.BEFORE, result);

		result = abstractComparator.compareValues(2.13, 2.12);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compareValues(2.99, 2.99);
		Assert.assertEquals(AbstractComparator.EQUAL, result);
	}

	@Test
	public void testCompareDateValues()
	{
		final Date date1 = new Date();
		final Date date2 = new Date(1);

		int result = abstractComparator.compareValues(date1, date1);
		Assert.assertEquals(AbstractComparator.EQUAL, result);

		result = abstractComparator.compareValues(null, date2);
		Assert.assertEquals(AbstractComparator.BEFORE, result);

		result = abstractComparator.compareValues(date1, null);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compareValues(date1, date2);
		Assert.assertEquals(AbstractComparator.AFTER, result);

		result = abstractComparator.compareValues(date2, date1);
		Assert.assertEquals(AbstractComparator.BEFORE, result);
	}
}

class TestAbstractComparator extends AbstractComparator<CountryModel>
{


	@Override
	protected int compareInstances(final CountryModel instance1, final CountryModel instance2)
	{
		int result = instance1.getName() != null ? instance1.getName().compareToIgnoreCase(instance2.getName()) : BEFORE;
		if (EQUAL == result)
		{
			result = instance1.getIsocode().compareToIgnoreCase(instance2.getIsocode());
		}
		return result;
	}


}
