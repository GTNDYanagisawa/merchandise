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
package de.hybris.platform.sap.core.common.message;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.module.ModuleResourceAccess;

import java.util.Locale;

import org.apache.log4j.Logger;


/**
 * The Message class helps you to handle messages which can by displayed on the UI or logged in the log file.
 * <p>
 * To create the message you give the type, the resource key and optional the name of a property.
 * </p>
 * <p>
 * The message text itself can be defined in to different ways:
 * <ul>
 * <li>Resource key: The resource key needs to be defined as described in {@link ModuleResourceAccess}. .</li>
 * <li>Message description: This is best approach if you already have a translated message description e.g. provided
 * from the back end.</li>
 * </ul>
 * The method {@link #getMessageText(Locale)} can be used to get the message text in any case.
 * </p>
 * 
 * @see MessageList
 * @see ModuleResourceAccess
 */
public class Message
{

	/**
	 * Constant to define initial value.
	 */
	final public static int INITIAL = 0;

	/**
	 * Constant to define message type success.
	 */
	final public static int SUCCESS = 1;

	/**
	 * Constant to define message type success.
	 */
	final public static int ERROR = 2;

	/**
	 * Constant to define message type warning.
	 */
	final public static int WARNING = 3;

	/**
	 * Constant to define message type info.
	 */
	final public static int INFO = 4;

	/**
	 * Constant to define message type debug.
	 */
	final public static int DEBUG = 5;

	private String description = "";
	private String resourceKey = "";
	private String[] resourceArgs = null;
	private int type = ERROR;
	private String property = "";
	private TechKey refTechKey;
	private int hashCode;
	private String pagelocation;
	private String position = "";
	private TechKey techKey;

	private ModuleResourceAccess moduleResourceAccess = null;

	/**
	 * Keeps the id of the field in the UI, to which the messages belongs.
	 */
	private String fieldId;

	/**
	 * Constructor to create a message with a resource key.
	 * 
	 * @param type
	 *           message type
	 * @param key
	 *           resource key to the message text
	 * @param args
	 *           an array of arguments.
	 * @param property
	 *           name of property of a bean (only if property isn't null)
	 */
	public Message(final int type, final String key, final String[] args, final String property)
	{

		this.type = type;
		this.resourceKey = key;
		this.resourceArgs = args;
		this.setProperty(property);
	}

	/**
	 * Constructor to create a message with a given type.
	 * 
	 * @param type
	 *           message type
	 */
	public Message(final int type)
	{

		this.type = type;
	}

	/**
	 * Constructor to create a message with a resource key without args.
	 * 
	 * @param type
	 *           message type
	 * @param key
	 *           resource key to the message text
	 * @param property
	 *           name of property of a bean
	 */
	public Message(final int type, final String key, final String property)
	{

		this.type = type;
		this.resourceKey = key;
		this.property = property;
	}

	/**
	 * Constructor to create a message only with resource key.
	 * 
	 * @param type
	 *           message type
	 * @param key
	 *           resource key to the message text
	 */
	public Message(final int type, final String key)
	{

		this.type = type;
		this.resourceKey = key;
	}

	/**
	 * Sets the module resource access from outside (optional, e.g. for tests)
	 * 
	 * @param moduleResourceAccess
	 *           module resource access
	 */
	public void setModuleResourceAccess(final ModuleResourceAccess moduleResourceAccess)
	{
		this.moduleResourceAccess = moduleResourceAccess;
	}

	/**
	 * Returns the module resource access.
	 * 
	 * @return module resource access
	 */
	private ModuleResourceAccess getModuleResourceAccess()
	{
		if (moduleResourceAccess == null)
		{
			moduleResourceAccess = (ModuleResourceAccess) GenericFactoryProvider.getInstance()
					.getBean("sapCoreModuleResourceAccess");
		}
		return moduleResourceAccess;
	}

	/**
	 * Determines the message text for the given locale.
	 * <p>
	 * The method checks first, if the there is a resourceKey is defined. If so, this is used to get a translated message
	 * text with the {@link ModuleResourceAccess}. If not, the message description, which is independent from the given
	 * locale, is used.
	 * </p>
	 * 
	 * @param locale
	 *           locale to get the correct translation.
	 * @return the translated message
	 * @see ModuleResourceAccess
	 */
	public String getMessageText(final Locale locale)
	{
		if (resourceKey != null)
		{
			if (locale != null)
			{
				return getModuleResourceAccess().getString(resourceKey, locale, resourceArgs);
			}
			else
			{
				return getModuleResourceAccess().getString(resourceKey, resourceArgs);
			}
		}
		else
		{
			return description;
		}
	}

