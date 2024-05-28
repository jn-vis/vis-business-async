package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionListingResumes implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionListingResumes() {}
	
	public static final VisAsyncBusinessPositionListingResumes INSTANCE = new VisAsyncBusinessPositionListingResumes();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
