package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeViewsGrouperByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeViewsGrouperByRecruiter() {
		
	}
	
	public static final VisAsyncBusinessResumeViewsGrouperByRecruiter INSTANCE = new VisAsyncBusinessResumeViewsGrouperByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
