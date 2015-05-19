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
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.message.MessageListHolder;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Base implementation for business object interface.
 */
public class BusinessObjectBase implements BusinessObject, BackendAware, Cloneable
{
	private static final Log4JWrapper LOG = Log4JWrapper.getInstance(BusinessObjectBase.class.getName());

	private static AtomicLong handleAtomicLong = new AtomicLong(0);

	/**
	 * Technical key.
	 */
	protected TechKey techKey; // NOPMD

	/**
	 * Temporary handle.
	 */
	protected String handle = ""; // NOPMD

	/**
	 * Business Object messages.
	 */
	protected MessageList bobMessages = new MessageList(); // NOPMD

	/**
	 * Business Object state.
	 */
	protected int bobState; // NOPMD

	/**
	 * Extension data map.
	 */
	protected Map<String, Object> extensionData; // NOPMD

	/**
	 * Backend type.
	 */
	protected String backendType; // NOPMD

	/**
	 * Backend Object reference.
	 */
	protected BackendBusinessObject backendObject; // NOPMD

	/**
	 * Generic Factory to create data container beans.
	 */
	protected GenericFactory genericFactory; // NOPMD

	/**
	 * Access to module configuration data.
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess = null; // NOPMD

	private boolean backendObjectDetermined;

	/**
	 * Standard constructor.
	 */
	public BusinessObjectBase()
	{
		techKey = TechKey.EMPTY_KEY;
		bobMessages = new MessageList();
		bobState = VALID;
	}

	/**
	 * Injection setter for {@link GenericFactory}.
	 * 
	 * @param genericFactory
	 *           {@link GenericFactory}
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Injection setter for {@link ModuleConfigurationAccess}.
	 * 
	 * @param moduleConfigurationAccess
	 *           the {@link ModuleConfigurationAccess} to set
	 */
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * Set the current backend business object (optional).
	 * 
	 * @param backendObject
	 *           the backend business object to use
	 */
	public void setBackendObject(final BackendBusinessObject backendObject)
	{
		this.backendObject = backendObject;
		this.backendObjectDetermined = false;
	}

	/**
	 * Set the current backend type.
	 * 
	 * @param backendType
	 *           backend type
	 */
	public void setBackendType(final String backendType)
	{
		this.backendType = backendType;
	}

	@Override
	public void init()
	{
		//
	}

	@Override
	public void destroy()
	{
		//
	}

	/**
	 * Get the current backend type.
	 * 
	 * @return backend type
	 */
	public String getBackendType()
	{
		if (backendType == null)
		{
			backendType = moduleConfigurationAccess.getBackendType();
		}
		if (backendType == null || backendType.length() == 0)
		{
			LOG.trace(LogSeverity.WARNING, "BackendType is empty");
		}
		else
		{
			LOG.debug("BackendType is " + backendType.toString());
		}
		return backendType;
	}

	@Override
	public BackendBusinessObject getBackendBusinessObject() throws BackendException
	{
		determineBackendObject(false);
		return backendObject;
	}

	@Override
	public BackendBusinessObject getBackendBusinessObject(final boolean initialize) throws BackendException
	{
		determineBackendObject(initialize);
		return backendObject;
	}

	@Override
	public TechKey getTechKey()
	{
		return techKey;
	}

	@Override
	public void setTechKey(final TechKey techKey)
	{
		this.techKey = techKey;
	}

	@Override
	public void createUniqueHandle()
	{
		handleAtomicLong.incrementAndGet();
		handle = "" + handleAtomicLong.get();
	}

	@Override
	public void setHandle(final String handle)
	{
		this.handle = handle;
	}

	@Override
	public String getHandle()
	{
		return handle;
	}

	@Override
	public boolean hasHandle()
	{
		return (handle != null);
	}

	@Override
	public void setInvalid()
	{
		this.bobState = INVALID;
	}

	@Override
	public void setValid()
	{
		this.bobState = VALID;
	}

