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
package de.hybris.platform.sap.core.jco.rec.version100;

import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.impl.JCoRecFunctionDecorator;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.DataElement;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.DataStructure;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.DataTable;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Element;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FieldType;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FunctionParameter;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.LineType;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaElement;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaStructure;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaTable;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Repository;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Row;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Structure;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.util.Codecs;


/**
 * Implementation of {@link RepositoryPlayback} for the version 1.0.0 of the {@link Repository}.
 */
public class RepositoryPlayback100 implements RepositoryPlayback
{
	private final Repository repo;

	private int executionCounter;

	/**
	 * Constructor.
	 * 
	 * @param repo
	 *           the file containing the recorded back-end data.
	 */
	public RepositoryPlayback100(final Repository repo)
	{
		this.repo = repo;
	}

	/**
	 * Increases execution counter.
	 * 
	 * @return current execution counter.
	 */
	private int increaseCounter()
	{
		executionCounter += Integer.valueOf(1);
		return executionCounter;
	}

	@Override
	public JCoFunction getFunction(final String functionName) throws JCoRecException
	{
		final int count = increaseCounter();//functionName);
		for (final FunctionParameter fp : repo.getFunctions().getFunction())
		{
			if (fp.getFunctionName().equals(functionName) && fp.getExecutionOrder().intValue() == count)
			{
				final JCoFunction function = createAndPopulateFunction(fp);
				return new JCoRecFunctionDecorator(function, fp.getExecutionOrder().intValue(), JCoRecMode.PLAYBACK);
			}
		}

		throw new JCoRecException("Function " + functionName + RecorderUtils.FUNCTIONKEY_SEPERATOR + count + " not in Repository!");
	}

	/**
	 * Creates and populates a JCoFunction.
	 * 
	 * @param fp
	 *           Function Parameter.
	 * @return JCoFunction.
	 */
	private JCoFunction createAndPopulateFunction(final FunctionParameter fp)
	{
		// parameter-list will only be considered as long as it is existent and not empty
		final boolean existImport = (fp.getImport() != null) && !fp.getImport().getElementOrStructureOrTable().isEmpty();
		final boolean existExport = (fp.getExport() != null) && !fp.getExport().getElementOrStructureOrTable().isEmpty();
		final boolean existChanging = (fp.getChanging() != null) && !fp.getChanging().getElementOrStructureOrTable().isEmpty();
		final boolean existTables = (fp.getTables() != null) && !fp.getTables().getElementOrStructureOrTable().isEmpty();

		JCoListMetaData imports = null;
		JCoListMetaData exports = null;
		JCoListMetaData changing = null;
		JCoListMetaData tables = null;
		final AbapException[] exceptions = null;

		if (existImport)
		{
			imports = createRecords(fp.getImport().getElementOrStructureOrTable(), Utils100.PARAMETERLIST_IMPORT_NAME);
		}
		if (existExport)
		{
			exports = createRecords(fp.getExport().getElementOrStructureOrTable(), Utils100.PARAMETERLIST_EXPORT_NAME);
		}
		if (existChanging)
		{
			changing = createRecords(fp.getChanging().getElementOrStructureOrTable(), Utils100.PARAMETERLIST_CHANGING_NAME);
		}
		if (existTables)
		{
			tables = createRecords(fp.getTables().getElementOrStructureOrTable(), Utils100.PARAMETERLIST_TABLES_NAME);
		}

		final JCoFunctionTemplate functionTemplate = JCo.createFunctionTemplate(fp.getFunctionName(), imports, exports, changing,
				tables, exceptions);
		final JCoFunction function = functionTemplate.getFunction();

		if (existImport)
		{
			populateRecords(fp.getImport().getElementOrStructureOrTable(), function.getImportParameterList());
		}
		if (existExport)
		{
			populateRecords(fp.getExport().getElementOrStructureOrTable(), function.getExportParameterList());
		}
		if (existChanging)
		{
			populateRecords(fp.getChanging().getElementOrStructureOrTable(), function.getChangingParameterList());
		}
		if (existTables)
		{
			populateRecords(fp.getTables().getElementOrStructureOrTable(), function.getTableParameterList());
		}

		return function;
	}

