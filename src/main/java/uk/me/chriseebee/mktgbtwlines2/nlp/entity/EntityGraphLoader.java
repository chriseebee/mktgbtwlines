package uk.me.chriseebee.mktgbtwlines2.nlp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tinkerpop.blueprints.Vertex;

import uk.me.chriseebee.mktgbtwlines2.AvailabilityException;
import uk.me.chriseebee.mktgbtwlines2.Utils;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.db.OrientClient;
import uk.me.chriseebee.mktgbtwlines2.db.StorageException;

public class EntityGraphLoader {
	
	private static String ENTITIES = "/entity-hierarchy.txt";
	
	private List<String> entityList = new ArrayList<String>();
	
	private OrientClient oc = null;

	public EntityGraphLoader() throws Exception {
		try {
			oc = new OrientClient();
		} catch (ConfigurationException | AvailabilityException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public void loadEntityHierarchyFromFile(boolean testMode) throws StorageException {
		
		Utils.getLines(ENTITIES, entityList );
		
		for (String line: entityList) {
			if (testMode) {
				System.out.println("- ");
			}
			
			Iterable<String> tokenIter = Arrays.asList(line.split("|"));
			Vertex prevVertex = null;
			for (String token : tokenIter) {
				if (testMode) {
					System.out.println("- "+token);
				} else {
					Vertex v1 = null;
					try {
						v1 = oc.putVertex("Product",token.trim());
					} catch (StorageException e) {
						e.printStackTrace();
						throw e;
					}
					if (prevVertex != null) {
						oc.putEdge(v1,prevVertex,"Child",null,null);
					}
					prevVertex = v1;
				}
			}
		}

	}
	
	public static void main(String[] args) {
		EntityGraphLoader egl;
		try {
			egl = new EntityGraphLoader();
			
			egl.loadEntityHierarchyFromFile(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
