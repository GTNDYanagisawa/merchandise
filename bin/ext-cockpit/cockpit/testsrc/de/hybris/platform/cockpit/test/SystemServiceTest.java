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

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.jalo.CockpitTest;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.services.login.LoginService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;


@IntegrationTest
public class SystemServiceTest extends CockpitTest
{
	private ModelService modelService;
	private LoginService loginService;
	private UserService userService;
	private I18NService i18nService;
	private UserModel user;
	private UserGroupModel userGroup;

	@Before
	public void setUp() throws Exception
	{
		userGroup = new UserGroupModel();
		userGroup.setUid("productlanguagemanagergroup");
		modelService.save(userGroup);
		assertThat(userGroup.getPk(), is(notNullValue()));

		user = new UserModel();
		user.setUid("productlanguagemanager");
		modelService.save(user);
		assertThat(user.getPk(), is(notNullValue()));

		userService.setPassword(user.getUid(), "test");
		user.setGroups(Collections.singleton((PrincipalGroupModel) userGroup));

		getOrCreateLanguage("de");
	}

	@Test
	public void testGetAllReadableLanguages()
	{
		final LanguageModel _en = i18nService.getLanguage("en");

		final Set<LanguageModel> readableLangs = Collections.singleton(_en);
		userGroup.setReadableLanguages(readableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final SystemService systemService = this.applicationContext.getBean(SystemService.class);
		final Set<LanguageModel> readableServiceLangs = systemService.getAllReadableLanguages();
		assertEquals("The readable languages are wrong", readableLangs, readableServiceLangs);
	}

	@Test
	public void testGetAllReadableLanguagesNothingSet()
	{
		final LanguageModel _de = i18nService.getLanguage("de");
		final LanguageModel _en = i18nService.getLanguage("en");
		final SystemService systemService = this.applicationContext.getBean(SystemService.class);

		final Set<LanguageModel> readableLangs = new HashSet<LanguageModel>();
		readableLangs.add(_de);
		readableLangs.add(_en);
		userGroup.setReadableLanguages(readableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final Set<LanguageModel> readableServiceLangs = systemService.getAllReadableLanguages();
		assertEquals("The readable languages are wrong", readableLangs, readableServiceLangs);
	}

	@Test
	public void testGetAllWritableLanguages()
	{
		final LanguageModel _en = i18nService.getLanguage("en");

		final Set<LanguageModel> writeableLangs = Collections.singleton(_en);
		userGroup.setWriteableLanguages(writeableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final SystemService systemService = this.applicationContext.getBean(SystemService.class);
		final Set<LanguageModel> writeableServiceLangs = systemService.getAllWriteableLanguages();
		assertEquals("The writeable languages are wrong", writeableLangs, writeableServiceLangs);
	}

	@Test
	public void testGetAllWritableLanguagesNothingSet()
	{
		final LanguageModel _de = i18nService.getLanguage("de");
		final LanguageModel _en = i18nService.getLanguage("en");
		final SystemService systemService = this.applicationContext.getBean(SystemService.class);

		final Set<LanguageModel> writeableLangs = new HashSet<LanguageModel>();
		writeableLangs.add(_de);
		writeableLangs.add(_en);
		userGroup.setReadableLanguages(writeableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final Set<LanguageModel> writeableServiceLangs = systemService.getAllWriteableLanguages();
		assertEquals("The writeable languages are wrong", writeableLangs, writeableServiceLangs);
	}

	@Test
	public void testGetAllReadableLanguageIsos()
	{
		final LanguageModel _en = i18nService.getLanguage("en");

		final Set<LanguageModel> readableLangs = Collections.singleton(_en);
		userGroup.setReadableLanguages(readableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final SystemService systemService = this.applicationContext.getBean(SystemService.class);
		final Set<String> readableServiceLangIsos = systemService.getAllReadableLanguageIsos();

		final Set<LanguageModel> readableServiceLangs = new HashSet<LanguageModel>();
		for (final String langIso : readableServiceLangIsos)
		{
			readableServiceLangs.add(i18nService.getLanguage(langIso));
		}
		assertEquals("The readable language isos are not correct", readableLangs, readableServiceLangs);
	}

	@Test
	public void testGetAllWritableLanguageIsos()
	{
		final LanguageModel _en = i18nService.getLanguage("en");

		final Set<LanguageModel> writeableLangs = Collections.singleton(_en);
		userGroup.setWriteableLanguages(writeableLangs);
		loginService.doLogin(user.getUid(), "test", null);

		final SystemService systemService = this.applicationContext.getBean(SystemService.class);
		final Set<String> writeableServiceLangIsos = systemService.getAllWriteableLanguageIsos();

		final Set<LanguageModel> writeableServiceLangs = new HashSet<LanguageModel>();
		for (final String langIso : writeableServiceLangIsos)
		{
			writeableServiceLangs.add(i18nService.getLanguage(langIso));
		}
		assertEquals("The writeable language isos are not correct", writeableLangs, writeableServiceLangs);
	}

	public void setLoginService(final LoginService loginService)
	{
		this.loginService = loginService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

}