	/**
	 * Create records.
	 * 
	 * @param list
	 *           list.
	 * @param listName
	 *           listName.
	 * @return List meta data.
	 */
	private JCoListMetaData createRecords(final List<? extends SuperToString> list, final String listName)
	{
		final JCoListMetaData lmd = JCo.createListMetaData(listName);
		for (final Object o : list)
		{
			if (o instanceof Element)
			{
				addElementToListMetaData((Element) o, lmd);
			}
			else if (o instanceof Structure)
			{
				addStructureToListMetaData((Structure) o, lmd);
			}
			else if (o instanceof Table)
			{
				addTableToListMetaData((Table) o, lmd);
			}
		}

		lmd.lock();
		return lmd;
	}

	/**
	 * Adds element to list meta data.
	 * 
	 * @param e
	 *           Element.
	 * @param lmd
	 *           List meta data.
	 */
	private void addElementToListMetaData(final Element e, final JCoListMetaData lmd)
	{
		final int bl = getLength(e);
		final int decimals = e.getBCDDecimals() == null ? 0 : e.getBCDDecimals().intValue();
		lmd.add(e.getName(), Utils100.fieldTypeToInt(e.getFieldType()), bl, 2 * bl, decimals, " ", e.getDescription(),
				JCoListMetaData.OPTIONAL_PARAMETER, null, null);
	}

	/**
	 * Adds a structure to list meta data.
	 * 
	 * @param s
	 *           Structure.
	 * @param lmd
	 *           List meta data.
	 */
	private void addStructureToListMetaData(final Structure s, final JCoListMetaData lmd)
	{
		final JCoRecordMetaData rmd = JCo.createRecordMetaData(s.getName());
		final int bl = populateStructureRMD(s, rmd);
		//		final int bl = rmd.getRecordLength();
		lmd.add(s.getName(), JCoMetaData.TYPE_STRUCTURE, bl, 2 * bl, 0, " ", s.getDescription(),
				JCoListMetaData.OPTIONAL_PARAMETER, rmd, null);
	}

	/**
	 * Adds a table to list meta data.
	 * 
	 * @param t
	 *           Table.
	 * 
	 * @param lmd
	 *           List meta data.
	 */
	private void addTableToListMetaData(final Table t, final JCoListMetaData lmd)
	{
		final JCoRecordMetaData rmd = populateRowRMD(t.getTableName(), t.getLineType());

		// add optional name and type to rows to ease filling of the JCoTable
		insertMetaDataIntoRowElements(t);

		final int bl = rmd.getRecordLength();
		lmd.add(t.getName(), JCoMetaData.TYPE_TABLE, bl, 2 * bl, 0, " ", t.getDescription(), JCoListMetaData.OPTIONAL_PARAMETER,
				rmd, null);
	}

	/**
	 * Inserts meta data into a row elements.
	 * 
	 * @param t
	 *           Datatable.
	 */
	private void insertMetaDataIntoRowElements(final DataTable t)
	{
		final List<? extends SuperToString> rowMeta = t.getLineType().getElementMetaDataOrStructureMetaDataOrTableMetaData();
		for (final Row r : t.getRow())
		{
			insertMetaDataIntoElements(r.getRowElementOrRowStructureOrRowTable(), rowMeta);
		}
	}

	/**
	 * Inserts meta data into row elements.
	 * 
	 * @param entry
	 *           Datatable.
	 * @param metaTable
	 *           Metatable.
	 */
	private void insertMetaDataIntoRowElements(final DataTable entry, final MetaTable metaTable)
	{
		entry.setLineType(metaTable.getLineType());
		insertMetaDataIntoRowElements(entry);
	}

