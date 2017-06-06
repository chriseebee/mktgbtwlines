package uk.me.chriseebee.mktgbtwlines2.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import uk.me.chriseebee.mktgbtwlines2.AvailabilityException;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

/* 
 * Setup
 * 
 * Vertices
 * 
 * 1. CREATE CLASS Person EXTENDS V
 * 2. CREATE CLASS Entity EXTENDS V
 * 
 * Edges
 * 
 * 1. CREATE CLASS Mention EXTENDS E
 * 2. CREATE CLASS Child EXTENDS E
 * 
 */
public class OrientClient {
	
	Logger logger = LoggerFactory.getLogger(OrientClient.class);
	OrientGraph graph = null;
	ODatabaseDocumentTx docDb = null;
	
	public OrientClient() throws ConfigurationException, AvailabilityException {
		
		ConfigLoader cl = null;
		String hostname = null;
		String username = null;
		String password = null;
		String database = null;
		
		try {
			cl = ConfigLoader.getConfigLoader();
			hostname = cl.getConfig().getOrientParams().get("hostname");
			username = cl.getConfig().getOrientParams().get("username");
			password = cl.getConfig().getOrientParams().get("password");
			database = cl.getConfig().getOrientParams().get("database");
			
		} catch( ConfigurationException ce) {
			logger.error("Failed to get Configuration for OrientDB",ce);
			throw ce;
		} 
		
		String connectionString = String.format("remote:%s/%s",hostname,database);
		
		OrientGraphFactory factory = new OrientGraphFactory(connectionString).setupPool(1,10);
		
		// EVERY TIME YOU NEED A GRAPH INSTANCE
		graph = factory.getTx();
		
		docDb = new ODatabaseDocumentTx(connectionString).open(username,password);

	}
	
	public OrientGraph getGraph() {
		return graph;
	}
	
	/*
	 * 		// CREATE A NEW DOCUMENT AND FILL IT
		
		doc.field( "name", "Luke" );
		doc.field( "surname", "Skywalker" );
		doc.field( "city", new ODocument("City")
		   .field("name","Rome")
		   .field("country", "Italy") );
	 */
	public ODocument createDocument(String className) {

		ODocument doc = new ODocument(className);
		return doc;
	}
	
	public void saveDocument(ODocument doc) {
		doc.save();
		//db.close();
	}
	
	 public void sendEventToDataStore(InterestingEvent ev) throws StorageException {
		 
		 // 1. Add or get the vertex for the Actor
		 // 2. Add or get the vertex for the Entity and one for the entity group if missing
		 // 3. Add an edge for the intent between actor and entity (add sentiment score to edge attribute)
		 Vertex v1 = putVertex("Person",ev.getActorName());
		 Vertex v2 = putVertex("Entity",ev.getEntity().getName());
		 Vertex v3 = putVertex("Entity",ev.getEntity().getType());
		 
		 putEdge(v1,v2,"Mention",ev.getIntent(),ev.getSentiment());
		 putEdge(v3,v2,"Child",null,null);
		 
		 // Log the raw event for more detailed analysis
		 ODocument doc = createDocument("InterestingEvent");
		 doc.field( "actorName", "UNKNOWN" );
		 doc.field( "entity", ev.getEntity().getName());
		 doc.field( "entityType", ev.getEntity().getType());
		 doc.field( "sentiment",ev.getSentiment());
		 doc.field( "intent", ev.getIntent());
		 doc.field( "timestamp",ev.getDateTime());
		
		 this.saveDocument(doc);
		 
	 }
	 
	 public Vertex putVertex(String vertexClassName, String vertexName) throws StorageException {
		// put a vertex in the graph
		 logger.debug("Finding vertices with name = "+vertexName);
		 Iterable<Vertex> iterable = graph.getVertices("name", vertexName);
		 Vertex v = null;
		 
		 int iterCount=0;
		 Iterator<Vertex> iter = iterable.iterator();
		 // iter should only ever be of size = 1
		 
		 while (iter.hasNext()) {
			iterCount++;
			v = (Vertex) iter.next();
		    try {
		    	int c0 = ((Integer)v.getProperty("counter")).intValue();
		    	logger.debug("Found a vertex, with existing counter : "+c0);
		    	c0++;
		    	logger.debug("Updating counter to : "+new Integer(c0).toString());
		    	v.setProperty( "counter",new Integer(c0));
				  graph.commit();
			} catch( Exception e ) {
				  graph.rollback();
				  logger.error("Error: "+ e.getMessage(),e);
				  throw new StorageException ("Could not update an existing graph node: "+vertexName);
			}
		 }

		if (iterCount==0) {
			// create the Vertex
			logger.debug(" - Creating a new vertex with name - "+vertexName);
		    try {
				  v = graph.addVertex("class:"+vertexClassName); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
				  v.setProperty( "name", vertexName );
				  v.setProperty( "counter", new Integer("1") );
				  graph.commit();
			} catch( Exception e ) {
				  graph.rollback();
			}
		}
		
		return v;
		    
	 }
	 
	 public void putEdge(Vertex outV, Vertex inV, String edgeClassName, String edgeName, Double sentiment) {
		 // when we put an edge in we need to iterate the usage counter if it 
		 // already exists, then put a timeout on the counter in a queue 
		 
		 Edge e = null;
		 Iterable<Edge> iterable = outV.getEdges(Direction.OUT,new String[0]);
		 Iterator<Edge> iter = iterable.iterator();
		 
		 boolean updated = false;
		 
		 // iter should only ever be of size = 1
		 while (iter.hasNext()) {
			e = (Edge) iter.next();
			if (e.getVertex(Direction.IN).equals(inV)) {
			    try {
			    	if (sentiment != null) {
					  e.setProperty( "sentiment", (new Double(e.getProperty("sentiment")).doubleValue())+sentiment);
			    	}
			    	  int c2 = new Integer(e.getProperty("counter")).intValue()+1;
					  e.setProperty( "counter", new Integer(c2));
					  graph.commit();
					  updated = true;
				} catch( Exception e2 ) {
					  graph.rollback();
				}
			}
		 }
		 
		if (!updated) {
		    try {
				  e = graph.addEdge("class:"+edgeClassName, outV, inV, edgeName);
				  if (sentiment != null) {
					  e.setProperty( "sentiment", sentiment);
				  }
				  e.setProperty( "counter",new Integer("1"));
				  graph.commit();
			} catch( Exception e3 ) {
				  graph.rollback();
			}
		}
	 }
	 
	 //TODO: Make this class specific
	 public void deleteVertex(String name) {
		 logger.debug("Finding vertices to delete with name = "+name);
		 Iterable<Vertex> iterable = graph.getVertices("name", name);
		 Vertex v = null;
		 
		 Iterator<Vertex> iter = iterable.iterator();
		 while (iter.hasNext()) {
			 logger.debug(" - Found a vertex to delete with name = "+name);
			v = (Vertex) iter.next();
		    try {
				  graph.removeVertex(v);
				  graph.commit();
				  logger.debug(" - Deleted a vertex to delete with name = "+name);
			} catch( Exception e ) {
				logger.error(" - Error deleting a vertex: "+ e.getMessage());
				  graph.rollback();
			}
		 }
		 
		
	 }

}
