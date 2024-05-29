package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeViewsRecruiterGrouper implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeViewsRecruiterGrouper() {}
	
	public static final VisAsyncBusinessResumeViewsRecruiterGrouper INSTANCE = new VisAsyncBusinessResumeViewsRecruiterGrouper();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
