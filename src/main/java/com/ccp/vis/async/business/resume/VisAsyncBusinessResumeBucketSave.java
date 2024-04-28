package com.ccp.vis.async.business.resume;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeBucketSave implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeBucketSave() {
		
	}
	
	public static final VisAsyncBusinessResumeBucketSave INSTANCE = new VisAsyncBusinessResumeBucketSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
