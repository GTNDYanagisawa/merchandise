/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;

import java.io.Serializable;
import java.util.List;


/**
 * BO representation of an address.
 *
 */
public interface Address extends BusinessObject, Cloneable, Comparable<Address>, Serializable
{

	/**
	 * Address type corresponding to an organization
	 */
	public final static String TYPE_ORGANISATION = "1";

	/**
	 * Address type corresponding to a business partner
	 */
	public final static String TYPE_PERSON = "2";

	/**
	 * All (delta) attributes indicating that bean attributes have been changed are set to true
	 */
	public void setAllXFields();

	/**
	 * Returns the attribute value for a given bean attribute
	 *
	 * @param field
	 *           Name of the field that should be returned
	 * @return attribute value
	 */
	public String get(String field);

	/**
	 * Set the property id
	 *
	 * @param id
	 *           the id
	 */
	public void setId(String id);

	/**
	 * Returns the property id
	 *
	 * @return id
	 */
	public String getId();

	/**
	 * Set the property type
	 *
	 * @param type
	 *           the type
	 */
	public void setType(String type);

	/**
	 * Returns the property type
	 *
	 * @return type
	 */
	public String getType();

	/**
	 * Set the property origin
	 *
	 * @param origin
	 *           the origin
	 */
	public void setOrigin(String origin);

	/**
	 * Returns the property origin
	 *
	 * @return origin
	 */
	public String getOrigin();

	/**
	 * Set the property personNumber
	 *
	 * @param personNumber
	 *           the person number (BAS)
	 */
	public void setPersonNumber(String personNumber);

	/**
	 * Returns the property personNumber
	 *
	 * @return personNumber
	 */
	public String getPersonNumber();

	/**
	 * @param titleKey
	 *           short ID of title
	 */
	public void setTitleKey(String titleKey);

	/**
	 * @param title
	 *           title description
	 */
	public void setTitle(String title);

	/**
	 * @param titleAca1Key
	 *           short ID of academic title
	 */
	public void setTitleAca1Key(String titleAca1Key);

	/**
	 * @param titleAca1
	 *           academic title description
	 */
	public void setTitleAca1(String titleAca1);

	/**
	 * @param firstName
	 *           the fist name
	 */
	public void setFirstName(String firstName);

	/**
	 * @param lastName
	 *           the last name
	 */
	public void setLastName(String lastName);

	/**
	 * @param birthName
	 *           the birth name
	 */
	public void setBirthName(String birthName);

	/**
	 * @param secondName
	 *           the second name
	 */
	public void setSecondName(String secondName);

	/**
	 * @param middleName
	 *           the middle name
	 */
	public void setMiddleName(String middleName);

	/**
	 * Setter for bean attribute
	 *
	 * @param nickName
	 *           the nickname
	 */
	public void setNickName(String nickName);

	/**
	 * Setter for bean attribute
	 *
	 * @param initials
	 *           the initials
	 */
	public void setInitials(String initials);

	/**
	 * Setter for bean attribute
	 *
	 * @param name1
	 *           the name 1
	 */
	public void setName1(String name1);

	/**
	 * Setter for bean attribute
	 *
	 * @param name2
	 *           the name 2
	 */
	public void setName2(String name2);

	/**
	 * Setter for bean attribute
	 *
	 * @param name3
	 *           the name 3
	 */
	public void setName3(String name3);

	/**
	 * Setter for bean attribute
	 *
	 * @param name4
	 *           the name 4
	 */
	public void setName4(String name4);

	/**
	 * Setter for bean attribute
	 *
	 * @param coName
	 *           company name
	 */
	public void setCoName(String coName);

	/**
	 * Setter for bean attribute
	 *
	 * @param city
	 *           the city
	 */
	public void setCity(String city);

	/**
	 * Setter for bean attribute
	 *
	 * @param district
	 *           geographic entity for tax jurisdiction code determination
	 */
	public void setDistrict(String district);

	/**
	 * Setter for bean attribute
	 *
	 * @param postlCod1
	 *           the postal code 1
	 */
	public void setPostlCod1(String postlCod1);

	/**
	 * Setter for bean attribute
	 *
	 * @param postlCod2
	 *           the postal code 2
	 */
	public void setPostlCod2(String postlCod2);

	/**
	 * Setter for bean attribute
	 *
	 * @param postlCod3
	 *           the postal code 3
	 */
	public void setPostlCod3(String postlCod3);

	/**
	 * Setter for bean attribute
	 *
	 * @param pcode1Ext
	 *           the postal code 1 ext
	 */
	public void setPcode1Ext(String pcode1Ext);

