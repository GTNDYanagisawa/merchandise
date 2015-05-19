/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

/**
 *
 *
 */
class AddressBookFlow extends AbstractUserTest {


	private getAddressBook(RESTClient client, customer, format){
		return restClient.get(
		path : getBasePathWithSite() + '/users/' + customer.id + "/addresses/",
		contentType : format,
		requestContentType : URLENC
		).data
	}

	def "Address book flow : #format"() {
		given: "a new, logged in customer with single address"
		def customer = registerAndAuthorizeCustomer(restClient, format)
		def firstAddress = createAddress(GOOD_ADDRESS_DE, restClient, customer, format)

		expect: "get address book, one address should be listed"
		with(getAddressBook(restClient , customer, format)){
			addresses.size()==1
		}

		and: "update address"
		def updates = [
			'line1' : 'Zwyciestwa 23',
			'postalCode' : '44-100',
			'town' : 'Gliwice',
			'country.isocode' : 'PL'
		]

		with(restClient.patch(
				path : getBasePathWithSite() + '/users/' + customer.id + "/addresses/"+firstAddress.id,
				contentType : format,
				body: updates,
				requestContentType : URLENC
				)){
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "fields should be updated as expected"

		with(getAddress(restClient, customer, firstAddress, format)){
			line1 == 'Zwyciestwa 23'
			postalCode == '44-100'
			town == 'Gliwice'
			country.isocode == 'PL'
		}

		and: "add another address, get customer. New address should not be marked as default"
		def secondAddress = createAddress(GOOD_ADDRESS_US, restClient, customer, format)

		with(restClient.get(
				path : getBasePathWithSite() + '/users/' + customer.id,
				contentType : format,
				query: ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC).data){
					defaultAddress.id == firstAddress.id
				}

		and: "get address book, should list two addresses now"
		with(getAddressBook(restClient , customer, format)){
			addresses.size()==2
		}

		and: "mark new address as default, old should not be default any more"
		HttpResponseDecorator response = restClient.patch(
				path : getBasePathWithSite() + '/users/' + customer.id + "/addresses/" + secondAddress.id,
				query : [
					'defaultAddress' : 'true'
				],
				contentType : format,
				requestContentType : URLENC)

		with(restClient.get(
				path : getBasePathWithSite() + '/users/' + customer.id,
				contentType : format,
				query: ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC).data){
					defaultAddress.id == secondAddress.id
				}

		and: "remove second address, first one should not be marked as default again"
		with(restClient.delete(
				path : getBasePathWithSite() + '/users/' + customer.id + "/addresses/" + secondAddress.id,
				contentType : format,
				requestContentType : URLENC)){
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		with(restClient.get(
				path : getBasePathWithSite() + '/users/' + customer.id,
				contentType : format,
				query: ["fields" : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC).data){
					!isNotEmpty(defaultAddress)
					//TODO: verify with Alistair
				}

		where:
		format << [JSON, XML]
	}
}
