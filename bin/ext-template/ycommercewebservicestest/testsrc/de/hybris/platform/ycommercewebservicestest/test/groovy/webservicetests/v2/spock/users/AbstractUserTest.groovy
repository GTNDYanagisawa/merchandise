/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

/**
 *
 *
 */
abstract class AbstractUserTest extends AbstractSpockFlowTest {

	protected static final PASSWORD = '12341234'
	protected static final GOOD_ADDRESS_DE = [
			'titleCode': 'mrs',
			'firstName': 'Jane',
			'lastName': 'Smith',
			'line1': 'Nymphenburger Str. 86 - Maillingerstrasse',
			'line2': 'pietro 2',
			'postalCode': '80331',
			'town': 'Muenchen',
			'country.isocode': 'DE'
	]
	protected static final GOOD_ADDRESS_DE_JSON = "{ \"titleCode\" : \"mrs\",\"firstName\" : \"Jane\",\"lastName\" : \"Smith\",\"line1\" : \"Nymphenburger Str. 86 - Maillingerstrasse\",\"line2\" : \"pietro 2\",\"postalCode\" : \"80331\",\"town\" : \"Muenchen\",\"country\": {\"isocode\": \"DE\"}}"
	protected static final GOOD_ADDRESS_DE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><address><titleCode>mrs</titleCode><firstName>Jane</firstName><lastName>Smith</lastName><line1>Nymphenburger Str. 86 - Maillingerstrasse</line1><line2>pietro 2</line2><postalCode>80331</postalCode><town>Muenchen</town><country><isocode>DE</isocode></country></address>"

	protected static final GOOD_ADDRESS_US = [
			'titleCode': 'mrs',
			'firstName': 'Jane',
			'lastName': 'Smith',
			'line1': '20 North Wacker Drive',
			'line2': '29th Floor',
			'postalCode': '60606',
			'town': 'Chicago',
			'country.isocode': 'US'
	]

	protected static final BAD_ADDRESS_DE = [
			'town': 'Muenchen',
			'country.isocode': 'DE'
	]
	protected static final BAD_ADDRESS_DE_JSON = "{ \"town\" : \"Muenchen\",\"country\": {\"isocode\": \"DE\"}}"
	protected static final BAD_ADDRESS_DE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><address><town>Muenchen</town><country><isocode>DE</isocode></country></address>"

	protected static final BAD_LINE1_ADDRESS_DE = [
			'titleCode': 'mrs',
			'firstName': 'Jane',
			'lastName': 'Smith',
			'line1': '12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452',
			'line2': 'pietro 2',
			'postalCode': '80331',
			'town': 'Muenchen',
			'country.isocode': 'DE'
	]

	protected static final BAD_LINE1_ADDRESS_DE_JSON = "{ \"titleCode\" : \"mrs\",\"firstName\" : \"Jane\",\"lastName\" : \"Smith\",\"line1\" : \"12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452\",\"line2\" : \"pietro 2\",\"postalCode\" : \"80331\",\"town\" : \"Muenchen\",\"country\": {\"isocode\": \"DE\"}}"
	protected static final BAD_LINE1_ADDRESS_DE_XML = "<address><titleCode>mrs</titleCode><firstName>Jane</firstName><lastName>Smith</lastName><line1>12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452</line1><line2>pietro 2</line2><postalCode>80331</postalCode><town>Muenchen</town><country><isocode>DE</isocode></country></address>"

	protected static final BAD_LINE2_ADDRESS_DE = [
			'titleCode': 'mrs',
			'firstName': 'Jane',
			'lastName': 'Smith',
			'line1': 'Nymphenburger Str. 86 - Maillingerstrasse',
			'line2': '12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452',
			'postalCode': '80331',
			'town': 'Muenchen',
			'country.isocode': 'DE'
	]

	protected static final BAD_LINE2_ADDRESS_DE_JSON = "{ \"titleCode\" : \"mrs\",\"firstName\" : \"Jane\",\"lastName\" : \"Smith\",\"line1\" : \"Nymphenburger Str. 86 - Maillingerstrasse\",\"line2\" : \"12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452\",\"postalCode\" : \"80331\",\"town\" : \"Muenchen\",\"country\": {\"isocode\": \"DE\"}}"
	protected static final BAD_LINE2_ADDRESS_DE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><address><titleCode>mrs</titleCode><firstName>Jane</firstName><lastName>Smith</lastName><line1>Nymphenburger Str. 86 - Maillingerstrasse</line1><line2>12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452</line2><postalCode>80331</postalCode><town>Muenchen</town><country><isocode>DE</isocode></country></address>"


