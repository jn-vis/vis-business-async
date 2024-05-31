package com.ccp.vis.async.business.position;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncBusinessPositionStatusChange implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessPositionStatusChange() {}
	
	public static final VisAsyncBusinessPositionStatusChange INSTANCE = new VisAsyncBusinessPositionStatusChange();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> whenPositionIsInactivated = y -> VisAsyncUtils.groupPositionsByRecruiters(y, x -> x.getMirrorEntity(), json.getAsStringList("email"));
		VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes whenPositionIsReactivated = VisAsyncBusinessPositionUpdateGroupingByRecruitersAndSendResumes.INSTANCE;
		VisAsyncUtils.changeStatus(json, VisEntityPosition.INSTANCE, whenPositionIsReactivated, whenPositionIsInactivated
		);
		return json;
	}

}
