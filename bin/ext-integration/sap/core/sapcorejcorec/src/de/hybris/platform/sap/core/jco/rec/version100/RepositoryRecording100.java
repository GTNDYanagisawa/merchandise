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
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;
import de.hybris.platform.sap.core.jco.rec.RepositoryRecording;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.ChangingParameterList;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.DataElement;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Element;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.ExportParameterList;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FieldType;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FunctionList;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FunctionParameter;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.ImportParameterList;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.LineType;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaElement;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaStructure;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.MetaTable;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Records;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Repository;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Row;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Structure;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Table;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.TableParameterList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordField;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.util.Codecs;



/**
 * Implementation of {@link RepositoryRecording} for the version 1.0.0 of the {@link Repository}.
 */
public class RepositoryRecording100 implements RepositoryRecording
{
	private final Repository repository;

	private int executionCounter;

	private final File repoFile;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *           this file is the location the recorded
	 */
	public RepositoryRecording100(final File file)
	{
		repoFile = file;
		repository = new Repository();
		repository.setRepositoryVersion("1.0.0");
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
	public int putFunction(final JCoFunction function)
	{
		if (repository.getFunctions() == null)
		{
			repository.setFunctions(new FunctionList());
		}
		final int count = increaseCounter();
		addFunction(populateFunction(function, count));
		return count;
	}

	/**
	 * Populates function.
	 * 
	 * @param function
	 *           Function.
	 * @param count
	 *           Count.
	 * @return Function Parameter.
	 */
	private FunctionParameter populateFunction(final JCoFunction function, final int count)
	{
		final FunctionParameter functionStub = new FunctionParameter();
		functionStub.setFunctionName(function.getName());
		functionStub.setExecutionOrder(BigInteger.valueOf(count));

		functionStub.setImport((ImportParameterList) populateRecords(function.getImportParameterList(), new ImportParameterList()));
		functionStub.setExport((ExportParameterList) populateRecords(function.getExportParameterList(), new ExportParameterList()));
		functionStub.setChanging((ChangingParameterList) populateRecords(function.getChangingParameterList(),
				new ChangingParameterList()));
		functionStub.setTables((TableParameterList) populateRecords(function.getTableParameterList(), new TableParameterList()));

		return functionStub;
	}

	/**
	 * Populates records.
	 * 
	 * @param jcoList
	 *           JCO Parameter List.
	 * @param list
	 *           Records.
	 * @return Records.
	 */
	private Records populateRecords(final JCoParameterList jcoList, final Records list)
	{
		if (jcoList == null)
		{
			return null;
		}

		final JCoParameterFieldIterator iter = jcoList.getParameterFieldIterator();
		while (iter.hasNextField())
		{
			final JCoParameterField field = iter.nextParameterField();
			final SuperToString p = populateParameter(field);

			list.getElementOrStructureOrTable().add(p);
		}

		return list;
	}

	/**
	 * Populates parameters.
	 * 
	 * @param field
	 *           Field.
	 * @return DTO Object.
	 */
	private SuperToString populateParameter(final JCoField field)
	{
		SuperToString param;
		if (field.isStructure())
		{
			param = populateStructure(field);
		}
		else if (field.isTable())
		{
			param = populateTable(field);
		}
		else
		{
			if (inMeta)
			{
				param = populateMetaElement(field);
			}
			else if (inData)
			{
				param = populateDataElement(field);
			}
			else
			{
				param = populateElement(field);
			}
		}

		return param;
	}

	/**
	 * Populates meta parameters.
	 * 
	 * @param field
	 *           Field.
	 * @return DTO Object.
	 */
	private SuperToString populateMetaParameter(final JCoField field)
	{
		SuperToString param;
		if (field.isStructure())
		{
			param = populateMetaStructure(field);
		}
		else if (field.isTable())
		{
			param = populateMetaTable(field);
		}
		else
		{
			param = populateMetaElement(field);
		}
		return param;
	}

	/**
	 * Populates structure.
	 * 
	 * @param field
	 *           Field.
	 * @return DTO Object.
	 */
	private Structure populateStructure(final JCoField field)
	{
		final Structure s = new Structure();
		s.setName(field.getName());
		final String description = field.getDescription();
		if (description != null && !description.isEmpty())
		{
			s.setDescription(description);
		}
		JCoStructure structure = null;
		structure = field.getStructure();
		final JCoRecordFieldIterator iter = structure.getRecordFieldIterator();
		while (iter.hasNextField())
		{
			final JCoRecordField record = iter.nextRecordField();
			s.getElementOrStructureOrTable().add(populateParameter(record));
		}

		return s;
	}

	/**
	 * Populates Meta structure.
	 * 
	 * @param field
	 *           Field.
	 * @return Meta structure.
	 */
	private MetaStructure populateMetaStructure(final JCoField field)
	{
		final MetaStructure s = new MetaStructure();
		final JCoRecordMetaData rmd = field.getRecordMetaData();
		s.setName(field.getName());
		s.setStructureName(rmd.getName());

		populateEmptyStructure(field, s);

		return s;
	}

	/**
	 * Populates empty structure.
	 * 
	 * @param field
	 *           Field.
	 * @param s
	 *           Meta structure.
	 */
	private void populateEmptyStructure(final JCoField field, final MetaStructure s)
	{
		final JCoRecordMetaData rmd = field.getRecordMetaData();
		for (int i = 0; i < rmd.getFieldCount(); i++)
		{
			final MetaElement e = new MetaElement();
			e.setName(rmd.getName(i));
			if (rmd.getType(i) != 99 && rmd.getType(i) != 17)
			{
				e.setFieldType(Utils100.fieldTypeFromInt(rmd.getType(i)));
			}
			else if (rmd.getType(i) == 99)
			{
				//				e.setFieldType(FieldType.TABLE);
				System.out.println("!!! TABLE WITHOUT ROWS WITH TABLE !!!");
			}
			else if (rmd.getType(i) == 17)
			{
				//				e.setFieldType(FieldType.STRUCTURE);
				System.out.println("!!! TABLE WITHOUT ROWS WITH STRUCTURE !!!");
			}
			final String description = rmd.getDescription(i);
			if (description != null && !description.isEmpty())
			{
				e.setDescription(description);
			}
			final int decimals = rmd.getDecimals(i);
			if (decimals > 0)
			{
				e.setBCDDecimals(BigInteger.valueOf(decimals));
			}
			final int length = rmd.getLength(i);
			if (length > 0)
			{
				e.setMaxLength(BigInteger.valueOf(length));
			}
			s.getElementMetaDataOrStructureMetaDataOrTableMetaData().add(e);
		}

	}

	private boolean inMeta;
	private boolean inData;

	/**
	 * Populates table.
	 * 
	 * @param field
	 *           Field.
	 * @return Table.
	 */
	private Table populateTable(final JCoField field)
	{
		final Table t = new Table();
		t.setName(field.getName());

		JCoTable table = null;
		final LineType lt = new LineType();
		lt.setName(field.getRecordMetaData().getLineType());

		try
		{
			table = field.getTable();
		}
		catch (final IllegalStateException exc)
		{
			// there are no rows for this table
			populateEmptyTableLineType(field, lt);
			t.setLineType(lt);
			return t;
		}

		t.setTableName(table.getRecordMetaData().getName());
		JCoRecordFieldIterator iter = table.getRecordFieldIterator();
		//		inMeta = true;
		while (iter.hasNextField())
		{
			final JCoRecordField record = iter.nextRecordField();
			lt.getElementMetaDataOrStructureMetaDataOrTableMetaData().add(populateMetaParameter(record));
		}
		//		inMeta = false;
		t.setLineType(lt);

		inData = true;
		table.firstRow();
		for (int i = 0; i < table.getNumRows(); i++)
		{
			final Row row = new Row();
			iter = table.getRecordFieldIterator();
			while (iter.hasNextField())
			{
				final JCoRecordField record = iter.nextRecordField();
				row.getRowElementOrRowStructureOrRowTable().add(populateParameter(record));
			}
			t.getRow().add(row);
			table.nextRow();
		}
		inData = false;

		return t;
	}

	/**
	 * Populates empty table line type.
	 * 
	 * @param field
	 *           Field.
	 * @param lt
	 *           Line type.
	 */
	private void populateEmptyTableLineType(final JCoField field, final LineType lt)
	{
		final JCoRecordMetaData rmd = field.getRecordMetaData();
		for (int i = 0; i < rmd.getFieldCount(); i++)
		{
			final MetaElement e = new MetaElement();
			e.setName(rmd.getName(i));
			if (rmd.getType(i) != 99 && rmd.getType(i) != 17)
			{
				e.setFieldType(Utils100.fieldTypeFromInt(rmd.getType(i)));
			}
			else if (rmd.getType(i) == 99)
			{
				//				e.setFieldType(FieldType.TABLE);
				System.out.println("!!! TABLE WITHOUT ROWS WITH TABLE !!!");
			}
			else if (rmd.getType(i) == 17)
			{
				//				e.setFieldType(FieldType.STRUCTURE);
				System.out.println("!!! TABLE WITHOUT ROWS WITH STRUCTURE !!!");
			}
			final String description = rmd.getDescription(i);
			if (description != null && !description.isEmpty())
			{
				e.setDescription(description);
			}
			final int decimals = rmd.getDecimals(i);
			if (decimals > 0)
			{
				e.setBCDDecimals(BigInteger.valueOf(decimals));
			}
			final int length = rmd.getLength(i);
			if (length > 0)
			{
				e.setMaxLength(BigInteger.valueOf(length));
			}
			lt.getElementMetaDataOrStructureMetaDataOrTableMetaData().add(e);
		}
	}

	/**
	 * Populates meta table.
	 * 
	 * @param field
	 *           Field.
	 * @return Meta table.
	 */
	private MetaTable populateMetaTable(final JCoField field)
	{
		final MetaTable t = new MetaTable();
		t.setName(field.getName());

		final LineType lt = new LineType();
		lt.setName(field.getRecordMetaData().getLineType());

		populateEmptyTableLineType(field, lt);
		t.setLineType(lt);

		return t;
	}

	/**
	 * Populates data element.
	 * 
	 * @param field
	 *           Field.
	 * @return Data element.
	 */
	private DataElement populateDataElement(final JCoField field)
	{
		final DataElement e = new DataElement();

		if (field.getValue() != null)
		{
			e.setValue(parseValue(field));
		}

		return e;
	}

	/**
	 * Populates element.
	 * 
	 * @param field
	 *           Field.
	 * @return Element.
	 */
	private Element populateElement(final JCoField field)
	{
		final Element e = new Element();

		e.setName(field.getName());
		final FieldType ft = Utils100.fieldTypeFromInt(field.getType());
		e.setFieldType(ft);
		final String description = field.getDescription();
		if (description != null && !description.isEmpty())
		{
			e.setDescription(description);
		}
		final int decimals = field.getDecimals();
		if (decimals > 0)
		{
			e.setBCDDecimals(BigInteger.valueOf(decimals));
		}
		final int length = field.getLength();
		if (length > 0 && hasTypeDynamicLength(ft))
		{
			e.setMaxLength(BigInteger.valueOf(length));
		}
		if (field.getValue() != null)
		{
			e.setValue(parseValue(field));
		}

		return e;
	}

	/**
	 * Checks if field type has dynamic length.
	 * 
	 * @param ft
	 *           Field type.
	 * @return true if it has dynamic length.
	 */
	private boolean hasTypeDynamicLength(final FieldType ft)
	{
		switch (ft)
		{
			case BYTE:
			case CHAR:
			case NUM:
			case STRING:
			case XSTRING:
				return true;
				//			case DATE:
				//				return false;
				//			case DECF_16:
				//				return false;
				//			case DECF_34:
				//				return false;
				//			case FLOAT:
				//				return false;
				//			case INT:
				//				return false;
				//			case INT_1:
				//				return false;
				//			case INT_2:
				//				return false;
				//			case TIME:
				//				return false;
				//			case BCD:
				//				return false;
			default:
				return false;
		}
	}

	/**
	 * Populates meta element.
	 * 
	 * @param field
	 *           Field.
	 * @return Meta element.
	 */
	private MetaElement populateMetaElement(final JCoField field)
	{
		final MetaElement e = new MetaElement();

		e.setName(field.getName());
		e.setFieldType(Utils100.fieldTypeFromInt(field.getType()));
		final String description = field.getDescription();
		if (description != null && !description.isEmpty())
		{
			e.setDescription(description);
		}
		final int decimals = field.getDecimals();
		if (decimals > 0)
		{
			e.setBCDDecimals(BigInteger.valueOf(decimals));
		}
		final int length = field.getLength();
		if (length > 0)
		{
			e.setMaxLength(BigInteger.valueOf(length));
		}

		return e;
	}

	/**
	 * Parse value from JCoField.
	 * 
	 * @param field
	 *           Field.
	 * @return Value as string.
	 */
	private String parseValue(final JCoField field)
	{
		final FieldType fieldType = Utils100.fieldTypeFromInt(field.getType());
		switch (fieldType)
		{
			case DATE:
				// ISO-8601-Format %tY-%tm-%td
				return String.format("%tF", field.getDate());
			case TIME:
				return String.format("%tT", field.getTime());
			case BYTE:
			case XSTRING:
				return Codecs.Base64.encode((byte[]) field.getValue());
			default:
				return field.getValue().toString();
		}

	}

	/**
	 * Adds a function to the repository.
	 * 
	 * @param fp
	 *           Function parameter.
	 */
	private void addFunction(final FunctionParameter fp)
	{
		repository.getFunctions().getFunction().add(fp);
	}

	@Override
	public void writeRepositoryToFile() throws JCoRecException
	{
		final File backup = createBackupCopy(repoFile);
		FileWriter fw = null;
		try
		{
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = schemaFactory.newSchema(new File(RecorderUtils.getSchemaFilePath()));
			final JAXBContext context = JAXBContext.newInstance(Repository.class);

			final Marshaller marshaller = context.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//			marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, RecorderUtils.SCHEMA_FILE_PATH);

			fw = new FileWriter(repoFile);
			marshaller.marshal(repository, fw);
		}
		catch (final SAXException | JAXBException | IOException e)
		{
			if (backup != null && backup.exists())
			{
				restoreFileFromBackupCopy(backup, repoFile, e);
			}
			else if (repoFile != null && repoFile.exists())
			{
				//				destroyFileWriter(fw);
				repoFile.delete();
			}
			throw new JCoRecException("An error occured while writing the repository to disk!", e);
		}
		finally
		{
			if (fw != null)
			{
				destroyFileWriter(fw);
				fw = null;
			}
		}
	}

	/**
	 * Destroys file writer.
	 * 
	 * @param fw
	 *           File writer.
	 */
	private void destroyFileWriter(final FileWriter fw)
	{
		// release reference to file
		if (fw != null)
		{
			try
			{
				fw.flush();
				fw.close();
			}
			catch (final IOException e)
			{
				// if closing fails, writer is probably already closed 
				// so nothing left to be done
				//				throw new JCoRecRuntimeException("I/O error occured during closing of the writer!", e);
			}
		}
	}

	/**
	 * Creates a backup copy.
	 * 
	 * @param file
	 *           File.
	 * @return File for backup copy.
	 * @throws JCoRecException
	 *            JCoRecException
	 */
	private File createBackupCopy(final File file) throws JCoRecException
	{
		if (file == null)
		{
			return null;
		}
		File copy = null;
		if (file.exists())
		{
			copy = new File(file.getAbsolutePath() + ".bak");
			try
			{
				Files.copy(file, copy);
			}
			catch (final IOException e)
			{
				throw new JCoRecException("An error occured during back up of old file", e);
			}
		}
		return copy;
	}

	/**
	 * Restores file from backup copy.
	 * 
	 * @param copy
	 *           File copy.
	 * @param file
	 *           File.
	 * @param e
	 *           Exception.
	 */
	private void restoreFileFromBackupCopy(final File copy, final File file, final Exception e)
	{
		try
		{
			Files.copy(copy, file);
			copy.delete();
		}
		catch (final IOException e1)
		{
			//should not happen
			e.addSuppressed(e1);
		}
	}
}
