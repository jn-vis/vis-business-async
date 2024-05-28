package com.ccp.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionChangeStatus;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionGrouperByRecruiter;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionGrouperNotifications;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionListingResumes;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionReceivingResumes;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionSendResumes;
import com.ccp.vis.async.business.recruiter.VisAsyncBusinessRecruiterReceivingResumes;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketGet;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeChangeOpinion;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeChangeStatus;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeDelete;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeGrouperNotifications;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeSaveOpinion;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeViewsGrouperByRecruiter;
import com.ccp.vis.async.business.skills.VisAsyncBusinessSkillsSuggest;
import com.jn.vis.commons.utils.VisAsyncBusiness;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	public static final VisAsyncBusinessFactory INSTANCE = new VisAsyncBusinessFactory();
	// Essa classe faz a ligação entre as filas e seus consumidores. De um lado você tem o nome da fila 
	//	do outro você tem a classe responsável pelo tratamento dessa fila.
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	private VisAsyncBusinessFactory() {
		this.map.put(VisAsyncBusiness.resumeViewsGrouperByRecruiter.name(), VisAsyncBusinessResumeViewsGrouperByRecruiter.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeGrouperNotifications.name(), VisAsyncBusinessResumeGrouperNotifications.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeChangeOpinion.name(), VisAsyncBusinessResumeChangeOpinion.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeChangeStatus.name(),VisAsyncBusinessResumeChangeStatus.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeSaveOpinion.name(), VisAsyncBusinessResumeSaveOpinion.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketSave.name(), VisAsyncBusinessResumeBucketSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketGet.name(), VisAsyncBusinessResumeBucketGet.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeDelete.name(), VisAsyncBusinessResumeDelete.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeSave.name(), VisAsyncBusinessResumeSave.INSTANCE);

		this.map.put(VisAsyncBusiness.positionGrouperNotifications.name(), VisAsyncBusinessPositionGrouperNotifications.INSTANCE);
		this.map.put(VisAsyncBusiness.positionSendResumesToEmails.name(), VisAsyncBusinessPositionSendResumes.INSTANCE);
		this.map.put(VisAsyncBusiness.positionGrouperByRecruiter.name(), VisAsyncBusinessPositionGrouperByRecruiter.INSTANCE);
		this.map.put(VisAsyncBusiness.positionReceivingResumes.name(), VisAsyncBusinessPositionReceivingResumes.INSTANCE);
		this.map.put(VisAsyncBusiness.positionListingResumes.name(), VisAsyncBusinessPositionListingResumes.INSTANCE);
		this.map.put(VisAsyncBusiness.positionChangeStatus.name(), VisAsyncBusinessPositionChangeStatus.INSTANCE);

		this.map.put(VisAsyncBusiness.recruiterReceivingResumes.name(), VisAsyncBusinessRecruiterReceivingResumes.INSTANCE);
		
		this.map.put(VisAsyncBusiness.skillsSuggest.name(), VisAsyncBusinessSkillsSuggest.INSTANCE);

	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
