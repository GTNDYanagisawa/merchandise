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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.HeaderBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase;

import java.util.List;


/**
 * Base Interface for all sales documents, e.g. Basket, Order etc.<br>
 * The document consists in principle of a Header and a ItemList, which again contains Items. The type of all 3 objects
 * can be passed to this class via generics to enable type safe access.<br>
 * <ul>
 * <li>The Header has to extend
 * {@link de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.HeaderBase}</li>
 * <li>The ItemList has to extend
 * {@link de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase}</li>
 * <li>The Item has to extend {@link de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase}
 * objects</li>
 * </ul>
 * 
 * @param <L>
 *           type of the item List
 * @param <I>
 *           type of the items of the item list
 * @param <H>
 *           type of the header
 */
public interface SalesDocumentBase<L extends ItemListBase<I>, I extends ItemBase, H extends HeaderBase> extends
		SimpleDocument<L, I, H>
{

	/**
	 * Constant defining that the number of items of a document is unknown. Thus it must be determined by the size if the
	 * itemList.
	 */
	public static final int NO_OF_ITEMS_UNKNOWN = 0;

	/**
	 * Return the list of all available shipTos of this document. The shipTos contain the address.
	 * 
	 * @return list of shipTos.
	 */
	public List<ShipTo> getAlternativeShipTos();

	/**
	 * Return the list of all available shipTos of this document. The billTos contain the address.
	 * 
	 * @return list of billTos.
	 */
	public List<BillTo> getAlternativeBillTos();

	/**
	 * Returns true if only the header should be changed
	 * 
	 * @return boolean true if only the header should be changed, false else
	 */
	public boolean isChangeHeaderOnly();

	/**
	 * Determines if manual Product-, Campaign-, ... Determination is necessary for at least one top level item.
	 * 
	 * @return boolean true if there is at least on item that needs manual determination for products, camapigns, etc.
	 */
	public boolean isDeterminationRequired();

	/**
	 * Sets the isDeterminationRequired flag
	 * 
	 * @param isDeterminationRequired
	 *           true if there are items that need manual determination either for camapigns, substitution products or
	 *           something similar
	 */
	public void setDeterminationRequired(boolean isDeterminationRequired);

	/**
	 * Returns the information, if document is persistent in the back end
	 * 
	 * @return true, if document persists on DB in the back end
	 */
	public boolean isPersistentInBackend();

	/**
	 * Set flag, if document is persistent in the back end
	 * 
	 * @param isPersistent
	 *           if <code>true</code> document is considered persistent
	 */
	public void setPersistentInBackend(boolean isPersistent);

}