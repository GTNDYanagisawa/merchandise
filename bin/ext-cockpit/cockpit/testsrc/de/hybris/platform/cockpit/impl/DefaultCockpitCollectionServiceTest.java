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
package de.hybris.platform.cockpit.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.constants.CockpitConstants;
import de.hybris.platform.cockpit.daos.CockpitObjectAbstractCollectionDao;
import de.hybris.platform.cockpit.enums.CockpitSpecialCollectionType;
import de.hybris.platform.cockpit.model.CockpitObjectAbstractCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectSpecialCollectionModel;
import de.hybris.platform.cockpit.model.ObjectCollectionItemReferenceModel;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test class that tests {@link DefaultCockpitCollectionService}
 */
@UnitTest
public class DefaultCockpitCollectionServiceTest
{
	private DefaultCockpitCollectionService cockpitCollectionService;
	private CockpitObjectCollectionModel cockpitObjectCollectionModel;
	private CockpitObjectSpecialCollectionModel cockpitObjectSpecialCollectionModel;
	private PK collectionPK;

	@Mock
	private CockpitObjectAbstractCollectionDao abstractCollectionDao;
	@Mock
	private ModelService modelService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private ItemModelContextImpl context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cockpitCollectionService = new DefaultCockpitCollectionService();
		cockpitCollectionService.setEnumerationService(enumerationService);
		cockpitCollectionService.setModelService(modelService);
		cockpitCollectionService.setObjectCollectionDao(abstractCollectionDao);

		collectionPK = PK.fromLong(1234567890);
		final LocaleProvider localeProvider = mock(LocaleProvider.class);

		cockpitObjectCollectionModel = mock(CockpitObjectCollectionModel.class);
		when(cockpitObjectCollectionModel.getQualifier()).thenReturn("abstractCollection");
		when(cockpitObjectCollectionModel.getPk()).thenReturn(collectionPK);
		setLocaleProvider(cockpitObjectCollectionModel, localeProvider);

		when(modelService.get(collectionPK)).thenReturn(cockpitObjectCollectionModel);

