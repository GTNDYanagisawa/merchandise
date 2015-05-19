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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class AddressImplTest
{
	AddressImpl classUnderTest = new AddressImpl();
	private final String firstName = "Robby";
	private final String value = "value";


	@Test
	public void testClear_X()
	{
		classUnderTest.firstName_X = true;
		classUnderTest.clear_X();
		assertFalse(classUnderTest.firstName_X);
	}

	@Test
	public void testCompareToNoChangeInAddString()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setFirstName("Hello");
		final int compareTo = classUnderTest.compareTo(otherAddress);
		assertTrue(compareTo == 0);
	}

	@Test
	public void testCompareToChangeInAddString()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setAddressString_C("Hello");
		final int compareTo = classUnderTest.compareTo(otherAddress);
		assertFalse(compareTo == 0);
	}

	@Test
	public void testGetNotExisting()
	{
		final String attribute = classUnderTest.get("DOES_NOT_EXIST");
		assertNull(attribute);
	}

	@Test
	public void testGetExisting()
	{

		classUnderTest.setFirstName(firstName);
		final String attribute = classUnderTest.get("firstName");
		assertEquals(firstName, attribute);
	}

	@Test
	public void testIsEqualTo()
	{
		final AddressImpl otherAddress = new AddressImpl();
		final boolean addressfieldsEqualTo = classUnderTest.isAddressfieldsEqualTo(otherAddress);
		assertTrue(addressfieldsEqualTo);
	}

	@Test
	public void testIsEqualToFirstNameDeviates()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setFirstName(firstName);
		final boolean addressfieldsEqualTo = classUnderTest.isAddressfieldsEqualTo(otherAddress);
		assertFalse(addressfieldsEqualTo);
	}

	@Test
	public void testIsChanged()
	{
		assertFalse(classUnderTest.isChanged());
	}

	@Test
	public void testIsChangedFirstNameDeviates()
	{
		classUnderTest.setFirstName(firstName);
		assertTrue(classUnderTest.isChanged());
	}

	@Test
	public void setAllXFields()
	{
		classUnderTest.setAllXFields();
		assertTrue(classUnderTest.firstName_X);
	}

	@Test
	public void testSetCity()
	{
		assertFalse(classUnderTest.city_X);
		classUnderTest.setCity("City");
		assertTrue(classUnderTest.city_X);
	}

	@Test
	public void testCoName()
	{
		assertFalse(classUnderTest.coName_X);
		classUnderTest.setCoName(value);
		assertTrue(classUnderTest.coName_X);
		assertEquals(value, classUnderTest.getCoName());
	}

	@Test
	public void testCompanyName()
	{
		assertFalse(classUnderTest.companyName_X);
		classUnderTest.setCompanyName(value);
		assertTrue(classUnderTest.companyName_X);
		assertEquals(value, classUnderTest.getCompanyName());
	}

	@Test
	public void testDistrict()
	{
		assertFalse(classUnderTest.district_X);
		classUnderTest.setDistrict(value);
		assertTrue(classUnderTest.district_X);
		assertEquals(value, classUnderTest.getDistrict());
	}

	@Test
	public void testEmail()
	{
		assertFalse(classUnderTest.email_X);
		classUnderTest.setEmail(value);
		assertTrue(classUnderTest.email_X);
		assertEquals(value, classUnderTest.getEmail());
	}

	@Test
	public void testFaxExtens()
	{
		assertFalse(classUnderTest.faxExtens_X);
		classUnderTest.setFaxExtens(value);
		assertTrue(classUnderTest.faxExtens_X);
		assertEquals(value, classUnderTest.getFaxExtens());
	}

	@Test
	public void testFaxNumber()
	{
		assertFalse(classUnderTest.faxNumber_X);
		classUnderTest.setFaxNumber(value);
		assertTrue(classUnderTest.faxNumber_X);
		assertEquals(value, classUnderTest.getFaxNumber());
	}

	@Test
	public void testFunction()
	{
		assertFalse(classUnderTest.function_X);
		classUnderTest.setFunction(value);
		assertTrue(classUnderTest.function_X);
		assertEquals(value, classUnderTest.getFunction());
	}

	@Test
	public void testHouseNo()
	{
		assertFalse(classUnderTest.houseNo_X);
		classUnderTest.setHouseNo(value);
		assertTrue(classUnderTest.houseNo_X);
		assertEquals(value, classUnderTest.getHouseNo());
	}

	@Test
	public void testLastName()
	{
		assertFalse(classUnderTest.lastName_X);
		classUnderTest.setLastName(value);
		assertTrue(classUnderTest.lastName_X);
		assertEquals(value, classUnderTest.getLastName());
	}

	@Test
	public void testName1()
	{
		assertFalse(classUnderTest.name1_X);
		classUnderTest.setName1(value);
		assertTrue(classUnderTest.name1_X);
		assertEquals(value, classUnderTest.getName1());
	}

	@Test
	public void testName2()
	{
		assertFalse(classUnderTest.name2_X);
		classUnderTest.setName2(value);
		assertTrue(classUnderTest.name2_X);
		assertEquals(value, classUnderTest.getName2());
	}

	@Test
	public void testName34()
	{
		classUnderTest.setName3(value);
		assertEquals(value, classUnderTest.getName3());
		classUnderTest.setName4(value);
		assertEquals(value, classUnderTest.getName4());
		classUnderTest.setSecondName(value);
		assertEquals(value, classUnderTest.getSecondName());
	}


	@Test
	public void testPoBox()
	{
		assertFalse(classUnderTest.poBox_X);
		classUnderTest.setPoBox(value);
		assertTrue(classUnderTest.poBox_X);
		assertEquals(value, classUnderTest.getPoBox());
	}

	@Test
	public void testMiddleName()
	{
		assertFalse(classUnderTest.middleName_X);
		classUnderTest.setMiddleName(value);
		assertTrue(classUnderTest.middleName_X);
		assertEquals(value, classUnderTest.getMiddleName());
	}

	@Test
	public void testPostlCod1()
	{
		assertFalse(classUnderTest.postlCod1_X);
		classUnderTest.setPostlCod1(value);
		assertTrue(classUnderTest.postlCod1_X);
		assertEquals(value, classUnderTest.getPostlCod1());
	}

	@Test
	public void testPostlCod2()
	{
		assertFalse(classUnderTest.postlCod2_X);
		classUnderTest.setPostlCod2(value);
		assertTrue(classUnderTest.postlCod2_X);
		assertEquals(value, classUnderTest.getPostlCod2());
	}

	@Test
	public void testPrefix1Key()
	{
		assertFalse(classUnderTest.prefix1Key_X);
		classUnderTest.setPrefix1Key(value);
		assertTrue(classUnderTest.prefix1Key_X);
		assertEquals(value, classUnderTest.getPrefix1Key());
	}

	@Test
	public void testPrefix2Key()
	{
		assertFalse(classUnderTest.prefix2Key_X);
		classUnderTest.setPrefix2Key(value);
		assertTrue(classUnderTest.prefix2Key_X);
		assertEquals(value, classUnderTest.getPrefix2Key());
	}

	@Test
	public void testRegion()
	{
		assertFalse(classUnderTest.region_X);
		classUnderTest.setRegion(value);
		assertTrue(classUnderTest.region_X);
		assertEquals(value, classUnderTest.getRegion());
	}

	@Test
	public void testAddrText()
	{
		classUnderTest.setAddrText(value);
		assertEquals(value, classUnderTest.getAddrText());
	}

	@Test
	public void testAddressPartner()
	{
		classUnderTest.setAddressPartner(value);
		assertEquals(value, classUnderTest.getAddressPartner());
	}

	@Test
	public void testAddressString()
	{
		classUnderTest.setAddressString(value);
		assertEquals(value, classUnderTest.getAddressString());
	}

	@Test
	public void testAddrguid()
	{
		classUnderTest.setAddrguid(value);
		assertEquals(value, classUnderTest.getAddrguid());
	}

	@Test
	public void testAddrnum()
	{
		classUnderTest.setAddrnum(value);
		assertEquals(value, classUnderTest.getAddrnum());
	}

	@Test
	public void testBirthName()
	{
		classUnderTest.setBirthName(value);
		assertEquals(value, classUnderTest.getBirthName());
	}

	@Test
	public void testBuilding()
	{
		classUnderTest.setBuilding(value);
		assertEquals(value, classUnderTest.getBuilding());
	}

	@Test
	public void testCategory()
	{
		classUnderTest.setCategory(value);
		assertEquals(value, classUnderTest.getCategory());
	}

	@Test
	public void testCountryISO()
	{
		classUnderTest.setCountryISO(value);
		assertEquals(value, classUnderTest.getCountryISO());
	}

	@Test
	public void testCountryText()
	{
		classUnderTest.setCountryText(value);
		assertEquals(value, classUnderTest.getCountryText());
	}

	@Test
	public void testDeviatingName()
	{
		assertFalse(classUnderTest.isDeviatingName());
		classUnderTest.setDeviatingName(true);
		assertTrue(classUnderTest.isDeviatingName());
	}

	@Test
	public void testDocumentAddress()
	{
		assertFalse(classUnderTest.isDocumentAddress());
		classUnderTest.setDocumentAddress(true);
		assertTrue(classUnderTest.isDocumentAddress());
	}

	@Test
	public void testDialingCode()
	{
		classUnderTest.setDialingCode(value);
		assertEquals(value, classUnderTest.getDialingCode());
	}

	@Test
	public void testStrSuppl1()
	{
		assertFalse(classUnderTest.strSuppl1_X);
		classUnderTest.setStrSuppl1(value);
		assertTrue(classUnderTest.strSuppl1_X);
		assertEquals(value, classUnderTest.getStrSuppl1());
	}

	@Test
	public void testStrSuppl2()
	{
		classUnderTest.setStrSuppl2(value);
		assertEquals(value, classUnderTest.getStrSuppl2());
	}

	@Test
	public void testStrSuppl3()
	{
		classUnderTest.setStrSuppl3(value);
		assertEquals(value, classUnderTest.getStrSuppl3());
	}

	@Test
	public void testEMailSeq()
	{
		classUnderTest.setEmailSeq(value);
		assertEquals(value, classUnderTest.getEmailSeq());
	}

	@Test
	public void testFaxNumberSeq()
	{
		classUnderTest.setFaxNumberSeq(value);
		assertEquals(value, classUnderTest.getFaxNumberSeq());
	}

	@Test
	public void testFloor()
	{
		classUnderTest.setFloor(value);
		assertEquals(value, classUnderTest.getFloor());
	}

	@Test
	public void testFullName()
	{
		classUnderTest.setFullName(value);
		assertEquals(value, classUnderTest.getFullName());
	}

	@Test
	public void testHomeCity()
	{
		classUnderTest.setHomeCity(value);
		assertEquals(value, classUnderTest.getHomeCity());
	}

	@Test
	public void testHouseNo2()
	{
		classUnderTest.setHouseNo2(value);
		assertEquals(value, classUnderTest.getHouseNo2());
	}

	@Test
	public void testHouseNo3()
	{
		classUnderTest.setHouseNo3(value);
		assertEquals(value, classUnderTest.getHouseNo3());
	}

	@Test
	public void testId()
	{
		classUnderTest.setId(value);
		assertEquals(value, classUnderTest.getId());
	}

	@Test
	public void testInitials()
	{
		classUnderTest.setInitials(value);
		assertEquals(value, classUnderTest.getInitials());
	}

	@Test
	public void testLocation()
	{
		classUnderTest.setLocation(value);
		assertEquals(value, classUnderTest.getLocation());
	}

	@Test
	public void testNickName()
	{
		classUnderTest.setNickName(value);
		assertEquals(value, classUnderTest.getNickName());
	}

	@Test
	public void testOrigin()
	{
		classUnderTest.setOrigin(value);
		assertEquals(value, classUnderTest.getOrigin());
	}

	@Test
	public void testPcodeExt()
	{
		classUnderTest.setPcode1Ext(value);
		assertEquals(value, classUnderTest.getPcode1Ext());
		classUnderTest.setPcode2Ext(value);
		assertEquals(value, classUnderTest.getPcode2Ext());
		classUnderTest.setPcode3Ext(value);
		assertEquals(value, classUnderTest.getPcode3Ext());
	}

	@Test
	public void testPersonNumber()
	{
		classUnderTest.setPersonNumber(value);
		assertEquals(value, classUnderTest.getPersonNumber());
	}

	@Test
	public void testPoBoxes()
	{
		classUnderTest.setPoBoxCit(value);
		assertEquals(value, classUnderTest.getPoBoxCit());
		classUnderTest.setPoBoxCtry(value);
		assertEquals(value, classUnderTest.getPoBoxCtry());
		classUnderTest.setPoBoxReg(value);
		assertEquals(value, classUnderTest.getPoBoxReg());
		classUnderTest.setPoCtryISO(value);
		assertEquals(value, classUnderTest.getPoCtryISO());
		classUnderTest.setPoWoNo(value);
		assertEquals(value, classUnderTest.getPoWoNo());
	}

	@Test
	public void testPostlCod3()
	{
		classUnderTest.setPostlCod3(value);
		assertEquals(value, classUnderTest.getPostlCod3());
	}

	@Test
	public void testPrefix()
	{
		classUnderTest.setPrefix1(value);
		assertEquals(value, classUnderTest.getPrefix1());
		classUnderTest.setPrefix2(value);
		assertEquals(value, classUnderTest.getPrefix2());
	}

	@Test
	public void testRegionTexts()
	{
		classUnderTest.setRegionText15(value);
		assertEquals(value, classUnderTest.getRegionText15());
		classUnderTest.setRegionText50(value);
		assertEquals(value, classUnderTest.getRegionText50());
	}

	@Test
	public void testBeanAttribs()
	{
		classUnderTest.setRoomNo(value);
		assertEquals(value, classUnderTest.getRoomNo());
		classUnderTest.setTel1NumbrSeq(value);
		assertEquals(value, classUnderTest.getTel1NumbrSeq());
		classUnderTest.setTelmob1Seq(value);
		assertEquals(value, classUnderTest.getTelmob1Seq());
		classUnderTest.setTitleAca1(value);
		assertEquals(value, classUnderTest.getTitleAca1());
		classUnderTest.setType(value);
		assertEquals(value, classUnderTest.getType());
	}

	@Test
	public void testStdAddress()
	{
		classUnderTest.setStdAddress(true);
		assertTrue(classUnderTest.stdAddress);
		classUnderTest.setStdAddress_X(true);
		assertTrue(classUnderTest.stdAddress_X);
	}

	@Test
	public void testStreet()
	{
		assertFalse(classUnderTest.street_X);
		classUnderTest.setStreet(value);
		assertTrue(classUnderTest.street_X);
		assertEquals(value, classUnderTest.getStreet());
	}

	@Test
	public void testTel1Ext()
	{
		assertFalse(classUnderTest.tel1Ext_X);
		classUnderTest.setTel1Ext(value);
		assertTrue(classUnderTest.tel1Ext_X);
		assertEquals(value, classUnderTest.getTel1Ext());
	}

	@Test
	public void testTelmob1()
	{
		assertFalse(classUnderTest.telmob1_X);
		classUnderTest.setTelmob1(value);
		assertTrue(classUnderTest.telmob1_X);
		assertEquals(value, classUnderTest.getTelmob1());
	}

	@Test
	public void testTitle()
	{
		assertFalse(classUnderTest.title_X);
		classUnderTest.setTitle(value);
		assertTrue(classUnderTest.title_X);
		assertEquals(value, classUnderTest.getTitle());
	}

	@Test
	public void testTitleAca1Key()
	{
		assertFalse(classUnderTest.titleAca1Key_X);
		classUnderTest.setTitleAca1Key(value);
		assertTrue(classUnderTest.titleAca1Key_X);
		assertEquals(value, classUnderTest.getTitleAca1Key());
	}

	@Test
	public void testTitleKey()
	{
		assertFalse(classUnderTest.titleKey_X);
		classUnderTest.setTitleKey(value);
		assertTrue(classUnderTest.titleKey_X);
		assertEquals(value, classUnderTest.getTitleKey());
	}

	@Test
	public void testTitleSupplKey()
	{
		classUnderTest.setTitleSupplKey_X(false);
		assertFalse(classUnderTest.titleSupplKey_X);
		classUnderTest.setTitleSupplKey(value);
		assertTrue(classUnderTest.titleSupplKey_X);
		assertEquals(value, classUnderTest.getTitleSupplKey());
	}
}
