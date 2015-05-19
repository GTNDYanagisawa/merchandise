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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.County;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * BO representation of an address
 *
 */
public class AddressImpl extends BusinessObjectBase implements Address
{
	//	static final protected WCFLocation LOG = WCFLocation.getInstance(AddressImpl.class.getName());
	//	protected static final Category LOGCATEGORY = LogUtil.APPS_BUSINESS_LOGIC;

	private static final long serialVersionUID = 1L;
	private static final String INITIAL_STRING = "";

	/**
	 * Postfix to create X fields names (indicating change flag) from standard field names
	 */
	public static final String X_STR = "_X";

	private String id = INITIAL_STRING;
	private String type = INITIAL_STRING; // 1=Organisation, 2=Person, 3=Contact
	// person
	private String origin = INITIAL_STRING;
	private String personNumber = INITIAL_STRING;
	private String titleKey = INITIAL_STRING;
	private String title = INITIAL_STRING;
	private String titleAca1Key = INITIAL_STRING;
	private String titleAca1 = INITIAL_STRING;
	private String firstName = INITIAL_STRING;
	private String lastName = INITIAL_STRING;
	private String birthName = INITIAL_STRING;
	private String secondName = INITIAL_STRING;
	private String middleName = INITIAL_STRING;
	private String nickName = INITIAL_STRING;
	private String initials = INITIAL_STRING;
	private String name1 = INITIAL_STRING;
	private String name2 = INITIAL_STRING;
	private String name3 = INITIAL_STRING;
	private String name4 = INITIAL_STRING;
	private String prefix1Key = INITIAL_STRING;
	private String prefix1 = INITIAL_STRING;
	private String prefix2Key = INITIAL_STRING;
	private String prefix2 = INITIAL_STRING;
	boolean stdAddress = false;
	private String coName = INITIAL_STRING;
	private String city = INITIAL_STRING;
	private String district = INITIAL_STRING;
	private String postlCod1 = INITIAL_STRING;
	private String postlCod2 = INITIAL_STRING;
	private String postlCod3 = INITIAL_STRING;
	private String pcode1Ext = INITIAL_STRING;
	private String pcode2Ext = INITIAL_STRING;
	private String pcode3Ext = INITIAL_STRING;
	private String poBox = INITIAL_STRING;
	private String poWoNo = INITIAL_STRING;
	private String poBoxCit = INITIAL_STRING;
	private String poBoxReg = INITIAL_STRING;
	private String poBoxCtry = INITIAL_STRING;
	private String poCtryISO = INITIAL_STRING;
	private String street = INITIAL_STRING;
	private String strSuppl1 = INITIAL_STRING;
	private String strSuppl2 = INITIAL_STRING;
	private String strSuppl3 = INITIAL_STRING;
	private String location = INITIAL_STRING;
	private String houseNo = INITIAL_STRING;
	private String houseNo2 = INITIAL_STRING;
	private String houseNo3 = INITIAL_STRING;
	private String building = INITIAL_STRING;
	private String floor = INITIAL_STRING;
	private String roomNo = INITIAL_STRING;
	private String country = INITIAL_STRING;
	private String countryISO = INITIAL_STRING;
	private String region = INITIAL_STRING;
	private String homeCity = INITIAL_STRING;
	private String taxJurCode = INITIAL_STRING;
	private String tel1Numbr = INITIAL_STRING;
	private String tel1NumbrSeq = INITIAL_STRING;
	private String tel1Ext = INITIAL_STRING;
	private String faxNumber = INITIAL_STRING;
	private String faxNumberSeq = INITIAL_STRING;
	private String faxExtens = INITIAL_STRING;
	private String email = INITIAL_STRING;
	private String emailSeq = INITIAL_STRING;
	private String countryText = INITIAL_STRING;
	private String regionText50 = INITIAL_STRING;
	private String regionText15 = INITIAL_STRING;

	private String telmob1 = INITIAL_STRING;
	private String telmob1Seq = INITIAL_STRING;
	private String addressString = INITIAL_STRING;
	private String addressString_C = INITIAL_STRING;
	private String addrnum = INITIAL_STRING;
	private String addrguid = INITIAL_STRING;
	private String addrText = INITIAL_STRING;
	private String addressPartner = INITIAL_STRING;
	private String category = INITIAL_STRING;
	private String function = INITIAL_STRING;
	private String companyName = INITIAL_STRING;
	private String dialingCode = INITIAL_STRING;
	private String titleSupplKey = INITIAL_STRING;
	private String fullName = INITIAL_STRING;

