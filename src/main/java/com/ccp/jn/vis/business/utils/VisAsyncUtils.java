package com.ccp.jn.vis.business.utils;

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
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.jn.async.commons.utils.JnAsyncMensageriaSender;
import com.ccp.jn.vis.business.utils.hash.GetHashFromJson;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.entities.VisEntityBalance;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityHashGrouper;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;
import com.jn.vis.commons.entities.VisEntityResumeView;
import com.jn.vis.commons.entities.VisEntityScheduleSendingResumeFees;
import com.jn.vis.commons.utils.VisTopics;

public class VisAsyncUtils {

	public static void sendFilteredResumesByEachPositionToEachRecruiter(CcpJsonRepresentation schedullingPlan, Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getResumes) {
		String frequency = schedullingPlan.getAsString("frequency");
		PositionSendFrequency valueOf = PositionSendFrequency.valueOf(frequency);
		
		CcpJsonRepresentation positionsGroupedByRecruiters = VisAsyncUtils.getPositionsGroupedByRecruiters(valueOf);

		List<CcpJsonRepresentation> resumes = getResumes.apply(schedullingPlan);

		List<CcpJsonRepresentation> positionsWithFilteredResumes = VisAsyncUtils.getPositionsWithFilteredResumes(positionsGroupedByRecruiters, resumes, valueOf);

		JnAsyncMensageriaSender.INSTANCE.send(VisTopics.sendResumesToThisPosition, positionsWithFilteredResumes);
	}

	
	public static List<String> getHashes(CcpJsonRepresentation json) {
		// Os ddds entram como string e são convertidos para integer. Vagas e currículos podem anotar mais que um ddd por vez, por isso vem como List.
		List<Integer> ddds = json.getAsStringList("ddd").stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());
		// O resumWord se trata das habilidades se este JSON se tratar de currículo.
		// O mandatorySkills trata das habilidades se este JSON se tratar de vaga.
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");
		
		GetHashFromJson hashFromJson = GetHashFromJson.getHashFromJson(json);
		
		List<Integer> disponibilities = json.get(hashFromJson.getDisponibilityValuesFromJson);

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(hashFromJson, json);
		
		List<String> seniorities = json.get(hashFromJson.getSenioritiesValuesFromJson);

		List<Boolean> pcds = json.get(hashFromJson.getPcdValuesFromJson);
		
