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
package de.hybris.platform.importcockpit.services.impex.generator.operations.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.mappingview.SourceColumnModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.MappingLineModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.AtomicTypeMapping;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultDataGeneratorOperationTest
{

	private static final String NON_BLANK_TEXT = "non_blank_text";

	@Mock
	private Map<Integer, String> inputDataLine;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultDataGeneratorOperation operation;

	@Mock
	private AtomicTypeMapping atomicTypeMapping;

	@Mock
	private MappingLineModel attributeLine;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(Boolean.valueOf(inputDataLine.isEmpty())).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(atomicTypeMapping.hasChildren())).thenReturn(Boolean.TRUE);
	}

	@Test
	public void generateDataLineForProductAttribEntryAttributeValueReturned()
	{
		when(attributeLine.getValue()).thenReturn(NON_BLANK_TEXT);
		final String entryResultValue = operation.generateDataLineForProductAttribEntry(inputDataLine, attributeLine);
		assertThat(entryResultValue).isEqualTo(NON_BLANK_TEXT);
		verify(attributeLine, Mockito.times(2)).getValue();
	}

	@Test
	public void generateDataLineForProductAttribEntryBlanValueReturned()
	{
		when(attributeLine.getValue()).thenReturn(StringUtils.EMPTY);
		when(attributeLine.getSource()).thenReturn(null);
		final String entryResultValue = operation.generateDataLineForProductAttribEntry(inputDataLine, attributeLine);
		assertThat(entryResultValue).isEqualTo(StringUtils.EMPTY);
		verify(attributeLine).getValue();
	}

	@Test
	public void generateDataLineForProductAttribEntrySourceLineValueReturned()
	{
		when(attributeLine.getValue()).thenReturn(StringUtils.EMPTY);
		final SourceColumnModel sourceColumnModel = Mockito.mock(SourceColumnModel.class);
		when(sourceColumnModel.getId()).thenReturn("42");
		when(attributeLine.getSource()).thenReturn(sourceColumnModel);
		when(inputDataLine.get(Integer.valueOf(42))).thenReturn(null);
		operation.generateDataLineForProductAttribEntry(inputDataLine, attributeLine);
		verify(sourceColumnModel, Mockito.times(2)).getId();
		verify(inputDataLine).get(Integer.valueOf(42));
	}

}
