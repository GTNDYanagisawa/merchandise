/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.data.converter;

import org.springframework.util.Assert;

import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.ProcessingRequestData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.RequestServiceType;
import de.hybris.platform.commerceservices.converter.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AlipayPaymentInfoModel;
// TODO check if ok import de.hybris.platform.core.model.order.payment.BankTransferPaymentInfoModel;
import de.hybris.platform.chinaaccelerator.alipay.enums.ServiceType;


public class AlipayRequestDataConverter extends AbstractPopulatingConverter<OrderModel, ProcessingRequestData>
{
	private AlipayConfiguration alipayConfiguration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
	 */
	@Override
	protected ProcessingRequestData createTarget()
	{
		return new ProcessingRequestData();
	}

	@Override
	public void populate(final OrderModel source, final ProcessingRequestData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		if (source.getPaymentInfo() != null)
		{
			if (source.getPaymentInfo() instanceof AlipayPaymentInfoModel)
			{
				final AlipayPaymentInfoModel paymentInfo = (AlipayPaymentInfoModel) source.getPaymentInfo();
				final ServiceType serviceType = paymentInfo.getServiceType();
				AlipayRequestData requestData = target.getAlipayRequestData();
				if(serviceType.equals(ServiceType.EXPRESSPAY)){
					target.setRequestServiceType(RequestServiceType.EXPRESSPAY);
				}else{
					target.setRequestServiceType(RequestServiceType.DIRECTPAY);
				}
				requestData.setOut_trade_no(source.getCode());
				requestData.setSubject(getAlipayConfiguration().getRequest_subject() + source.getCode());
				requestData.setTotal_fee(getAlipayConfiguration().getRequestPrice(source.getTotalPrice()));
				
// TODO: check if ok
//				if (serviceType.equals(ServiceType.BANKPAY))
//				{
//					requestData.setDefaultbank(((BankTransferPaymentInfoModel) paymentInfo).getBankType().getCode());
//					target.setRequestServiceType(RequestServiceType.BANKPAY);
//				}

				if(getAlipayConfiguration().getRequest_timeout()!=null){
					requestData.setIt_b_pay(getAlipayConfiguration().getRequest_timeout());
				}
				boolean setReturnUrl = paymentInfo.getIsToSupplyReturnUrl();
				if(getAlipayConfiguration().getIs_supply_returnurl()!=null){
					setReturnUrl = Boolean.parseBoolean(getAlipayConfiguration().getIs_supply_returnurl());
				}
				target.setAlipayRequestData(requestData);
				target.setToSupplyReturnUrl(setReturnUrl);
				//TODO set other attributes, and set other service types.
			}
		}
	}

	/**
	 * @return the alipayConfiguration
	 */
	public AlipayConfiguration getAlipayConfiguration() {
		return alipayConfiguration;
	}

	/**
	 * @param alipayConfiguration the alipayConfiguration to set
	 */
	public void setAlipayConfiguration(AlipayConfiguration alipayConfiguration) {
		this.alipayConfiguration = alipayConfiguration;
	}
	
}
