package com.ccp.jn.vis.business.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

class GetPcdValues implements Function<CcpJsonRepresentation, List<Boolean>>{

	public List<Boolean> apply(CcpJsonRepresentation json) {

		boolean isCandidateJson = json.containsAllKeys("experience");
		
		boolean pcd = json.getAsBoolean("pcd");

		if(isCandidateJson) {
			
			if(pcd) {
				return Arrays.asList(true, false);
			}
			return Arrays.asList(false);
		}
		
		if(pcd) {
			return Arrays.asList(true);
		}

		return Arrays.asList(true, false);
	}

}
