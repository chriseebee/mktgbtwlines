package uk.me.chriseebee.mktgbtwlines2.nlp.entity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;

import uk.me.chriseebee.mktgbtwlines2.AvailabilityException;
import uk.me.chriseebee.mktgbtwlines2.Utils;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.db.OrientClient;
import uk.me.chriseebee.mktgbtwlines2.db.StorageException;


public class BrandLoader {

	Logger logger = LoggerFactory.getLogger(BrandLoader.class);
	private static String BRANDS = "./ner_brands_full_name.txt";
	
	private List<String> brandList = new ArrayList<String>();
	
	private OrientClient oc = null;

	public BrandLoader() throws Exception {
		try {
			oc = new OrientClient();
		} catch (ConfigurationException | AvailabilityException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void loadEntityHierarchyFromFile(boolean testMode) throws StorageException {
		
		Utils.getLines(BRANDS, brandList );
		
		for (String line: brandList) {

			if (testMode) {
				logger.debug("- "+line);
			} else {
				Vertex v1 = null;
				try {
					v1 = oc.putVertex("Brand",line.trim(),false);
					if (v1!=null) {
						logger.debug(" -- Vertex ID = "+v1.getId());
						
						
						// now we need to call Watson NLU and get the category for that brand so we can join them up.
						
					} else {
						logger.debug(" Vertex is null");
					}
					
				} catch (StorageException e) {
					e.printStackTrace();
					throw e;
				}

			}
			
		}
	}
	
	public static void main(String[] args) {
		EntityGraphLoader egl;
		try {
			egl = new EntityGraphLoader();
			
			if (args.length == 1 && args[0].equals("TEST")) {
				egl.loadEntityHierarchyFromFile(true);
			} else {
				egl.loadEntityHierarchyFromFile(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

