package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessGroupSkills implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupSkills() {}
	
	public static final VisAsyncBusinessGroupSkills INSTANCE = new VisAsyncBusinessGroupSkills();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