	@Override
	public boolean isValid()
	{
		LOG.debugWithArgs("Check Validity for Business Object {0} with Key {1}", this.getClass().getName(), getTechKey()
				.getIdAsString());

		if (bobState == INVALID)
		{
			LOG.debug("Business Object is not valid"); // Can we have more information here what businessobject we have in scope?	
			return (false);
		}

		final Iterator<BusinessObjectBase> i = getSubObjectIterator();

		while (i != null && i.hasNext())
		{
			final Object obj = i.next();
			LOG.debugWithArgs("Validate Sub Object {0}", obj.getClass().getName());

			if (obj instanceof BusinessObjectBase)
			{
				final BusinessObjectBase bo = (BusinessObjectBase) obj;
				if (LOG.isDebugEnabled())
				{
					LOG.debugWithArgs("Techkey of Sub Object {0}", bo.getTechKey().getIdAsString());
				}
				if (!bo.isValid())
				{
					LOG.debug("Sub Object is not valid");
					return false;
				}
			}
		}

		LOG.debug("Object is valid");
		return true;
	}

	@Override
	public void addMessage(final Message message)
	{
		if (message.isError())
		{
			this.bobState = INVALID;
		}
		bobMessages.add(message);
	}

	@Override
	public void copyMessages(final BusinessObjectBase bob)
	{
		copyMessages((MessageListHolder) bob);
	}

	@Override
	public void copyMessages(final MessageListHolder messageListHolder)
	{
		final Iterator<Message> i = messageListHolder.getMessageList().iterator();
		while (i.hasNext())
		{
			addMessage(i.next());
		}
	}

