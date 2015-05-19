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
package de.hybris.platform.cockpit.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.CockpitCollectionService;
import de.hybris.platform.cockpit.daos.CockpitObjectAbstractCollectionDao;
import de.hybris.platform.cockpit.enums.CockpitSpecialCollectionType;
import de.hybris.platform.cockpit.model.CockpitObjectAbstractCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectSpecialCollectionModel;
import de.hybris.platform.cockpit.model.collection.ObjectCollection;
import de.hybris.platform.cockpit.model.collection.impl.ObjectCollectionImpl;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.impl.ObjectCollectionServiceImpl;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test class that tests {@link ObjectCollectionService}
 */
@UnitTest
public class ObjectCollectionServiceTest
{
	private ObjectCollectionServiceImpl objectCollectionService;
	private CockpitObjectCollectionModel abstractCollectionModel;
	private CockpitObjectSpecialCollectionModel objectSpecialCollectionModel;
	private PK collectionPK;
	private ObjectCollection objectCollection;

	@Mock
	private CockpitObjectAbstractCollectionDao abstractCollectionDao;
	@Mock
	private ModelService modelService;
	@Mock
	private TypeService typeService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private CockpitCollectionService cockpitCollectionService;
	@Mock
	private ItemModelContextImpl context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		objectCollectionService = new ObjectCollectionServiceImpl();
		objectCollectionService.setCockpitTypeService(typeService);
		objectCollectionService.setEnumerationService(enumerationService);
		objectCollectionService.setModelService(modelService);
		objectCollectionService.setObjectCollectionDao(abstractCollectionDao);
		objectCollectionService.setObjectTypeFilterList(Collections.singletonList("testType"));
		objectCollectionService.setCockpitCollectionService(cockpitCollectionService);

		collectionPK = PK.fromLong(1234567890);
		final LocaleProvider localeProvider = mock(LocaleProvider.class);

		abstractCollectionModel = mock(CockpitObjectCollectionModel.class);
		when(abstractCollectionModel.getQualifier()).thenReturn("abstractCollection");
		when(abstractCollectionModel.getPk()).thenReturn(collectionPK);
		setLocaleProvider(abstractCollectionModel, localeProvider);

		objectCollection = mock(ObjectCollectionImpl.class);
		when(objectCollection.getPK()).thenReturn(collectionPK);

		when(modelService.get(collectionPK)).thenReturn(abstractCollectionModel);

