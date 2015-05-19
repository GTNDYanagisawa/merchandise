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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ConfigurationTestData
{
	public final static String CONFIG_ID = "1";
	public final static String CONFIG_NAME = "Config Name";

	public final static String ROOT_INSTANCE_ID = "1";
	public final static String ROOT_INSTANCE_NAME = "SIMPLE_PRODUCT";
	public final static String ROOT_INSTANCE_LANG_DEP_NAME = "simple product for";

	public final static String STR_NAME = "SAP_STRING_SIMPLE";
	public final static String STR_LD_NAME = "Simple String:";

	public final static String CHBOX_NAME = "SAP_CHECKBOX_SIMPLE";
	public final static String CHBOX_LD_NAME = "Simple Checkbox:";

	public final static String RB_NAME = "SAP_RADIOBUTTON_SIMPLE";
	public final static String RB_LD_NAME = "Simple RadioButton:";

	public final static String DROPD_NAME = "SAP_DROPDOWN_SIMPLE";
	public final static String DROPD_LD_NAME = "Simple DropDownList:";

	public final static String CHBOX_LIST_NAME = "SAP_CHECKBOX_LIST_SIMPLE";
	public final static String CHBOX_LIST_LD_NAME = "Checkbox List:";

	public final static String RB_ADDV_NAME = "SAP_RADIOBUTTON_ADDVALUE";
	public final static String RB_ADDV_LD_NAME = "Radio Button With additional Value:";

	public final static String DROPD_AADV_NAME = "SAP_DROPDOWN_ADDVALUE";
	public final static String DROPD_ADDV_LD_NAME = "DropDownList with Additional Values:";
	private static final String DROPD_ADDV_NAME = null;

	static int instanceId = 1;



	public static ConfigModel createConfigModelWithCstic()
	{
		final ConfigModel model = createEmptyConfigModel();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(new BigDecimal(1000));
		basePrice.setCurrency("EUR");
		model.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(50));
		selectedOptionsPrice.setCurrency("EUR");
		model.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1050));
		currentTotalPrice.setCurrency("EUR");
		model.setCurrentTotalPrice(currentTotalPrice);

		final InstanceModel rootInstance = model.getRootInstance();
		// Characteristics and Values

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		cstics.add(createSTRCstic());
		rootInstance.setCstics(cstics);

		return model;
	}

	public static ConfigModel createConfigModelWithGroups()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();

		rootInstance.getCstics().add(createCheckBoxListCsticWithValue2Assigned());
		rootInstance.getCstics().add(createCheckBoxCstic());

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2", "Group 2", CHBOX_LIST_NAME);

		csticGroups.add(group1);
		csticGroups.add(group2);

		rootInstance.setCsticGroups(csticGroups);

		return configModel;
	}

	public static ConfigModel createEmptyConfigModel()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId(CONFIG_ID);
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		// Root Instance
		InstanceModel rootInstance;
		rootInstance = createInstance();

		model.setRootInstance(rootInstance);

		return model;
	}

	public static ConfigModel createConfigModelWithSubInstanceOnly()
	{
		final ConfigModel model = createEmptyConfigModel();

		final InstanceModel rootInstance = model.getRootInstance();

		final InstanceModel subInstance = createInstance();
		rootInstance.getSubInstances().add(subInstance);

		return model;
	}


	/**
	 * @return
	 */
	private static InstanceModel createInstance()
	{
		InstanceModel rootInstance;
		rootInstance = new InstanceModelImpl();
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName(ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		rootInstance.setSubInstances(new ArrayList<InstanceModel>());
		return rootInstance;
	}

	public static ConfigModel createConfigModelWithSubInstance()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId(CONFIG_ID);
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		// Root Instance
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName(ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		final ArrayList<InstanceModel> subInstances = new ArrayList<InstanceModel>();
		subInstances.add(createSubInstance("SUBINSTANCE1"));
		rootInstance.setSubInstances(subInstances);

		model.setRootInstance(rootInstance);

		// Characteristics and Values

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		cstics.add(createSTRCstic());
		rootInstance.setCstics(cstics);

		return model;
	}

	public static ConfigModel createConfigModelWithGroupsAndSubInstances()
	{
		final ConfigModel model = createConfigModelWithGroups();

		final ArrayList<InstanceModel> subInstancesLevel1 = new ArrayList<InstanceModel>();
		final InstanceModel subInstance1Level1 = createSubInstance("SUBINSTANCE1LEVEL1");
		subInstancesLevel1.add(subInstance1Level1);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1INST1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2INST1", "Group 2", CHBOX_LIST_NAME);
		csticGroups.add(group1);
		csticGroups.add(group2);
		subInstance1Level1.setCsticGroups(csticGroups);
		List<CsticModel> cstics = new ArrayList<CsticModel>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCstic());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level1.setCstics(cstics);


		final ArrayList<InstanceModel> subInstancesLevel2 = new ArrayList<InstanceModel>();
		final InstanceModel subInstance1Level2 = createSubInstance("SUBINSTANCE1LEVEL2");
		cstics = new ArrayList<CsticModel>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCstic());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level2.setCstics(cstics);
		subInstancesLevel2.add(subInstance1Level2);
		subInstancesLevel2.add(createSubInstance("SUBINSTANCE2LEVEL2"));
		subInstance1Level1.setSubInstances(subInstancesLevel2);

		final ArrayList<InstanceModel> subInstancesLevel3 = new ArrayList<InstanceModel>();
		subInstancesLevel3.add(createSubInstance("SUBINSTANCE1LEVEL3"));
		subInstance1Level2.setSubInstances(subInstancesLevel3);

		subInstancesLevel1.add(createSubInstance("SUBINSTANCE2LEVEL1"));
		model.getRootInstance().setSubInstances(subInstancesLevel1);

		return model;
	}



	private static CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}

	public static CsticModel createSTRCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(STR_NAME);
		cstic.setLanguageDependentName(STR_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);
		cstic.setVisible(true);
		cstic.setLongText("Model long text");

		return cstic;
	}



	public static CsticModel createReadOnlyCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(STR_NAME);
		cstic.setLanguageDependentName(STR_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);

		cstic.setReadonly(true);

		return cstic;
	}

	public static CsticModel createCheckBoxCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setSingleValue("X");



		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("X");
		assignableValues.add(value);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	public static CsticModel createCheckBoxListCsticWithValue2Assigned()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CHBOX_LIST_NAME);
		cstic.setLanguageDependentName(CHBOX_LIST_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(30);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(true);

		final CsticValueModelImpl value1 = createCsticValueModel(1);
		final CsticValueModelImpl value2 = createCsticValueModel(2);
		final CsticValueModelImpl value3 = createCsticValueModel(3);



		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		assignedValues.add(value2);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	private static CsticValueModelImpl createCsticValueModel(final int ii)
	{
		final CsticValueModelImpl value1 = new CsticValueModelImpl();
		value1.setName("VAL" + ii);
		value1.setLanguageDependentName("VALUE " + ii);
		value1.setDomainValue(true);
		return value1;
	}

	public static CsticModel createRadioButtonCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	public static CsticModel createDropDownCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		assignableValues.add(createCsticValue("VALUE_3"));
		assignableValues.add(createCsticValue("VALUE_4"));
		assignableValues.add(createCsticValue("VALUE_5"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	private static CsticValueModel createCsticValue(final String value)
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		valueModel.setName(value);
		valueModel.setLanguageDependentName(value);
		return valueModel;
	}

	public static CsticModel createUndefinedCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName("DUMMY");
		cstic.setLanguageDependentName("dummy");
		cstic.setValueType(CsticModel.TYPE_UNDEFINED);
		setDefaultProperties(cstic);

		return cstic;
	}

	private static void setDefaultProperties(final CsticModel cstic)
	{
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		cstic.setAssignableValues(assignableValues);
	}

	public static CsticModel createFloatCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		final String name = "Numeric";
		cstic.setName(name);
		cstic.setLanguageDependentName(name);
		setDefaultProperties(cstic);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		cstic.setTypeLength(8);
		cstic.setNumberScale(2);
		cstic.setSingleValue("0");

		return cstic;
	}

	public static CsticModel createIntegerCstic()
	{

		final CsticModel cstic = new CsticModelImpl();
		final String name = "Numeric";
		cstic.setName(name);
		cstic.setLanguageDependentName(name);
		setDefaultProperties(cstic);
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setSingleValue("0");

		return cstic;
	}

	/**
	 * @return
	 */
	public static CsticModel createSTRCsticWithConflicts()
	{
		final CsticModel cstic = createSTRCstic();

		ConflictModel conflict = new ConflictModelImpl();
		cstic.addConflict(conflict);

		conflict = new ConflictModelImpl();
		cstic.addConflict(conflict);
		return cstic;
	}

	private static InstanceModel createSubInstance(final String instanceName)
	{
		final InstanceModel subInstance = new InstanceModelImpl();
		instanceId++;
		subInstance.setId(String.valueOf(instanceId));
		subInstance.setName(instanceName);
		return subInstance;
	}

	/**
	 * @return
	 */
	public static CsticModel createRadioButtonWithAddValueCstic()
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(RB_ADDV_NAME);
		cstic.setLanguageDependentName(RB_ADDV_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setAllowsAdditionalValues(true);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	/**
	 * @return
	 */
	public static CsticModel createDropDownWithAddValueCstic()
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(DROPD_AADV_NAME);
		cstic.setLanguageDependentName(DROPD_ADDV_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setAllowsAdditionalValues(true);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(createCsticValue("VAL1"));
		assignableValues.add(createCsticValue("VAL2"));
		assignableValues.add(createCsticValue("VAL3"));
		assignableValues.add(createCsticValue("VAL4"));
		assignableValues.add(createCsticValue("VAL5"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

}
