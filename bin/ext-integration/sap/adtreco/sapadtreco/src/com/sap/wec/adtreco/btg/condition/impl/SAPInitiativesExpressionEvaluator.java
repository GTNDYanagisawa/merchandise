package com.sap.wec.adtreco.btg.condition.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.wec.adtreco.btg.SAPInitiativeSet;

import de.hybris.platform.btg.condition.impl.PlainCollectionExpressionEvaluator;

public class SAPInitiativesExpressionEvaluator extends
		PlainCollectionExpressionEvaluator {
	public SAPInitiativesExpressionEvaluator() {
		super(createOperatorMap());
	}

	private static Map<String, Set<Class>> createOperatorMap() {
		Map<String, Set<Class>> operatorMap = new HashMap<>();
		operatorMap.put(CONTAINS_ALL, createSet(SAPInitiativeSet.class));
		operatorMap.put(CONTAINS_ANY, createSet(SAPInitiativeSet.class));
		operatorMap.put(NOT_CONTAINS, createSet(SAPInitiativeSet.class));
		return operatorMap;
	}

	private static Set<Class> createSet(Class<SAPInitiativeSet> clazz) {
		Set<Class> set = new HashSet<>(1);
		set.add(clazz);
		return set;
	}

	@Override
	public Class getLeftType() {
		return SAPInitiativeSet.class;
	}
}