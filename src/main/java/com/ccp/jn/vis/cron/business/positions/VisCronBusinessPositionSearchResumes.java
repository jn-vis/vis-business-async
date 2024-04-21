package com.ccp.jn.vis.cron.business.positions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.dao.CcpDaoUnionAll;
import com.ccp.exceptions.process.CcpAsyncProcess;
import com.ccp.jn.vis.business.utils.PositionSendFrequency;
import com.ccp.jn.vis.business.utils.VisAsyncUtils;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.vis.commons.entities.VisEntityBalance;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityFees;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityHashGrouper;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResumeNegativeted;
import com.jn.vis.commons.entities.VisEntityResumeView;
import com.jn.vis.commons.utils.VisTopics;

public class VisCronBusinessPositionSearchResumes  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {
		
		VisAsyncUtils.saveHashes(PositionSendFrequency.hourly, new VisEntityResume());

		VisAsyncUtils.saveHashes(PositionSendFrequency.hourly, new VisEntityPosition());

		
		List<CcpJsonRepresentation> positionsBySchedullingFrequency = VisAsyncUtils.getPositionsBySchedullingFrequency(schedullingPlan);

		List<CcpJsonRepresentation> positionsFilteredByRecruiterFunds = this.getPositionsFilteredByRecruiterFunds(schedullingPlan, positionsBySchedullingFrequency);
		
		CcpDaoUnionAll resumesHahes = this.getAllResumesHashes(positionsFilteredByRecruiterFunds);

		CcpJsonRepresentation recruitersWithResumes = this.getRecruitersWithResumes(positionsFilteredByRecruiterFunds, resumesHahes);
		
		List<CcpJsonRepresentation> positionsWithResumes = this.getPositionsWithResumes(positionsFilteredByRecruiterFunds, recruitersWithResumes);
		
		for (CcpJsonRepresentation positionWithResume : positionsWithResumes) {
			new CcpAsyncProcess().send(positionWithResume, VisTopics.sendResumesToThisPosition.name(), new JnEntityAsyncTask());
		}
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
					//TODO NOTIFICAÇÃO PARA RECRUTADOR
					continue;
				}
				CcpJsonRepresentation positionWithResume = recruiterPosition.put("resumes", ableResumesToThisPosition);
				positionsWithResumes.add(positionWithResume);
			}
		}
		return positionsWithResumes;
	}


	public CcpJsonRepresentation getRecruitersWithResumes(List<CcpJsonRepresentation> positions, CcpDaoUnionAll resumesHahes) {
		
		Set<String> recruiters = new ArrayList<>(positions).stream().map(position -> position.getAsString("email")).collect(Collectors.toSet());
		VisEntityHashGrouper entityResumeHash = new VisEntityHashGrouper();
		
		CcpJsonRepresentation recruitersWithResumes = CcpConstants.EMPTY_JSON;
		
		for (String recruiter : recruiters) {
			
			Set<String> allResumesReachedByThisRecruiter = new HashSet<>();
			
			List<CcpJsonRepresentation> recruiterPositions = new ArrayList<>(positions).stream().filter(position -> position.getAsString("email").equals(recruiter)).collect(Collectors.toList());
	
			for (CcpJsonRepresentation recruiterPosition : recruiterPositions) {
				List<String> allFilteredCandidatesToThisPosition = new ArrayList<>();
				List<String> positionHashes = recruiterPosition.getAsStringList("hash");
				
				for (String positionHash : positionHashes) {
					
					boolean ignoreThisHash = resumesHahes.isPresent(entityResumeHash.getEntityName(), positionHash) == false;
					
					if(ignoreThisHash) {
						continue;
					}
					
					CcpJsonRepresentation jsonResumeHash = resumesHahes.get(entityResumeHash.getEntityName(), positionHash);
				
					boolean firstPositionHash = allFilteredCandidatesToThisPosition.isEmpty();
					List<String> candidatesFromThisPositionHash = jsonResumeHash.getAsStringList("email");
					if(firstPositionHash) {
						allFilteredCandidatesToThisPosition = candidatesFromThisPositionHash;
					}
					
					List<String> intersectList = new CcpCollectionDecorator(allFilteredCandidatesToThisPosition).getIntersectList(candidatesFromThisPositionHash);
					allFilteredCandidatesToThisPosition = intersectList;
				}
				allResumesReachedByThisRecruiter.addAll(allFilteredCandidatesToThisPosition);
			}
			recruitersWithResumes = recruitersWithResumes.put(recruiter, allResumesReachedByThisRecruiter);
		}
		return recruitersWithResumes;
	}

	public CcpDaoUnionAll getAllResumesHashes(List<CcpJsonRepresentation> positions) {
		
		VisEntityHashGrouper entityResumeHash = new VisEntityHashGrouper();
		CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);

		
		HashSet<String> allHashes = new HashSet<>();
		
		for (CcpJsonRepresentation position : positions) {
			List<String> hashes = position.getAsStringList("hash");
			allHashes.addAll(hashes);
		}
		
		CcpDaoUnionAll resumesHahes = dao.unionAll(allHashes, entityResumeHash);
		return resumesHahes;
	}

	//TODO HASH DO E-MAIL
	private List<CcpJsonRepresentation> getPositionsFilteredByRecruiterFunds( CcpJsonRepresentation schedullingPlan, List<CcpJsonRepresentation> schedulledPositions){
		//TODO FILTRAR APENAS EMAIL + FREQUENCY COM SALDO PARA ENVIO
		List<CcpJsonRepresentation> allBalances = new VisEntityBalance().getManyByIds(schedulledPositions);

		List<CcpJsonRepresentation> balancesNotFound = new ArrayList<CcpJsonRepresentation>(allBalances).stream().filter(balance -> balance.getAsBoolean("_found") == false).collect(Collectors.toList());

		//TODO NOTIFICAÇÃO PARA RECRUTADOR
		CcpConstants.EMPTY_JSON.put("reason", "balanceNotFound").put("data", balancesNotFound);
		
		List<CcpJsonRepresentation> balancesFound = new ArrayList<CcpJsonRepresentation>(allBalances).stream().filter(balance -> balance.getAsBoolean("_found")).collect(Collectors.toList());
		
		CcpJsonRepresentation jsonFee = new VisEntityFees().getOneById(schedullingPlan);
		
		Double fee = jsonFee.getAsDoubleNumber("fee");

		List<CcpJsonRepresentation> balancesAble = new ArrayList<CcpJsonRepresentation>(balancesFound)
				.stream().filter(balance -> balance.getAsDoubleNumber("balance") > fee)
				.collect(Collectors.toList());
		
		List<CcpJsonRepresentation> insufficientFunds = new ArrayList<CcpJsonRepresentation>(balancesFound)
				.stream().filter(balance -> balance.getAsDoubleNumber("balance") <= fee)
				.map(x -> x.putAll(x.getInnerJson("_originalQuery"))
						.removeKey("_originalQuery").put("fee", fee)
						.putAll(schedullingPlan))
				.collect(Collectors.toList());
		//TODO NOTIFICAÇÃO PARA RECRUTADOR
		CcpConstants.EMPTY_JSON.put("reason", "insufficientFunds").put("data", insufficientFunds);
		
		return balancesAble;
	}
}
