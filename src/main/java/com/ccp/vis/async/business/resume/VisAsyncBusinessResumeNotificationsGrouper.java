package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeNotificationsGrouper implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeNotificationsGrouper() {}
	
	public static final VisAsyncBusinessResumeNotificationsGrouper INSTANCE = new VisAsyncBusinessResumeNotificationsGrouper();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
