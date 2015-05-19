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
package de.hybris.platform.mobileservices;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mobileservices.facade.impl.DefaultDetectionService;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mobile.device.LiteDeviceResolver;


/**
 * The Class TestMobileDetection.
 */
@UnitTest
public class MobileDetectionTest
{

	/** The detection service. */
	private DefaultDetectionService detectionService;

	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(MobileDetectionTest.class.getName());


	/** The mobile_ua. */
	private final String[] mobile_ua =
	{
			"Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10",
			"BlackBerry8120/4.3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/142",
			"LG-CU720/V1.0l Obigo/Q05A Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Link/6.3.1.20.0",
			"LG-CU920/V1.0s Obigo/Q05A Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Link/6.3.1.20.0",
			"LG-KE970 MIC/1.1.14 MIDP-2.0/CLDC-1.1",
			"LG-KG290 Obigo/WAP2.0 MIDP-2.0/CLDC-1.1",
			"LG-KU380/v1.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"MOT-L6/0A.61.0CR MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"MOT-L6i/0A.65.07R MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"MOT-RAZRV3x/85.9B.E5P MIB/BER2.2 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"MOT-V360v/08.B7.58R MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Link/6.5.0.0.06.5.0.0.06.5.0.0.06.5.0.0.06.5.0.0.06.5.0.0.0",
			"Mozilla/4.0 SonyEricssonC905v/R1DE Browser/NetFront/3.4 Profile/MIDP-2.1 Configuration/CLDC-1.1 JavaPlatform/JP-8.4.1 UP.Link/6.5.0.0.06.5.0.0.06.5.0.0.0",
			"Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 Nokia6120c/3.83; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413",
			"Nokia2600c-2/2.0 (06.82) Profile/MIDP-2.1 Configuration/CLDC-1.1",
			"Nokia3250/2.0 (3.21) SymbianOS/9.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia5200/2.0 (05.00) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia5200/2.0 (07.00) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia5220XpressMusic/2.0 (05.20) Profile/MIDP-2.1 Configuration/CLDC-1.1",
			"Nokia5300/2.0 (05.00) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia5310XpressMusic/2.0 (10.10) Profile/MIDP-2.1 Configuration/CLDC-1.1",
			"Nokia6020/2.0 (04.90) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia6030/2.0 (5.40) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia6300/2.0 (07.21) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia6500s-1/2.0 (08.30) Profile/MIDP-2.1 Configuration/CLDC-1.1",
			"Nokia6600/1.0 (5.53.0) SymbianOS/7.0s Series60/2.0 Profile/MIDP-2.0 Configuration/CLDC-1.0",
			"Nokia6680/1.0 (5.04.40) SymbianOS/8.0 Series60/2.6 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Nokia7373/2.0 (05.50) Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"NokiaN73-1/3.0638.0.0.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"NokiaN90-1/5.0607.7.3 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Link/6.5.0.0.06.5.0.0.0",
			"Opera/8.01 (J2ME/MIDP; Opera Mini/2.0.4509/1724; id; U; ssr)",
			"SAMSUNG-SGH-A177/A177UCIC3 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Browser/6.2.3.3.c.1.101 (GUI) MMP/2.0 UP.Link/6.3.1.20.0",
			"SAMSUNG-SGH-U900/BVHD3 SHP/VPP/R5 NetFront/3.4 SMM-MMS/1.2.0 profile/MIDP-2.0 configuration/CLDC-1.1",
			"SAMSUNG-SGH-Z170/BVGF1 SHP/VPP/R5 NetFront/3.4 SMM-MMS/1.2.0 profile/MIDP-2.0 configuration/CLDC-1.1",
			"SEC-SGHD840/1.0 NetFront/3.2 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SEC-SGHM620/1.0 Openwave/6.2.3 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Browser/6.2.3.3.c.1.101 (GUI) MMP/2.0",
			"SEC-SGHU600/1.0 NetFront/3.2 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SGH-Z510/1.0 NetFront/3.2 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SIE-CF75/10 UP.Browser/7.0.2.2.d.5(GUI) MMP/2.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonG705/R1EA Browser/NetFront/3.4 Profile/MIDP-2.1 Configuration/CLDC-1.1 JavaPlatform/JP-8.4.2",
			"SonyEricssonK770i/R8BC Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonK770i/R8BC Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Link/6.5.1.3.0",
			"SonyEricssonK800i/R1KG Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonK800i/R8BF Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonK800iv/R1CE Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonK850i/R1EA Browser/NetFront/3.4 Profile/MIDP-2.1 Configuration/CLDC-1.1 UP.Link/6.2.3.20.0",
			"SonyEricssonS500i/R8BE Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonTM506/R3DA Browser/NetFront/3.4 Profile/MIDP-2.1 Configuration/CLDC-1.1 JavaPlatform/JP-8.3.2",
			"SonyEricssonW200i/R4GB Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonW200i/R4HA Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonW200i/R4JA Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"SonyEricssonZ530i/R6DA Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
			"Vodafone/1.0/SAMSUNG-SGH-J700V/BUHB2/1.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Browser/6.2.3.3.c.1.101 (GUI) MMP/2.0",
			"Vodafone/1.0/SamsungSGHB520V/B520BUHI1 Browser/Openwave/6.2.3 Profile/MIDP-2.0 Configuration/CLDC-1.1 UP.Browser/6.2.3.3.c.1.101 (GUI) MMP/2.0",
			"Alcatel-OT-S520/1.0 ObigoInternetBrowser/Q03C", "Amoi 8512/R18.7 NF-Browser/3.3",
			"HTC_Touch_Pro2_T7373 Opera/9.50 (Windows NT 5.1; U; en)", "KWC-S1300/1007 UP.Browser/7.2.6.1.839 (GUI) MMP/2.0",
			"LG9100/1.0 UP.Browser/6.2.3.9 (GUI) MMP/2.0", "MAUI_WAP_Browser",
			"MOT-SPARK/00.62 UP.Browser/6.2.3.4.c.1.123 (GUI) MMP/2.0",
			"Mozilla/5.0 (SymbianOS/9.1; U; en-us) AppleWebKit/413 (KHTML, like Gecko) Safari/413", "NEC-N600/1.0 HopenOS/2.0",
			"Nokia3100/1.0 (compatible; WukongB___ot)", "SAMSUNG-SGH-i900/1.0 Opera 9.5", "SEC-SGHD410", "SEC-SGHX660/1.0 TSS/2.5",
			"SEC-SGHX680/1.0 TSS/2.5", "Samsung-SPHM510 AU-OBIGO/Q04C1-1.22 MMP/2.0",
			"X1i Mozilla/4.0 (compatible; MSIE 6.0; Windows CE; IEMobile 7.11)", "sam-r450 UP.Browser/6.2.3.8 (GUI) MMP/2.0" };

