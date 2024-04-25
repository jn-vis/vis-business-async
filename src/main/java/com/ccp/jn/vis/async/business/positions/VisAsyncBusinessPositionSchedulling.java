package com.ccp.jn.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionSchedulling implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		VisEntityPosition.INSTANCE.createOrUpdate(position);
		//TODO FALTA TODOS OS OUTROS PASSOS AQUI
		return CcpConstants.EMPTY_JSON;
	}

}
/*
	TODO calculo de reputação
*/