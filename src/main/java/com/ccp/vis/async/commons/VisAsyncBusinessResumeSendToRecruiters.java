package com.ccp.vis.async.commons;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeSendToRecruiters implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSendToRecruiters() {}
	
	public static final VisAsyncBusinessResumeSendToRecruiters INSTANCE = new VisAsyncBusinessResumeSendToRecruiters();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resumeWithSkills) {
		
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> Arrays.asList(resumeWithSkills);
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(CcpConstants.EMPTY_JSON.put("frequency", ResumeSendFrequencyOptions.minute), function);
		
		return resumeWithSkills;
	}
	
}
