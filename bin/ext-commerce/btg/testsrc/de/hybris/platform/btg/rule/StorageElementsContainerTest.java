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
package de.hybris.platform.btg.rule;

import de.hybris.platform.btg.rule.impl.StorageElementsContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for StorageElementsContainer operations. This does not test threading issues.
 */
public class StorageElementsContainerTest
{
	private StorageElementsContainer<Serializable> storageElementsContainer;

	@Before
	public void init()
	{
		this.storageElementsContainer = new StorageElementsContainer<Serializable>();
	}


	/**
	 * Tests if add and remove operation works correctly including "counting" of how many an element has been added. This
	 * means that to successfully remove an element the 'remove' method must be called as many times as the 'add' method
	 * has been called.
	 */
	@Test
	public void testAddRemove()
	{
		final String[] elements = new String[]
		{ "2", "3", "1" };
		final Set<Serializable> verification = new HashSet<Serializable>(Arrays.asList(elements));

		for (final String element : elements)
		{
			final int reps = Integer.parseInt(element);
			for (int i = 1; i <= reps; i++)
			{
				storageElementsContainer.add(element);
			}
		}

		final Set<Serializable> allElements = storageElementsContainer.getAllElements();
		Assert.assertEquals(verification, allElements);


		for (final String element : elements)
		{
			Assert.assertTrue(storageElementsContainer.contains(element));
		}

		final List<String> rotatedElements = Arrays.asList(elements);
		Collections.rotate(rotatedElements, 2);

		for (final String element : rotatedElements)
		{
			final int reps = Integer.parseInt(element);
			for (int i = 1; i <= reps; i++)
			{
				Assert.assertTrue(storageElementsContainer.contains(element));
				storageElementsContainer.remove(element);
			}
			Assert.assertFalse(storageElementsContainer.contains(element));
		}

		final Set<Serializable> noElements = storageElementsContainer.getAllElements();
		Assert.assertEquals(Collections.EMPTY_SET, noElements);
	}


	/**
	 * Tests interleaved sequence of adding and remove operations: For input sequence of elements: a,b,c,d,...,y,z it
	 * does the following: add(a) and then in sequence:
	 * add(b),remove(a),add(c),remove(b),add(d),remove(c),...,add(z),remove(y) and then it finishes with: remove(z).
	 */
	@Test
	public void testAddRemoveInterleavedSequence()
	{
		final String[] elements = new String[]
		{ "2", "3", "1" };

		final List<String> sequence = new ArrayList<String>();

		for (final String element : elements)
		{
			final int reps = Integer.parseInt(element);
			for (int i = 1; i <= reps; i++)
			{
				sequence.add(element);
			}
		}

		Assert.assertTrue(sequence.size() == 6);

		int index = 0;
		storageElementsContainer.add(sequence.get(index++));
		while (index < sequence.size())
		{
			storageElementsContainer.add(sequence.get(index));
			storageElementsContainer.remove(sequence.get(index - 1));
			index++;
		}

		storageElementsContainer.remove(sequence.get(index - 1));

		Assert.assertEquals(Collections.EMPTY_SET, storageElementsContainer.getAllElements());
	}

	/**
	 * Test the {@link StorageElementsContainer#removeAll(Serializable)} method
	 */
	@Test
	public void testRemoveAllForElement()
	{

		final String[] elements = new String[]
		{ "2", "3", "1" };
		final Set<Serializable> verification = new HashSet<Serializable>(Arrays.asList(elements));

		for (final String element : elements)
		{
			final int reps = Integer.parseInt(element);
			for (int i = 1; i <= reps; i++)
			{
				storageElementsContainer.add(element);
			}
		}

		final String elementToRemove = elements[1];
		storageElementsContainer.removeAll(elementToRemove);
		verification.remove(elementToRemove);
		Assert.assertTrue(storageElementsContainer.contains(elements[0]));
		Assert.assertFalse(storageElementsContainer.contains(elements[1]));
		Assert.assertTrue(storageElementsContainer.contains(elements[2]));

		Assert.assertEquals(verification, storageElementsContainer.getAllElements());
	}

	/**
	 * Test the {@link StorageElementsContainer#removeAll()} method
	 */
	@Test
	public void testRemoveAll()
	{
		final String[] elements = new String[]
		{ "2", "3", "1" };
		final Set<Serializable> verification = new HashSet<Serializable>(Arrays.asList(elements));

		for (final String element : elements)
		{
			final int reps = Integer.parseInt(element);
			for (int i = 1; i <= reps; i++)
			{
				storageElementsContainer.add(element);
			}
		}

		final Set<Serializable> allElements = storageElementsContainer.getAllElements();
		Assert.assertEquals(verification, allElements);

		storageElementsContainer.removeAll();
		Assert.assertEquals(Collections.EMPTY_SET, storageElementsContainer.getAllElements());
	}
}
