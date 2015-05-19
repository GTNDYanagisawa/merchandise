package sap.hybris.integration.models.services;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;

public interface SAPUnitService extends UnitService
{



public UnitModel getUnitForSAPCode(final String code);

}
