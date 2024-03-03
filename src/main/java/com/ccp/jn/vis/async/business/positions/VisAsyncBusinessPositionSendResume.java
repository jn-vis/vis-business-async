package com.ccp.jn.vis.async.business.positions;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityResumeHash;

public class VisAsyncBusinessPositionSendResume  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation position) {
		
		List<String> hashes = position.getAsStringList("hash");
		
		List<CcpJsonRepresentation> lista = new VisEntityResumeHash().getManyById(hashes);
		
		CcpCollectionDecorator allEmails = lista.get(0).getAsCollectionDecorator("email");
		
		for (CcpJsonRepresentation item : lista) {
			List<String> emails = item.getAsStringList("email");
			List<String> intersectList = allEmails.getIntersectList(emails);
			allEmails = new CcpCollectionDecorator(intersectList);
		}
		/*
		 * Candidatos negativados por recrutadores (email_recruiter)
		 * Consultorias proibidas por candidatos (domain_email)
		 * Ultimas visualizações de candidatos por recrutadores (email_recruiter)
		 * Reputações (email)
		 */
		
		List<CcpJsonRepresentation> collect = allEmails.content.stream()
		.map(email -> position
				.putTransformedValue("email", "domain", mail -> mail.toString().split("@")[1])
				.renameKey("email", "recruiter")
				.put("email", email)
				)
		.collect(Collectors.toList());
		;

		return CcpConstants.EMPTY_JSON;
	}

}
