package com.ccp.vis.async.business.recruiter;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessGroupResumesOpinionsByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumesOpinionsByRecruiter() {}
	
	public static final VisAsyncBusinessGroupResumesOpinionsByRecruiter INSTANCE = new VisAsyncBusinessGroupResumesOpinionsByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
