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

/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu
/**
 * Aggregate information about running test (test name, called web service request)
 */
//@ToString(includeNames=true)
class SummaryOfRunningTest {
	def String testName;
	List<WSRequestSummary> requests = new ArrayList<WSRequestSummary>();

	void addRequest(String resource, String accept, String method)	{
		requests.add(new WSRequestSummary(resource: resource, accept: accept, httpMethod:method));
	}

	void addResponse(String response) {
		int lastIndex = requests.size() - 1;
		WSRequestSummary last = requests.get(lastIndex);
		if(last.response != null) {
			throw new IllegalStateException();
		}
		last.response = response;
	}
}
