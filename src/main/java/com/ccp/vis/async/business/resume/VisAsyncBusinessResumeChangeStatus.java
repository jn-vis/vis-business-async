package com.ccp.vis.async.business.resume;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeChangeStatus  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeChangeStatus() {
		
	}
	public static final VisAsyncBusinessResumeChangeStatus INSTANCE = new VisAsyncBusinessResumeChangeStatus();
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		CcpEntity mirrorEntity = VisEntityResume.INSTANCE.getMirrorEntity();
		TransferRecordBetweenEntities tryToChangeResumeStatusToActive = new TransferRecordBetweenEntities(VisEntityResume.INSTANCE);
		TransferRecordBetweenEntities tryToChangeResumeStatusToInactive = new TransferRecordBetweenEntities(mirrorEntity);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				resume 
				, tryToChangeResumeStatusToActive
				, tryToChangeResumeStatusToInactive
				);
		return CcpConstants.EMPTY_JSON;
	}

}
