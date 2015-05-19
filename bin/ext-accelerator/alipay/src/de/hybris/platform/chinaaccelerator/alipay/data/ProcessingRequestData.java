package de.hybris.platform.chinaaccelerator.alipay.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.RequestServiceType;

public class ProcessingRequestData {
	
	/**
	 * Operational Attributes */
	private String requestUrl;
//	private ServiceType serviceType;
	private RequestServiceType requestServiceType;
	private boolean isToSupplyReturnUrl;
	
	private AlipayRequestData alipayRequestData;
	
// REDDRA-39 METHOD SEEMS NOT BEING USED	
//	public List<String> getProcessingAttributes(){ System.out.println("KAI:getProcessingAttributes");
//		List<String> array = new ArrayList<String>();
//		Field[] fields = this.getClass().getDeclaredFields(); // reflection gets fields
//		for (int i = 0; i < fields.length; i++) // loops over fields
//		{
//			fields[i].setAccessible(true); // gets Java fields, changes "private to public" modifier b) doesnt always work (JVM setting), a) hacky
//			final String key = fields[i].getName();
//			array.add(key);
//		}
//		return array;		
//	}
	
	/**
	 * @return the isToSupplyReturnUrl
	 */
	public boolean isToSupplyReturnUrl() {
		return isToSupplyReturnUrl;
	}
	/**
	 * @param isToSupplyReturnUrl the isToSupplyReturnUrl to set
	 */
	public void setToSupplyReturnUrl(boolean isToSupplyReturnUrl) {
		this.isToSupplyReturnUrl = isToSupplyReturnUrl;
	}

	/**
	 * @return the requestServiceType
	 */
	public RequestServiceType getRequestServiceType() {
		return requestServiceType;
	}

	/**
	 * @param requestServiceType the requestServiceType to set
	 */
	public void setRequestServiceType(RequestServiceType requestServiceType) {
		this.requestServiceType = requestServiceType;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}
	/**
	 * @param requestUrl the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * @return the alipayRequestData
	 */
	public AlipayRequestData getAlipayRequestData() {
		if(alipayRequestData == null){
			return new AlipayRequestData();
		}else{
			return alipayRequestData;
		}
	}

	/**
	 * @param alipayRequestData the alipayRequestData to set
	 */
	public void setAlipayRequestData(AlipayRequestData alipayRequestData) {
		this.alipayRequestData = alipayRequestData;
	}

}
