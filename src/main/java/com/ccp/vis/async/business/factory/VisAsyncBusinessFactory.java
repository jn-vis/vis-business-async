package com.ccp.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.vis.async.business.positions.VisAsyncBusinessPositionGrouperNotifications;
import com.ccp.vis.async.business.positions.VisAsyncBusinessPositionInactivate;
import com.ccp.vis.async.business.positions.VisAsyncBusinessPositionSearchResumes;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketGet;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeBucketSave;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeChangeStatus;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeDelete;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeGrouperNotifications;
import com.ccp.vis.async.business.resume.VisAsyncBusinessResumeSendToRecruiters;
import com.jn.vis.commons.utils.VisAsyncBusiness;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	public static final VisAsyncBusinessFactory INSTANCE = new VisAsyncBusinessFactory();
	// Essa classe faz a ligação entre as filas e seus consumidores. De um lado você tem o nome da fila 
	//	do outro você tem a classe responsável pelo tratamento dessa fila.
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	private VisAsyncBusinessFactory() {
		this.map.put(VisAsyncBusiness.positionGrouperNotifications.name(), VisAsyncBusinessPositionGrouperNotifications.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeGrouperNotifications.name(), VisAsyncBusinessResumeGrouperNotifications.INSTANCE);
		this.map.put(VisAsyncBusiness.sendResumesToThisPositions.name(), VisAsyncBusinessPositionSearchResumes.INSTANCE);
		this.map.put(VisAsyncBusiness.sendResumeToRecruiters.name(), VisAsyncBusinessResumeSendToRecruiters.INSTANCE);
		this.map.put(VisAsyncBusiness.inactivatePosition.name(), VisAsyncBusinessPositionInactivate.INSTANCE);
		this.map.put(VisAsyncBusiness.changeResumeStatus.name(),VisAsyncBusinessResumeChangeStatus.INSTANCE);
		this.map.put(VisAsyncBusiness.resumeBucketGet.name(), VisAsyncBusinessResumeBucketGet.INSTANCE);
		this.map.put(VisAsyncBusiness.saveResumeFile.name(), VisAsyncBusinessResumeBucketSave.INSTANCE);
		this.map.put(VisAsyncBusiness.deleteResume.name(), VisAsyncBusinessResumeDelete.INSTANCE);

	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
