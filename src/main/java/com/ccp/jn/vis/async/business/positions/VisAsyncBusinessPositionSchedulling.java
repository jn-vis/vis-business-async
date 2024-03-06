package com.ccp.jn.vis.async.business.positions;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.CalculateHashes;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityPositionSchedulleSendResumes;

public class VisAsyncBusinessPositionSchedulling implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		//TODO um save só
	
		new VisEntityPosition().createOrUpdate(position);
		List<String> hashes = CalculateHashes.getHashes(position).stream().map(x -> x.getAsString("_id")).collect(Collectors.toList());
		CcpJsonRepresentation put = position.put("hash", hashes);
		new VisEntityPositionSchedulleSendResumes().createOrUpdate(put);
		return CcpConstants.EMPTY_JSON;
	}

}
/*
	visualizaçoes por recrutador
	calculo de hashes dos curriculos
	calculo de hashes das vagas
	calculo de reputação
	agrupar vagas por schedulling
	calculo de saldos
*/