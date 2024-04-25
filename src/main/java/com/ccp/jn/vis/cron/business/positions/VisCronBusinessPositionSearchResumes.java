package com.ccp.jn.vis.cron.business.positions;

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
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;
import com.jn.vis.commons.entities.VisEntityResumeView;

public class VisCronBusinessPositionSearchResumes  implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {
		/*
		 *[X] A) Filtrar vagas desta frequencia, criar lista de vagas individualizada para cada recrutador
		 *[X] B) Trazer candidatos atualizados recentemente
		 *[X] C) Verificar afinidades entre recrutadores e candidatos e candidatos contra vagas e recrutadores que tenham saldo cadastrado e que o saldo seja superior ao custo dos envios somados das vagas que ele cadastrou pra essa frequencia
		 *[ ] D) Enviar curriculos individualizado para cada vaga de cada recrutador
		 */
		
//		jnAsyncMensageriaSender.send(VisTopics.sendResumesToThisPosition, positionsWithResumes);
		
		return CcpConstants.EMPTY_JSON;
	}

	public List<CcpJsonRepresentation> getPositionsWithResumes(List<CcpJsonRepresentation> positions,  CcpJsonRepresentation recruitersWithResumes) {
		
		List<CcpJsonRepresentation> positionsWithResumes = new ArrayList<>();
		Set<String> recruiters = new ArrayList<>(positions).stream().map(position -> position.getAsString("email")).collect(Collectors.toSet());
		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		
		for (String recruiter : recruiters) {
			Set<String> allResumesReachedByThisRecruiter = recruitersWithResumes.getAsObject(recruiter);
			
			List<CcpJsonRepresentation> allSearchParameters = allResumesReachedByThisRecruiter.stream()
					.map(email -> CcpConstants.EMPTY_JSON
							.put("domain", recruiter.toString().split("@")[1])
							.put("recruiter", recruiter)
							.put("email", email)
							)
					.collect(Collectors.toList());

			VisEntityResume visEntityResume = new VisEntityResume();
			VisEntityResumeView visEntityResumeView = new VisEntityResumeView();
			VisEntityResumeNegativeted visEntityResumeNegativeted = new VisEntityResumeNegativeted();
			VisEntityDeniedViewToCompany visEntityDeniedViewToCompany = new VisEntityDeniedViewToCompany();
			
			CcpDaoUnionAll searchResults = dao.unionAll(
					allSearchParameters
					,visEntityDeniedViewToCompany
					,visEntityResumeNegativeted
					,visEntityResumeView
					,visEntityResume
					);
			
			List<CcpJsonRepresentation> ableResumesToThisRecruiter = new ArrayList<>();
			
			for (CcpJsonRepresentation searchParameters : allSearchParameters) {
				
				boolean inactiveResume = searchResults.isPresent(visEntityResume, searchParameters) == false;
				
				if(inactiveResume) {
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
				
				CcpJsonRepresentation resume = searchResults.get(visEntityResume, searchParameters);
				
				boolean thisResumeNeverHasSeenBefore = searchResults.isPresent(visEntityResumeView, searchParameters) == false;
				
				if(thisResumeNeverHasSeenBefore) {
					ableResumesToThisRecruiter.add(resume);
					continue;
				}
				
				CcpJsonRepresentation resumeView = searchResults.get(visEntityResumeView, searchParameters);
				Long resumeLastView = resumeView.getAsLongNumber("lastView");
				Long resumeLastUpdate = resume.getAsLongNumber("lastUpdate");
				boolean thisResumeDoesNotChangedSinceTheLastRecruiterView = resumeLastView > resumeLastUpdate;
				
				if(thisResumeDoesNotChangedSinceTheLastRecruiterView) {
					continue;
				}
				ableResumesToThisRecruiter.add(resume);
			}
			
			List<CcpJsonRepresentation> recruiterPositions = new ArrayList<>(positions).stream().filter(position -> position.getAsString("email").equals(recruiter)).collect(Collectors.toList());

			for (CcpJsonRepresentation recruiterPosition : recruiterPositions) {
			
				List<CcpJsonRepresentation> ableResumesToThisPosition = new ArrayList<>(ableResumesToThisRecruiter).stream()
						.filter(ableResume -> VisAsyncUtils.matches(recruiterPosition, ableResume))
					.collect(Collectors.toList());
				
				if(ableResumesToThisPosition.isEmpty()) {
					continue;
				}
				CcpJsonRepresentation positionWithResume = recruiterPosition.put("resumes", ableResumesToThisPosition);
				positionsWithResumes.add(positionWithResume);
			}
		}
		return positionsWithResumes;
	}

}
