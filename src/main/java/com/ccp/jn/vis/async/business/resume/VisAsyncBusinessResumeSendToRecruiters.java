package com.ccp.jn.vis.async.business.resume;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;

public class VisAsyncBusinessResumeSendToRecruiters implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> function = x -> Arrays.asList(resume);
		
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(CcpConstants.EMPTY_JSON.put("frequency", PositionSendFrequency.minute), function);
		
		return CcpConstants.EMPTY_JSON;
	}
}
