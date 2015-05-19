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
package com.sap.wec.adtreco.bo.impl;

import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.zkoss.zul.Messagebox;

import com.sap.wec.adtreco.be.intf.ADTInitiativesBE;
import com.sap.wec.adtreco.bo.intf.SAPInitiativeReader;


/**
 *
 */
@BackendType("CEI")
public class SAPInitiativeReaderImpl extends BusinessObjectBase implements SAPInitiativeReader
{
	private static final String DT_selectTerms = "Name,Description,InitiativeId,InitiativeIdExt,LifecycleStatus,TargetGroup/CustomerMemberCount";
	private static final String RT_selectTerms = "Name,Description,InitiativeId,InitiativeIdExt";
	private static final String INITIATIVES = "Initiatives";
	private static final String B2B_CUSTOMER = "B2BCustomer";
	private static final String B2C_CUSTOMER = "Customer";
	private static final String TARGETGROUPS = "TargetGroup";
	private static final String IN_PREPARATION = "1";
	private static final String RELEASED = "2";
	private static final String ACTIVE = "1";
	private static final String PLANNED = "2";
	private static final String EQ_UTF8 = " eq '";
	private static final String QUOT_UTF8 = "'";
	private static final String AND_UTF8 = " and ";
	private static final String OR_UTF8 = " or ";

	protected ADTInitiativesBE accessBE;
	protected String idOrigin;
	protected String filterCategory;

	public B2BCustomerService b2bCustomerService;
	public B2BUnitService b2bUnitService;

	public List<SAPInitiative> getAllInitiatives() throws ODataException, URISyntaxException, IOException
	{
		final String filterStatus = "LifecycleStatus/StatusCode" + EQ_UTF8 + "2" + QUOT_UTF8;
		final ODataFeed feed = accessBE.getInitiatives(DT_selectTerms, filterStatus, INITIATIVES, "TargetGroups");
		final List<ODataEntry> foundEntries = feed.getEntries();
		return extractInitiatives(foundEntries);
	}

	public List<SAPInitiative> searchInitiatives(final String search) throws ODataException, URISyntaxException, IOException
	{
		final String filterCategory = "Category/CategoryCode" + EQ_UTF8 + this.filterCategory + QUOT_UTF8;
		final String filterDescription = "Search/SearchTerm" + EQ_UTF8 + search + QUOT_UTF8;

		final String filterStatus = "(Search/TileFilterCategory" + EQ_UTF8 + ACTIVE + QUOT_UTF8 + OR_UTF8
				+ "Search/TileFilterCategory" + EQ_UTF8 + PLANNED + QUOT_UTF8 + ")";

		final String filterTerms = filterDescription + AND_UTF8 + filterCategory + AND_UTF8 + filterStatus;
		final ODataFeed feed = accessBE.getInitiatives(DT_selectTerms, filterTerms, INITIATIVES, TARGETGROUPS);

		List<SAPInitiative> initiatives = new ArrayList<SAPInitiative>();
		if (feed != null)
		{
			final List<ODataEntry> foundEntries = feed.getEntries();
			initiatives = extractInitiatives(foundEntries);
		}

		return initiatives;
	}

	public List<SAPInitiative> searchInitiativesForBP(final String businessPartner) throws ODataException, URISyntaxException,
			IOException
	{
		String customerType = B2C_CUSTOMER;
		String unitId = "";
		String filterBP = "";

		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		if (b2bCustomer != null)
		{
			unitId = b2bUnitService.getParent(b2bCustomer).getUid();
			if (unitId != null)
			{
				customerType = B2B_CUSTOMER;
			}
		}

		if (businessPartner != null && businessPartner.length() > 0)
		{
			if (customerType.equals(B2C_CUSTOMER))
			{
				filterBP = "Filter/InteractionContactIdOrigin" + EQ_UTF8 + this.idOrigin + QUOT_UTF8
						+ " and Filter/InteractionContactId" + EQ_UTF8 + businessPartner + QUOT_UTF8;
			}
			else if (customerType.equals(B2B_CUSTOMER))
			{
				filterBP = "Filter/CustomerId" + EQ_UTF8 + unitId + QUOT_UTF8;
			}
		}

		final String filterCategory = "Category/CategoryCode" + EQ_UTF8 + this.filterCategory + QUOT_UTF8;
		final String filterStatus = "Search/TileFilterCategory" + EQ_UTF8 + ACTIVE + QUOT_UTF8;
		final String filterTerms = filterCategory + AND_UTF8 + filterStatus + AND_UTF8 + filterBP;

		final ODataFeed feed = accessBE.getInitiatives(RT_selectTerms, filterTerms, INITIATIVES, null);
		if (feed == null)
		{
			return new ArrayList<SAPInitiative>();
		}
		else
		{
			final List<ODataEntry> foundEntries = feed.getEntries();
			return extractInitiatives(foundEntries);
		}
	}

