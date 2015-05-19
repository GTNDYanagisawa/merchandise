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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;


public class DefaultB2BCommerceUnitService extends DefaultCompanyB2BCommerceService implements B2BCommerceUnitService
{
	@Override
	public Collection<? extends B2BUnitModel> getOrganization()
	{
		final B2BUnitModel rootUnit = this.getRootUnit();
		return getB2BUnitService().getBranch(rootUnit);
	}

	@Override
	public Collection<? extends B2BUnitModel> getBranch()
	{
		return getB2BUnitService().getBranch(getParentUnit());
	}

	@Override
	public <T extends B2BUnitModel> T getRootUnit()
	{
		return (T) getB2BUnitService().getRootUnit(getB2BUnitService().getParent(getCurrentUser()));
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnit()
	{
		return (T) getB2BUnitService().getParent(getCurrentUser());
	}

	@Override
	public Collection<? extends B2BUnitModel> getAllUnitsOfOrganization()
	{
		return getB2BUnitService().getAllUnitsOfOrganization(getCurrentUser());
	}

	@Override
	public void setParentUnit(final B2BUnitModel unitModel, final B2BUnitModel parentUnit)
	{
		getB2BUnitService().addMember(parentUnit, unitModel);
	}

	@Override
	public Collection<? extends B2BUnitModel> getAllowedParentUnits(final B2BUnitModel unit)
	{
		final B2BUnitModel sessionUnitParent = getParentUnit();

		final Set<B2BUnitModel> branch = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2BUnitService().getBranch(sessionUnitParent);
			}
		});

		final Set<B2BUnitModel> allowedUnits = new HashSet<B2BUnitModel>(CollectionUtils.select(branch, new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final PrincipalGroupModel principalGroup = (PrincipalGroupModel) object;
				return getB2BGroupCycleValidator().validateGroups(unit, principalGroup);

			}
		}));
		final B2BUnitModel parentUnit = getParentUnit(unit);
		if (parentUnit != null)
		{
			allowedUnits.add(parentUnit);
		}
		return allowedUnits;
	}

	@Override
	public void updateBranchInSession()
	{
		getB2BUnitService().updateBranchInSession(this.getSessionService().getCurrentSession(), this.getCurrentUser());
	}

	@Override
	public B2BCustomerModel addApproverToUnit(final String unitId, final String approverId)
	{
		final B2BUnitModel unit = this.getUnitForUid(unitId);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(unit.getApprovers());
		final B2BCustomerModel approver = getCustomerForUid(approverId);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(approver.getGroups());
		groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));
		approver.setGroups(groups);
		approvers.add(approver);
		unit.setApprovers(approvers);
		this.getModelService().saveAll(approver, unit);
		return approver;
	}


	@Override
	public B2BCustomerModel removeApproverFromUnit(final String unitUid, final String approverUid)
	{
		final B2BUnitModel unit = this.getUnitForUid(unitUid);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(unit.getApprovers());
		final B2BCustomerModel approver = getCustomerForUid(approverUid);
		approvers.remove(approver);
		unit.setApprovers(approvers);
		this.getModelService().saveAll(approver, unit);
		return approver;
	}

	@Override
	public void disableUnit(final String uid)
	{
		final B2BUnitModel unit = getUnitForUid(uid);
		validateParameterNotNullStandardMessage("B2BUnit", uid);
		getB2BUnitService().disableBranch(unit);
	}

	@Override
	public void enableUnit(final String unit)
	{
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				final B2BUnitModel unitModel = getUnitForUid(unit);
				validateParameterNotNullStandardMessage("B2BUnit", unit);
				unitModel.setActive(Boolean.TRUE);
				getModelService().save(unitModel);
			}
		});
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedUsersForUnit(final PageableData pageableData, final String unit)
	{
		return getPagedB2BCustomerDao().findPagedCustomersForUnit("byName", pageableData, unit);
	}

	@Override
	public SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(final PageableData pageableData,
			final String unitUid, final String... usergroupUid)
	{
		return getPagedB2BCustomerDao().findPagedApproversForUnitByGroupMembership("byName", pageableData, unitUid, usergroupUid);
	}

	@Override
	public void saveAddressEntry(final B2BUnitModel unitModel, final AddressModel addressModel)
	{
		final Collection<AddressModel> addresses = new ArrayList<AddressModel>(unitModel.getAddresses());
		addressModel.setOwner(unitModel);
		addresses.add(addressModel);
		unitModel.setAddresses(addresses);
		getModelService().save(unitModel);
	}

	@Override
	public void removeAddressEntry(final String unitUid, final String addressId)
	{
		final B2BUnitModel unit = this.getUnitForUid(unitUid);
		validateParameterNotNullStandardMessage("B2BUnit", unit);
		final Collection<AddressModel> addresses = new ArrayList<AddressModel>(unit.getAddresses());
		for (final AddressModel addressModel : addresses)
		{
			if (addressModel.getPk().getLongValueAsString().equals(addressId))
			{
				addresses.remove(addressModel);
				unit.setAddresses(addresses);
				getModelService().remove(addressModel);
				break;
			}
		}
	}

	@Override
	public AddressModel getAddressForCode(final B2BUnitModel unit, final String id)
	{
		for (final AddressModel addressModel : unit.getAddresses())
		{
			if (addressModel.getPk().getLongValueAsString().equals(id))
			{
				return addressModel;
			}
		}
		return null;
	}

	@Override
	public void editAddressEntry(final B2BUnitModel unitModel, final AddressModel addressModel)
	{
		getModelService().save(addressModel);
	}
}
