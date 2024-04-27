package com.ccp.vis.async.commons.hash.positions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetDisponibilityValuesFromResume implements Function<CcpJsonRepresentation, List<Integer>> {
	
	public List<Integer> apply(CcpJsonRepresentation json) {

		List<Integer> response = new ArrayList<>();
		
		int end = json.getAsDoubleNumber("disponibility").intValue();
		
		for(int k = end; k <= 70; k++) {
			response.add(k);
		}
		
		return response;
	}
	
	
}
