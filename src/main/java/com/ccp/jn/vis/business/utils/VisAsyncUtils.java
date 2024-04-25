package com.ccp.jn.vis.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.jn.vis.business.utils.hash.GetHashFromJson;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.vis.commons.entities.VisEntityHashGrouper;
import com.jn.vis.commons.entities.VisEntityPosition;

public class VisAsyncUtils {

	public static List<CcpJsonRepresentation> getHashes(CcpJsonRepresentation json, Function<CcpJsonRepresentation, CcpJsonRepresentation> function) {
		// Os ddds entram como string e são convertidos para integer. Vagas e currículos podem anotar mais que um ddd por vez, por isso vem como List.
		List<Integer> ddds = json.getAsStringList("ddd").stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());
		// O resumWord se trata das habilidades se este JSON se tratar de currículo.
		// O mandatorySkills trata das habilidades se este JSON se tratar de vaga.
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");
		
		GetHashFromJson hashFromJson = GetHashFromJson.getHashFromJson(json);
		
		List<Integer> disponibilities = json.get(hashFromJson.getDisponibilityValuesFromJson);

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(hashFromJson, json);
		
		List<String> seniorities = json.get(hashFromJson.getSenioritiesValuesFromJson);

		List<Boolean> pcds = json.get(hashFromJson.getPcdValuesFromJson);
		
		List<CcpJsonRepresentation> hashes = new ArrayList<>();

		String email = json.getAsString("email");
		// Todas as futuras possibilidades são gravadas em uma Lista
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {// 5 (vaga) = [5, 4, 3, 2, 1, 0] || 6 (candidato) [6, 7, 8, 9
				for (String seniority : seniorities) {// vaga = [PL, SR] || candidato = 2 anos [JR]
					for (CcpJsonRepresentation moneyValue : moneyValues) {
						for (String resumeWord : resumeWords) {
							for (Integer ddd : ddds) {
								CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON
										.put("disponibility", disponibility)
										.put("resumeWord", resumeWord)
										.put("seniority", seniority)
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

		return hashes;
	}
	
	private static List<CcpJsonRepresentation> getMoneyValues(GetHashFromJson hashFromJson, CcpJsonRepresentation json){
		
		ArrayList<CcpJsonRepresentation> result = new ArrayList<>();
		
		List<CcpJsonRepresentation> btcValues = hashFromJson.getBtcValuesFromJson.apply(json);
		List<CcpJsonRepresentation> cltValues = hashFromJson.getCltValuesFromJson.apply(json);
		List<CcpJsonRepresentation> pjValues = hashFromJson.getPjValuesFromJson.apply(json);

		result.addAll(btcValues);
		result.addAll(cltValues);
		result.addAll(pjValues);
		
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

	public static List<CcpJsonRepresentation> getPositionsBySchedullingFrequency(PositionSendFrequency frequency) {
		// Injetando dependência do executor de query complexa
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		// Linha abaixo se refere a construção de uma query para filtrar vagas pela frequência
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.startSimplifiedQuery()
						.match(VisEntityPosition.Fields.frequency, frequency)
					.endSimplifiedQueryAndBackToRequest()
				;
		// Escolhendo as tabelas para fazer a busca (from)
		String[] resourcesNames = new String[] {new VisEntityPosition().getEntityName()};
		// Trazendo a lista de resultados para a memória
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
		CcpJsonRepresentation oldValue = entity.getOneById(newValue, CcpConstants.DO_BY_PASS);
		List<String> newHashes = VisAsyncUtils.getHashes(oldValue, function).stream().map(x -> new VisEntityHashGrouper().getId(x)).collect(Collectors.toList());
		
		CcpJsonRepresentation dataToSave = newValue
		.put("lastUpdate", System.currentTimeMillis())
		;
		entity.createOrUpdate(dataToSave);
		
		return null;
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
