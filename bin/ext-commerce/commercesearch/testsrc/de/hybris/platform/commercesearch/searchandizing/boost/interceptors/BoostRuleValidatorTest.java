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
package de.hybris.platform.commercesearch.searchandizing.boost.interceptors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercesearch.enums.SolrBoostConditionOperator;
import de.hybris.platform.commercesearch.model.SolrBoostRuleModel;
import de.hybris.platform.commercesearch.searchandizing.boost.operators.BoostOperatorsRegistry;
import de.hybris.platform.commercesearch.searchandizing.boost.validators.BoostValueValidator;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class BoostRuleValidatorTest
{
	private static final String PARSABLE_INT_VALUE = "111";
	private BoostRuleValidator boostRuleValidator;

	@Mock
	private SolrBoostRuleModel solrBoostRule;
	@Mock
	private InterceptorContext ctx;
	@Mock
	private SolrIndexedPropertyModel solrIndexProperty;
	@Mock
	private BoostOperatorsRegistry boostOperatorsRegistry;
	@Mock
	private L10NService l10nService;

	@Mock
	private BoostValueValidator floatValidator;
	@Mock
	private BoostValueValidator dateValidator;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		boostRuleValidator = new BoostRuleValidator();
		boostRuleValidator.setL10nService(l10nService);
		boostRuleValidator.setBoostOperatorsRegistry(boostOperatorsRegistry);
		when(solrBoostRule.getSolrIndexedProperty()).thenReturn(solrIndexProperty);
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldThrowExceptionWhenTypeIsNotSupported() throws InterceptorException
	{
		//given
		final SolrPropertiesTypes textType = SolrPropertiesTypes.TEXT;
		final SolrBoostConditionOperator operator = SolrBoostConditionOperator.GREATER_THAN;

		when(l10nService.getLocalizedString(BoostRuleValidator.NOT_SUPPORTEDTYPE, new String[]
		{ textType.toString(), operator.toString() })).thenReturn(BoostRuleValidator.NOT_SUPPORTEDTYPE);
		when(solrIndexProperty.getType()).thenReturn(textType);
		when(solrBoostRule.getOperator()).thenReturn(operator);
		when(boostOperatorsRegistry.isOperatorSupportedForGivenType(textType, operator)).thenReturn(false);
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage(BoostRuleValidator.NOT_SUPPORTEDTYPE);

		//when
		boostRuleValidator.onValidate(solrBoostRule, ctx);
	}


	@Test
	public void shouldChangeTypeToStringWhenSolrIndexedPropertyHasRange() throws InterceptorException
	{
		//given
		final SolrPropertiesTypes textType = SolrPropertiesTypes.STRING;
		final SolrBoostConditionOperator operator = SolrBoostConditionOperator.GREATER_THAN;

		when(l10nService.getLocalizedString(BoostRuleValidator.NOT_SUPPORTEDTYPE, new String[]
		{ textType.toString(), operator.toString() })).thenReturn(BoostRuleValidator.NOT_SUPPORTEDTYPE);
		when(solrIndexProperty.getType()).thenReturn(textType);
		when(solrIndexProperty.getName()).thenReturn("Megapixels");
		when(solrIndexProperty.getRangeSet()).thenReturn(new SolrValueRangeSetModel());
		when(solrBoostRule.getOperator()).thenReturn(operator);
		when(solrBoostRule.getPropertyValue()).thenReturn(PARSABLE_INT_VALUE);
		when(boostOperatorsRegistry.isTypeSupported(textType)).thenReturn(true);
		when(boostOperatorsRegistry.isOperatorSupportedForGivenType(textType, operator)).thenReturn(false);

		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage(BoostRuleValidator.NOT_SUPPORTEDTYPE);
		//when
		boostRuleValidator.onValidate(solrBoostRule, ctx);
	}


	@Test
	public void shouldThrowExceptionWhenValidationFails() throws InterceptorException
	{
		//given
		final String value = "asds";
		final SolrPropertiesTypes floatType = SolrPropertiesTypes.FLOAT;
		final SolrPropertiesTypes dateType = SolrPropertiesTypes.DATE;
		final SolrBoostConditionOperator operator = SolrBoostConditionOperator.GREATER_THAN;
		solrBoostRule.setPropertyValue(value);

		when(floatValidator.isApplicable(floatType)).thenReturn(true);
		doThrow(new InterceptorException("notparsable.float")).when(floatValidator).validate(anyString());
		doThrow(new InterceptorException("notparsable.date")).when(dateValidator).validate(anyString());
		when(dateValidator.isApplicable(dateType)).thenReturn(false);

		boostRuleValidator.setValidators(Sets.newHashSet(dateValidator, floatValidator));

		when(solrIndexProperty.getType()).thenReturn(floatType);
		when(solrBoostRule.getOperator()).thenReturn(operator);
		when(solrBoostRule.getPropertyValue()).thenReturn(PARSABLE_INT_VALUE);
		when(boostOperatorsRegistry.isTypeSupported(floatType)).thenReturn(true);
		when(boostOperatorsRegistry.isOperatorSupportedForGivenType(floatType, operator)).thenReturn(true);

		//when
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage("notparsable.float");
		boostRuleValidator.onValidate(solrBoostRule, ctx);
	}

	@Test
	public void shouldThrowExceptionWhenBoostFactorIsNegative() throws InterceptorException
	{
		//given
		final SolrPropertiesTypes textType = SolrPropertiesTypes.TEXT;
		final SolrBoostConditionOperator operator = SolrBoostConditionOperator.GREATER_THAN;

		when(l10nService.getLocalizedString(BoostRuleValidator.NEGATIVE_BOOSTFACTOR)).thenReturn("Only positive values are allowed.");
		when(solrBoostRule.getBoostFactor()).thenReturn(-10);
		when(boostOperatorsRegistry.isOperatorSupportedForGivenType(textType, operator)).thenReturn(false);

		//when
		//then
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage("Only positive values are allowed.");
		boostRuleValidator.onValidate(solrBoostRule, ctx);
	}

	@Test
	public void shouldThrowExceptionWhenBoostFactorBiggerThanMaxBoostFactor() throws InterceptorException
	{
		//given
		final SolrPropertiesTypes textType = SolrPropertiesTypes.TEXT;
		final SolrBoostConditionOperator operator = SolrBoostConditionOperator.GREATER_THAN;

		when(l10nService.getLocalizedString(BoostRuleValidator.MAX_BOOSTFACTOR, new String[]
				{ Double.toString(100) })).thenReturn("Max boost value exceeded. Please provide a number value less or equal to: [100].");
		when(solrBoostRule.getBoostFactor()).thenReturn(101);
		when(boostOperatorsRegistry.isOperatorSupportedForGivenType(textType, operator)).thenReturn(false);

		//when
		//then
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage("Max boost value exceeded. Please provide a number value less or equal to: [100].");
		boostRuleValidator.onValidate(solrBoostRule, ctx);
	}
}