	private boolean changed = false;
	private boolean documentAddress = false;
	private boolean deviatingName;
	boolean firstName_X;
	boolean lastName_X;
	boolean name1_X;
	boolean name2_X;
	boolean prefix1Key_X;
	boolean prefix2Key_X;
	boolean middleName_X;
	boolean email_X;
	boolean city_X;
	boolean poBox_X;
	boolean stdAddress_X;
	boolean strSuppl1_X;
	private boolean country_X;
	boolean street_X;
	boolean houseNo_X;
	boolean region_X;
	boolean postlCod1_X;
	boolean postlCod2_X;
	boolean district_X;
	private boolean tel1Numbr_X;
	boolean tel1Ext_X;
	boolean function_X;
	boolean telmob1_X;
	boolean faxNumber_X;
	boolean faxExtens_X;
	private boolean taxJurCode_X;
	boolean titleKey_X;
	boolean companyName_X;
	boolean coName_X;
	boolean title_X;
	boolean titleAca1Key_X;
	boolean titleSupplKey_X;

	private List<County> countyList = null;

	private Operation operation;

	/**
	 * simple constructor
	 */
	public AddressImpl()
	{
		this.setOperation(Operation.NONE);
		this.setTechKey(TechKey.generateKey());
	}


	/**
	 * Set the property id
	 *
	 * @param id
	 *           the id
	 */
	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Returns the property id
	 *
	 * @return id
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 * Set the property type
	 *
	 * @param type
	 *           the type
	 */
	@Override
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * Returns the property type
	 *
	 * @return type
	 */
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public String getCategory()
	{
		return category;
	}

	/**
	 * Set the property origin
	 *
	 * @param origin
	 *           the origin
	 */
	@Override
	public void setOrigin(final String origin)
	{
		this.origin = origin;
	}

	/**
	 * Returns the property origin
	 *
	 * @return origin
	 */
	@Override
	public String getOrigin()
	{
		return origin;
	}

	/**
	 * Set the property personNumber
	 *
	 * @param personNumber
	 *           the peron number
	 */
	@Override
	public void setPersonNumber(final String personNumber)
	{
		this.personNumber = personNumber;
	}

	/**
	 * Returns the property personNumber
	 *
	 * @return personNumber
	 */
	@Override
	public String getPersonNumber()
	{
		return personNumber;
	}