	/**
	 * Setter for bean attribute
	 *
	 * @param pcode2Ext
	 *           the postal code 2 ext
	 */
	public void setPcode2Ext(String pcode2Ext);

	/**
	 * Setter for bean attribute
	 *
	 * @param pcode3Ext
	 *           the postal code 1 ext
	 */
	public void setPcode3Ext(String pcode3Ext);

	/**
	 * Setter for bean attribute
	 *
	 * @param poBox
	 *           the post box
	 */
	public void setPoBox(String poBox);

	/**
	 * Setter for bean attribute
	 *
	 * @param poWoNo
	 *           Flag: PO Box without number
	 */
	public void setPoWoNo(String poWoNo);

	/**
	 * Setter for bean attribute
	 *
	 * @param poBoxCit
	 *           the city of the post box
	 */
	public void setPoBoxCit(String poBoxCit);

	/**
	 * Setter for bean attribute
	 *
	 * @param poBoxReg
	 *           the post box region
	 */
	public void setPoBoxReg(String poBoxReg);

	/**
	 * Setter for bean attribute
	 *
	 * @param poBoxCtry
	 *           the post box country
	 */
	public void setPoBoxCtry(String poBoxCtry);

	/**
	 * Setter for bean attribute
	 *
	 * @param poCtryISO
	 *           the country iso code
	 */
	public void setPoCtryISO(String poCtryISO);

	/**
	 * Setter for bean attribute
	 *
	 * @param street
	 *           The street
	 */
	public void setStreet(String street);

	/**
	 * Setter for bean attribute
	 *
	 * @param strSuppl1
	 *           The street supplement 1
	 */
	public void setStrSuppl1(String strSuppl1);

	/**
	 * Setter for bean attribute
	 *
	 * @param strSuppl2
	 *           The street supplement 2
	 */
	public void setStrSuppl2(String strSuppl2);

	/**
	 * Setter for bean attribute
	 *
	 * @param strSuppl3
	 *           The street supplement 3
	 */
	public void setStrSuppl3(String strSuppl3);

	/**
	 * Setter for bean attribute
	 *
	 * @param location
	 *           The location
	 */
	public void setLocation(String location);

	/**
	 * Setter for bean attribute
	 *
	 * @param houseNo
	 *           The house no
	 */
	public void setHouseNo(String houseNo);

	/**
	 * Setter for bean attribute
	 *
	 * @param houseNo2
	 *           The house no 2
	 */
	public void setHouseNo2(String houseNo2);

	/**
	 * Setter for bean attribute
	 *
	 * @param houseNo3
	 *           The house no3
	 */
	public void setHouseNo3(String houseNo3);

	/**
	 * Setter for bean attribute
	 *
	 * @param building
	 *           The building
	 */
	public void setBuilding(String building);

	/**
	 * Setter for bean attribute
	 *
	 * @param floor
	 *           The floor
	 */
	public void setFloor(String floor);

	/**
	 * Setter for bean attribute
	 *
	 * @param roomNo
	 *           The room number
	 */
	public void setRoomNo(String roomNo);

	/**
	 * Setter for bean attribute
	 *
	 * @param country
	 *           The Country
	 */
	public void setCountry(String country);

	/**
	 * Setter for bean attribute
	 *
	 * @param countryISO
	 *           The country ISO code
	 */
	public void setCountryISO(String countryISO);

	/**
	 * Setter for bean attribute
	 *
	 * @param region
	 *           The region
	 */
	public void setRegion(String region);

	/**
	 * Setter for bean attribute
	 *
	 * @param homeCity
	 *           The home city
	 */
	public void setHomeCity(String homeCity);

	/**
	 * Setter for bean attribute
	 *
	 * @param taxJurCode
	 *           tax jurisdiction code. Can be determined from full address or from district
	 */
	public void setTaxJurCode(String taxJurCode);

	/**
	 * Setter for bean attribute
	 *
	 * @param tel1Numbr
	 *           the telephone number
	 */
	public void setTel1Numbr(String tel1Numbr);

	/**
	 * Setter for bean attribute
	 *
	 * @param tel1Ext
	 *           the telephone extension
	 */
	public void setTel1Ext(String tel1Ext);

	/**
	 * Setter for bean attribute
	 *
	 * @param faxNumber
	 *           The fax number
	 */
	public void setFaxNumber(String faxNumber);

	/**
	 * Setter for bean attribute
	 *
	 * @param faxExtens
	 *           the fax number extension
	 */
	public void setFaxExtens(String faxExtens);

