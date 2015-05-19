/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.order.dao;

import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collection;


public interface AlipayPaymentTransactionDao extends GenericDao<AlipayPaymentTransactionModel>
{
	AlipayPaymentTransactionModel findPaymentTransactionByTradeNumber(String tradeNumber);
}
