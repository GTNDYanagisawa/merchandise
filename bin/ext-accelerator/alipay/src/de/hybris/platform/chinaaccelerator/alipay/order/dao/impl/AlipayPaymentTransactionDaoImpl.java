/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.order.dao.impl;

import de.hybris.platform.chinaaccelerator.alipay.order.dao.AlipayPaymentTransactionDao;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



public class AlipayPaymentTransactionDaoImpl extends DefaultGenericDao<AlipayPaymentTransactionModel> implements
		AlipayPaymentTransactionDao
{
	Logger LOG = Logger.getLogger(AlipayPaymentTransactionDaoImpl.class);
	
	/**
	 * @param typecode
	 */
	public AlipayPaymentTransactionDaoImpl()
	{
		super(AlipayPaymentTransactionModel._TYPECODE);
	}

	@Override
	public AlipayPaymentTransactionModel findPaymentTransactionByTradeNumber(final String tradeNumber)
	{
		AlipayPaymentTransactionModel trans = null;
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(AlipayPaymentTransactionModel.TRADEALIPAYNO, tradeNumber);
		final List<AlipayPaymentTransactionModel> transactions = find(params);
		if(transactions == null){
			LOG.error("There is no AlipayPaymentTransaction with alipay_trade_no: "+ tradeNumber);
		}else if(transactions.size()>1){
			LOG.error("There can only be one AlipayPaymentTransaction for one order, and only one AlipayPaymentTransaction with trade number: "+ tradeNumber);
		}else{
			trans = transactions.get(0);
		}
		return trans;
	}

}
