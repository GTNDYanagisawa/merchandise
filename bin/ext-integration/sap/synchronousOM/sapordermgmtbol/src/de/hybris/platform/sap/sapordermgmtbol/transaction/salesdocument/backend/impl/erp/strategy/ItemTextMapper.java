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
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;

import com.sap.conn.jco.JCoTable;


/**
 * Handles item texts.
 */
public class ItemTextMapper extends TextMapper
{

	/**
	 * Logging instance
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(ItemTextMapper.class.getName());

	@Override
	public void init()
	{
		/* nothing to initialize */
	}

	/**
	 * Handle output of header and item text. The ERP data is converted into header text, header answer text and item
	 * texts
	 * 
	 * @param etItemTextComV
	 *           JCO item text table
	 * @param items
	 *           BO items
	 * @param objInstMap
	 *           Storage of object/parent object relation
	 */
	public void read(final JCoTable etItemTextComV, final ObjectInstances objInstMap, final ItemList items)
	{

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("handle texts for item id: " + configTextId);
		}

		if (!etItemTextComV.isEmpty())
		{
			etItemTextComV.firstRow();
			do
			{
				read1Row(etItemTextComV, objInstMap, items);
			}
			while (etItemTextComV.nextRow());
		}
	}

	/**
	 * Handles item texts. Transfers them from LO-API tables to BOL items
	 * 
	 * @param etItemTextComV
	 * @param objInstMap
	 * @param items
	 *           BOL item list
	 */
	protected void read1Row(final JCoTable etItemTextComV, final ObjectInstances objInstMap, final ItemList items)
	{

		final String textID = JCoHelper.getString(etItemTextComV, ConstantsR3Lrd.FIELD_ID);
		if (configTextId.equals(textID))
		{
			// Find item for text handle
			final String handle = JCoHelper.getString(etItemTextComV, ConstantsR3Lrd.FIELD_HANDLE);
			final String parentHandle = objInstMap.getParent(handle);
			final Item item = items.get(new TechKey(parentHandle));
			if (item != null)
			{
				final Text text = item.createText();
				text.setId(textID);
				text.setText(JCoHelper.getString(etItemTextComV, ConstantsR3Lrd.FIELD_TEXT_STRING));
				text.setHandle(handle);
				item.setText(text);

				if (sapLogger.isDebugEnabled())
				{
					traceReadTexts(text, "Item text found for handle " + parentHandle, sapLogger);
				}

			}
			else
			{
				if (sapLogger.isDebugEnabled())
				{
					final String msg = MessageFormat.format("Item text not found for handle {0}", new Object[]
					{ handle });
					sapLogger.debug(msg);
				}
			}
		} // else // ignore row
	}

	/**
	 * Writes item texts. Fills JCO tables from the BOL items before the LO-API update call
	 * 
	 * @param salesDoc
	 * @param itemsToBeChanged
	 * @param textV
	 *           Text table
	 * @param textX
	 *           Table of change indicators
	 * @param objInst
	 */
	public void write(final SalesDocument salesDoc, final Set<String> itemsToBeChanged, final JCoTable textV,
			final JCoTable textX, final JCoTable objInst)
	{

		if (salesDoc.getItemList() != null)
		{
			final Iterator<Item> itemIT = salesDoc.getItemList().iterator();
			while (itemIT.hasNext())
			{
				final Item item = itemIT.next();
				final String itemHandle = item.getHandle();

				// check whether we need to send text
				if ((itemsToBeChanged == null) || itemsToBeChanged.contains(itemHandle))
				{

					final Text text = item.getText();

					if (text == null)
					{
						continue;
					}

					boolean sendKeyFields = false;
					String handle = text.getHandle();

					/*
					 * Now check whether we need to send this item text If no text has been maintained for a new text, we
					 * won't send anything.
					 * 
					 * But note that this check only applies to a new text, if we already have a handle from ERP, we need to
					 * update the text anyhow!
					 */
					if ((handle == null) || handle.isEmpty())
					{
						final String content = text.getText();
						if (content == null || content.isEmpty())
						{
							continue;
						}
						sendKeyFields = true;
						if (sapLogger.isDebugEnabled())
						{
							sapLogger.debug("We need to create a new item handle and set additional fields");
						}
						handle = TechKey.generateKey().getIdAsString();
					}

					textV.appendRow();
					if (sapLogger.isDebugEnabled())
					{
						traceText("Item text will be sent to ERP", configTextId, configLangIso, item.getText(), handle, itemHandle,
								sapLogger);
					}
					JCoHelper.setValue(textV, handle, ConstantsR3Lrd.FIELD_HANDLE);
					if (sendKeyFields)
					{
						JCoHelper.setValue(textV, configTextId, ConstantsR3Lrd.FIELD_ID);
						JCoHelper.setValue(textV, configLangIso, ConstantsR3Lrd.FIELD_SPRAS_ISO);
					}
					JCoHelper.setValue(textV, item.getText().getText(), ConstantsR3Lrd.FIELD_TEXT_STRING);

					textX.appendRow();
					JCoHelper.setValue(textX, handle, ConstantsR3Lrd.FIELD_HANDLE);
					if (sendKeyFields)
					{
						JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_ID);
						JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_SPRAS_ISO);
					}
					JCoHelper.setValue(textX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_TEXT_STRING);

					addToObjInst(objInst, handle, itemHandle, OBJECT_ID_TEXT);

				}
			}
		}
	}

}
