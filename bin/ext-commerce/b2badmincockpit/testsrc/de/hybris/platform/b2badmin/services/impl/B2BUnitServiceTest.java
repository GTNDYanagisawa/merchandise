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
package de.hybris.platform.b2badmin.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.jalo.B2BCustomer;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.services.ValidationService;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class B2BUnitServiceTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(B2BUnitServiceTest.class);

	@Resource
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	@Resource
	private BaseDao baseDao;
	@Resource
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private SessionService sessionService;
	@Resource
	private I18NService i18nService;
	@Resource
	private SearchRestrictionService searchRestrictionService;
	@Resource
	private ValidationService validationService;

	@Before
	public void before() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");

		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void testRestrictionsOfUsersAndUnits() throws Exception
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final String userId = "IC CEO";
				login(userId);

				// test restrictions on units and employees
				Assert.assertNull(b2bUnitService.getUnitForUid("GC"));
				Assert.assertNull(baseDao.findFirstByAttribute(B2BCustomer.UID, "GC CEO", B2BCustomerModel.class));

				// test costcenter and budget restriction
				Assert.assertNotNull(baseDao.findFirstByAttribute(B2BCostCenterModel.CODE, "IC 0", B2BCostCenterModel.class));
				Assert.assertNull(baseDao.findFirstByAttribute(B2BCostCenterModel.CODE, "GC 0", B2BCostCenterModel.class));
				Assert.assertNotNull(baseDao.findFirstByAttribute(B2BBudgetModel.CODE, "IC BUDGET EUR 1M", B2BBudgetModel.class));
				Assert.assertNull(baseDao.findFirstByAttribute(B2BBudgetModel.CODE, "GC BUDGET EUR 1M", B2BBudgetModel.class));

				// change the session IC_USER to the customer who belongs to different organization
				final UserModel GC_USER = (UserModel) sessionService.executeInLocalView(new SessionExecutionBody()
				{
					@Override
					public Object execute()
					{
						searchRestrictionService.disableSearchRestrictions();
						final UserModel user = baseDao.findFirstByAttribute(B2BCustomerModel.UID, "GC CEO", B2BCustomerModel.class);
						Assert.assertNotNull(user);
						return user;
					}
				});
				Assert.assertNotNull(GC_USER);
				login(GC_USER);
				// should now we able to see only items linked to C_2.. units
				Assert.assertNull(baseDao.findFirstByAttribute(B2BCostCenterModel.CODE, "IC 0", B2BCostCenterModel.class));
				Assert.assertNotNull(baseDao.findFirstByAttribute(B2BCostCenterModel.CODE, "GC 0", B2BCostCenterModel.class));
				Assert.assertNull(baseDao.findFirstByAttribute(B2BBudgetModel.CODE, "IC BUDGET EUR 1M", B2BBudgetModel.class));
				Assert.assertNotNull(baseDao.findFirstByAttribute(B2BBudgetModel.CODE, "GC BUDGET EUR 1M", B2BBudgetModel.class));
			}
		}, userService.getAdminUser());


	}


	@Test
	public void testGetBranch() throws Exception
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final String userId = "IC CEO";
				login(userId);

				// test getBranchMethod
				B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");
				Assert.assertNotNull(unit);
				final Set<B2BUnitModel> decendents = b2bUnitService.getBranch(unit);
				Assert.assertNotNull(decendents);
				Assert.assertEquals(11, decendents.size());

				// test getAllUnitsOfOrganization
				final Set<B2BUnitModel> allUnits = b2bUnitService.getBranch(unit);
				Assert.assertNotNull(allUnits);
				Assert.assertEquals(11, allUnits.size());


				unit = b2bUnitService.getUnitForUid("IC Sales");
				Assert.assertNotNull(unit);
				Assert.assertEquals(b2bUnitService.getParent(unit).getUid(), "IC");
				Assert.assertEquals(b2bUnitService.getRootUnit(unit).getUid(), "IC");
				Assert.assertEquals(9, b2bUnitService.getBranch(unit).size());
			}
		}, userService.getAdminUser());

	}

	@Test
	public void testGetApprovalProcessCode() throws Exception
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final String userId = "IC CEO";
				String approvalCode = null;
				login(userId);

				// test findApprovalProcessCodeForUnit
				final B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");
				Assert.assertNotNull(unit);
				// test
				approvalCode = b2bUnitService.getApprovalProcessCodeForUnit(unit);
				Assert.assertNull(approvalCode);

				unit.setApprovalProcessCode("simpleapproval");
				modelService.save(unit);


				approvalCode = b2bUnitService.getApprovalProcessCodeForUnit(unit);
				Assert.assertNotNull(approvalCode);
				// test findApprovalProcessCodeForUnit - get value from parent
				final B2BUnitModel unitSales = b2bUnitService.getUnitForUid("IC Sales");
				Assert.assertNotNull(unitSales);
				approvalCode = b2bUnitService.getApprovalProcessCodeForUnit(unitSales);
				Assert.assertNotNull(approvalCode);

				unit.setApprovalProcessCode(null);
				modelService.save(unit);
				approvalCode = b2bUnitService.getApprovalProcessCodeForUnit(unitSales);
				Assert.assertNull(approvalCode);

			}
		}, userService.getAdminUser());
	}


	@Test
	public void shouldUpdateBranchInSession() throws Exception
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final JaloSession currentSession = JaloSession.getCurrentSession();
				final DefaultSession session = (DefaultSession) sessionService.getSession(currentSession.getSessionID());
				Assert.assertEquals(currentSession, session.getJaloSession());

				session.setAttribute("test", new Object());
				Assert.assertNotNull(currentSession.getAttribute("test"));
				final B2BCustomerModel user = (B2BCustomerModel) userService.getUserForUID("IC CEO");
				session.setAttribute(B2BConstants.CTX_ATTRIBUTE_BRANCH, null);
				session.setAttribute(B2BConstants.CTX_ATTRIBUTE_ROOTUNIT, null);
				Assert.assertNull(currentSession.getAttribute(B2BConstants.CTX_ATTRIBUTE_BRANCH));
				Assert.assertNull(currentSession.getAttribute(B2BConstants.CTX_ATTRIBUTE_ROOTUNIT));

				b2bUnitService.updateBranchInSession(session, user);
				Assert.assertNotNull(currentSession.getAttribute(B2BConstants.CTX_ATTRIBUTE_BRANCH));
				Assert.assertNotNull(currentSession.getAttribute(B2BConstants.CTX_ATTRIBUTE_ROOTUNIT));

			}
		}, userService.getAdminUser());
	}

	/**
	 * This test is specially added for fetching the tree of child Unit in the organization hierarchy. So used a user Id
	 * which is not the root unit for example GC.
	 */
	@Test
	public void shouldGetAllBranchAndParentUnitOfChildUnit()
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final String userId = "GC Sales UK Boss";
				login(userId);
				final Set<B2BUnitModel> allUnitsOfOrganization = b2bUnitService.getAllUnitsOfOrganization(b2bCustomerService
						.getCurrentB2BCustomer());
				Assert.assertTrue(allUnitsOfOrganization.size() >= 3);

			}
		}, userService.getAdminUser());
	}

	@Test
	public void shouldDisableBranchofParentUnit()
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final String userId = "GC CEO";
				login(userId);

				final B2BUnitModel unitUK = b2bUnitService.getUnitForUid("GC Sales UK");

				b2bUnitService.disableBranch(unitUK);

				LOG.debug("Test all units are disabled for : " + unitUK.getUid());

				final Set<B2BUnitModel> allUnitsOfOrganization = b2bUnitService.getBranch(unitUK);

				for (final B2BUnitModel unit : allUnitsOfOrganization)
				{
					Assert.assertFalse(unit.getActive().booleanValue());
				}
			}
		}, userService.getAdminUser());
	}

	@Test
	public void shouldGetParentUnitForCustomerWithMultipleUnitsAssigned()
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final B2BCustomerModel user = userService.getUserForUID("GC CEO", B2BCustomerModel.class);
				// assign user to session
				login(user);
				final B2BUnitModel parent = b2bUnitService.getParent(user);
				Assert.assertNotNull(parent);
				// try to add the member which the user is already a member of
				b2bUnitService.addMember(parent, user);
				modelService.save(user);
				Assert.assertEquals(1,
						CollectionUtils.select(user.getGroups(), PredicateUtils.instanceofPredicate(B2BUnitModel.class)).size());

				// assign a 2nd unit to GC CEO
				final B2BUnitModel unitUK = b2bUnitService.getUnitForUid("GC Sales UK");
				b2bUnitService.addMember(unitUK, user);
				modelService.save(user);
				Assert.assertEquals(2,
						CollectionUtils.select(user.getGroups(), PredicateUtils.instanceofPredicate(B2BUnitModel.class)).size());

				b2bUnitService.updateParentB2BUnit(unitUK, user);
				modelService.save(user);

				Assert.assertEquals(unitUK, b2bUnitService.getParent(user));
				// make sure user is still belongs to 2 units
				Assert.assertEquals(2,
						CollectionUtils.select(user.getGroups(), PredicateUtils.instanceofPredicate(B2BUnitModel.class)).size());
			}
		}, userService.getAdminUser());

	}

	/*
	 * Tests that a change in the unit's active state triggers a warning message. Tests that the unitActive constraint
	 * does not prevent the model from being saved.
	 */
	@Ignore
	public void changeInActiveStateTriggersWarningMessage()
	{

		//test warning triggered when changing active state to false
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC Sales UK");
		unit.setActive(Boolean.FALSE);
		modelService.save(unit);
		Set<HybrisConstraintViolation> constraintViolations = validationService.validate(unit);

		Assert.assertTrue(checkForB2BUnitActiveAnnotation(constraintViolations));
		Assert.assertFalse(unit.getActive().booleanValue());
		Assert.assertTrue(modelService.isUpToDate(unit));

		//test warning triggered when changing active state to true
		unit.setActive(Boolean.TRUE);
		modelService.save(unit);
		constraintViolations = validationService.validate(unit);

		Assert.assertTrue(checkForB2BUnitActiveAnnotation(constraintViolations));
		Assert.assertTrue(unit.getActive().booleanValue());
		Assert.assertTrue(modelService.isUpToDate(unit));
	}

	/*
	 * Helper method that checks for unitActive's annotation.
	 */
	private boolean checkForB2BUnitActiveAnnotation(final Set<HybrisConstraintViolation> constraintViolations)
	{
		for (final Iterator constraintIterator = constraintViolations.iterator(); constraintIterator.hasNext();)
		{

			final HybrisConstraintViolation hybrisConstraintViolation = (HybrisConstraintViolation) constraintIterator.next();
			final ConstraintViolation constraintViolation = hybrisConstraintViolation.getConstraintViolation();
			final ConstraintDescriptor descriptor = constraintViolation.getConstraintDescriptor();
			final Annotation annotation = descriptor.getAnnotation();
			if (annotation.getClass().getName().equals("B2BUnitActive"))
			{
				constraintViolations.remove(hybrisConstraintViolation);
				return true;
			}
		}
		return false;
	}




	/**
	 * Sets the user in the session and updates the branch in session context.
	 * 
	 * @param userId
	 * @return A {@link de.hybris.platform.core.model.user.UserModel}
	 */
	public B2BCustomerModel login(final String userId)
	{
		final B2BCustomerModel user = userService.getUserForUID(userId, B2BCustomerModel.class);
		org.junit.Assert.assertNotNull(userId + " user is null", user);
		login(user);
		return user;
	}

	/**
	 * @param user
	 */
	public void login(final UserModel user)
	{
		userService.setCurrentUser(user);
		b2bUnitService.updateBranchInSession(sessionService.getCurrentSession(), user);
	}
}
