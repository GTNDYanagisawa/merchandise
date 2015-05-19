package de.hybris.platform.sap.sappricingbol.businessobject.impl;

import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

public class SapPricingPartnerFunctionImpl implements SapPricingPartnerFunction {
	
	private String language;
	private String currency;
	private String soldTo;
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getSoldTo() {
		return soldTo;
	}

	public void setSoldTo(String soldTo) {
		this.soldTo = soldTo;
	}
	
	public SapPricingPartnerFunctionImpl() {
		super();
	}
	
	public SapPricingPartnerFunctionImpl(String language, String currency,
			String soldTo) {
		super();
		this.language = language;
		this.currency = currency;
		this.soldTo = soldTo;
	}
	
	@Override
	public String toString() {
		return "DefaultSapPricingPartnerFunction [language=" + language
				+ ", currency=" + currency + ", soldTo=" + soldTo + "]";
	}
	
	
	
	
	

}
