package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessGroupResumeViewsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByResume() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByResume INSTANCE = new VisAsyncBusinessGroupResumeViewsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
