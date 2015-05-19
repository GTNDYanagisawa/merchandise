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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;

import java.text.MessageFormat;


/**
 * Single message mapping rule definition
 * 
 * @see MessageMappingRulesContainerImpl
 * @see MessageMappingRulesParserImpl
 */
public class MessageMappingRule
{

	/**
	 * Matching part = the pattern
	 */
	static public class Pattern
	{
		/**
		 * Maximum array size
		 */
		public static final int VAR_ARRAY_SIZE = 4;
		/**
		 * Pattern which indicates that rule should apply for any value in this attribute
		 */
		public static final String ANY_PATTERN = "*";
		/**
		 * Message class (application area)
		 */
		protected String beClass;
		/**
		 * Message number in back end
		 */
		protected String beNumber;
		/**
		 * Severity (I, W, E)
		 */
		protected String beSeverity;
		/**
		 * Message parameters
		 */
		protected final String[] beVars;
		/**
		 * Pattern attribute degree:
		 * <ol>
		 * <li>More the number of attributes are defined then bigger is the degree;
		 * <li>Left attribute (e.g VAR1} has greater degree as the right one (e.g. VAR4)
		 * <li>Number of attributes has absolute priority over left to right order.
		 * </ol>
		 */
		int attrDegree;

		/**
		 * Standard constructor
		 */
		protected Pattern()
		{
			beVars = new String[VAR_ARRAY_SIZE];
			beClass = null;
			beNumber = null;
			beSeverity = null;
			attrDegree = 0;
		}

		/**
		 * Constructor
		 * 
		 * @param beClass
		 *           Message class (application area)
		 * @param beNumber
		 *           Message number in back end
		 * @param beSeverity
		 */
		public Pattern(final String beClass, final String beNumber, final String beSeverity)
		{
			this();
			this.beClass = agjustField(beClass);
			this.beNumber = agjustField(beNumber);
			this.beSeverity = agjustField(beSeverity);
		}

		/**
		 * Constructor
		 * 
		 * @param beClass
		 *           Message class (application area)
		 * @param beNumber
		 *           Message number in back end
		 * @param beSeverity
		 * @param beV1
		 * @param beV2
		 * @param beV3
		 * @param beV4
		 */
		public Pattern(final String beClass, final String beNumber, final String beSeverity, final String beV1, final String beV2,
				final String beV3, final String beV4)
		{
			this(beClass, beNumber, beSeverity);
			this.beVars[0] = agjustField(beV1);
			this.beVars[1] = agjustField(beV2);
			this.beVars[2] = agjustField(beV3);
			this.beVars[3] = agjustField(beV4);
			calculateDegree();
		}

		/**
		 * @return Message class (application area)
		 */
		public String getBeClass()
		{
			return beClass;
		}

		/**
		 * @return Message number
		 */
		public String getBeNumber()
		{
			return beNumber;
		}

		/**
		 * @return Message severity
		 */
		public String getBeSeverity()
		{
			return beSeverity;
		}

		/**
		 * @return Parameter1
		 */
		public String getBeV1()
		{
			return beVars[0];
		}

		/**
		 * @return Parameter2
		 */
		public String getBeV2()
		{
			return beVars[1];
		}

		/**
		 * @return Parameter3
		 */
		public String getBeV3()
		{
			return beVars[2];
		}

		/**
		 * @return Parameter4
		 */
		public String getBeV4()
		{
			return beVars[3];
		}

		/**
		 * Adjust the field:
		 * <ul>
		 * <li>maps all forms of string containing one star '*' to null. Null mean any value.
		 * <li>But keeps "", because it means (match) an empty string.
		 * </ul>
		 * 
		 * @param value
		 * @return adjusted field
		 */
		final static protected String agjustField(String value)
		{
			if (value == null)
			{
				return null;
			}
			else
			{
				value = value.trim();
				if (value.equals(ANY_PATTERN))
				{
					return null;
				}
				else
				{
					return value;
				}
			}
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}

