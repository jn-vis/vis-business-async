package com.ccp.vis.async.business.recruiter;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumeViewsByRecruiter;
import com.jn.vis.commons.entities.VisEntityResumeFreeView;

public class VisAsyncBusinessGroupResumeViewsByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByRecruiter() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByRecruiter INSTANCE = new VisAsyncBusinessGroupResumeViewsByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeFreeView.INSTANCE, 
				VisEntityGroupResumeViewsByRecruiter.INSTANCE, 
				VisEntityResumeFreeView.Fields.email, 
				VisEntityGroupResumeViewsByRecruiter.Fields.viewDetails, 
				VisEntityResumeFreeView.Fields.timestamp
				);
		
		return groupDetailsByMasters;
	}

}
