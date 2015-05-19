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
package de.hybris.platform.webservices.objectgraphtransformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * The purpose of this class is to compare objects (of any type) using their javaBean property (getter method) values.
 * This means it checks if both objects passed to compare() method have the same javaBean property (property name is
 * given in constructor), then it reads the property values from both objects and then it compares these values
 * (assuming the values are themselves objects that implements java.lang.Comparable). If a property with a given name
 * does not exist on objects being compared (or this property returns a value that is not Comparable), this comparator
 * has a fallback mechanism that compares objects directly, assuming that both objects have the same runtime class and
 * this class implements java.lang.Comparable. The property name to use for comparison is set at the construction time
 * and cannot be changed. It means that single instance of this class can be used to compare only using one property.
 * The property used to compare objects must be a valid javaBean property (getter method must exist) and it must return
 * a type that implements java.lang.Comparable.
 * <p>
 * Known limitations:
 * </p>
 * <ul>
 * <li>The classes of objects being compared must be accessible. Package-private classes won't work. Anonymous inner
 * classes won't work for the same reason</li>
 * <li>The code checks if BOTH objects passed to {@link DynamicComparator#compare(Object, Object)} method have the same
 * property and that return type of these properties are the same (so for now this comparator will not work with
 * covariant return types).</li>
 * </ul>
 */
public class DynamicComparator implements Comparator<Object>
{
	private final String propertyName;
	private final String methodName;
	private final boolean reversedOrder;

	//Map used to speed up reflective operations (searching for Comparable method in a Class)
	private final Map<Class<?>, Method> comparableMethods = new HashMap<Class<?>, Method>();

	/**
	 * Convienience constructor that is the same as DynamicComparator(propertyName, false);
	 */
	public DynamicComparator(final String propertyName)
	{
		this(propertyName, false);
	}

	/**
	 * Creates instance that compares objects by specified javaBean-style property using specified order (normal or
	 * reversed).
	 * 
	 * @param propertyName
	 *           name of the property that will be used to do comparison. This property must exist on both objects passed
	 *           to {@link #compare(Object, Object)} method and it must return a ReferenceType that implements
	 *           java.lang.Comparable (this excludes java primitive types and arrays)
	 * @param reversedOrder
	 *           if equals true, the comparison order is reversed. It is normal (so called "natural") order otherwise.
	 */
	public DynamicComparator(final String propertyName, final boolean reversedOrder)
	{
		this.propertyName = propertyName;
		this.methodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		this.reversedOrder = reversedOrder;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Object object1, final Object object2)
	{
		final Class<?> clazz1 = object1.getClass();
		final Class<?> clazz2 = object2.getClass();

		if (clazz1 == clazz2)
		{
			//simple case - same classes

			final Method method1 = comparableMethods.containsKey(clazz1) ? comparableMethods.get(clazz1) : getComparableMethod(clazz1);
			if (method1 != null)
			{
				try
				{
					int result = ((Comparable) method1.invoke(object1, (Object[]) null)).compareTo(method1.invoke(object2, (Object[]) null));
					if (reversedOrder)
					{
						result *= -1;
					}
					return result;
				}
				catch (final InvocationTargetException ex)
				{
					throw new RuntimeException(ex);
				}
				catch (final IllegalAccessException ex)
				{
					throw new RuntimeException(ex);
				}
			}
			else
			{
				/*
				 * The fallback mechanism: if comparable property is not found in the clazz1/clazz2 class, Try to compare
				 * objects directly - if clazz1/clazz2 implements java.lang.Comparable. This fallback mechanism allows to
				 * apply this Comparator not only on List<SomethingModel>, but also on List<String>, List<Number>, etc.
				 */
				//we do not check o2, because its type is the same as o1
				if ((object1 instanceof Comparable))
				{
					int result = ((Comparable) object1).compareTo(object2);
					if (reversedOrder)
					{
						result *= -1;
					}
					return result;
				}
				else
				{
					/*
					 * Everything failed: expected property is not found inside the class of given objects, and the class
					 * itself does not implement java.lang.Comparable... We have no way to compare the two provided
					 * instances.
					 */
					throw new IllegalArgumentException("Comparable property: " + propertyName + " of class: " + clazz1.getName()
							+ " does not exist and the class does not implement java.lang.Comparable");
				}
			}
		}
		else
		{
			//complex case - different classes.
			//We are allowing comparison operation, if both classes have the same property with the same return type.
			//This is necessary because we have such cases -  for example Catalogs, 
			//where ClassificationSystem is a subclass of Catalog and it gets listed together with Catalogs.
			//YTODO: What about covariant return types? For now it is not handled.
			final Method method1 = comparableMethods.containsKey(clazz1) ? comparableMethods.get(clazz1) : getComparableMethod(clazz1);
			final Method method2 = comparableMethods.containsKey(clazz2) ? comparableMethods.get(clazz2) : getComparableMethod(clazz2);

			if (method1 == null)
			{
				throw new IllegalArgumentException("Comparable property: " + propertyName + " of class: " + clazz1.getName()
						+ " does not exist!");
			}

			if (method2 == null)
			{
				throw new IllegalArgumentException("Comparable property: " + propertyName + " of class: " + clazz2.getName()
						+ " does not exist!");
			}

			if (method1.getReturnType() != method2.getReturnType())
			{
				throw new IllegalArgumentException( //
						"Cannot compare property: " + clazz1.getName() + "#" //
								+ method1.getName() + ": " + method1.getReturnType().getName() //
								+ " with: " + clazz2.getName() + "#" //
								+ method2.getName() + ": " + method2.getReturnType().getName() //
				);
			}
			else
			{
				try
				{
					int result = ((Comparable) method1.invoke(object1, (Object[]) null)).compareTo(method2.invoke(object2, (Object[]) null));
					if (reversedOrder)
					{
						result *= -1;
					}
					return result;
				}
				catch (final InvocationTargetException ex)
				{
					throw new RuntimeException(ex);
				}
				catch (final IllegalAccessException ex)
				{
					throw new RuntimeException(ex);
				}
			}
		}
	}

	/**
	 * Looks for a javabean-style property getter method inside a given class. The target method must be public, must
	 * take no parameters and must return an object of a type that implements java.lang.Comparable (Name of the target
	 * method is determined by this.methodName)
	 * 
	 * @param clazz
	 *           the Class to introspect
	 * @return a Method object if found or null if there's no such method in the given class.
	 */
	private Method getComparableMethod(final Class<?> clazz)
	{
		Method result = null;

		final Method[] methods = clazz.getMethods();
		for (final Method possiblyComparableMethod : methods)
		{
			//We are searching for a public method with a specific name that takes no parameter. WE DO CASE_INSENSITIVE COMPARISON FOR METHOD NAMES!!!
			if (Modifier.isPublic(possiblyComparableMethod.getModifiers())
					&& this.methodName.equalsIgnoreCase(possiblyComparableMethod.getName())
					&& possiblyComparableMethod.getParameterTypes().length == 0)
			{
				//Ensure that the return type implements java.lang.Comparable
				if (java.lang.Comparable.class.isAssignableFrom(possiblyComparableMethod.getReturnType()))
				{
					result = possiblyComparableMethod;
					break;
				}
			}
		}

		//save result in a map to speed up future method resolution. Null value means no method found!
		if (!this.comparableMethods.containsKey(clazz))
		{
			this.comparableMethods.put(clazz, result);
		}

		return result;
	}
}
