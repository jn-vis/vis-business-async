package com.ccp.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionGrouperNotifications implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessPositionGrouperNotifications INSTANCE = new VisAsyncBusinessPositionGrouperNotifications();
	
	private VisAsyncBusinessPositionGrouperNotifications() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		// TODO Auto-generated method stub
		return null;
	}

}
