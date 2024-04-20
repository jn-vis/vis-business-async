package com.ccp.jn.vis.async.business.factory;

import com.ccp.dependency.injection.CcpInstanceProvider;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;

public class CcpVisAsyncBusinessFactory implements CcpInstanceProvider<CcpAsyncBusinessFactory> {

	public CcpAsyncBusinessFactory getInstance() {
		return new VisAsyncBusinessFactory();
	}
}