	@Override
	public boolean hasMessages()
	{
		if (!bobMessages.isEmpty())
		{
			return true;
		}
		final Iterator<BusinessObjectBase> iter = getSubObjectIterator();
		while (iter != null && iter.hasNext())
		{
			final Object obj = iter.next();
			if (obj instanceof BusinessObjectBase)
			{
				final BusinessObjectBase bo = (BusinessObjectBase) obj;
				if (!bo.getMessageList().isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void clearMessages(final String resourceKey)
	{
		bobState = VALID;
		bobMessages.clear();
		final Iterator<BusinessObjectBase> i = getSubObjectIterator();
		while (i != null && i.hasNext())
		{
			final Object obj = i.next();
			if (obj instanceof BusinessObjectBase)
			{
				final BusinessObjectBase bo = (BusinessObjectBase) obj;
				bo.clearOwnMessages();
			}
		}
	}

	@Override
	public boolean hasOwnMessages()
	{
		if (!bobMessages.isEmpty())
		{
			return true;
		}
		return false;
	}

	@Override
	public MessageList getOwnMessageList()
	{
		return bobMessages;
	}

	@Override
	public void clearMessages()
	{
		bobState = VALID;
		bobMessages.clear();

		final Iterator<BusinessObjectBase> i = getSubObjectIterator();

		while (i != null && i.hasNext())
		{
			final Object obj = i.next();
			if (obj instanceof BusinessObjectBase)
			{
				final BusinessObjectBase bo = (BusinessObjectBase) obj;
				bo.clearOwnMessages();
			}
		}
	}

	@Override
	public void clearOwnMessages()
	{
		bobState = VALID;
		bobMessages.clear();
	}

	@Override
	public MessageList getMessageList()
	{
		final MessageList retMessages = new MessageList();
		retMessages.add(bobMessages);

		final Iterator<BusinessObjectBase> i = getSubObjectIterator();

		while (i != null && i.hasNext())
		{
			final Object obj = i.next();
			if (obj instanceof BusinessObjectBase)
			{
				final BusinessObjectBase bo = (BusinessObjectBase) obj;
				retMessages.add(bo.bobMessages);
			}
		}

		return retMessages;
	}

	@Override
	public void logMessage(final Message message)
	{
		if (message.isError())
		{
			this.bobState = INVALID;
		}
		message.log(LOG.getLogger());
	}

	/**
	 * The method returns an iterator over all sub objects of the BusinessObject.
	 * 
	 * @return Iterator to loop over sub objects
	 */
	public Iterator<BusinessObjectBase> getSubObjectIterator()
	{
		final List<BusinessObjectBase> objList = new ArrayList<BusinessObjectBase>();

		if (this instanceof HasHeader)
		{
			final BusinessObjectBase bo = ((HasHeader) this).getHeader();
			if (bo != null)
			{
				// add the found object
				objList.add(bo);
			}
		}

		if (this instanceof Iterable)
		{

			final Iterator<?> i = ((Iterable<?>) this).iterator();

			while (i != null && i.hasNext())
			{
				final Object obj = i.next();
				if (obj instanceof BusinessObjectBase)
				{
					final BusinessObjectBase bo = (BusinessObjectBase) obj;
					// add the found object
					objList.add(bo);

					// add all sub object of the object
					final Iterator<BusinessObjectBase> objIterator = bo.getSubObjectIterator();
					while (objIterator.hasNext())
					{
						objList.add(objIterator.next());
					}

				}
			}

		}

		return objList.iterator();
	}

	@Override
	public void addExtensionData(final String key, final Object value)
	{
		if (extensionData == null)
		{
			extensionData = new HashMap<String, Object>();
		}
		extensionData.put(key, value);
	}

	@Override
	public Object getExtensionData(final String key)
	{
		if (extensionData == null)
		{
			return null;
		}
		else
		{
			return extensionData.get(key);
		}
	}

	@Override
	public void removeExtensionData(final String key)
	{
		if (extensionData != null)
		{
			extensionData.remove(key);
		}
	}

	@Override
	public Set<Entry<String, Object>> getExtensionDataValues()
	{
		if (extensionData == null)
		{
			extensionData = new HashMap<String, Object>();
		}
		return extensionData.entrySet();
	}

	@Override
	public void removeExtensionDataValues()
	{
		if (extensionData != null)
		{
			extensionData.clear();
		}
	}

	@Override
	public void setExtensionMap(final Map<String, Object> extensionData)
	{
		this.extensionData = extensionData;
	}

	@Override
	public Map<String, Object> getExtensionMap()
	{
		if (extensionData == null)
		{
			extensionData = new HashMap<String, Object>();
		}
		return extensionData;
	}

	/**
	 * Returns the {@link ModuleConfigurationAccess}.
	 * 
	 * @return the {@link ModuleConfigurationAccess}
	 */
	protected ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		if (moduleConfigurationAccess == null)
		{
			throw new BORuntimeException("No module configuration access available.");
		}
		return moduleConfigurationAccess;
	}

	/**
	 * Determines the backend object.
	 * 
	 * @param initialize
	 *           if true, backend object is reset first (works only in determination mode!)
	 */
	@SuppressWarnings("unchecked")
	protected void determineBackendObject(final boolean initialize)
	{
		// Backend object injected or set by coding
		if (!backendObjectDetermined && backendObject != null)
		{
			// Initialize not possible
			if (initialize)
			{
				throw new BackendDeterminationRuntimeException(
						"Initializing of the backend object works only in determination mode! Here, the backend object has been set explicitly using injection or coding.");
			}
			return;
		}

		// Reset backend object if requested
		if (initialize)
		{
			backendObject = null;
		}

		// Determine backend object if it has not been determined yet
		if (backendObject == null)
		{
			if (this.getClass().getAnnotation(BackendInterface.class) != null
					&& this.getClass().getAnnotation(BackendInterface.class).value() != null)
			{
				// Determined backend beans
				final Map<String, BackendType> determinedBackendBeanNames = new HashMap<String, BackendType>();
				final Class<?> backendInterfaceName = this.getClass().getAnnotation(BackendInterface.class).value();
				final String[] backendBeanNames = genericFactory.getBeanNamesForType(backendInterfaceName);
				for (int i = 0; i < backendBeanNames.length; i++)
				{
					final String backendBeanName = backendBeanNames[i];
					final BackendType backendTypeAnnotation = genericFactory.findAnnotationOnBean(backendBeanName, BackendType.class);
					// Set backend object if there is no @BackendType annotation or the value fits to the requested backend type
					if (backendTypeAnnotation == null || (backendTypeAnnotation.value().equals(getBackendType())))
					{
						determinedBackendBeanNames.put(backendBeanName, backendTypeAnnotation);
					}
				}
				// ---
				// Error: No backend beans found 
				if (determinedBackendBeanNames.isEmpty())
				{
					final String exceptionMessage = "Determination of the backend object failed since no implementation for backend interface '"
							+ backendInterfaceName + "' has been found!";
					LOG.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_CORE, exceptionMessage);
					throw new BackendDeterminationRuntimeException(exceptionMessage);
				}
				// Error: Multiple backend beans found
				if (determinedBackendBeanNames.size() > 1)
				{
					String exceptionMessage = "Determination of the backend object failed since there exists multiple implementations for backend interface '"
							+ backendInterfaceName + "'!  --> ";
					for (final Entry<String, BackendType> determinedBackendBeanNameEntry : determinedBackendBeanNames.entrySet())
					{
						exceptionMessage += "Bean '" + determinedBackendBeanNameEntry.getKey() + "'";
						final BackendType backendTypeAnnotation = determinedBackendBeanNameEntry.getValue();
						if (backendTypeAnnotation != null)
						{
							exceptionMessage += " [@BackendType('" + backendTypeAnnotation.value() + "')]";
						}
						exceptionMessage += "; ";
					}
					LOG.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_CORE, exceptionMessage);
					throw new BackendDeterminationRuntimeException(exceptionMessage);
				}
				// Create backend object bean
				final String backendBeanName = determinedBackendBeanNames.keySet().iterator().next();
				if (initialize)
				{
					genericFactory.removeBean(backendBeanName);
				}
				backendObject = (BackendBusinessObject) genericFactory.getBean(backendBeanName);
				backendObjectDetermined = true;
			}
			else
			{
				// No @BackendInterface annotation found
				final String exceptionMessage = "Determination of the backend object failed since the business object has no @BackendInterface annotation!";
				LOG.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_CORE, exceptionMessage);
				throw new BackendDeterminationRuntimeException(exceptionMessage);
			}
		}
	}

