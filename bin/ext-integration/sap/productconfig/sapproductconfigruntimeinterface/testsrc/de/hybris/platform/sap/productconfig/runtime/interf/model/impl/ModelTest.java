package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class ModelTest
{
	private static final String SKIP_NOTHING = "";
	private static final String SKIP_CSTIC_FIELDS = "conflicts|changedByFrontend|allowsAdditionalValues|intervalInDomain|entryFieldMask|author";
	private static final String SKIP_CSTIC_VALUE_FIELDS = "domainValue|languageDependentName|selectable|author|authorExternal|deltaPrice";
	private static final String IGNORE_ALWAYS_FIELD = "$jacocoData";

	@Test
	public void testCsticModel() throws Exception
	{
		final CsticModelImpl cstic1 = new CsticModelImpl();
		CsticModel cstic2 = new CsticModelImpl();
		basicEqualsTests(cstic1, cstic2);
		assertTrue(cstic1.equals(cstic1.clone()));

		equalsTestOnFields(cstic1, cstic2, SKIP_CSTIC_FIELDS);

		cstic2 = cstic1.clone();
		assertTrue(cstic1.hashCode() == cstic2.hashCode());
		assertTrue(cstic1.equals(cstic2));
		assertTrue(cstic2.equals(cstic1));
	}

	@Test
	public void testConflictModel() throws Exception
	{
		final ConflictModel conflict1 = new ConflictModelImpl();
		ConflictModel conflict2 = new ConflictModelImpl();
		basicEqualsTests(conflict1, conflict2);
		assertTrue(conflict1.equals(conflict1.clone()));

		equalsTestOnFields(conflict1, conflict2, SKIP_NOTHING);

		conflict2 = conflict1.clone();
		assertTrue(conflict1.hashCode() == conflict2.hashCode());
		assertTrue(conflict1.equals(conflict2));
		assertTrue(conflict2.equals(conflict1));
	}

	@Test
	public void testInstanceModel() throws Exception
	{
		final InstanceModel instance1 = new InstanceModelImpl();
		InstanceModel instance2 = new InstanceModelImpl();
		basicEqualsTests(instance1, instance2);
		assertTrue(instance1.equals(instance1.clone()));

		equalsTestOnFields(instance1, instance2, SKIP_NOTHING);

		instance2 = instance1.clone();
		assertTrue(instance1.hashCode() == instance2.hashCode());
		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}

	@Test
	public void testCsticGroupModel() throws Exception
	{
		final CsticGroupModel instance1 = new CsticGroupModelImpl();
		CsticGroupModel instance2 = new CsticGroupModelImpl();
		basicEqualsTests(instance1, instance2);
		assertTrue(instance1.equals(instance1.clone()));

		equalsTestOnFields(instance1, instance2, SKIP_NOTHING);

		instance2 = instance1.clone();
		assertTrue(instance1.hashCode() == instance2.hashCode());
		assertTrue(instance1.equals(instance2));
		assertTrue(instance2.equals(instance1));
	}

	@Test
	public void testConfigModel() throws Exception
	{
		final ConfigModel config1 = new ConfigModelImpl();
		ConfigModel config2 = new ConfigModelImpl();
		basicEqualsTests(config1, config2);
		assertTrue(config1.equals(config1.clone()));

		equalsTestOnFields(config1, config2, SKIP_NOTHING);

		config2 = config1.clone();
		assertTrue(config1.hashCode() == config2.hashCode());
		assertTrue(config1.equals(config2));
		assertTrue(config2.equals(config1));
	}

	@Test
	public void testCsticValueModel() throws Exception
	{
		final CsticValueModel csticValue1 = new CsticValueModelImpl();
		CsticValueModel csticValue2 = new CsticValueModelImpl();
		basicEqualsTests(csticValue1, csticValue2);
		assertTrue(csticValue1.equals(csticValue1.clone()));

		equalsTestOnFields(csticValue1, csticValue2, SKIP_CSTIC_VALUE_FIELDS);

		csticValue2 = csticValue1.clone();
		assertTrue(csticValue1.hashCode() == csticValue2.hashCode());
		assertTrue(csticValue1.equals(csticValue2));
		assertTrue(csticValue2.equals(csticValue1));
	}

	private void equalsTestOnFields(final Object obj1, final Object obj2, final String skipFields) throws Exception
	{
		final Class clazz = obj1.getClass();
		final Field[] fields = clazz.getDeclaredFields();

		for (final Field field : fields)
		{
			if (Modifier.isFinal(field.getModifiers()))
			{
				continue;
			}

			final String fieldName = field.getName();
			if (skipFields.contains(fieldName) || IGNORE_ALWAYS_FIELD.contains(fieldName))
			{
				continue;
			}

			final Class<?> type = field.getType();
			final Object obj = getObjectForType(type);
			final Object negativeObj = getNegativeObjectForType(type);
			field.setAccessible(true);
			field.set(obj1, obj);
			field.set(obj2, negativeObj);
			assertFalse(fieldName, obj1.equals(obj2));
			assertFalse(fieldName, obj2.equals(obj1));
			assertFalse(fieldName, obj1.hashCode() == obj2.hashCode());

			field.set(obj2, obj);
			assertTrue(fieldName, obj1.equals(obj2));
			assertTrue(fieldName, obj2.equals(obj1));
			assertTrue(fieldName, obj1.hashCode() == obj2.hashCode());
		}
	}

	private Object getObjectForType(final Class<?> type) throws Exception
	{
		String typeName = type.getName();
		Object obj;
		if (typeName.equals("int"))
		{
			obj = Integer.valueOf(100);
		}
		else if (typeName.equals("boolean"))
		{
			obj = Boolean.TRUE;
		}
		else if (typeName.equals("java.util.List"))
		{
			final List list = new ArrayList();
			final Type listType = type.getTypeParameters()[0];
			if (listType instanceof ParameterizedType)
			{
				final Type elementType = ((ParameterizedType) listType).getActualTypeArguments()[0];
				list.add(getObjectForType((Class) elementType));
			}

			obj = list;
		}
		else if (typeName.startsWith("de.hybris.platform.sap.productconfig.runtime.interf.model"))
		{
			typeName = typeName.replaceAll("de.hybris.platform.sap.productconfig.runtime.interf.model",
					"de.hybris.platform.sap.productconfig.runtime.interf.model.impl") + "Impl";
			final Class clazz = Class.forName(typeName);
			obj = clazz.newInstance();
		}
		else if (typeName.equals("java.lang.String"))
		{
			obj = "TEST";
		}
		else if (typeName.equals("java.math.BigDecimal"))
		{
			obj = new BigDecimal(100);
		}
		else
		{
			try
			{
				obj = type.newInstance();
			}
			catch (final Exception e)
			{
				System.out.println(typeName);
				e.printStackTrace();
				obj = null;
			}
		}
		return obj;
	}

	private Object getNegativeObjectForType(final Class<?> type) throws Exception
	{
		final String typeName = type.getName();
		Object obj;
		if (typeName.equals("int"))
		{
			obj = Integer.valueOf(1);
		}
		else if (typeName.equals("boolean"))
		{
			obj = Boolean.FALSE;
		}
		else
		{
			obj = null;
		}
		return obj;
	}

	private void basicEqualsTests(final Object obj1, final Object obj2)
	{
		assertTrue(obj1.equals(obj1));
		assertTrue(obj1.equals(obj2));
		assertTrue(obj1.hashCode() == obj2.hashCode());

		assertFalse(obj1.equals(null));
		assertFalse(obj1.equals("TEST"));

		assertNotNull(obj1.toString());
	}
}
