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
		// Na linha abaixo, estamos selecionando todas as vagas cujo recrutador as cadastrou para receber currículos instantaneamente
		List<CcpJsonRepresentation> instantlyPositions = VisAsyncUtils.getPositionsBySchedullingFrequency(PositionSendFrequency.minute);
		// Email do candidato 
		String email = resume.getAsString("email");
		// Recrutadores que cadastraram vagas que precisam receber currículos instantaneamente (assim que o candidato altera ou insere currículo)
		Set<String> recruiters = new ArrayList<>(instantlyPositions).stream().map(position -> position.getAsString("email")).collect(Collectors.toSet());
		// Injetando dependência do CRUD
		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		// Criando o WHERE para select union
		List<CcpJsonRepresentation> allSearchParameters = recruiters.stream().map(recruiter -> CcpConstants.EMPTY_JSON
				.put("domain", new CcpStringDecorator(recruiter).email().getProfessionalDomain())
				.put("recruiter", recruiter)
				.put("email", email)
				).collect(Collectors.toList());
		VisEntityDeniedViewToCompany visEntityDeniedViewToCompany = new VisEntityDeniedViewToCompany();
		VisEntityResumeNegativeted visEntityResumeNegativeted = new VisEntityResumeNegativeted();
		VisEntityPosition visEntityPosition = new VisEntityPosition();
		VisEntityResume visEntityResume = new VisEntityResume();
		// Buscando tudo o que é necessário de um vez só das tabelas abaixo.
		CcpDaoUnionAll searchResults = dao.unionAll(
				allSearchParameters
				,visEntityDeniedViewToCompany
				,visEntityResumeNegativeted
				,visEntityPosition
				,visEntityResume
				);
		List<CcpJsonRepresentation> ablePositionsToThisResume = new ArrayList<>();

		for (CcpJsonRepresentation searchParameters : allSearchParameters) {
			
			boolean inactivePosition = searchResults.isPresent(visEntityPosition, searchParameters) == false;
			
			if(inactivePosition) {
				// Ignorando a vaga por ter sido inativada (excluída) recentemente
				continue;
			}

			boolean negativetedResume = searchResults.isPresent(visEntityResumeNegativeted, searchParameters);
			
			if(negativetedResume) {
				// Ignorando a vaga pois este currículo não é aceito por esse recrutador
				continue;
			}

			boolean deniedResume = searchResults.isPresent(visEntityDeniedViewToCompany, searchParameters);
			
			if(deniedResume) {
				// Ignorando a vaga pois o candidato não quer que o currículo vá para essa consultoria (domain)
				continue;
			}

			CcpJsonRepresentation position = searchResults.get(visEntityPosition, searchParameters);

			boolean doesNotMatch = VisAsyncUtils.matches(position, resume) == false;
			
			if(doesNotMatch) {
				// Ignorando a vaga pois os requisitos (hashes) desta não são compatíveis com os requisitos do currículo
				continue;
			}
			// Se chegou até aqui, logo a vaga preenche todos os requisitos do currículo e vice versa.
			ablePositionsToThisResume.add(position);
		}
		sendThisResumeToAblePositions(ablePositionsToThisResume);
	}

	private void sendThisResumeToAblePositions(List<CcpJsonRepresentation> ablePositionsToThisResume) {
		// TODO Vis.1.3		
	}

}
