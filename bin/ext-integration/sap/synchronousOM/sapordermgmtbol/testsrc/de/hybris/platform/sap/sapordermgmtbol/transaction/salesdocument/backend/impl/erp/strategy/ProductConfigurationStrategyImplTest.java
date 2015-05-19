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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.CharacteristicValueImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


@UnitTest
@SuppressWarnings("javadoc")
public class ProductConfigurationStrategyImplTest extends JCORecTestBase
{
	ProductConfigurationStrategyImpl classUnderTest = null;
	ConfigModel configModel = null;
	JCoConnection connection = null;
	JCoParameterList importParameters = null;
	JCoParameterList exportParameters = null;

	JCoParameterList importParametersRead = null;
	JCoParameterList exportParametersRead = null;
	JCoParameterList tableParametersRead = null;

	JCoFunction function = null;
	JCoFunction functionRead = null;
	JCoTable instExt = null;
	JCoTable charExt = null;
	JCoTable messages = null;
	JCoTable unprocessed = null;

	//JCO tables for configuration read
	JCoTable instancesTableRead = null;
	JCoTable csticsTableRead = null;
	JCoTable refCharTableRead = null;

	private JCoTable tableItemHandles;

	final String handle = "A";
	private final static String rootId = "1";
	private final static String subInstId = "2";
	private final static String subInstBOMNr = "0010";
	private final static String csticName = "CHAR1";
	private final static String csticVal = "VAL1";
	private final static String csticName2 = "CHAR2";
	private final static String csticVal2 = "VAL2";
	private SalesDocument salesDoc;
	private Item item;
	private List<String> configurableItems;


