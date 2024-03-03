package com.ccp.jn.vis.async.business.positions;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.CalculateHashes;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityPositionSchedulleSendResumes;
import com.jn.vis.commons.entities.VisEntityResumeHash;

public class VisAsyncBusinessPositionSchedulling implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		//TODO um save só
	
		VisEntityResumeHash visEntityResumeHash = new VisEntityResumeHash();

		new VisEntityPosition().createOrUpdate(position);
		List<String> hashes = CalculateHashes.getHashes(position).stream()
				.map(x -> visEntityResumeHash.getId(x))
				.collect(Collectors.toList());
		CcpJsonRepresentation put = position.put("hash", hashes);
		new VisEntityPositionSchedulleSendResumes().createOrUpdate(put);
		return CcpConstants.EMPTY_JSON;
	}

}