	/**
	 * <p>
	 * Determine the message text for the default locale given with {@link java.util.Locale#getDefault()}.
	 * </p>
	 * <p>
	 * For further details take a look at {@link #getMessageText(Locale)}
	 * </p>
	 * 
	 * @return translated message tag
	 */
	public String getMessageText()
	{

		return getMessageText(null);
	}

	/**
	 * Gets the description of the message.
	 * 
	 * @return description of the message
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the message.
	 * 
	 * @param description
	 *           Description of the message
	 */
	public void setDescription(final String description)
	{
		this.description = description;
		hashCode = 0;
	}

	/**
	 * Gets the page location of the message.
	 * 
	 * @return The page location
	 * 
	 */
	public String getPageLocation()
	{
		return pagelocation;
	}

	/**
	 * Sets the page location of the message.
	 * <p>
	 * The page location is used in the accessibility mode to describe, where the error occurs. Please define a resource
	 * key <code>access.message.location.<value></code> for your page locations.
	 * </p>
	 * 
	 * @param pagelocation
	 *           Location where the message occurs
	 */
	public void setPageLocation(final String pagelocation)
	{
		this.pagelocation = pagelocation;
	}

	/**
	 * Gets the position of the message.
	 * 
	 * @return return the exact position where the message occurs
	 */
	public String getPosition()
	{
		return position;
	}

	/**
	 * Sets the position of the message.
	 * 
	 * @param position
	 *           Exact position where the message occurs
	 */
	public void setPosition(final String position)
	{
		this.position = position;
	}

	/**
	 * Returns if the message is an error message.
	 * 
	 * @return true if the message is an error message
	 */
	public boolean isError()
	{
		return type == Message.ERROR ? true : false;
	}

	/**
	 * Returns if the message is a warning.
	 * 
	 * @return true if the message is a warning
	 */
	public boolean isWarning()
	{
		return type == Message.WARNING ? true : false;
	}

	/**
	 * Sets the name of the property to which the message belongs.
	 * 
	 * @param property
	 *           name of the property to which the message belongs (only if property isn't null)
	 */
	public void setProperty(final String property)
	{
		if (property != null)
		{
			this.property = property;
			hashCode = 0;
		}
	}

	/**
	 * Gets the name of the property to which the message belongs.
	 * 
	 * @return field name of the property to which the message belongs
	 */
	public String getProperty()
	{
		return property;
	}

	/**
	 * Sets the property refTechKey.
	 * 
	 * @param refTechKey
	 *           techKey for object to which belongs the message. is only for message which can not directly assign to
	 *           the corresponding business object.
	 */
	public void setRefTechKey(final TechKey refTechKey)
	{
		this.refTechKey = refTechKey;
		hashCode = 0;
	}

	/**
	 * Returns the property refTechKey.
	 * 
	 * @return refTechKey
	 */
	public TechKey getRefTechKey()
	{
		return this.refTechKey;
	}

	/**
	 * Sets the property resourceKey.
	 * 
	 * @param resourceKey
	 *           resource key
	 */
	public void setResourceKey(final String resourceKey)
	{
		this.resourceKey = resourceKey;
		hashCode = 0;
	}

	/**
	 * Returns the property resourceKey.
	 * 
	 * @return resource key
	 */
	public String getResourceKey()
	{
		return this.resourceKey;
	}

	/**
	 * Sets the property resourceArgs.
	 * 
	 * @param resourceArgs
	 *           resource key arguments
	 */
	public void setResourceArgs(final String[] resourceArgs)
	{
		this.resourceArgs = resourceArgs;
		hashCode = 0;
	}

	/**
	 * Returns the property resourceArgs.
	 * 
	 * @return resource key arguments
	 */
	public String[] getResourceArgs()
	{
		return this.resourceArgs;
	}

