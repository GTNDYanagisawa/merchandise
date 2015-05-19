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
package de.hybris.platform.cockpit.services.config.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import de.hybris.platform.cockpit.model.advancedsearch.config.EditorConditionEntry;
import de.hybris.platform.cockpit.model.advancedsearch.config.impl.DefaultEditorConditionEntry;
import de.hybris.platform.cockpit.model.advancedsearch.config.impl.DefaultShortcutConditionEntry;
import de.hybris.platform.cockpit.model.editor.search.impl.AbstractExtensibleConditionUIEditor.ClearConditionEntry;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.services.config.AdvancedSearchConfiguration;
import de.hybris.platform.cockpit.services.config.SearchFieldConfiguration;
import de.hybris.platform.cockpit.services.config.SearchFieldConfiguration.EntryListMode;
import de.hybris.platform.cockpit.services.config.SearchFieldGroupConfiguration;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.AdvancedSearch;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Condition;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.ConditionList;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Group;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Label;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Mode;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Parameter;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Property;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.RelatedTypes;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.RootGroup;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.ShortcutValue;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.ShortcutValueList;
import de.hybris.platform.cockpit.services.config.jaxb.advancedsearch.Type;
import de.hybris.platform.cockpit.util.jaxb.impl.DefaultJAXBContextCache;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests conversion mechanism of the advanced search persisting strategy. This mechanism is responsible for conversion
 * of the cockpit configuration java class (<code>AdvancedSearchConfiguration</code>) to the <code>JAXB</code>
 * equivalent ( <code>AdvancedSearch</code>).
 */
public class AdvancedSearchConfigurationPersistingStrategyTest extends AbstractConfigurationPersistingStrategyTest
{
	private AdvancedSearchConfigurationPersistingStrategy strategy;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		this.strategy = new AdvancedSearchConfigurationPersistingStrategy();
		this.strategy.setJaxbContextCache(new DefaultJAXBContextCache());
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithoutContext()
	{
		final AdvancedSearchConfiguration config = createConfig(false);
		final AdvancedSearch search = this.strategy.updateJaxb(config);
		compare(search, config, false);
	}

	@Test
	public void testUpdateJaxbEditorConfigurationWithContext()
	{
		final AdvancedSearchConfiguration config = createConfig(true);
		final AdvancedSearch search = this.strategy.updateJaxb(config);
		compare(search, config, true);
	}

