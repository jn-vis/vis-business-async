package com.ccp.vis.async.business.position;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.ResumeSendFrequencyOptions;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionResumesReceiving  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessPositionResumesReceiving() {}
	
	public static final VisAsyncBusinessPositionResumesReceiving INSTANCE = new VisAsyncBusinessPositionResumesReceiving();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {

		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, ResumeSendFrequencyOptions.valueOf(x.getAsString("frequency")));
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(schedullingPlan, function);
	
		return schedullingPlan;
	}
}
