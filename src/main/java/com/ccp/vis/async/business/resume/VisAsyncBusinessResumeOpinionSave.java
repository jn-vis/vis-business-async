package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;

public class VisAsyncBusinessResumeOpinionSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeOpinionSave() {}
	
	public static final VisAsyncBusinessResumeOpinionSave INSTANCE = new VisAsyncBusinessResumeOpinionSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(
				json, VisEntityResumeOpinion.INSTANCE, 
				CcpConstants.DO_NOTHING);
		
		return json;
	}
	
}
