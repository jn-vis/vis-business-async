package com.ccp.jn.vis.async.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

class GetDisponibilityValues implements Function<CcpJsonRepresentation, List<Integer>> {

	public List<Integer> apply(CcpJsonRepresentation json) {

		boolean isCandidateJson = json.containsAllKeys("experience");
		
		if(isCandidateJson) {
			
			List<Integer> response = new ArrayList<>();
			
			int end = json.getAsDoubleNumber("disponibility").intValue();
			
			for(int k = 0; k <= end; k--) {
				response.add(k);
			}
			
			return response;
		}
		
		List<Integer> response = new ArrayList<>();
		
		int start = json.getAsDoubleNumber("disponibility").intValue();
		
		for(int k = start; k <= 70; k++) {
			response.add(k);
		}
		
		return response;
	}
	
	
}
