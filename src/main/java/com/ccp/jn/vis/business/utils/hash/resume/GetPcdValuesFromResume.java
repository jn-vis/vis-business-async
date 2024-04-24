package com.ccp.jn.vis.business.utils.hash.resume;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetPcdValuesFromResume implements Function<CcpJsonRepresentation, List<Boolean>>{

	public List<Boolean> apply(CcpJsonRepresentation json) {

		boolean pcdCandidate = json.getAsBoolean("pcd");

		if(pcdCandidate) {
			// Candidatos PCD podem competir a vagas normais e a vagas PCD's.
			return Arrays.asList(true, false);
		}
		// Candidatos normais podem competir apenas a vagas normais.
		return Arrays.asList(false);
	}

}
