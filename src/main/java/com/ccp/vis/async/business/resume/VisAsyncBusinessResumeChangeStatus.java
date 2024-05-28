package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeChangeStatus  implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeChangeStatus() {}
	
	public static final VisAsyncBusinessResumeChangeStatus INSTANCE = new VisAsyncBusinessResumeChangeStatus();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		VisAsyncUtils.changeStatus(json,  VisEntityResume.INSTANCE, VisAsyncBusinessResumeSave.INSTANCE);

		return json;
	}


}
