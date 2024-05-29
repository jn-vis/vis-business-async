package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeOpinionChange implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeOpinionChange() {}
	
	public static final VisAsyncBusinessResumeOpinionChange INSTANCE = new VisAsyncBusinessResumeOpinionChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
