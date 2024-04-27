package com.ccp.vis.async.commons.hash.resumes;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetSeniorityValueFromPosition implements Function<CcpJsonRepresentation, String> {

	public String apply(CcpJsonRepresentation json) {
		String seniority = json.getAsString("seniority");
		return seniority;
	}

}