	/**
	 * Inserts meta data into elements.
	 * 
	 * @param data
	 *           data list.
	 * @param metaData
	 *           meta data list.
	 */
	private void insertMetaDataIntoElements(final List<? extends SuperToString> data, final List<? extends SuperToString> metaData)
	{
		for (int i = 0; i < data.size(); i++)
		{
			final Object entry = data.get(i);
			if (entry instanceof DataElement)
			{
				final MetaElement me = (MetaElement) metaData.get(i);
				((DataElement) entry).setName(me.getName());
				((DataElement) entry).setFieldType(me.getFieldType());
			}
			else if (entry instanceof DataStructure)
			{
				final MetaStructure ms = (MetaStructure) metaData.get(i);
				((DataStructure) entry).setName(ms.getName());
				insertMetaDataIntoElements(((DataStructure) entry).getDataElementOrDataStructureOrDataTable(),
						ms.getElementMetaDataOrStructureMetaDataOrTableMetaData());
			}
			else if (entry instanceof DataTable)
			{
				insertMetaDataIntoRowElements((DataTable) entry, (MetaTable) metaData.get(i));
			}
		}
	}

	/**
	 * Populates structure record meta data.
	 * 
	 * @param s
	 *           Structure.
	 * @param rmd
	 *           Record meta data.
	 * @return Length of the inserted record.
	 */
	private int populateStructureRMD(final Structure s, final JCoRecordMetaData rmd)
	{
		int bo = 0;
		for (final Object o : s.getElementOrStructureOrTable())
		{
			if (o instanceof Element)
			{
				bo += addElementToRecordMetaData((Element) o, bo, rmd);
			}
			else if (o instanceof Structure)
			{
				bo += addStructureToRecordMetaData((Structure) o, bo, rmd);
			}
			else if (o instanceof Table)
			{
				bo += addTableToRecordMetaData((Table) o, null, bo, rmd);
				insertMetaDataIntoRowElements((Table) o);
			}
		}

		return bo;
	}

	/**
	 * Populates structure record meta data.
	 * 
	 * @param s
	 *           Meta structure.
	 * @param rmd
	 *           Record meta data.
	 * @return Length of the inserted record.
	 */
	private int populateStructureRMD(final MetaStructure s, final JCoRecordMetaData rmd)
	{
		int bo = 0;
		for (final Object o : s.getElementMetaDataOrStructureMetaDataOrTableMetaData())
		{
			if (o instanceof MetaElement)
			{
				bo += addElementToRecordMetaData((MetaElement) o, bo, rmd);
			}
			else if (o instanceof MetaStructure)
			{
				bo += addStructureToRecordMetaData((MetaStructure) o, bo, rmd);
			}
			else if (o instanceof MetaTable)
			{
				bo += addTableToRecordMetaData(null, (MetaTable) o, bo, rmd);
			}
		}

		return bo;
	}

	/**
	 * Populate row record meta data.
	 * 
	 * @param tableName
	 *           Table name.
	 * @param lineType
	 *           Line type.
	 * @return Record meta data.
	 */
	private JCoRecordMetaData populateRowRMD(final String tableName, final LineType lineType)
	{
		final JCoRecordMetaData rmd = JCo.createRecordMetaData(tableName);//lineType.getName());
		int bo = 0;
		for (final Object o : lineType.getElementMetaDataOrStructureMetaDataOrTableMetaData())
		{
			if (o instanceof MetaElement)
			{
				bo += addElementToRecordMetaData((MetaElement) o, bo, rmd);
			}
			else if (o instanceof MetaStructure)
			{
				bo += addStructureToRecordMetaData((MetaStructure) o, bo, rmd);
			}
			else if (o instanceof MetaTable)
			{
				bo += addTableToRecordMetaData(null, (MetaTable) o, bo, rmd);
			}
		}
		return rmd;
	}

	/**
	 * Add element to record meta data.
	 * 
	 * @param e
	 *           Element.
	 * @param bo
	 *           Length of the record to be inserted.
	 * @param rmd
	 *           Record meta data.
	 * @return Length of the inserted record.
	 */
	private int addElementToRecordMetaData(final Element e, final int bo, final JCoRecordMetaData rmd)
	{
		return addElementToRecordMetaData(getLength(e), e.getBCDDecimals(), e.getName(), e.getFieldType(), e.getDescription(), bo,
				rmd);
	}

