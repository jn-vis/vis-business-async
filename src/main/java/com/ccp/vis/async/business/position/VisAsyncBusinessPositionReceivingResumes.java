package com.ccp.vis.async.business.position;

import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.ResumeSendFrequencyOptions;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionReceivingResumes  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private VisAsyncBusinessPositionReceivingResumes() {
		
	}
	
	public static final VisAsyncBusinessPositionReceivingResumes INSTANCE = new VisAsyncBusinessPositionReceivingResumes();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {

		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, ResumeSendFrequencyOptions.valueOf(x.getAsString("frequency")));
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(schedullingPlan, function);
	
		return CcpConstants.EMPTY_JSON;
	}
}
