package com.ccp.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionStatusChange;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionRecruiterGrouper;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionNotificationsGrouper;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionResumesListing;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionResumesReceiving;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionResumesSend;
import com.ccp.vis.async.business.recruiter.VisAsyncBusinessRecruiterReceivingResumes;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketGet;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeOpinionChange;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeStatusChange;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeDelete;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeNotificationsGrouper;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeOpinionSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeViewsRecruiterGrouper;
import com.ccp.vis.async.business.skills.VisAsyncBusinessSkillsSuggest;
import com.jn.vis.commons.utils.VisAsyncBusiness;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	public static final VisAsyncBusinessFactory INSTANCE = new VisAsyncBusinessFactory();
	// Essa classe faz a ligação entre as filas e seus consumidores. De um lado você tem o nome da fila 
	//	do outro você tem a classe responsável pelo tratamento dessa fila.
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	private VisAsyncBusinessFactory() {
		this.map.put(VisAsyncBusiness.resumeViewsGrouperByRecruiter.name(), VisAsyncBusinessResumeViewsRecruiterGrouper.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeGrouperNotifications.name(), VisAsyncBusinessResumeNotificationsGrouper.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeChangeOpinion.name(), VisAsyncBusinessResumeOpinionChange.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeChangeStatus.name(),VisAsyncBusinessResumeStatusChange.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeSaveOpinion.name(), VisAsyncBusinessResumeOpinionSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketSave.name(), VisAsyncBusinessResumeBucketSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketGet.name(), VisAsyncBusinessResumeBucketGet.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeDelete.name(), VisAsyncBusinessResumeDelete.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeSave.name(), VisAsyncBusinessResumeSave.INSTANCE);

		this.map.put(VisAsyncBusiness.positionGrouperNotifications.name(), VisAsyncBusinessPositionNotificationsGrouper.INSTANCE);
		this.map.put(VisAsyncBusiness.positionSendResumesToEmails.name(), VisAsyncBusinessPositionResumesSend.INSTANCE);
		this.map.put(VisAsyncBusiness.positionGrouperByRecruiter.name(), VisAsyncBusinessPositionRecruiterGrouper.INSTANCE);
		this.map.put(VisAsyncBusiness.positionReceivingResumes.name(), VisAsyncBusinessPositionResumesReceiving.INSTANCE);
		this.map.put(VisAsyncBusiness.positionListingResumes.name(), VisAsyncBusinessPositionResumesListing.INSTANCE);
		this.map.put(VisAsyncBusiness.positionChangeStatus.name(), VisAsyncBusinessPositionStatusChange.INSTANCE);

		this.map.put(VisAsyncBusiness.recruiterReceivingResumes.name(), VisAsyncBusinessRecruiterReceivingResumes.INSTANCE);
		
		this.map.put(VisAsyncBusiness.skillsSuggest.name(), VisAsyncBusinessSkillsSuggest.INSTANCE);

	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