	/**
	 * Add element to record meta data.
	 * 
	 * @param e
	 *           Element.
	 * @param bo
	 *           Length of the record to be inserted.
	 * @param rmd
	 *           Record meta data.
	 * @return Length of the inserted record.
	 */
	private int addElementToRecordMetaData(final MetaElement e, final int bo, final JCoRecordMetaData rmd)
	{
		return addElementToRecordMetaData(getLength(e), e.getBCDDecimals(), e.getName(), e.getFieldType(), e.getDescription(), bo,
				rmd);
	}

	/**
	 * Add element to record meta data.
	 * 
	 * @param bl
	 *           Data field length.
	 * @param elementDecimals
	 *           Decimals information.
	 * @param elementName
	 *           Element name.
	 * @param elementFieldType
	 *           Element type.
	 * @param elementDescription
	 *           Element description.
	 * @param bo
	 *           Length of the record to be inserted.
	 * @param rmd
	 *           Record meta data.
	 * @return Length of the inserted record.
	 */
	private int addElementToRecordMetaData(final int bl, final BigInteger elementDecimals, final String elementName,
			final FieldType elementFieldType, final String elementDescription, final int bo, final JCoRecordMetaData rmd)
	{
		final int decimals = elementDecimals == null ? 0 : elementDecimals.intValue();
		rmd.add(elementName, Utils100.fieldTypeToInt(elementFieldType), bl, bo, 2 * bl, 2 * bo, decimals, elementDescription, null,
				null);
		return bl;
	}

	/**
	 * Does the same as {@link #addStructureToRecordMetaData(Structure, int, JCoRecordMetaData)} but with a Structure as
	 * parameter.
	 * 
	 * @param s
	 *           the structure to add.
	 * @param bo
	 *           the current byte offset of the given JCoRecordMetaData.
	 * @param rmd
	 *           into this JCoRecordMetaData is the created JCoStructure added.
	 * @return Returns the byte length of the newly created JCoStructure.
	 */
	private int addStructureToRecordMetaData(final Structure s, final int bo, final JCoRecordMetaData rmd)
	{
		final JCoRecordMetaData subRmd = JCo.createRecordMetaData(s.getStructureName());
		final int length = populateStructureRMD(s, subRmd);
		return addStructureToRecordMetaData(s.getName(), s.getDescription(), bo, subRmd, length, rmd);
	}

	/**
	 * Creates a JCoRecordMetaData for the given MetaStructure and adds it to the given JCoRecordMetaData.
	 * 
	 * @param s
	 *           the structure to add.
	 * @param bo
	 *           the current byte offset of the given JCoRecordMetaData.
	 * @param rmd
	 *           into this JCoRecordMetaData is the created JCoStructure added.
	 * @return Returns the byte length of the newly created JCoStructure.
	 */
	private int addStructureToRecordMetaData(final MetaStructure s, final int bo, final JCoRecordMetaData rmd)
	{
		final JCoRecordMetaData subRmd = JCo.createRecordMetaData(s.getStructureName());
		final int length = populateStructureRMD(s, subRmd);
		return addStructureToRecordMetaData(s.getName(), s.getDescription(), bo, subRmd, length, rmd);
	}

	/**
	 * Adds a JCoStructure with the given parameters to the given JCoRecordMetaData rmd.
	 * 
	 * @param structureName
	 *           the name for the JCoStructure to add.
	 * @param structureDescription
	 *           the description for the JCoStructure to add.
	 * @param bo
	 *           the current byte offset of the given JCoRecordMetaData rmd.
	 * @param subRmd
	 *           the JCoRecordMetaData for the JCoStructure to add.
	 * @param subRmdLength
	 *           the byte length for the JCoStructure to add.
	 * @param rmd
	 *           into this JCoRecordMetaData is the created JCoStructure added.
	 * @return Returns the byte length of the newly created JCoStructure.
	 */
	private int addStructureToRecordMetaData(final String structureName, final String structureDescription, final int bo,
			final JCoRecordMetaData subRmd, final int subRmdLength, final JCoRecordMetaData rmd)
	{
		final int bl = subRmdLength;

		rmd.add(structureName, JCoMetaData.TYPE_STRUCTURE, bl, bo, 2 * bl, 2 * bo, 0, structureDescription, subRmd, null);
		return bl;
	}

