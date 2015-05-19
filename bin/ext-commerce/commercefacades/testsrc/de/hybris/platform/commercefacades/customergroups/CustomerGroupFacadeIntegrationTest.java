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
package de.hybris.platform.commercefacades.customergroups;

import static org.fest.assertions.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customergroups.exceptions.InvalidCustomerGroupException;
import de.hybris.platform.commercefacades.customergroups.impl.DefaultCustomerGroupFacade;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.testframework.TestUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration tests for {@link DefaultCustomerGroupFacade}
 */
@IntegrationTest
public class CustomerGroupFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private CustomerGroupFacade customerGroupFacade;
	@Resource
	private CustomerFacade customerFacade;
	@Resource
	private ModelService modelService;
	@Resource
	private UserGroupDao userGroupDao;
	@Resource
	private UserService userService;
	@Resource
	private BaseSiteService baseSiteService;

	private static final String UID = "abcTestUid";
	private static final String LOCALIZED_NAME = "abcTestName";
	private static final String CUSTOMER_GROUP_UID = "customergroup";
	private static final String TEST_GROUP_ID = "testCustomerGroup";
	private static final String TEST_MEMBER1_ID = "member1";
	private static final String TEST_MEMBER2_ID = "member2";
	private static final String TEST_SUBGROUP = "subgroup";
	private static final String TEST_SITE = "test";


	@Before
	public void before() throws Exception
	{
		importCsv("/commercefacades/test/customerGroupFacadeIntegrationTest.impex", "UTF-8");
	}

	@Test
	public void testCustomerGroupCreated()
	{
		//given
		UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(UID);
		Assert.assertNull(customerGroup);
		//when
		customerGroupFacade.createCustomerGroup(UID, LOCALIZED_NAME);
		//then
		customerGroup = userGroupDao.findUserGroupByUid(UID);
		Assert.assertNotNull(customerGroup);
		Assert.assertNotNull(customerGroup.getGroups());
		Assert.assertTrue(customerGroup.getGroups().contains(userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID)));
		Assert.assertEquals(LOCALIZED_NAME, customerGroup.getLocName());
	}

	@Test(expected = ModelSavingException.class)
	public void testTryCreateCustomerGroupWithDuplicateId()
	{
		customerGroupFacade.createCustomerGroup(UID, LOCALIZED_NAME);
		try
		{
			TestUtils.disableFileAnalyzer("Creating user group with no unique id should throw exception");
			customerGroupFacade.createCustomerGroup(UID, LOCALIZED_NAME);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test(expected = ModelSavingException.class)
	public void testTryCreateCustomerGroupWithEmptyUid()
	{
		try
		{
			TestUtils.disableFileAnalyzer("Creating user group with empty id should throw exception");
			customerGroupFacade.createCustomerGroup(null, LOCALIZED_NAME);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testTryCreateCustomerGroupWithEmptyName()
	{
		customerGroupFacade.createCustomerGroup(UID, null);
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(UID);
		Assert.assertNotNull(customerGroup);
		Assert.assertNull(customerGroup.getName());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testTryCreateCustomerGroupWhenSuperGroupDoesNotExist()
	{
		modelService.remove(userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID));
		Assert.assertNull(CUSTOMER_GROUP_UID + " group not found", userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID));
		try
		{
			TestUtils
					.disableFileAnalyzer("Creating customer group when group with id 'customergroup' does not exists should throw exception");
			customerGroupFacade.createCustomerGroup(UID, LOCALIZED_NAME);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testAddUserToCustomerGroup() throws Exception
	{
		//given
		//when register customer, user uid =  customer_login.toLowerCase()
		final String login = generateUniqueLogin();
		final UserModel user = registerUser(login);
		Assert.assertNotNull(user);

		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		Assert.assertNotNull(CUSTOMER_GROUP_UID + "is null", customerGroup);
		//when
		customerGroupFacade.addUserToCustomerGroup(CUSTOMER_GROUP_UID, user.getUid());
		modelService.refresh(customerGroup);
		final Set<PrincipalModel> members  = customerGroup.getMembers();
		//then
		Assert.assertTrue(user + "should be a member of " + CUSTOMER_GROUP_UID, members.contains(user));
	}

	@Test
	public void testAddUserToCustomerSubGroup() throws Exception
	{
		//when register customer, user uid =  customer_login.toLowerCase()
		final String login = generateUniqueLogin();
		final UserModel user = registerUser(login);
		Assert.assertNotNull(user);

		final String customerSubGroupName = "customerSubGroupName";
		customerGroupFacade.createCustomerGroup(customerSubGroupName, null);
		final UserGroupModel customerSubGroup = userGroupDao.findUserGroupByUid(customerSubGroupName);
		Assert.assertNotNull(customerSubGroup);
		Assert.assertFalse(customerSubGroup.getMembers().contains(user));
		customerGroupFacade.addUserToCustomerGroup(customerSubGroupName, user.getUid());
		modelService.refresh(customerSubGroup);
		Assert.assertTrue(customerSubGroup.getMembers().contains(user));
	}

	@Test
	public void testAddUserToCustomerSubSubGroup() throws Exception
	{
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		final String userGroup1 = "userGroup1";
		final String userGroup2 = "userGroup2";
		final UserGroupModel ug1 = modelService.create(UserGroupModel.class);
		ug1.setUid(userGroup1);
		modelService.save(ug1);

		final UserGroupModel ug2 = modelService.create(UserGroupModel.class);
		ug2.setUid(userGroup2);
		modelService.save(ug2);

		final Set<PrincipalModel> customerGroupMembers = new HashSet<PrincipalModel>(customerGroup.getMembers());
		customerGroupMembers.add(ug1);
		customerGroup.setMembers(customerGroupMembers);
		modelService.save(customerGroup);

		final Set<PrincipalModel> ug1Members = new HashSet<PrincipalModel>();
		ug1Members.add(ug2);
		ug1.setMembers(ug1Members);
		modelService.save(ug1);

		final String login = generateUniqueLogin();
		final UserModel user = registerUser(login);
		Assert.assertNotNull(user);

		customerGroupFacade.addUserToCustomerGroup(ug2.getUid(), user.getUid());
		modelService.refresh(ug2);
		Assert.assertNotNull(ug2.getMembers());
		Assert.assertTrue(ug2.getMembers().contains(user));


	}

	@Test
	public void testTryToAddNotExistingUserToCustomerGroup()
	{
		final String notExistingUserUid = "notExistingUserUid";
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		Assert.assertNotNull(customerGroup);
		final int numberOfMembersBeforeAdd = customerGroup.getMembers() != null ? customerGroup.getMembers().size() : 0;
		try
		{
			TestUtils.disableFileAnalyzer("Add not existing user to customer group should throw exception");
			customerGroupFacade.addUserToCustomerGroup(CUSTOMER_GROUP_UID, notExistingUserUid);
			Assert.fail("Add not existing user to customer group should throw exception");
		}
		catch (final Exception ex)//NOPMD
		{
			//throwing exception is expected behavior
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		modelService.refresh(customerGroup);
		final int numberOfMemberAfterAdd = customerGroup.getMembers() != null ? customerGroup.getMembers().size() : 0;
		Assert.assertTrue("Number of members of customer group with id " + CUSTOMER_GROUP_UID + " should not change",
				numberOfMembersBeforeAdd == numberOfMemberAfterAdd);
	}

	@Test
	public void testTryToAddUserToNotExistingGroup() throws Exception
	{
		final String login = generateUniqueLogin().toLowerCase();
		final String notExistingUserGroupUid = "notExistingUserGroupUid";
		final UserModel user = registerUser(login);
		Assert.assertNotNull(user);
		try
		{
			TestUtils.disableFileAnalyzer("Add user to not existing group should throw exception");
			customerGroupFacade.addUserToCustomerGroup(notExistingUserGroupUid, user.getUid());
			Assert.fail("Add user to not existing group should throw exception");
		}
		catch (final Exception ex)//NOPMD
		{
			// throwing exception is expected behavior
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testTryToAddUserToUserGroupWhichIsNotSubGroupOfCustomerGroup() throws Exception
	{
		final String userGroupId = "userGroupIdWhichIsNotCustomerGroup";
		final UserGroupModel userGroup = createUserGroup(userGroupId);
		final String login = generateUniqueLogin();
		final UserModel user = registerUser(login);
		Assert.assertNotNull(userGroup);
		Assert.assertNotNull(user);

		try
		{
			TestUtils.disableFileAnalyzer("Add user to user group which is not sub-group of customer group should throw exception");
			customerGroupFacade.addUserToCustomerGroup(userGroup.getUid(), user.getUid());
			Assert.fail("Add user to user group which is not sub-group of customer group should throw exception");
		}
		catch (final Exception ex) //NOPMD
		{
			//throwing exception is expected behavior
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testRemoveUserFromCustomerGroup() throws Exception
	{
		//
		final UserModel user = registerUser(generateUniqueLogin());
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		customerGroupFacade.addUserToCustomerGroup(customerGroup.getUid(), user.getUid());

		modelService.refresh(customerGroup);
		Assert.assertTrue(customerGroup.getMembers().contains(user));
		customerGroupFacade.removeUserFromCustomerGroup(customerGroup.getUid(), user.getUid());
		modelService.refresh(customerGroup);
		Assert.assertFalse(customerGroup.getMembers().contains(user));

		final String customerSubGroupName = "customerGroupTest";
		customerGroupFacade.createCustomerGroup(customerSubGroupName, null);
		final UserGroupModel customerSubGroup = userGroupDao.findUserGroupByUid(customerSubGroupName);
		customerGroupFacade.addUserToCustomerGroup(customerSubGroup.getUid(), user.getUid());
		modelService.refresh(customerSubGroup);
		Assert.assertTrue(customerSubGroup.getMembers().contains(user));
		customerGroupFacade.removeUserFromCustomerGroup(customerSubGroup.getUid(), user.getUid());
		modelService.refresh(customerSubGroup);
		Assert.assertFalse(customerSubGroup.getMembers().contains(user));
	}

	@Test
	public void testRemoveUserFromCustomerGroupWhileHeHasNotBeenMemberOfThisGroup() throws Exception
	{
		final UserModel user = registerUser(generateUniqueLogin());
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		try
		{
			customerGroupFacade.removeUserFromCustomerGroup(customerGroup.getUid(), user.getUid());
		}
		catch (final Exception ex)
		{
			Assert.fail("Remove user from CustomerGroup while he has not been member of this group should not throw exception");
		}
	}

	@Test
	public void testTryToRemoveUserFromNotCustomerGroup() throws Exception
	{
		final UserModel user = registerUser(generateUniqueLogin());
		final UserGroupModel group = createUserGroup("NotCustomerGroup");
		try
		{
			TestUtils.disableFileAnalyzer("Trying to remove user from not customer group should throw exception");
			customerGroupFacade.removeUserFromCustomerGroup(group.getUid(), user.getUid());
			Assert.fail("Trying to remove user from not customer group should throw exception");

		}
		catch (final Exception ex)// NOPMD
		{
			// throwing exception is expected behavior
		}
		finally
		{
			//
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testGetCustomerGroupForCurrentUser() throws UnknownIdentifierException, IllegalArgumentException,
			DuplicateUidException
	{
		final String userLogin = generateUniqueLogin();
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		final String userGroup2Uid = "userGroup2Uid";
		final String userGroup2Name = "userGroup2Name";
		customerGroupFacade.createCustomerGroup(userGroup2Uid, userGroup2Name);
		final UserGroupModel secondCustomerGroup = userGroupDao.findUserGroupByUid(userGroup2Uid);

		final UserModel user = registerUser(userLogin);
		userService.setCurrentUser(user);
		// Customer gets added to 'customergroup' by default
		List<UserGroupData> allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(ReflectionToStringBuilder.toString(allGroupsForUser), 0, allGroupsForUser.size());

		customerGroupFacade.addUserToCustomerGroup(customerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(1, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);

		customerGroupFacade.addUserToCustomerGroup(secondCustomerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(2, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, secondCustomerGroup);

		customerGroupFacade.removeUserFromCustomerGroup(secondCustomerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(1, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);

		customerGroupFacade.removeUserFromCustomerGroup(customerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(0, allGroupsForUser.size());

		// check that getCustomerGroupsForCurrentUser does not return user groups which are not customer groups 
		final UserGroupModel notCustomerGroup = createUserGroup("notCustomerGroup");
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>();
		members.add(user);
		notCustomerGroup.setMembers(members);
		modelService.save(notCustomerGroup);
		modelService.refresh(user);
		Assert.assertEquals(1, user.getGroups().size());
		Assert.assertTrue(user.getGroups().contains(notCustomerGroup));
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForCurrentUser();
		Assert.assertEquals(0, allGroupsForUser.size());
	}

	@Test
	public void testGetCustomerGroup()
	{
		UserGroupData resultCustomerGroup = customerGroupFacade.getCustomerGroup(TEST_GROUP_ID,
				Collections.singleton(UserGroupOption.BASIC));
		Assert.assertNotNull(resultCustomerGroup);
		Assert.assertEquals(TEST_GROUP_ID, resultCustomerGroup.getUid());
		Assert.assertEquals(2, resultCustomerGroup.getMembersCount().intValue());
		Assert.assertNull(resultCustomerGroup.getMembers());
		Assert.assertNull(resultCustomerGroup.getSubGroups());

		resultCustomerGroup = customerGroupFacade.getCustomerGroup(TEST_GROUP_ID, Collections.singleton(UserGroupOption.MEMBERS));
		Assert.assertNotNull(resultCustomerGroup);
		Assert.assertEquals(TEST_GROUP_ID, resultCustomerGroup.getUid());
		Assert.assertEquals(2, resultCustomerGroup.getMembersCount().intValue());
		Assert.assertNull(resultCustomerGroup.getSubGroups());
		assertThat(resultCustomerGroup.getMembers()).hasSize(2);
		assertGroupMembersContainUid(resultCustomerGroup.getMembers(), TEST_MEMBER1_ID);
		assertGroupMembersContainUid(resultCustomerGroup.getMembers(), TEST_MEMBER2_ID);

		resultCustomerGroup = customerGroupFacade.getCustomerGroup(TEST_GROUP_ID, Collections.singleton(UserGroupOption.SUBGROUPS));
		Assert.assertNotNull(resultCustomerGroup);
		Assert.assertEquals(TEST_GROUP_ID, resultCustomerGroup.getUid());
		Assert.assertEquals(2, resultCustomerGroup.getMembersCount().intValue());
		Assert.assertNull(resultCustomerGroup.getMembers());
		assertThat(resultCustomerGroup.getSubGroups()).hasSize(1);
		Assert.assertEquals(TEST_SUBGROUP, resultCustomerGroup.getSubGroups().get(0).getUid());

		resultCustomerGroup = customerGroupFacade.getCustomerGroup(TEST_GROUP_ID,
				new HashSet(Arrays.asList(UserGroupOption.MEMBERS, UserGroupOption.SUBGROUPS)));
		Assert.assertNotNull(resultCustomerGroup);
		Assert.assertEquals(TEST_GROUP_ID, resultCustomerGroup.getUid());
		Assert.assertEquals(2, resultCustomerGroup.getMembersCount().intValue());
		assertThat(resultCustomerGroup.getMembers()).hasSize(2);
		assertGroupMembersContainUid(resultCustomerGroup.getMembers(), TEST_MEMBER1_ID);
		assertGroupMembersContainUid(resultCustomerGroup.getMembers(), TEST_MEMBER2_ID);
		assertThat(resultCustomerGroup.getSubGroups()).hasSize(1);
		Assert.assertEquals(TEST_SUBGROUP, resultCustomerGroup.getSubGroups().get(0).getUid());

	}

	private void assertGroupMembersContainUid(final List<? extends PrincipalData> members, final String testMember1Id)
	{
		boolean found = false;
		for (final PrincipalData principal : members)
		{
			if (testMember1Id.equals(principal.getUid()))
			{
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);
	}

	@Test
	public void testTryToGetSubGroupsOfGroupWhichDoesNotExist()
	{
		try
		{
			TestUtils
					.disableFileAnalyzer("customerGroupFacade.getSubGroupsOfCustomerGroup should throw exception when pass uid of group which does not exists");
			customerGroupFacade.getCustomerGroup("groupName" + System.currentTimeMillis(),
					Collections.singleton(UserGroupOption.BASIC));
			Assert.fail("customerGroupFacade.getSubGroupsOfCustomerGroup should throw exception when pass uid group which does not exists");
		}
		catch (final UnknownIdentifierException ex)//NOPMD
		{
			//thrown exception is expected behavior
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testTryToGetSubGroupOfGroupWhichIsNotCustomerGroup()
	{
		final UserGroupModel userGroup = createUserGroup("userGroup_" + System.currentTimeMillis());

		try
		{
			TestUtils
					.disableFileAnalyzer("customerGroupFacade.getSubGroupsOfCustomerGroup should throw exception when pass uid of group which is not customer Group");
			customerGroupFacade.getCustomerGroup(userGroup.getUid(), Collections.singleton(UserGroupOption.BASIC));
			Assert.fail("customerGroupFacade.getSubGroupsOfCustomerGroup should throw exception when pass uid of group which is not customer Group");
		}
		catch (final InvalidCustomerGroupException ex)//NOPMD
		{
			//throwing exception is expected behavior
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testGetCustomerGroupForUser() throws UnknownIdentifierException, IllegalArgumentException, DuplicateUidException
	{
		final String userLogin = generateUniqueLogin();
		final UserGroupModel customerGroup = userGroupDao.findUserGroupByUid(CUSTOMER_GROUP_UID);
		final String userGroup2Uid = "userGroup2Uid";
		final String userGroup2Name = "userGroup2Name";
		customerGroupFacade.createCustomerGroup(userGroup2Uid, userGroup2Name);
		final UserGroupModel secondCustomerGroup = userGroupDao.findUserGroupByUid(userGroup2Uid);
		final UserModel user = registerUser(userLogin);

		List<UserGroupData> allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(user.getUid());
		Assert.assertEquals(ReflectionToStringBuilder.toString(allGroupsForUser), 0, allGroupsForUser.size());

		customerGroupFacade.addUserToCustomerGroup(customerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(user.getUid());
		Assert.assertEquals(1, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);

		customerGroupFacade.addUserToCustomerGroup(secondCustomerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(user.getUid());
		Assert.assertEquals(2, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, secondCustomerGroup);

		customerGroupFacade.removeUserFromCustomerGroup(secondCustomerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(user.getUid());
		Assert.assertEquals(1, allGroupsForUser.size());
		assertThaUserGroupDataListContainEquivalentUserGroupModel(allGroupsForUser, customerGroup);

		customerGroupFacade.removeUserFromCustomerGroup(customerGroup.getUid(), user.getUid());
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(userLogin);
		Assert.assertEquals(0, allGroupsForUser.size());

		// check that getCustomerGroupsForUser does not return user groups which are not customer groups 
		final UserGroupModel notCustomerGroup = createUserGroup("notCustomerGroup");
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>();
		members.add(user);
		notCustomerGroup.setMembers(members);
		modelService.save(notCustomerGroup);
		modelService.refresh(user);
		Assert.assertEquals(1, user.getGroups().size());
		Assert.assertTrue(user.getGroups().contains(notCustomerGroup));
		allGroupsForUser = customerGroupFacade.getCustomerGroupsForUser(userLogin);
		Assert.assertEquals(0, allGroupsForUser.size());
	}

	private String generateUniqueLogin()
	{
		return "test" + System.currentTimeMillis();
	}

	@Test
	public void testGetAllCustomerGroups()
	{
		final PageOption simplePageOption = PageOption.createWithoutLimits();
		UserGroupDataList userGroupDataList = customerGroupFacade.getAllCustomerGroups(simplePageOption);
		final int sizeAtTheBeginning = userGroupDataList.getUserGroups().size();

		assertCustomerGroupsContainGroup(userGroupDataList.getUserGroups(), CUSTOMER_GROUP_UID);

		final String uid1 = TEST_GROUP_ID + "1";
		final String uid2 = TEST_GROUP_ID + "2";
		final String uid3 = TEST_GROUP_ID + "3";
		final String uid4 = TEST_GROUP_ID + "4";
		customerGroupFacade.createCustomerGroup(uid1, null);
		userGroupDataList = customerGroupFacade.getAllCustomerGroups(simplePageOption);
		Assert.assertEquals(sizeAtTheBeginning + 1, userGroupDataList.getUserGroups().size());
		assertGroupMembersContainUid(userGroupDataList.getUserGroups(), uid1);
		customerGroupFacade.createCustomerGroup(uid2, null);
		userGroupDataList = customerGroupFacade.getAllCustomerGroups(simplePageOption);
		Assert.assertEquals(sizeAtTheBeginning + 2, userGroupDataList.getUserGroups().size());
		assertGroupMembersContainUid(userGroupDataList.getUserGroups(), uid1);
		assertGroupMembersContainUid(userGroupDataList.getUserGroups(), uid2);

		createUserGroup(uid3);
		userGroupDataList = customerGroupFacade.getAllCustomerGroups(simplePageOption);
		Assert.assertEquals(sizeAtTheBeginning + 2, userGroupDataList.getUserGroups().size());
		Assert.assertEquals(0, userGroupDataList.getCurrentPage().intValue());
		Assert.assertEquals(1, userGroupDataList.getNumberOfPages().intValue());
		Assert.assertEquals(Integer.MAX_VALUE, userGroupDataList.getPageSize().intValue());
		Assert.assertEquals(sizeAtTheBeginning + 2, userGroupDataList.getTotalNumber().intValue());

		customerGroupFacade.createCustomerGroup(uid4, null);
		final int smallPageSize = 2;
		final int expectedNumberOfcustomerGroup = sizeAtTheBeginning + 3;
		final int expectedNumberOfPages = (int) Math.ceil((double) expectedNumberOfcustomerGroup / (double) smallPageSize);

		for (int currentPage = 0; currentPage < expectedNumberOfPages; currentPage++)
		{
			final PageOption advancedPageOption = PageOption.createForPageNumberAndPageSize(currentPage, smallPageSize);
			userGroupDataList = customerGroupFacade.getAllCustomerGroups(advancedPageOption);
			Assert.assertEquals(Integer.valueOf(currentPage), userGroupDataList.getCurrentPage());
			Assert.assertEquals(Integer.valueOf(expectedNumberOfPages), userGroupDataList.getNumberOfPages());
			Assert.assertEquals(Integer.valueOf(smallPageSize), userGroupDataList.getPageSize());
			Assert.assertEquals(Integer.valueOf(expectedNumberOfcustomerGroup), userGroupDataList.getTotalNumber());
		}
	}

	private void assertCustomerGroupsContainGroup(final List<UserGroupData> userGroups, final String customerGroupUid)
	{
		boolean contains = false;
		for (final UserGroupData userGroup : userGroups)
		{
			if (userGroup.getUid().equals(customerGroupUid))
			{
				contains = true;
				break;
			}
		}
		Assert.assertTrue(contains);
	}

	private void assertThaUserGroupDataListContainEquivalentUserGroupModel(final List<UserGroupData> userGroupDataList,
			final UserGroupModel model)
	{
		boolean found = false;
		for (final UserGroupData data : userGroupDataList)
		{
			if (StringUtils.equals(data.getUid(), model.getUid()) && StringUtils.equals(data.getName(), model.getLocName()))
			{
				found = true;
				break;
			}
		}
		Assert.assertTrue("Cannot find user group with uid: " + model.getUid() + ", and name: " + model.getName(), found);
	}

	private UserGroupModel createUserGroup(final String uid)
	{
		final UserGroupModel userGroup = new UserGroupModel();
		userGroup.setUid(uid);
		modelService.save(userGroup);
		return userGroupDao.findUserGroupByUid(uid);
	}

	private UserModel registerUser(final String login) throws UnknownIdentifierException, IllegalArgumentException,
			DuplicateUidException
	{
		final BaseSiteModel site = new BaseSiteModel();
		site.setName(TEST_SITE, Locale.ENGLISH);
		site.setUid(TEST_SITE);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);

		try
		{
			userService.getTitleForCode("mr");
		}
		catch (UnknownIdentifierException e)
		{
			final TitleModel titleModel = new TitleModel();
			titleModel.setCode("mr");
			modelService.save(titleModel);
		}


		final RegisterData registerData = new RegisterData();
		registerData.setFirstName("firstName");
		registerData.setLastName("lastName");
		registerData.setLogin(login);
		registerData.setPassword("***");
		registerData.setTitleCode("mr");

		customerFacade.register(registerData);
		final UserModel user = userService.getUserForUID(login);
		 // clear groups since JaloIntiDefaultsInterceptor is setting customergroup for customer type
		user.setGroups(Collections.EMPTY_SET);
		modelService.save(user);
		return user;
	}
}
