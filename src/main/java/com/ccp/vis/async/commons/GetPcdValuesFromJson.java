package com.ccp.vis.async.commons;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public enum GetPcdValuesFromJson implements Function<CcpJsonRepresentation, List<Boolean>> {
	resume {
		public List<Boolean> apply(CcpJsonRepresentation json) {
			boolean pcdCandidate = json.getAsBoolean("pcd");

			if(pcdCandidate) {
				// Candidatos PCD podem competir a vagas normais e a vagas PCD's.
				return Arrays.asList(true, false);
			}
			// Candidatos normais podem competir apenas a vagas normais.
			return Arrays.asList(false);
		}
	}, position {
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
	};

	public abstract List<Boolean> apply(CcpJsonRepresentation json);
	
}