		cockpitObjectSpecialCollectionModel = mock(CockpitObjectSpecialCollectionModel.class);
		when(cockpitObjectSpecialCollectionModel.getQualifier()).thenReturn("specialCollection");
		when(cockpitObjectSpecialCollectionModel.getPk()).thenReturn(collectionPK);
		setLocaleProvider(cockpitObjectSpecialCollectionModel, localeProvider);
	}

	@Deprecated
	private void setLocaleProvider(final AbstractItemModel model, final LocaleProvider localeProvider)
	{
		when(model.getItemModelContext()).thenReturn(context);
		when(context.getLocaleProvider(true)).thenReturn(localeProvider);
	}

	@Test
	public void testAddToCollection()
	{
		final ItemModel elementModel = mock(ItemModel.class);

		when(modelService.create(ObjectCollectionItemReferenceModel.class)).thenReturn(new ObjectCollectionItemReferenceModel());

		final int added = cockpitCollectionService.addToCollection(cockpitObjectCollectionModel,
				Collections.singletonList(elementModel));
		Assert.assertEquals("One added element expected!", 1, added);
	}

	@Test
	public void testRemoveFromCollection()
	{
		final ItemModel model = mock(ItemModel.class);
		final ItemModel notCollectionModel = mock(ItemModel.class);
		final ObjectCollectionItemReferenceModel itemReferenceModel = mock(ObjectCollectionItemReferenceModel.class);

		when(abstractCollectionDao.findElementsContainingItem(cockpitObjectCollectionModel, model)).thenReturn(
				Collections.singletonList(itemReferenceModel));
		when(abstractCollectionDao.findElementsContainingItem(cockpitObjectCollectionModel, notCollectionModel)).thenReturn(
				Collections.EMPTY_LIST);

		int removed = cockpitCollectionService.removeFromCollection(cockpitObjectCollectionModel,
				Collections.singletonList(notCollectionModel));
		Assert.assertEquals("Zero elements should be removed", 0, removed);
		verify(modelService, times(0)).remove(Mockito.any());

		removed = cockpitCollectionService.removeFromCollection(cockpitObjectCollectionModel, Collections.singletonList(model));
		Assert.assertEquals("One element should be removed", 1, removed);
		verify(modelService, times(1)).remove(Mockito.any());
	}

	@Test
	public void testGetElements()
	{
		final List<ObjectCollectionItemReferenceModel> elements = new ArrayList<ObjectCollectionItemReferenceModel>();

		final ObjectCollectionItemReferenceModel elementReference = mock(ObjectCollectionItemReferenceModel.class);
		final ItemModel elementModel = mock(ItemModel.class);

		when(elementReference.getItem()).thenReturn(elementModel);
		when(abstractCollectionDao.findElements(cockpitObjectCollectionModel, 0, 1)).thenReturn(elements);
		elements.add(elementReference);

		final List<ItemModel> objects = cockpitCollectionService.getElements(cockpitObjectCollectionModel, 0, 1);
		Assert.assertEquals("One element expected!", 1, objects.size());
		Assert.assertEquals("Unexpected object in the list", elementModel, objects.get(0));
	}

	@Test
	public void testCloneCollection()
	{
		final UserModel userModel = mock(UserModel.class);
		final BaseType newElementType = mock(BaseType.class);
		final CockpitObjectAbstractCollectionModel colModel = mock(CockpitObjectCollectionModel.class);
		final LocaleProvider localeProvider = mock(LocaleProvider.class);
		final CockpitObjectAbstractCollectionModel colModelClone = mock(CockpitObjectCollectionModel.class);
		final PK collectionClonePK = PK.fromLong(987654321);

		when(colModelClone.getPk()).thenReturn(collectionClonePK);
		setLocaleProvider(colModel, localeProvider);
		when(newElementType.getCode()).thenReturn("newElementType");
		when(modelService.get(collectionClonePK)).thenReturn(colModelClone);
		when(modelService.create(ObjectCollectionItemReferenceModel.class)).thenReturn(new ObjectCollectionItemReferenceModel());

		when(cockpitObjectCollectionModel.getItemtype()).thenReturn(CockpitObjectCollectionModel.class.getName());
		when(modelService.create(CockpitObjectCollectionModel.class)).thenReturn(colModelClone);

		final CockpitObjectAbstractCollectionModel clone = cockpitCollectionService.cloneCollection(cockpitObjectCollectionModel,
				userModel);
		Assert.assertNotNull(clone);
		Assert.assertEquals("Got wrong object as clone", collectionClonePK, clone.getPk());
	}

	@Test
	public void testGetCollectionsForUser()
	{
		final UserModel userModel = mock(UserModel.class);
		final List<ObjectCollectionItemReferenceModel> elements = new ArrayList<ObjectCollectionItemReferenceModel>();
		final ObjectCollectionItemReferenceModel elementReference = mock(ObjectCollectionItemReferenceModel.class);
		elements.add(elementReference);

		final ItemModel elementModel = mock(ItemModel.class);

		when(elementReference.getItem()).thenReturn(elementModel);
		when(elementReference.getObjectTypeCode()).thenReturn("testType");
		when(abstractCollectionDao.findElements(cockpitObjectCollectionModel, 0, 1)).thenReturn(elements);
		when(abstractCollectionDao.findCollectionsByUser(userModel)).thenReturn(
				Collections.singletonList(cockpitObjectCollectionModel));

		final List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(userModel);
		Assert.assertEquals("One element expected", 1, collections.size());
		Assert.assertEquals("Wrong object returned", collectionPK, collections.get(0).getPk());
	}

	@Test
	public void testGetSpecialCollectionsForUser()
	{
		final UserModel userModel = mock(UserModel.class);

		when(abstractCollectionDao.findSpecialCollectionsByUser(userModel)).thenReturn(
				Collections.singletonList(cockpitObjectSpecialCollectionModel));

		Assert.assertEquals("One special collection expected", 1, cockpitCollectionService.getSpecialCollectionsForUser(userModel)
				.size());
	}

	@Test
	public void testGetSpecialCollections()
	{
		final UserModel userModel = new UserModel();
		final String collectionType = CockpitSpecialCollectionType.BLACKLIST.getCode();

		when(enumerationService.getEnumerationValue(CockpitConstants.TC.COCKPITSPECIALCOLLECTIONTYPE, collectionType)).thenReturn(
				CockpitSpecialCollectionType.BLACKLIST);
		when(abstractCollectionDao.findSpecialCollections(userModel, CockpitSpecialCollectionType.BLACKLIST)).thenReturn(
				Collections.singletonList(cockpitObjectSpecialCollectionModel));

		final List<CockpitObjectSpecialCollectionModel> collections = cockpitCollectionService.getSpecialCollections(userModel,
				collectionType);
		Assert.assertEquals("One collection expected", 1, collections.size());
		Assert.assertEquals("Wrong collection returned", collectionPK, collections.get(0).getPk());
	}

	@Test
	public void testHasReadCollectionRight()
	{
		final PrincipalModel userModel = mock(UserModel.class);
		final PrincipalModel userGroup = mock(PrincipalGroupModel.class);
		when(cockpitObjectCollectionModel.getUser()).thenReturn((UserModel) userModel);

		Assert.assertTrue("Should have rights",
				cockpitCollectionService.hasReadCollectionRight(userModel, cockpitObjectCollectionModel));

		when(cockpitObjectCollectionModel.getUser()).thenReturn(null);
		when(cockpitObjectCollectionModel.getWritePrincipals()).thenReturn(Collections.singletonList(userGroup));

		Assert.assertTrue("Should have rights",
				cockpitCollectionService.hasReadCollectionRight(userGroup, cockpitObjectCollectionModel));
	}

	@Test
	public void testHasWriteCollectionRight()
	{
		final PrincipalModel userModel = mock(UserModel.class);
		final PrincipalModel userGroup = mock(PrincipalGroupModel.class);
		when(cockpitObjectCollectionModel.getUser()).thenReturn((UserModel) userModel);

		Assert.assertTrue("Should have rights",
				cockpitCollectionService.hasWriteCollectionRight(userModel, cockpitObjectCollectionModel));

		when(cockpitObjectCollectionModel.getUser()).thenReturn(null);
		when(cockpitObjectCollectionModel.getWritePrincipals()).thenReturn(Collections.singletonList(userGroup));

		Assert.assertTrue("Should have rights",
				cockpitCollectionService.hasWriteCollectionRight(userGroup, cockpitObjectCollectionModel));
	}

	@Test
	public void testIsInCollection()
	{
		final TypedObject typedObject = mock(TypedObject.class);
		final ItemModel model = mock(ItemModel.class);

		when(typedObject.getObject()).thenReturn(model);
		when(Boolean.valueOf(abstractCollectionDao.collectionContains(cockpitObjectCollectionModel, model))).thenReturn(
				Boolean.TRUE);

		Assert.assertTrue("Should be in collection", cockpitCollectionService.isInCollection(model, cockpitObjectCollectionModel));

		when(Boolean.valueOf(abstractCollectionDao.collectionContains(cockpitObjectCollectionModel, model))).thenReturn(
				Boolean.FALSE);

		Assert.assertFalse("Should not be in collection",
				cockpitCollectionService.isInCollection(model, cockpitObjectCollectionModel));
	}

	@Test
	public void testAddReadUser()
	{
		final CockpitObjectAbstractCollectionModel collectionModel = modelService.get(collectionPK);
		Assert.assertNotNull("Returned collectionModel is null!", collectionModel);
		Assert.assertEquals("Zero read users expected!", 0, collectionModel.getReadPrincipals().size());

		final PrincipalModel principalModel = new PrincipalModel();
		principalModel.setReadCollections(new ArrayList<CockpitObjectAbstractCollectionModel>());
		cockpitCollectionService.addReadUser(principalModel, cockpitObjectCollectionModel);
		Assert.assertEquals("One read user expected!", 1, principalModel.getReadCollections().size());
		verify(modelService, times(1)).save(principalModel);
	}

	@Test
	public void testAddWriteUser()
	{
		final CockpitObjectAbstractCollectionModel collectionModel = modelService.get(collectionPK);
		Assert.assertNotNull("Returned collectionModel is null!", collectionModel);
		Assert.assertEquals("Zero write users expected!", 0, collectionModel.getReadPrincipals().size());

		final PrincipalModel principalModel = new PrincipalModel();
		principalModel.setWriteCollections(new ArrayList<CockpitObjectAbstractCollectionModel>());
		cockpitCollectionService.addWriteUser(principalModel, cockpitObjectCollectionModel);
		Assert.assertEquals("One write user expected!", 1, principalModel.getWriteCollections().size());
		verify(modelService, times(1)).save(principalModel);
	}

	@Test
	public void testRemoveReadUser()
	{
		final PrincipalModel principalModel = new PrincipalModel();
		principalModel.setReadCollections(new ArrayList<CockpitObjectAbstractCollectionModel>());

		cockpitCollectionService.addReadUser(principalModel, cockpitObjectCollectionModel);
		Assert.assertEquals("One read user expected!", 1, principalModel.getReadCollections().size());

		cockpitCollectionService.removeReadUser(principalModel, cockpitObjectCollectionModel);
		Assert.assertEquals("Zero read users expected!", 0, principalModel.getReadCollections().size());
		verify(modelService, times(2)).save(principalModel);
	}

	@Test
	public void testRemoveWriteUser()
	{
		final PrincipalModel principalModel = new PrincipalModel();
		principalModel.setWriteCollections(new ArrayList<CockpitObjectAbstractCollectionModel>());

		cockpitCollectionService.addWriteUser(principalModel, cockpitObjectCollectionModel);
		Assert.assertEquals("One write user expected!", 1, principalModel.getWriteCollections().size());

		cockpitCollectionService.removeWriteUser(principalModel, cockpitObjectCollectionModel);
		verify(modelService, times(2)).save(principalModel);
	}

	@After
	public void tearDown()
	{
		System.clearProperty(AbstractItemModel.MODEL_CONTEXT_FACTORY);
	}

}