	/**
	 * Returns the type of the message.
	 * 
	 * @return The type of the message
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Overwrites the method of the object class.
	 * 
	 * @param obj
	 *           the object which will be compare with the object
	 * @return true if the objects are equal
	 */
	@Override
	public boolean equals(final Object obj)
	{

		if (obj == null)
		{
			return false;
		}
		else if (obj == this)
		{
			return true;
		}
		else if (obj instanceof Message)
		{
			final Message message = (Message) obj;

			final int myHashCode = hashCode();
			final int otherHashCode = message.hashCode();

			if (myHashCode != otherHashCode)
			{
				return false;
			}
			else
			{
				boolean result = (message.type == type);

				if (message.fieldId != null)
				{
					result &= message.fieldId.equals(fieldId);
				}

				result &= !(message.fieldId == null && fieldId != null);

				if (message.refTechKey != null)
				{
					result &= message.refTechKey.equals(refTechKey);
				}

				if (message.property != null)
				{
					result &= message.property.equals(property);
				}

				if (resourceKey == null || resourceKey.trim().length() == 0)
				{
					if (message.description != null)
					{
						result &= message.description.equals(description);
					}
				}
				else
				{
					if (message.resourceKey != null)
					{
						result &= message.resourceKey.equals(resourceKey);
					}

					if (resourceArgs != null)
					{
						if (resourceArgs.length != message.resourceArgs.length)
						{
							result = false;
						}

						for (int i = 0; i < resourceArgs.length && result; i++)
						{
							result &= resourceArgs[i].equals(message.resourceArgs[i]);
						}
					}
				}

				return result;
			}
		}
		return false;
	}

	/**
	 * Returns hash code for the message.
	 * 
	 * @return hash code of the objects
	 */
	@Override
	public int hashCode()
	{

		// I'm ignoring the resourceArg and the resourceKeys
		// because this fields are of no use for the hashCode
		synchronized (this)
		{
			if (hashCode == 0)
			{
				if (resourceKey == null || resourceKey.trim().length() == 0)
				{
					hashCode = (description == null ? 0 : description.hashCode()) ^ (property == null ? 0 : property.hashCode())
							^ type ^ (refTechKey == null ? 0 : refTechKey.hashCode());
				}
				else
				{
					hashCode = (resourceKey == null ? 0 : resourceKey.hashCode()) ^ (property == null ? 0 : property.hashCode())
							^ type ^ (refTechKey == null ? 0 : refTechKey.hashCode());

					if (resourceArgs != null)
					{
						for (int i = 0; i < resourceArgs.length; i++)
						{
							if (resourceArgs[i] != null)
							{
								hashCode = hashCode ^ resourceArgs[i].hashCode();
							}
						}
					}
				}
			}
		}

		return hashCode;
	}

	/**
	 * Logs the message in the given logger.
	 * 
	 * @param logger
	 *           logger
	 */
	public void log(final Logger logger)
	{

		final String messageText = getMessageText();
		//		final Integer severity = null;

		switch (type)
		{
			case ERROR:
				logger.error(messageText);
				break;

			case WARNING:
				logger.warn(messageText);
				break;

			default:
				logger.info(messageText);
				break;
		}

	}

	/**
	 * Returns the object as string. <br>
	 * 
	 * @return String which contains all fields of the object
	 */
	@Override
	public String toString()
	{

		StringBuffer str = new StringBuffer("Message Type: ");
		str.append(type).append(", ").append("Description: ").append(description).append(", ").append("Property: ")
				.append(property).append(", ").append("FieldId: ").append(fieldId).append(", ").append("ResourceKey: ")
				.append(resourceKey).append(", ");

		if (resourceArgs != null)
		{
			str = str.append("Args: ");
			for (final String arg : resourceArgs)
			{
				str.append(arg).append(" ");
			}

		}
		return str.toString();
	}

	/**
	 * Returns the technical key of the message. For CRM or ERP messages the technical key consists of the message ID and
	 * message number separated by space (e.g. "CRM_ORDER 002").
	 * 
	 * @return the techKey
	 */
	public TechKey getTechKey()
	{
		return techKey;
	}

	/**
	 * Sets the techKey of the message.
	 * 
	 * @param techKey
	 *           the techKey to set
	 */
	public void setTechKey(final TechKey techKey)
	{
		this.techKey = techKey;
	}

	/**
	 * Gets the field id.
	 * 
	 * @return field id
	 */
	public String getFieldId()
	{
		return fieldId;
	}

	/**
	 * Sets the field id.
	 * 
	 * @param fieldId
	 *           field id
	 */
	public void setFieldId(final String fieldId)
	{
		this.fieldId = fieldId;
	}
}