			if (!(obj instanceof Pattern))
			{
				return false; // quick check by hashCode
			}

			final Pattern o = (Pattern) obj;
			if (!(equalsField(beClass, o.beClass) && equalsField(beNumber, o.beNumber) && equalsField(beSeverity, o.beSeverity)))
			{
				return false;
			}
			for (int i = 0; i < VAR_ARRAY_SIZE; ++i)
			{
				if (!equalsField(beVars[i], o.beVars[i]))
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode()
		{
			int hachCode = 1 //
					+ hashCodeField(beClass) + hashCodeField(beNumber) + hashCodeField(beSeverity);
			for (int i = 0; i < VAR_ARRAY_SIZE; ++i)
			{
				hachCode += hashCodeField(beVars[i]);
			}
			return hachCode;
		}

		@Override
		public String toString()
		{
			return MessageFormat.format("{0}/{1}/{2}/({3},{4},{5},{6}) ", new Object[]
			{ beClass, beNumber, beSeverity, beVars[0], beVars[1], beVars[2], beVars[3] });
		}

		/**
		 * Does a pattern match a specific message, identified by its attributes?
		 * 
		 * @param beClass
		 * @param beNumber
		 * @param beSeverity
		 * @param var1
		 * @param var2
		 * @param var3
		 * @param var4
		 * @return Match?
		 */
		public boolean match(final String beClass, final String beNumber, final String beSeverity, final String var1,
				final String var2, final String var3, final String var4)
		{
			return matchCNS(beClass, beNumber, beSeverity) && matchVARS(var1, var2, var3, var4);
		}

		/**
		 * Does a pattern match a specific message, only considering the static attributes?
		 * 
		 * @param beClass
		 * @param beNumber
		 * @param beSeverity
		 * @return Match?
		 */
		public boolean matchCNS(final String beClass, final String beNumber, final String beSeverity)
		{
			return matchField(this.beClass, beClass) && matchField(this.beNumber, beNumber)
					&& matchField(this.beSeverity, beSeverity);
		}

		/**
		 * Does a pattern match a specific message, considering the parameters?
		 * 
		 * @param var1
		 * @param var2
		 * @param var3
		 * @param var4
		 * @return Match?
		 */
		public boolean matchVARS(final String var1, final String var2, final String var3, final String var4)
		{
			return matchField(this.beVars[0], var1) && matchField(this.beVars[1], var2) && matchField(this.beVars[2], var3)
					&& matchField(this.beVars[3], var4);
		}

		/**
		 * Does a part of the pattern match a value?
		 * 
		 * @param pattern
		 * @param value
		 * @return Match?
		 */
		protected static boolean matchField(final String pattern, final String value)
		{
			return pattern == null || pattern.isEmpty() || pattern.equals(ANY_PATTERN) || pattern.equals(value);
		}

		/**
		 * @return Attribute degree
		 */
		public int attrDergee()
		{
			return attrDegree;
		}

		/**
		 * Calculates the degree of the pattern
		 */
		protected void calculateDegree()
		{
			attrDegree = 0;
			for (int i = 0; i < VAR_ARRAY_SIZE; ++i)
			{ // from 0 to
				if (beVars[i] != null)
				{
					attrDegree += 0x10 + (0x1 << (VAR_ARRAY_SIZE - 1 - i));
				}
			}
		}

	}

	/**
	 * Result of a message mapping
	 */
	static public class Result
	{
		/**
		 * Hide message
		 */
		protected Boolean hide;
		/**
		 * Severity of result message
		 */
		protected Character severity;
		/**
		 * Java resource key
		 */
		protected String resourceKey;
		/**
		 * ID of callback which can be used to change the message on application side.
		 * 
		 * @see MessageMappingCallbackProcessor
		 */
		protected String callbackId;
		/**
		 * To which process step is this message attached to (checkout?)
		 */
		protected String processStep;

