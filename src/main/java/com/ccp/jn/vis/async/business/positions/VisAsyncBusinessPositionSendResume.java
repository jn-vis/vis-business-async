package com.ccp.jn.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessPositionSendResume  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		return CcpConstants.EMPTY_JSON;
	}

}