	/**
	 * Creates a new JCoTable JCoRecordMetaData from one of the given tables and adds it to the given JCoRecordMetaData.
	 * 
	 * @param t
	 *           if given MetaTable is null, this Table is used.
	 * @param mt
	 *           if this is not null, this MetaTable is used.
	 * @param bo
	 *           the current byte offset for the given JCoRecordMetaData.
	 * @param rmd
	 *           into this JCoRecordMetaData is the created JCoTable added.
	 * @return Returns the byte length of the newly created JCoTable.
	 */
	private int addTableToRecordMetaData(final Table t, final MetaTable mt, final int bo, final JCoRecordMetaData rmd)
	{
		final boolean notMeta = mt == null;
		final String name = (notMeta ? t.getName() : mt.getName());
		final String tableName = (notMeta ? t.getTableName() : mt.getTableName());
		final LineType lineType = (notMeta ? t.getLineType() : mt.getLineType());
		final String description = (notMeta ? t.getDescription() : mt.getDescription());

		final JCoRecordMetaData subRmd = populateRowRMD(tableName, lineType);
		final int bl = subRmd.getRecordLength();
		rmd.add(name, JCoMetaData.TYPE_TABLE, bl, bo, 2 * bl, 2 * bo, 0, description, subRmd, null);
		return bl;
	}

	/**
	 * Does the same as {@link #fillRecord(List, JCoRecord)} but with a JCoParameterList as parameter.
	 * 
	 * @param list
	 *           the source object.
	 * @param parameterList
	 *           the target object.
	 */
	private void populateRecords(final List<? extends SuperToString> list, final JCoParameterList parameterList)
	{
		fillRecord(list, parameterList);
	}

	/**
	 * Takes every element from the list and inserts its value into the given JCoRecord.
	 * 
	 * @param list
	 *           the source object.
	 * @param record
	 *           the target object.
	 */
	private void fillRecord(final List<?> list, final JCoRecord record)
	{
		for (final Object listEntry : list)
		{
			if (listEntry instanceof Element)
			{
				setRecordValue((Element) listEntry, record);
			}
			else if (listEntry instanceof DataElement)
			{
				setRecordValue((DataElement) listEntry, record);
			}
			else if (listEntry instanceof Structure)
			{
				final Structure s = (Structure) listEntry;
				fillRecord(s.getElementOrStructureOrTable(), record.getStructure(s.getName()));
			}
			else if (listEntry instanceof Table)
			{
				final Table t = (Table) listEntry;
				final JCoTable table = record.getTable(t.getName());
				for (final Row r : t.getRow())
				{
					table.appendRow();
					fillRecord(r.getRowElementOrRowStructureOrRowTable(), table);
				}
			}
			else if (listEntry instanceof Row && record instanceof JCoTable)
			{
				final JCoTable table = (JCoTable) record;
				table.appendRow();
				fillRecord(((Row) listEntry).getRowElementOrRowStructureOrRowTable(), table);
			}
		}
	}

	/**
	 * Does the same as {@link #setRecordValue(Element, JCoRecord)} but with DataElement as parameter.
	 * 
	 * @param r
	 *           the source object.
	 * @param record
	 *           the target object.
	 */
	private void setRecordValue(final DataElement r, final JCoRecord record)
	{
		// convert to element and call method with parameter-type Element
		final Element e = new Element();
		e.setName(r.getName());
		e.setValue(r.getValue());
		e.setFieldType(r.getFieldType());

		setRecordValue(e, record);
	}

