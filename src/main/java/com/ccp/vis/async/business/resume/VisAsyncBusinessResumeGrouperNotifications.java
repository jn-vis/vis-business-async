package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeGrouperNotifications implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeGrouperNotifications() {
		
	}
	
	public static final VisAsyncBusinessResumeGrouperNotifications INSTANCE = new VisAsyncBusinessResumeGrouperNotifications();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
