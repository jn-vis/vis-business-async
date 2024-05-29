package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionNotificationsGrouper implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessPositionNotificationsGrouper INSTANCE = new VisAsyncBusinessPositionNotificationsGrouper();
	
	private VisAsyncBusinessPositionNotificationsGrouper() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
