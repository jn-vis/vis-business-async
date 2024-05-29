package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.file.bucket.CcpFileBucketOperation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.utils.VisCommonsUtils;

public class VisAsyncBusinessResumeDelete implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeDelete INSTANCE = new VisAsyncBusinessResumeDelete();

	private VisAsyncBusinessResumeDelete() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		VisCommonsUtils.removeFromCache(json, "text", "file");

		String tentant = VisCommonsUtils.getTenant();

		String folder = VisCommonsUtils.getBucketFolderResume(json);
		
		CcpFileBucketOperation.delete.execute(tentant, folder, "text", "file");

		JnAsyncCommitAndAudit.INSTANCE.executeBulk(json, VisEntityResume.INSTANCE, CcpEntityOperationType.delete);
		
		return json;
	}


}
