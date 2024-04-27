package com.ccp.vis.async.business.positions;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionInactivate  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessPositionInactivate() {
		
	}
	
	public static final VisAsyncBusinessPositionInactivate INSTANCE = new VisAsyncBusinessPositionInactivate();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return null;
	}

}
