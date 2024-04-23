package com.ccp.jn.vis.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.jn.vis.async.business.positions.VisAsyncBusinessPositionInactivate;
import com.ccp.jn.vis.async.business.positions.VisAsyncBusinessPositionSchedulling;
import com.ccp.jn.vis.async.business.positions.VisAsyncBusinessPositionSendResume;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeInactivate;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeSave;
import com.ccp.jn.vis.async.business.resume.VisAsyncBusinessResumeSaveFile;
import com.jn.vis.commons.utils.VisTopics;

class VisAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	public VisAsyncBusinessFactory() {
		this.map.put(VisTopics.sendResumesToThisPosition.name(), new VisAsyncBusinessPositionSendResume());
		this.map.put(VisTopics.inactivatePosition.name(), new VisAsyncBusinessPositionInactivate());
		this.map.put(VisTopics.inactivateResume.name(), new VisAsyncBusinessResumeInactivate());
		this.map.put(VisTopics.savePosition.name(), new VisAsyncBusinessPositionSchedulling());
		this.map.put(VisTopics.saveResumeFile.name(), new VisAsyncBusinessResumeSaveFile());
		this.map.put(VisTopics.saveResume.name(), new VisAsyncBusinessResumeSave());
	}

	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}
	
	
}
