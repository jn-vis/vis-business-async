package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityGroupResumesByPosition;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes() {}
	
	public static final VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes INSTANCE = new VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation duplicateValueFromKey = json.duplicateValueFromKey("email", "masters");
		
		VisAsyncUtils.groupPositionsByRecruiters(duplicateValueFromKey);
		
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getLastUpdatedResumes = x -> VisAsyncUtils.getLastUpdated(VisEntityResume.INSTANCE, FrequencyOptions.yearly, VisEntityResume.Fields.timestamp.name());
		
		List<String> email = json.getAsStringList("email");

		Function<String, CcpJsonRepresentation> getSavingPosition = frequency -> CcpConstants.EMPTY_JSON.put(email.get(0), json);

		List<CcpJsonRepresentation> positionsWithFilteredAndSortedResumesAndTheirStatis = VisAsyncUtils.sendFilteredAndSortedResumesAndTheirStatisByEachPositionToEachRecruiter(json, getLastUpdatedResumes, getSavingPosition);
		
		CcpJsonRepresentation positionWithFilteredAndSortedResumesAndTheirStatis = positionsWithFilteredAndSortedResumesAndTheirStatis.get(0);
		
		List<CcpJsonRepresentation> resumes = positionWithFilteredAndSortedResumesAndTheirStatis.getAsJsonList("resumes");
		
		CcpJsonRepresentation position = positionWithFilteredAndSortedResumesAndTheirStatis.getInnerJson("position");

		List<CcpBulkItem> allPagesTogether = new ArrayList<>();
		int listSize = 10;
		int totalPages = resumes.size()  % listSize + 1;
		int resumeIndex = 0;
		
		for(int from = 0; from < totalPages; from++) {
			List<CcpJsonRepresentation> resumesPage = new ArrayList<>();
			for(;(resumeIndex + 1) % listSize !=0 && resumeIndex < resumes.size(); resumeIndex++) {
				CcpJsonRepresentation resume = resumes.get(resumeIndex);
				CcpJsonRepresentation put = resume.put("resumeIndex", resumeIndex);
				resumesPage.add(put);
			}
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
					.put("resumes", resumesPage)
					.put("position", position)
					.put("listSize", listSize)
					.put("from", from)
					;
			CcpBulkItem bulkItem = VisEntityGroupResumesByPosition.INSTANCE.toBulkItem(put, CcpEntityOperationType.create);
			allPagesTogether.add(bulkItem);
		}
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(allPagesTogether);
		
		//TODO descobrir uma forma de gravar o agrupamento de vagas por currículos
		
		return positionWithFilteredAndSortedResumesAndTheirStatis;
	}

}
