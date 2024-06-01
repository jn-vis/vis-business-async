package com.ccp.vis.async.business.recruiter;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumeViewsByRecruiter;
import com.jn.vis.commons.entities.VisEntityResumeView;

public class VisAsyncBusinessGroupResumeViewsByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByRecruiter() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByRecruiter INSTANCE = new VisAsyncBusinessGroupResumeViewsByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeView.INSTANCE, 
				VisEntityGroupResumeViewsByRecruiter.INSTANCE, 
				VisEntityResumeView.Fields.email, 
				VisEntityGroupResumeViewsByRecruiter.Fields.viewDetails, 
				VisEntityResumeView.Fields.timestamp
				);
		
		return groupDetailsByMasters;
	}

}
