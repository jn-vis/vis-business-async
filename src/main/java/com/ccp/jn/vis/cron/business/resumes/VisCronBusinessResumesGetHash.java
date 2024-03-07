package com.ccp.jn.vis.cron.business.resumes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.ccp.jn.async.business.JnAsyncBusinessCommitAndAudit;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityHashGrouper;

public class VisCronBusinessResumesGetHash implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	private JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.startRange()
							.startFieldRange("lastUpdate")
								.greaterThan(System.currentTimeMillis() - 3_600_000)
							.endFieldRangeAndBackToRange()
						.endRangeAndBackToSimplifiedQuery()
					.endSimplifiedQueryAndBackToRequest()
				;
		String[] resourcesNames = new String[] {new VisEntityResume().name()};

		List<CcpJsonRepresentation> resumes = queryExecutor.getResultAsList(queryToSearchLastUpdatedResumes, resourcesNames, "hash");
		
		CcpJsonRepresentation allHashes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation resume : resumes) {
			String email = resume.getAsString("email");
			CcpJsonRepresentation jsonHash = resume.getInnerJson("hash");
			List<String> hashesToRemove = jsonHash.getAsStringList("remove");
			List<String> hashesToInsert = jsonHash.getAsStringList("insert");
			allHashes = this.putHash(allHashes, email, hashesToRemove, "remove");
			allHashes = this.putHash(allHashes, email, hashesToInsert, "insert");
		}
		
		Set<String> keySet = allHashes.keySet();
		String[] array = keySet.toArray(new String[keySet.size()]);
		List<CcpJsonRepresentation> manyByIds = new VisEntityHashGrouper().getManyByIds(array);
		List<CcpJsonRepresentation> hashesToCreate = new ArrayList<>();
		List<CcpJsonRepresentation> hashesToUpdate = new ArrayList<>();
		
		for (CcpJsonRepresentation item : manyByIds) {
			String hash = item.getAsString("_id");
			
			CcpJsonRepresentation hashJson = allHashes.getInnerJson(hash);
			
			List<String> allEmails = item.getAsCollectionDecorator("email").getSubCollection(0, 10_000_000).getExclusiveList(new ArrayList<String>());
			
			List<String> emailsToRemove = hashJson.getAsStringList("remove");
			
			List<String> emailsToInsert = hashJson.getAsStringList("insert");
			
			allEmails.removeAll(emailsToRemove);

			allEmails.addAll(emailsToInsert);
			
			CcpJsonRepresentation updatedHash = item.put("email", allEmails);
			
			boolean found = item.getAsBoolean("_found");
			
			if(found) {
				hashesToUpdate.add(updatedHash);
				continue;
			}
			hashesToCreate.add(updatedHash);
		}
		/*
		 * TODO PROBLEMA DE COMPETIÇÃO
		 */
		this.commitAndAudit.execute(hashesToCreate, CcpEntityOperationType.create, new VisEntityHashGrouper());

		this.commitAndAudit.execute(hashesToUpdate, CcpEntityOperationType.update, new VisEntityHashGrouper());
	
		return CcpConstants.EMPTY_JSON;
	}

	
	
	private CcpJsonRepresentation putHash(CcpJsonRepresentation allHashes, String email, List<String> hashes, String command) {
		for (String hash : hashes) {
			CcpJsonRepresentation innerJson = allHashes.getInnerJson(hash);
			innerJson = innerJson.addToList(command, email);
			allHashes = allHashes.put(hash, innerJson);
		}
		return allHashes;
	}

}
