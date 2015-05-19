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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Class able to group the flat list of items into a hierarchical structure. This version is used to maintain a special
 * view on the items of an document without changing the data of the items itself.
 * <p>
 * To use this class, create an instance giving the list of items you want to control with as a parameter of the
 * constructor. After that you may ask for the status of an item by using the appropiate method.
 * </p>
 * 
 * <pre>
 * ItemHierarchy itemHierarchy = new ItemHierarchy(items);
 * if (itemHierarchy.hasSubItems(item))
 * {
 * 	// do something useful
 * }
 * </pre>
 * <p>
 * <b>Note -</b> This implementation has two uses: 1. in case of product substitution, checks for the property itmUsage
 * for . If this property is <code>ATP</code> the item is considered as an subitem in all other cases it is not an
 * subitem. 2. in case of multi-level configurable materials, checks for matches of item.parentId and parent.itemguid,
 * ie. the current item is sub-item to an existing node if its parentId matches the node's item-guid The assignment of
 * the subitems to the main item is done using the order of the items in the item list. Therefore the order in the list
 * is vital for the functionality of this class.
 * </p>
 * 
 */
public class ItemHierarchy
{

	// NOTE: Some parts of the code are commented out, because they are not
	// used in the current JSPs. Therefor I could not extensively test
	// this code and removed the methods.
	private final Node rootNode = new Node(null, null);
	private final Map<TechKey, Node> nodeCache = new HashMap<TechKey, Node>();
	private GenericFactory genericFactory;

	/**
	 * Indicates next item
	 */
	protected static final int NEXT = 1;
	/**
	 * Indicates previous item
	 */
	protected static final int PREVIOUS = 0;

	/**
	 * Inner class for the creation of a tree list.
	 */
	private static class Node
	{
		private final List<Node> subNodes = new ArrayList<Node>();
		private final Map<TechKey, Node> subKeys = new HashMap<TechKey, Node>();
		private final Node parent;
		private final Item referredItem;
		private TechKey itemGuid;
		private int level = 0;
		private boolean rootConfigurable = false;
		private boolean parentConfigurable = false;

		public Node(final Node parent, final Item referredItem)
		{
			this.parent = parent;
			this.referredItem = referredItem;

			if (referredItem != null)
			{

				itemGuid = referredItem.getTechKey();
				level = parent.getLevel() + 1;

				if (referredItem.getParentId() == null || referredItem.getParentId().isInitial())
				{ // header
				  // level?
					rootConfigurable = referredItem.isConfigurable();
					// retrieve isConfig property from referred item
				}
				else
				{
					rootConfigurable = parent.isRootConfigurable();
					parentConfigurable = parent.referredItem.isConfigurable();
					// sub-item is marked configurable if header item is
					// configable
				}
			}
			else
			{ // root node
				level = 0;
				itemGuid = new TechKey(null);
			}
		}

		public boolean isRootConfigurable()
		{
			return rootConfigurable;
		}

		public boolean isParentConfigurable()
		{
			return parentConfigurable;
		}

		public int getLevel()
		{
			return level;
		}

		public void addNode(final Node node)
		{
			subNodes.add(node);

			if (!node.itemGuid.isInitial())
			{
				subKeys.put(node.itemGuid, node);
			}
		}

		public Node findSubNode(final TechKey techKey)
		{
			Node result = subKeys.get(techKey);

			if (result == null)
			{
				final int size = subNodes.size();
				for (int i = 0; i < size; i++)
				{
					result = (subNodes.get(i)).findSubNode(techKey);
					if (result != null)
					{
						break;
					}
				}
			}
			return result;
		}

		public boolean isParent(final Item item)
		{
			return (itemGuid.equals(item.getParentId()));
		}

		public Node getSubNode(final int i)
		{
			return subNodes.get(i);
		}

		public Node getParent()
		{
			return parent;
		}

		public int getNumSubNodes()
		{
			return subNodes.size();
		}

		public boolean hasSubNodes()
		{
			return !subNodes.isEmpty();
		}
	}

