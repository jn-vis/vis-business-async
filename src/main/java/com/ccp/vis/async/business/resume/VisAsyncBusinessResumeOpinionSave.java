package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeOpinionSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeOpinionSave() {}
	
	public static final VisAsyncBusinessResumeOpinionSave INSTANCE = new VisAsyncBusinessResumeOpinionSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