	private AdvancedSearchConfiguration createConfig(final boolean withContext)
	{
		JaxbBasedUIComponentConfigurationContext context = null;

		final DefaultAdvancedSearchConfiguration config = new DefaultAdvancedSearchConfiguration(templateProduct);
		if (withContext)
		{
			final AdvancedSearch root = new AdvancedSearch();
			context = new JaxbBasedUIComponentConfigurationContext(root);
		}
		config.setRelatedTypes(Arrays.asList(new ObjectTemplate[]
		{ templateProductCpu, templateProductVariant }));

		final DefaultSearchFieldGroupConfiguration rootGroup = new DefaultSearchFieldGroupConfiguration("root");
		config.setRootGroupConfiguration(rootGroup);
		rootGroup.setAllLabels(createLabels("root"));
		rootGroup.setVisible(true);
		config.setRootGroupConfiguration(rootGroup);
		if (withContext)
		{
			final RootGroup rootJaxbGroup = new RootGroup();
			context.registerJaxbElement(rootGroup, rootJaxbGroup);
		}

		List<SearchFieldConfiguration> fields = new ArrayList<SearchFieldConfiguration>();
		rootGroup.setSearchFieldConfigurations(fields);

		PropertySearchFieldConfiguration field = new PropertySearchFieldConfiguration(propCode);
		fields.add(field);
		field.setEditor(null);
		field.setEntryListMode(null);
		field.setName("code");
		field.setSortDisabled(false);
		field.setVisible(true);
		if (withContext)
		{
			final Property jaxbProperty = new Property();
			context.registerJaxbElement(field, jaxbProperty);
		}
		List<EditorConditionEntry> conditionEntries = new ArrayList<EditorConditionEntry>();
		field.setConditionEntries(conditionEntries);
		DefaultEditorConditionEntry condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Equals");
		condition.setIndex(1);
		condition.setOperator("equals");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT" });
		condition.setViewComponents(new String[]
		{ "_TYPE" });
		condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Between");
		condition.setIndex(2);
		condition.setOperator("between");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT", "INT" });
		condition.setViewComponents(new String[]
		{ "TEXT1", "TEXT2" });

		field = new PropertySearchFieldConfiguration(propName);
		fields.add(field);
		field.setEditor("TEXT");
		field.setEntryListMode(EntryListMode.APPEND);
		field.setName("name");
		field.setSortDisabled(false);
		field.setVisible(true);
		if (withContext)
		{
			final Property jaxbProperty = new Property();
			context.registerJaxbElement(field, jaxbProperty);
		}
		conditionEntries = new ArrayList<EditorConditionEntry>();
		field.setConditionEntries(conditionEntries);
		condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Equals");
		condition.setIndex(1);
		condition.setOperator("equals");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT" });
		condition.setViewComponents(new String[]
		{ "_TYPE" });
		condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Between");
		condition.setIndex(2);
		condition.setOperator("between");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT", "INT" });
		condition.setViewComponents(new String[]
		{ "TEXT1", "TEXT2" });

		final DefaultShortcutConditionEntry shortcutCondition = new DefaultShortcutConditionEntry();
		conditionEntries.add(shortcutCondition);
		shortcutCondition.setI3Label("Between");
		shortcutCondition.setIndex(3);
		shortcutCondition.setOperator("between");
		shortcutCondition.setValidAttributeTypes(new String[]
		{ "TEXT", "NUMBER", "BOOLEAN" });
		shortcutCondition.setViewComponents(new String[]
		{ "TEXT1", "TEXT2" });
		final List<Object> conditionValues = new ArrayList<Object>();
		conditionValues.add("stringval");
		conditionValues.add(Integer.valueOf(3));
		conditionValues.add(Boolean.TRUE);
		shortcutCondition.setConditionValues(conditionValues);

		final ClearConditionEntry clearCondition = new ClearConditionEntry();
		conditionEntries.add(clearCondition);
		clearCondition.setI3Label("Clear");
		clearCondition.setIndex(4);
		clearCondition.setOperator(null);
		clearCondition.setValidAttributeTypes(new String[] {});
		clearCondition.setViewComponents(new String[] {});

		final List<SearchFieldGroupConfiguration> subgroups = new ArrayList<SearchFieldGroupConfiguration>();
		rootGroup.setSearchFieldGroupConfigurations(subgroups);

		final DefaultSearchFieldGroupConfiguration subgroup = new DefaultSearchFieldGroupConfiguration("general");
		subgroups.add(subgroup);
		subgroup.setAllLabels(createLabels("additional"));
		subgroup.setVisible(false);
		if (withContext)
		{
			final Group jaxbGroup = new Group();
			context.registerJaxbElement(subgroup, jaxbGroup);
		}

		fields = new ArrayList<SearchFieldConfiguration>();
		subgroup.setSearchFieldConfigurations(fields);

		field = new PropertySearchFieldConfiguration(propUnitName);
		fields.add(field);
		field.setEditor(null);
		field.setEntryListMode(null);
		field.setName("unit name");
		field.setSortDisabled(false);
		field.setVisible(true);
		if (withContext)
		{
			final Property jaxbProperty = new Property();
			context.registerJaxbElement(field, jaxbProperty);
		}
		conditionEntries = new ArrayList<EditorConditionEntry>();
		field.setConditionEntries(conditionEntries);
		condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Equals");
		condition.setIndex(1);
		condition.setOperator("equals");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT" });
		condition.setViewComponents(new String[]
		{ "_TYPE" });
		condition = new DefaultEditorConditionEntry();
		conditionEntries.add(condition);
		condition.setI3Label("Between");
		condition.setIndex(2);
		condition.setOperator("between");
		condition.setValidAttributeTypes(new String[]
		{ "TEXT", "INT" });
		condition.setViewComponents(new String[]
		{ "TEXT1", "TEXT2" });

		return config;
	}

	private void compare(final AdvancedSearch search, final AdvancedSearchConfiguration config, final boolean withContext)
	{
		assertNotNull(config);

		final RelatedTypes jaxbRelatedTypes = search.getRelatedTypes();
		compareRelatedTypes(jaxbRelatedTypes, config.getRelatedTypes());

		compareGroup(search.getGroup(), config.getRootGroupConfiguration(), config);
	}