	/**
	 * Creates a new object using a list of items. The created object represents the hierarchy of the items.
	 * 
	 * @param itemList
	 *           the list of items used to construct the internal hierarchy
	 */
	public ItemHierarchy(final ItemList itemList)
	{
		final Iterator<Item> it = itemList.iterator();
		Node lastNode = rootNode;
		Node subNode = null;

		while (it.hasNext())
		{
			final Item item = it.next();
			if (item.getParentId() == null || item.getParentId().isInitial())
			{ // main
			  // position
				lastNode = new Node(rootNode, item);
				rootNode.addNode(lastNode);
			}
			else
			{ // sub position
				while (lastNode != null && !lastNode.isParent(item))
				{
					lastNode = lastNode.getParent();
				}
				if (lastNode != null)
				{
					subNode = new Node(lastNode, item);
					lastNode.addNode(subNode);
					lastNode = subNode;
				}
				else
				{
					// Maybe the sub positions are not defined subsequently
					// try to find the parent
					lastNode = getNode(item.getParentId());
					if (lastNode != null)
					{
						subNode = new Node(lastNode, item);
						lastNode.addNode(subNode);
						lastNode = subNode;
					}
					else
					{
						// tree inconsistent
						throw new ApplicationBaseRuntimeException("Item Hierarchy is inconsistent!");
					}
				}
			}
		}
	}

	/**
	 * Find the node for the given TechKey
	 * 
	 * @param itemKey
	 *           the key of the node to search for
	 * @return the node for the given TechKey or null if not found
	 */
	protected Node getNode(final TechKey itemKey)
	{
		// Try the cache
		Node found = nodeCache.get(itemKey);

		if (found == null)
		{
			found = rootNode.findSubNode(itemKey);
			nodeCache.put(itemKey, found);
		}

		return found;
	}

	/**
	 * Determines the hierarchy level of a given item.
	 * 
	 * @param item
	 *           The item to answer the question for
	 * @return <code>level</code> if the given item is in the hierarchy; otherwise <code>0</code> is returned.
	 */

	public int getLevel(final SimpleItem item)
	{
		Node found;
		final TechKey techkey = item.getTechKey();

		// Try the cache
		found = nodeCache.get(techkey);

		if (found == null)
		{
			found = rootNode.findSubNode(techkey);
			nodeCache.put(techkey, found);
		}

		if (found != null)
		{
			return found.getLevel();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Determines whether the porent node of a given item is conf'able.
	 * 
	 * @param item
	 *           The item to answer the question for
	 * @return <code>true</code> if the parent item of a given item is conf'able; otherwise <code>false</code> is
	 *         returned.
	 */

	public boolean isParentConfigurable(final Item item)
	{
		Node found;
		final TechKey techkey = item.getTechKey();

		// Try the cache
		found = nodeCache.get(techkey);

		if (found == null)
		{
			found = rootNode.findSubNode(techkey);
			nodeCache.put(techkey, found);
		}

		if (found != null)
		{
			return found.isParentConfigurable();
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the number of subItems or null if no subitems are present
	 * 
	 * @param item
	 *           The item to answer the question for
	 * @return number of subitems for the given item or 0 if no subitems are present.
	 */
	public int getNoOfSubItems(final Item item)
	{
		Node found;
		final TechKey techkey = item.getTechKey();

		// Try the cache
		found = nodeCache.get(techkey);

		if (found == null)
		{
			found = rootNode.findSubNode(techkey);
			nodeCache.put(techkey, found);
		}

		return found.getNumSubNodes();
	}

	/**
	 * Returns all subItems or null if no subitems are present
	 * 
	 * @param item
	 *           The item to answer the question for
	 * @return list of subitems for the given item or 0 if no subitems are present.
	 */
	public ItemList getAllLevelSubItems(final SimpleItem item)
	{

		final ItemList subItems = (ItemList) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_LIST);

		Node found;
		final TechKey techkey = item.getTechKey();

		// Try the cache
		found = nodeCache.get(techkey);

		if (found == null)
		{
			found = rootNode.findSubNode(techkey);
			nodeCache.put(techkey, found);
		}

		addSubItems(found, subItems);

		return subItems;
	}

	/**
	 * Add subitesm to given list, if there are any
	 * 
	 * @param node
	 *           the node to look for subitems
	 * @param subItems
	 *           the subuitem list to be changed
	 */
	protected void addSubItems(final Node node, final ItemList subItems)
	{
		if (node.hasSubNodes())
		{
			for (int i = 0; i < node.getNumSubNodes(); i++)
			{
				subItems.add(node.getSubNode(i).referredItem);
				addSubItems(node.getSubNode(i), subItems);
			}
		}
	}

}
