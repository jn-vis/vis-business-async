package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public enum GetMoneyValuesFromJson  {
	resume {
		public List<CcpJsonRepresentation> apply(CcpJsonRepresentation json, String field) {
			boolean fieldIsNotPresent = json.containsAllKeys(field) == false;
			
			if(fieldIsNotPresent) {
				return new ArrayList<>();
			}

			List<CcpJsonRepresentation> response = new ArrayList<>();
			
			int valueGaveByCandidate = json.getAsDoubleNumber(field).intValue();
			
			for(int k = valueGaveByCandidate; k <= 100000; k += 100) {
				CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", field);
				response.add(put);
			}
			
			return response;
		}
	}, position {
		public List<CcpJsonRepresentation> apply(CcpJsonRepresentation json, String field) {
			boolean fieldIsNotPresent = json.containsAllKeys(field) == false;
			
			if(fieldIsNotPresent) {
				return new ArrayList<>();
			}

			List<CcpJsonRepresentation> response = new ArrayList<>();
			
			int maxValueFromThisPosition = json.getAsDoubleNumber(field).intValue();
			
			for(int k = maxValueFromThisPosition; k >= 1000; k -= 100) {
				CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("moneyValue", k).put("moneyType", field);
				response.add(put);
			}
			
			return response;
		}
	};

	public abstract List<CcpJsonRepresentation> apply(CcpJsonRepresentation json, String field);
	
}
