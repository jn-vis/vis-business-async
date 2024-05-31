package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;

public class VisAsyncBusinessResumeOpinionChange implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeOpinionChange() {}
	
	public static final VisAsyncBusinessResumeOpinionChange INSTANCE = new VisAsyncBusinessResumeOpinionChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		VisAsyncUtils.changeStatus(json, VisEntityResumeOpinion.INSTANCE, VisAsyncBusinessResumeSave.INSTANCE, CcpConstants.DO_NOTHING);
		return json;
	}

}