	/**
	 * Setter for bean attribute
	 *
	 * @param email
	 *           the email address
	 */
	public void setEmail(String email);

	/**
	 * Setter for bean attribute
	 *
	 * @param partner
	 *           business partner owning the address
	 */
	public void setAddressPartner(String partner);

	/**
	 * Setter for bean attribute
	 *
	 * @param category
	 *           the address category
	 */
	public void setCategory(String category);

	/**
	 * @return respective bean attribute
	 */
	public String getTitleKey();

	/**
	 * @return title in language dependent format
	 */
	public String getTitle();

	/**
	 * @return academic title key (language independent)
	 */
	public String getTitleAca1Key();

	/**
	 * @return academic title
	 */
	public String getTitleAca1();

	/**
	 * @return respective bean attribute
	 */
	public String getFirstName();

	/**
	 * @return respective bean attribute
	 */
	public String getLastName();

	/**
	 * @return respective bean attribute
	 */
	public String getBirthName();

	/**
	 * @return respective bean attribute
	 */
	public String getSecondName();

	/**
	 * @return respective bean attribute
	 */
	public String getMiddleName();

	/**
	 * @return respective bean attribute
	 */
	public String getNickName();

	/**
	 * @return respective bean attribute
	 */
	public String getInitials();

	/**
	 * @return respective bean attribute
	 */
	public String getName1();

	/**
	 * @return respective bean attribute
	 */
	public String getName2();

	/**
	 * @return respective bean attribute
	 */
	public String getName3();

	/**
	 * @return respective bean attribute
	 */
	public String getName4();

	/**
	 * @return company name
	 */
	public String getCoName();

	/**
	 * @return respective bean attribute
	 */
	public String getCity();

	/**
	 * @return geographic entity used for tax jurisdiction code determination
	 */
	public String getDistrict();

	/**
	 * @return respective bean attribute
	 */
	public String getPostlCod1();

	/**
	 * @return respective bean attribute
	 */
	public String getPostlCod2();

	/**
	 * @return respective bean attribute
	 */
	public String getPostlCod3();

	/**
	 * @return respective bean attribute
	 */
	public String getPcode1Ext();

	/**
	 * @return respective bean attribute
	 */
	public String getPcode2Ext();

	/**
	 * @return respective bean attribute
	 */
	public String getPcode3Ext();

	/**
	 * @return respective bean attribute
	 */
	public String getPoBox();

	/**
	 * @return respective bean attribute
	 */
	public String getPoWoNo();

	/**
	 * @return respective bean attribute
	 */
	public String getPoBoxCit();

	/**
	 * @return respective bean attribute
	 */
	public String getPoBoxReg();

	/**
	 * @return respective bean attribute
	 */
	public String getPoBoxCtry();

	/**
	 * @return respective bean attribute
	 */
	public String getPoCtryISO();

	/**
	 * @return respective bean attribute
	 */
	public String getStreet();

	/**
	 * @return respective bean attribute
	 */
	public String getStrSuppl1();

	/**
	 * @return respective bean attribute
	 */
	public String getStrSuppl2();

	/**
	 * @return respective bean attribute
	 */
	public String getStrSuppl3();

	/**
	 * @return respective bean attribute
	 */
	public String getLocation();

	/**
	 * @return respective bean attribute
	 */
	public String getHouseNo();

	/**
	 * @return respective bean attribute
	 */
	public String getHouseNo2();

	/**
	 * @return respective bean attribute
	 */
	public String getHouseNo3();

	/**
	 * @return respective bean attribute
	 */
	public String getBuilding();

	/**
	 * @return respective bean attribute
	 */
	public String getFloor();

	/**
	 * @return respective bean attribute
	 */
	public String getRoomNo();

	/**
	 * @return respective bean attribute
	 */
	public String getCountry();

	/**
	 * @return respective bean attribute
	 */
	public String getCountryISO();

	/**
	 * @return respective bean attribute
	 */
	public String getRegion();

	/**
	 * @return respective bean attribute
	 */
	public String getHomeCity();

	/**
	 * @return tax jurisdiction which can be derived from complete address or from country, regions city, street and
	 *         district
	 */
	public String getTaxJurCode();

	/**
	 * @return respective bean attribute
	 */
	public String getTel1Numbr();

	/**
	 * @return respective bean attribute
	 */
	public String getTel1Ext();

	/**
	 * @return respective bean attribute
	 */
	public String getFaxNumber();

	/**
	 * @return respective bean attribute
	 */
	public String getFaxExtens();

	/**
	 * @return respective bean attribute
	 */
	public String getEmail();

	/**
	 * @return respective bean attribute
	 */
	public String getCategory();

