package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityPositionsByRecruiter;

public class PositionsGroupedByRecruiters implements Consumer<CcpJsonRepresentation>{
	
	private CcpJsonRepresentation positionsGroupedByRecruiters = CcpConstants.EMPTY_JSON;

	public void accept(CcpJsonRepresentation position) {
		String name2 = VisEntityPositionsByRecruiter.Fields.email.name();
		String email = position.getAsString(name2);
		this.positionsGroupedByRecruiters = this.positionsGroupedByRecruiters.addToList(email, position);
	}
	
	
	public void saveAllPositionsGroupedByRecruiters(){
		String name2 = VisEntityPositionsByRecruiter.Fields.email.name();
		String name = VisEntityPositionsByRecruiter.Fields.position.name();
		Set<String> emails = this.positionsGroupedByRecruiters.keySet();
		List<CcpJsonRepresentation> result = new ArrayList<>();
		for (String email : emails) {
			List<CcpJsonRepresentation> positions = this.positionsGroupedByRecruiters.getAsJsonList(email);
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put(name2, email).put(name, positions);
			result.add(put);
		}

		JnAsyncCommitAndAudit.INSTANCE.executeBulk(result, CcpEntityOperationType.create, VisEntityPositionsByRecruiter.INSTANCE);
	}

}
