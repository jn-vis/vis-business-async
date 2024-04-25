package com.ccp.jn.vis.async.business.resume;

import java.util.Arrays;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;

public class VisAsyncBusinessResumeSendToRecruiters implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		VisAsyncUtils.sendFilteredResumesByEachPositionToEachRecruiter(CcpConstants.EMPTY_JSON.put("frequency", PositionSendFrequency.minute), x -> Arrays.asList(resume));
		
		return CcpConstants.EMPTY_JSON;
	}
}