	@Before
	public void init() throws BackendException
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PCFG_STRATEGY);
		readTablesFromRepository();

		configModel = getConfigModel(null);
		connection = EasyMock.createMock(JCoConnection.class);
		importParameters = EasyMock.createMock(JCoParameterList.class);
		importParameters.setValue("IV_HANDLE_ITEM", handle);
		importParameters.setValue("IV_INTERNAL_INPUT", "X");

		importParametersRead = EasyMock.createMock(JCoParameterList.class);
		importParametersRead.setValue("IV_VIEWVAR", ProductConfigurationStrategyImpl.WEIGHTED_CHARACTERISTICS);
		importParametersRead.setValue("IV_INTERNAL_OUTPUT", "X");
		EasyMock.expect(importParametersRead.getTable("IT_HANDLE_ITEM")).andReturn(tableItemHandles).anyTimes();
		exportParameters = EasyMock.createMock(JCoParameterList.class);
		exportParametersRead = EasyMock.createMock(JCoParameterList.class);
		tableParametersRead = EasyMock.createMock(JCoParameterList.class);

		function = EasyMock.createMock(JCoFunction.class);
		functionRead = EasyMock.createMock(JCoFunction.class);
		EasyMock.expect(connection.getFunction(ProductConfigurationStrategyImpl.RFC_NAME)).andReturn(function);
		EasyMock.expect(connection.getFunction(ProductConfigurationStrategyImpl.RFC_NAME_READ)).andReturn(functionRead);
		connection.execute(function);
		connection.execute(functionRead);
		EasyMock.expect(function.getImportParameterList()).andReturn(importParameters);
		EasyMock.expect(functionRead.getImportParameterList()).andReturn(importParametersRead);
		EasyMock.expect(functionRead.getExportParameterList()).andReturn(exportParametersRead).anyTimes();
		EasyMock.expect(exportParametersRead.getTable(ProductConfigurationStrategyImpl.TABLE_ET_VCFG_INST)).andReturn(
				instancesTableRead);
		EasyMock.expect(exportParametersRead.getTable(ProductConfigurationStrategyImpl.TABLE_ET_VCFG_CHAR)).andReturn(
				csticsTableRead);
		EasyMock.expect(exportParametersRead.getTable(ProductConfigurationStrategyImpl.TABLE_MESSAGES)).andReturn(messages);
		EasyMock.expect(tableParametersRead.getTable(ProductConfigurationStrategyImpl.TABLE_TT_REFCHAR))
				.andReturn(refCharTableRead);
		EasyMock.expect(functionRead.getTableParameterList()).andReturn(tableParametersRead);
		EasyMock.expect(function.getExportParameterList()).andReturn(exportParameters).anyTimes();
		EasyMock.expect(importParameters.getTable(ProductConfigurationStrategyImpl.TABLE_IT_INST_EXT)).andReturn(instExt);
		EasyMock.expect(importParameters.getTable(ProductConfigurationStrategyImpl.TABLE_IT_CHAR_EXT)).andReturn(charExt);
		EasyMock.expect(exportParameters.getTable(ProductConfigurationStrategyImpl.TABLE_MESSAGES)).andReturn(messages);
		EasyMock.expect(exportParameters.getTable(ProductConfigurationStrategyImpl.TABLE_UNPROCESSED)).andReturn(unprocessed);
		EasyMock.replay(connection, importParameters, importParametersRead, exportParameters, function, functionRead,
				exportParametersRead, tableParametersRead);

		salesDoc = new BasketImpl();
		salesDoc.setItemList(new ItemListImpl());
		item = new ItemSalesDoc();
		item.setHandle(handle);
		item.setTechKey(new TechKey(handle));
		salesDoc.addItem(item);
		final Header header = new HeaderSalesDocument();
		header.setCreatedAt(new Date(System.currentTimeMillis()));
		salesDoc.setHeader(header);
		configurableItems = new ArrayList<>();
		configurableItems.add(handle);




	}

	public static ConfigModel getConfigModel(final String author)
	{
		final ConfigModel model = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId(rootId);
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		final CsticValueModel assignedValue = new CsticValueModelImpl();
		if (author != null)
		{
			assignedValue.setAuthorExternal(author);
		}

		assignedValue.setName(csticVal);
		cstic.setAssignedValues(Arrays.asList(assignedValue));
		rootInstance.setCstics(Arrays.asList(cstic));
		model.setRootInstance(rootInstance);
		return model;
	}

	private ConfigModel getConfigModelMulti()
	{
		final ConfigModel model = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId(rootId);
		model.setRootInstance(rootInstance);
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setPosition(subInstBOMNr);
		subInstance.setId(subInstId);
		rootInstance.setSubInstances(Arrays.asList(subInstance));
		return model;
	}

	@Test
	public void testWriteConfiguration()
	{
		final BusinessObject bo = new BasketImpl();

		classUnderTest.writeConfiguration(connection, configModel, handle, bo);
		assertEquals(2, bo.getMessageList().size());
	}

	@Test
	public void testSetImportParams()
	{
		classUnderTest.fillImportParameters(importParameters, handle);
	}

	@Test
	public void testToTablesRoot()
	{
		classUnderTest.toTables(getConfigModel(null), instExt, charExt);
		assertEquals(1, instExt.getNumRows());
		instExt.firstRow();
		assertEquals(rootId, instExt.getString("INSTANCE"));
		assertEquals("", instExt.getString("PARENT"));
		assertEquals("", instExt.getString("ITEM_NUMBER"));
		assertEquals("X", instExt.getString("CRESET"));
	}

	@Test
	public void testToTablesRootChars()
	{
		classUnderTest.toTables(getConfigModel(null), instExt, charExt);
		checkBasicCsticAttrs();
	}



	@Test
	public void testToTablesRootCharsAuthorDefault()
	{
		classUnderTest.toTables(getConfigModel(ProductConfigurationStrategyImpl.AUTHOR_DEFAULT), instExt, charExt);
		checkBasicCsticAttrs();
	}

	@Test
	public void testToTablesRootCharsAuthorX()
	{
		classUnderTest.toTables(getConfigModel(ProductConfigurationStrategyImpl.AUTHOR_EXTERNAL), instExt, charExt);
		checkBasicCsticAttrs();
	}

	@Test
	public void testToTablesRootCharsAuthorOther()
	{
		classUnderTest.toTables(getConfigModel("5"), instExt, charExt);
		assertEquals(0, charExt.getNumRows());
	}



	@Test
	public void testToTablesMulti()
	{
		classUnderTest.toTables(getConfigModelMulti(), instExt, charExt);
		assertEquals(2, instExt.getNumRows());
		instExt.lastRow();
		assertEquals(subInstId, instExt.getString("INSTANCE"));
		assertEquals(rootId, instExt.getString("PARENT"));
		assertEquals(subInstBOMNr, instExt.getString("ITEM_NUMBER"));
		assertEquals("X", instExt.getString("CRESET"));
	}


	@Test
	public void testToTablesNoRoot()
	{
		configModel.setRootInstance(null);
		classUnderTest.toTables(configModel, instExt, charExt);
		assertEquals(0, instExt.getNumRows());
	}

	@Test
	public void testAddMessages()
	{
		final BusinessObject cart = new BasketImpl();
		classUnderTest.addMessages(cart, messages);
		assertEquals(1, cart.getMessageList().size());
	}

	@Test
	public void testAddMessagesEmptyMessageTable()
	{
		final BusinessObject cart = new BasketImpl();
		messages.clear();
		classUnderTest.addMessages(cart, messages);
		assertEquals(0, cart.getMessageList().size());
	}

	@Test
	public void testGetMessageMapper()
	{
		assertNotNull(classUnderTest.getMessageMapper());
	}

	@Test
	public void testAddMessagesUnprocessed()
	{

		final BusinessObject cart = new BasketImpl();
		classUnderTest.addMessagesUnprocessed(cart, getConfigModelMulti().getRootInstance(), unprocessed);
		assertEquals(1, cart.getMessageList().size());
	}

	@Test
	public void testAddMessagesUnprocessedEmpty()
	{

		final BusinessObject cart = new BasketImpl();
		unprocessed.clear();
		classUnderTest.addMessagesUnprocessed(cart, getConfigModelMulti().getRootInstance(), unprocessed);
		assertEquals(0, cart.getMessageList().size());
	}

	private void checkBasicCsticAttrs()
	{
		assertEquals(1, charExt.getNumRows());
		charExt.firstRow();
		assertEquals(csticName, charExt.getString("ATNAM"));
		assertEquals(csticVal, charExt.getString("ATVAL"));
	}

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposConfigurationStrategy");

			instExt = testRepository.getTable("IT_INST_EXT");
			charExt = testRepository.getTable("IT_CHAR_EXT");
			messages = testRepository.getTable("ET_MESSAGES");
			unprocessed = testRepository.getTable("ET_CHAR_UNPROCESSED");

			//These tables will be filled, we don't want content, but need to have 1 line 
			//to identify object as table
			instExt.clear();
			charExt.clear();
			//Table messages and unprocessed we provide from XML

			//tables for reading configuration
			tableItemHandles = testRepository.getTable("IT_HANDLE_ITEM");
			tableItemHandles.clear();

			instancesTableRead = testRepository.getTable("ET_VCFG_INST");
			csticsTableRead = testRepository.getTable("ET_VCFG_CHAR");
			refCharTableRead = testRepository.getTable("TT_REFCHAR");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

	@Test
	public void testReadConfiguration()
	{


		classUnderTest.readConfiguration(connection, salesDoc, configurableItems);
	}

	@Test
	public void testSetImportParametersRead()
	{
		classUnderTest.fillImportParametersRead(importParametersRead, configurableItems);
		assertEquals(1, importParametersRead.getTable("IT_HANDLE_ITEM").getNumRows());
	}

	@Test
	public void testOutputChars()
	{
		final InstanceModel instance = new InstanceModelImpl();
		final CsticModel characteristic = new CsticModelImpl();
		final CsticValueModel csticValue = new CsticValueModelImpl();
		classUnderTest.debugOutputChars(instance, characteristic, csticValue);
	}

	@Test
	public void testCreateConfigModel()
	{
		final Configuration configuration = classUnderTest.createConfigModel(handle, instancesTableRead, csticsTableRead,
				refCharTableRead);
		assertNotNull(configuration);
		final Instance rootInstance = configuration.getRootInstance();
		assertNotNull(rootInstance);
	}

	@Test
	public void testAddKbKey()
	{
		final Configuration configuration = classUnderTest.createConfigModel(handle, instancesTableRead, csticsTableRead,
				refCharTableRead);
		assertNotNull(configuration);
		classUnderTest.addKbKey(configuration, salesDoc);
		assertNotNull(configuration.getKbKey());
		assertNotNull(configuration.getKbKey().getDate());
	}

	@Test
	public void testCreateInstances()
	{
		final Configuration externalConfiguration = new ConfigurationImpl();
		classUnderTest.createInstances(handle, externalConfiguration, instancesTableRead);
		final Instance rootInstance = externalConfiguration.getRootInstance();
		assertNotNull(rootInstance);
		final List<Instance> instances = externalConfiguration.getInstances();
		assertEquals(3, instances.size());

		//now check second instance
		final Instance subInstance = instances.get(1);
		assertEquals("", subInstance.getAuthor());
		assertEquals("300", subInstance.getClassType());
		assertEquals("2", subInstance.getId());
		assertEquals("KD990GRINDER", subInstance.getObjectKey());
		assertEquals("Grinder", subInstance.getObjectText());
		assertEquals("MARA", subInstance.getObjectType());
		assertEquals("1.0", subInstance.getQuantity());
		assertEquals("ST", subInstance.getQuantityUnit());
		assertTrue(subInstance.isComplete());
		assertTrue(subInstance.isConsistent());
	}

	@Test
	public void testCheckPartOfRelations()
	{
		final Configuration externalConfiguration = new ConfigurationImpl();
		classUnderTest.createInstances(handle, externalConfiguration, instancesTableRead);
		final List<PartOfRelation> partOfRelations = externalConfiguration.getPartOfRelations();
		assertEquals(2, partOfRelations.size());
	}

	@Test
	public void testCreatePartOfRelation()
	{

		//go to second row to create a part-of relation
		instancesTableRead.nextRow();
		final PartOfRelation partOfRelation = classUnderTest.createPartOfRelation(instancesTableRead);
		assertNotNull(partOfRelation);
		assertEquals("1", partOfRelation.getParentInstId());
		assertEquals("2", partOfRelation.getInstId());
		assertEquals("", partOfRelation.getAuthor());
		assertEquals("300", partOfRelation.getClassType());
		assertEquals("KD990GRINDER", partOfRelation.getObjectKey());
		assertEquals("MARA", partOfRelation.getObjectType());
		assertEquals("10", partOfRelation.getPosNr());

	}

	@Test
	public void testMapObjectCategory()
	{
		final String objectCategory = "M";
		final String objectType = "MARA";
		assertEquals(objectType, classUnderTest.mapObjectCategory(objectCategory));
		assertEquals("KLAH", classUnderTest.mapObjectCategory("C"));
		//no exception in this case
		assertEquals("", classUnderTest.mapObjectCategory("1"));
	}

	@Test
	public void testCreateCharacteristics()
	{
		final Configuration externalConfiguration = new ConfigurationImpl();
		classUnderTest.createCharacteristics(handle, externalConfiguration, csticsTableRead);
		final List<CharacteristicValue> characteristicValues = externalConfiguration.getCharacteristicValues();
		assertNotNull(characteristicValues);
		assertEquals(3, characteristicValues.size());
	}



	@Test
	public void testCreateConfigModels()
	{
		classUnderTest.createConfigModels(configurableItems, salesDoc, instancesTableRead, csticsTableRead, refCharTableRead);
		final Item itemAfterReadingCfg = salesDoc.getItemList().get(0);
		assertNotNull(itemAfterReadingCfg);
		assertNotNull(itemAfterReadingCfg.getProductConfigurationExternal());
	}

	@Test
	public void testIsParentInitial()
	{
		final String parent = null;
		assertTrue(classUnderTest.isParentInitial(parent));
		assertTrue(classUnderTest.isParentInitial(""));
	}

	@Test
	public void testIsParentInitialNumStrings()
	{
		assertTrue(classUnderTest.isParentInitial("0"));
		assertTrue(classUnderTest.isParentInitial("000000"));
		assertFalse(classUnderTest.isParentInitial("000001"));
	}

	@Test
	public void testRefChars()
	{
		final Configuration configuration = classUnderTest.createConfigModel(handle, instancesTableRead, csticsTableRead,
				refCharTableRead);
		assertNotNull(configuration);

		final List<ContextAttribute> contextAttributes = configuration.getContextAttributes();
		assertNotNull(contextAttributes);
		assertEquals(3, contextAttributes.size());
		final ContextAttribute contextAttribute = contextAttributes.get(0);
		assertEquals("VBAP-MATNR", contextAttribute.getName());
		assertEquals("VAL1", contextAttribute.getValue());
	}

	@Test
	public void testAddRefChars()
	{
		final Configuration configuration = new ConfigurationImpl();

		final CharacteristicValue characteristicValue = new CharacteristicValueImpl();
		characteristicValue.setCharacteristic(csticName);
		characteristicValue.setValue(csticVal);
		configuration.addCharacteristicValue(characteristicValue);

		final CharacteristicValue characteristicValue2 = new CharacteristicValueImpl();
		characteristicValue2.setCharacteristic(csticName2);
		characteristicValue2.setValue(csticVal2);
		configuration.addCharacteristicValue(characteristicValue2);

		classUnderTest.createContextAttributes(handle, configuration, refCharTableRead);

		assertEquals(2, configuration.getContextAttributes().size());
	}

	@Test
	public void testAddRefCharsNoChars()
	{
		final Configuration configuration = new ConfigurationImpl();
		classUnderTest.createContextAttributes(handle, configuration, refCharTableRead);

		assertEquals(0, configuration.getContextAttributes().size());
	}

	@Test
	public void testGetCharacteristicValues()
	{
		final Configuration configuration = classUnderTest.createConfigModel(handle, instancesTableRead, csticsTableRead,
				refCharTableRead);
		final List<CharacteristicValue> characteristicValues = configuration.getCharacteristicValues();
		assertEquals(3, characteristicValues.size());
		final List<CharacteristicValue> filteredValues = classUnderTest.getCharacteristicValues(csticName2, characteristicValues);
		assertEquals(2, filteredValues.size());
	}
}
