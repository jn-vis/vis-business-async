package com.ccp.vis.async.business.recruiter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.vis.commons.entities.VisEntityPosition;
import com.vis.commons.entities.VisEntityResume;
import com.vis.commons.entities.VisEntityResumeFreeView;
import com.vis.commons.entities.VisEntityResumeLastView;
import com.vis.commons.entities.VisEntityResumePerception;

public class VisAsyncBusinessResumeViewSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessResumeViewSave() {}
	
	public static final VisAsyncBusinessResumeViewSave INSTANCE = new VisAsyncBusinessResumeViewSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		List<CcpBulkItem> bulkItems = new ArrayList<>();
		
		boolean resumeViewIsNotFree = VisEntityResumeFreeView.ENTITY.isPresentInThisJsonInMainEntity(json);
		
		if(resumeViewIsNotFree) {
			//LATER IMPLEMENTAR PARTE FINANCEIRA
		}

		boolean negativatedResume = VisEntityResumePerception.ENTITY.getTwinEntity().isPresentInThisJsonInMainEntity(json);
		boolean inactivePosition = VisEntityPosition.ENTITY.getTwinEntity().isPresentInThisJsonInMainEntity(json);
	
//		CcpJsonRepresentation opinion = VisEntityResumePerception.INSTANCE.getInnerJsonFromMainAndMirrorEntities(json);
		CcpJsonRepresentation position = VisEntityPosition.ENTITY.getInnerJsonFromMainAndTwinEntities(json);
		CcpJsonRepresentation resume = VisEntityResume.ENTITY.getInnerJsonFromMainAndTwinEntities(json);
		
		CcpJsonRepresentation dataToSave = json
				.put(VisEntityResumeLastView.Fields.resume.name(), resume)
//				.put(VisEntityResumeLastView.Fields.opinion.name(), opinion)
				.put(VisEntityResumeLastView.Fields.position.name(), position)
				.put(VisEntityResumeLastView.Fields.inactivePosition.name(), inactivePosition)
				.put(VisEntityResumeLastView.Fields.negativatedResume.name(), negativatedResume)
				;
		
		CcpBulkItem itemResumeLastView = VisEntityResumeLastView.ENTITY.toBulkItem(dataToSave, CcpEntityOperationType.create);
		CcpBulkItem itemResumeFreeView = VisEntityResumeFreeView.ENTITY.toBulkItem(dataToSave, CcpEntityOperationType.create);
		
		bulkItems.add(itemResumeFreeView);
		bulkItems.add(itemResumeLastView);
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(bulkItems);
		return json;
	}

	
}
