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
package de.hybris.platform.cockpit.test;

import de.hybris.platform.cockpit.jalo.CockpitManager;
import de.hybris.platform.cockpit.jalo.CockpitTransactionalTest;
import de.hybris.platform.cockpit.services.config.UIConfigurationService;
import de.hybris.platform.cockpit.services.login.LoginService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.model.ModelService;

import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;


@Ignore
public abstract class UIConfigurationTestBase extends CockpitTransactionalTest
{

	protected UserModel productManagerUser;
	protected UserGroup cockpitGroup;
	protected UserGroup defaultRoleGroup;
	protected UIConfigurationService uiConfigurationService;
	protected TypeService typeService;
	protected ModelService modelService;
	protected User productManagerUserItem;
	protected LoginService loginService;

	@Before
	public void setUp() throws Exception
	{
		final UserManager userManager = UserManager.getInstance();
		this.cockpitGroup = getOrCreateUserGroup(userManager, "cockpitusergroup");
		this.defaultRoleGroup = getOrCreateUserGroup(userManager, uiConfigurationService.getFallbackRole().getName());
		cockpitGroup.addMember(defaultRoleGroup);
		productManagerUserItem = getOrCreateUser(userManager, "productmanager");
		productManagerUserItem.setPassword("test");
		userManager.getAdminUserGroup().addMember(productManagerUserItem);
		defaultRoleGroup.addMember(productManagerUserItem);
		this.productManagerUser = (UserModel) modelService.get(productManagerUserItem);
		final CockpitManager cockpitManager = CockpitManager.getInstance();

		final Media media1 = createMedia("TestListViewConfigurationProductManager.xml");
		cockpitManager.createCockpitUIComponentConfiguration("listViewConfigurationFactory", "listViewProductManager",
				"Product.test", defaultRoleGroup, media1);

		final Media media2 = createMedia("TestListViewConfigurationCockpitUser.xml");
		cockpitManager.createCockpitUIComponentConfiguration("listViewConfigurationFactory", "listViewCockpitUser", "Product.test",
				cockpitGroup, media2);

		final Media media3 = createMedia("TestEditorProductManager.xml");
		cockpitManager.createCockpitUIComponentConfiguration("editorConfigurationFactory", "editorProductManager", "Product.test",
				defaultRoleGroup, media3);

		final Media media4 = createMedia("TestAdvancedSearchProductManager.xml");
		cockpitManager.createCockpitUIComponentConfiguration("advancedSearchConfigurationFactory", "advancedSearchProductManager",
				"Product.test", defaultRoleGroup, media4);

		final Media media5 = createMedia("TestBaseProductManager.xml");
		cockpitManager.createCockpitUIComponentConfiguration("baseConfigurationFactory", "base", "Product.test", null, media5);

		CockpitManager.getInstance().createCockpitItemTemplate("test", Product.class);

		getOrCreateLanguage("de");

		loginService.doLogin("productmanager", "test", null);

	}

	protected User getOrCreateUser(final UserManager userManager, final String login)
	{
		User ret = null;
		try
		{
			ret = userManager.getUserByLogin(login);
		}
		catch (final JaloItemNotFoundException e)
		{
			try
			{
				ret = userManager.createUser(login);
			}
			catch (final ConsistencyCheckException e1)
			{
				throw new JaloSystemException(e1);
			}
		}
		return ret;
	}

	protected UserGroup getOrCreateUserGroup(final UserManager userManager, final String groupID)
	{
		UserGroup ret;
		try
		{
			ret = userManager.getUserGroupByGroupID(groupID);
		}
		catch (final JaloItemNotFoundException e)
		{
			try
			{
				ret = userManager.createUserGroup(groupID);
			}
			catch (final ConsistencyCheckException e1)
			{
				throw new JaloSystemException(e1);
			}
		}
		return ret;
	}

	private Media createMedia(final String file) throws JaloBusinessException
	{
		final MediaManager mediaManager = MediaManager.getInstance();
		final Media media = mediaManager.createMedia("listViewConfiguration");
		final URL resource = getClass().getResource("/test/" + file);
		if (resource == null)
		{
			throw new NullPointerException("No such configuration XML file found: " + file);
		}
		media.setURL(resource.toExternalForm());
		media.setRealFileName(file);
		return media;
	}

	public void setUiConfigurationService(final UIConfigurationService uiConfigurationService)
	{
		this.uiConfigurationService = uiConfigurationService;
	}

	public void setCockpitTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public void setLoginService(final LoginService loginService)
	{
		this.loginService = loginService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
