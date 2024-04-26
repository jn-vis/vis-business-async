package com.ccp.jn.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.jn.vis.async.business.positions.VisAsyncBusinessPositionInactivate;
import com.ccp.jn.vis.async.business.positions.VisAsyncBusinessPositionSendResume;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeInactivate;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeSendToRecruiters;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeSaveFile;
import com.jn.vis.commons.utils.VisTopics;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	// Essa classe faz a ligação entre as filas e seus consumidores. De um lado você tem o nome da fila 
	//	do outro você tem a classe responsável pelo tratamento dessa fila.
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	public VisAsyncBusinessFactory() {
		this.map.put(VisTopics.sendResumeToRecruiters.name(), new VisAsyncBusinessResumeSendToRecruiters());
		this.map.put(VisTopics.sendResumesToThisPositions.name(), new VisAsyncBusinessPositionSendResume());
		this.map.put(VisTopics.inactivatePosition.name(), new VisAsyncBusinessPositionInactivate());
		this.map.put(VisTopics.inactivateResume.name(), new VisAsyncBusinessResumeInactivate());
		this.map.put(VisTopics.saveResumeFile.name(), new VisAsyncBusinessResumeSaveFile());
	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
