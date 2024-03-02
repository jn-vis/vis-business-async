package com.ccp.jn.vis.async.business.positions;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityPositionSchedulleSendResumes;

public class VisAsyncBusinessPositionSchedulling implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		//TODO um save só
		new VisEntityPosition().createOrUpdate(position);
		new VisEntityPositionSchedulleSendResumes().createOrUpdate(position);
		return CcpConstants.EMPTY_JSON;
	}

}
