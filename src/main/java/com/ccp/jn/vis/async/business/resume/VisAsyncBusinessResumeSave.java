package com.ccp.jn.vis.async.business.resume;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.dao.CcpDaoUnionAll;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;

public class VisAsyncBusinessResumeSave implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {

		VisEntityResume entityResume = new VisEntityResume();
		// Vis.1.1
		List<String> hashes = VisAsyncUtils.calculateHashesAndSaveEntity(resume, entityResume);
		// Vis.1.3
		this.sendResumeToPositions(resume, hashes);
		
		return CcpConstants.EMPTY_JSON;
	}

	private void sendResumeToPositions(CcpJsonRepresentation resume, List<String> hashesToInsertIn) {
		
		List<CcpJsonRepresentation> instantlyPositions = VisAsyncUtils.getPositionsBySchedullingFrequency(PositionSendFrequency.minute);
		
		String email = resume.getAsString("email");
		
		Set<String> recruiters = new ArrayList<>(instantlyPositions).stream().map(position -> position.getAsString("email")).collect(Collectors.toSet());
		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		List<CcpJsonRepresentation> allSearchParameters = recruiters.stream().map(recruiter -> CcpConstants.EMPTY_JSON
				.put("domain", new CcpStringDecorator(recruiter).email().getProfessionalDomain())
				.put("recruiter", recruiter)
				.put("email", email)
				).collect(Collectors.toList());
		VisEntityPosition visEntityPosition = new VisEntityPosition();
		
		VisEntityResumeNegativeted visEntityResumeNegativeted = new VisEntityResumeNegativeted();
		VisEntityDeniedViewToCompany visEntityDeniedViewToCompany = new VisEntityDeniedViewToCompany();
		
		CcpDaoUnionAll searchResults = dao.unionAll(
				allSearchParameters
				,visEntityDeniedViewToCompany
				,visEntityResumeNegativeted
				,visEntityPosition
				);
		List<CcpJsonRepresentation> ablePositionsToThisResume = new ArrayList<>();

		for (CcpJsonRepresentation searchParameters : allSearchParameters) {
			
			boolean inactivePosition = searchResults.isPresent(visEntityPosition, searchParameters) == false;
			
			if(inactivePosition) {
				continue;
			}

			boolean negativetedResume = searchResults.isPresent(visEntityResumeNegativeted, searchParameters);
			
			if(negativetedResume) {
				continue;
			}

			boolean deniedResume = searchResults.isPresent(visEntityDeniedViewToCompany, searchParameters);
			
			if(deniedResume) {
				continue;
			}

			CcpJsonRepresentation position = searchResults.get(visEntityPosition, searchParameters);

			boolean doesNotMatch = VisAsyncUtils.matches(position, resume) == false;
			
			if(doesNotMatch) {
				continue;
			}

			ablePositionsToThisResume.add(position);
		}
	}

}
