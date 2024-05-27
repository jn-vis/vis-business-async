package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessSkillsSuggest implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessSkillsSuggest() {}
	
	public static final VisAsyncBusinessSkillsSuggest INSTANCE = new VisAsyncBusinessSkillsSuggest();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return CcpConstants.EMPTY_JSON;
	}

}