	// setter methods
	@Override
	public void setTitleKey(final String titleKey)
	{
		// cut spaces from ui
		final String newValue = titleKey != null ? titleKey.trim() : null;
		final String oldValue = getTitleKey();
		if (!oldValue.equals(titleKey))
		{
			this.titleKey_X = true;
		}
		this.titleKey = titleKey == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTitle(final String title)
	{
		// cut spaces from ui
		final String newValue = title != null ? title.trim() : null;
		final String oldValue = getTitle();
		if (!oldValue.equals(title))
		{
			this.title_X = true;
		}
		this.title = title == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTitleAca1Key(final String titleAca1Key)
	{
		if (!getTitleAca1Key().equals(titleAca1Key))
		{
			this.titleAca1Key_X = true;
		}
		this.titleAca1Key = titleAca1Key == null ? INITIAL_STRING : titleAca1Key;
	}

	@Override
	public void setTitleAca1(final String titleAca1)
	{
		this.titleAca1 = titleAca1;
	}

	@Override
	public void setFirstName(final String firstName)
	{
		// cut spaces from ui
		final String newValue = firstName != null ? firstName.trim() : null;
		final String oldValue = getFirstName();
		if (!oldValue.equals(firstName))
		{
			this.firstName_X = true;
		}
		this.firstName = firstName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setLastName(final String lastName)
	{
		// cut spaces from ui
		final String newValue = lastName != null ? lastName.trim() : null;
		final String oldValue = getLastName();
		if (!oldValue.equals(lastName))
		{
			this.lastName_X = true;
		}
		this.lastName = lastName == null ? INITIAL_STRING : newValue;

	}

	@Override
	public void setName1(final String name1)
	{
		final String newValue = name1 != null ? name1.trim() : null;
		final String oldValue = getName1();
		if (!oldValue.equals(name1))
		{
			this.name1_X = true;
		}
		this.name1 = name1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setName2(final String name2)
	{
		final String newValue = name2 != null ? name2.trim() : null;
		final String oldValue = getName2();
		if (!oldValue.equals(name2))
		{
			this.name2_X = true;
		}
		this.name2 = name2 == null ? INITIAL_STRING : newValue;

	}

	@Override
	public void setBirthName(final String birthName)
	{
		this.birthName = birthName;
	}

	@Override
	public void setSecondName(final String secondName)
	{
		this.secondName = secondName;
	}

	@Override
	public void setMiddleName(final String middleName)
	{
		// cut spaces from ui
		final String newValue = middleName != null ? middleName.trim() : null;
		final String oldValue = getMiddleName();
		if (!oldValue.equals(middleName))
		{
			this.middleName_X = true;
		}
		this.middleName = middleName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setNickName(final String nickName)
	{
		this.nickName = nickName;
	}

	@Override
	public void setInitials(final String initials)
	{
		this.initials = initials;
	}

	@Override
	public void setCoName(final String coName)
	{
		// cut spaces from ui
		final String newValue = coName != null ? coName.trim() : null;
		final String oldValue = getCoName();
		if (!oldValue.equals(coName))
		{
			this.coName_X = true;
		}
		this.coName = coName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setCity(final String city)
	{
		// cut spaces from ui
		final String newValue = city != null ? city.trim() : null;
		final String oldValue = getCity();
		if (!oldValue.equals(city))
		{
			this.city_X = true;
			this.setTaxJurCode("");
		}
		this.city = city == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setDistrict(final String district)
	{
		// cut spaces from ui
		final String newValue = district != null ? district.trim() : null;
		final String oldValue = getDistrict();
		if (!oldValue.equals(district))
		{
			this.district_X = true;
			this.setTaxJurCode("");
		}
		this.district = district == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getDistrict_X()
	{
		return district_X;
	}

	@Override
	public void setPostlCod1(final String postlCod1)
	{
		// cut spaces from ui
		final String newValue = postlCod1 != null ? postlCod1.trim() : null;
		final String oldValue = getPostlCod1();
		if (!oldValue.equals(postlCod1))
		{
			this.postlCod1_X = true;
			this.setTaxJurCode("");
		}
		this.postlCod1 = postlCod1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getPostlCod1_X()
	{
		return postlCod1_X;
	}

	@Override
	public void setPostlCod2(final String postlCod2)
	{
		// cut spaces from ui
		final String newValue = postlCod2 != null ? postlCod2.trim() : null;
		final String oldValue = getPostlCod2();
		if (!oldValue.equals(postlCod2))
		{
			this.postlCod2_X = true;
		}
		this.postlCod2 = postlCod2 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setPostlCod3(final String postlCod3)
	{
		this.postlCod3 = postlCod3;
	}

	@Override
	public void setPcode1Ext(final String pcode1Ext)
	{
		this.pcode1Ext = pcode1Ext;
	}

	@Override
	public void setPcode2Ext(final String pcode2Ext)
	{
		this.pcode2Ext = pcode2Ext;
	}

	@Override
	public void setPcode3Ext(final String pcode3Ext)
	{
		this.pcode3Ext = pcode3Ext;
	}

	@Override
	public void setPoBox(final String poBox)
	{
		// cut spaces from ui
		final String newValue = poBox != null ? poBox.trim() : null;
		final String oldValue = getPoBox();
		if (!oldValue.equals(poBox))
		{
			this.poBox_X = true;
		}
		this.poBox = poBox == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setPoWoNo(final String poWoNo)
	{
		this.poWoNo = poWoNo;
	}

	@Override
	public void setPoBoxCit(final String poBoxCit)
	{
		this.poBoxCit = poBoxCit;
	}

	@Override
	public void setPoBoxReg(final String poBoxReg)
	{
		this.poBoxReg = poBoxReg;
	}

	@Override
	public void setPoBoxCtry(final String poBoxCtry)
	{
		this.poBoxCtry = poBoxCtry;
	}

	@Override
	public void setPoCtryISO(final String poCtryISO)
	{
		this.poCtryISO = poCtryISO;
	}

	@Override
	public void setStreet(final String street)
	{
		// cut spaces from ui
		final String newValue = street != null ? street.trim() : null;
		final String oldValue = getStreet();
		if (!oldValue.equals(street))
		{
			this.street_X = true;
		}
		this.street = street == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setStrSuppl1(final String strSuppl1)
	{
		// cut spaces from ui
		final String newValue = strSuppl1 != null ? strSuppl1.trim() : null;
		final String oldValue = getStrSuppl1();
		if (!oldValue.equals(strSuppl1))
		{
			this.strSuppl1_X = true;
		}
		this.strSuppl1 = strSuppl1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setStrSuppl2(final String strSuppl2)
	{
		this.strSuppl2 = strSuppl2;
	}

	@Override
	public void setStrSuppl3(final String strSuppl3)
	{
		this.strSuppl3 = strSuppl3;
	}

	@Override
	public void setLocation(final String location)
	{
		this.location = location;
	}

	@Override
	public void setHouseNo(final String houseNo)
	{
		// cut spaces from ui
		final String newValue = houseNo != null ? houseNo.trim() : null;
		final String oldValue = getHouseNo();
		if (!oldValue.equals(houseNo))
		{
			this.houseNo_X = true;
		}
		this.houseNo = houseNo == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setHouseNo2(final String houseNo2)
	{
		this.houseNo2 = houseNo2;
	}

	@Override
	public void setHouseNo3(final String houseNo3)
	{
		this.houseNo3 = houseNo3;
	}

	@Override
	public void setBuilding(final String building)
	{
		this.building = building;
	}

	@Override
	public void setFloor(final String floor)
	{
		this.floor = floor;
	}

	@Override
	public void setRoomNo(final String roomNo)
	{
		this.roomNo = roomNo;
	}

	@Override
	public void setCountry(final String country)
	{
		// cut spaces from ui
		final String newValue = country != null ? country.trim() : null;
		final String oldValue = getCountry();
		if (!oldValue.equals(country))
		{
			this.country_X = true;
			this.setTaxJurCode("");
		}
		this.country = country == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setCountryISO(final String countryISO)
	{
		this.countryISO = countryISO;
	}

	@Override
	public void setRegion(final String region)
	{
		// cut spaces from ui
		final String newValue = region != null ? region.trim() : null;
		final String oldValue = getRegion();
		if (!oldValue.equals(region))
		{
			this.region_X = true;
			this.setTaxJurCode("");
		}
		this.region = region == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setHomeCity(final String homeCity)
	{
		this.homeCity = homeCity;
	}

	@Override
	public void setTaxJurCode(final String taxJurCode)
	{
		// cut spaces from ui
		final String newValue = taxJurCode != null ? taxJurCode.trim() : null;
		final String oldValue = getTaxJurCode();
		if (!oldValue.equals(taxJurCode))
		{
			this.taxJurCode_X = true;
		}
		this.taxJurCode = taxJurCode == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTel1Numbr(final String tel1Numbr)
	{
		// cut spaces from ui
		final String newValue = tel1Numbr != null ? tel1Numbr.trim() : null;
		final String oldValue = getTel1Numbr();
		if (!oldValue.equals(tel1Numbr))
		{
			this.tel1Numbr_X = true;
		}
		this.tel1Numbr = tel1Numbr == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTel1Ext(final String tel1Ext)
	{
		// cut spaces from ui
		final String newValue = tel1Ext != null ? tel1Ext.trim() : null;
		final String oldValue = getTel1Ext();
		if (!oldValue.equals(tel1Ext))
		{
			this.tel1Ext_X = true;
		}
		this.tel1Ext = tel1Ext == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setFaxNumber(final String faxNumber)
	{
		// cut spaces from ui
		final String newValue = faxNumber != null ? faxNumber.trim() : null;
		final String oldValue = getFaxNumber();
		if (!oldValue.equals(faxNumber))
		{
			this.faxNumber_X = true;
		}
		this.faxNumber = faxNumber == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getFaxNumber_X()
	{
		return faxNumber_X;
	}

	@Override
	public void setFaxExtens(final String faxExtens)
	{
		// cut spaces from ui
		final String newValue = faxExtens != null ? faxExtens.trim() : null;
		final String oldValue = getFaxExtens();
		if (!oldValue.equals(faxExtens))
		{
			this.faxExtens_X = true;
		}
		this.faxExtens = faxExtens == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setAddressPartner(final String partner)
	{
		addressPartner = partner;
	}

	// getter methods
	@Override
	public String getTitleKey()
	{
		return titleKey;
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public String getTitleAca1Key()
	{
		return titleAca1Key;
	}

	@Override
	public String getTitleAca1()
	{
		return titleAca1;
	}

	@Override
	public String getFirstName()
	{
		return firstName;
	}

	@Override
	public String getLastName()
	{
		return lastName;
	}

	@Override
	public String getBirthName()
	{
		return birthName;
	}

	@Override
	public String getSecondName()
	{
		return secondName;
	}

	@Override
	public String getMiddleName()
	{
		return middleName;
	}

	@Override
	public String getNickName()
	{
		return nickName;
	}

	@Override
	public String getInitials()
	{
		return initials;
	}

	@Override
	public String getName1()
	{
		return name1;
	}

	@Override
	public String getName2()
	{
		return name2;
	}

	@Override
	public String getName3()
	{
		return name3;
	}

	@Override
	public String getName4()
	{
		return name4;
	}

	@Override
	public String getCoName()
	{
		return coName;
	}

	@Override
	public String getCity()
	{
		return city;
	}

	@Override
	public String getDistrict()
	{
		return district;
	}

	@Override
	public String getPostlCod1()
	{
		return postlCod1;
	}

	@Override
	public String getPostlCod2()
	{
		return postlCod2;
	}

	@Override
	public String getPostlCod3()
	{
		return postlCod3;
	}

	@Override
	public String getPcode1Ext()
	{
		return pcode1Ext;
	}

	@Override
	public String getPcode2Ext()
	{
		return pcode2Ext;
	}

	@Override
	public String getPcode3Ext()
	{
		return pcode3Ext;
	}

	@Override
	public String getPoBox()
	{
		return poBox;
	}

	@Override
	public String getPoWoNo()
	{
		return poWoNo;
	}

	@Override
	public String getPoBoxCit()
	{
		return poBoxCit;
	}

	@Override
	public String getPoBoxReg()
	{
		return poBoxReg;
	}

	@Override
	public String getPoBoxCtry()
	{
		return poBoxCtry;
	}

	@Override
	public String getPoCtryISO()
	{
		return poCtryISO;
	}

	@Override
	public String getStreet()
	{
		return street;
	}

	@Override
	public String getStrSuppl1()
	{
		return strSuppl1;
	}

	@Override
	public boolean getStrSuppl1_X()
	{
		return strSuppl1_X;
	}

	@Override
	public String getStrSuppl2()
	{
		return strSuppl2;
	}

	@Override
	public String getStrSuppl3()
	{
		return strSuppl3;
	}

	@Override
	public String getLocation()
	{
		return location;
	}

	@Override
	public String getHouseNo()
	{
		return houseNo;
	}

	@Override
	public String getHouseNo2()
	{
		return houseNo2;
	}

	@Override
	public String getHouseNo3()
	{
		return houseNo3;
	}

	@Override
	public String getBuilding()
	{
		return building;
	}

	@Override
	public String getFloor()
	{
		return floor;
	}

	@Override
	public String getRoomNo()
	{
		return roomNo;
	}

	@Override
	public String getCountry()
	{
		return country;
	}

	@Override
	public String getCountryISO()
	{
		return countryISO;
	}

	@Override
	public String getRegion()
	{
		return region;
	}

	@Override
	public String getHomeCity()
	{
		return homeCity;
	}

	@Override
	public String getTaxJurCode()
	{
		return taxJurCode;
	}

	@Override
	public String getTel1Numbr()
	{
		return tel1Numbr;
	}

	@Override
	public String getTel1Ext()
	{
		return tel1Ext;
	}

	@Override
	public String getFaxNumber()
	{
		return faxNumber;
	}

	@Override
	public void setDialingCode(final String dialingCode)
	{
		this.dialingCode = dialingCode;
	}

	@Override
	public String getDialingCode()
	{
		return dialingCode;
	}

	@Override
	public String getFaxExtens()
	{
		return faxExtens;
	}

	@Override
	public String getAddressPartner()
	{
		return addressPartner;
	}

	/**
	 * Set the property eMail
	 *
	 * @param email
	 *           the email address
	 */
	@Override
	public void setEmail(final String email)
	{
		// cut spaces from ui
		final String newValue = email != null ? email.trim() : null;
		final String oldValue = getEmail();
		if (!oldValue.equals(email))
		{
			this.email_X = true;
		}
		this.email = email == null ? INITIAL_STRING : newValue;
	}

	/**
	 * Returns the property email
	 *
	 * @return email
	 */
	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public String getName()
	{
		return lastName.length() > 0 ? lastName : name1;
	}

	@Override
	public void setCountryText(final String countryText)
	{
		this.countryText = countryText;
	}

	@Override
	public String getCountryText()
	{
		return countryText;
	}

	@Override
	public void setRegionText50(final String regionText50)
	{
		this.regionText50 = regionText50;
	}

	@Override
	public String getRegionText50()
	{
		return regionText50;
	}

	@Override
	public void setRegionText15(final String regionText15)
	{
		this.regionText15 = regionText15;
	}

	@Override
	public String getRegionText15()
	{
		return regionText15;
	}

	@Override
	public void setCategory(final String category)
	{
		this.category = category;
	}

	@Override
	public AddressImpl clone()
	{
		try
		{
			final AddressImpl addressClone = (AddressImpl) super.clone();
			addressClone.setCountyList(getClonedCountyList());
			return addressClone;
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}
	}

	private List<County> getClonedCountyList()
	{
		List<County> clone = null;
		if (countyList != null)
		{
			clone = new ArrayList<County>(this.countyList.size());
			for (final County item : countyList)
			{
				clone.add(item.clone());
			}
		}
		return clone;
	}

	@Override
	public void setOperation(final Operation operation)
	{
		this.operation = operation == null ? Operation.NONE : operation;
	}

	@Override
	public Operation getOperation()
	{
		return operation;
	}

	@Override
	public boolean isStdAddress_X()
	{
		return stdAddress_X;
	}

	/**
	 * Sets change indicator for standard address
	 *
	 * @param stdAddress_X
	 *           std address changed flag
	 */
	public void setStdAddress_X(final boolean stdAddress_X)
	{
		this.stdAddress_X = stdAddress_X;
	}

	@Override
	public String getTelmob1()
	{
		return telmob1;
	}

	@Override
	public void setTelmob1(final String telmob1)
	{
		// cut spaces from ui
		final String newValue = telmob1 != null ? telmob1.trim() : null;
		final String oldValue = getTelmob1();
		if (!oldValue.equals(telmob1))
		{
			this.telmob1_X = true;
		}
		this.telmob1 = telmob1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getTelmob1_X()
	{
		return telmob1_X;
	}

	@Override
	public boolean getEmail_X()
	{
		return email_X;
	}

	@Override
	public String getAddrnum()
	{
		return addrnum;
	}

	@Override
	public void setAddrnum(final String addrnum)
	{
		this.addrnum = addrnum;
	}

	@Override
	public String getAddrguid()
	{
		return addrguid;
	}

	@Override
	public void setAddrguid(final String addrguid)
	{
		this.addrguid = addrguid == null ? INITIAL_STRING : addrguid;
	}

	@Override
	public String getFunction()
	{
		return function;
	}

	@Override
	public void setFunction(final String function)
	{
		// cut spaces from ui
		final String newValue = function != null ? function.trim() : null;
		final String oldValue = getFunction();
		if (!oldValue.equals(function))
		{
			this.function_X = true;
		}
		this.function = function == null ? INITIAL_STRING : newValue;

	}

	@Override
	public boolean getLastName_X()
	{
		return lastName_X;
	}

	@Override
	public List<County> getCountyList()
	{
		return countyList;
	}

	@Override
	public void setCountyList(final List<County> countyList)
	{
		this.countyList = countyList;
	}

	@Override
	public boolean getFirstName_X()
	{
		return firstName_X;
	}

	@Override
	public boolean getMiddleName_X()
	{
		return middleName_X;
	}

	@Override
	public boolean getName1_X()
	{
		return name1_X;
	}

	@Override
	public boolean getName2_X()
	{
		return name2_X;
	}

	@Override
	public boolean getCity_X()
	{
		return city_X;
	}

	@Override
	public boolean getPoBox_X()
	{
		return poBox_X;
	}

	@Override
	public boolean getCountry_X()
	{
		return country_X;
	}

	@Override
	public boolean getStreet_X()
	{
		return street_X;
	}

	@Override
	public boolean getRegion_X()
	{
		return region_X;
	}

	@Override
	public boolean getFunction_X()
	{
		return function_X;
	}

	@Override
	public boolean getHouseNo_X()
	{
		return houseNo_X;
	}

	@Override
	public boolean getTel1Numbr_X()
	{
		return tel1Numbr_X;
	}

	@Override
	public boolean getTel1Ext_X()
	{
		return tel1Ext_X;
	}

	@Override
	public boolean getFaxExtens_X()
	{
		return faxExtens_X;
	}

	@Override
	public boolean getTaxJurCode_X()
	{
		return taxJurCode_X;
	}

	@Override
	public boolean getTitleKey_X()
	{
		return titleKey_X;
	}

	@Override
	public String getAddrText()
	{
		return addrText;
	}

	@Override
	public void setAddrText(final String addrText)
	{
		this.addrText = addrText;
	}

	@Override
	public void setTelmob1Seq(final String telmob1_seq)
	{
		this.telmob1Seq = telmob1_seq;
	}

	@Override
	public String getTelmob1Seq()
	{
		return telmob1Seq;
	}

	@Override
	public String getCompanyName()
	{
		return companyName;
	}

	@Override
	public void setCompanyName(final String companyName)
	{
		// cut spaces from ui
		final String newValue = companyName != null ? companyName.trim() : null;
		final String oldValue = getCompanyName();
		if (!oldValue.equals(companyName))
		{
			this.companyName_X = true;
		}
		this.companyName = companyName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getCompanyName_X()
	{
		return companyName_X;
	}

	@Override
	public String getAddressString()
	{
		return addressString;
	}

	@Override
	public void setAddressString(final String addressString)
	{
		this.addressString = addressString;
	}

	@Override
	public String getAddressString_C()
	{
		return addressString_C;
	}

	@Override
	public void setAddressString_C(final String addressString_C)
	{
		this.addressString_C = addressString_C;
	}

	/**
	 * <b>clear_X</b> This method is to clear the values of all the <i>"_X"</i> variables
	 */
	@Override
	public void clear_X()
	{
		this.firstName_X = false;
		this.lastName_X = false;
		this.middleName_X = false;
		this.email_X = false;
		this.city_X = false;
		this.poBox_X = false;
		this.name1_X = false;
		this.name2_X = false;
		this.stdAddress_X = false;
		this.strSuppl1_X = false;
		this.country_X = false;
		this.street_X = false;
		this.houseNo_X = false;
		this.region_X = false;
		this.postlCod1_X = false;
		this.postlCod2_X = false;
		this.district_X = false;
		this.tel1Numbr_X = false;
		this.tel1Ext_X = false;
		this.function_X = false;
		this.telmob1_X = false;
		this.faxNumber_X = false;
		this.faxExtens_X = false;
		this.taxJurCode_X = false;
		this.titleKey_X = false;
		this.companyName_X = false;
		this.coName_X = false;
		this.title_X = false;
		this.titleAca1Key_X = false;
		this.titleSupplKey_X = false;
		this.prefix1Key_X = false;
		this.prefix2Key_X = false;
	}

	/**
	 * <b>getIs_changed</b> This method will check all the "_X" fields and see if the address has changed or not
	 *
	 * @return boolean - changed or not
	 */
	@Override
	public boolean isChanged()
	{
		changed = false;

		if (this.firstName_X || this.lastName_X || this.middleName_X || this.email_X || this.city_X || this.poBox_X
				|| this.stdAddress_X || this.strSuppl1_X || this.country_X || this.street_X || this.name1_X || this.name2_X
				|| this.houseNo_X || this.region_X || this.postlCod1_X || this.postlCod2_X || this.district_X || this.tel1Numbr_X
				|| this.tel1Ext_X || this.function_X || this.telmob1_X || this.faxNumber_X || this.faxExtens_X || this.taxJurCode_X
				|| this.titleKey_X || this.companyName_X || this.coName_X || this.title_X || this.titleAca1Key_X || this.prefix1Key_X
				|| this.prefix2Key_X)
		{
			changed = true;
		}
		return changed;
	}



	@Override
	public void setAllXFields()
	{
		this.firstName_X = true;
		this.lastName_X = true;
		this.middleName_X = true;
		this.email_X = true;
		this.name1_X = true;
		this.name2_X = true;
		this.city_X = true;
		this.poBox_X = true;
		this.stdAddress_X = true;
		this.strSuppl1_X = true;
		this.country_X = true;
		this.street_X = true;
		this.houseNo_X = true;
		this.region_X = true;
		this.postlCod1_X = true;
		this.postlCod2_X = true;
		this.district_X = true;
		this.tel1Numbr_X = true;
		this.tel1Ext_X = true;
		this.function_X = true;
		this.telmob1_X = true;
		this.faxNumber_X = true;
		this.faxExtens_X = true;
		this.taxJurCode_X = true;
		this.titleKey_X = true;
		this.companyName_X = true;
		this.coName_X = true;
		this.title_X = true;
		this.titleAca1Key_X = true;
		this.titleSupplKey_X = true;
		this.prefix1Key_X = true;
		this.prefix2Key_X = true;
	}

	@Override
	public boolean getPostlCod2_X()
	{
		return postlCod2_X;
	}

	@Override
	public void setName3(final String name3)
	{
		this.name3 = name3;
	}

	@Override
	public void setName4(final String name4)
	{
		this.name4 = name4;
	}

	@Override
	public boolean getCoName_X()
	{
		return coName_X;
	}

	@Override
	public String getFaxNumberSeq()
	{
		return faxNumberSeq;
	}

	@Override
	public void setFaxNumberSeq(final String faxNumberSeq)
	{
		this.faxNumberSeq = faxNumberSeq;
	}

	@Override
	public String getTel1NumbrSeq()
	{
		return tel1NumbrSeq;
	}

	@Override
	public void setTel1NumbrSeq(final String tel1NumbrSeq)
	{
		this.tel1NumbrSeq = tel1NumbrSeq;
	}

	@Override
	public boolean isDocumentAddress()
	{
		return documentAddress;
	}

	@Override
	public void setDocumentAddress(final boolean documentAddress)
	{
		this.documentAddress = documentAddress;
	}

	@Override
	public boolean getTitle_X()
	{
		return this.title_X;
	}

	@Override
	public boolean isStdAddress()
	{
		return this.stdAddress;
	}

	@Override
	public void setStdAddress(final boolean stdAddress)
	{
		this.stdAddress_X = stdAddress != this.stdAddress;
		this.stdAddress = stdAddress;
	}

	@Override
	// do not remove when cleaning up - we still need a generic get method
	public String get(final String fieldName)
	{
		try
		{
			final Field field = this.getClass().getDeclaredField(fieldName);
			// get the current value
			final Object oldValue = field.get(this);
			return oldValue.toString();
		}
		catch (final IllegalAccessException ex)
		{
			//			LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final IllegalArgumentException ex)
		{
			//			LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final SecurityException ex)
		{
			//			LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final NoSuchFieldException ex)
		{
			//			LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		return null;
	}


	@Override
	public boolean isDeviatingName()
	{
		return deviatingName;
	}

	@Override
	public void setDeviatingName(final boolean deviatingName)
	{
		this.deviatingName = deviatingName;
	}

	@Override
	public String getEmailSeq()
	{
		return emailSeq;
	}

	@Override
	public void setEmailSeq(final String emailSeq)
	{
		this.emailSeq = emailSeq;
	}

	@Override
	public boolean isAddressfieldsEqualTo(final Address a)
	{

		return birthName.equals(a.getBirthName()) && building.equals(a.getBuilding()) && city.equals(a.getCity())
				&& companyName.equals(a.getCompanyName()) && coName.equals(a.getCoName()) && country.equals(a.getCountry())
				&& dialingCode.equals(a.getDialingCode()) && district.equals(a.getDistrict()) && email.equals(a.getEmail())
				&& emailSeq.equals(a.getEmailSeq()) && faxExtens.equals(a.getFaxExtens()) && faxNumber.equals(a.getFaxNumber())
				&& faxNumberSeq.equals(a.getFaxNumberSeq()) && firstName.equals(a.getFirstName()) && floor.equals(a.getFloor())
				&& homeCity.equals(a.getHomeCity()) && houseNo.equals(a.getHouseNo()) && houseNo2.equals(a.getHouseNo2())
				&& houseNo3.equals(a.getHouseNo3()) && initials.equals(a.getInitials()) && lastName.equals(a.getLastName())
				&& location.equals(a.getLocation()) && middleName.equals(a.getMiddleName()) && name1.equals(a.getName1())
				&& name2.equals(a.getName2()) && name3.equals(a.getName3()) && name4.equals(a.getName4())
				&& nickName.equals(a.getNickName()) && pcode1Ext.equals(a.getPcode1Ext()) && pcode2Ext.equals(a.getPcode2Ext())
				&& pcode3Ext.equals(a.getPcode3Ext()) && poBox.equals(a.getPoBox()) && poBoxCit.equals(a.getPoBoxCit())
				&& poBoxCtry.equals(a.getPoBoxCtry()) && poBoxReg.equals(a.getPoBoxReg()) && poCtryISO.equals(a.getPoCtryISO())
				&& postlCod1.equals(a.getPostlCod1()) && postlCod2.equals(a.getPostlCod2()) && postlCod3.equals(a.getPostlCod3())
				&& poWoNo.equals(a.getPoWoNo()) && region.equals(a.getRegion()) && roomNo.equals(a.getRoomNo())
				&& secondName.equals(a.getSecondName()) && street.equals(a.getStreet()) && strSuppl1.equals(a.getStrSuppl1())
				&& strSuppl2.equals(a.getStrSuppl2()) && strSuppl3.equals(a.getStrSuppl3()) && tel1Ext.equals(a.getTel1Ext())
				&& tel1Numbr.equals(a.getTel1Numbr()) && tel1NumbrSeq.equals(a.getTel1NumbrSeq()) && telmob1.equals(a.getTelmob1())
				&& telmob1Seq.equals(a.getTelmob1Seq()) && titleKey.equals(a.getTitleKey())
				&& titleAca1Key.equals(a.getTitleAca1Key()) && titleSupplKey.equals(a.getTitleSupplKey())
				&& prefix1Key.equals(a.getPrefix1Key()) && prefix2Key.equals(a.getPrefix2Key())

		;
	}

	@Override
	public int compareTo(final Address o)
	{
		return addressString_C.compareTo(o.getAddressString_C());
	}

	@Override
	public boolean getTitleAca1Key_X()
	{
		return titleAca1Key_X;
	}

	@Override
	public String getTitleSupplKey()
	{
		return titleSupplKey;
	}

	@Override
	public void setTitleSupplKey(final String titleSupplKey)
	{
		final String newValue = titleSupplKey != null ? titleSupplKey.trim() : null;
		final String oldValue = getTitleSupplKey();
		if (!oldValue.equals(titleSupplKey))
		{
			this.titleSupplKey_X = true;
		}
		this.titleSupplKey = titleSupplKey == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getTitleSupplKey_X()
	{
		return titleSupplKey_X;
	}

	/**
	 * Set the title supplement key changed flag
	 *
	 * @param titleSupplKeyX
	 *           the changed flag
	 */
	public void setTitleSupplKey_X(final boolean titleSupplKeyX)
	{
		titleSupplKey_X = titleSupplKeyX;
	}


	@Override
	public boolean getPrefix1Key_X()
	{
		return prefix1Key_X;
	}

	@Override
	public boolean getPrefix2Key_X()
	{
		return prefix2Key_X;
	}

	@Override
	public String getPrefix1Key()
	{
		return prefix1Key;
	}

	@Override
	public void setPrefix1Key(final String prefix1Key)
	{
		final String newValue = prefix1Key != null ? prefix1Key.trim() : null;
		final String oldValue = getPrefix1Key();
		if (!oldValue.equals(prefix1Key))
		{
			this.prefix1Key_X = true;
		}
		this.prefix1Key = prefix1Key == null ? INITIAL_STRING : newValue;
	}

	@Override
	public String getPrefix1()
	{
		return prefix1;
	}

	@Override
	public void setPrefix1(final String prefix1)
	{
		this.prefix1 = prefix1;
	}

	@Override
	public String getPrefix2Key()
	{
		return prefix2Key;
	}

	@Override
	public void setPrefix2Key(final String prefix2Key)
	{
		final String newValue = prefix2Key != null ? prefix2Key.trim() : null;
		final String oldValue = getPrefix2Key();
		if (!oldValue.equals(prefix2Key))
		{
			this.prefix2Key_X = true;
		}
		this.prefix2Key = prefix2Key == null ? INITIAL_STRING : newValue;
	}

	@Override
	public String getPrefix2()
	{
		return prefix2;
	}

	@Override
	public void setPrefix2(final String prefix2)
	{
		this.prefix2 = prefix2;
	}

	@Override
	public String getFullName()
	{
		return fullName;
	}

	@Override
	public void setFullName(final String fullName)
	{
		this.fullName = fullName;
	}







}