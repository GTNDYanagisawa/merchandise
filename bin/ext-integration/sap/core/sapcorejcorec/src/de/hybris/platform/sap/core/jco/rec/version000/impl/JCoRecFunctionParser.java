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
package de.hybris.platform.sap.core.jco.rec.version000.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;
import de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParserException;
import de.hybris.platform.sap.core.jco.rec.version000.Utils000;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.util.Codecs;


/**
 * This class provides functionality to parse a DOM document.
 */
class JCoRecFunctionParser
{
	private final String TYPE_EMPTY_STRUCTURE = "emptyStructure";
	private final String TYPE_EMPTY_TABLE = "emptyTable";
	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Transformer transformer;


	/**
	 * Parses functions from a JCoRecorder repository file.
	 * 
	 * @param doc
	 *           the parsed content will be added to this document.
	 * @param repo
	 *           the JCoRecorder repository that should be parsed.
	 * @throws JCoRecXMLParserException
	 * <br/>
	 *            if<br/>
	 *            <ul>
	 *            <li>{@code repo == null}</li>
	 *            <li>the xml-transformer can't be initialized</li>
	 *            <li>the parsing of the content raises an exception</li>
	 *            <li>the repository contains unexpected content</li>
	 *            </ul>
	 */
	public void parseRepository(final Document doc, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		if (repo.getMetaDataRepository() == null)
		{
			throw new JCoRecXMLParserException("No MetaData in JCoRecRepository " + repo + " available");
		}

		if (transformer == null)
		{
			try
			{
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			}
			catch (final TransformerConfigurationException e)
			{
				throw new JCoRecXMLParserException(e);
			}
		}
		// NodeList list =
		// doc.getElementsByTagName(JCoRecXMLConstants.TAGNAME_SINGLE_RFC);
		final Element root = doc.getDocumentElement();

		final NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node currNode = list.item(i);

			final String tagName = currNode.getNodeName();
			if (tagName.equalsIgnoreCase(Utils000.TAGNAME_FUNCTION_ROOT))
			{
				parseFunctions(currNode.getChildNodes(), repo);
			}
			else if (tagName.equalsIgnoreCase(Utils000.TAGNAME_RECORDS_ROOT))
			{
				parseRecords(currNode.getChildNodes(), repo);
			}
			else if (tagName.equalsIgnoreCase(Utils000.TAGNAME_METADATA_ROOT))
			{
				continue;
			}
			else
			{
				throw new JCoRecXMLParserException("Expected " + Utils000.TAGNAME_FUNCTION_ROOT + " or "
						+ Utils000.TAGNAME_RECORDS_ROOT + " but found " + tagName);
			}
		}

	}

	/**
	 * Parses all records from the {@code list} and adds them to the {@code repo}.
	 * 
	 * @param recordsList
	 *           the list that should be parsed.
	 * @param repo
	 *           the repo containing the records afterwards.
	 * @throws JCoRecXMLParserException
	 *            if a JCoRecXMLParserException is thrown while parsing a single record, or if there is data for a record
	 *            missing.
	 */
	private void parseRecords(final NodeList recordsList, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		for (int i = 0; i < recordsList.getLength(); i++)
		{
			final Node record = recordsList.item(i);
			if (record == null)
			{
				continue;
			}
			final String recordType = record.getNodeName();
			final String recordKey = record.getAttributes().getNamedItem("key").getNodeValue();
			String emptyStructOrTable = "default";
			final Node typeAttributeNode = record.getAttributes().getNamedItem("type");
			if (typeAttributeNode != null)
			{
				emptyStructOrTable = typeAttributeNode.getNodeValue();
			}
			JCoRecord newRecord = null;
			JCoRecordMetaData metaData = null;

			try
			{
				metaData = repo.getMetaDataRepository().getRecordMetaData(recordType);
				if (metaData == null)
				{
					throw new JCoRecRuntimeException("Meta data not found: " + recordType);
				}

				if (emptyStructOrTable.equalsIgnoreCase(TYPE_EMPTY_STRUCTURE))
				{
					newRecord = JCo.createStructure(metaData);
					repo.put(recordKey, newRecord);
					continue;
				}
				else if (emptyStructOrTable.equalsIgnoreCase(TYPE_EMPTY_TABLE))
				{
					newRecord = JCo.createTable(metaData);
					repo.put(recordKey, newRecord);
					continue;
				}
				else if (record.getFirstChild() != null && record.getFirstChild().getNodeName().equals(Utils000.TAGNAME_TABLE_ITEM))
				{
					// Table
					newRecord = JCo.createTable(metaData);
				}
				else
				{
					// Structure
					newRecord = JCo.createStructure(metaData);
				}
				parseJCoRecord(record, newRecord);
				if (newRecord instanceof JCoTable)
				{
					((JCoTable) newRecord).firstRow();
				}
				repo.put(recordKey, newRecord);
			}
			catch (final JCoException e)
			{
				throw new JCoRecXMLParserException("Meta data not found: " + recordType, e);
			}

		}
	}

	/**
	 * Parses all functions from the {@code list} and adds them to the {@code repo}.
	 * 
	 * @param list
	 *           the list that should be parsed.
	 * @param repo
	 *           the repo containing the functions afterwards.
	 * @throws JCoRecXMLParserException
	 *            if there is data for a function missing.
	 */
	private void parseFunctions(final NodeList list, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node rfc = list.item(i);
			String functionKey;
			String functionName;

			final Node functionNameNode = rfc.getAttributes().getNamedItem(Utils000.ATTNAME_FUNCTION_NAME);
			final Node functionKeyNode = rfc.getAttributes().getNamedItem(Utils000.ATTNAME_FUNCTION_KEY);

			if (functionKeyNode == null)
			{
				throw new JCoRecXMLParserException("Missing Attribute (" + Utils000.ATTNAME_FUNCTION_KEY + ") in "
						+ rfc.getNodeName());
			}
			if (functionNameNode == null)
			{
				throw new JCoRecXMLParserException("Missing Attribute (" + Utils000.ATTNAME_FUNCTION_NAME + ") in "
						+ rfc.getNodeName());
			}

			functionName = functionNameNode.getNodeValue();
			functionKey = functionKeyNode.getNodeValue();

			JCoFunction newFunc;
			try
			{
				final JCoFunctionTemplate functionTemplate = repo.getMetaDataRepository().getFunctionTemplate(functionName);
				if (functionTemplate == null)
				{
					throw new JCoRecXMLParserException("No Function Template available for " + functionName);
				}
				newFunc = functionTemplate.getFunction();
			}
			catch (final JCoException e)
			{
				throw new JCoRecXMLParserException("No Function Template available for " + functionName, e);
			}

			final NodeList rfcChildren = rfc.getChildNodes();
			for (int j = 0; j < rfcChildren.getLength(); j++)
			{
				final Node rfcChild = rfcChildren.item(j);

				if (rfcChild.getNodeName().equalsIgnoreCase(Utils000.TAGNAME_IMPORT_PARAM))
				{
					parseJCoRecord(rfcChild, newFunc.getImportParameterList());
				}
				else if (rfcChild.getNodeName().equalsIgnoreCase(Utils000.TAGNAME_EXPORT_PARAM))
				{
					parseJCoRecord(rfcChild, newFunc.getExportParameterList());
				}
				else if (rfcChild.getNodeName().equalsIgnoreCase(Utils000.TAGNAME_TABLE_PARAM))
				{

					parseJCoRecord(rfcChild, newFunc.getTableParameterList());
				}
				else if (rfcChild.getNodeName().equalsIgnoreCase(Utils000.TAGNAME_CHANGING_PARAM))
				{

					parseJCoRecord(rfcChild, newFunc.getChangingParameterList());
				}
				else if (rfcChild.getNodeName().equalsIgnoreCase(Utils000.TAGNAME_EXCEPTION_PARAM))
				{
					throw new JCoRecXMLParserException("Parameterlist \"exceptions\" is not supported");
				}
				else
				{
					throw new JCoRecXMLParserException("Unexpected Tag: " + rfcChild.getNodeName());
				}
			}
			final int counter = convertFunctionKeyToExecutionOrder(functionKey);
			//			repo.put
			//			repo.put(new JCoRecFunctionDecorator(newFunc, timestamp, counter, JCoRecMode.PLAYBACK));
			repo.put(newFunc, counter);//function);
		}

	}

	/**
	 * Extracts the execution order number from the function key.
	 * 
	 * @param functionKey
	 *           the key containing the execution order.
	 * @return Returns the number.
	 * @throws JCoRecXMLParserException
	 *            if functionKey is null or no appropriate number of {@link RecorderUtils#FUNCTIONKEY_SEPERATOR} is
	 *            contained.
	 */
	private int convertFunctionKeyToExecutionOrder(final String functionKey) throws JCoRecXMLParserException
	{
		if (functionKey == null)
		{
			throw new JCoRecXMLParserException("FunctionKey can't be null!");
		}
		final String[] parts = functionKey.split(RecorderUtils.FUNCTIONKEY_SEPERATOR);
		if (parts.length < 2)
		{
			throw new JCoRecXMLParserException("Illegal functionKey (" + functionKey
					+ ")found! FunctionKey must met following schema: functionName::executionOrder");
		}

		return Integer.valueOf(parts[1]);
	}

	/**
	 * Parses a Node to the given JCoRecord.
	 * 
	 * @param child
	 *           the Node to be parsed
	 * @param record
	 *           the JCoRecord to append the parsed values to
	 * @throws JCoRecXMLParserException
	 *            if a JCoRecXMLParserException is thrown while parsing the contents.
	 */
	private void parseJCoRecord(final Node child, final JCoRecord record) throws JCoRecXMLParserException
	{
		final NodeList childChildren = child.getChildNodes();
		for (int i = 0; i < childChildren.getLength(); i++)
		{
			final Node currChild = childChildren.item(i);

			if (currChild.getNodeType() != Node.ELEMENT_NODE)
			{
				throw new JCoRecXMLParserException("Expected an Element Node but found " + currChild.getNodeName());
			}
			if (!currChild.hasChildNodes())
			{
				continue;
			}

			final String currChildTagName = currChild.getNodeName();
			final boolean hasItemTag = currChildTagName.equals(Utils000.TAGNAME_TABLE_ITEM);
			final boolean isFirstChildTextNode = currChild.getFirstChild().getNodeType() == Node.TEXT_NODE;

			if (hasItemTag && isFirstChildTextNode)
			{
				// if first child (of current child) is a text node and its tag name is "item"
				final String set = currChild.getFirstChild().getNodeValue();
				// set = content of the text (child-) node
				((JCoTable) record).appendRow();
				// JCoPramaterList.toXML() has made an entry without name.
				// So the key for this value is the empty string.
				record.setValue("", set);
			}
			else if (isFirstChildTextNode)
			{
				// if first child (of current child) is a text node and its tag name is NOT "item"
				String set = currChild.getTextContent();
				// set = concatenation of values of current child and its child nodes
				final int type = record.getMetaData().getType(currChildTagName);
				switch (type)
				{
					case JCoMetaData.TYPE_BYTE:
						record.setValue(currChildTagName, Codecs.Base64.decode(set));
						break;

					case JCoMetaData.TYPE_INT1:
					case JCoMetaData.TYPE_INT2:
					case JCoMetaData.TYPE_INT:
						record.setValue(currChildTagName, Integer.parseInt(set));
						break;

					case JCoMetaData.TYPE_TABLE:
					case JCoMetaData.TYPE_STRUCTURE:
					case JCoMetaData.UNINITIALIZED:
						throw new JCoRecXMLParserException("Unexpected Failure: Type "
								+ record.getMetaData().getTypeAsString(currChildTagName) + "(" + currChildTagName + " ; "
								+ record.getMetaData().getName() + ") not supported in this context ... BUG!?");

					case JCoMetaData.TYPE_BCD:
						set = handleBCD(set);
					default:
						record.setValue(currChildTagName, set);
						break;
				}
			}
			else if (hasItemTag)
			{
				// if current child is NOT a text node and its tag is "item"
				// then it is a row of a table
				((JCoTable) record).appendRow();
				parseJCoRecord(currChild, record);
			}
			else if (currChild.getFirstChild().getNodeName().equals(Utils000.TAGNAME_TABLE_ITEM)
					&& currChild.getLastChild().getNodeName().equals(Utils000.TAGNAME_TABLE_ITEM))
			{
				// if current child is NOT a text node and each of its children has tag name "item"
				// then it is a table
				parseJCoRecord(currChild, record.getTable(currChildTagName));
			}
			else
			{
				// if current child is NOT a text node and none of its children has tag name "item"
				// then it is a structure
				parseJCoRecord(currChild, record.getStructure(currChildTagName));
			}
		}
	}

	/**
	 * Type BCD needs to have decimal places even if the value is integer.
	 * 
	 * @param set
	 *           The string to handle as BCD
	 * @return a String representing a BCD with decimal places
	 */
	private String handleBCD(final String set)
	{
		return set.contains(".") ? set : set + ".0";
	}
}
