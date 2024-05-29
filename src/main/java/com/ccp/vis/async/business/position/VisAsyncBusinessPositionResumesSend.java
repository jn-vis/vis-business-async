package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionResumesSend implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionResumesSend() {}
	
	public static final VisAsyncBusinessPositionResumesSend INSTANCE = new VisAsyncBusinessPositionResumesSend();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
