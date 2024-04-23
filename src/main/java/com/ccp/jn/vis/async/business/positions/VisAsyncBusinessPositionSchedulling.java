package com.ccp.jn.vis.async.business.positions;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionSchedulling implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
	
		Function<CcpJsonRepresentation, CcpJsonRepresentation> function = hash -> {
			String title = position.getAsString("title");
			return hash
					.renameKey("email", "recruiter")
					.put("title", title)
					;
		}
				;
		VisEntityPosition entity = new VisEntityPosition();
		VisAsyncUtils.saveEntityValue(position, entity, function);
		return CcpConstants.EMPTY_JSON;
	}

}
/*
	TODO calculo de reputação
*/