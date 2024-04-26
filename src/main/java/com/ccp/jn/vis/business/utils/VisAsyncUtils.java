package com.ccp.jn.vis.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.dao.CcpDaoUnionAll;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.jn.async.commons.utils.JnAsyncMensageriaSender;
import com.ccp.jn.vis.business.resune.sort.PositionResumesSort;
import com.ccp.jn.vis.business.utils.hash.GetHashFromJson;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.entities.VisEntityBalance;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityHashGrouper;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeComment;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;
import com.jn.vis.commons.entities.VisEntityResumeView;
import com.jn.vis.commons.entities.VisEntityScheduleSendingResumeFees;
import com.jn.vis.commons.utils.VisTopics;

public class VisAsyncUtils {

	public static void sendFilteredResumesByEachPositionToEachRecruiter(CcpJsonRepresentation schedullingPlan, Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getResumes) {
		
		String frequency = schedullingPlan.getAsString("frequency");
		
		PositionSendFrequency valueOf = PositionSendFrequency.valueOf(frequency);
		
		CcpJsonRepresentation allPositionsGroupedByRecruiters = VisAsyncUtils.getAllPositionsGroupedByRecruiters(valueOf);

		List<CcpJsonRepresentation> resumes = getResumes.apply(schedullingPlan);

		List<CcpJsonRepresentation> allPositionsWithFilteredResumes = VisAsyncUtils.getAllPositionsWithFilteredResumes(allPositionsGroupedByRecruiters, resumes, valueOf);

		JnAsyncMensageriaSender.INSTANCE.send(VisTopics.sendResumesToThisPositions, allPositionsWithFilteredResumes);
	}

