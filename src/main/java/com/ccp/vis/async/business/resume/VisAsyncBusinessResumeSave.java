package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.VisAsyncBusinessResumeSendToRecruiters;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSave() {}
	
	public static final VisAsyncBusinessResumeSave INSTANCE = new VisAsyncBusinessResumeSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		
		CcpJsonRepresentation resumeWithSkills = VisAsyncUtils.getResumeWithSkills(resume);
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(
				resumeWithSkills, VisEntityResume.INSTANCE, 
				VisAsyncBusinessResumeSendToRecruiters.INSTANCE);
		
		return resumeWithSkills;
	}
	
}
