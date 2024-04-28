package com.ccp.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionGrouperNotifications implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessPositionGrouperNotifications INSTANCE = new VisAsyncBusinessPositionGrouperNotifications();
	
	private VisAsyncBusinessPositionGrouperNotifications() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		return CcpConstants.EMPTY_JSON;
	}

}
