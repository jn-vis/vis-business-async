package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncBusinessResumeSendToRecruiters;
import com.jn.commons.utils.JnCommonsExecuteBulkOperation;
import com.vis.commons.cache.tasks.PutSkillsInJson;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSave() {}
	
	public static final VisAsyncBusinessResumeSave INSTANCE = new VisAsyncBusinessResumeSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		PutSkillsInJson putSkillsInJson = new PutSkillsInJson("resumeText", VisEntityResume.Fields.skill.name());
		
		CcpJsonRepresentation jsonWithSkills = json.extractInformationFromJson(putSkillsInJson);
		
		JnCommonsExecuteBulkOperation.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndTwinEntities(
				jsonWithSkills, 
				VisEntityResume.ENTITY, 
				VisAsyncBusinessResumeSendToRecruiters.INSTANCE
				);
		
		return jsonWithSkills;
	}
	
}