	protected static final ADDRESS_TO_CORRECT = [
			'titleCode': 'mrs',
			'firstName': 'Jane',
			'lastName': 'Smith',
			'line1': 'Nymphenburger Str. 86 - Maillingerstrasse',
			'postalCode': '80331',
			'town': 'review',
			'country.isocode': 'DE'
	]

	protected static final ADDRESS_TO_CORRECT_JSON = "{ \"titleCode\" : \"mrs\",\"firstName\" : \"Jane\",\"lastName\" : \"Smith\",\"line1\" : \"Nymphenburger Str. 86 - Maillingerstrasse\",\"postalCode\" : \"80331\",\"town\" : \"review\",\"country\": {\"isocode\": \"DE\"}}"
	protected static final ADDRESS_TO_CORRECT_XML = "<address><titleCode>mrs</titleCode><firstName>Jane</firstName><lastName>Smith</lastName><line1>Nymphenburger Str. 86 - Maillingerstrasse</line1><postalCode>80331</postalCode><town>review</town><country><isocode>DE</isocode></country></address>"

	protected static final DEFAULT_PAYMENT = [
			"accountHolderName": "John Doe",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "01",
			"expiryYear": "2117",
			"defaultPaymentInfo": true,
			"saved": true,
			"billingAddress.titleCode": "mr",
			"billingAddress.firstName": "John",
			"billingAddress.lastName": "Doe",
			"billingAddress.line1": "test1",
			"billingAddress.line2": "test2",
			"billingAddress.postalCode": "12345",
			"billingAddress.town": "somecity",
			"billingAddress.country.isocode": "DE"
	]
	protected static final String DEFAULT_PAYMENT_JSON = "{\"accountHolderName\" : \"John Doe\", \"cardNumber\" : \"4111111111111111\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"01\", \"expiryYear\" : \"2117\", \"defaultPayment\" : true, \"saved\" : true,\"billingAddress\" : { \"titleCode\" : \"mr\", \"firstName\" : \"John\", \"lastName\" : \"Doe\", \"line1\" : \"test1\", \"line2\" : \"test2\", \"postalCode\" : \"12345\", \"town\" : \"somecity\",\"country\":{\"isocode\" : \"DE\"}}}";
	protected static final String DEFAULT_PAYMENT_XML = "<paymentDetails><accountHolderName>John Doe</accountHolderName><cardNumber>4111111111111111</cardNumber><cardType><code>visa</code></cardType><expiryMonth>1</expiryMonth><expiryYear>2117</expiryYear><defaultPayment>true</defaultPayment><saved>true</saved><billingAddress><firstName>John</firstName><lastName>Doe</lastName><titleCode>mr</titleCode><country><isocode>DE</isocode></country><postalCode>12345</postalCode><town>somecity</town><line1>test1</line1><line2>test2</line2></billingAddress></paymentDetails>"


	protected static final UNSAVED_PAYMENT = [
			"accountHolderName": "John Doe",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "01",
			"expiryYear": "2017",
			"defaultPaymentInfo": false,
			"saved": false,
			"billingAddress.titleCode": "mr",
			"billingAddress.firstName": "John",
			"billingAddress.lastName": "Doe",
			"billingAddress.line1": "test1",
			"billingAddress.line2": "test2",
			"billingAddress.postalCode": "12345",
			"billingAddress.town": "somecity",
			"billingAddress.country.isocode": "DE"
	]
	protected static final String UNSAVED_PAYMENT_JSON = "{\"accountHolderName\" : \"John Doe\", \"cardNumber\" : \"4111111111111111\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"01\", \"expiryYear\" : \"2117\", \"defaultPayment\" : false, \"saved\" : false,\"billingAddress\" : { \"titleCode\" : \"mr\", \"firstName\" : \"John\", \"lastName\" : \"Doe\", \"line1\" : \"test1\", \"line2\" : \"test2\", \"postalCode\" : \"12345\", \"town\" : \"somecity\",\"country\":{\"isocode\" : \"DE\"}}}";
	protected static final String UNSAVED_PAYMENT_XML = "<paymentDetails><accountHolderName>John Doe</accountHolderName><cardNumber>4111111111111111</cardNumber><cardType><code>visa</code></cardType><expiryMonth>1</expiryMonth><expiryYear>2117</expiryYear><defaultPayment>false</defaultPayment><saved>false</saved><billingAddress><firstName>John</firstName><lastName>Doe</lastName><titleCode>mr</titleCode><country><isocode>DE</isocode></country><postalCode>12345</postalCode><town>somecity</town><line1>test1</line1><line2>test2</line2></billingAddress></paymentDetails>"

