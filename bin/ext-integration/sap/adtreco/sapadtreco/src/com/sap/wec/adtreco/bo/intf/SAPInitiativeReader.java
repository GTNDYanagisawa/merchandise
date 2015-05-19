package com.sap.wec.adtreco.bo.intf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.wec.adtreco.bo.impl.SAPInitiative;


public interface SAPInitiativeReader extends BusinessObject
{

	public List<SAPInitiative> getAllInitiatives() throws ODataException, URISyntaxException, IOException;

	public List<SAPInitiative> searchInitiatives(final String search) throws ODataException, URISyntaxException, IOException;

	public List<SAPInitiative> searchInitiativesForBP(final String businesPartner) throws ODataException, URISyntaxException,
			IOException;

	public SAPInitiative getInitiative(final String id);

	public SAPInitiative getSelectedInitiative(final String id);


}
