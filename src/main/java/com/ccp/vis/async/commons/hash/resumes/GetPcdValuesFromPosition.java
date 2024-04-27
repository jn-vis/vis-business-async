package com.ccp.vis.async.commons.hash.resumes;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetPcdValuesFromPosition implements Function<CcpJsonRepresentation, List<Boolean>>{

	public List<Boolean> apply(CcpJsonRepresentation json) {

		boolean pcd = json.getAsBoolean("pcd");

		boolean pcdPosition = pcd;
		// Vagas PCD podem filtrar apenas vagas PCD's.
		if(pcdPosition) {
			return Arrays.asList(true);
		}
		// Vagas normais podem filtrar candidatos PCD's e candidatos normais.
		return Arrays.asList(true, false);
	}

}
