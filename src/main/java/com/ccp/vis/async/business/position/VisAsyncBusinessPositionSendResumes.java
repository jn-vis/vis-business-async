package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionSendResumes implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionSendResumes() {}
	
	public static final VisAsyncBusinessPositionSendResumes INSTANCE = new VisAsyncBusinessPositionSendResumes();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