	/**
	 * Takes the value from the given Element and inserts it into the given JCoRecord.
	 * 
	 * @param e
	 *           the source object.
	 * @param record
	 *           the target object.
	 */
	private void setRecordValue(final Element e, final JCoRecord record)
	{
		final int type = Utils100.fieldTypeToInt(e.getFieldType());
		final String name = e.getName();
		final String value = e.getValue();
		switch (type)
		{
			case JCoMetaData.TYPE_XSTRING:
			case JCoMetaData.TYPE_BYTE:
				record.setValue(name, Codecs.Base64.decode(value));
				break;

			case JCoMetaData.TYPE_INT1:
			case JCoMetaData.TYPE_INT2:
			case JCoMetaData.TYPE_INT:
				record.setValue(name, Integer.parseInt(value));
				break;

			case JCoMetaData.TYPE_BCD:
				record.setValue(name, new BigDecimal(value));
				break;
			default:
				record.setValue(name, value);
				break;

			case JCoMetaData.TYPE_TABLE:
			case JCoMetaData.TYPE_STRUCTURE:
				throw new JCoRecRuntimeException("Table or Structure is here not allowed and can't happen!");
		}
	}

	@Override
	public JCoRecord getRecord(final String recordName) throws JCoRecException
	{
		// check records
		JCoRecord result = checkForRecord(recordName, repo.getRecords().getElementOrStructureOrTable());
		if (result != null)
		{
			return result;
		}

		// check parameterlists of every funtion
		String functionName = null;
		String parameterType = null;
		String splitRecordName = null;
		final String[] parts = recordName.split(RecorderUtils.RECORDNAME_SEPERATOR);
		if (parts.length == 3)
		{
			functionName = parts[0];
			parameterType = parts[1];
			splitRecordName = parts[2];

			for (final FunctionParameter fp : repo.getFunctions().getFunction())
			{
				if (!fp.getFunctionName().equals(functionName))
				{
					continue;
				}
				switch (parameterType)
				{
					case Utils100.PARAMETERLIST_IMPORT_NAME:
						// check ImportParameter
						result = checkForRecord(splitRecordName, fp.getImport().getElementOrStructureOrTable());
						if (result != null)
						{
							return result;
						}
						break;
					case Utils100.PARAMETERLIST_EXPORT_NAME:
						// check ExportParameter
						result = checkForRecord(splitRecordName, fp.getExport().getElementOrStructureOrTable());
						if (result != null)
						{
							return result;
						}
						break;
					case Utils100.PARAMETERLIST_CHANGING_NAME:
						// check ChangingParameter
						result = checkForRecord(splitRecordName, fp.getChanging().getElementOrStructureOrTable());
						if (result != null)
						{
							return result;
						}
						break;
					case Utils100.PARAMETERLIST_TABLES_NAME:
						// check TableParameter
						result = checkForRecord(splitRecordName, fp.getTables().getElementOrStructureOrTable());
						if (result != null)
						{
							return result;
						}
						break;
					default:
						throw new JCoRecException("Record of type " + parameterType + " not in Repository!");
				}
			}
		}
		else if (parts.length > 1)
		{
			throw new JCoRecRuntimeException(
					"Recordname "
							+ recordName
							+ " doesn't meet schema 'functionName#parameterType#recordName', where parameterType equals INPUT, OUTPUT, CHANGING or TABLES!");
		}

		throw new JCoRecRuntimeException("Record " + recordName + " not in Repository!");
	}

	/**
	 * Checks if the given list contains a record with the given name.
	 * 
	 * @param recordName
	 *           the name to search for.
	 * @param recordList
	 *           where to search.
	 * @return Returns the corresponding JCoRecord or null if there is no record with the given name.
	 */
	private JCoRecord checkForRecord(final String recordName, final List<? extends SuperToString> recordList)
	{
		for (final Object record : recordList)
		{
			if (record instanceof Structure && ((Structure) record).getName().equals(recordName))
			{
				return convertStructure((Structure) record);
			}
			else if (record instanceof Table && ((Table) record).getName().equals(recordName))
			{
				return convertTable((Table) record);
			}
			else if (record instanceof Element)
			{
				// DONE: does it make sense to request a single element from the repository? NO!
			}
		}
		return null;
	}

	/**
	 * Converts the given Structure to a JCoStructure.
	 * 
	 * @param s
	 *           the structure to convert to a JCoStructure.
	 * @return Returns the corresponding JCoStructure.
	 */
	private JCoStructure convertStructure(final Structure s)
	{
		final JCoRecordMetaData rmd = JCo.createRecordMetaData(s.getName());
		populateStructureRMD(s, rmd);
		rmd.lock();
		final JCoStructure structure = JCo.createStructure(rmd);
		fillRecord(s.getElementOrStructureOrTable(), structure);
		return structure;
	}

