package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.VisAsyncBusinessResumeSendToRecruiters;
import com.vis.commons.cache.tasks.PutSkillsInJson;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSave() {}
	
	public static final VisAsyncBusinessResumeSave INSTANCE = new VisAsyncBusinessResumeSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		PutSkillsInJson putSkillsInJson = new PutSkillsInJson("resumeText", VisEntityResume.Fields.skill.name());
		
		CcpJsonRepresentation jsonWithSkills = json.extractInformationFromJson(putSkillsInJson);
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndTwinEntities(
				jsonWithSkills, 
				VisEntityResume.ENTITY, 
				VisAsyncBusinessResumeSendToRecruiters.INSTANCE
				);
		
		return jsonWithSkills;
	}
	
}
