package de.hybris.platform.chinaaccelerator.alipay.order.dao;

import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;

public interface AlipayPaymentTransactionEntryDao {
	public AlipayPaymentTransactionEntryModel findPaymentTransactionEntryByRequestId(String batch_no);
	public AlipayPaymentTransactionEntryModel findPaymentTransactionEntryByNotifyId(final String notify_id);
}
