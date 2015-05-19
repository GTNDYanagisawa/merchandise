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
package de.hybris.platform.sap.core.test.property;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * Utility class to handle property file access in test environments. Usage Pattern:<br>
 * <code>
 * PropertyAccess propAcc = new PropertyAccessImpl();<br>
 * propAcc.addPropertyFile("propertyFolder\\myBaseProperties.properties");<br>
 * propAcc.addPropertyFile("propertyFolder\\myTestProperties.properties");<br>
 * propAcc.loadProperties();<br>
 * <br>
 * String myPropValue = propAcc.getData(myPropKey);<br>
 * </code>
 * 
 */
public interface PropertyAccess
{
	/**
	 * Used to set locale for property files. <br>
	 * e.g. env.locale=en
	 */
	public static final String PROP_KEY_ENV_LOCALE = "env.locale";
	/**
	 * Used to set additional suffixes with a comma separate list. <br>
	 * e.g. SSC,B2B
	 * 
	 */
	public static final String PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX = "env.additional.props.suffix";

	/**
	 * Returns the value of the property with the given key. Throws an exception if the property is not defined. if you
	 * want to check whether an property is set use the {@link PropertyAccess#isPropertySet(String)} method.
	 * 
	 * @param key
	 *           the hashtable key
	 * @return value as string
	 */
	public String getStringProperty(String key);

	/**
	 * Returns the value of the property with the given key or the default if the property is not defined.
	 * 
	 * @param key
	 *           the hashtable key
	 * @param defaultValue
	 *           a default value
	 * @return value as string
	 */
	public String getStringProperty(String key, String defaultValue);

	/**
	 * Returns the value of the property with the given key. Throws an exception if the property is not defined. if you
	 * want to check whether an property is set use the @link PropertyAccess#isPropertySet(String)} method.<br>
	 * 
	 * @param key
	 *           the hashtable key
	 * @return <code>true</code> only if the property is defined and equals ignore case <code>"true"</code>
	 */
	public boolean getBooleanProperty(String key);

	/**
	 * Returns the value of the property with the given key or the default if the property is not defined.
	 * 
	 * @param key
	 *           the hashtable key
	 * @param defaultValue
	 *           a default value
	 * @return <code>true</code> only if the property is defined and equals ignore case <code>"true"</code>
	 */
	public boolean getBooleanProperty(String key, boolean defaultValue);

	/**
	 * Checks if a property is set.
	 * 
	 * @param key
	 *           the hashtable key
	 * @return <code>true</code> if the property is set
	 */
	public boolean isPropertySet(String key);

	/**
	 * Returns the data as a list, assuming that the entries are separated by the given separator. For example reading
	 * the following property<br>
	 * <code>pros.myKey=entry1;entry2;entry3</code> will result in a list with 3 entries
	 * <ul>
	 * <li>entry1</li>
	 * <li>entry2</li>
	 * <li>entry3</li>
	 * </ul>
	 * 
	 * @param key
	 *           the hashtable key
	 * @param separator
	 *           separator string
	 * @return list of strings
	 */
	public List<String> getStringList(String key, String separator);

	/**
	 * Returns the data as a list, assuming that the entries are separated by the a comma. For example reading the
	 * following property<br>
	 * <code>pros.myKey=entry1,entry2,entry3</code> will result in a list with 3 entries
	 * <ul>
	 * <li>entry1</li>
	 * <li>entry2</li>
	 * <li>entry3</li>
	 * </ul>
	 * 
	 * @param key
	 *           key
	 * @return list of strings
	 */
	public List<String> getStringList(String key);

	/**
	 * Adds a property file. <br>
	 * Makes only sense to be called, before {@link PropertyAccess#loadProperties()} is called.
	 * 
	 * 
	 * @param path
	 *           relative path to the property file
	 */
	public void addPropertyFile(String path);

	/**
	 * Adds a property on the fly. Caution: if the property already exists, it will be overwritten.<br>
	 * 
	 * @param key
	 *           the hashtable key
	 * @param value
	 *           property value
	 * @throws FileNotFoundException
	 *            {@link FileNotFoundException}
	 * @throws IOException
	 *            {@link IOException}
	 */
	public void setStringProperty(String key, String value) throws FileNotFoundException, IOException;


	/**
	 * Loads the requested property files from the file system. <br>
	 * Once this method has been executed, additional properties can only be set via
	 * {@link PropertyAccess#setStringProperty(String, String)}<br>
	 * Loading sequence (if 2 props have the same key, the last loaded will win):
	 * <ol>
	 * <li>Normal Properties added via {@link PropertyAccessImpl#addPropertyFile(String)} method.</li>
	 * <li>Language dependent properties, in case they are defined.</li>
	 * <li>Additional Properties. Via property key {@link PropertyAccess#PROP_KEY_ENV_ADDITIONAL_PROPS_SUFFIX} you can
	 * provide one or several suffixes separated by comma. For each normal property loaded beforehand it is checked if
	 * there exists also a property file with that suffix, which is then loaded additionally.<br>
	 * For Example if assume that property file "propDir\\myprops.properties" and suffix "SSC,B2B" were provided. It is
	 * then checked if the property files "propDir\\myprops_SSC.properties" and "propDir\\myprops_B2B.properties exist
	 * and if so, they are loaded as well.</li>
	 * </ol>
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	public void loadProperties() throws IOException;


	/**
	 * Makes only sense to be called, before {@link PropertyAccess#loadProperties()} is called.
	 * 
	 * @param propertiesPathPrefix
	 *           will by used as prefix for all property paths during property load
	 */
	public void setPropertyPathPrefix(String propertiesPathPrefix);

}
