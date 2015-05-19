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
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
/**
 *
 */
@Category(CollectOutputFromTest.class)
@ManualTest
class CustomerGroupTests extends BaseWSTest {

	def randomUID = null;
	def access_token = null;

	@Before
	public void before() {
		randomUID = System.currentTimeMillis()
	}

	@Test
	public void testCreateCustomerGroup() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		String postBody = "uid=${randomUID}&localizedName=bbbbbb"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody,null, access_token );
	}

	@Test
	public void testCreateCustomerGroupJSON() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		String postBody = "uid=${randomUID}&localizedName=bbbbbb"
		testUtil.getSecureConnection("/customergroups", "POST", "JSON", HttpURLConnection.HTTP_CREATED, postBody,null, access_token );
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	public void testCreateCustomerGroupWithoutName() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		String postBody = "uid=${randomUID}_withoutName"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody, null, access_token );
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	public void testCreateCustomerGroupAndSpecifyLang() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		String postBody = "uid=${randomUID}_withLangDe&localizedName=bbbbbb&lang=de"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody, null, access_token );
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	public void testCreateCustomerGroupWithDuplicateId() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		String postBody = "uid=${randomUID}_duplicated"
		def con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody, null,access_token );
		con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token);
		def error = con.errorStream.text;
		println error
		def response = new XmlSlurper().parseText(error)
		assert response.error.type == 'ModelSavingError'
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	public void testTryToCreateCustomerGroupAsNotAdminUser() {
		CustomerTests customerTests = new CustomerTests();
		def uid = customerTests.registerUser();
		access_token = testUtil.getAccessToken(uid, CustomerTests.password);
		String postBody = "uid=${randomUID}&localizedName=bbbbbb"
		def con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_UNAUTHORIZED, postBody, null,access_token );
	}

	@Test
	public void testAssignUserToCustomerGroup() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		access_token = testUtil.getAccessToken("customermanager", "1234")

		def userUid1 = "${randomUID}_useruid_1@hybris.de"
		def userUid2 = "${randomUID}_useruid_2@hybris.de"
		def customerGroup1="${randomUID}_customerGroup"
		// add first user
		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		// add second user
		body = "login=${userUid2}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)

		// add customer group
		body = "uid=${customerGroup1}"
		con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null,access_token );

		def putBody = "member=${userUid1}"
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null,access_token );

		putBody = "member=${userUid2}"
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "JSON", HttpURLConnection.HTTP_OK, putBody, null,access_token );

		// add one more time user to the same group - no error expected
		putBody = "member=${userUid1}"
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null,access_token );

		// add 0 memebers to group - error expected
		putBody = ""
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_BAD_REQUEST, putBody, null,access_token );

		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}?options=MEMBERS", "GET", "XML", HttpURLConnection.HTTP_OK, null, null,access_token );
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.membersCount == 2
		assert response.members.find{it['@uid'] == '${userUid1}'} != null
		assert response.members.find{it['@uid'] == '${userUid2}'} != null
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	public void testTryToAssignUserToCustomerGroupAsNotAdminUser() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		access_token = testUtil.getAccessToken("customermanager", "1234")


		def userUid1 = "${randomUID}_useruid_1@hybris.de"
		def customerGroup1="${randomUID}_customerGroup"
		// add user
		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)

		def acces_token_for_UserUid1 = testUtil.getAccessToken(userUid1,"password")

		// add customer group
		body = "uid=${customerGroup1}"
		con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null,access_token );

		def putBody = "member=${userUid1}"
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_UNAUTHORIZED, putBody, null,acces_token_for_UserUid1 );
	}

	@Test
	public void testRemoveUserFromCustomerGroup() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		access_token = testUtil.getAccessToken("customermanager", "1234")

		def userUid1 = "${randomUID}_useruid_1@hybris.de"
		def userUid2 = "${randomUID}_useruid_2@hybris.de"

		def customerGroup1="${randomUID}_customerGroup"
		// add users
		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		// add users
		body = "login=${userUid2}&password=password&firstName=firstName2&lastName=lastName2&titleCode=dr"
		con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		// add customer group
		body = "uid=${customerGroup1}"
		con = testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null,access_token );

		def putBody = "member=${userUid1}&member=${userUid2}"
		// add member
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null,access_token );
		// and remove member
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members/${userUid1}" , "DELETE", "XML", HttpURLConnection.HTTP_OK, null, null,access_token );
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members/${userUid1}" , "DELETE", "JSON", HttpURLConnection.HTTP_OK, null, null,access_token );
		// remove one more time user from the same group - no error expected
		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members/${userUid1}" , "DELETE", "XML", HttpURLConnection.HTTP_OK, null, null,access_token );
		//check if ROLE_ADMINGROUP is required
		CustomerTests customerTests = new CustomerTests();
		def uid = customerTests.registerUser();
		def simpleUserAccessToken = testUtil.getAccessToken(uid, CustomerTests.password)

		con = testUtil.getSecureConnection("/customergroups/${customerGroup1}/members/${userUid1}" , "DELETE", "XML", HttpURLConnection.HTTP_UNAUTHORIZED, null, null,simpleUserAccessToken );
	}

	@Test
	public void testGetAllCustomerGroupsXML() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		def con = testUtil.getSecureConnection("/customergroups","GET","XML", HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con);
		def sizeBeforeWithoutParam = response.userGroup.size()
		def customerGroupName = "customergroup"
		//add new customer group
		def uid = "${randomUID}" + "_customerGroup"
		def body = "uid=${uid}"
		con = testUtil.getSecureConnection("/customergroups", "POST","XML",HttpURLConnection.HTTP_CREATED,body, null, access_token)
		con = testUtil.getSecureConnection("/customergroups","GET","XML", HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con,true);
		assert sizeBeforeWithoutParam + 1 == response.userGroup.size()
		assert response.userGroup.find {it['@uid'] == uid} != null
		assert response.userGroup.find {it['@uid'] == 'customergroup'} != null
		assert response.pageSize != null
		assert response.pageSize == Integer.MAX_VALUE
		assert response.totalNumber != null
		assert response.totalNumber == sizeBeforeWithoutParam + 1
		assert response.numberOfPages != null
		assert response.numberOfPages == 1
		assert response.currentPage != null
		assert response.currentPage == 0


		def groupsNo = sizeBeforeWithoutParam + 1

		int pageSize = 3
		int maxPages = (int)Math.ceil((double)groupsNo/(double)pageSize)

		for(int currPage = 0; currPage < maxPages ; currPage++) {
			con = testUtil.getSecureConnection("/customergroups?currentPage=${currPage}&pageSize=${pageSize}","GET","XML", HttpURLConnection.HTTP_OK, null, null, access_token)
			response = testUtil.verifiedXMLSlurper(con,true);
			assert response.userGroup.size() <= pageSize
			assert response.pageSize != null
			assert response.pageSize == pageSize
			assert response.totalNumber != null
			assert response.totalNumber == sizeBeforeWithoutParam + 1
			assert response.numberOfPages != null
			assert response.numberOfPages == (int)maxPages
			assert response.currentPage != null
			assert response.currentPage == currPage
		}
	}

	@Test
	public void testGetAllCustomerGroupsJSON() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		def con = testUtil.getSecureConnection("/customergroups","GET","JSON", HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con,true);
		def sizeBeforeWithoutParam = response.userGroups.size()
		def customerGroupName = "customergroup"
		//add new customer group
		def uid = "${randomUID}" + "_customerGroup"
		def body = "uid=${uid}"
		con = testUtil.getSecureConnection("/customergroups", "POST","XML",HttpURLConnection.HTTP_CREATED,body, null, access_token)
		con = testUtil.getSecureConnection("/customergroups","GET","JSON", HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con,true);
		println "response.userGroups[0].size(): " +  response.userGroups.size()
		assert sizeBeforeWithoutParam + 1 == response.userGroups.size()
		response.userGroups.each { println it.uid }
		assert response.userGroups.find {it.uid == uid} != null
		assert response.userGroups.find {it.uid == 'customergroup'} != null
		assert response.pageSize != null
		assert response.pageSize == Integer.MAX_VALUE
		assert response.totalNumber != null
		assert response.totalNumber == sizeBeforeWithoutParam + 1
		assert response.numberOfPages != null
		assert response.numberOfPages == 1
		assert response.currentPage != null
		assert response.currentPage == 0


		def groupsNo = sizeBeforeWithoutParam + 1

		int pageSize = 3
		int maxPages = (int)Math.ceil((double)groupsNo/(double)pageSize)

		for(int currPage = 0; currPage < maxPages ; currPage++) {
			con = testUtil.getSecureConnection("/customergroups?currentPage=${currPage}&pageSize=${pageSize}","GET","JSON", HttpURLConnection.HTTP_OK, null, null, access_token)
			response = testUtil.verifiedJSONSlurper(con,true);
			assert response.userGroups[0].size() <= pageSize
			assert response.pageSize != null
			assert response.pageSize == pageSize
			assert response.totalNumber != null
			assert response.totalNumber == sizeBeforeWithoutParam + 1
			assert response.numberOfPages != null
			assert response.numberOfPages == (int)maxPages
			assert response.currentPage != null
			assert response.currentPage == currPage
		}
	}

	@Test
	public void testGetSpecifiedGroupXML() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		def uid = "${randomUID}" + "_customerGroup"
		def body = "uid=${uid}"
		def con = testUtil.getSecureConnection("/customergroups", "POST","XML",HttpURLConnection.HTTP_CREATED,body, null, access_token)
		con = testUtil.getSecureConnection("/customergroups/${uid}","GET","XML", HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con,true);
		assert response.uid == "${uid}"
	}

	@Test
	public void testGetSpecifiedGroupJSON() {
		access_token = testUtil.getAccessToken("customermanager", "1234")
		def uid = "${randomUID}" + "_customerGroup"
		def body = "uid=${uid}"
		def con = testUtil.getSecureConnection("/customergroups", "POST","XML",HttpURLConnection.HTTP_CREATED,body, null, access_token)
		con = testUtil.getSecureConnection("/customergroups/${uid}","GET","JSON", HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con,true);
		assert response.uid == "${uid}"
	}

	@Test
	public void testTryGetSubGroupsWithoutRequiredRole()	{
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def userUid1 = "${randomUID}_useruid_1@hybris.de"
		def customerGroupName = "customergroups"
		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		def simpleUserAccessToken = testUtil.getAccessToken(userUid1, "password")
		con = testUtil.getSecureConnection("/customergroups/${customerGroupName}","GET","JSON", HttpURLConnection.HTTP_UNAUTHORIZED, null, null, simpleUserAccessToken)
		con = testUtil.getSecureConnection("/customergroups","GET","JSON", HttpURLConnection.HTTP_UNAUTHORIZED, null, null, simpleUserAccessToken)

	}
}
