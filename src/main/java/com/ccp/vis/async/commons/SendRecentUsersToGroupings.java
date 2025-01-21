package com.ccp.vis.async.commons;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.jn.commons.entities.JnEntityDisposableRecord;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.vis.commons.utils.VisAsyncBusiness;

public class SendRecentUsersToGroupings implements Consumer<List<CcpJsonRepresentation>> {
	
	private SendRecentUsersToGroupings() {}
	
	public static SendRecentUsersToGroupings INSTANCE = new SendRecentUsersToGroupings();

	public void accept(List<CcpJsonRepresentation> records) {
		List<String> emails = records.stream()
		.map(rec ->	rec.getAsString(JnEntityDisposableRecord.Fields.id.name()))
		.map(id -> new CcpJsonRepresentation(id))
		.map(json -> json.getAsString(JnEntityLoginSessionCurrent.Fields.email.name()))
		.collect(Collectors.toList());
		
		CcpJsonRepresentation message = CcpOtherConstants.EMPTY_JSON.put("masters", emails);
		
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.groupResumesOpinionsByRecruiter, message);
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.groupResumesOpinionsByResume, message);
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.groupResumeViewsByRecruiter, message);
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.groupResumeViewsByResume, message);
	}

}
