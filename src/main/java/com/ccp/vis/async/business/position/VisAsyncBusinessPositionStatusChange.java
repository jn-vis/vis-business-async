package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionStatusChange implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionStatusChange() {}
	
	public static final VisAsyncBusinessPositionStatusChange INSTANCE = new VisAsyncBusinessPositionStatusChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		//TODO ação pós reativar vaga???
		VisAsyncUtils.changeStatus(json,  VisEntityPosition.INSTANCE, CcpConstants.DO_BY_PASS);
		return json;
	}

}
