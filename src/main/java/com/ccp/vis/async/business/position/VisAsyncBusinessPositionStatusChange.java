package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionStatusChange implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionStatusChange() {}
	
	public static final VisAsyncBusinessPositionStatusChange INSTANCE = new VisAsyncBusinessPositionStatusChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> whenPositionIsInactivated = data -> VisAsyncUtils.groupPositionsGroupedByRecruiters(data.duplicateValueFromField("email", "masters"));
		VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes whenPositionIsReactivated = VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes.INSTANCE;
		VisAsyncUtils.changeStatus(json, VisEntityPosition.INSTANCE, whenPositionIsReactivated, whenPositionIsInactivated);
		return json;
	}

}
