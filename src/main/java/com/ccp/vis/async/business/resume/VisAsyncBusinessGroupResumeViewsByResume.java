package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumeViewsByResume;
import com.jn.vis.commons.entities.VisEntityResumeView;

public class VisAsyncBusinessGroupResumeViewsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByResume() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByResume INSTANCE = new VisAsyncBusinessGroupResumeViewsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeView.INSTANCE, 
				VisEntityGroupResumeViewsByResume.INSTANCE, 
				VisEntityResumeView.Fields.email, 
				VisEntityGroupResumeViewsByResume.Fields.viewDetails, 
				VisEntityResumeView.Fields.timestamp
				);
		
		return groupDetailsByMasters;
	}

}
