package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.cache.tasks.ReadSkillsFromDataBase;
import com.jn.vis.commons.entities.VisEntityBalance;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityGroupPositionsByRecruiter;
import com.jn.vis.commons.entities.VisEntityHashGrouper;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;
import com.jn.vis.commons.entities.VisEntityResumeView;
import com.jn.vis.commons.entities.VisEntityScheduleSendingResumeFees;
import com.jn.vis.commons.utils.VisAsyncBusiness;
import com.jn.vis.commons.utils.VisCommonsUtils;

public class VisAsyncUtils {
	
	private static Set<String> nonProfessionalDomains = new HashSet<>();
	
	static {
		nonProfessionalDomains.add("globalweb.com.br");
		nonProfessionalDomains.add("localweb.com.br");
		nonProfessionalDomains.add("protonmail.com");
		nonProfessionalDomains.add("locaweb.com.br");
		nonProfessionalDomains.add("outlook.com.br");
		nonProfessionalDomains.add("yahoo.com.br");
		nonProfessionalDomains.add("terra.com.br");
		nonProfessionalDomains.add("outlook.com");
		nonProfessionalDomains.add("hotmail.com");
		nonProfessionalDomains.add("uol.com.br");
		nonProfessionalDomains.add("bol.com.br");
		nonProfessionalDomains.add("uolinc.com");
		nonProfessionalDomains.add("yahoo.com");
		nonProfessionalDomains.add("gmail.com");
		nonProfessionalDomains.add("ig.com.br");
		nonProfessionalDomains.add("live.com");
		nonProfessionalDomains.add("msn.com");
	}

	
	//TODO BOTAR EM FILA SEPARANDO AS VAGAS EM LOTE DE RECRUTADORES NAO REPETIDOS

	public static List<CcpJsonRepresentation> sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(CcpJsonRepresentation schedullingPlan, Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getResumes, Function<String, CcpJsonRepresentation> getPositions) {
		
		String frequency = schedullingPlan.getAsString("frequency");
		
		CcpJsonRepresentation allPositionsGroupedByRecruiters = getPositions.apply(frequency);

		List<CcpJsonRepresentation> resumes = getResumes.apply(schedullingPlan);

		FrequencyOptions valueOf = FrequencyOptions.valueOf(frequency);

		List<CcpJsonRepresentation> allPositionsWithFilteredResumesAndTheirStatis = VisAsyncUtils.getAllPositionsWithFilteredAndSortedResumesAndTheirStatis(allPositionsGroupedByRecruiters, resumes, valueOf);

		List<CcpJsonRepresentation> allPositionsWithFilteredAndSortedResumesAndStatis = allPositionsWithFilteredResumesAndTheirStatis.stream().map(positionsWithFilteredResumes -> getStatisToThisPosition(positionsWithFilteredResumes)).collect(Collectors.toList());
		
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.positionResumesSend, allPositionsWithFilteredAndSortedResumesAndStatis);
		
