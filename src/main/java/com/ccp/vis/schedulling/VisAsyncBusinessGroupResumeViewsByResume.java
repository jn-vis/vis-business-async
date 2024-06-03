package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumeViewsByResume;
import com.jn.vis.commons.entities.VisEntityResumeFreeView;

public class VisAsyncBusinessGroupResumeViewsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumeViewsByResume() {}
	
	public static final VisAsyncBusinessGroupResumeViewsByResume INSTANCE = new VisAsyncBusinessGroupResumeViewsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeFreeView.INSTANCE, 
				VisEntityGroupResumeViewsByResume.INSTANCE, 
				VisEntityResumeFreeView.Fields.email, 
				VisEntityResumeFreeView.Fields.timestamp
				);
		
		return groupDetailsByMasters;
	}

}
