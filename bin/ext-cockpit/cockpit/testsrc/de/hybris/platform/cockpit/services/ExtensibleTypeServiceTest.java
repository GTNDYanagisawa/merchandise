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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.ExtendedType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.meta.impl.AbstractPropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.DefaultObjectType;
import de.hybris.platform.cockpit.model.meta.impl.ItemType;
import de.hybris.platform.cockpit.model.template.CockpitItemTemplateModel;
import de.hybris.platform.cockpit.services.meta.ExtendedTypeLoader;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.meta.impl.AbstractTypeLoader;
import de.hybris.platform.cockpit.services.meta.impl.DefaultExtendedTypeChain;
import de.hybris.platform.cockpit.services.meta.impl.DefaultPropertyService;
import de.hybris.platform.cockpit.services.meta.impl.ExtensibleTypeService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ExtensibleTypeServiceTest
{
	private ExtendedType fooType;
	private ExtendedType barType;

	private ExtensibleTypeService extensibleTypeService;
	private List<ExtendedTypeLoader> extendedTypeLoaders;

	private TypedObject testTypedObject;// = new DefaultTypedObject(new ItemType(new ComposedTypeModel()), new Object());

	@Mock
	private ModelService modelService;
	@Mock
	private de.hybris.platform.servicelayer.type.TypeService typeService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		extensibleTypeService = new ExtensibleTypeService()
		{
			@SuppressWarnings("deprecation")
			@Override
			protected de.hybris.platform.cockpit.model.template.CockpitItemTemplateModel fetchItemTemplate(
					final ComposedTypeModel composedType, final String templateCode)
			{
				if ("Product".equals(composedType.getCode()) && "Foo".equals(templateCode))
				{
					final CockpitItemTemplateModel cockpitItemTemplateModel = new CockpitItemTemplateModel();
					cockpitItemTemplateModel.setCode(composedType.getCode() + "." + templateCode);
					return cockpitItemTemplateModel;
				}
				return null;
			}
		};
		final DefaultExtendedTypeChain typeLoaderChain = new DefaultExtendedTypeChain();

		extensibleTypeService.setExtendedTypeChain(typeLoaderChain);
		extensibleTypeService.setModelService(modelService);
		extensibleTypeService.setPropertyService(new DefaultPropertyService());
		extensibleTypeService.setTypeService(typeService);

		extendedTypeLoaders = new ArrayList<ExtendedTypeLoader>();
		extendedTypeLoaders.add(new TestTypeLoader());
		typeLoaderChain.setExtendedTypeLoaders(extendedTypeLoaders);
	}


	@Test
	public void testExtendedTypes()
	{

		final ExtendedType extendedType = extensibleTypeService.getExtendedType("Foo");
		assertNotNull("Couldn't load extended type", extendedType);
		assertEquals("ExtendedType instance not the same as expected", fooType, extendedType);

		final ObjectType objectType = extensibleTypeService.getObjectType("Foo");
		assertEquals("Couldn't load object type", fooType, objectType);

		ObjectType objectType2 = null;
		try
		{
			objectType2 = extensibleTypeService.getObjectType("bar");
		}
		catch (final IllegalArgumentException e) // NOPMD
		{
			// ok, expected
		}
		Assert.assertNull(objectType2);


		extendedTypeLoaders.add(new TestTypeLoaderExt());

		objectType2 = extensibleTypeService.getObjectType("bar");
		Assert.assertNotNull(objectType2);

		assertEquals("ObjectType instance not same as expected", objectType, objectType2.getSupertypes().iterator().next());

	}


	@Test
	public void testProperties()
	{
		final ExtendedType extendedType = extensibleTypeService.getExtendedType("Foo");
		assertEquals("Number of property descriptors for extended type not same as expected", 2, extendedType
				.getPropertyDescriptors().size());

		PropertyDescriptor bar1Desc = null;

		for (final PropertyDescriptor property : extendedType.getPropertyDescriptors())
		{
			if (!("Foo.bar1".equals(property.getQualifier()) || "Foo.bar2".equals(property.getQualifier())))
			{
				Assert.fail("Property not expected.");
			}

			if ("Foo.bar1".equals(property.getQualifier()))
			{
				bar1Desc = property;
			}
		}

		final String attributeCode = extensibleTypeService.getAttributeCodeFromPropertyQualifier("Foo.bar");
		assertEquals("Attribute code not same as expected", "bar", attributeCode);

		final String typeCode = extensibleTypeService.getTypeCodeFromPropertyQualifier("Foo.bar");
		assertEquals("Expected 'Foo' for typecode, but got " + typeCode, "Foo", typeCode);

		final List<Object> values = extensibleTypeService.getAvailableValues(bar1Desc);
		final String valueTypeCode = extensibleTypeService.getValueTypeCode(bar1Desc);

		assertEquals("Number of available values not same as expected", 3, values.size());
		assertEquals("Expected 'foobarPropertyTypeCode', but got " + valueTypeCode, "foobarPropertyTypeCode", valueTypeCode);
	}

	@Test
	public void testItemTemplates()
	{
		createMockedProduct();
		final ObjectTemplate objectTemplate = extensibleTypeService.getObjectTemplate("Product.Foo");
		assertNotNull("ObjectTemplate was null", objectTemplate);
	}


	@Test
	public void testExtendedTypedObjectsOneType()
	{
		testTypedObject = extensibleTypeService.wrapItem(createMockedProduct());

		final Collection<ExtendedType> extendedTypes = testTypedObject.getExtendedTypes();
		assertEquals("Expected number of extended types was 1, but got " + extendedTypes.size(), 1, extendedTypes.size());
		assertEquals("ExtendedType instance not the same as expected", fooType, extendedTypes.iterator().next());
	}

	@Test
	public void testExtendedTypedObjectsTwoTypes()
	{
		extendedTypeLoaders.add(new TestTypeLoaderExt());

		testTypedObject = extensibleTypeService.wrapItem(createMockedProduct());

		final Collection<ExtendedType> extendedTypes = testTypedObject.getExtendedTypes();
		assertEquals("Number of extended types not same as expected", 2, extendedTypes.size());
		Assert.assertTrue(extendedTypes.contains(fooType));
		Assert.assertTrue(extendedTypes.contains(barType));
	}



	private ProductModel createMockedProduct()
	{
		final String productTC = "Product";

		Mockito.when(typeService.getComposedTypeForCode(productTC)).thenReturn(new ComposedTypeModel()
		{
			@Override
			public String getCode()
			{
				return productTC;
			}
		});

		final ItemContextBuilder builder = ItemContextBuilder.createDefaultBuilder(ProductModel.class);
		builder.setItemType(productTC);
		return new ProductModel(builder.build());
	}


	/////////////////////////////////////////////////////
	// TEST CLASSES
	/////////////////////////////////////////////////////

	private class TestPropertyDescriptor extends AbstractPropertyDescriptor
	{
		private final String qualifier;


		public TestPropertyDescriptor(final String qualifier)
		{
			super();
			this.qualifier = qualifier;
		}

		@Override
		public String getQualifier()
		{
			return qualifier;
		}

		@Override
		public String getName(final String languageIso)
		{
			return null;
		}

		@Override
		public boolean isReadable()
		{
			return false;
		}

		@Override
		public boolean isWritable()
		{
			return false;
		}


	}

	private class TestType extends DefaultObjectType implements ExtendedType
	{

		public TestType(final String code)
		{
			super(code);
		}


		@Override
		public Set<PropertyDescriptor> getDeclaredPropertyDescriptors()
		{
			final Set<PropertyDescriptor> ret = new HashSet<PropertyDescriptor>();
			ret.add(new TestPropertyDescriptor("Foo.bar1"));
			ret.add(new TestPropertyDescriptor("Foo.bar2"));
			return ret;
		}

		@Override
		public boolean isAbstract()
		{
			return false;
		}

		@Override
		public String getName()
		{
			return "foo";
		}

		@Override
		public String getName(final String languageIsoCode)
		{
			return getName();
		}

		@Override
		public String getDescription()
		{
			return null;
		}

		@Override
		public String getDescription(final String languageIsoCode)
		{
			return null;
		}

	}


	private class TestTypeLoader extends AbstractTypeLoader
	{

		@Override
		public ExtendedType loadType(final String code, final TypeService typeService)
		{
			if ("Foo".equals(code))
			{
				fooType = new TestType("Foo");
				return fooType;
			}
			return null;
		}

		@Override
		public TypeModel getValueType(final ObjectType enclosingType, final PropertyDescriptor propertyDescriptor,
				final TypeService typeService)
		{
			if ("Foo.bar1".equals(propertyDescriptor.getQualifier()))
			{
				final TypeModel typeModel = new ComposedTypeModel();
				typeModel.setCode("foobarPropertyTypeCode");
				return typeModel;
			}
			return null;
		}

		@Override
		public List<Object> getAvailableValues(final TypeModel valueType, final PropertyDescriptor propertyDescriptor,
				final TypeService typeService)
		{
			if ("Foo.bar1".equals(propertyDescriptor.getQualifier()))
			{
				return Arrays.asList((Object) "val1", "val2", "val3");
			}

			return null;
		}

		@Override
		public Set<String> getExtendedTypeCodes(final TypedObject item)
		{
			if (item.equals(testTypedObject))
			{
				return Collections.singleton("Foo");
			}
			return null;
		}

		@Override
		public String getAttributeCodeFromPropertyQualifier(final String propertyQualifier)
		{

			return null;
		}

		@Override
		public String getTypeCodeFromPropertyQualifier(final String propertyQualifier)
		{
			return null;
		}

		@Override
		public Collection<ExtendedType> getExtendedTypesForTemplate(final ItemType type, final String templateCode,
				final TypeService typeService)
		{
			return null;
		}


	}

	private class TestTypeLoaderExt extends TestTypeLoader
	{

		@Override
		public ExtendedType loadType(final String code, final TypeService typeService)
		{
			if ("bar".equals(code))
			{
				barType = new TestType("bar");
				((TestType) barType).setSupertypes(Collections.singleton(typeService.getExtendedType("Foo")));
				return barType;
			}
			return null;
		}

		@Override
		public Set<String> getExtendedTypeCodes(final TypedObject item)
		{
			if (item.equals(testTypedObject))
			{
				return Collections.singleton("bar");
			}
			return null;
		}
	}
}
