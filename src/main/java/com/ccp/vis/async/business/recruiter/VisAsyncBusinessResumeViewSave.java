package com.ccp.vis.async.business.recruiter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeFreeView;
import com.jn.vis.commons.entities.VisEntityResumeLastView;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;

public class VisAsyncBusinessResumeViewSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeViewSave() {}
	
	public static final VisAsyncBusinessResumeViewSave INSTANCE = new VisAsyncBusinessResumeViewSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		List<CcpBulkItem> bulkItems = new ArrayList<>();
		
		boolean resumeViewIsNotFree = VisEntityResumeFreeView.INSTANCE.isPresentInThisJsonInMainEntity(json);
		
		if(resumeViewIsNotFree) {
			//TODO IMPLEMENTAR PARTE FINANCEIRA
		}

		boolean negativatedResume = VisEntityResumeOpinion.INSTANCE.getMirrorEntity().isPresentInThisJsonInMainEntity(json);
		boolean inactivePosition = VisEntityPosition.INSTANCE.getMirrorEntity().isPresentInThisJsonInMainEntity(json);
	
		CcpJsonRepresentation opinion = VisEntityResumeOpinion.INSTANCE.getInnerJsonFromMainAndMirrorEntities(json);
		CcpJsonRepresentation position = VisEntityPosition.INSTANCE.getInnerJsonFromMainAndMirrorEntities(json);
		CcpJsonRepresentation resume = VisEntityResume.INSTANCE.getInnerJsonFromMainAndMirrorEntities(json);
		
		CcpJsonRepresentation dataToSave = json
				.put(VisEntityResumeLastView.Fields.resume.name(), resume)
				.put(VisEntityResumeLastView.Fields.opinion.name(), opinion)
				.put(VisEntityResumeLastView.Fields.position.name(), position)
				.put(VisEntityResumeLastView.Fields.inactivePosition.name(), inactivePosition)
				.put(VisEntityResumeLastView.Fields.negativatedResume.name(), negativatedResume)
				;
		
		CcpBulkItem itemResumeLastView = VisEntityResumeLastView.INSTANCE.toBulkItem(dataToSave, CcpEntityOperationType.create);
		CcpBulkItem itemResumeFreeView = VisEntityResumeFreeView.INSTANCE.toBulkItem(dataToSave, CcpEntityOperationType.create);
		
		bulkItems.add(itemResumeFreeView);
		bulkItems.add(itemResumeLastView);
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(bulkItems);
		return json;
	}

	
}
