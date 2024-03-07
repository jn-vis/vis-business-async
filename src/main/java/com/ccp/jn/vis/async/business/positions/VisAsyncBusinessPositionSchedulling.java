package com.ccp.jn.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionSchedulling implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
	
		Function<CcpJsonRepresentation, CcpJsonRepresentation> function = hash -> hash
				.put("title", position.getAsString("title"))
				.renameKey("email", "recruiter")
				;
		VisAsyncUtils.saveEntityValue(position, new VisEntityPosition(), function);
		return CcpConstants.EMPTY_JSON;
	}

}
/*
	TODO
	visualizaçoes por recrutador
	calculo de reputação
	agrupar vagas por schedulling
	calculo de saldos
*/