package com.ccp.jn.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionSchedulling implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
	
		Function<CcpJsonRepresentation, CcpJsonRepresentation> function = hash -> {
			String title = position.getAsString("title");
			CcpJsonRepresentation renameKey = hash.renameKey("email", "recruiter");
			CcpJsonRepresentation put = renameKey.put("title", title);
			return put;
		};
		VisEntityPosition entityPosition = new VisEntityPosition();
		VisAsyncUtils.saveEntityValue(position, entityPosition, function);
		return CcpConstants.EMPTY_JSON;
	}

}
/*
	TODO calculo de reputação
*/