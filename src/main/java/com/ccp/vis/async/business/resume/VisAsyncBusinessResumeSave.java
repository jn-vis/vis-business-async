package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.VisAsyncBusinessResumeSendToRecruiters;
import com.jn.vis.commons.utils.VisCommonsUtils;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSave() {}
	
	public static final VisAsyncBusinessResumeSave INSTANCE = new VisAsyncBusinessResumeSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation jsonWithSkills = VisCommonsUtils.getJsonWithSkills(
				json
				, VisEntityResume.Fields.resumeText.name()
				, VisEntityResume.Fields.skill.name()
				);
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(
				jsonWithSkills, VisEntityResume.INSTANCE, 
				VisAsyncBusinessResumeSendToRecruiters.INSTANCE);
		
		return jsonWithSkills;
	}
	
}
