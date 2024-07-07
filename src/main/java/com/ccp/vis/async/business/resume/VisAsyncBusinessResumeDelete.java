package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.utils.VisCommonsUtils;
import com.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeDelete implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeDelete INSTANCE = new VisAsyncBusinessResumeDelete();

	private VisAsyncBusinessResumeDelete() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		String tentant = VisCommonsUtils.getTenant();

		String email = json.getAsString("email");
		String folder = "resumes/" + email;
		
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);

		bucket.delete(tentant, folder);
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(json, VisEntityResume.INSTANCE, CcpEntityOperationType.delete);
		
		return json;
	}


}
