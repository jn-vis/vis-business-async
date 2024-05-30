package com.ccp.vis.async.business.position;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.ResumeSendFrequencyOptions;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionResumesReceivingByFrequency  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessPositionResumesReceivingByFrequency() {}
	
	public static final VisAsyncBusinessPositionResumesReceivingByFrequency INSTANCE = new VisAsyncBusinessPositionResumesReceivingByFrequency();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {

		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getLastUpdatedResumes = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, ResumeSendFrequencyOptions.valueOf(x.getAsString("frequency")));

		Function<String, CcpJsonRepresentation> getLastUpdatedPositions = frequency -> VisAsyncUtils.getAllPositionsGroupedByRecruiters(ResumeSendFrequencyOptions.valueOf(frequency));

		VisAsyncUtils.sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(schedullingPlan, getLastUpdatedResumes, getLastUpdatedPositions);
	
		return schedullingPlan;
	}
}
