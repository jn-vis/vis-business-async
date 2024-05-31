package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessGroupResumesOpinionsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumesOpinionsByResume() {}
	
	public static final VisAsyncBusinessGroupResumesOpinionsByResume INSTANCE = new VisAsyncBusinessGroupResumesOpinionsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
