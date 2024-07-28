package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeStatusChange  implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeStatusChange() {}
	
	public static final VisAsyncBusinessResumeStatusChange INSTANCE = new VisAsyncBusinessResumeStatusChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
	
		JnAsyncCommitAndAudit.INSTANCE.
		changeStatus(json, VisEntityResume.INSTANCE)
		;
		
		return json;
	}


}
