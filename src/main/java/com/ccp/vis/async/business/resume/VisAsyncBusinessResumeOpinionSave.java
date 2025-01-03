package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.vis.commons.entities.VisEntityResumePerception;

public class VisAsyncBusinessResumeOpinionSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeOpinionSave() {}
	
	public static final VisAsyncBusinessResumeOpinionSave INSTANCE = new VisAsyncBusinessResumeOpinionSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndTwinEntities(
				json, VisEntityResumePerception.ENTITY, 
				CcpOtherConstants.DO_NOTHING);
		
		return json;
	}
	
}
