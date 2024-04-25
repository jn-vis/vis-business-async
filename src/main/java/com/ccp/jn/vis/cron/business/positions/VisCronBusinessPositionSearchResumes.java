package com.ccp.jn.vis.cron.business.positions;

import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisCronBusinessPositionSearchResumes  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {

		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, PositionSendFrequency.valueOf(x.getAsString("frequency")));
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(schedullingPlan, function);
	
		return CcpConstants.EMPTY_JSON;
	}
}
