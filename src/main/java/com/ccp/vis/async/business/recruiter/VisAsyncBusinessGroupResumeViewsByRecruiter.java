package com.ccp.vis.async.business.recruiter;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessGroupResumeViewsByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByRecruiter() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByRecruiter INSTANCE = new VisAsyncBusinessGroupResumeViewsByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
