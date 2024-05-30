package com.ccp.vis.async.commons;

import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes() {}
	
	public static final VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes INSTANCE = new VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		List<String> email = json.getAsStringList("email");
		
		VisAsyncUtils.groupPositionsByRecruiters(email);
		
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getLastUpdatedResumes = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, ResumeSendFrequencyOptions.valueOf(x.getAsString("frequency")));
		
		Function<String, CcpJsonRepresentation> getSavingPosition = frequency -> CcpConstants.EMPTY_JSON.put(email.get(0), json);

		List<CcpJsonRepresentation> positionsWithFilteredAndSortedResumesAndTheirStatis = VisAsyncUtils.sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(json, getLastUpdatedResumes, getSavingPosition);
		
		CcpJsonRepresentation positionWithFilteredAndSortedResumesAndTheirStatis = positionsWithFilteredAndSortedResumesAndTheirStatis.get(0);
		
		return positionWithFilteredAndSortedResumesAndTheirStatis;
	}

}
