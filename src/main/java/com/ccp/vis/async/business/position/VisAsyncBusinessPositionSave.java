package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionSave() {}
	
	public static final VisAsyncBusinessPositionSave INSTANCE = new VisAsyncBusinessPositionSave();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		VisEntityPosition.INSTANCE.createOrUpdate(json);
		
		CcpJsonRepresentation apply = VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes.INSTANCE.apply(json);
		
		return apply;
	}

}
