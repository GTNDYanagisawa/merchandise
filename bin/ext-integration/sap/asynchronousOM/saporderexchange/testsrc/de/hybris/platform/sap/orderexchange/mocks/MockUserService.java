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
package de.hybris.platform.sap.orderexchange.mocks;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.exceptions.CannotDecodePasswordException;
import de.hybris.platform.servicelayer.user.exceptions.PasswordEncoderNotFoundException;

import java.util.Collection;
import java.util.Set;


/**
 * Mock to be used for spring tests
 */
public class MockUserService implements UserService
{

	@Override
	public EmployeeModel getAdminUser()
	{
		return null;
	}

	@Override
	public UserGroupModel getAdminUserGroup()
	{
		return null;
	}

	@Override
	public Set<UserGroupModel> getAllGroups(final UserModel arg0)
	{
		return null;
	}

	@Override
	public Collection<TitleModel> getAllTitles()
	{
		return null;
	}

	@Override
	public Set<UserGroupModel> getAllUserGroupsForUser(final UserModel arg0)
	{
		return null;
	}

	@Override
	public <T extends UserGroupModel> Set<T> getAllUserGroupsForUser(final UserModel arg0, final Class<T> arg1)
	{
		return null;
	}

	@Override
	public CustomerModel getAnonymousUser()
	{
		return null;
	}

	@Override
	public UserModel getCurrentUser()
	{
		return null;
	}

	@Override
	public String getPassword(final String arg0) throws CannotDecodePasswordException, PasswordEncoderNotFoundException
	{
		return null;
	}

	@Override
	public String getPassword(final UserModel arg0) throws CannotDecodePasswordException, PasswordEncoderNotFoundException
	{
		return null;
	}

	@Override
	public TitleModel getTitleForCode(final String arg0)
	{
		return null;
	}

	@Override
	public UserModel getUser(final String arg0)
	{
		return null;
	}

	@Override
	public UserModel getUserForUID(final String arg0)
	{
		return null;
	}

	@Override
	public <T extends UserModel> T getUserForUID(final String arg0, final Class<T> arg1)
	{
		return null;
	}

	@Override
	public UserGroupModel getUserGroup(final String arg0)
	{
		return null;
	}

	@Override
	public UserGroupModel getUserGroupForUID(final String arg0)
	{
		return null;
	}

	@Override
	public <T extends UserGroupModel> T getUserGroupForUID(final String arg0, final Class<T> arg1)
	{
		return null;
	}

	@Override
	public boolean isAdmin(final UserModel arg0)
	{
		return false;
	}

	@Override
	public boolean isAnonymousUser(final UserModel arg0)
	{
		return false;
	}

	@Override
	public boolean isMemberOfGroup(final UserModel arg0, final UserGroupModel arg1)
	{
		return false;
	}

	@Override
	public boolean isMemberOfGroup(final UserGroupModel arg0, final UserGroupModel arg1)
	{
		return false;
	}

	@Override
	public boolean isUserExisting(final String arg0)
	{
		return false;
	}

	@Override
	public void setCurrentUser(final UserModel arg0)
	{

	}

	@Override
	public void setPassword(final String arg0, final String arg1) throws PasswordEncoderNotFoundException
	{

	}

	@Override
	public void setPassword(final String arg0, final String arg1, final String arg2) throws PasswordEncoderNotFoundException
	{

	}

	@Override
	public void setPassword(final UserModel arg0, final String arg1, final String arg2) throws PasswordEncoderNotFoundException
	{

	}

	@Override
	public void setPasswordWithDefaultEncoding(final UserModel arg0, final String arg1) throws PasswordEncoderNotFoundException
	{

	}

}
