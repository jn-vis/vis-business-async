package com.ccp.vis.async.business.recruiter;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeViewAdd implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeViewAdd() {}
	
	public static final VisAsyncBusinessResumeViewAdd INSTANCE = new VisAsyncBusinessResumeViewAdd();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
