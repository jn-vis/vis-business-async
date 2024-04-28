package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
// Vis.1.2
public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessResumeSave() {
		
	}
	
	public static final VisAsyncBusinessResumeSave INSTANCE = new VisAsyncBusinessResumeSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