	protected static final SECOND_PAYMENT_DEFAULT = [
			"accountHolderName": "Jane Smith",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "05",
			"expiryYear": "2025",
			"defaultPaymentInfo": true,
			"saved": true,
			"billingAddress.titleCode": "mrs",
			"billingAddress.firstName": "Jane",
			"billingAddress.lastName": "Smith",
			"billingAddress.line1": "test1",
			"billingAddress.line2": "test2",
			"billingAddress.postalCode": "12345",
			"billingAddress.town": "somecity",
			"billingAddress.country.isocode": "US"
	]

	protected static final SECOND_PAYMENT_NON_DEFAULT = [
			"accountHolderName": "Jane Smith",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "05",
			"expiryYear": "2117",
			"defaultPaymentInfo": false,
			"saved": true,
			"billingAddress.titleCode": "mrs",
			"billingAddress.firstName": "Jane",
			"billingAddress.lastName": "Smith",
			"billingAddress.line1": "test1",
			"billingAddress.line2": "test2",
			"billingAddress.postalCode": "12345",
			"billingAddress.town": "somecity",
			"billingAddress.country.isocode": "US"
	]
	protected static final String SECOND_PAYMENT_NON_DEFAULT_JSON = "{\"accountHolderName\" : \"Jane Smith\", \"cardNumber\" : \"4111111111111111\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"05\", \"expiryYear\" : \"2117\", \"defaultPayment\" : false, \"saved\" : true, \"billingAddress\" : { \"titleCode\" : \"mrs\", \"firstName\" : \"Jane\", \"lastName\" : \"Smith\", \"line1\" : \"test1\", \"line2\" : \"test2\", \"postalCode\" : \"12345\", \"town\" : \"somecity\",\"country\":{\"isocode\" : \"US\"}}}";
	protected static final String SECOND_PAYMENT_NON_DEFAULT_XML = "<paymentDetails><accountHolderName>Jane Smith</accountHolderName><cardNumber>4111111111111111</cardNumber><cardType><code>visa</code></cardType><expiryMonth>05</expiryMonth><expiryYear>2117</expiryYear><defaultPayment>false</defaultPayment><saved>true</saved><billingAddress><firstName>Jane</firstName><lastName>Smith</lastName><titleCode>mrs</titleCode><country><isocode>US</isocode></country><postalCode>12345</postalCode><town>somecity</town><line1>test1</line1><line2>test2</line2></billingAddress></paymentDetails>"

	protected static final GOOOD_PAYMENT = [
			"accountHolderName": "Jan Kowalski",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "03",
			"expiryYear": "2020",
			"defaultPaymentInfo": true,
			"saved": true,
			"billingAddress.titleCode": "mr",
			"billingAddress.firstName": "Jan",
			"billingAddress.lastName": "Kowalski",
			"billingAddress.line1": "Zwyciestwa 23",
			"billingAddress.line2": "2 pietro",
			"billingAddress.postalCode": "44100",
			"billingAddress.town": "Gliwice",
			"billingAddress.country.isocode": "DE"
	]
	protected static final String GOOOD_PAYMENT_JSON = "{\"accountHolderName\" : \"Jan Kowalski\", \"cardNumber\" : \"4111111111111111\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"03\", \"expiryYear\" : \"2020\", \"defaultPayment\" : true, \"saved\" : true,\"billingAddress\" : { \"titleCode\" : \"mr\", \"firstName\" : \"Jan\", \"lastName\" : \"Kowalski\", \"line1\" : \"Zwyciestwa 23\", \"line2\" : \"2 pietro\", \"postalCode\" : \"44100\", \"town\" : \"Gliwice\",\"country\":{\"isocode\" : \"DE\"}}}";
	protected static final String GOOOD_PAYMENT_XML = "<paymentDetails><accountHolderName>Jan Kowalski</accountHolderName><cardNumber>4111111111111111</cardNumber><cardType><code>visa</code></cardType><expiryMonth>03</expiryMonth><expiryYear>2020</expiryYear><defaultPayment>true</defaultPayment><saved>true</saved><billingAddress><firstName>Jan</firstName><lastName>Kowalski</lastName><titleCode>mr</titleCode><country><isocode>DE</isocode></country><postalCode>44100</postalCode><town>Gliwice</town><line1>Zwyciestwa 23</line1><line2>2 pietro</line2></billingAddress></paymentDetails>"

