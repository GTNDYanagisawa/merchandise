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
package de.hybris.platform.commerceservices.spring.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/commerceservices/mergedirective-test-spring.xml")
@UnitTest
public class ListMergeDirectiveTest
{
	@Resource
	private List<String> addToEndListBean;

	@Resource
	private List<MergeTestBean> insertBeforeClassListBean;

	@Resource
	private List<MergeTestBean> insertBeforeNameListBean;

	@Resource
	private List<MergeTestBean> insertBeforeMultipleListBean;

	@Resource
	private List<MergeTestBean> insertAfterClassListBean;

	@Resource
	private List<MergeTestBean> insertAfterNameListBean;

	@Resource
	private List<MergeTestBean> insertBetweenListBean;

	@Resource
	private MultipleListMergeBean multipleListMergeBean;

	@Test
	public void testAddToEnd()
	{
		assertEquals(4, addToEndListBean.size());
		assertEquals("Was expecting \"four\", but got " + addToEndListBean.get(addToEndListBean.size() - 1), "four",
				addToEndListBean.get(addToEndListBean.size() - 1));
	}

	@Test
	public void testAddViaPropertyDescriptor()
	{
		assertEquals(5, multipleListMergeBean.getPropertyList().size());
		assertEquals(
				"Was expecting \"last\", but got "
						+ multipleListMergeBean.getPropertyList().get(multipleListMergeBean.getPropertyList().size() - 1).getName(),
				"last", multipleListMergeBean.getPropertyList().get(multipleListMergeBean.getPropertyList().size() - 1).getName());
	}

	@Test
	public void testAddViaFieldName() throws NoSuchFieldException, IllegalAccessException
	{
		final Field fieldList = MultipleListMergeBean.class.getDeclaredField("fieldList");
		fieldList.setAccessible(true);
		final List<MergeTestBean> list = (List<MergeTestBean>) fieldList.get(multipleListMergeBean);

		assertEquals(5, list.size());
		assertEquals("Was expecting \"last\", but got " + list.get(list.size() - 1).getName(), "last", list.get(list.size() - 1)
				.getName());
	}

	@Test
	public void testInsertBeforeViaClass()
	{
		assertEquals(5, insertBeforeClassListBean.size());
		assertEquals("Was expecting \"first\", but got " + insertBeforeClassListBean.get(0).getName(), "first",
				insertBeforeClassListBean.get(0).getName());
	}

	@Test
	public void testInsertBeforeViaBeanName()
	{
		assertEquals(3, insertBeforeNameListBean.size());
		assertEquals("Was expecting \"last\", but got " + insertBeforeNameListBean.get(1).getName(), "last",
				insertBeforeNameListBean.get(1).getName());
	}

	@Test
	public void testInsertBeforeMultiple()
	{
		assertEquals(5, insertBeforeMultipleListBean.size());
		int insertedIndex = -1;
		int lastIndex = -1;
		int beforeIndex = -1;
		for (int i = 0; i < insertBeforeMultipleListBean.size(); i++)
		{
			final MergeTestBean m = insertBeforeMultipleListBean.get(i);
			if (m.getName().equals("first"))
			{
				insertedIndex = i;
			}
			if (m.getName().equals("last"))
			{
				lastIndex = i;
			}
			if (m.getName().equals("insertBefore"))
			{
				beforeIndex = i;
			}
		}

		assertTrue(insertedIndex < lastIndex && insertedIndex < beforeIndex);
	}

	@Test
	public void testInsertAfterViaClass()
	{
		assertEquals(5, insertAfterClassListBean.size());
		assertEquals("Was expecting \"last\", but got "
				+ insertAfterClassListBean.get(insertAfterClassListBean.size() - 1).getName(), "last",
				insertAfterClassListBean.get(insertAfterClassListBean.size() - 1).getName());
	}

	@Test
	public void testInsertAfterViaBeanName()
	{
		assertEquals(3, insertAfterNameListBean.size());
		assertEquals("Was expecting \"last\", but got "
				+ insertAfterClassListBean.get(insertAfterClassListBean.size() - 1).getName(), "last",
				insertAfterClassListBean.get(insertAfterClassListBean.size() - 1).getName());
	}

	@Test
	public void testInsertBetween()
	{
		assertEquals(5, insertBetweenListBean.size());
		assertEquals("was expecting merge, got " + insertBetweenListBean.get(2).getName(), "merge", insertBetweenListBean.get(2)
				.getName());
	}
}