	private void compareRelatedTypes(final RelatedTypes jaxbRelatedTypes, final List<ObjectTemplate> relatedTypes)
	{
		assertEquals(jaxbRelatedTypes.getType().size(), relatedTypes.size());
		if (!relatedTypes.isEmpty())
		{
			final List<String> relatedTypeCodes = new ArrayList<String>();
			for (final ObjectTemplate template : relatedTypes)
			{
				relatedTypeCodes.add(template.getCode());
			}

			for (final Type jaxbType : jaxbRelatedTypes.getType())
			{
				assertTrue(relatedTypeCodes.contains(jaxbType.getCode()));
			}
		}
	}

	private void compareGroup(final Group jaxbGroup, final SearchFieldGroupConfiguration group,
			final AdvancedSearchConfiguration config)
	{
		assertNotNull(jaxbGroup);

		if (jaxbGroup instanceof RootGroup)
		{
			assertEquals(((RootGroup) jaxbGroup).getType(), config.getRootType().getCode());
		}

		assertEquals(group.isVisible(), jaxbGroup.isVisible());
		assertEquals(group.getName(), jaxbGroup.getName());

		compareLabel(group.getAllLabels(), jaxbGroup.getLabel());

		assertEquals(group.getSearchFieldGroupConfigurations().size(), jaxbGroup.getGroup().size());
		if (!group.getSearchFieldGroupConfigurations().isEmpty())
		{
			int index = 0;
			for (final SearchFieldGroupConfiguration subgroup : group.getSearchFieldGroupConfigurations())
			{
				compareGroup(jaxbGroup.getGroup().get(index++), subgroup, config);
			}
		}

		assertEquals(group.getSearchFieldConfigurations().size(), jaxbGroup.getProperty().size());
		if (!group.getSearchFieldConfigurations().isEmpty())
		{
			int index = 0;
			for (final SearchFieldConfiguration field : group.getSearchFieldConfigurations())
			{
				compareField(jaxbGroup.getProperty().get(index++), field);
			}
		}
	}

	private void compareLabelStringBased(final Map<String, String> configLabels, final List<Label> jaxbLabels)
	{
		assertEquals(configLabels.keySet().size(), jaxbLabels.size());
		if (!jaxbLabels.isEmpty())
		{
			final Map<String, String> labelLangMap = new HashMap<String, String>(jaxbLabels.size());
			for (final Label label : jaxbLabels)
			{
				labelLangMap.put(label.getLang(), label.getValue());
			}

			for (final Map.Entry<String, String> entry : configLabels.entrySet())
			{
				assertEquals(entry.getValue(), labelLangMap.get(entry.getKey()));
			}
		}
	}

	private void compareLabel(final Map<LanguageModel, String> configLabels, final List<Label> jaxbLabels)
	{
		assertEquals(configLabels.keySet().size(), jaxbLabels.size());
		if (!jaxbLabels.isEmpty())
		{
			final Map<String, String> labelLangMap = new HashMap<String, String>(jaxbLabels.size());
			for (final Label label : jaxbLabels)
			{
				labelLangMap.put(label.getLang(), label.getValue());
			}

			for (final Map.Entry<LanguageModel, String> entry : configLabels.entrySet())
			{
				assertEquals(entry.getValue(), labelLangMap.get(entry.getKey().getIsocode()));
			}
		}
	}

	private void compareField(final Property property, final SearchFieldConfiguration field)
	{
		assertEquals(field.getPropertyDescriptor().getQualifier(), property.getQualifier());
		assertEquals(field.isSortDisabled(), !property.isSortEnabled());
		assertEquals(field.isVisible(), property.isVisible());
		assertEquals(field.getEditor(), property.getEditor());

		compareParameters(field.getParameters(), property.getParameter());

		final ConditionList conditionList = property.getConditions();
		compareMode(conditionList.getMode(), field.getEntryListMode());

		final List<EditorConditionEntry> condtitionEntries = new ArrayList<EditorConditionEntry>(field.getConditionEntries());
		for (final EditorConditionEntry entry : field.getConditionEntries())
		{
			if (entry instanceof ClearConditionEntry)
			{
				// clear condition entry should not be persisted
				condtitionEntries.remove(entry);
			}
		}

		assertEquals(condtitionEntries.size(), conditionList.getCondition().size());
		if (!condtitionEntries.isEmpty())
		{
			int index = 0;
			for (final EditorConditionEntry entry : condtitionEntries)
			{
				final Condition condition = conditionList.getCondition().get(index++);

				compareCondition(condition, entry);
			}
		}
		property.getConditions();

	}