		/**
		 * Default constructor
		 */
		public Result()
		{
			hide = null;
			severity = null;
			resourceKey = null;
		}

		/**
		 * @return Severity
		 */
		public Character getSeverity()
		{
			return severity;
		}

		/**
		 * @return Java resource key
		 */
		public String getResourceKey()
		{
			return resourceKey;
		}

		/**
		 * @return Hide message?
		 */
		public boolean isHide()
		{
			return hide != null && hide.booleanValue();
		}

		/**
		 * @return the callbackId
		 */
		public String getCallbackId()
		{
			return callbackId;
		}

		/**
		 * @return the callbackId
		 */
		public String getProcessStep()
		{
			return processStep;
		}

		/**
		 * Constructor
		 * 
		 * @param isHide
		 */
		public Result(final Boolean isHide)
		{
			this.hide = isHide;
			this.severity = null;
			this.resourceKey = null;
			this.callbackId = null;
			this.processStep = null;
		}

		/**
		 * Constructor
		 * 
		 * @param severity
		 * @param resourceKey
		 * @param callbackId
		 * @param processStep
		 */
		public Result(final Character severity, final String resourceKey, final String callbackId, final String processStep)
		{
			this.severity = severity;
			this.resourceKey = resourceKey;
			this.callbackId = callbackId;
			this.processStep = processStep;
		}

		/**
		 * @param severity
		 * @param resourceKey
		 */
		public Result(final Character severity, final String resourceKey)
		{
			this.severity = severity;
			this.resourceKey = resourceKey;
			this.callbackId = null;
			this.processStep = null;
		}

		@Override
		public int hashCode()
		{
			return 1 + hashCodeField(hide) + hashCodeField(severity) + hashCodeField(resourceKey);
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}

			if (!(obj instanceof Result))
			{
				return false; // quick check by hashCode
			}

			final Result o = (Result) obj;
			return equalsField(hide, o.hide) && equalsField(severity, o.severity) && equalsField(resourceKey, o.resourceKey);
		}

		@Override
		public String toString()
		{
			if (isHide())
			{
				return "hide";
			}
			else
			{
				return MessageFormat.format("{0}/{1}", new Object[]
				{ severity, resourceKey });
			}
		}

	}

	/**
	 * @param a1
	 * @param a2
	 * @return Are 2 fields equal?
	 */
	final protected static boolean equalsField(final Object a1, final Object a2)
	{
		return a1 == a2 || a1 != null && a1.equals(a2);
	}

	/**
	 * Compiles hash code
	 * 
	 * @param obj
	 * @return Hashcode (1 if obj is null)
	 */
	final protected static int hashCodeField(final Object obj)
	{
		return (obj != null ? obj.hashCode() : 1);
	}

	/**
	 * The pattern we search for
	 */
	protected final Pattern pattern;

	/**
	 * Mapping result
	 */
	protected final Result result;

	/**
	 * Constructor
	 */
	public MessageMappingRule()
	{
		pattern = new Pattern();
		result = new Result();
	}

	/**
	 * Constructor
	 * 
	 * @param pattern
	 * @param result
	 */
	public MessageMappingRule(final Pattern pattern, final Result result)
	{
		this.pattern = pattern;
		this.result = result;
	}

	/**
	 * @return Pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * @return Mapping result
	 */
	public Result getResult()
	{
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof MessageMappingRule))
		{
			return false; // quick check by hashCode
		}

		final MessageMappingRule o = (MessageMappingRule) obj;
		return equalsField(pattern, o.pattern) && equalsField(result, o.result);
	}

	@Override
	public int hashCode()
	{
		return 1 + hashCodeField(pattern) + hashCodeField(result);
	}

	@Override
	public String toString()
	{
		return MessageFormat.format(" {0} -> {1}", new Object[]
		{ pattern, result });
	}



}
