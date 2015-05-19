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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.CharacteristicValueImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ContextAttributeImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.InstanceImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.PartOfRelationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ProductConfigurationStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.tc.logging.Severity;


/**
 * Handles the exchange of product configuration data between RFC / LO-API and BOL
 */
public class ProductConfigurationStrategyImpl implements ProductConfigurationStrategy
{

	protected static final String TABLE_IT_CHAR_EXT = "IT_CHAR_EXT";
	protected static final String TABLE_IT_INST_EXT = "IT_INST_EXT";
	protected static final String TABLE_MESSAGES = "ET_MESSAGES";
	protected static final String TABLE_UNPROCESSED = "ET_CHAR_UNPROCESSED";
	protected static final String TABLE_ET_VCFG_INST = "ET_VCFG_INST";
	protected static final String TABLE_ET_VCFG_CHAR = "ET_VCFG_CHAR";
	protected static final String TABLE_TT_REFCHAR = "TT_REFCHAR";

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(ProductConfigurationStrategyImpl.class.getName());

	/**
	 * FM for reading product configuration via LO-API: ERP_LORD_SET_VCFG_ALL
	 */
	public static final String RFC_NAME = "ERP_LORD_SET_VCFG_ALL";
	/**
	 * FM for writing product configuration via LO-API: ERP_WEC_GET_VCFG_ALL
	 */
	public static final String RFC_NAME_READ = "ERP_WEC_GET_VCFG_ALL";
	private BackendMessageMapper messageMapper;
	static final String AUTHOR_USER = " ";
	static final String AUTHOR_DEFAULT = "8";
	static final String AUTHOR_EXTERNAL = "X";
	/**
	 * Import attribute for read call
	 */
	public static final String WEIGHTED_CHARACTERISTICS = "C";

	@Override
	public void writeConfiguration(final JCoConnection connection, final ConfigModel configModel, final String handle,
			final BusinessObject bo)
	{
		JCoFunction function;
		try
		{
			function = connection.getFunction(RFC_NAME);
			final JCoParameterList importParameters = function.getImportParameterList();
			fillImportParameters(importParameters, handle);
			final JCoTable tableInst = importParameters.getTable(TABLE_IT_INST_EXT);
			final JCoTable tableCstics = importParameters.getTable(TABLE_IT_CHAR_EXT);
			toTables(configModel, tableInst, tableCstics);
			JCoHelper.logCall(RFC_NAME, tableInst, null, sapLogger);
			JCoHelper.logCall(RFC_NAME, tableCstics, null, sapLogger);

			connection.execute(function);
			addMessages(bo, function.getExportParameterList().getTable(TABLE_MESSAGES));
			addMessagesUnprocessed(bo, configModel.getRootInstance(), function.getExportParameterList().getTable(TABLE_UNPROCESSED));
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not access function module", e);
		}

	}

	protected void fillImportParameters(final JCoParameterList importParameters, final String handle)
	{
		importParameters.setValue("IV_HANDLE_ITEM", handle);
		importParameters.setValue("IV_INTERNAL_INPUT", "X");
	}

	protected void toTables(final ConfigModel configModel, final JCoTable instExt, final JCoTable charExt)
	{
		//No root instance: Nothing to do
		final InstanceModel rootInstance = configModel.getRootInstance();
		if (rootInstance != null)
		{
			toTables(rootInstance, "", instExt, charExt);
		}

	}

	protected void toTables(final InstanceModel instance, final String parentId, final JCoTable instExt, final JCoTable charExt)
	{
		instExt.appendRow();
		instExt.setValue("PARENT", parentId);
		if (!parentId.isEmpty())
		{
			instExt.setValue("ITEM_NUMBER", instance.getPosition());
		}
		instExt.setValue("INSTANCE", instance.getId());
		instExt.setValue("CRESET", "X");
		for (final CsticModel characteristic : instance.getCstics())
		{
			toCsticTable(instance, charExt, characteristic);
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			toTables(subInstance, instance.getId(), instExt, charExt);
		}
	}

	/**
	 * @param instance
	 * @param charExt
	 * @param characteristic
	 */
	protected void toCsticTable(final InstanceModel instance, final JCoTable charExt, final CsticModel characteristic)
	{

		for (final CsticValueModel csticValue : characteristic.getAssignedValues())
		{
			if (sapLogger.isDebugEnabled())
			{
				debugOutputChars(instance, characteristic, csticValue);
			}
			final String author = csticValue.getAuthorExternal();
			if (checkAuthorValid(author))
			{
				charExt.appendRow();
				charExt.setValue("INSTANCE", instance.getId());
				charExt.setValue("ATNAM", characteristic.getName());
				charExt.setValue("ATAUT", csticValue.getAuthorExternal());
				charExt.setValue("ATVAL", csticValue.getName());
			}
		}
	}