	private void compareMode(final Mode jaxbMode, final EntryListMode mode)
	{
		if (mode == null || mode.equals(EntryListMode.REPLACE))
		{
			assertTrue(jaxbMode == null || jaxbMode.equals(Mode.REPLACE));
		}
		else if (EntryListMode.APPEND.equals(mode))
		{
			assertEquals(jaxbMode, Mode.APPEND);
		}
		else if (EntryListMode.EXCLUDE.equals(mode))
		{
			assertEquals(jaxbMode, Mode.EXCLUDE);
		}
		else
		{
			fail("Unknown field mode: " + mode);
		}
	}

	private void compareCondition(final Condition condition, final EditorConditionEntry entry)
	{
		assertEquals(entry.getIndex(), condition.getIndex().intValue());
		assertEquals(entry.getOperator(), condition.getOperator());
		if (entry instanceof DefaultShortcutConditionEntry)
		{
			final DefaultShortcutConditionEntry shortcutEntry = (DefaultShortcutConditionEntry) entry;
			compareLabelStringBased(shortcutEntry.getAllLabels(), condition.getLabel());

			final ShortcutValueList shortcutValueList = condition.getValues();
			final List<Object> values = shortcutEntry.getConditionValues();
			if (values == null || values.isEmpty())
			{
				assertTrue(shortcutValueList == null || shortcutValueList.getValue().isEmpty());
			}
			else
			{
				assertEquals(values.size(), shortcutValueList.getValue().size());
				int index = 0;
				for (final ShortcutValue shortcutValue : shortcutValueList.getValue())
				{
					try
					{
						final Class valueClass = Class.forName(shortcutValue.getType());
						final Object wrappedValue = wrapValue(valueClass, shortcutValue.getValue());
						assertEquals(wrappedValue, values.get(index++));
					}
					catch (final Exception e)
					{
						fail(e.getMessage());
					}
				}
			}
		}
	}

	private void compareParameters(final Map<String, String> parameters, final List<Parameter> jaxbParams)
	{
		if (parameters == null || parameters.isEmpty())
		{
			assertTrue(jaxbParams == null || jaxbParams.isEmpty());
		}
		else
		{
			assertEquals(parameters.keySet().size(), jaxbParams.size());
			if (!jaxbParams.isEmpty())
			{
				final Map<String, String> paramNameMap = new HashMap<String, String>(jaxbParams.size());
				for (final Parameter para : jaxbParams)
				{
					paramNameMap.put(para.getName(), para.getValue());
				}

				for (final Map.Entry<String, String> entry : parameters.entrySet())
				{
					assertEquals(entry.getValue(), paramNameMap.get(entry.getKey()));
				}
			}
		}
	}

	private Object wrapValue(final Class clazz, final String stringRep) throws ParseException
	{
		if (String.class.equals(clazz))
		{
			return stringRep;
		}
		else if (Integer.class.equals(clazz))
		{
			return Integer.valueOf(Integer.parseInt(stringRep));
		}
		else if (Long.class.equals(clazz))
		{
			return Long.valueOf(Long.parseLong(stringRep));
		}
		else if (Short.class.equals(clazz))
		{
			return Short.valueOf(Short.parseShort(stringRep));
		}
		else if (Byte.class.equals(clazz))
		{
			return Byte.valueOf(Byte.parseByte(stringRep));
		}
		else if (Float.class.equals(clazz))
		{
			return Float.valueOf(Float.parseFloat(stringRep));
		}
		else if (Double.class.equals(clazz))
		{
			return Double.valueOf(Double.parseDouble(stringRep));
		}
		else if (Date.class.equals(clazz))
		{
			return DateFormat.getInstance().parse(stringRep);
		}
		else if (Boolean.class.equals(clazz))
		{
			return Boolean.valueOf(stringRep);
		}
		else
		{
			throw new ParseException("No wrapper defined", 0);
		}
	}

}
