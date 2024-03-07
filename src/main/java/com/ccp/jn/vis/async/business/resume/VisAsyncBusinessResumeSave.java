package com.ccp.jn.vis.async.business.resume;

import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {

		List<String> hashes = VisAsyncUtils.saveEntityValue(resume, new VisEntityResume(), CcpConstants.DO_NOTHING);

		this.sendResumeToPositions(resume, hashes);

		return CcpConstants.EMPTY_JSON;
	}

	private void sendResumeToPositions(CcpJsonRepresentation resume, List<String> hashesToInsertIn) {
		List<CcpJsonRepresentation> positions = VisAsyncUtils.getPositionsBySchedullingFrequency(PositionSendFrequency.minute);
		positions.stream().filter(position -> VisAsyncUtils.matches(position, resume));
		/*
		 * TODO thisResumeDoesNotChangedSinceTheLastRecruiterView, inactivePosition, deniedResume, negativetedResume
		 */
	}

}
