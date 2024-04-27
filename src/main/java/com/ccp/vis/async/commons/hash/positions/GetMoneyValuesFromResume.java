package com.ccp.vis.async.commons.hash.positions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class GetMoneyValuesFromResume implements Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> {

	private final String field;
	
	public GetMoneyValuesFromResume(String field) {
		this.field = field;
	}

	public List<CcpJsonRepresentation> apply(CcpJsonRepresentation json) {

		boolean fieldIsNotPresent = json.containsAllKeys(this.field) == false;
		
		if(fieldIsNotPresent) {
			return new ArrayList<>();
		}

		List<CcpJsonRepresentation> response = new ArrayList<>();
		
		int valueGaveByCandidate = json.getAsDoubleNumber(this.field).intValue();
		
		for(int k = valueGaveByCandidate; k <= 100000; k += 100) {
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", this.field);
			response.add(put);
		}
		
		return response;
	}
	
	
}
