package com.ccp.jn.vis.business.utils.hash.position;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetSenioritiesValuesFromPosition implements Function<CcpJsonRepresentation, List<String>> {

	public List<String> apply(CcpJsonRepresentation json) {
		List<String> seniorities = json.getAsStringList("seniority");
		return seniorities;
	}

}
