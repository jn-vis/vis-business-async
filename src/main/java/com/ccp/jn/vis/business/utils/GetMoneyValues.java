package com.ccp.jn.vis.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

class GetMoneyValues implements Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> {

	private final String field;
	
	public GetMoneyValues(String field) {
		this.field = field;
	}

	public List<CcpJsonRepresentation> apply(CcpJsonRepresentation json) {

		boolean fieldIsNotPresent = json.containsAllKeys(this.field) == false;
		
		if(fieldIsNotPresent) {
			return new ArrayList<>();
		}

		boolean isCandidateJson = json.containsAllKeys("experience");
		
		if(isCandidateJson) {
			
			List<CcpJsonRepresentation> response = new ArrayList<>();
			
			int valueGaveByCandidate = json.getAsDoubleNumber(this.field).intValue();
			
			for(int k = valueGaveByCandidate; k <= 100000; k += 100) {
				CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", this.field);
				response.add(put);
			}
			
			return response;
		}
		
		List<CcpJsonRepresentation> response = new ArrayList<>();
		
		int maxValueFromThisPosition = json.getAsDoubleNumber(this.field).intValue();
		
		for(int k = maxValueFromThisPosition; k >= 1000; k -= 100) {
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", this.field);
			response.add(put);
		}
		
		return response;
	}
	
	
}
