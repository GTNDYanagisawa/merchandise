package de.hybris.platform.sap.sapmodel.daos;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.daos.impl.DefaultUnitDao;

public class SAPDefaultUnitDao extends DefaultUnitDao implements SAPUnitDao
{
	
	public SAPDefaultUnitDao()
	{
		super(UnitModel._TYPECODE);
	}
	public SAPDefaultUnitDao(final String typecode)
	{
		super(typecode);
	}
	
	
	public List<UnitModel> findUnitBySAPUnitCode(final String unitType)
	{
		validateParameterNotNull(unitType, "unitType must not be null!");
		final List<UnitModel> result = find(Collections.singletonMap(UnitModel.SAPCODE, (Object) unitType));
		return result;
	}
	
}
