/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billing.api.base;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.openmrs.module.billing.api.base.entity.db.hibernate.EntityCriteria;

/**
 * Allows for ordering hibernate queries by some customized sql (for example, a database function).
 * Adapted from: http://blog.hexican.com/2012/05/how-to-customize-hibernate-order-by/
 */
public class CustomizedOrderBy implements EntityCriteria.SortOrder {
	
	private final String sqlExpression;
	
	public static EntityCriteria.SortOrder asc(String sqlFormula) {
		if (!StringUtils.endsWith(sqlFormula, " asc")) {
			sqlFormula += " asc";
		}
		
		return new CustomizedOrderBy(sqlFormula);
	}
	
	public static EntityCriteria.SortOrder desc(String sqlFormula) {
		if (!StringUtils.endsWith(sqlFormula, " desc")) {
			sqlFormula += " desc";
		}
		
		return new CustomizedOrderBy(sqlFormula);
	}
	
	protected CustomizedOrderBy(String sqlExpression) {
		this.sqlExpression = sqlExpression;
	}
	
	@Override
	public Order toOrder(CriteriaBuilder cb, Root<?> root) {
		boolean descending = StringUtils.endsWith(sqlExpression, " desc");
		String formula = StringUtils.removeEnd(StringUtils.removeEnd(sqlExpression, " desc"), " asc");
		Expression<Object> expression = ((HibernateCriteriaBuilder) cb).sql(formula, Object.class);
		
		return descending ? cb.desc(expression) : cb.asc(expression);
	}
	
	public String toString() {
		return sqlExpression;
	}
	
}
