/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.customergroups
import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import spock.lang.Unroll
/**
 * @author monika.morawiecka
 *
 */
@Unroll
@ManualTest
class CustomerGroupsTest extends AbstractUserTest {

	def "Customer manager tries to create customer group without uid  when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"Validation error is returned"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].reason == 'missing'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'uid'
			data.errors[0].message == 'This field is required.'
		}

		where:
		requestFormat | responseFormat | postBody
		JSON | JSON | "{\"name\" : \"aaa\"}"
		XML | XML | "<userGroup><name>aaa</name></userGroup>"
	}

	def "Customer manager tries to add member without uid when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, a group and a customer"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)

		when: "customer manager wants to add customer to customer group without user id"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body: postBody,
				requestContentType : requestFormat)

		then:"ValidationError is returned"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].reason == 'invalid'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'members'
			data.errors[0].message == "Field 'uid' is required."
		}

		where:
		requestFormat | responseFormat | postBody
		JSON | JSON | "{\"members\" : {\"name\": \"testUserWithoutId\"}}"
		XML | XML | "<memberList><members><name>testUserWithoutId</name></members></memberList>"
	}

	def "Customer manager tries to create customer group with incorrect member when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"Validation error is returned"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].reason == 'invalid'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'userGroup'
			data.errors[0].message == "Field 'members.uid' is required."
		}

		where:
		requestFormat | responseFormat | postBody
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\",\"name\" : \"aaa\",\"members\" : {\"name\": \"testUserWithoutId\"}}"
		XML | XML | "<userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid><name>aaa</name><members><name>testUserWithoutId</name></members></userGroup>"
	}

	def "Customer manager creates customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){ status == SC_CREATED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': "" + "1_" + System.currentTimeMillis() + "_customerGroup", 'localizedName' :'aaa']
		URLENC | XML | ['groupId': "" + "2_" + System.currentTimeMillis() + "_customerGroup", 'localizedName' :'aaa']
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\",\"name\" : \"aaa\"}"
		XML | XML | "<userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid><name>aaa</name></userGroup>"
	}

	def "Customer manager creates customer group with members when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager and a customer"
		def customer = registerCustomerWithTrustedClient(restClient, JSON)
		def groupId = "${System.currentTimeMillis()}_customerGroup"
		authorizeCustomerManager(restClient)
		postBody = postBody.replace("customer.id", customer.id).replace("group.id", groupId)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)
		and: "retrieves customer group"
		def group = getUserGroup(restClient, groupId,responseFormat)

		then:"user group with members is created"
		with (response){
			println data;
			status == SC_CREATED
		}
		with (group){
			uid == groupId
			membersCount == 1
			members[0].uid == customer.id
		}

		where:
		requestFormat | responseFormat | postBody
		JSON | JSON | "{\"uid\" : \"group.id\",\"name\" : \"aaa\",\"members\" : {\"uid\": \"customer.id\"}}"
		XML | XML | "<userGroup><uid>group.id</uid><name>aaa</name><members><uid>customer.id</uid></members></userGroup>"
	}

	def "Customer manager creates customer group without a name when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){ status == SC_CREATED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': "" + "1_" + System.currentTimeMillis() + "_customerGroup"]
		URLENC | XML | ['groupId': "" + "2_" + System.currentTimeMillis() + "_customerGroup"]
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid></userGroup>"
	}

	def "Customer manager creates customer group with localized name when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				query :['lang':'de'],
				body : postBody,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){ status == SC_CREATED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': "" + "1_" + System.currentTimeMillis() + "_customerGroup", 'localizedName' :'aaa']
		URLENC | XML | ['groupId': "" + "2_" + System.currentTimeMillis() + "_customerGroup", 'localizedName' :'aaa']
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\",\"name\" : \"aaa\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid><name>aaa</name></userGroup>"
	}

	def "Customer manager creates customer group with duplicate UID when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager and existing customer group"
		authorizeCustomerManager(restClient)
		def groupId = createUserGroup(restClient, null, JSON)

		if(requestFormat.equals(URLENC))
			postBody.put("groupId", groupId)
		else
			postBody = postBody.replace("groupId", groupId)

		when: "customer manager wants to create user group with duplicate ID"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ModelSavingError'
			data.errors[0].message.toString().contains('ambiguous unique keys')
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': '', 'localizedName' :'aaa']
		URLENC | XML | ['groupId': '', 'localizedName' :'aaa']
		JSON | JSON | "{\"uid\" : \"groupId\",\"name\" : \"aaa\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><userGroup><uid>groupId</uid><name>aaa</name></userGroup>"
	}

	def "Client tries to create customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized client"
		authorizeClient(restClient)

		when: "client wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"he is forbidden to do so"
		with (response){
			status == SC_UNAUTHORIZED
			isNotEmpty(data.errors)
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': "" + "1_" + System.currentTimeMillis() + "_customerGroup"]
		URLENC | XML | ['groupId': "" + "2_" + System.currentTimeMillis() + "_customerGroup"]
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid></userGroup>"
	}

	def "Customer tries to create customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer "
		registerAndAuthorizeCustomer(restClient, JSON)

		when: "customer wants to create user group"
		def response = restClient.post(
				path : getBasePathWithSite() + "/customergroups",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"he is forbidden to do so"
		with (response){
			status == SC_UNAUTHORIZED
			isNotEmpty(data.errors)
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['groupId': "" + "1_" + System.currentTimeMillis() + "_customerGroup"]
		URLENC | XML | ['groupId': "" + "2_" + System.currentTimeMillis() + "_customerGroup"]
		JSON | JSON | "{\"uid\" : \"3_${System.currentTimeMillis()}_customerGroup\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><userGroup><uid>4_${System.currentTimeMillis()}_customerGroup</uid></userGroup>"
	}

	def "Customer manager adds customer to customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, a group and a customer"
		def customer = registerCustomerWithTrustedClient(restClient, JSON)
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)

		if(requestFormat.equals(URLENC))
			postBody.put("members", customer.id)
		else
			postBody = postBody.replace("customer.id", customer.id)

		when: "customer manager wants to add customer to customer group"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"customer is added to group"
		with (response){ status == SC_OK }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['members': "customer.id"]
		URLENC | XML | ['members': "customer.id"]
		JSON | JSON | '{"members" : {"uid": "customer.id"}}'
		XML | XML | "<memberList><members><uid>customer.id</uid></members></memberList>"
	}

	def "Customer manager adds two customers to customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, a group and and two customers"
		def customer = registerCustomerWithTrustedClient(restClient, JSON)
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)
		def customer1 = registerCustomer(restClient)
		def customer2 = registerCustomer(restClient)

		if(requestFormat.equals(URLENC)){
			postBody = "members=${customer1.id}&members=${customer2.id}"
		}
		else
			postBody = postBody.replace('customer1.id', customer1.id).replace('customer2.id', customer2.id)

		when: "customer manager adds customers to customer group"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		and: "retrieves customer group"
		def group = getUserGroup(restClient, groupID,responseFormat)

		then:"customers are added to group"
		with (response){ status == SC_OK }
		and:
		with (group){
			uid == groupID
			membersCount == 2
			members[0].uid == customer1.id || members[0].uid == customer2.id
			members[1].uid == customer1.id || members[1].uid == customer2.id
		}
		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ''
		URLENC | XML | ''
		JSON | JSON | "{\"members\" : [{\"uid\": \"customer1.id\"},{\"uid\": \"customer2.id\"}]}"
		XML | XML | "<membersList><members><uid>customer1.id</uid></members><members><uid>customer2.id</uid></members></membersList>"
	}

	def "Customer manager adds 0 customers to customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager and a group"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)

		when: "customer manager wants to add 0 customers to customer group (empty list)"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				body : postBody,
				contentType : responseFormat,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == errorType;
			data.errors[0].message == errorMessage
			errorSubject == null | errorSubject == data.errors[0].subject.toString()
		}

		where:
		requestFormat | responseFormat | postBody | errorType | errorMessage | errorSubject
		URLENC | JSON | "" | 'MissingServletRequestParameterError' | "Required List parameter 'members' is not present" | null
		URLENC | XML | "" | 'MissingServletRequestParameterError' | "Required List parameter 'members' is not present" | null
		JSON | JSON | "{}" |'ValidationError' | 'This field is required.' | 'members'
		XML | XML | "<memberList></memberList>" |'ValidationError' | 'This field is required.' |'members'
	}

	def "Non-customer manager adds customer to customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer, a group and a customer"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)
		def customer = registerAndAuthorizeCustomer(restClient, JSON)

		if(requestFormat.equals(URLENC))
			postBody.put("members", customer.id)
		else
			postBody = postBody.replace("customer.id", customer.id)

		when: "customer manager wants to add customer to customer group"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"user group is created in the system"
		with (response){ status == SC_UNAUTHORIZED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['members': "customer.id"]
		URLENC | XML | ['members': "customer.id"]
		JSON | JSON | '{"members" : {"uid": "customer.id"}}'
		XML | XML | "<memberList><members><uid>customer.id</uid></members></memberList>"
	}

	def "Customer gets all his groups : #format"(){
		given: "a customer who belongs to user groups"
		def customer = registerCustomerWithTrustedClient(restClient, format)
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, format)
		addToGroup(restClient, groupID, customer.id, format)
		authorizeCustomer(restClient, customer)

		when: "customer requests groups that he belongs to"
		def response = restClient.get(
				path : getBasePathWithSite() + "/users/" + customer.id +"/customergroups",
				contentType : format,
				query : ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then:"he receives requested list"
		with (response){
			status == SC_OK
			isNotEmpty(data.userGroups)
			data.userGroups.size() == 2
		}

		where:
		format << [XML, JSON]
	}

	def "Customer gets groups of another customer : #format"(){
		given: "two customers, one logged in"
		def customer1 = registerCustomerWithTrustedClient(restClient, format)
		def customer2 = registerAndAuthorizeCustomer(restClient, format)

		when: "logged in customer requests groups that another customer belongs to"
		def response = restClient.get(
				path : getBasePathWithSite() + "/users/" + customer1.id +"/customergroups",
				contentType : format,
				query : ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then:"his request is denied"
		with (response){ status == SC_FORBIDDEN }

		where:
		format << [XML, JSON]
	}

	def "Customer manager gets all groups for existing customer : #format"(){
		given: "a customer who belongs to user groups and logged in customer manager"
		def customer = registerCustomerWithTrustedClient(restClient, format)
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, format)
		addToGroup(restClient, groupID, customer.id, format)

		when: "customer manager requests groups that customer belongs to"
		def response = restClient.get(
				path : getBasePathWithSite() + "/users/" + customer.id +"/customergroups",
				contentType : format,
				query : ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then:"he receives requested list"
		with (response){
			status == SC_OK
			isNotEmpty(data.userGroups)
			data.userGroups.size() == 2
		}

		where:
		format << [XML, JSON]
	}

	def "Non-customer tries to get all his groups : #format"() {
		given: "a user who is not a customer"
		def admin = [
			"id" : "admin",
			"password" : "nimda"
		]
		authorizeCustomer(restClient, admin)

		when: "such user requests customer groups"
		def response = restClient.get(
				path : getBasePathWithSite() + "/users/" + admin.id +"/customergroups",
				contentType : format,
				query : ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then: "he is rejected"
		with (response){ status == SC_FORBIDDEN }

		where:
		format << [XML, JSON]
	}

	def "Customer manager gets all customer groups : #format"(){
		given: "an authorized customer manager"
		authorizeCustomerManager(restClient)

		when: "customer manager requests all customer groups"
		def response = restClient.get(
				path : getBasePathWithSite() + "/customergroups",
				contentType : format,
				requestContentType : URLENC)

		then:"he is able to retrive the data"
		with (response){
			status == SC_OK
			isNotEmpty(data.userGroups)
		}

		where:
		format << [XML, JSON]
	}

	def "Non-customer manager gets all customer groups : #format"(){
		given: "an authorized non - customer manager"
		registerAndAuthorizeCustomer(restClient, format)

		when: "user requests all customer groups"
		def response = restClient.get(
				path : getBasePathWithSite() + "/customergroups",
				contentType : format,
				requestContentType : URLENC)

		then:"he is not able to retrive the data"
		with (response){ status == SC_UNAUTHORIZED }

		where:
		format << [XML, JSON]
	}

	def "Customer manager removes customer from group : #format"(){
		given: "customer group with two members and authorized customer manager"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, format)
		def group = getUserGroup(restClient, groupID, format)
		def customer1 = registerCustomer(restClient)
		def customer2 = registerCustomer(restClient)
		addToGroup(restClient, groupID, customer1.id, format)
		addToGroup(restClient, groupID, customer2.id, format)

		when: "customer manager removes customer from customer groups"
		def response = restClient.delete(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members/" + customer2.id,
				contentType : format,
				requestContentType : URLENC)

		and: "retrieves customer group"
		group = getUserGroup(restClient, groupID, format)

		then: "customer is removed from group"
		with(response){ status == SC_OK }

		and:
		with (group){
			members.size() == 1
		}

		where:
		format << [XML, JSON]
	}

	def "Customer manager replace members of customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, a group with two customers"
		def customer = registerCustomerWithTrustedClient(restClient, JSON)
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, responseFormat)
		def customer1 = registerCustomer(restClient)
		addToGroup(restClient, groupID, customer1.id, responseFormat)
		def customer2 = registerCustomer(restClient)
		addToGroup(restClient, groupID, customer2.id, responseFormat)
		def customer3 = registerCustomer(restClient)

		if(requestFormat.equals(URLENC)){
			postBody = "members=${customer2.id}&members=${customer3.id}"
		}
		else
			postBody = postBody.replace('customer2.id', customer2.id).replace('customer3.id', customer3.id)

		when: "customer manager replace members of customer group"
		def response = restClient.put(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		and: "retrieves customer group"
		def group = getUserGroup(restClient, groupID, responseFormat)

		then:"members of customer group are changed"
		with (response){ status == SC_OK }
		and :
		with (group){
			uid == groupID
			membersCount == 2
			members[0].uid == customer2.id || members[0].uid == customer3.id
			members[1].uid == customer2.id || members[1].uid == customer3.id
		}
		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | 'members=customer2.id&members=customer3.id'
		URLENC | XML | 'members=customer2.id&members=customer3.id'
		JSON | JSON | "{\"members\" : [{\"uid\": \"customer2.id\"},{\"uid\": \"customer3.id\"}]}"
		XML | XML | "<membersList><members><uid>customer2.id</uid></members><members><uid>customer3.id</uid></members></membersList>"
	}

	def "Customer manager tries to set memebers without uid when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, and group"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)

		when: "customer manager tries to set members without uid"
		def response = restClient.put(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body: postBody,
				requestContentType : requestFormat)

		then:"ValidationError is returned"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].reason == 'invalid'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'members'
			data.errors[0].message == "Field 'uid' is required."
		}

		where:
		requestFormat | responseFormat | postBody
		JSON | JSON | "{\"members\" : {\"name\": \"testUserWithoutId\"}}"
		XML | XML | "<memberList><members><name>testUserWithoutId</name></members></memberList>"
	}

	def "Customer manager tries to set memebers with not existing uid when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager, and group"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)

		when: "customer manager tries to set members without uid"
		def response = restClient.put(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body: postBody,
				requestContentType : requestFormat)

		then:"ValidationError is returned"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].message.toString().contains("User 'notExistingUser' doesn't exist or you have no privileges")
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | 'members=notExistingUser'
		URLENC | XML | 'members=notExistingUser'
		JSON | JSON | "{\"members\" : {\"uid\": \"notExistingUser\"}}"
		XML | XML | "<memberList><members><uid>notExistingUser</uid></members></memberList>"
	}


	def "Customer manager set empty members list for customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer manager and a group"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)
		def customer1 = registerCustomer(restClient)
		addToGroup(restClient, groupID, customer1.id, responseFormat)

		when: "customer manager set empty member list for customer group"
		def response = restClient.put(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				body : postBody,
				contentType : responseFormat,
				requestContentType : requestFormat)

		and: "retrieves customer group"
		def group = getUserGroup(restClient, groupID, responseFormat)

		then:"empty members list is set"
		with (response){ status == SC_OK }
		with (group){ membersCount==0 }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ""
		URLENC | XML | ""
		JSON | JSON | "{}"
		XML | XML | "<memberList></memberList>"
	}

	def "Non-customer manager tries to set members of customer group when request: #requestFormat and response: #responseFormat"(){
		given: "an authorized customer, a group and a customer"
		authorizeCustomerManager(restClient)
		def groupID = createUserGroup(restClient, null, JSON)
		def customer = registerAndAuthorizeCustomer(restClient, JSON)

		if(requestFormat.equals(URLENC))
			postBody.put("members", customer.id)
		else
			postBody = postBody.replace("customer.id", customer.id)

		when: "non-customer manager wants to set members of customer group"
		def response = restClient.patch(
				path : getBasePathWithSite() + "/customergroups/" + groupID + "/members",
				contentType : responseFormat,
				body : postBody,
				requestContentType : requestFormat)

		then:"error is returned"
		with (response){ status == SC_UNAUTHORIZED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | JSON | ['members': "customer.id"]
		URLENC | XML | ['members': "customer.id"]
		JSON | JSON | '{"members" : {"uid": "customer.id"}}'
		XML | XML | "<memberList><members><uid>customer.id</uid></members></memberList>"
	}
}