	/**
	 * Converts the given Table to a JCoTable.
	 * 
	 * @param t
	 *           the table to convert to a JCoTable.
	 * @return Returns the corresponding JCoTable.
	 */
	private JCoTable convertTable(final Table t)
	{
		final JCoRecordMetaData rowRmd = populateRowRMD(t.getName(), t.getLineType());
		rowRmd.lock();
		final JCoTable table = JCo.createTable(rowRmd);
		// add optional name and type to rows to ease filling of the JCoTable
		insertMetaDataIntoRowElements(t);
		fillRecord(t.getRow(), table);
		return table;
	}

	/**
	 * Gets the actual byte length of the given {@link Element}. If the elements length attribute is null, then the
	 * standard length for the type of element is returned.
	 * 
	 * @param e
	 *           the byte length for the {@link Element} is returned.
	 * @return Returns the corresponding byte length for the given {@link Element}.
	 */
	private int getLength(final Element e)
	{
		return (e.getMaxLength() == null) ? getStandardLength(e) : e.getMaxLength().intValue();
	}

	/**
	 * Gets the actual byte length of the given {@link MetaElement}. If the elements length attribute is null, then the
	 * standard length for the type of element is returned.
	 * 
	 * @param e
	 *           the byte length for the {@link MetaElement} is returned.
	 * @return Returns the corresponding byte length for the given {@link MetaElement}.
	 */
	private int getLength(final MetaElement e)
	{
		return (e.getMaxLength() == null) ? getStandardLength(e) : e.getMaxLength().intValue();
	}

	/**
	 * The same as {@link #getStandardLength(FieldType)} but with a {@link Element} as parameter.
	 * 
	 * @param e
	 *           the standard length for this {@link Element} is returned.
	 * @return Returns the corresponding standard length for the given {@link Element}.
	 */
	private int getStandardLength(final Element e)
	{
		return getStandardLength(e.getFieldType());
	}

	/**
	 * The same as {@link #getStandardLength(FieldType)} but with a {@link MetaElement} as parameter.
	 * 
	 * @param e
	 *           the standard length for this {@link MetaElement} is returned.
	 * @return Returns the corresponding standard length for the given {@link MetaElement}.
	 */
	private int getStandardLength(final MetaElement e)
	{
		return getStandardLength(e.getFieldType());
	}

	/**
	 * Gives the standard byte length of an JCo element type represented by the given {@link FieldType}.
	 * 
	 * @param type
	 *           the standard length of the type is returned.
	 * @return Returns the corresponding standard length for the given {@link FieldType}.
	 */
	private int getStandardLength(final FieldType type)
	{
		switch (type)
		{
			case BCD:
				return Utils100.STANDARD_LENGTH_BCD;
			case BYTE:
				return Integer.valueOf(1);
			case CHAR:
				return Utils100.STANDARD_LENGTH_SYMBOLCHAIN;
			case DATE:
				return Integer.valueOf(8);
			case DECF_16:
				return Integer.valueOf(8);
			case DECF_34:
				return Integer.valueOf(16);
			case FLOAT:
				return Integer.valueOf(8);
			case INT:
				return Integer.valueOf(4);
			case INT_1:
				return Integer.valueOf(1);
			case INT_2:
				return Integer.valueOf(2);
			case NUM:
				return Utils100.STANDARD_LENGTH_SYMBOLCHAIN;
			case STRING:
				return Utils100.STANDARD_LENGTH_SYMBOLCHAIN;// in ABAP it will be initialized with Integer.valueOf(0);//
			case TIME:
				return Integer.valueOf(6);
			case XSTRING:
				return Utils100.STANDARD_LENGTH_SYMBOLCHAIN;// in ABAP it will be initialized with Integer.valueOf(0);//
			default:
				throw new UnsupportedOperationException("The Fieldtype value " + type + " does not exist!");
		}
	}

}
