package com.ccp.jn.vis.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.jn.async.business.JnAsyncBusinessCommitAndAudit;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityHashGrouper;

public class VisAsyncUtils {

	public static List<CcpJsonRepresentation> getHashes(CcpJsonRepresentation json, Function<CcpJsonRepresentation, CcpJsonRepresentation> function) {

		List<Integer> ddds = json.getAsStringList("ddd").stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());
		
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");

		List<String> synonyms = json.getAsStringList("synonym");
		
		List<Integer> disponibilities = json.get(new GetDisponibilityValues());

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(json);
		
		List<String> seniorities = json.get(new GetSenioritiesValues());

		List<Boolean> pcds = json.get(new GetPcdValues());
		
		List<CcpJsonRepresentation> hashes = new ArrayList<>();

		String email = json.getAsString("email");
		
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (String seniority : seniorities) {// vaga = [PL, SR] || candidato = 2 anos [JR]
					for (CcpJsonRepresentation moneyValue : moneyValues) {
						for (String resumeWord : resumeWords) {
							for (String synonym : synonyms) {
								for (Integer ddd : ddds) {
									CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON
											.put("disponibility", disponibility)
											.put("resumeWord", resumeWord)
											.put("seniority", seniority)
											.put("synonym", synonym)
											.put("email", email)
											.putAll(moneyValue)
											.put("pcd", pcd)
											.put("ddd", ddd)
											;
									CcpJsonRepresentation apply = function.apply(hash);
									hashes.add(apply);

								}
							}
						}
					}
				}
			}
		}		

		return hashes;
	}
	
	private static List<CcpJsonRepresentation> getMoneyValues(CcpJsonRepresentation json){
		ArrayList<CcpJsonRepresentation> result = new ArrayList<>();
		
		result.addAll(new GetMoneyValues("btc").apply(json));
		result.addAll(new GetMoneyValues("clt").apply(json));
		result.addAll(new GetMoneyValues("pj").apply(json));
		
		return result;
	}

	public static List<CcpJsonRepresentation> getLastUpdated(JnBaseEntity entity, PositionSendFrequency valueOf) {
		
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdated = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.startRange()
							.startFieldRange("lastUpdate")
								.greaterThan(System.currentTimeMillis() - valueOf.hours * 3_600_000)
							.endFieldRangeAndBackToRange()
						.endRangeAndBackToSimplifiedQuery()
					.endSimplifiedQueryAndBackToRequest()
				;
		String[] resourcesNames = new String[] {entity.getEntityName()};

		List<CcpJsonRepresentation> result = queryExecutor.getResultAsList(queryToSearchLastUpdated, resourcesNames);
		return result;
	}

	public static List<CcpJsonRepresentation> getPositionsBySchedullingFrequency(CcpJsonRepresentation schedullingPlan){
		String frequency = schedullingPlan.getAsString("frequency");
		PositionSendFrequency valueOf = PositionSendFrequency.valueOf(frequency);

		List<CcpJsonRepresentation> positions = getPositionsBySchedullingFrequency(valueOf);
		return positions;
	}

	public static List<CcpJsonRepresentation> getPositionsBySchedullingFrequency(PositionSendFrequency valueOf) {
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, valueOf)
					.endSimplifiedQueryAndBackToRequest()
				;
		String[] resourcesNames = new String[] {new VisEntityPosition().getEntityName()};

		List<CcpJsonRepresentation> positions = queryExecutor.getResultAsList(queryToSearchLastUpdatedResumes, resourcesNames);
		
		return positions;
	}
	
	public static void disableEntity(CcpJsonRepresentation id) {

	}

	public static List<String> calculateHashesAndSaveEntity(CcpJsonRepresentation newValue, JnBaseEntity entity) {
		List<String> saveEntityValue = saveEntityValue(newValue, entity, CcpConstants.DO_BY_PASS);
		return saveEntityValue;
	}
	
	public static List<String> saveEntityValue(CcpJsonRepresentation newValue, JnBaseEntity entity, Function<CcpJsonRepresentation, CcpJsonRepresentation> function) {
		
		VisEntityHashGrouper entityHash = new VisEntityHashGrouper();	

		CcpJsonRepresentation oldValue = entity.getOneById(newValue, CcpConstants.DO_BY_PASS);
		
		CcpJsonRepresentation oldHash = oldValue.getInnerJson("hash");
		List<String> incomingHashes = VisAsyncUtils.getHashes(newValue, function).stream().map(x -> entityHash.getId(x)).collect(Collectors.toList());
		List<String> existentHashes = oldHash.getAsStringList("insert");

		List<String> hashesToRemoveIn = new CcpCollectionDecorator(existentHashes).getExclusiveList(incomingHashes);
		List<String> hashesToInsertIn = new CcpCollectionDecorator(incomingHashes).getExclusiveList(existentHashes);

		CcpJsonRepresentation dataToSave = newValue
		.put("lastUpdate", System.currentTimeMillis())
		.putSubKey("hash", "insert", hashesToInsertIn)
		.putSubKey("hash", "remove", hashesToRemoveIn)
		;
		entity.createOrUpdate(dataToSave);
		
		return hashesToInsertIn;
	}


	public static void saveHashes(PositionSendFrequency frequency, JnBaseEntity entity) {
		List<CcpJsonRepresentation> lastUpdatedes = VisAsyncUtils.getLastUpdated(entity, frequency);
		
		CcpJsonRepresentation allHashes = CcpConstants.EMPTY_JSON;
		
		VisEntityHashGrouper entityHash = new VisEntityHashGrouper();

		for (CcpJsonRepresentation lastUpdated : lastUpdatedes) {
			String entityId = entity.getId(lastUpdated);
			
			CcpJsonRepresentation jsonHash = lastUpdated.getInnerJson("hash");
			List<String> hashesToRemove = jsonHash.getAsStringList("remove");
			List<String> hashesToInsert = jsonHash.getAsStringList("insert");
			allHashes = putHash(allHashes, hashesToRemove, "remove", entityId);
			allHashes = putHash(allHashes, hashesToInsert, "insert", entityId);
		}
		
		Set<String> keySet = allHashes.keySet();
		String[] array = keySet.toArray(new String[keySet.size()]);
		List<CcpJsonRepresentation> manyByIds = entityHash.getManyByIds(array);
		List<CcpJsonRepresentation> hashesToCreate = new ArrayList<>();
		List<CcpJsonRepresentation> hashesToUpdate = new ArrayList<>();
		
		for (CcpJsonRepresentation item : manyByIds) {
			String hash = item.getAsString("_id");
			
			CcpJsonRepresentation hashJson = allHashes.getInnerJson(hash);
			
			List<String> hashes = item.getAsCollectionDecorator("hash").getSubCollection(0, 10_000_000).getExclusiveList(new ArrayList<String>());
			
			List<String> hashesToRemove = hashJson.getAsStringList("remove");
			
			List<String> hashesToInsert = hashJson.getAsStringList("insert");
			
			hashes.removeAll(hashesToRemove);

			hashes.addAll(hashesToInsert);
			
			CcpJsonRepresentation updatedHash = item.put("hash", hashes);
			
			boolean found = item.getAsBoolean("_found");
			
			if(found) {
				hashesToUpdate.add(updatedHash);
				continue;
			}
			hashesToCreate.add(updatedHash);
		}
		JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

		commitAndAudit.execute(hashesToCreate, CcpEntityOperationType.create, entityHash);

		commitAndAudit.execute(hashesToUpdate, CcpEntityOperationType.update, entityHash);
	}

	private static CcpJsonRepresentation putHash(CcpJsonRepresentation allHashes, List<String> hashes, String command, String entityId) {
		for (String hash : hashes) {
			CcpJsonRepresentation innerJson = allHashes.getInnerJson(hash);
			innerJson = innerJson.addToList(command, entityId);
			allHashes = allHashes.put(hash, innerJson);
		}
		return allHashes;
	}

	public static boolean matches(CcpJsonRepresentation position, CcpJsonRepresentation resume) {
		
		CcpJsonRepresentation positionHash = position.getInnerJson("hash");
		
		CcpJsonRepresentation resumeHash = resume.getInnerJson("hash");//{insert: ['a', 'b', 'c'], remove: ['d'] }
		List<String> resumeInsert = resumeHash.getAsStringList("insert");
		//a,b,c,d,e
		//b,c,d
		boolean matches = positionHash.itIsTrueThatTheFollowingFields("insert")
				.ifTheyAreAllArrayValuesThenEachOne().isTextAndItIsContainedAtTheList(resumeInsert);
		
		return matches;
	}

}