	/** The desktop_ua. */
	private final String[] desktop_ua =
	{
			/* Safari was wrongly identified as iPhone */
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/532.5 (KHTML, like Gecko) Chrome/4.0.249.49 Safari/532.5",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES) AppleWebKit/531.9 (KHTML, like Gecko) Version/4.0.3 Safari/531.9.1",
			/* others */
			"Mozilla/5.0 (X11; U; Linux x86_64; c) AppleWebKit/528.5+ (KHTML, like Gecko, Safari/528.5+) WebShot",
			"Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.0.13) Gecko/2009080317 Ubuntu/8.04 (hardy) Firefox/3.0.13",
			"Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.0.5) Gecko/2009011817 Gentoo Firefox/3.0.5",
			"Opera/9.30 (Nintendo Wii; U; ; 2047-7; es-Es)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; .NET CLR 3.0.04506;)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; GTB6; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506; InfoPath.2)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; GTB6; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30618)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506; MSN 9.0;MSN 9.1; MSNbVZ02; MSNmen-us; MSNcOTH)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.30618; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; WOW64; Trident/4.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; .NET CLR 3.0.30729; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; WOW64; Trident/4.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30618)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727; OfficeLiveConnector.1.3; OfficeLivePatch.0.0; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; OfficeLiveConnector.1.3; OfficeLivePatch.0.0; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; InfoPath.2)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; InfoPath.2; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; FunWebProducts; GTB6; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30618; yie8)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; GTB6; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30618; FDM)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729)",
			"Mozilla/4.76 (Windows 98; U) Opera 5.12 [en]",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; es-ES; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB5",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6; en-us) AppleWebKit/531.9 (KHTML, like Gecko) Version/4.0.3 Safari/531.9",
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.18) Gecko/20081029 Firefox/2.0.0.18",
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/418 (KHTML, like Gecko) Safari/417.9.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/2.0.172.43 Safari/530.5",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1) Gecko/20061010 Firefox/2.0 Me.dium/1.0 (http://me.dium.com)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.4) Gecko/20070515 Firefox/2.0.0.4",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.5) Gecko/20070713 Firefox/2.0.0.5",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.9",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.13) Gecko/2009073022 Firefox/3.0.13",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en; rv:1.9.0.13) Gecko/2009073022 Firefox/3.5.2 (.NET CLR 3.5.30729) SurveyB___ot/2.3 (DomainTools)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.0.13) Gecko/2009073022 Firefox/3.0.13 GTB5",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.0.14) Gecko/2009082707 Firefox/3.0.14",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.0.14) Gecko/2009082707 Firefox/3.0.14 (.NET CLR 3.5.30729)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.3) Gecko/20090824 Firefox/2.0.0.14;MEGAUPLOAD 1.0",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 (.NET CLR 3.5.30729)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.2) Gecko/2008090514 Firefox/3.0.2",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; es-ES; rv:1.9.0.14) Gecko/2009082707 Firefox/3.0.14 (.NET CLR 3.5.30729)",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; es-ES; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3",
			"Mozilla/5.0 (X11; U; Linux i686; de; rv:1.8) Gecko/20051128 SUSE/1.5-0.1 Firefox/1.5",
			"Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/532.0 (KHTML, like Gecko) Chrome/4.0.207.0 Safari/532.0",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12) Gecko/20051010 Firefox/1.0.7",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12; ips-agent) Gecko/20050922 Fedora/1.0.7-1.1.fc4 Firefox/1.0.7",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7 MG(Novarra-Vision/7.3)",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.13) Gecko/2009080315 Ubuntu/9.04 (jaunty) Firefox/3.0.13 GTB5",
			"Mozilla/5.0 (X11; U; Linux i686; es-ES; rv:1.9.0.13) Gecko/2009080315 Ubuntu/8.10 (intrepid) Firefox/3.0.13",
			"Mozilla/5.0 (X11; U; Linux i686; es-ES; rv:1.9.0.14) Gecko/2009090216 Ubuntu/9.04 (jaunty) Firefox/3.0.14",
			"Mozilla/5.0 (X11; U; Linux i686; ru-RU; rv:1.9.1.2) Gecko/20090804 Firefox/3.5.2",
			"Mozilla/5.0 (X11; U; Linux x86_64; c) AppleWebKit/528.5+ (KHTML, like Gecko, Safari/528.5+) WebShot",
			"Mozilla/4.0 (compatible;  MSIE 6.0;  Windows NT 5.2;  SV1;  .NET CLR 1.1.4322;  .NET CLR 2.0.50727;  .NET CLR 3.0.30729;  .NET CLR 3.5.30707)",
			"Mozilla/4.0 (compatible;  MSIE 6.0;  Windows NT 5.2;  SV1;  .NET CLR 1.1.4325)",
			"Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)",
			"Mozilla/4.0 (compatible; MSIE 6.0; AOL 9.0; Windows NT 5.1; SV1; FunWebProducts; .NET CLR 1.0.3705; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 6.0; WWindows NT 5.1; SV1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; .NET CLR 1.1.4322; SpamBlockerUtility 4.8.4)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Every Toolbar; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; (R1 1.5); .NET CLR 1.1.4322; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; MEGAUPLOAD 1.0; SeekmoToolbar 4.8.4)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; HbTools 4.8.4; IEMB3)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; .NET CLR 3.0.04506.30)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; FunWebProducts; .NET CLR 1.1.4322; ZangoToolbar 4.8.2; yplus 5.1.04b)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; FunWebProducts; .NET CLR 2.0.50727; HbTools 4.8.2)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; GTB6)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; GTB6; .NET CLR 2.0.50727; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; IEMB3; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; YPC 3.0.0; FunWebProducts)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Wanadoo cable; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; {552BAFC5-78A5-43E5-8E97-4BF5AAD8E289}; SV1; .NET CLR 1.1.4322; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; {DB258E51-CD23-D046-8027-065117959629}; SV1; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 7.0;  Windows NT 5.2)",
			"Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.0; Windows NT 5.1; FunWebProducts; (R1 1.5); .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; ###104144652331201356592507###; NetBrowserPro; .NET CLR 1.1.4322; HbTools 4.8.0; SpamBlockerUtility 4.8.0; IEMB3; ZangoToolbar 4.8.3; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; IEMB3)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 3.1; IEMB3; HbTools 4.8.2; IEMB3)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; MSN 9.0; MSNbBBYZ; MSNmen-us; MSNcIA; MPLUS)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; InfoPath.1; Alexa Toolbar; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; ADVPLUGIN|K114|04|S-803287567|dial; snprtz|T13068600000224|2600#Service Pack 2#2#5#1)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; AT&T CSM8.0; .NET CLR 1.1.4322; MSN 9.0;MSN 9.1; MSNbQ002; MSNmen-us; MSNcOTH)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; FunWebProducts; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; FunWebProducts; .NET CLR 1.1.4322; .NET CLR 1.0.3705; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; FunWebProducts; .NET CLR 1.1.4322; HbTools 4.8.4; (R1 1.5))",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; FunWebProducts; Echochat-zilla 4.0; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; FunWebProducts; GTB6; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; .NET CLR 1.1.4322; InfoPath.2; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB6; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; InfoPath.1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; MS-RTC LM 8; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; JENSMIL; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; YPC 3.2.0; ZangoToolbar 4.8.3; .NET CLR 2.0.50727; .NET CLR 1.1.4322; Windows-Media-Player/10.00.00.3990; yplus 5.6.04b)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; {F70454EA-FC56-4C10-CC8A-B0CB7069FDBC}; .NET CLR 1.1.4322)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 6.0)",
			"Mozilla/4.8 [en] (Windows NT 6.0; U)", "Opera/9.25 (Windows NT 6.0; U; en)"

	};

	@Before
	public void setUp()
	{
		detectionService = new DefaultDetectionService();
		detectionService.setDeviceResolver(new LiteDeviceResolver());
	}

	/**
	 * Test non desktop is identified as an iPhone.
	 */
	@Test
	public void testDesktopNonIphone()
	{

		for (final String ua : desktop_ua)
		{
			LOG.info("Testing :" + ua);
			assertTrue("Api fails to identify as a desktop device: (" + ua + ")", detectionService.isDesktop(ua));
			assertFalse("Api wrongly identify as a iphone: (" + ua + ")", detectionService.isiPhoneCapable(ua,
					new ArrayList<String>()));
			assertFalse("Api wrongly identify as a mobile device: (" + ua + ")", detectionService.isMobileDevice(ua));
		}

	}



	/**
	 * Test mobile detection1.
	 */
	@Test
	public void testMobileDetection1()
	{

		for (final String ua : mobile_ua)
		{
			LOG.info("Testing :" + ua);

			assertTrue("At least a markup method should be supported", detectionService.isWmlCapable(ua, null)
					|| detectionService.isXhtmlCapable(ua, null) || detectionService.isiPhoneCapable(ua, null));

			assertTrue("Api fails to identify as a mobile device: (" + ua + ")", detectionService.isMobileDevice(ua));
			assertFalse("Api wrongly identify as a desktop device: (" + ua + ")", detectionService.isDesktop(ua));
			assertFalse("Api wrongly identify as a bot: (" + ua + ")", detectionService.isBot(ua));
		}

	}


	/**
	 * Test mobile detection for empty non valid user agents.
	 */
	@Test
	public void testMobileDetectionEmptyUserAgent()
	{
		final String[] uas =
		{ "-", "", " ", "\n", "\t", "\0" };

		for (int index = 0; index < uas.length; index++)
		{
			final String userAgent = uas[index];
			LOG.info("Testing :" + userAgent);

			assertFalse("No markup method should render a false positive", detectionService.isWmlCapable(userAgent, null)
					|| detectionService.isXhtmlCapable(userAgent, null) || detectionService.isiPhoneCapable(userAgent, null));

			assertFalse("Api should fail to identify as a mobile device: (" + userAgent + ")",
					detectionService.isMobileDevice(userAgent));
			assertFalse("Api wrongly identify as a desktop device: (" + userAgent + ")", detectionService.isDesktop(userAgent));
		}

	}


	/**
	 * Test desktop detection1.
	 */
	@Test
	public void testDesktopDetection1()
	{

		for (final String ua : desktop_ua)
		{
			LOG.info("Testing :" + ua);
			assertTrue("Api fails to identify as a desktop device: (" + ua + ")", detectionService.isDesktop(ua));
			assertFalse("Api wrongly identify as a mobile device: (" + ua + ")", detectionService.isMobileDevice(ua));
			assertFalse("Api wrongly identify as a bot: (" + ua + ")", detectionService.isBot(ua));
		}

	}



}
