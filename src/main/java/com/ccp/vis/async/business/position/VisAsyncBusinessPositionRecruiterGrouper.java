	package com.ccp.vis.async.business.position;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.vis.async.commons.PositionsGroupedByRecruiters;
import com.jn.commons.entities.JnEntityAudit;
import com.jn.commons.entities.JnEntityAudit.Fields;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionRecruiterGrouper implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionRecruiterGrouper() {}
	
	public static final VisAsyncBusinessPositionRecruiterGrouper INSTANCE = new VisAsyncBusinessPositionRecruiterGrouper();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		Set<String> allValidLogins = this.getAllValidLogins();

		CcpDbQueryOptions query = CcpDbQueryOptions.INSTANCE
				.startQuery()
					.startBool()
						.startMust()
							.terms(VisEntityPosition.Fields.email, allValidLogins)
						.endMustAndBackToBool()
					.endBoolAndBackToQuery()
				.endQueryAndBackToRequest()
				.addAscSorting("position.timestamp")
		;
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		String entityName = VisEntityPosition.INSTANCE.getEntityName();
		String[] resourcesNames = new String[]{entityName};
		
		PositionsGroupedByRecruiters positionsGroupedByRecruiters = new PositionsGroupedByRecruiters();
		queryExecutor.consumeQueryResult(query, resourcesNames, entityName, 10000, positionsGroupedByRecruiters, resourcesNames);
		positionsGroupedByRecruiters.saveAllPositionsGroupedByRecruiters();
		
		return json;
	}

	private Set<String> getAllValidLogins() {
		//FIXME paginar aggregation query
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		String entityName = JnEntityLoginSessionCurrent.INSTANCE.getEntityName();
		Fields field = JnEntityAudit.Fields.id;
		String fieldName = field.name();
		CcpDbQueryOptions query = 
				CcpDbQueryOptions.INSTANCE
					.zeroResults()
					.startQuery()
						.startBool()
							.startMust()
								.startRange()
									.startFieldRange(JnEntityAudit.Fields.timestamp.name())
										.greaterThan(System.currentTimeMillis() - 8766 * 3_600_000)
									.endFieldRangeAndBackToRange()
								.endRangeAndBackToMust()
								.term(JnEntityAudit.Fields.entity, entityName)
							.endMustAndBackToBool()
						.endBoolAndBackToQuery()
					.endQueryAndBackToRequest()
					.startAggregations()
						.startBucket(fieldName, field, 6666)
						.endTermsBuckedAndBackToAggregations()
					.endAggregationsAndBackToRequest()
				;

		String entityName2 = JnEntityAudit.INSTANCE.getEntityName();
		CcpJsonRepresentation aggregations = queryExecutor.getAggregations(query, entityName2);
		CcpJsonRepresentation innerJson = aggregations.getInnerJson(fieldName);
		Set<String> emails = innerJson.keySet().stream().map(id -> new CcpJsonRepresentation(id).getAsString("email")).collect(Collectors.toSet());
		return emails;
	}

}