	void debugOutputChars(final InstanceModel instance, final CsticModel characteristic, final CsticValueModel csticValue)
	{
		sapLogger.debug("Cstic will be sent: "
				+ checkAuthorValid(csticValue.getAuthorExternal() + " : " + instance.getId() + ", " + characteristic.getName() + ", "
						+ csticValue.getName() + ", " + csticValue.getAuthor()));

	}

	protected boolean checkAuthorValid(final String author)
	{
		return author == null || author.equals(AUTHOR_USER) || author.equals(AUTHOR_DEFAULT) || author.equals(AUTHOR_EXTERNAL);
	}

	protected void addMessages(final BusinessObject cart, final JCoTable messages)
	{
		try
		{
			messageMapper.map(cart, null, messages);
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not add messages", e);
		}

	}

	/**
	 * @return the messageMapper
	 */
	public BackendMessageMapper getMessageMapper()
	{
		return messageMapper;
	}

	/**
	 * @param messageMapper
	 *           the messageMapper to set
	 */
	public void setMessageMapper(final BackendMessageMapper messageMapper)
	{
		this.messageMapper = messageMapper;
	}

	/**
	 * Evaluate unprocessed cstics and add message to BO if needed
	 * 
	 * @param bo
	 *           Will get the messages attached
	 * @param unprocessed
	 *           Table of unprocessed cstics
	 */
	protected void addMessagesUnprocessed(final BusinessObject bo, final InstanceModel rootInstance, final JCoTable unprocessed)
	{
		if (unprocessed.getNumRows() > 0)
		{
			final Message message = new Message(Message.ERROR, "sapordermgmt.be.prodCfg.characteristicNotProcessed.message",
					new String[]
					{ rootInstance.getName() }, null);
			bo.addMessage(message);

			// log issues
			for (int i = 0; i < unprocessed.getNumRows(); i++)
			{
				unprocessed.setRow(i);
				sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
						"Could not process value: " + unprocessed.getString("INSTANCE") + ", " + unprocessed.getString("ATNAM") + ", "
								+ unprocessed.getString("ATVAL") + ", " + unprocessed.getString("UPDMOD"));
			}
		}

	}


	@Override
	public void readConfiguration(final JCoConnection connection, final SalesDocument salesDoc,
			final List<String> configurableItems)
	{
		try
		{
			final JCoFunction function = connection.getFunction(RFC_NAME_READ);
			fillImportParametersRead(function.getImportParameterList(), configurableItems);
			connection.execute(function);
			final JCoParameterList exportParameterList = function.getExportParameterList();
			final JCoTable instanceTable = exportParameterList.getTable(TABLE_ET_VCFG_INST);
			final JCoTable characteristicTable = exportParameterList.getTable(TABLE_ET_VCFG_CHAR);
			JCoHelper.logCall(RFC_NAME_READ, null, characteristicTable, sapLogger);
			JCoHelper.logCall(RFC_NAME_READ, null, instanceTable, sapLogger);
			createConfigModels(configurableItems, salesDoc, instanceTable, characteristicTable, function.getTableParameterList()
					.getTable(TABLE_TT_REFCHAR));

			addMessages(salesDoc, function.getExportParameterList().getTable(TABLE_MESSAGES));


		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not access function module: " + RFC_NAME_READ, e);
		}

	}

	/**
	 * Fill import parameters for configuration read call
	 * 
	 * @param importParametersRead
	 * @param configurableItems
	 */
	protected void fillImportParametersRead(final JCoParameterList importParametersRead, final List<String> configurableItems)
	{
		importParametersRead.setValue("IV_VIEWVAR", WEIGHTED_CHARACTERISTICS);
		importParametersRead.setValue("IV_INTERNAL_OUTPUT", "X");
		final JCoTable tableHandleItem = importParametersRead.getTable("IT_HANDLE_ITEM");

		for (final String itemHandle : configurableItems)
		{
			tableHandleItem.appendRow();
			tableHandleItem.setValue(0, itemHandle);
		}
	}

	/**
	 * Creates a configuration model from the JCO tables read via ERP_WEC_GET_VCFG_ALL. Filters the tables on the
	 * specified item handle
	 * 
	 * @param handle
	 *           Reference to the item we want to create the configuration for
	 * @param instancesTableRead
	 *           Instances table
	 * @param csticsTableRead
	 *           Characteristics table
	 * @param refCharTableRead
	 *           Table of reference characteristics, ABAP type TDT_RFC_WEC_REFCHAR
	 * @return Configuration
	 */
	protected Configuration createConfigModel(final String handle, final JCoTable instancesTableRead,
			final JCoTable csticsTableRead, final JCoTable refCharTableRead)
	{
		final Configuration configuration = new ConfigurationImpl();
		createInstances(handle, configuration, instancesTableRead);
		createCharacteristics(handle, configuration, csticsTableRead);
		createContextAttributes(handle, configuration, refCharTableRead);
		return configuration;
	}

	/**
	 * Creates the instance tree from the instances tables read from ERP
	 * 
	 * @param handle
	 *           Key of item we want to create the instance tree for
	 * @param configuration
	 *           Configuration Model
	 * @param instancesTableRead
	 *           Instance table of type TDT_RFC_WEC_VCFG_INST
	 */
	protected void createInstances(final String handle, final Configuration configuration, final JCoTable instancesTableRead)
	{
		if (instancesTableRead.isEmpty())
		{
			return;
		}
		instancesTableRead.firstRow();
		do
		{
			final String currentHandle = instancesTableRead.getString("HANDLE_ITEM");
			if (currentHandle.equals(handle))
			{
				final Instance newInstance = new InstanceImpl();
				final String parent = instancesTableRead.getString("PARENT");
				//author not provided on instance level
				newInstance.setAuthor("");
				newInstance.setId(instancesTableRead.getString("INSTANCE"));
				newInstance.setClassType(instancesTableRead.getString("CLASS_TYPE"));
				newInstance.setObjectType(mapObjectCategory(instancesTableRead.getString("OBJECT_CATEGORY")));
				newInstance.setObjectKey(instancesTableRead.getString("COMPONENT"));
				newInstance.setComplete(instancesTableRead.getString("INCOMPLETE").isEmpty());
				newInstance.setConsistent(instancesTableRead.getString("INCONSISTENT").isEmpty());
				newInstance.setObjectText(instancesTableRead.getString("COMPONENT_T"));
				newInstance.setQuantity(instancesTableRead.getString("COMPONENT_QTY"));
				newInstance.setQuantityUnit(instancesTableRead.getString("COMPONENT_UNIT"));
				if (isParentInitial(parent))
				{
					configuration.setRootInstance(newInstance);
				}
				else
				{
					final PartOfRelation partOf = createPartOfRelation(instancesTableRead);
					configuration.addPartOfRelation(partOf);
				}
				configuration.addInstance(newInstance);
			}

		}
		while (instancesTableRead.nextRow());

	}





	/**
	 * Creates the characteristic value assignment for the instance specified
	 * 
	 * @param handle
	 *           Handle of item we want to attach the characters to
	 * @param configuration
	 *           External configuration
	 * @param csticsTableRead
	 *           JCO table containing the characteristics, of ABAP type TDT_RFC_WEC_VCFG_CHAR
	 */
	protected void createCharacteristics(final String handle, final Configuration configuration, final JCoTable csticsTableRead)
	{
		if (csticsTableRead.isEmpty())
		{
			return;
		}
		csticsTableRead.firstRow();
		do
		{
			final String currentHandle = csticsTableRead.getString("HANDLE_ITEM");
			if (currentHandle.equals(handle))
			{
				final CharacteristicValue newCharacteristic = new CharacteristicValueImpl();
				newCharacteristic.setCharacteristic(csticsTableRead.getString("ATNAM"));
				newCharacteristic.setCharacteristicText(csticsTableRead.getString("ATNAM_T"));
				newCharacteristic.setInstId(csticsTableRead.getString("INSTANCE"));
				newCharacteristic.setAuthor(csticsTableRead.getString("ATAUT"));
				newCharacteristic.setInvisible(csticsTableRead.getString("ATVIE").equals("X"));
				newCharacteristic.setValue(csticsTableRead.getString("ATVAL"));
				newCharacteristic.setValueText(csticsTableRead.getString("ATVAL_T"));
				configuration.addCharacteristicValue(newCharacteristic);
			}

		}
		while (csticsTableRead.nextRow());


	}

	/**
	 * Creates the configuration models attached to the specified sales document items
	 * 
	 * @param configurableItems
	 * @param salesDoc
	 * @param instancesTableRead
	 * @param csticsTableRead
	 * @param refCharTableRead
	 */
	public void createConfigModels(final List<String> configurableItems, final SalesDocument salesDoc,
			final JCoTable instancesTableRead, final JCoTable csticsTableRead, final JCoTable refCharTableRead)
	{
		for (final String itemHandle : configurableItems)
		{
			final Configuration configuration = createConfigModel(itemHandle, instancesTableRead, csticsTableRead, refCharTableRead);
			addKbKey(configuration, salesDoc);
			final Item item = salesDoc.getItem(new TechKey(itemHandle));
			item.setProductConfigurationExternal(configuration);
		}
	}

	/**
	 * Does this instance ID point to an empty parent, i.e. is no parent assigned?
	 * 
	 * @param parent
	 * @return No parent?
	 */
	protected boolean isParentInitial(final String parent)
	{
		if (parent == null || parent.isEmpty())
		{
			return true;
		}
		final BigDecimal parentAsBD = new BigDecimal(parent);
		return parentAsBD.intValue() == 0;
	}

	/**
	 * Create a parent/child relation
	 * 
	 * @param instancesTableRead
	 *           JCO table reflecting the instance information passed from ERP
	 * @return PartOf relation build from the current row of JCO table
	 */
	protected PartOfRelation createPartOfRelation(final JCoTable instancesTableRead)
	{
		final PartOfRelation partOf = new PartOfRelationImpl();
		//author on instance level not provided from ERP
		partOf.setAuthor("");
		partOf.setClassType(instancesTableRead.getString("CLASS_TYPE"));
		partOf.setInstId(instancesTableRead.getString("INSTANCE"));
		partOf.setObjectKey(instancesTableRead.getString("COMPONENT"));
		partOf.setObjectType(mapObjectCategory(instancesTableRead.getString("OBJECT_CATEGORY")));
		partOf.setParentInstId(instancesTableRead.getString("PARENT"));
		partOf.setPosNr(instancesTableRead.getString("ITEM_NUMBER"));
		return partOf;
	}

	/**
	 * Maps object category received from ERP into object type for external configuration
	 * 
	 * @param objectCategory
	 * @return Object type of BOM item
	 */
	protected String mapObjectCategory(final String objectCategory)
	{
		String result = "";
		switch (objectCategory)
		{
			case "M":
				result = "MARA";
				break;
			case "C":
				result = "KLAH";
				break;
			case "":
				//this is fine, as root instances don't carry this attribute
		}
		if (result.isEmpty() && (!objectCategory.isEmpty()))
		{
			sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Unknow object category in reading configuration: "
					+ objectCategory);
		}
		return result;

	}

	/**
	 * Creates context attributes from data provided in ERP
	 * 
	 * @param handle
	 *           Handle of sales document item we are interested in
	 * 
	 * @param configuration
	 *           External representation of configuration
	 * @param refCharTableRead
	 *           JCO table of context or 'reference characteristic' attributes. ABAP type TDT_RFC_WEC_REFCHAR
	 */
	protected void createContextAttributes(final String handle, final Configuration configuration, final JCoTable refCharTableRead)
	{
		final List<CharacteristicValue> characteristicValues = configuration.getCharacteristicValues();
		if (refCharTableRead.isEmpty() || characteristicValues == null)
		{
			return;
		}

		refCharTableRead.firstRow();
		do
		{
			if (handle.equals(refCharTableRead.getString("HANDLE_ITEM")))
			{
				final String characteristicName = refCharTableRead.getString("ATNAM");
				final List<CharacteristicValue> valuesForContextAttribute = getCharacteristicValues(characteristicName,
						characteristicValues);
				if (valuesForContextAttribute.size() > 0)
				{
					final String contextName = refCharTableRead.getString("ATTAB") + "-" + refCharTableRead.getString("ATFEL");
					for (final CharacteristicValue valueForContextAttribute : valuesForContextAttribute)
					{
						final ContextAttribute contextAttribute = new ContextAttributeImpl();
						contextAttribute.setName(contextName);
						contextAttribute.setValue(valueForContextAttribute.getValue());
						configuration.addContextAttribute(contextAttribute);
					}
				}
			}
		}
		while (refCharTableRead.nextRow());

	}

	/**
	 * Filters the characteristic value list with a given characteristic name.
	 * 
	 * @param characteristicName
	 *           The characteristic we want to get the values for
	 * @param characteristicValues
	 *           Entire list of characteristics
	 * @return Filtered list
	 */
	protected List<CharacteristicValue> getCharacteristicValues(final String characteristicName,
			final List<CharacteristicValue> characteristicValues)
	{
		final List<CharacteristicValue> result = new ArrayList<>();
		for (final CharacteristicValue value : characteristicValues)
		{
			if (value.getCharacteristic().equals(characteristicName))
			{
				result.add(value);
			}
		}
		return result;
	}

	/**
	 * Adds date information for the current configuration
	 * 
	 * @param configuration
	 * @param salesDoc
	 */
	protected void addKbKey(final Configuration configuration, final SalesDocument salesDoc)
	{
		final KBKey kbKey = new KBKeyImpl(configuration.getRootInstance().getObjectKey(), null, null, null, salesDoc.getHeader()
				.getCreatedAt());
		configuration.setKbKey(kbKey);
	}





	//



}