	// *********************************************************
	// standard object methods
	// *********************************************************

	@Override
	public String toString()
	{
		final StringBuffer retVal = new StringBuffer();

		retVal.append(super.toString());
		retVal.append(", TechKey=[");
		retVal.append(techKey);
		retVal.append("], hashCode=[");
		retVal.append(hashCode());
		retVal.append("]");

		return retVal.toString();
	}

	/**
	 * Compares this object to the specified object. The result is <code>true</code> if and only if the argument is not
	 * <code>null</code> and is of the same class that has the same technical key ( <code>TechKey</code>) as this object.
	 * 
	 * @param o
	 *           Object to compare with
	 * @return <code>true</code> if the objects are identical; otherwiese <code>false</code>.
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (o == null)
		{
			return false;
		}
		else if (o == this)
		{
			return true;
		}
		else if (o.getClass().equals(this.getClass()))
		{
			return techKey.equals(((BusinessObjectBase) o).techKey);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the hash code for this object.
	 * 
	 * @return Hash code
	 */
	@Override
	public int hashCode()
	{
		return techKey.hashCode();
	}


	/**
	 * Makes a copy of the object. The MessageList is copied.
	 * 
	 * @return a copy of object
	 * @throws CloneNotSupportedException
	 *            {@link CloneNotSupportedException}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException
	{

		final BusinessObjectBase bob = (BusinessObjectBase) super.clone();
		if (extensionData != null)
		{
			bob.extensionData = (HashMap<String, Object>) ((HashMap<String, Object>) extensionData).clone();
		}
		bob.handle = handle;
		bob.techKey = new TechKey(techKey.getIdAsString());
		bob.bobMessages = new MessageList();

		bob.copyMessages(this);

		return bob;
	}

}
