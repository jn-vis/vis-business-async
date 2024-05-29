package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeBucketGet implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeBucketGet() {}
	
	public static final VisAsyncBusinessResumeBucketGet INSTANCE = new VisAsyncBusinessResumeBucketGet();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
