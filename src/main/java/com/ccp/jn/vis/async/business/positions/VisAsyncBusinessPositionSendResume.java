package com.ccp.jn.vis.async.business.positions;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityDeniedViewToCompany;
import com.jn.vis.commons.entities.VisEntityResumeHash;

public class VisAsyncBusinessPositionSendResume  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		
		List<String> hashes = position.getAsStringList("hash");
		
		List<CcpJsonRepresentation> lista = new VisEntityResumeHash().getManyById(hashes);
		
		CcpCollectionDecorator allEmails = lista.get(0).getAsCollectionDecorator("email");
		
		for (CcpJsonRepresentation item : lista) {
			List<String> emails = item.getAsStringList("email");
			List<Object> intersectList = allEmails.getIntersectList(emails);
			allEmails = new CcpCollectionDecorator(intersectList);
		}
		
		
		String recruiter = position.getAsString("email");
		String domain = recruiter.split("@")[1];
		
		List<CcpJsonRepresentation> collect = allEmails.content.stream()
		.map(email -> CcpConstants.EMPTY_JSON.put("domain", domain)
				.put("recruiter", recruiter)
				.put("email", email))
		.collect(Collectors.toList());
		;
		
		
		
		return CcpConstants.EMPTY_JSON;
	}

}
