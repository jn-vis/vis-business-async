package com.ccp.vis.schedulling;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.FrequencyOptions;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.vis.commons.entities.VisEntityPosition;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionResumesReceivingByFrequency  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessPositionResumesReceivingByFrequency() {}
	
	public static final VisAsyncBusinessPositionResumesReceivingByFrequency INSTANCE = new VisAsyncBusinessPositionResumesReceivingByFrequency();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {

		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getLastUpdatedResumes = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.ENTITY, FrequencyOptions.valueOf(x.getAsString("frequency")), VisEntityPosition.Fields.timestamp.name());

		Function<String, CcpJsonRepresentation> getLastUpdatedPositions = frequency -> VisAsyncUtils.getAllPositionsGroupedByRecruiters(FrequencyOptions.valueOf(frequency));

		VisAsyncUtils.sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(schedullingPlan, getLastUpdatedResumes, getLastUpdatedPositions);
	
		return schedullingPlan;
	}
}
