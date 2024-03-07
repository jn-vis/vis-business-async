package com.ccp.jn.vis.async.business.resume;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.dao.CcpDaoUnionAll;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;
import com.jn.vis.commons.entities.VisEntityResumeView;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {

		List<String> hashes = VisAsyncUtils.saveEntityValue(resume, new VisEntityResume(), CcpConstants.DO_NOTHING);

		this.sendResumeToPositions(resume, hashes);

		return CcpConstants.EMPTY_JSON;
	}

	private void sendResumeToPositions(CcpJsonRepresentation resume, List<String> hashesToInsertIn) {
		
		List<CcpJsonRepresentation> positions = VisAsyncUtils.getPositionsBySchedullingFrequency(PositionSendFrequency.minute);
		
		String email = resume.getAsString("email");
		
		Set<String> recruiters = new ArrayList<>(positions).stream().map(position -> position.getAsString("email")).collect(Collectors.toSet());
		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		List<CcpJsonRepresentation> allSearchParameters = recruiters.stream().map(recruiter -> CcpConstants.EMPTY_JSON
				.put("domain",  recruiter.split("@")[1])
				.put("recruiter", recruiter)
				.put("email", email)
				).collect(Collectors.toList());
		VisEntityPosition visEntityPosition = new VisEntityPosition();
		
		VisEntityResumeView visEntityResumeView = new VisEntityResumeView();
		VisEntityResumeNegativeted visEntityResumeNegativeted = new VisEntityResumeNegativeted();
		VisEntityDeniedViewToCompany visEntityDeniedViewToCompany = new VisEntityDeniedViewToCompany();
		
		CcpDaoUnionAll searchResults = dao.unionAll(
				allSearchParameters
				,visEntityDeniedViewToCompany
				,visEntityResumeNegativeted
				,visEntityResumeView
				);
		List<CcpJsonRepresentation> ablePositionsToThisResume = new ArrayList<>();

		for (CcpJsonRepresentation searchParameters : allSearchParameters) {
			
			boolean inactivePosition = searchResults.isPresent(visEntityPosition, searchParameters) == false;
			
			if(inactivePosition) {
				//TODO SALVAR ESSA OCORRENCIA
				continue;
			}

			boolean negativetedResume = searchResults.isPresent(visEntityResumeNegativeted, searchParameters);
			
			if(negativetedResume) {
				//TODO SALVAR ESSA OCORRENCIA
				continue;
			}

			boolean deniedResume = searchResults.isPresent(visEntityDeniedViewToCompany, searchParameters);
			
			if(deniedResume) {
				//TODO SALVAR ESSA OCORRENCIA
				continue;
			}
			
			
			boolean thisResumeNeverHasSeenBefore = searchResults.isPresent(visEntityResumeView, searchParameters) == false;

			CcpJsonRepresentation position = searchResults.get(visEntityPosition, searchParameters);
			
			boolean doesNotMatch = VisAsyncUtils.matches(position, resume) == false;
			
			if(doesNotMatch) {
				continue;
			}

			if(thisResumeNeverHasSeenBefore) {
				ablePositionsToThisResume.add(position);
				continue;
			}
			
			CcpJsonRepresentation resumeView = searchResults.get(visEntityResumeView, searchParameters);
			
			Long resumeLastView = resumeView.getAsLongNumber("lastView");
			Long resumeLastUpdate = resume.getAsLongNumber("lastUpdate");
			boolean thisResumeDoesNotChangedSinceTheLastRecruiterView = resumeLastView > resumeLastUpdate;
			
			if(thisResumeDoesNotChangedSinceTheLastRecruiterView) {
				continue;
			}
			
			ablePositionsToThisResume.add(position);
		}

		
		/*
		 * TODO sendResumeToThisPosition
		 */
	}

}
