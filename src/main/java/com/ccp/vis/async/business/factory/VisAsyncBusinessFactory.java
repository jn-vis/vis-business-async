package com.ccp.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionResumesSend;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionSave;
import com.ccp.vis.async.business.position.VisAsyncBusinessPositionStatusChange;
import com.ccp.vis.async.business.recruiter.VisAsyncBusinessRecruiterReceivingResumes;
import com.ccp.vis.async.business.recruiter.VisAsyncBusinessResumeViewSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResume;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeDelete;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeOpinionChange;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeOpinionSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeStatusChange;
import com.ccp.vis.async.business.skills.VisAsyncBusinessSkillsSuggest;
import com.ccp.vis.schedulling.VisAsyncBusinessGetRecentLoggedUsers;
import com.ccp.vis.schedulling.VisAsyncBusinessGroupResumeViewsByRecruiter;
import com.ccp.vis.schedulling.VisAsyncBusinessGroupResumeViewsByResume;
import com.ccp.vis.schedulling.VisAsyncBusinessGroupResumesOpinionsByRecruiter;
import com.ccp.vis.schedulling.VisAsyncBusinessGroupResumesOpinionsByResume;
import com.ccp.vis.schedulling.VisAsyncBusinessPositionResumesReceivingByFrequency;
import com.jn.vis.commons.utils.VisAsyncBusiness;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	public static final VisAsyncBusinessFactory INSTANCE = new VisAsyncBusinessFactory();
	// Essa classe faz a ligação entre as filas e seus consumidores. De um lado você tem o nome da fila 
	//	do outro você tem a classe responsável pelo tratamento dessa fila.
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	private VisAsyncBusinessFactory() {
		
		this.map.put(VisAsyncBusiness.groupResumesOpinionsByResume.name(), VisAsyncBusinessGroupResumesOpinionsByResume.INSTANCE);
		this.map.put(VisAsyncBusiness.groupResumeViewsByResume.name(), VisAsyncBusinessGroupResumeViewsByResume.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeOpinionChange.name(), VisAsyncBusinessResumeOpinionChange.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeStatusChange.name(),VisAsyncBusinessResumeStatusChange.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeOpinionSave.name(), VisAsyncBusinessResumeOpinionSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketSave.name(), VisAsyncBusinessResumeBucketSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeDelete.name(), VisAsyncBusinessResumeDelete.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeSave.name(), VisAsyncBusinessResumeSave.INSTANCE);
		this.map.put(VisAsyncBusiness.resume.name(), VisAsyncBusinessResume.INSTANCE);

		this.map.put(VisAsyncBusiness.positionResumesReceivingByFrequency.name(), VisAsyncBusinessPositionResumesReceivingByFrequency.INSTANCE);
		this.map.put(VisAsyncBusiness.groupResumesOpinionsByRecruiter.name(), VisAsyncBusinessGroupResumesOpinionsByRecruiter.INSTANCE);
		this.map.put(VisAsyncBusiness.groupResumeViewsByRecruiter.name(), VisAsyncBusinessGroupResumeViewsByRecruiter.INSTANCE);
		this.map.put(VisAsyncBusiness.positionStatusChange.name(), VisAsyncBusinessPositionStatusChange.INSTANCE);
		this.map.put(VisAsyncBusiness.positionResumesSend.name(), VisAsyncBusinessPositionResumesSend.INSTANCE);
		this.map.put(VisAsyncBusiness.positionSave.name(), VisAsyncBusinessPositionSave.INSTANCE);

		this.map.put(VisAsyncBusiness.recruiterReceivingResumes.name(), VisAsyncBusinessRecruiterReceivingResumes.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeViewSave.name(), VisAsyncBusinessResumeViewSave.INSTANCE);
		
		this.map.put(VisAsyncBusiness.skillsSuggest.name(), VisAsyncBusinessSkillsSuggest.INSTANCE);
		
		this.map.put(VisAsyncBusiness.getRecentLoggedUsers.name(), VisAsyncBusinessGetRecentLoggedUsers.INSTANCE);

	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
