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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.user.converters.populator.PrincipalPopulator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;



@UnitTest
public class CustomerReviewPopulatorTest
{
	private static final String USER_UID = "author";
	private static final String COMMENT = "comment";
	private static final Double RATING = Double.valueOf(5.0);

	private final CustomerReviewPopulator customerReviewPopulator = new CustomerReviewPopulator();
	private AbstractPopulatingConverter<CustomerReviewModel, ReviewData> customerReviewConverter;

	private final AbstractPopulatingConverter<PrincipalModel, PrincipalData> principalConverter = new ConverterFactory<PrincipalModel, PrincipalData, PrincipalPopulator>()
			.create(PrincipalData.class, new PrincipalPopulator());

	private PK pk;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		customerReviewPopulator.setPrincipalConverter(principalConverter);

		customerReviewConverter = new ConverterFactory<CustomerReviewModel, ReviewData, CustomerReviewPopulator>().create(
				ReviewData.class, customerReviewPopulator);

		// Create fake PK
		pk = PK.createFixedUUIDPK(1, System.nanoTime());
	}

	@Test
	public void testConvert()
	{
		final CustomerReviewModel source = mock(CustomerReviewModel.class);
		final UserModel userModel = mock(UserModel.class);

		given(source.getUser()).willReturn(userModel);
		given(userModel.getUid()).willReturn(USER_UID);
		given(source.getComment()).willReturn(COMMENT);
		given(source.getRating()).willReturn(RATING);
		given(source.getPk()).willReturn(pk);

		final ReviewData result = customerReviewConverter.convert(source);

		Assert.assertEquals(COMMENT, result.getComment());
		Assert.assertEquals(RATING, result.getRating());
		Assert.assertEquals(USER_UID, result.getPrincipal().getUid());
		Assert.assertEquals(pk.getLongValueAsString(), result.getId());
	}
}
