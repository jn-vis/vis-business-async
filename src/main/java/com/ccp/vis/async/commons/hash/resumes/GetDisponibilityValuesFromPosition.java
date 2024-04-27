package com.ccp.vis.async.commons.hash.resumes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetDisponibilityValuesFromPosition implements Function<CcpJsonRepresentation, List<Integer>> {
	
	public List<Integer> apply(CcpJsonRepresentation json) {

		List<Integer> response = new ArrayList<>();
		
		int maxDisponibility = json.getAsDoubleNumber("disponibility").intValue();
		
		for(int k = maxDisponibility; k >= 0; k--) {
			response.add(k);
		}
		
		return response;
	}
	
	
}
