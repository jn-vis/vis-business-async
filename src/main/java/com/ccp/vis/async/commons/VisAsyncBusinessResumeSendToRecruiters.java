package com.ccp.vis.async.commons;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class VisAsyncBusinessResumeSendToRecruiters implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private VisAsyncBusinessResumeSendToRecruiters() {}
	
	public static final VisAsyncBusinessResumeSendToRecruiters INSTANCE = new VisAsyncBusinessResumeSendToRecruiters();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resumeWithSkills) {
		
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getSavingResume = x -> Arrays.asList(resumeWithSkills);
		
		Function<String, CcpJsonRepresentation> getLastUpdatedPositions = frequency -> VisAsyncUtils.getAllPositionsGroupedByRecruiters(FrequencyOptions.valueOf(frequency));
		
		VisAsyncUtils.sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(CcpOtherConstants.EMPTY_JSON.put("frequency", FrequencyOptions.minute), getSavingResume, getLastUpdatedPositions);
		
		return resumeWithSkills;
	}
	
}