		List<String> hashes = new ArrayList<>();
		// Todas as futuras possibilidades são gravadas em uma Lista
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (String seniority : seniorities) {// vaga = [PL, SR] || candidato = 2 anos [JR]
					for (CcpJsonRepresentation moneyValue : moneyValues) {
						for (String resumeWord : resumeWords) {
							for (Integer ddd : ddds) {
								CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON
										.put("disponibility", disponibility)
										.put("resumeWord", resumeWord)
										.put("seniority", seniority)
										.putAll(moneyValue)
										.put("pcd", pcd)
										.put("ddd", ddd)
										;
								String hashValue = VisEntityHashGrouper.INSTANCE.getId(hash);
								hashes.add(hashValue);
							}
						}
					}
				}
			}
		}		

		return hashes;
	}
	
	private static List<CcpJsonRepresentation> getMoneyValues(GetHashFromJson hashFromJson, CcpJsonRepresentation json){
		
		ArrayList<CcpJsonRepresentation> result = new ArrayList<>();
		
		List<CcpJsonRepresentation> btcValues = hashFromJson.getBtcValuesFromJson.apply(json);
		List<CcpJsonRepresentation> cltValues = hashFromJson.getCltValuesFromJson.apply(json);
		List<CcpJsonRepresentation> pjValues = hashFromJson.getPjValuesFromJson.apply(json);

		result.addAll(btcValues);
		result.addAll(cltValues);
		result.addAll(pjValues);
		
		return result;
	}

	public static List<CcpJsonRepresentation> getLastUpdated(JnBaseEntity entity, PositionSendFrequency valueOf) {
		
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdated = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.startRange()
							.startFieldRange("lastUpdate")
								.greaterThan(System.currentTimeMillis() - valueOf.hours * 3_600_000)
							.endFieldRangeAndBackToRange()
						.endRangeAndBackToSimplifiedQuery()
					.endSimplifiedQueryAndBackToRequest()
				;
		String[] resourcesNames = new String[] {entity.getEntityName()};

		List<CcpJsonRepresentation> result = queryExecutor.getResultAsList(queryToSearchLastUpdated, resourcesNames);
		return result;
	}

	public static CcpJsonRepresentation getPositionsGroupedByRecruiters(PositionSendFrequency frequency) {
		// Injetando dependência do executor de query complexa
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		// Linha abaixo se refere a construção de uma query para filtrar vagas pela frequência
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, frequency)
					.endSimplifiedQueryAndBackToRequest()
				;
		// Escolhendo as tabelas para fazer a busca (from)
		String[] resourcesNames = new String[] {VisEntityPosition.INSTANCE.getEntityName()};

		CcpJsonRepresentation positionsGroupedByRecruiters = queryExecutor.getMap(queryToSearchLastUpdatedResumes, resourcesNames, "email");
		return positionsGroupedByRecruiters;
	}

	public static List<CcpJsonRepresentation> getPositionsWithFilteredResumes(CcpJsonRepresentation positionsGroupedByRecruiters, 
			List<CcpJsonRepresentation> resumes, PositionSendFrequency frequency) {
		
		List<CcpJsonRepresentation> allSearchParameters = getAllSearchParameters(positionsGroupedByRecruiters, resumes,	frequency);

		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		CcpDaoUnionAll searchResults = dao.unionAll(
				allSearchParameters
				,VisEntityResume.INSTANCE
				,VisEntityBalance.INSTANCE
				,VisEntityResumeView.INSTANCE
				,VisEntityResumeNegativeted.INSTANCE
				,VisEntityDeniedViewToCompany.INSTANCE
				,VisEntityScheduleSendingResumeFees.INSTANCE
				);
		
		List<CcpJsonRepresentation> positionsWithFilteredResumes = new ArrayList<>();

		for (CcpJsonRepresentation searchParameters : allSearchParameters) {

			boolean inexistingFee = searchResults.isPresent(VisEntityScheduleSendingResumeFees.INSTANCE, searchParameters) == false;

			if(inexistingFee) {
				throw new RuntimeException("It is missing the " + VisEntityScheduleSendingResumeFees.class.getSimpleName() + " of frequency " + frequency);
			}
			
			boolean inexistingBalance = searchResults.isPresent(VisEntityBalance.INSTANCE, searchParameters) == false;

			if(inexistingBalance) {
				continue;
			}

			CcpJsonRepresentation fee = searchResults.get(VisEntityScheduleSendingResumeFees.INSTANCE);
			Double feeValue = fee.getAsDoubleNumber("fee");
			
			CcpJsonRepresentation balance = searchResults.get(VisEntityBalance.INSTANCE);
			Double balanceValue = balance.getAsDoubleNumber("balance");
			
			String recruiter = searchParameters.getAsString("recruiter");
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter = positionsGroupedByRecruiters.getAsJsonList(recruiter);
			int countPositionsGroupedByThisRecruiter = positionsGroupedByThisRecruiter.size();
			Double totalCostToThisRecruiter = feeValue * countPositionsGroupedByThisRecruiter;
			
			boolean insuficientFunds = balanceValue <= totalCostToThisRecruiter;
			
			if(insuficientFunds) {
				continue;
			}

			boolean inactiveResume = searchResults.isPresent(VisEntityResume.INSTANCE, searchParameters) == false;
			
			if(inactiveResume) {
				continue;
			}

			boolean negativetedResume = searchResults.isPresent(VisEntityResumeNegativeted.INSTANCE, searchParameters);
			
			if(negativetedResume) {
				continue;
			}

			boolean deniedResume = searchResults.isPresent(VisEntityDeniedViewToCompany.INSTANCE, searchParameters);
			
			if(deniedResume) {
				continue;
			}
			
			CcpJsonRepresentation resume = searchResults.get(VisEntityResume.INSTANCE, searchParameters);
			
			boolean thisResumeNeverHasSeenBefore = searchResults.isPresent(VisEntityResumeView.INSTANCE, searchParameters) == false;
			
			if(thisResumeNeverHasSeenBefore) {
				CcpJsonRepresentation positionWithFilteredResumes = getPositionWithFilteredResumes(
						positionsGroupedByRecruiters, positionsGroupedByThisRecruiter, resume);
				
				positionsWithFilteredResumes.add(positionWithFilteredResumes);
				continue;
			}
			
			CcpJsonRepresentation resumeView = searchResults.get(VisEntityResumeView.INSTANCE, searchParameters);
			Long resumeLastView = resumeView.getAsLongNumber("lastView");
			Long resumeLastUpdate = resume.getAsLongNumber("lastUpdate");
			boolean thisResumeDoesNotChangedSinceTheLastRecruiterView = resumeLastView > resumeLastUpdate;
			
			if(thisResumeDoesNotChangedSinceTheLastRecruiterView) {
				continue;
			}
			
			CcpJsonRepresentation positionWithFilteredResumes = getPositionWithFilteredResumes(
					positionsGroupedByRecruiters, positionsGroupedByThisRecruiter, resume);
			
			positionsWithFilteredResumes.add(positionWithFilteredResumes);
		}
		
		return positionsWithFilteredResumes;
	}

	private static CcpJsonRepresentation getPositionWithFilteredResumes(
			CcpJsonRepresentation positionsGroupedByRecruiters,
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter, CcpJsonRepresentation resume) {
		CcpJsonRepresentation positionWithFilteredResumes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation positionByThisRecruiter : positionsGroupedByThisRecruiter) {
			List<String> positionHashes = getHashes(positionByThisRecruiter);
			List<String> resumeHashes = getHashes(resume);
			
			boolean resumeDoesNotMatch = resumeHashes.containsAll(positionHashes) == false;
		
			if(resumeDoesNotMatch) {
				continue;
			}
			
			positionWithFilteredResumes = positionWithFilteredResumes
			.put("position", positionsGroupedByRecruiters)
			.addToList("resumes", resume)
			;
		}
		return positionWithFilteredResumes;
	}

	private static List<CcpJsonRepresentation> getAllSearchParameters(
			CcpJsonRepresentation positionsGroupedByRecruiters, List<CcpJsonRepresentation> resumes, PositionSendFrequency frequency) {
		List<CcpJsonRepresentation> allSearchParameters = new ArrayList<>();
		
		Set<String> recruiters = positionsGroupedByRecruiters.keySet();
		for (String recruiter : recruiters) {
			for (CcpJsonRepresentation resume : resumes) {
				String professionalDomain = new CcpStringDecorator(recruiter).email().getProfessionalDomain();
				String email = resume.getAsString("email");
				
				CcpJsonRepresentation searchParameters = CcpConstants.EMPTY_JSON
						.put("domain", professionalDomain)
						.put("recruiter", recruiter)
						.put("frequency", frequency)
						.put("owner", recruiter)
						.put("email", email)
						;
				allSearchParameters.add(searchParameters);
			}
		}
		return allSearchParameters;
	}
	public static List<CcpJsonRepresentation> getPositionsBySchedullingFrequency(PositionSendFrequency frequency) {
		// Injetando dependência do executor de query complexa
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		// Linha abaixo se refere a construção de uma query para filtrar vagas pela frequência
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, frequency)
					.endSimplifiedQueryAndBackToRequest()
				;
		// Escolhendo as tabelas para fazer a busca (from)
		String[] resourcesNames = new String[] {VisEntityPosition.INSTANCE.getEntityName()};
		// Trazendo a lista de resultados para a memória
		List<CcpJsonRepresentation> positions = queryExecutor.getResultAsList(queryToSearchLastUpdatedResumes, resourcesNames);
		
		return positions;
	}

	public static void disableEntity(CcpJsonRepresentation id) {

	}

	public static boolean matches(CcpJsonRepresentation position, CcpJsonRepresentation resume) {
		
		CcpJsonRepresentation positionHash = position.getInnerJson("hash");
		
		CcpJsonRepresentation resumeHash = resume.getInnerJson("hash");//{insert: ['a', 'b', 'c'], remove: ['d'] }
		List<String> resumeInsert = resumeHash.getAsStringList("insert");
		//a,b,c,d,e
		//b,c,d
		boolean matches = positionHash.itIsTrueThatTheFollowingFields("insert")
				.ifTheyAreAllArrayValuesThenEachOne().isTextAndItIsContainedAtTheList(resumeInsert);
		
		return matches;
	}

}
