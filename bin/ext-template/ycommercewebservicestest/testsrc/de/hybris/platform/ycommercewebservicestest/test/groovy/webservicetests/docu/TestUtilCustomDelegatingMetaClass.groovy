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

import org.codehaus.groovy.runtime.wrappers.PojoWrapper

import groovy.json.JsonOutput


/**
 * Intercept TestUtil static method calls: get(Secure)Connection, verified(XML|JSON)Slurper
 * and send event TestUtilMethodInvEvent to registered event listeners
 */
class TestUtilCustomDelegatingMetaClass extends DelegatingMetaClass {
	private static List<TestUtilMethodInvListener> listeners = new ArrayList<TestUtilMethodInvListener>();

	/**
	 * Register listener which want to be notified about interception TestUtil method calls
	 * @param listener
	 */
	public static void addEventListener(TestUtilMethodInvListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove listener
	 * @param listener
	 */
	public static void removeEventListener(TestUtilMethodInvListener listener) {
		listeners.remove(listener);
	}

	private void sendEvent(TestUtilMethodInvEvent ev) {
		for(TestUtilMethodInvListener listener:listeners) {
			listener.onEvent(ev);
		}
	}

	TestUtilCustomDelegatingMetaClass(final Class aClass) {
		super(aClass)
		initialize()
	}

	@Override
	public Object invokeStaticMethod(Object object, String methodName,
	Object[] arguments) {

		//PojoWrapper
		if(arguments != null && arguments.size() > 0 && arguments.getAt(0) instanceof PojoWrapper) {
			return super.invokeStaticMethod(object, methodName, arguments);
		}
		switch (methodName) {
			case "getConnection":
				return logMethodGetConnection(object, methodName, arguments);
			case "getSecureConnection":
				return logMethodGetSecureConnection(object, methodName, arguments);
			case "getSecureURLConnection":
				return logGetSecureURLConnection(object, methodName, arguments);
			case "verifiedXMLSlurper":
				return logMethodVerifiedXMLSlurper(object, methodName, arguments);
			case "verifiedJSONSlurper":
				return logMethodVerifiedJSONSlurper(object, methodName, arguments);
			default:
				return super.invokeStaticMethod(object, methodName, arguments);
		}
	}


	private Object logGetSecureURLConnection(Object object, String methodName,
	Object[] arguments) {
		String path, method;
		path = arguments[0];
		method = arguments[1];
		sendEvent(new TestUtilMethodInvEvent(path, null,method));
		return super.invokeStaticMethod(object, methodName, arguments);
	}

	private Object logMethodGetSecureConnection(Object object, String methodName,
	Object[] arguments) {
		String path, method, accept;
		path = arguments[0];
		if(arguments.size() >= 2)
			method = arguments?.getAt(1);
		else
			method = 'GET';
		if(arguments.size() >= 3)
			accept = arguments?.getAt(2);
		else
			accept = 'XML';


		sendEvent(new TestUtilMethodInvEvent(path, accept,method));
		return super.invokeStaticMethod(object, methodName, arguments);
	}

	private Object logMethodGetConnection(Object object, String methodName,
	Object[] arguments) {
		String path, method, accept;
		//
		path = arguments[0];
		if(arguments.size() >= 2)
			method = arguments?.getAt(1);
		else
			method = 'GET';
		if(arguments.size() >= 3)
			accept = arguments?.getAt(2);
		else
			accept = 'XML';


		sendEvent(new TestUtilMethodInvEvent(path, accept,method));
		return super.invokeStaticMethod(object, methodName, arguments);
	}

	private Object logMethodVerifiedXMLSlurper(Object object, String methodName,
	Object[] arguments) {


		Object ret =super.invokeStaticMethod(object, methodName, arguments);
		//convert response from verifiedXMLSlurpler to well-formated XML text
		String xmlText = new groovy.xml.StreamingMarkupBuilder().bindNode(ret) as String;
		def root = new XmlParser().parseText(xmlText);

		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		XmlNodePrinter nodePrinter = new XmlNodePrinter(pw);
		nodePrinter.setPreserveWhitespace(true);
		nodePrinter.print(root);

		sendEvent(new TestUtilMethodInvEvent(stringWriter.toString()));
		return ret;
	}

	private Object logMethodVerifiedJSONSlurper(Object object, String methodName,
	Object[] arguments) {

		Object ret =super.invokeStaticMethod(object, methodName, arguments);
		//convert response from verifiedJSONSlurpler to well-formated JSON text
		JsonOutput jsonOutput = new JsonOutput();
		String jsonText = jsonOutput.toJson(ret);
		jsonText = jsonOutput.prettyPrint(jsonText);
		sendEvent(new TestUtilMethodInvEvent(jsonText));
		return ret;
	}
}
