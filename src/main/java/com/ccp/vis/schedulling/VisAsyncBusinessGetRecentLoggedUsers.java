package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.vis.async.commons.FrequencyOptions;
import com.ccp.vis.async.commons.SendRecentUsersToGroupings;
import com.jn.commons.entities.JnEntityDisposableRecord;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;

public class VisAsyncBusinessGetRecentLoggedUsers implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGetRecentLoggedUsers() {}
	
	public static final VisAsyncBusinessGetRecentLoggedUsers INSTANCE = new VisAsyncBusinessGetRecentLoggedUsers();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		
		String entityName = JnEntityLoginSessionCurrent.INSTANCE.getEntityName();
		CcpDbQueryOptions queryToSearchLastUpdated = 
				CcpDbQueryOptions.INSTANCE
					.startQuery()
						.startBool()
							.startMust()
								.startRange()
									.startFieldRange(JnEntityDisposableRecord.Fields.timestamp.name())
										.greaterThan(System.currentTimeMillis() - FrequencyOptions.yearly.hours * 3_600_000)
									.endFieldRangeAndBackToRange()
								.endRangeAndBackToMust()	
								.term(JnEntityDisposableRecord.Fields.entity, entityName)
							.endMustAndBackToBool()
						.endBoolAndBackToQuery()
					.endQueryAndBackToRequest()
					.maxResults()
					.addDescSorting(JnEntityDisposableRecord.Fields.timestamp.name())
				;
		String[] resourcesNames = new String[] {JnEntityDisposableRecord.INSTANCE.getEntityName()};

		queryExecutor.consumeQueryResult(queryToSearchLastUpdated, resourcesNames, "10m", 10000L, SendRecentUsersToGroupings.INSTANCE, JnEntityDisposableRecord.Fields.id.name());
		
		return json;
	}

}