	public B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	public B2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected String convertToInternalKey(final String id)
	{
		final Integer in = Integer.valueOf(id);
		final String intKey = String.format("%010d", in);
		System.out.println("id: " + id + " --> int id: " + intKey);
		return intKey;
	}

	public SAPInitiative getSelectedInitiative(final String id)
	{
		final String keyValue = "'" + convertToInternalKey(id) + "'";
		final ODataEntry entry = accessBE.getInitiative(DT_selectTerms, keyValue, INITIATIVES);
		if (entry != null)
		{
			return extractInitiative(entry);
		}
		else
		{
			return null;
		}
	}

	public SAPInitiative getInitiative(final String id)
	{
		final String keyValue = "'" + convertToInternalKey(id) + "'";
		final ODataEntry entry = accessBE.getInitiative(RT_selectTerms, keyValue, INITIATIVES);
		if (entry != null)
		{
			return extractInitiative(entry);
		}
		else
		{
			return null;
		}
	}

	/**
	 *
	 */
	protected List<SAPInitiative> extractInitiatives(final List<ODataEntry> foundEntities)
	{
		final List<SAPInitiative> initiatives = new ArrayList<SAPInitiative>();
		if (foundEntities != null)
		{
			final Iterator<ODataEntry> iter = foundEntities.iterator();

			while (iter.hasNext())
			{
				final ODataEntry entity = iter.next();
				final SAPInitiative initiative = extractInitiative(entity);
				initiatives.add(initiative);

				/*
				 * if (fields.equals(DT_selectTerms)) { if (checkInitiativeSchedule(initiative) == true) {
				 * initiatives.add(initiative); } } else { initiatives.add(initiative); }
				 */
			}
		}
		return initiatives;
	}

	protected boolean checkInitiativeSchedule(final SAPInitiative init)
	{
		final Date currentDate = new Date();

		if ((init.getStartDate().before(currentDate) && init.getEndDate().after(currentDate)) //Initiative is Active
				|| (init.getStartDate().after(currentDate) && init.getEndDate().after(currentDate))) //Initiative is Planned
		{
			return true;
		}
		return false;
	}

	/**
	 *
	 */
	protected SAPInitiative extractInitiative(final ODataEntry entity)
	{
		final SAPInitiative initiative = new SAPInitiative();
		final Map<String, Object> props = entity.getProperties();
		if (props != null)
		{
			initiative.setName(props.get("Name").toString());
			initiative.setDescription(props.get("Description").toString());
			initiative.setId(props.get("InitiativeIdExt").toString());
			final HashMap<String, String> status = (HashMap<String, String>) props.get("LifecycleStatus");
			if (status != null)
			{
				final String statusName = status.get("StatusDescription");
				initiative.setStatus(statusName);
			}

			final ODataEntry tg = (ODataEntry) props.get("TargetGroup");
			if (tg != null)
			{
				final Map<String, Object> tgProps = tg.getProperties();
				initiative.setMemberCount(tgProps.get("CustomerMemberCount").toString());
			}
		}
		return initiative;
	}

	public String getFilterCategory()
	{
		return filterCategory;
	}

	public void setFilterCategory(final String filterCategory)
	{
		this.filterCategory = filterCategory;
	}

	public String getIdOrigin()
	{
		return idOrigin;
	}

	public void setIdOrigin(final String idOrigin)
	{
		this.idOrigin = idOrigin;
	}

	public ADTInitiativesBE getAccessBE()
	{
		return accessBE;
	}

	public void setAccessBE(final ADTInitiativesBE accessBE)
	{
		this.accessBE = accessBE;
	}

}
