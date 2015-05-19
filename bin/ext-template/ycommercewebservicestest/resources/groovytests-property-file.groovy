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

HOST = 'localhost'
PORT = 9001
SECURE_PORT = 9002
BASE_SITE = 'wsTest'

DEFAULT_HTTP_URI = "http://${HOST}:${PORT}"
DEFAULT_HTTPS_URI = "https://${HOST}:${SECURE_PORT}"

BASE_PATH = "/${WEBROOT}/${VERSION}"
BASE_PATH_WITH_SITE = "/${WEBROOT}/${VERSION}/${BASE_SITE}"

FULL_BASE_URI = DEFAULT_HTTP_URI + BASE_PATH_WITH_SITE
FULL_SECURE_BASE_URI = DEFAULT_HTTPS_URI + BASE_PATH_WITH_SITE

OAUTH2_TOKEN_URI = DEFAULT_HTTPS_URI
OAUTH2_TOKEN_ENDPOINT_PATH = "/${WEBROOT}/oauth/token"
OAUTH2_TOKEN_ENDPOINT_URI = OAUTH2_TOKEN_URI + OAUTH2_TOKEN_ENDPOINT_PATH

OAUTH2_CALLBACK_URI = "http://${HOST}:8080/oauth2_callback"

CLIENT_ID='mobile_android'
CLIENT_SECRET='secret'
TRUSTED_CLIENT_ID='trusted_client'
TRUSTED_CLIENT_SECRET='secret'

FAIL_ON_NAMING_CONVENTION_ERROR=false
