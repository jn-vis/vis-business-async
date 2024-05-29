package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;

public class VisAsyncBusinessResumeBucketSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeBucketSave INSTANCE = new VisAsyncBusinessResumeBucketSave();

	private VisAsyncBusinessResumeBucketSave() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		VisAsyncUtils.saveResume(json, "resumeBase64", "file");
		VisAsyncUtils.saveResume(json, "resumeText", "text");
		return json;
	}

}
