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
package de.hybris.platform.customerreview;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.customerreview.setup.CustomerReviewSystemSetup;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests search restrictions in customer review context.
 */
public class SearchRestrictionTest extends ServicelayerTest
{

	private static final String INVALID_NUMBER_OF_CUSTOMER_REVIEWS = "invalid number of customer reviews";
	private static final String TEST_PRODUCT = "test_product";
	private static final String TEST_VERSION = "test_version";
	private static final String TEST_CATALOG = "test_catalog";
	private static final String TEST_USER = "test_user";
	private static final String COMMENT = "comment";
	private static final String LANG_EN = "en";


	@Resource
	private CustomerReviewSystemSetup customerReviewSystemSetup;
	@Resource
	private SearchRestrictionService searchRestrictionService;
	@Resource
	private CustomerReviewService customerReviewService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;

	@Test
	public void searchRestrictionTest()
	{
		final UserGroupModel group = new UserGroupModel();
		group.setUid("customergroup");
		modelService.save(group);
		// create test user
		final UserModel user = new UserModel();
		user.setUid(TEST_USER);
		// assign user to customergroup
		user.setGroups(Collections.singleton((PrincipalGroupModel) group));
		modelService.save(user);
		// create test catalog
		final CatalogModel catalog = new CatalogModel();
		catalog.setId(TEST_CATALOG);
		modelService.save(catalog);
		// create test catalog version
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion(TEST_VERSION);
		modelService.save(catalogVersion);
		// create test product
		final ProductModel product = new ProductModel();
		product.setCode(TEST_PRODUCT);
		product.setCatalogVersion(catalogVersion);
		modelService.save(product);
		// set current user
		userService.setCurrentUser(user);
		// create search restriction                                    
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));
		final Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("customerreview.searchrestrictions.create", new String[]
		{ "true" });
		final SystemSetupContext ctx = new SystemSetupContext(params, Type.ESSENTIAL, Process.ALL, "customerreview");
		customerReviewSystemSetup.createSearchRestrictions(ctx);
		// enable search restrictions
		searchRestrictionService.enableSearchRestrictions();
		// make sure that number of customer reviews is 0
		assertEquals(INVALID_NUMBER_OF_CUSTOMER_REVIEWS, Integer.valueOf(0), customerReviewService.getNumberOfReviews(product));
		// create restricted customer review 
		createCustomerReview(null, user, product, CustomerReviewApprovalType.PENDING);
		// create valid customer review
		createCustomerReview("headline", user, product, CustomerReviewApprovalType.APPROVED);
		// make sure that number of customer reviews is 1
		assertEquals(INVALID_NUMBER_OF_CUSTOMER_REVIEWS, Integer.valueOf(1), customerReviewService.getNumberOfReviews(product));
		// disable search restrictions
		searchRestrictionService.disableSearchRestrictions();
		// make sure that number of customer reviews is 2
		assertEquals(INVALID_NUMBER_OF_CUSTOMER_REVIEWS, Integer.valueOf(2), customerReviewService.getNumberOfReviews(product));
	}

	private void createCustomerReview(final String headline, final UserModel user, final ProductModel product,
			final CustomerReviewApprovalType approvalStatus)
	{
		final CustomerReviewModel review = customerReviewService.createCustomerReview(Double.valueOf(3.0), headline, COMMENT, user,
				product);
		review.setApprovalStatus(approvalStatus);
		review.setLanguage(commonI18NService.getCurrentLanguage());
		modelService.save(review);
	}
}
