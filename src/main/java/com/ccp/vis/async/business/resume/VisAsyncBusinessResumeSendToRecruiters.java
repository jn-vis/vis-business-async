package com.ccp.vis.async.business.resume;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.ResumeSendFrequencyOptions;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSendToRecruiters implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSendToRecruiters() {}
	
	public static final VisAsyncBusinessResumeSendToRecruiters INSTANCE = new VisAsyncBusinessResumeSendToRecruiters();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		
		CcpJsonRepresentation resumeWithSkills = VisAsyncUtils.getResumeWithSkills(resume);
		
		JnAsyncCommitAndAudit.INSTANCE.executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(
				resumeWithSkills, VisEntityResume.INSTANCE);
		
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> Arrays.asList(resumeWithSkills);
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(CcpConstants.EMPTY_JSON.put("frequency", ResumeSendFrequencyOptions.minute), function);
		
		return resumeWithSkills;
	}
	
}
