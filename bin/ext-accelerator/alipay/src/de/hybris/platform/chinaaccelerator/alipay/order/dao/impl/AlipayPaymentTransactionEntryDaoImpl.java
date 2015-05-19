package de.hybris.platform.chinaaccelerator.alipay.order.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.chinaaccelerator.alipay.order.dao.AlipayPaymentTransactionEntryDao;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class AlipayPaymentTransactionEntryDaoImpl extends DefaultGenericDao<AlipayPaymentTransactionEntryModel> implements
AlipayPaymentTransactionEntryDao
{
Logger LOG = Logger.getLogger(AlipayPaymentTransactionDaoImpl.class);
	
	/**
	 * @param typecode
	 */
	public AlipayPaymentTransactionEntryDaoImpl()
	{
		super(AlipayPaymentTransactionEntryModel._TYPECODE);
	}

	@Override
	public AlipayPaymentTransactionEntryModel findPaymentTransactionEntryByRequestId(final String requestId) {
		AlipayPaymentTransactionEntryModel entry = null;
		if(requestId!=null){
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put(AlipayPaymentTransactionEntryModel.REQUESTID, requestId.trim());
			final List<AlipayPaymentTransactionEntryModel> entries = find(params);
			if(entries == null || entries.isEmpty()){
				LOG.error("There is no AlipayPaymentTransactionEntry with requestId: "+ requestId);
			}else if(entries.size()>1){
				LOG.error("There can only be one AlipayPaymentTransactionEntry for one batch number: "+ requestId);
			}else{
				entry = entries.get(0);
			}
		}else{
			LOG.error("findPaymentTransactionEntryByRequestId requestId cannot be null." );
		}
		return entry;
	}
	
	@Override
	public AlipayPaymentTransactionEntryModel findPaymentTransactionEntryByNotifyId(final String notify_id){
		validateParameterNotNull(notify_id, "notify_id cannot be null");
		AlipayPaymentTransactionEntryModel entry = null;
		if(notify_id!=null){
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put(AlipayPaymentTransactionEntryModel.NOTIFYID, notify_id);
			final List<AlipayPaymentTransactionEntryModel> entries = find(params);
			if(entries == null || entries.isEmpty()){
				if(LOG.isDebugEnabled()){
					LOG.debug("There is no AlipayPaymentTransactionEntry with notify_id: "+ notify_id);
				}
				return null;
			}else if(entries.size()>1){
				LOG.error("There can only be one AlipayPaymentTransactionEntry for one notify id: "+ notify_id);
			}
			entry = entries.get(0);
		}
		return entry;
	}

}
