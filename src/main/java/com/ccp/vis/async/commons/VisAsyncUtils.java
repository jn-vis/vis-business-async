package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.ccp.vis.exceptions.RequiredSkillsMissingInResume;
import com.jn.vis.commons.status.ViewResumeStatus;
import com.jn.vis.commons.utils.VisAsyncBusiness;
import com.jn.vis.commons.utils.VisCommonsUtils;
import com.vis.commons.entities.VisEntityBalance;
import com.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.vis.commons.entities.VisEntityGroupPositionsByRecruiter;
import com.vis.commons.entities.VisEntityGroupResumesByPosition;
import com.vis.commons.entities.VisEntityPosition;
import com.vis.commons.entities.VisEntityResume;
import com.vis.commons.entities.VisEntityResumeLastView;
import com.vis.commons.entities.VisEntityResumePerception;
import com.vis.commons.entities.VisEntityScheduleSendingResumeFees;
import com.vis.commons.entities.VisEntityVirtualHashGrouper;

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
	//TODO UNION ALL COMEÇANDO PELOS AGRUPADORES POR CURRICULO E RECRUTADOR
	//TODO PAGINAÇÃO DE BUCKET

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
				boolean fieldIsMissing = resume.containsAllFields(field) == false;
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
				positionsWithFilteredResumes = positionsWithFilteredResumes.addToItem("statis", field, avg);
			}
		}
		int resumesSize = resumes.size();
		positionsWithFilteredResumes = positionsWithFilteredResumes.addToItem("statis", "resumes", resumesSize);
		return positionsWithFilteredResumes;
	}
	
	private static List<String> getHashes(CcpJsonRepresentation json) {

		String enumsType = json.containsField("experience") ? "resume" : "position";
		List<Integer> disponibilities = json.getTransformed(GetDisponibilityValuesFromJson.valueOf(enumsType));

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(enumsType, json);

		String seniority = json.getTransformed(GetSeniorityValueFromJson.valueOf(enumsType));

		List<Boolean> pcds = json.getTransformed(GetPcdValuesFromJson.valueOf(enumsType));;

		List<String> hashes = new ArrayList<>();
		// Todas as futuras possibilidades são gravadas em uma Lista
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (CcpJsonRepresentation moneyValue : moneyValues) {
						CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON.put("disponibility", disponibility)
								.put("seniority", seniority).putAll(moneyValue)
								.put("pcd", pcd);
						//TODO ELIMINAR NECESSIDADE DE CRIAR ESSA TABELA, ALEM DE ELIMINAR O VIRTUALENTITY
						String hashValue = VisEntityVirtualHashGrouper.ENTITY.calculateId(hash);
						hashes.add(hashValue);
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

	public static List<CcpJsonRepresentation> getLastUpdated(CcpEntity entity, FrequencyOptions valueOf, String filterFieldName) {
		
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		
		CcpDbQueryOptions queryToSearchLastUpdated = 
				CcpDbQueryOptions.INSTANCE
					.startSimplifiedQuery()
						.startRange()
							.startFieldRange(filterFieldName)
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

		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);

		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				CcpDbQueryOptions.INSTANCE
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, frequency.name())
					.endSimplifiedQueryAndBackToRequest()
				;
		String[] resourcesNames = new String[] {VisEntityPosition.ENTITY.getEntityName()};
		CcpJsonRepresentation positionsGroupedByRecruiters = queryExecutor.getMap(queryToSearchLastUpdatedResumes, resourcesNames, "email");
		return positionsGroupedByRecruiters;
	}

	private static List<CcpJsonRepresentation> getAllPositionsWithFilteredAndSortedResumesAndTheirStatis(
			CcpJsonRepresentation allPositionsGroupedByRecruiters, 
			List<CcpJsonRepresentation> resumes, 
			FrequencyOptions frequency) {
		
		List<CcpJsonRepresentation> allSearchParameters = getAllSearchParameters(allPositionsGroupedByRecruiters, resumes,	frequency);
		boolean positionsNotFound = allSearchParameters.isEmpty();
		
		if(positionsNotFound) {
			return new ArrayList<>();
		}
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll searchResults = crud.unionAll(
				allSearchParameters
				,VisEntityResume.ENTITY
				,VisEntityBalance.ENTITY
				,VisEntityResumePerception.ENTITY
				,VisEntityResumeLastView.ENTITY
				,VisEntityDeniedViewToCompany.ENTITY
				,VisEntityScheduleSendingResumeFees.ENTITY
				,VisEntityResumePerception.ENTITY.getTwinEntity()
				);
		
		CcpJsonRepresentation allPositionsWithFilteredResumes = CcpConstants.EMPTY_JSON;
		
		List<CcpBulkItem> errors = new ArrayList<>();
		
		for (CcpJsonRepresentation searchParameters : allSearchParameters) {

			boolean feeNotFound = VisEntityScheduleSendingResumeFees.ENTITY.isPresentInThisUnionAll(searchResults, searchParameters) == false;

			if(feeNotFound) {
				throw new RuntimeException("It is missing the " + VisEntityScheduleSendingResumeFees.class.getSimpleName() + " of frequency " + frequency);
			}
			
			boolean balanceNotFound = VisEntityBalance.ENTITY.isPresentInThisUnionAll(searchResults, searchParameters) == false;

			if(balanceNotFound) {
				CcpBulkItem error = ViewResumeStatus.missingBalance.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}

			CcpJsonRepresentation fee = VisEntityScheduleSendingResumeFees.ENTITY.getRequiredEntityRow(searchResults, searchParameters);
			
			CcpJsonRepresentation balance = VisEntityBalance.ENTITY.getRequiredEntityRow(searchResults, searchParameters);
			
			String recruiter = searchParameters.getAsString("recruiter");
			List<CcpJsonRepresentation> positionsGroupedByThisRecruiter = allPositionsGroupedByRecruiters.getAsJsonList(recruiter);
			int countPositionsGroupedByThisRecruiter = positionsGroupedByThisRecruiter.size();
			
			boolean insuficientFunds = VisCommonsUtils.isInsufficientFunds(countPositionsGroupedByThisRecruiter, fee, balance);
			
			if(insuficientFunds) {
				CcpBulkItem error = ViewResumeStatus.insufficientFunds.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}

			boolean inactiveResume = VisEntityResume.ENTITY.getTwinEntity().isPresentInThisUnionAll(searchResults, searchParameters);
			
			if(inactiveResume) {
				CcpBulkItem error = ViewResumeStatus.inactiveResume.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}
			
			
			
			boolean resumeNotFound = VisEntityResume.ENTITY.isPresentInThisUnionAll(searchResults, searchParameters) == false;
			
			if(resumeNotFound) {
				CcpBulkItem error = ViewResumeStatus.resumeNotFound.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}

			boolean negativetedResume = VisEntityResumePerception.ENTITY.getTwinEntity().isPresentInThisUnionAll(searchResults, searchParameters);
			
			if(negativetedResume) {
				CcpBulkItem error = ViewResumeStatus.negativatedResume.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}
				/*
				 * TI -> backend -> java -> spring -> springboot
				 */
				
			boolean deniedResume = VisEntityDeniedViewToCompany.ENTITY.isPresentInThisUnionAll(searchResults, searchParameters);
			
			if(deniedResume) {
				CcpBulkItem error = ViewResumeStatus.notAllowedRecruiter.toBulkItemCreate(searchParameters);	
				errors.add(error);
				continue;
			}
			
			allPositionsWithFilteredResumes = getPositionWithFilteredResumes(positionsGroupedByThisRecruiter, 
					allPositionsGroupedByRecruiters, allPositionsWithFilteredResumes, searchParameters, searchResults);
		}
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(errors);
		
	 	CcpJsonRepresentation allPositionsWithFilteredResumesCopy = CcpConstants.EMPTY_JSON.putAll(allPositionsWithFilteredResumes);
		
		List<CcpJsonRepresentation> positionsWithSortedResumes = allPositionsWithFilteredResumes.fieldSet().stream().map(positionId -> getPositionWithSortedResumes(positionId, allPositionsWithFilteredResumesCopy) ).collect(Collectors.toList());
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

			CcpJsonRepresentation resume = VisEntityResume.ENTITY.getRequiredEntityRow(searchResults, searchParameters);
			
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
			
			List<CcpJsonRepresentation> requiredSkills;
			
			try {
				requiredSkills = getRequiredSkillsInThisResume(positionByThisRecruiter, resume);
			} catch (RequiredSkillsMissingInResume e) {
				// TODO: salvar skills faltando no curriculo
				continue;
			}
			
			
			boolean resumeAlreadySeen = resumeAlreadySeen(positionByThisRecruiter, searchResults, searchParameters);
			
			if(resumeAlreadySeen) {
				continue;
			}
			
			String positionId = VisEntityPosition.ENTITY.calculateId(positionByThisRecruiter);
			
			CcpJsonRepresentation emailMessageValuesToSent = allPositionsWithFilteredResumes.getInnerJson(positionId);

			CcpJsonRepresentation resumeLastView = VisEntityResumeLastView.ENTITY.getRecordFromUnionAll(searchResults, searchParameters);

			CcpJsonRepresentation resumeOpinion = VisEntityResumePerception.ENTITY.getRecordFromUnionAll(searchResults, searchParameters);
	
			CcpJsonRepresentation resumeWithCommentAndVisualizationDetails = resume
					.put("resumeOpinion", resumeOpinion).put("resumeLastView", resumeLastView);

			emailMessageValuesToSent = emailMessageValuesToSent
					.addToList("resumes", resumeWithCommentAndVisualizationDetails)
					.put("position", allPositionsGroupedByRecruiters)
					.put("requiredSkills", requiredSkills)
					;
			
			allPositionsWithFilteredResumes = allPositionsWithFilteredResumes.put(positionId, emailMessageValuesToSent);
		}
		return positionWithFilteredResumes;
	}

	private static List<CcpJsonRepresentation> getRequiredSkillsInThisResume(
			CcpJsonRepresentation positionByThisRecruiter, 
			CcpJsonRepresentation resume) {

		List<String> requiredSkillsFromPosition = positionByThisRecruiter.getAsStringList(VisEntityPosition.Fields.requiredSkill.name());
		
		List<CcpJsonRepresentation> skillsFromResume = resume.getAsJsonList(VisEntityResume.Fields.skill.name());
		List<String> requiredSkillsMissingInResume = new ArrayList<String>();
		List<CcpJsonRepresentation> response = new ArrayList<>();
		for (String requiredSkillFromPosition : requiredSkillsFromPosition) {
			
			boolean skillDirectlyFoundInResume = skillsFromResume.stream().filter(s -> s.getAsString("skill").equals(requiredSkillFromPosition)).findFirst().isPresent();
			
			if(skillDirectlyFoundInResume) {
				CcpJsonRepresentation skill = CcpConstants.EMPTY_JSON
					.put("type", ResumeSkillFoundType.CONTAINED_IN_RESUME)
					.put("skill", requiredSkillFromPosition);
				response.add(skill);
				continue;
			}
			
			Optional<CcpJsonRepresentation> synonymFound = skillsFromResume.stream().filter(s -> s.getAsStringList("synonyms").contains(requiredSkillFromPosition)).findFirst();
			boolean skillFoundBySynonymInResume = synonymFound.isPresent();
			
			if(skillFoundBySynonymInResume) {
				CcpJsonRepresentation synonym = synonymFound.get();
				String synonymName = synonym.getAsString("skill");
				CcpJsonRepresentation skill = CcpConstants.EMPTY_JSON
						.put("type", ResumeSkillFoundType.SYNONYM)
						.put("skill", requiredSkillFromPosition)
						.put("synonym", synonymName)
						;
					response.add(skill);
					continue;
			}
			List<String> parents = skillsFromResume.stream().filter(s -> 
			s.getAsStringList("parent").contains(requiredSkillFromPosition))
			.map(s -> s.getAsString("skill"))
			.collect(Collectors.toList());
			
			boolean skillFoundByParentsInResume = parents.isEmpty() == false;
			
			if(skillFoundByParentsInResume) {
				CcpJsonRepresentation skill = CcpConstants.EMPTY_JSON
						.put("type", ResumeSkillFoundType.PARENT)
						.put("skill", requiredSkillFromPosition)
						.put("parents", parents)
						;
					response.add(skill);
				continue;
			}
			
			requiredSkillsMissingInResume.add(requiredSkillFromPosition);
		}
		
		
		boolean itIsMissingRequiredSkillInThisResume = requiredSkillsMissingInResume.isEmpty() == false;
		
		if(itIsMissingRequiredSkillInThisResume) {
			throw new RequiredSkillsMissingInResume(requiredSkillsMissingInResume);
		}
	
		return response;
	}

	private static boolean resumeAlreadySeen(CcpJsonRepresentation positionByThisRecruiter, CcpSelectUnionAll searchResults, CcpJsonRepresentation searchParameters) {
	
		boolean doNotFilterResumesAlreadySeen = positionByThisRecruiter.getAsBoolean("filterResumesAlreadySeen") == false;
		
		if(doNotFilterResumesAlreadySeen) {
			return false;
		}
		
		boolean thisResumeWasNeverSeenBefore = VisEntityResumeLastView.ENTITY.isPresentInThisUnionAll(searchResults, searchParameters) == false;
		
		if(thisResumeWasNeverSeenBefore) {
			return false;
		}
		
		CcpJsonRepresentation resumeLastView = VisEntityResumeLastView.ENTITY.getRequiredEntityRow(searchResults, searchParameters);
		
		CcpJsonRepresentation resume = VisEntityResume.ENTITY.getRequiredEntityRow(searchResults, resumeLastView);
		
		Long resumeLastSeen = resumeLastView.getAsLongNumber(VisEntityResumeLastView.Fields.timestamp.name());

		Long resumeLastUpdate = resume.getAsLongNumber(VisEntityResume.Fields.timestamp.name());
		
		return resumeLastUpdate <= resumeLastSeen;
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
		
		boolean positionsNotFound = allPositionsGroupedByRecruiters.isEmpty();

		if(positionsNotFound) {
			return new ArrayList<>();
		}
		
		List<CcpJsonRepresentation> allSearchParameters = new ArrayList<>();
		
		Set<String> recruiters = allPositionsGroupedByRecruiters.fieldSet();
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
	
	
	@SuppressWarnings("unchecked")
	public static void changeStatus(CcpJsonRepresentation json, CcpEntity activeEntity,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> actionPosActivate,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> actionPosInactivate
			) {
		CcpEntity inactiveResumeEntity = activeEntity.getTwinEntity();
		TransferRecordToReverseEntity tryToChangeStatusToActive = new TransferRecordToReverseEntity(inactiveResumeEntity, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING);
		TransferRecordToReverseEntity tryToChangeStatusToInactive = new TransferRecordToReverseEntity(activeEntity, actionPosInactivate, actionPosActivate, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, tryToChangeStatusToActive
				, tryToChangeStatusToInactive
				);
	}


	
	public static CcpJsonRepresentation groupPositionsGroupedByRecruiters(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation groupDetailsByMasters = groupDetailsByMasters(json, VisEntityPosition.ENTITY, 
				VisEntityGroupPositionsByRecruiter.ENTITY, VisEntityPosition.Fields.email, VisEntityPosition.Fields.timestamp);
		
		return groupDetailsByMasters;
	}
	
	public static CcpJsonRepresentation groupDetailsByMasters(
			CcpJsonRepresentation json, 
			CcpEntity entity, 
			CcpEntity groupEntity, 
			CcpEntityField masterField, 
			CcpEntityField ascField) {
		//1
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
		
		String[] entitiesToSelect = entity.getEntitiesToSelect();
		
		GroupDetailsByMasters detailsGroupedByMasters = new GroupDetailsByMasters(masterField.name(), entity, groupEntity);
		
		queryExecutor.consumeQueryResult(query, entitiesToSelect, "10s", 10000, detailsGroupedByMasters);
		
		detailsGroupedByMasters.saveAllDetailsGroupedByMasters();
		
		return json;
	}
	
	
	
	public static void saveRecordsInPages(
			List<CcpJsonRepresentation> records, 
			CcpJsonRepresentation primaryKeySupplier,
			CcpEntity entity) {

		List<CcpBulkItem> allPagesTogether = getRecordsInPages(records, primaryKeySupplier, entity);
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(allPagesTogether);
	}

	public static List<CcpBulkItem> getRecordsInPages(List<CcpJsonRepresentation> records,
			CcpJsonRepresentation primaryKeySupplier, CcpEntity entity) {
		List<CcpBulkItem> allPagesTogether = new ArrayList<>();
		int listSize = 10;
		int totalPages = records.size()  % listSize + 1;
		int index = 0;
		
		for(int from = 0; from < totalPages; from++) {
			List<CcpJsonRepresentation> page = new ArrayList<>();
			for(;(index + 1) % listSize !=0 && index < records.size(); index++) {
				CcpJsonRepresentation resume = records.get(index);
				CcpJsonRepresentation put = resume.put("index", index);
				page.add(put);
			}
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
					.put(VisEntityGroupResumesByPosition.Fields.detail.name(), page)
					.put(VisEntityGroupResumesByPosition.Fields.listSize.name(), listSize)
					.put(VisEntityGroupResumesByPosition.Fields.from.name(), from)
					.putAll(primaryKeySupplier)
					;
			CcpBulkItem bulkItem = entity.toBulkItem(put, CcpEntityOperationType.create);
			allPagesTogether.add(bulkItem);
		}
		return allPagesTogether;
	}


}
