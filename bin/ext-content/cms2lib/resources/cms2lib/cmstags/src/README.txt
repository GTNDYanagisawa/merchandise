/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */

cmstags-src.zip contains sources of all tags shipped with cms2lib.

../cmstags.tld - Tag Library Descriptor

One can easily adjust tags that are used in application. In order to achieve this please:


1) Remove already generated tags from application (remove cmstags.jar from <extension>/web/webroot/WEB-INF/lib/)

2) Copy sources to you web-src folder in application.

3) Adjust tag sources accordingly.

4) Adjust packages names within Tag Library Descriptor - cmstags.tld if necessary

5) Copy Tag Library Descriptor to <extension>/web/webroot/WEB-INF/tld	

6) Use proper taglib in your frontend <%@ taglib prefix="cms" uri="/WEB-INF/tld/cmstags.tld" %>