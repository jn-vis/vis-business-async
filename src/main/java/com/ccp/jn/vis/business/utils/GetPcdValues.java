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
			boolean pcdCandidate = pcd;
			if(pcdCandidate) {
				// Candidatos PCD podem competir a vagas normais e a vagas PCD's.
				return Arrays.asList(true, false);
			}
			// Candidatos normais podem competir apenas a vagas normais.
			return Arrays.asList(false);
		}
		boolean pcdPosition = pcd;
		// Vagas PCD podem filtrar apenas vagas PCD's.
		if(pcdPosition) {
			return Arrays.asList(true);
		}
		// Vagas normais podem filtrar candidatos PCD's e candidatos normais.
		return Arrays.asList(true, false);
	}

}