		return allPositionsWithFilteredAndSortedResumesAndStatis;
	}

	private static CcpJsonRepresentation getStatisToThisPosition(CcpJsonRepresentation positionsWithFilteredResumes) {

		List<CcpJsonRepresentation> resumes = positionsWithFilteredResumes.getAsJsonList("resumes");
		List<String> fields = Arrays.asList("experience", "btc", "pj", "clt", "disponibility");
		
		for (String field : fields) {
			int total = 0;
			double sum = 0;
			for (CcpJsonRepresentation resume : resumes) {
				boolean fieldIsMissing = resume.containsAllKeys(field) == false;
				if(fieldIsMissing) {
					continue;
				}
				Double asDoubleNumber = resume.getAsDoubleNumber(field);
				sum += asDoubleNumber;
				total++;
			}	
			
			boolean hasAtLeastOneResume = total > 0;
			if(hasAtLeastOneResume) {
				double avg = sum / total;
				positionsWithFilteredResumes = positionsWithFilteredResumes.putSubKey("statis", field, avg);
			}
		}
		int resumesSize = resumes.size();
		positionsWithFilteredResumes = positionsWithFilteredResumes.putSubKey("statis", "resumes", resumesSize);
		return positionsWithFilteredResumes;
	}
	
	private static List<String> getHashes(CcpJsonRepresentation json) {
		// O resumWord se trata das habilidades se este JSON se tratar de currículo.
		// O mandatorySkills trata das habilidades se este JSON se tratar de vaga.
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");

		String enumsType = json.containsKey("experience") ? "resume" : "position";
		List<Integer> disponibilities = json.get(GetDisponibilityValuesFromJson.valueOf(enumsType));

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(enumsType, json);

		String seniority = json.get(GetSeniorityValueFromJson.valueOf(enumsType));

		List<Boolean> pcds = json.get(GetPcdValuesFromJson.valueOf(enumsType));;

		List<String> hashes = new ArrayList<>();
		// Todas as futuras possibilidades são gravadas em uma Lista
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (CcpJsonRepresentation moneyValue : moneyValues) {
					for (String resumeWord : resumeWords) {
						CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON.put("disponibility", disponibility)
								.put("resumeWord", resumeWord).put("seniority", seniority).putAll(moneyValue)
								.put("pcd", pcd);
						String hashValue = VisEntityHashGrouper.INSTANCE.calculateId(hash);
						hashes.add(hashValue);
					}
				}
			}
		}
		return hashes;
	}
	
	private static List<CcpJsonRepresentation> getMoneyValues(String enumsType, CcpJsonRepresentation json){
		
		ArrayList<CcpJsonRepresentation> result = new ArrayList<>();
		
		GetMoneyValuesFromJson valueOf = GetMoneyValuesFromJson.valueOf(enumsType);
		
		List<CcpJsonRepresentation> btcValues = valueOf.apply(json, "btc");
		List<CcpJsonRepresentation> cltValues = valueOf.apply(json, "clt");
		List<CcpJsonRepresentation> pjValues = valueOf.apply(json, "pj");

		result.addAll(btcValues);
		result.addAll(cltValues);
		result.addAll(pjValues);
		
		return result;
	}

	public static List<CcpJsonRepresentation> getLastUpdated(JnBaseEntity entity, FrequencyOptions valueOf) {
		
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		
		CcpDbQueryOptions queryToSearchLastUpdated = 
				CcpDbQueryOptions.INSTANCE
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

	public static CcpJsonRepresentation getAllPositionsGroupedByRecruiters(FrequencyOptions frequency) {
		// Injetando dependência do executor de query complexa
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		// Linha abaixo se refere a construção de uma query para filtrar vagas pela frequência
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				CcpDbQueryOptions.INSTANCE
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, frequency.name())
					.endSimplifiedQueryAndBackToRequest()
				;
		// Escolhendo as tabelas para fazer a busca (from)
		String[] resourcesNames = new String[] {VisEntityPosition.INSTANCE.getEntityName()};
		CcpJsonRepresentation positionsGroupedByRecruiters = queryExecutor.getMap(queryToSearchLastUpdatedResumes, resourcesNames, "email");
		return positionsGroupedByRecruiters;
	}

	private static List<CcpJsonRepresentation> getAllPositionsWithFilteredAndSortedResumesAndTheirStatis(
			CcpJsonRepresentation allPositionsGroupedByRecruiters, 
			List<CcpJsonRepresentation> resumes, 
			FrequencyOptions frequency) {
		
		List<CcpJsonRepresentation> allSearchParameters = getAllSearchParameters(allPositionsGroupedByRecruiters, resumes,	frequency);

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll searchResults = crud.unionAll(
				allSearchParameters
				,VisEntityResume.INSTANCE
				,VisEntityBalance.INSTANCE
				,VisEntityResumeView.INSTANCE
				,VisEntityResumeOpinion.INSTANCE
				,VisEntityDeniedViewToCompany.INSTANCE
				,VisEntityScheduleSendingResumeFees.INSTANCE
				,VisEntityResumeOpinion.INSTANCE.getMirrorEntity()
				);
		
		CcpJsonRepresentation allPositionsWithFilteredResumes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation searchParameters : allSearchParameters) {

			boolean feeNotFound = VisEntityScheduleSendingResumeFees.INSTANCE.isPresentInThisUnionAll(searchResults, searchParameters) == false;

			if(feeNotFound) {
				throw new RuntimeException("It is missing the " + VisEntityScheduleSendingResumeFees.class.getSimpleName() + " of frequency " + frequency);
			}
			
			boolean balanceNotFound = VisEntityBalance.INSTANCE.isPresentInThisUnionAll(searchResults, searchParameters) == false;

			if(balanceNotFound) {
				continue;
			}

			CcpJsonRepresentation fee = VisEntityScheduleSendingResumeFees.INSTANCE.getRequiredEntityRow(searchResults, searchParameters);
			Double feeValue = fee.getAsDoubleNumber("fee");
			
			CcpJsonRepresentation balance = VisEntityBalance.INSTANCE.getRequiredEntityRow(searchResults, searchParameters);
			Double balanceValue = balance.getAsDoubleNumber("balance");
			
			String recruiter = searchParameters.getAsString("recruiter");
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter = allPositionsGroupedByRecruiters.getAsJsonList(recruiter);
			int countPositionsGroupedByThisRecruiter = positionsGroupedByThisRecruiter.size();
			Double totalCostToThisRecruiter = feeValue * countPositionsGroupedByThisRecruiter;
			
			boolean insuficientFunds = balanceValue <= totalCostToThisRecruiter;
			
			if(insuficientFunds) {
				continue;
			}
			//TODO NOTIFICAR CANDIDATO DAQUILO QUE ELE PERDEU POR SEU CURRICULO ESTAR INATIVO
			boolean inactiveResume = VisEntityResume.INSTANCE.isPresentInThisUnionAll(searchResults, searchParameters) == false;
			
			if(inactiveResume) {
				continue;
			}

			boolean negativetedResume = VisEntityResumeOpinion.INSTANCE.getMirrorEntity().isPresentInThisUnionAll(searchResults, searchParameters);
			
			if(negativetedResume) {
				continue;
			}

			boolean deniedResume = VisEntityDeniedViewToCompany.INSTANCE.isPresentInThisUnionAll(searchResults, searchParameters);
			
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
			CcpSelectUnionAll searchResults
			) {
	
		CcpJsonRepresentation positionWithFilteredResumes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation positionByThisRecruiter : positionsGroupedByThisRecruiter) {

			CcpJsonRepresentation resume = VisEntityResume.INSTANCE.getRequiredEntityRow(searchResults, searchParameters);
			
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
			
			String positionId = VisEntityPosition.INSTANCE.calculateId(positionByThisRecruiter);
			
			CcpJsonRepresentation emailMessageValuesToSent = allPositionsWithFilteredResumes.getInnerJson(positionId);

			CcpJsonRepresentation resumeView = VisEntityResumeView.INSTANCE.getRecordFromUnionAll(searchResults, searchParameters);

			CcpJsonRepresentation resumeComment = VisEntityResumeOpinion.INSTANCE.getRecordFromUnionAll(searchResults, searchParameters);
	
			CcpJsonRepresentation resumeWithCommentAndVisualizationDetails = resume
					.put("resumeComment", resumeComment).put("resumeView", resumeView);

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
	
	
	private static String getDomain(String recruiter) {
	
		String domain = new CcpStringDecorator(recruiter).email().getDomain();
		
		boolean isProfessional = nonProfessionalDomains.contains(domain);
		
		if(isProfessional) {
			return domain;
		}
		return "";
	}
	
	private static List<CcpJsonRepresentation> getAllSearchParameters(
			CcpJsonRepresentation allPositionsGroupedByRecruiters, List<CcpJsonRepresentation> resumes, FrequencyOptions frequency) {
		List<CcpJsonRepresentation> allSearchParameters = new ArrayList<>();
		
		Set<String> recruiters = allPositionsGroupedByRecruiters.keySet();
		for (String recruiter : recruiters) {
			String recruiterDomain = getDomain(recruiter);
			for (CcpJsonRepresentation resume : resumes) {

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
	
	
	public static void saveResume(CcpJsonRepresentation resume, String propertyName, String fileName) {
		String email = resume.getAsString("email");
		String propertyValue = resume.getAsString(propertyName);
		CcpCacheDecorator resumeCache = VisCommonsUtils.getResumeCache(email, fileName);
		resumeCache.put(propertyValue, 86400);
		String bucketFolderResume = VisCommonsUtils.getBucketFolderResume(resume);
		String tenant = VisCommonsUtils.getTenant();
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		bucket.save(tenant, bucketFolderResume, fileName, propertyValue);
	}
	
	public static CcpJsonRepresentation getResumeWithSkills(CcpJsonRepresentation resume) {
		CcpCacheDecorator cache = new CcpCacheDecorator("skills");
		List<CcpJsonRepresentation> resultAsList = cache.get(ReadSkillsFromDataBase.INSTANCE, 86400);
		
		String resumeText = resume.getAsString("resumeText");
		List<CcpJsonRepresentation> skills = new ArrayList<>();
		
		for (CcpJsonRepresentation skill : resultAsList) {
		
			String skillName = skill.getAsString("skill");
			
			boolean skillNotFoundInResume = resumeText.toUpperCase().contains(skillName.toUpperCase()) == false;
			
			if(skillNotFoundInResume) {
				continue;
			}
			
			List<CcpJsonRepresentation> prerequistes = skill.getAsStringList("prerequiste").stream().map(x -> CcpConstants.EMPTY_JSON.put("name", x).put("type", "prerequiste")).collect(Collectors.toList());
			List<CcpJsonRepresentation> synonyms = skill.getAsStringList("synonym").stream().map(x -> CcpConstants.EMPTY_JSON.put("name", x).put("type", "synonym")).collect(Collectors.toList());
			skills.addAll(prerequistes);
			skills.addAll(synonyms);
			CcpJsonRepresentation mainSkill = CcpConstants.EMPTY_JSON.put("name", skillName).put("type", "main");
			skills.add(mainSkill);
		}
		
		CcpJsonRepresentation resumeWithSkills = resume.put("skills", skills);
		return resumeWithSkills;
	}
	
	@SuppressWarnings("unchecked")
	public static void changeStatus(CcpJsonRepresentation json, CcpEntity activeEntity,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> actionPosActivate,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> actionPosInactivate
			) {
		CcpEntity inactiveResumeEntity = activeEntity.getMirrorEntity();
		TransferRecordToReverseEntity tryToChangeStatusToActive = new TransferRecordToReverseEntity(inactiveResumeEntity, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING);
		TransferRecordToReverseEntity tryToChangeStatusToInactive = new TransferRecordToReverseEntity(activeEntity, actionPosInactivate, actionPosActivate);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, tryToChangeStatusToActive
				, tryToChangeStatusToInactive
				);
	}


	
	public static CcpJsonRepresentation groupPositionsByRecruiters(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation groupDetailsByMasters = groupDetailsByMasters(json, VisEntityPosition.INSTANCE, VisEntityGroupPositionsByRecruiter.INSTANCE, VisEntityPosition.Fields.email, VisEntityGroupPositionsByRecruiter.Fields.position, VisEntityPosition.Fields.timestamp);
		
		return groupDetailsByMasters;
	}
	
	public static CcpJsonRepresentation groupDetailsByMasters(
			CcpJsonRepresentation json, 
			CcpEntity entity, 
			CcpEntity groupEntity, 
			CcpEntityField masterField, 
			CcpEntityField detailsField, 
			CcpEntityField ascField) {
		
		List<String> masters = json.getAsStringList("masters");
		
		CcpDbQueryOptions query = CcpDbQueryOptions.INSTANCE
				.startQuery()
					.startBool()
						.startMust()
							.terms(masterField, masters)
						.endMustAndBackToBool()
					.endBoolAndBackToQuery()
				.endQueryAndBackToRequest()
				.addAscSorting(ascField.name())
		;
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpEntity mirrorEntity = entity.getMirrorEntity();
		String mirrorName = mirrorEntity.getEntityName();
		String entityName = entity.getEntityName();
		String[] resourcesNames = new String[]{entityName, mirrorName};
		
		GroupDetailsByMasters detailsGroupedByMasters = new GroupDetailsByMasters(detailsField.name(), masterField.name(), entity, groupEntity);
		
		queryExecutor.consumeQueryResult(query, resourcesNames, entityName, 10000, detailsGroupedByMasters, resourcesNames);
		detailsGroupedByMasters.saveAllDetailsGroupedByMasters();
		return json;
	}

}
