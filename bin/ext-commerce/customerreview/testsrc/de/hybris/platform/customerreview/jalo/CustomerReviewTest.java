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
package de.hybris.platform.customerreview.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the CustomerReview extension.
 * 
 * 
 */
public class CustomerReviewTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	/*
	 * Define a logger for printing messages
	 */
	private static final Logger LOG = Logger.getLogger(CustomerReviewTest.class.getName());
	/**
	 * The member headline is used as a general headline for the test cases
	 */
	private final static String headline = "Test headline";
	/**
	 * The member comment is used as a general comment for the test cases
	 */
	private final static String comment = "Text text text";
	/**
	 * The member demoUsers is used as a general list of users for the test cases
	 */
	private final List<Customer> demoUsers = new ArrayList<Customer>();
	/**
	 * The member demoProduct is used as a general Product for the test cases
	 */
	private Product demoProduct = null;
	/**
	 * The member ratingLegal is used as a general valid rating for the test cases
	 */
	private Double ratingLegal = null;

	private String oldMinimalRating;
	private String oldMaximalRating;

	@Before
	public void setUp() throws ConsistencyCheckException
	{
		demoProduct = ProductManager.getInstance().createProduct("demo1");
		// do not use already defined users, for example "demo"

		// Create some demo users and add them to the list of demo users 
		final Customer demo22 = UserManager.getInstance().createCustomer("demo22");
		final Customer demo3 = UserManager.getInstance().createCustomer("demo3");
		final Customer demo4 = UserManager.getInstance().createCustomer("demo4");

		demoUsers.add(demo22);
		demoUsers.add(demo3);
		demoUsers.add(demo4);

		oldMinimalRating = Config.getParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING);
		oldMaximalRating = Config.getParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING);

		// Set some configuration values to be used generally in the tests
		Config.setParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING, String.valueOf(2));
		Config.setParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING, String.valueOf(4));
		ratingLegal = new Double(3);
	}

	@After
	public void tearDown()
	{
		Config.setParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING, oldMinimalRating);
		Config.setParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING, oldMaximalRating);
	}

	/**
	 * Helper for creating a CustomerReview item
	 * 
	 * @param rating
	 *           The rating to be set
	 * @return CustomerReview item with given rating, using default product and user
	 */
	private CustomerReview createCR(final Double rating)
	{
		return createCR(rating, demoUsers.get(0));
	}

	/**
	 * Helper for creating a CustomerReview item
	 * 
	 * @param rating
	 *           The rating to be set
	 * @param user
	 *           The user to be set
	 * @return CustomerReview item with given rating and user, using default product.
	 */
	private CustomerReview createCR(final Double rating, final User user)
	{
		return CustomerReviewManager.getInstance().createCustomerReview(rating, headline, comment, user, demoProduct);
	}

	/**
	 * Test if a too small rating value in createCustomerReview(...) causes an exception throwing
	 */
	public void testRatingTooSmall() //NOPMD
	{
		final Double ratingTooSmall = new Double(CustomerReviewConstants.getInstance().MINRATING - 1.0);
		try
		{
			createCR(ratingTooSmall);
			fail("JaloInvalidParameterException expected");
		}
		catch (final JaloInvalidParameterException e)
		{
			// expected
		}
	}

	/**
	 * Test if a too large rating value in createCustomerReview(...) causes an exception throwing
	 */
	public void testRatingTooLarge() //NOPMD
	{
		final Double ratingTooLarge = new Double(CustomerReviewConstants.getInstance().MAXRATING + 1.0);
		try
		{
			createCR(ratingTooLarge);
			fail("JaloInvalidParameterException expected");
		}
		catch (final JaloInvalidParameterException e)
		{
			// expected
		}
	}

	/**
	 * Test if the parameters given to the createCustomerReview and to the setRating method are set correctly
	 */
	@Test
	public void testCorrectSetting()
	{
		final CustomerReview cr = createCR(ratingLegal);
		assertEquals(ratingLegal, cr.getRating());
		assertEquals(headline, cr.getHeadline());
		assertEquals(comment, cr.getComment());
		cr.setRating(CustomerReviewConstants.getInstance().MAXRATING);
		assertEquals(new Double(CustomerReviewConstants.getInstance().MAXRATING), cr.getRating());
	}

	/**
	 * Test if a too large rating value in setRating(...) causes an exception throwing
	 */
	public void testRatingTooLargeInSetter()
	{
		final Double ratingTooLarge = new Double(CustomerReviewConstants.getInstance().MAXRATING + 1.0);
		final CustomerReview cr = createCR(ratingLegal);
		try
		{
			cr.setRating(ratingTooLarge);
			fail("JaloInvalidParameterException expected");
		}
		catch (final JaloInvalidParameterException e)
		{
			// expected
		}
	}

	/**
	 * Test if average rating is correctly calculated
	 */
	public void testAverageCorrectness()
	{
		Config.setParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING, String.valueOf(1));
		Config.setParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING, String.valueOf(10));

		createCR(Double.valueOf(2.0), demoUsers.get(0));
		createCR(Double.valueOf(4.0), demoUsers.get(1));
		createCR(Double.valueOf(7.0), demoUsers.get(2));

		assertEquals((2.0 + 4.0 + 7.0) / 3.0, CustomerReviewManager.getInstance().getAverageRatingAsPrimitive(demoProduct),
				0.0000001);
	}

	/**
	 * Simple test of order of reviews. Test if new review is returned first.
	 */
	@Test
	public void testOrderOfReviews()
	{
		// create some reviews
		Config.setParameter("customerreview.minimalrating", String.valueOf(1));
		Config.setParameter("customerreview.maximalrating", String.valueOf(10));
		createCR(new Double(2), demoUsers.get(0));
		createCR(new Double(2), demoUsers.get(1));
		createCR(new Double(3), demoUsers.get(2));
		// crate a new review, but wait a second to ensure diffent time stamps
		try
		{
			Thread.sleep(1000);
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		final CustomerReview newReview = CustomerReviewManager.getInstance().createCustomerReview(new Double(3), headline, comment,
				demoUsers.get(1), demoProduct);
		final SessionContext ctx = CustomerReviewManager.getInstance().getSession().getSessionContext();
		final List<CustomerReview> reviewList = CustomerReviewManager.getInstance().getAllReviews(ctx, demoProduct);
		final CustomerReview firstReview = reviewList.get(0);
		// Test is meaningless if list contains less than 2 elements
		if (reviewList.size() < 2)
		{
			fail("Test of order is nonsense as only one review in list");
		}
		assertEquals(newReview.getPK(), firstReview.getPK());

	}

	/**
	 * Test that blocked reviews are not returned
	 */
	@Test
	public void testBlockedReviews()
	{
		UserGroup userGroup = null;
		try
		{
			userGroup = UserManager.getInstance().createUserGroup("customergroup");
		}
		catch (final ConsistencyCheckException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try
		{
			CustomerReviewManager.getInstance().createEssentialData(null, null);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set user in context to be a customer user 
		final SessionContext ctx = CustomerReviewManager.getInstance().getSession().getSessionContext();
		demoUsers.get(0).addToGroup(userGroup);
		ctx.setUser(demoUsers.get(0));

		// create reviews

		createCR(new Double(2), demoUsers.get(0));
		final CustomerReview cR2 = createCR(new Double(3), demoUsers.get(1));

		/*
		 * Test that the created review are returned, by creation they are not blocked
		 */
		List<CustomerReview> reviewList = CustomerReviewManager.getInstance().getAllReviews(ctx, demoProduct);

		// Check that both reviews are returned
		assertEquals(2, reviewList.size());

		/*
		 * Test that second review is not returned, if it is blocked
		 */
		cR2.setBlocked(true);

		reviewList = CustomerReviewManager.getInstance().getAllReviews(ctx, demoProduct);

		// Check for exactly one review returned
		assertEquals(1, reviewList.size());

		// Check that returned review is the correct one (rating=1)
		assertEquals(new Double(2.0), reviewList.get(0).getRating());


		/*
		 * Test that second review is returned, if the blocking is canceled
		 */
		cR2.setBlocked(false);

		reviewList = CustomerReviewManager.getInstance().getAllReviews(ctx, demoProduct);

		// Check for exactly one review returned
		assertEquals(2, reviewList.size());
	}
}
