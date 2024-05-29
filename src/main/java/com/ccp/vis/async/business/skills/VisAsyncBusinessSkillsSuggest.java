package com.ccp.vis.async.business.skills;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessSkillsSuggest implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessSkillsSuggest() {}
	
	public static final VisAsyncBusinessSkillsSuggest INSTANCE = new VisAsyncBusinessSkillsSuggest();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}

}