	private static List<String> getHashes(CcpJsonRepresentation json) {
		// O resumWord se trata das habilidades se este JSON se tratar de currículo.
		// O mandatorySkills trata das habilidades se este JSON se tratar de vaga.
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");

		GetHashFromJson hashFromJson = GetHashFromJson.getHashFromJson(json);

		List<Integer> disponibilities = json.get(hashFromJson.getDisponibilityValuesFromJson);

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(hashFromJson, json);

		String seniority = hashFromJson.getSeniorityValueFromJson.apply(json);

		List<Boolean> pcds = json.get(hashFromJson.getPcdValuesFromJson);

		List<String> hashes = new ArrayList<>();
		// Todas as futuras possibilidades são gravadas em uma Lista
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (CcpJsonRepresentation moneyValue : moneyValues) {
					for (String resumeWord : resumeWords) {
						CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON.put("disponibility", disponibility)
								.put("resumeWord", resumeWord).put("seniority", seniority).putAll(moneyValue)
								.put("pcd", pcd);
						String hashValue = VisEntityHashGrouper.INSTANCE.getId(hash);
						hashes.add(hashValue);
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

	private static CcpJsonRepresentation getAllPositionsGroupedByRecruiters(PositionSendFrequency frequency) {
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

	private static List<CcpJsonRepresentation> getAllPositionsWithFilteredResumes(CcpJsonRepresentation allPositionsGroupedByRecruiters, 
			List<CcpJsonRepresentation> resumes, PositionSendFrequency frequency) {
		
		List<CcpJsonRepresentation> allSearchParameters = getAllSearchParameters(allPositionsGroupedByRecruiters, resumes,	frequency);

		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
		
		CcpDaoUnionAll searchResults = dao.unionAll(
				allSearchParameters
				,VisEntityResume.INSTANCE
				,VisEntityBalance.INSTANCE
				,VisEntityResumeView.INSTANCE
				,VisEntityResumeComment.INSTANCE
				,VisEntityResumeNegativeted.INSTANCE
				,VisEntityDeniedViewToCompany.INSTANCE
				,VisEntityScheduleSendingResumeFees.INSTANCE
				);
		
		CcpJsonRepresentation allPositionsWithFilteredResumes = CcpConstants.EMPTY_JSON;
		/*
		 * {
		 *  "dfbgtlsamd": {
		 *  
		 *  	"position": {},
		 *  	"resumes": [{}, {}, {}]
		 *  },
		 * 
		 *  "sadedfdasdsw": {
		 *  
		 *  	"position": {},
		 *  	"resumes": [{}, {}, {}]
		 *  },
		 * }
		 */
		
		for (CcpJsonRepresentation searchParameters : allSearchParameters) {

			boolean feeNotFound = searchResults.isPresent(VisEntityScheduleSendingResumeFees.INSTANCE, searchParameters) == false;

			if(feeNotFound) {
				throw new RuntimeException("It is missing the " + VisEntityScheduleSendingResumeFees.class.getSimpleName() + " of frequency " + frequency);
			}
			
			boolean balanceNotFound = searchResults.isPresent(VisEntityBalance.INSTANCE, searchParameters) == false;

			if(balanceNotFound) {
				continue;
			}

			CcpJsonRepresentation fee = searchResults.get(VisEntityScheduleSendingResumeFees.INSTANCE);
			Double feeValue = fee.getAsDoubleNumber("fee");
			
			CcpJsonRepresentation balance = searchResults.get(VisEntityBalance.INSTANCE);
			Double balanceValue = balance.getAsDoubleNumber("balance");
			
			String recruiter = searchParameters.getAsString("recruiter");
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter = allPositionsGroupedByRecruiters.getAsJsonList(recruiter);
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
			
			allPositionsWithFilteredResumes = getPositionWithFilteredResumes(positionsGroupedByThisRecruiter, 
					allPositionsGroupedByRecruiters, allPositionsWithFilteredResumes, searchParameters, searchResults);
		}
		
	 	CcpJsonRepresentation allPositionsWithFilteredResumesCopy = CcpConstants.EMPTY_JSON.putAll(allPositionsWithFilteredResumes);
		
		List<CcpJsonRepresentation> positionsWithSortedResumes = allPositionsWithFilteredResumes.keySet().stream().map(positionId -> getPositionWithSortedResumes(positionId, allPositionsWithFilteredResumesCopy) ).collect(Collectors.toList());
		return positionsWithSortedResumes;
	}
	
	private static CcpJsonRepresentation getPositionWithFilteredResumes(
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter, 
			CcpJsonRepresentation allPositionsGroupedByRecruiters,
			CcpJsonRepresentation allPositionsWithFilteredResumes,
			CcpJsonRepresentation searchParameters,
			CcpDaoUnionAll searchResults
			) {
	
		CcpJsonRepresentation positionWithFilteredResumes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation positionByThisRecruiter : positionsGroupedByThisRecruiter) {

			CcpJsonRepresentation resume = searchResults.get(VisEntityResume.INSTANCE, searchParameters);
			
			CcpCollectionDecorator dddsPosition = positionByThisRecruiter.getAsCollectionDecorator("ddd");
			CcpCollectionDecorator dddsResume = resume.getAsCollectionDecorator("ddd");
			boolean differentDdds = dddsResume.hasIntersect(dddsPosition.content) == false;
			
			if(differentDdds) {
				continue;
			}
			
			List<String> positionHashes = getHashes(positionByThisRecruiter);
			List<String> resumeHashes = getHashes(resume);
			
			boolean resumeDoesNotMatch = resumeHashes.containsAll(positionHashes) == false;
		
			if(resumeDoesNotMatch) {
				continue;
			}
			
			String positionId = VisEntityPosition.INSTANCE.getId(positionByThisRecruiter);
			
			CcpJsonRepresentation emailMessageValuesToSent = allPositionsWithFilteredResumes.getInnerJson(positionId);

			CcpJsonRepresentation resumeView = searchResults.get(VisEntityResumeView.INSTANCE, searchParameters);

			CcpJsonRepresentation resumeComment = searchResults.get(VisEntityResumeComment.INSTANCE, searchParameters);
	
			CcpJsonRepresentation resumeWithCommentAndVisualizationDetails = resume.put("resumeComment", resumeComment).put("resumeView", resumeView);

			emailMessageValuesToSent = emailMessageValuesToSent
					.addToList("resumes", resumeWithCommentAndVisualizationDetails)
					.put("position", allPositionsGroupedByRecruiters)
					;
			
			allPositionsWithFilteredResumes = allPositionsWithFilteredResumes.put(positionId, emailMessageValuesToSent);
		}
		return positionWithFilteredResumes;
	}

	private static CcpJsonRepresentation getPositionWithSortedResumes(String positionId, CcpJsonRepresentation allPositionsWithFilteredResumes) {
		
		CcpJsonRepresentation positionWithResumes = allPositionsWithFilteredResumes.getInnerJson(positionId);
		
		List<CcpJsonRepresentation> resumes = positionWithResumes.getAsJsonList("resumes");
		
		boolean singleResume = resumes.size() <= 1;
		
		if(singleResume) {
			return positionWithResumes;
		}
		
		CcpJsonRepresentation position = positionWithResumes.getInnerJson("position");
		PositionResumesSort positionResumesSort = new PositionResumesSort(position);
		resumes.sort(positionResumesSort);
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.putAll(positionWithResumes).put("resumes", resumes);
		return put;
	}
	

	private static List<CcpJsonRepresentation> getAllSearchParameters(
			CcpJsonRepresentation allPositionsGroupedByRecruiters, List<CcpJsonRepresentation> resumes, PositionSendFrequency frequency) {
		List<CcpJsonRepresentation> allSearchParameters = new ArrayList<>();
		
		Set<String> recruiters = allPositionsGroupedByRecruiters.keySet();
		for (String recruiter : recruiters) {
			for (CcpJsonRepresentation resume : resumes) {
				String recruiterDomain = new CcpStringDecorator(recruiter).email().getProfessionalDomain();
				String email = resume.getAsString("email");
				
				CcpJsonRepresentation searchParameters = CcpConstants.EMPTY_JSON
						.put("domain", recruiterDomain)
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

	public static void disableEntity(CcpJsonRepresentation id) {

	}
}
