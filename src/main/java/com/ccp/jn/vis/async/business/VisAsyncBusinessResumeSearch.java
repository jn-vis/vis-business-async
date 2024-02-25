package com.ccp.jn.vis.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.ccp.jn.async.business.JnAsyncBusinessCommitAndAudit;
import com.ccp.jn.vis.async.business.resumes.send.VisAsyncBusinessSchedulledSendingResumeToRecruiter;
import com.ccp.jn.vis.async.business.utils.CalculateHashes;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityPositionAndResume;
import com.jn.vis.commons.entities.VisEntityPositionDailySendingResumes;
import com.jn.vis.commons.entities.VisEntityPositionHourlySendingResumes;
import com.jn.vis.commons.entities.VisEntityPositionMonthlySendingResumes;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSearch
		implements java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		new VisEntityPosition().create(json);
		// TODO verificar saldo do recrutador na LN
		List<CcpJsonRepresentation> hashes = CalculateHashes.getHashes(json);
		List<CcpJsonRepresentation> resumes = new VisEntityResume().getManyByIds(hashes);
		String recruiter = json.getAsString("email");
		String title = json.getAsString("title");
		List<CcpJsonRepresentation> positionsAndResumes = resumes.stream()
				.map(x -> x.put("recruiter", recruiter).put("title", title)).collect(Collectors.toList());

		String frequency = json.getAsString("frequency");

		boolean instantSending = "minute".equals(frequency);
		
		if(instantSending) {
			this.commitAndAudit.execute(positionsAndResumes, CcpEntityOperationType.create,new VisEntityPositionAndResume());
			CcpJsonRepresentation apply = new VisAsyncBusinessSchedulledSendingResumeToRecruiter().apply(json);
			return apply;			
		}
		
		JnBaseEntity entity = switch (frequency) {
		case "monthly" -> new VisEntityPositionMonthlySendingResumes();
		case "hourly" -> new VisEntityPositionHourlySendingResumes();
		case "daily" -> new VisEntityPositionDailySendingResumes();
		default -> null;
		};
		
		this.commitAndAudit.execute(positionsAndResumes, CcpEntityOperationType.create, entity);
		this.commitAndAudit.execute(positionsAndResumes, CcpEntityOperationType.create,new VisEntityPositionAndResume());

		
		return CcpConstants.EMPTY_JSON;
	}

}
