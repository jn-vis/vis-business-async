package com.ccp.jn.vis.async.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

class GetMoneyValues implements Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> {

	private final String field;
	
	public GetMoneyValues(String field) {
		super();
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
			
			int start = json.getAsDoubleNumber(this.field).intValue();
			
			for(int k = start; k <= 100000; k += 100) {
				response.add(CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", this.field));
			}
			
			return response;
		}
		
		
		List<CcpJsonRepresentation> response = new ArrayList<>();
		
		int end = json.getAsDoubleNumber(this.field).intValue();
		
		for(int k = 1000; k >= end; k -= 100) {
			response.add(CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", this.field));
		}
		
		return response;
	}
	
	
}