	protected static final BAD_PAYMENT = [
			"accountHolderName": "Jan Kowalski",
			"cardNumber": "4111111111111111",
			"cardType": "visa",
			"expiryMonth": "03",
			"defaultPaymentInfo": true,
			"saved": true,
			"billingAddress.titleCode": "mr",
			"billingAddress.firstName": "Jan",
			"billingAddress.lastName": "Kowalski",
			"billingAddress.line1": "Zwyciestwa 23",
			"billingAddress.line2": "2 pietro",
			"billingAddress.postalCode": "44100",
			"billingAddress.town": "Gliwice",
			"billingAddress.country.isocode": "DE"
	]
	protected static final String BAD_PAYMENT_JSON = "{\"accountHolderName\" : \"Jan Kowalski\", \"cardNumber\" : \"4111111111111111\", \"cardType\" : {\"code\":\"visa\"}, \"expiryMonth\" : \"03\", \"defaultPayment\" : true, \"saved\" : true,\"billingAddress\" : { \"titleCode\" : \"mr\", \"firstName\" : \"Jan\", \"lastName\" : \"Kowalski\", \"line1\" : \"Zwyciestwa 23\", \"line2\" : \"2 pietro\", \"postalCode\" : \"44100\", \"town\" : \"Gliwice\",\"country\":{\"isocode\" : \"DE\"}}}";
	protected static final String BAD_PAYMENT_XML = "<paymentDetails><accountHolderName>Jan Kowalski</accountHolderName><cardNumber>4111111111111111</cardNumber><cardType><code>visa</code></cardType><expiryMonth>03</expiryMonth><defaultPayment>true</defaultPayment><saved>true</saved><billingAddress><firstName>Jan</firstName><lastName>Kowalski</lastName><titleCode>mr</titleCode><country><isocode>DE</isocode></country><postalCode>44100</postalCode><town>Gliwice</town><line1>Zwyciestwa 23</line1><line2>2 pietro</line2></billingAddress></paymentDetails>"

	/**
	 * This method creates new customer and logs him in.
	 * @param client REST client to be used
	 * @param format format to be used
	 * @return created customer
	 */
	protected registerAndAuthorizeCustomer(RESTClient client, format) {
		def customer = registerCustomerWithTrustedClient(client, format)
		authorizeCustomer(client, customer)
		return customer
	}

	/**
	 * This method creates new customer.
	 * After it finishes you are authorized as trusted client.
	 * @param client REST client to be used
	 * @param format format to be used
	 * @return created customer
	 */
	protected registerCustomerWithTrustedClient(RESTClient client, format) {
		authorizeTrustedClient(client)
		def customer = registerCustomer(client, format)
		return customer
	}