	/**
	 * @return partner who owns this address
	 */
	public String getAddressPartner();

	/**
	 * Set language dependent text for country
	 *
	 * @param countryText
	 *           the country text
	 */
	public void setCountryText(String countryText);

	/**
	 * @return respective bean attribute
	 */
	public String getCountryText();

	/**
	 * Set language dependent text for region in length 50
	 *
	 * @param regionText50
	 *           the region text length 50
	 */
	public void setRegionText50(String regionText50);

	/**
	 * @return respective bean attribute
	 */
	public String getRegionText50();

	/**
	 * Set language dependent text for region in length 15
	 *
	 * @param regionText15
	 *           the region text length 15
	 */
	public void setRegionText15(String regionText15);

	/**
	 * @return respective bean attribute
	 */
	public String getRegionText15();

	/**
	 * Sets operation mode on current address
	 *
	 * @param operation
	 *           the address operation
	 * @see Operation
	 */
	public void setOperation(Operation operation);

	/**
	 * The operation which is possible on an address
	 *
	 */
	public enum Operation
	{
		/**
		 * Default value. Assigned if actual operation cannot be determined
		 */
		NONE,
		/**
		 * Add a new address
		 */
		ADD,
		/**
		 * Change an address
		 */
		CHANGE,
		/**
		 * Delete an address
		 */
		DELETE
	}

	/**
	 * @return current operation
	 * @see Operation
	 */
	public Operation getOperation();

	/**
	 * @return is this a standard address for a business partner. Cannot be deleted. This only applies to CRM where
	 *         multiple addresses are available
	 */
	public boolean isStdAddress();

	/**
	 * States that current address is a standard address. See {@link Address#isStdAddress()}
	 *
	 * @param stdAddress
	 *           std address flag
	 */
	public void setStdAddress(boolean stdAddress);

	/**
	 * @return respective bean attribute
	 */
	public String getTelmob1();

	/**
	 * Sets first mobile number
	 *
	 * @param telmob1
	 *           the telmob1
	 */
	public void setTelmob1(String telmob1);

	/**
	 * @return address number from CRM or ERP backend if available
	 */
	public String getAddrnum();

	/**
	 * Sets address number which is available in the CRM or ERP backend
	 *
	 * @param addrnum
	 *           the address number (BAS)
	 */
	public void setAddrnum(String addrnum);

	/**
	 * @return guid of address, only available in CRM case
	 */
	public String getAddrguid();

	/**
	 * Sets address guid (only relevant for CRM backend)
	 *
	 * @param addrguid
	 *           the address guid
	 */
	public void setAddrguid(String addrguid);

	/**
	 * @return respective bean attribute
	 */
	public String getFunction();

	/**
	 * Sets job function
	 *
	 * @param function
	 *           the function
	 */
	public void setFunction(String function);

	/**
	 * @return list of available counties. Relevant for tax jurisdiction code determination
	 */
	public List<County> getCountyList();

	/**
	 * Sets lists of available counties. Relevant for tax jurisdiction code determination
	 *
	 * @param countyList
	 *           the county list
	 */
	public void setCountyList(List<County> countyList);

	/**
	 * @return respective bean change attribute
	 */
	public boolean getLastName_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getFirstName_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getName1_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getName2_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getMiddleName_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getCity_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getDistrict_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean isStdAddress_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getPoBox_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getCountry_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getStreet_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getRegion_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getFunction_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getEmail_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getTelmob1_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getFaxNumber_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getStrSuppl1_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getHouseNo_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getFaxExtens_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getPostlCod1_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getPostlCod2_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTel1Numbr_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTel1Ext_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTitleKey_X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTaxJurCode_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getTitle_X();

	/**
	 * @return respective bean change attribute
	 */
	public boolean getCoName_X();

	/**
	 * Sets country dialing code
	 *
	 * @param dialingCode
	 *           the dialing code
	 */
	public void setDialingCode(String dialingCode);

	/**
	 * @return respective bean attribute
	 */
	public String getDialingCode();

	/**
	 * @return respective bean attribute
	 */
	public String getAddrText();

	/**
	 * Sets address short text
	 *
	 * @param addrText
	 *           the address text
	 */
	public void setAddrText(String addrText);

	/**
	 * @return respective bean attribute
	 */
	public String getCompanyName();

	/**
	 * Sets company name
	 *
	 * @param companyName
	 *           the company name
	 */
	public void setCompanyName(String companyName);

	/**
	 * @return respective bean change attribute
	 */
	public boolean getCompanyName_X();

