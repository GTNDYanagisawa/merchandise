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
package de.hybris.platform.catalog.interceptors;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fest.assertions.Fail;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CheckVersionsRemoveInterceptorTest
{
	@Mock
	private CatalogVersionDao catalogVersionDao;

	@Mock
	private L10NService l10nService;

	private CheckVersionsRemoveInterceptor defaultRemoveCatalogCheckVersions;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultRemoveCatalogCheckVersions = new CheckVersionsRemoveInterceptor();
		defaultRemoveCatalogCheckVersions.setCatalogVersionDao(catalogVersionDao);
		defaultRemoveCatalogCheckVersions.setL10nService(l10nService);
	}

	//catalog

	@Test
	public void checkCanRemoveTest()
	{
		final CatalogModel model = new CatalogModel();
		final Set<CatalogVersionModel> catalogVersions = new HashSet<CatalogVersionModel>();

		final CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
		catalogVersionModel1.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel2 = new CatalogVersionModel();
		catalogVersionModel2.setActive(Boolean.FALSE);
		catalogVersions.addAll(Arrays.asList(catalogVersionModel1, catalogVersionModel2));

		model.setCatalogVersions(catalogVersions);
		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(model, null);
		}
		catch (final InterceptorException e)
		{
			Fail.fail();
		}
	}

	@Test
	public void checkCannotDefaultRemoveTest()
	{
		final CatalogModel model = new CatalogModel();
		final Set<CatalogVersionModel> catalogVersions = new HashSet<CatalogVersionModel>();

		final CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
		catalogVersionModel1.setActive(Boolean.TRUE);
		final CatalogVersionModel catalogVersionModel2 = new CatalogVersionModel();
		catalogVersionModel2.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel3 = new CatalogVersionModel();
		catalogVersionModel3.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel4 = new CatalogVersionModel();
		catalogVersionModel4.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel5 = new CatalogVersionModel();
		catalogVersionModel5.setActive(Boolean.FALSE);


		catalogVersions.addAll(Arrays.asList(catalogVersionModel1, catalogVersionModel2, catalogVersionModel3,
				catalogVersionModel4, catalogVersionModel5));

		model.setCatalogVersions(catalogVersions);

		Mockito.when(catalogVersionDao.findAllCategoriesCount(catalogVersionModel2)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllKeywordsCount(catalogVersionModel3)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllMediasCount(catalogVersionModel4)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllProductsCount(catalogVersionModel5)).thenReturn(Integer.valueOf(1));

		final Matcher<Object[]> match = new ArgumentMatcher()
		{

			@Override
			public boolean matches(final Object argument)
			{
				if (argument instanceof Object[])
				{

					final Set<CatalogVersionModel> res = (Set<CatalogVersionModel>) ((Object[]) argument)[0];
					if (!res.contains(catalogVersionModel1))
					{
						return false;
					}
					if (!res.contains(catalogVersionModel2))
					{
						return false;
					}
					if (!res.contains(catalogVersionModel3))
					{
						return false;
					}
					if (!res.contains(catalogVersionModel4))
					{
						return false;
					}
					if (!res.contains(catalogVersionModel5))
					{
						return false;
					}
					return true;
				}
				return false;
			}
		};


		Mockito.when(
				l10nService.getLocalizedString(Mockito.eq("error.catalog.contains_non_removable_versions"), Mockito.argThat(match)))
				.thenReturn("error msg");
		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(model, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}
	}


	///catalog version
	@Test
	public void checkCanRemoveCatalogVersionTest()
	{

		final Set<CatalogVersionModel> catalogVersions = new HashSet<CatalogVersionModel>();

		final CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
		catalogVersionModel1.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel2 = new CatalogVersionModel();
		catalogVersionModel2.setActive(Boolean.FALSE);
		catalogVersions.addAll(Arrays.asList(catalogVersionModel1, catalogVersionModel2));

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel1, null);
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel2, null);
		}
		catch (final InterceptorException e)
		{
			Fail.fail();
		}
	}

	@Test
	public void checkCannotDefaultRemoveCatalogVersionTest()
	{
		final CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
		catalogVersionModel1.setActive(Boolean.TRUE);
		final CatalogVersionModel catalogVersionModel2 = new CatalogVersionModel();
		catalogVersionModel2.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel3 = new CatalogVersionModel();
		catalogVersionModel3.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel4 = new CatalogVersionModel();
		catalogVersionModel4.setActive(Boolean.FALSE);
		final CatalogVersionModel catalogVersionModel5 = new CatalogVersionModel();
		catalogVersionModel5.setActive(Boolean.FALSE);

		Mockito.when(catalogVersionDao.findAllCategoriesCount(catalogVersionModel2)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllKeywordsCount(catalogVersionModel3)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllMediasCount(catalogVersionModel4)).thenReturn(Integer.valueOf(1));
		Mockito.when(catalogVersionDao.findAllProductsCount(catalogVersionModel5)).thenReturn(Integer.valueOf(1));

		final Matcher<Object[]> match = new ArgumentMatcher()
		{

			@Override
			public boolean matches(final Object argument)
			{
				if (argument instanceof Object[])
				{

					final Set<CatalogVersionModel> res = (Set<CatalogVersionModel>) ((Object[]) argument)[0];
					if (res.size() != 1)
					{
						return false;
					}

					if (res.contains(catalogVersionModel1))
					{
						return true;
					}
					if (res.contains(catalogVersionModel2))
					{
						return true;
					}
					if (res.contains(catalogVersionModel3))
					{
						return true;
					}
					if (res.contains(catalogVersionModel4))
					{
						return true;
					}
					if (res.contains(catalogVersionModel5))
					{
						return true;
					}
					return false;
				}
				return false;
			}
		};


		Mockito.when(
				l10nService.getLocalizedString(Mockito.eq("error.catalog.contains_non_removable_versions"), Mockito.argThat(match)))
				.thenReturn("error msg");

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel1, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel2, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel3, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel4, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}

		try
		{
			defaultRemoveCatalogCheckVersions.onRemove(catalogVersionModel5, null);
			Fail.fail();
		}
		catch (final InterceptorException e)
		{
			assertThat(e.getMessage()).isEqualTo("[null]:error msg");
		}
	}

}