	/**
	 * This method retrieves address by id
	 * @param client REST client to be used
	 * @param user customer to whom the address belongs
	 * @param address map containing id of an address
	 * @param format format to be used
	 * @return
	 */
	protected getAddress(RESTClient client, user, address, format, fullFields = false) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + user.id + '/addresses/' + address.id,
				query: fullFields ? ['fields': FIELD_SET_LEVEL_FULL] : [:],
				contentType: format,
				requestContentType: URLENC)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}
		return response.data
	}

	/**
	 * This method creates address as provided in the parameter
	 * @param address a map representing the address that must contain all required fields; will be used as request body
	 * @param client REST client to be used
	 * @param user customer for which the address will be created
	 * @param format format to be used
	 * @return created address
	 */
	protected createAddress(address, RESTClient client, user, format = JSON) {
		HttpResponseDecorator response = client.post(
				path: getBasePathWithSite() + '/users/' + user.id + '/addresses',
				body: address,
				contentType: format,
				requestContentType: URLENC)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
		return response.data
	}

	/**
	 * This method creates default payment info
	 * @param client REST client to be used
	 * @param customer customer for whom payment info is created
	 * @param cartId cart to which payment info is to be added
	 * @param format format to be used
	 * @return payment info
	 */
	protected createPaymentInfo(RESTClient client, customer, cartId, format = JSON) {
		HttpResponseDecorator response = client.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/paymentdetails',
				body: DEFAULT_PAYMENT,
				contentType: format,
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
		return response.data
	}
	/**
	 * This method creates payment info as provided in the parameter
	 * @param payment a map representing payment to be created that must contain all required fields; will be used as request body
	 * @param client REST client to be used
	 * @param customer customer for whom payment info is created
	 * @param cartId cart to which payment info is to be added.
	 * @param format format to be used
	 * @return payment info
	 */
	protected createPaymentInfo(payment, RESTClient client, customer, cartId, format = JSON) {
		HttpResponseDecorator response = client.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartId + '/paymentdetails',
				body: payment,
				contentType: format,
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
		return response.data
	}
	/**
	 * This is service method that prepares customer and empty cart that belongs to the customer.
	 * When the method finishes customer is logged in.
	 * @param client REST client to use
	 * @param format format to be used
	 * @return an array holding customer at position 0 and cart at position 1
	 */
	protected createAndAuthorizeCustomerWithCart(RESTClient client, format = JSON) {
		def customer = registerAndAuthorizeCustomer(restClient, format)
		def cart = createCart(restClient, customer, format)
		return [customer, cart]
	}

	/**
	 * Convenience method creating customer cart and payment info
	 * @param client REST client to use
	 * @param format format to be used
	 * @return created customer and payment info, customer at position [0], info at position [1], cart at position [2]
	 */
	protected createCustomerWithPaymentInfo(RESTClient client, format = JSON) {
		def customer = registerAndAuthorizeCustomer(client, format)
		def cart = createCart(client, customer, format)
		def info = createPaymentInfo(client, customer, cart.code, format)
		return [customer, info, cart]
	}

	/**
	 * This method retrieves payment info by provided ID
	 * @param client REST client to use
	 * @param customer customer, to whom payment info belongs
	 * @param infoID id of payment info
	 * @param format format to be used
	 * @return
	 */
	protected getPaymentInfo(RESTClient client, customer, infoID, format) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails/' + infoID,
				query: ["fields": FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}
		return response.data;
	}

	/**
	 * This method creates user group.
	 * After it finishes you will be authorized as customer manager
	 * @param client client to be used
	 * @param group a map with at least required fields that represents group, will be used as post body. Pass null to get a new group with random id created
	 * @param format format to be used
	 *
	 * return id of created group
	 */
	protected createUserGroup(RESTClient client, group, format) {
		authorizeCustomerManager(restClient)
		def id = group == null ? "" + System.currentTimeMillis() + "_customerGroup" : group.groupId
		HttpResponseDecorator response = client.post(
				path: getBasePathWithSite() + "/customergroups",
				contentType: format,
				body: group == null ? ["groupId": id] : group,
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
		return id
	}

	/**
	 * This method retrieves user group by provided ID
	 * After it finishes you will be authorized as customer manager
	 * @param client client to be used
	 * @param groupID ID of customer group to be retrieved
	 * @param format format to be used
	 * @return requested user group. Returned group will be a member of customergroup
	 */
	protected getUserGroup(RESTClient client, groupID, format) {
		authorizeCustomerManager(restClient)
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + "/customergroups/" + groupID,
				contentType: format,
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}
		return response.data
	}

	/**
	 * This method adds customer to 'customergroup'
	 * @param client client to be used
	 * @param ID ID of customer or group that should become member of customergroup
	 * @param format format to be used
	 */
	protected addToCustomergroup(RESTClient client, ID, format) {
		def baseCustomerGroup = "customergroup"
		addToGroup(client, baseCustomerGroup, ID, format)
	}

	/**
	 * This method adds customer to specified group.
	 * @param client client to be used
	 * @param targetGroupID
	 * @param ID ID of customer or group that should become member of target group
	 * @param format format to be used
	 */
	protected addToGroup(RESTClient client, targetGroupID, ID, format) {
		HttpResponseDecorator response = client.patch(
				path: getBasePathWithSite() + "/customergroups/" + targetGroupID + "/members",
				contentType: format,
				body: ["members": ID],
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}
	}

	protected getOrders(RESTClient client, customer, format) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + "/users/" + customer.id + "/orders",
				contentType: format,
				query: ["fields": FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}
		return response
	}
}
