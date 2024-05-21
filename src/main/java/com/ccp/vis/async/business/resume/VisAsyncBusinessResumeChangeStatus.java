package com.ccp.vis.async.business.resume;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeChangeStatus  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeChangeStatus() {
		
	}
	public static final VisAsyncBusinessResumeChangeStatus INSTANCE = new VisAsyncBusinessResumeChangeStatus();
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		
		VisEntityResume activeResume = VisEntityResume.INSTANCE;
		CcpEntity inactiveResumeEntity = activeResume.getMirrorEntity();
		VisAsyncBusinessResumeSendToRecruiters sendResumeToRecruiters = VisAsyncBusinessResumeSendToRecruiters.INSTANCE;
		TransferRecordToReverseEntity tryToChangeResumeStatusToInactive = new TransferRecordToReverseEntity(activeResume);
		TransferRecordToReverseEntity tryToChangeResumeStatusToActive = new TransferRecordToReverseEntity(inactiveResumeEntity, sendResumeToRecruiters);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				resume 
				, tryToChangeResumeStatusToActive
				, tryToChangeResumeStatusToInactive
				);
		return CcpConstants.EMPTY_JSON;
	}

}
