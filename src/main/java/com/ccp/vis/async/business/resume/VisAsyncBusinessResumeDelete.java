package com.ccp.vis.async.business.resume;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeDelete implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeDelete() {
		
	}
	
	public static final VisAsyncBusinessResumeDelete INSTANCE = new VisAsyncBusinessResumeDelete();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
