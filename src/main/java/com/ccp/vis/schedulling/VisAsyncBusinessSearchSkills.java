package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessSearchSkills implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessSearchSkills() {}
	
	public static final VisAsyncBusinessSearchSkills INSTANCE = new VisAsyncBusinessSearchSkills();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
