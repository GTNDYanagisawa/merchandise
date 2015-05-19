package de.hybris.platform.sap.sapmodel.daos;

import java.util.List;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.daos.UnitDao;

public interface SAPUnitDao extends UnitDao
{
	
	
	public List<UnitModel> findUnitBySAPUnitCode(final String unitType);
}