	/**
	 * <b>clear_X</b> This method is to dynamically clear the values of all the <i>"_X"</i> variables except the ones in
	 * the except array. Dynamically fetches the fields names and clears their values.
	 */
	public void clear_X();

	/**
	 * @return respective bean attribute
	 */
	public String getFullName();

	/**
	 * @return has this address been changed
	 */
	public boolean isChanged();

	/**
	 * @return respective bean attribute
	 */
	public String getTel1NumbrSeq();

	/**
	 * Sets respective bean attribute
	 *
	 * @param tel1Seq
	 *           the tel1 sequence number
	 */
	public void setTel1NumbrSeq(String tel1Seq);

	/**
	 * @return respective bean attribute
	 */
	public String getTelmob1Seq();

	/**
	 * Sets respective bean attribute
	 *
	 * @param telmob1Seq
	 *           the telmob1 sequence number
	 */
	public void setTelmob1Seq(String telmob1Seq);

	/**
	 * @return respective bean attribute
	 */
	public String getFaxNumberSeq();

	/**
	 * Sets respective bean attribute
	 *
	 * @param string
	 *           the fax sequence number
	 */
	public void setFaxNumberSeq(String string);

	/**
	 * Sets address in short format
	 *
	 * @param string
	 *           the address string
	 */
	public void setAddressString(String string);

	/**
	 * @return respective bean attribute
	 */
	public String getName();

	/**
	 * @return address in string format
	 */
	public String getAddressString();

	/**
	 * @return is it an address specific to a certain document, e.g. sales transaction
	 */
	public boolean isDocumentAddress();

	/**
	 * Indicates that this address is a document address which means that it's not available in master data but specific
	 * to a sales transaction e.g.
	 *
	 * @param documentAddress
	 *           flag for document address
	 */
	public void setDocumentAddress(boolean documentAddress);

	/**
	 * Sets address string including name
	 *
	 * @param addressString_c
	 *           address in string format, including name
	 */
	public void setAddressString_C(String addressString_c);

	/**
	 * @return address string including name
	 */
	public String getAddressString_C();

	/**
	 * @return a clone of this address
	 */
	public Address clone();


	/**
	 * @return has this address a deviating name
	 */
	public boolean isDeviatingName();

	/**
	 * Setter for the deviating name flag
	 *
	 * @param deviatingName
	 *           flag for deviating name
	 */
	public void setDeviatingName(boolean deviatingName);

	/**
	 * @return the email sequence number
	 */
	String getEmailSeq();

	/**
	 * Setter for the email sequence number
	 *
	 * @param emailSeq
	 *           the email sequence numebr
	 */
	void setEmailSeq(String emailSeq);

	/**
	 * Compares all address content fields
	 *
	 * @param address
	 *           the address to be compared
	 *
	 * @return true, if all address content fields are equal
	 */
	public boolean isAddressfieldsEqualTo(Address address);

	/**
	 * @return respective bean change attribute
	 */
	public boolean getTitleAca1Key_X();

	/**
	 * @return the title supplement key
	 */
	String getTitleSupplKey();

	/**
	 * Setter for the title supplement key
	 *
	 * @param titleSuppl
	 *           the title supplement key
	 */
	void setTitleSupplKey(String titleSuppl);

	/**
	 * @return the title supplement key change flag
	 */
	boolean getTitleSupplKey_X();

	/**
	 * @return the prefix 1 key change flag
	 */
	public boolean getPrefix1Key_X();

	/**
	 * @return the prefix 1 key change flag
	 */
	public boolean getPrefix2Key_X();

	/**
	 * @return the prefix 1
	 */
	public String getPrefix1();

	/**
	 * @return the prefix 2
	 */
	public String getPrefix2();

	/**
	 * @param prefix2
	 */
	void setPrefix2(String prefix2);

	/**
	 * Setter for the name prefix 2 key
	 *
	 * @param prefix2Key
	 *           the prefix 2 key
	 */
	void setPrefix2Key(String prefix2Key);

	/**
	 * @return the prefix 2 key
	 */
	String getPrefix2Key();

	/**
	 * Setter for the name prefix text
	 *
	 * @param prefix1
	 *           the name 1 prefix test
	 */
	void setPrefix1(String prefix1);

	/**
	 * Setter for the name prefix 1 key
	 *
	 * @param prefix1Key
	 *           the prefix 1 key
	 */
	void setPrefix1Key(String prefix1Key);

	/**
	 * @return the prefix 1 key
	 */
	String getPrefix1Key();

	/**
	 * Setter for the full name text
	 *
	 * @param fullName
	 *           the full name string
	 */
	void setFullName(String fullName);

}