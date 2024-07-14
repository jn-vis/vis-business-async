package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.VisAsyncBusinessResumeSendToRecruiters;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeStatusChange  implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeStatusChange() {}
	
	public static final VisAsyncBusinessResumeStatusChange INSTANCE = new VisAsyncBusinessResumeStatusChange();
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		TransferRecordToReverseEntity executeUnlock = new TransferRecordToReverseEntity(
				VisEntityResume.INSTANCE, 
				VisAsyncBusinessResumeSendToRecruiters.INSTANCE, 
				CcpConstants.DO_NOTHING
				);
	
		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, executeUnlock
				);
		
		return json;
	}


}