		objectSpecialCollectionModel = mock(CockpitObjectSpecialCollectionModel.class);
		when(objectSpecialCollectionModel.getQualifier()).thenReturn("specialCollection");
		when(objectSpecialCollectionModel.getPk()).thenReturn(collectionPK);
		setLocaleProvider(objectSpecialCollectionModel, localeProvider);
	}

	@Deprecated
	private void setLocaleProvider(final AbstractItemModel model, final LocaleProvider localeProvider)
	{
		when(model.getItemModelContext()).thenReturn(context);
		when(context.getLocaleProvider(true)).thenReturn(localeProvider);
	}

	@Test
	public void testGetElements()
	{
		final ItemModel elementModel = mock(ItemModel.class);
		final TypedObject elementObject = mock(TypedObject.class);
		final List<ItemModel> elements = new ArrayList<ItemModel>();
		elements.add(elementModel);

		when(cockpitCollectionService.getElements(abstractCollectionModel, 0, 1)).thenReturn(elements);
		when(typeService.wrapItem(elementModel)).thenReturn(elementObject);

		final List<TypedObject> objects = objectCollectionService.getElements(objectCollection, 0, 1);
		Assert.assertEquals("One element expected!", 1, objects.size());
		Assert.assertEquals("Unexpected object in the list", elementObject, objects.get(0));
	}

	@Test
	public void testGetElementCount()
	{
		when(Integer.valueOf(abstractCollectionDao.getElementCount(abstractCollectionModel))).thenReturn(Integer.valueOf(23));

		final int size = objectCollectionService.getElementCount(objectCollection);
		Assert.assertEquals("23 elements expected!", 23, size);
	}

	@Test
	public void testCreateCollection()
	{
		final CockpitObjectAbstractCollectionModel colModel = new CockpitObjectCollectionModel();

		final LocaleProvider localeProvider = mock(LocaleProvider.class);
		when(localeProvider.getCurrentDataLocale()).thenReturn(Locale.ENGLISH);
		when(localeProvider.toDataLocale(Locale.ENGLISH)).thenReturn(Locale.ENGLISH);
		((ItemModelContextImpl) colModel.getItemModelContext()).setLocaleProvider(localeProvider);

		final UserModel userModel = mock(UserModel.class);
		setLocaleProvider(userModel, localeProvider);

		when(modelService.create(CockpitObjectCollectionModel.class)).thenReturn(colModel);
		final ObjectCollection created = objectCollectionService.createCollection("newCol", userModel);

		Assert.assertEquals("newCol", created.getQualifier());
		Assert.assertEquals(userModel, created.getUser());
	}

	@Test
	public void testCreateSpecialCollection()
	{
		final CockpitObjectSpecialCollectionModel colModel = new CockpitObjectSpecialCollectionModel();

		final LocaleProvider localeProvider = mock(LocaleProvider.class);
		when(localeProvider.getCurrentDataLocale()).thenReturn(Locale.ENGLISH);
		when(localeProvider.toDataLocale(Locale.ENGLISH)).thenReturn(Locale.ENGLISH);
		((ItemModelContextImpl) colModel.getItemModelContext()).setLocaleProvider(localeProvider);

		final UserModel userModel = mock(UserModel.class);
		setLocaleProvider(userModel, localeProvider);

		when(modelService.create(CockpitObjectSpecialCollectionModel.class)).thenReturn(colModel);
		final ObjectCollection created = objectCollectionService.createSpecialCollection("newCol", userModel,
				CockpitSpecialCollectionType.BLACKLIST);

		Assert.assertEquals("newCol", created.getQualifier());
		Assert.assertEquals(userModel, created.getUser());
		Assert.assertEquals(CockpitSpecialCollectionType.BLACKLIST, colModel.getCollectionType());
	}

	@Test
	public void testIsCollectionOwner()
	{
		final UserModel userModel = new UserModel();
		when(objectCollection.getUser()).thenReturn(userModel);

		Assert.assertEquals("Wrong owner detected", Boolean.TRUE,
				objectCollectionService.isCollectionOwner(userModel, objectCollection));
	}

	@Test
	public void testPublishCollection()
	{
		objectCollectionService.publishCollection(objectCollection);
		Assert.assertEquals("Collection user should be null", null, objectCollection.getUser());
		Assert.assertEquals("Collection mocel user should be null", null, abstractCollectionModel.getUser());
	}

	@Test
	public void testRemoveCollection()
	{
		objectCollectionService.removeCollection(objectCollection);
		verify(modelService, times(1)).remove(collectionPK);
	}

	@Test
	public void testRenameCollection()
	{
		objectCollectionService.renameCollection(objectCollection, "new name");
		verify(modelService, times(1)).save(abstractCollectionModel);
	}

	@Test
	public void testGetReadUsersForCollection()
	{
		final PrincipalModel userModel = mock(UserModel.class);

		when(abstractCollectionModel.getReadPrincipals()).thenReturn(Collections.singletonList(userModel));

		final Collection<PrincipalModel> users = objectCollectionService.getReadUsersForCollection(objectCollection);
		Assert.assertEquals("One user expected", 1, users.size());
	}

	@Test
	public void testGetWriteUsersForCollection()
	{
		final PrincipalModel userModel = mock(UserModel.class);

		when(abstractCollectionModel.getWritePrincipals()).thenReturn(Collections.singletonList(userModel));

		final Collection<PrincipalModel> users = objectCollectionService.getWriteUsersForCollection(objectCollection);
		Assert.assertEquals("One user expected", 1, users.size());
	}

	@After
	public void tearDown()
	{
		System.clearProperty(AbstractItemModel.MODEL_CONTEXT_FACTORY);
	}

}
