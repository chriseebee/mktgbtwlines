package uk.me.chrismbell.text_classify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import uk.me.chrismbell.text_classify.nlu.google.GoogleNLUClient;
import uk.me.chrismbell.text_classify.nlu.razor.TextRazorClient;
import uk.me.chrismbell.text_classify.nlu.watson.WatsonClient;
import uk.me.chrismbell.text_classify.search.google.GoogleSearchClient;
import uk.me.chrismbell.text_classify.search.google.GoogleSearchResults;
import uk.me.chrismbell.text_classify.search.ms.BingSearchResultsEnvelope;
import uk.me.chrismbell.text_classify.search.ms.BingSearchResultsItem;
import uk.me.chrismbell.text_classify.search.ms.MicrosoftBingSearchClient;


/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class App 
{

	static Logger logger = LoggerFactory.getLogger(App.class);

	/* [[[start:clustering-document-list-intro]]]
	 * 
	 * <div>
	 * <p>
	 * The easiest way to get started with Carrot2 is to cluster a collection
	 * of {@link org.carrot2.core.Document}s. Each document can consist of:
	 * </p>
	 * 
	 * <ul>
	 * <li>document content: a query-in-context snippet, document abstract or full text,</li>
	 * <li>document title: optional, some clustering algorithms give more weight to document titles,</li>
	 * <li>document URL: optional, used by the {@link org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, 
	 * ignored by other algorithms.</li>
	 * </ul>
	 * 
	 * <p>
	 * To make the example short, the code shown below clusters only 5 documents. Use
	 * at least 20 to get reasonable clusters. If you have access to the query that generated
	 * the documents being clustered, you should also provide it to Carrot2 to get better clusters.
	 * </p>
	 * </div>
	 * 
	 * [[[end:clustering-document-list-intro]]]
	 */
	public void clusterDocuments(String searchText) throws ConfigurationException {

		// [[[start:clustering-document-list]]]
		/* A few example documents, normally you would need at least 20 for reasonable clusters. */



		//            /* A controller to manage the processing pipeline. */
		//            final Controller controller = ControllerFactory.createSimple();
		//
		//            /*
		//             * Perform clustering by topic using the Lingo algorithm. Lingo can 
		//             * take advantage of the original query, so we provide it along with the documents.
		//             */
		//           final ProcessingResult byTopicClusters = controller.process(documents, "data mining", BisectingKMeansClusteringAlgorithm.class);
		//           // final ProcessingResult byTopicClusters = controller.process(documents, "data mining", LingoClusteringAlgorithm.class);
		//           // final ProcessingResult byTopicClusters = controller.process(documents, "data mining", STCClusteringAlgorithm.class);
		//            
		//            final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
		//            
		//            /* Perform clustering by domain. In this case query is not useful, hence it is null. */
		//            final ProcessingResult byDomainClusters = controller.process(documents, null,
		//                ByUrlClusteringAlgorithm.class);
		//            final List<Cluster> clustersByDomain = byDomainClusters.getClusters();
		//            // [[[end:clustering-document-list]]]
		//            
		//            ConsoleFormatter.displayClusters(clustersByTopic);
		//            //ConsoleFormatter.displayClusters(clustersByDomain);
	}

	public void process(String searchText) throws SearchException, ConfigurationException {
		logger.info("------------------------------");
		logger.info("-- START");
		logger.info("------------------------------");

		List<String> list_Google = new ArrayList<String>();
		List<String> list_Bing = new ArrayList<String>();
		String finalUnstructedTextBlock_Google = "";
		String finalUnstructedTextBlock_Bing = "";
		String finalUnstructedTextBlock = "";

		if (ConfigLoader.getConfig().getRunOptions().get("execGoogleSearch").equals("Y")) {
			GoogleSearchClient sc = null;
			GoogleSearchResults sr = null;
			try {
				sc = new GoogleSearchClient();
				sr = sc.getResults(searchText);
			} catch (SearchException | ConfigurationException e) {
				e.printStackTrace();
				throw e;
			}
			
			finalUnstructedTextBlock_Google = sc.getTextFromSearchResuts(sr, searchText,list_Google);

		}

		if (ConfigLoader.getConfig().getRunOptions().get("execBingSearch").equals("Y")) {
			MicrosoftBingSearchClient client = null;
			BingSearchResultsEnvelope res = null;
			try {
				client = new MicrosoftBingSearchClient();
				res = client.getInfo(searchText);

			} catch (ConfigurationException e) {
				e.printStackTrace();
				throw e;
			} catch (SearchException e) {
				e.printStackTrace();
			}
			
			finalUnstructedTextBlock_Bing = client.getTextFromSearchResuts(res, searchText,list_Bing);
		}

		if (ConfigLoader.getConfig().getRunOptions().get("compareOrCombine").equals("compare")) {
			logger.info("------------------------------");
			logger.info("-- GOOGLE");
			logger.info("------------------------------");
			executeNLU(finalUnstructedTextBlock_Google,list_Google);

			logger.info("------------------------------");
			logger.info("-- BING");
			logger.info("------------------------------");
			executeNLU(finalUnstructedTextBlock_Bing,list_Bing);

		} else {
			logger.info("------------------------------");
			logger.info("-- GOOGLE & BING");
			logger.info("------------------------------");
			
			finalUnstructedTextBlock = finalUnstructedTextBlock_Google + ". "+ finalUnstructedTextBlock_Bing;
			
			list_Google.addAll(list_Bing);
			executeNLU(finalUnstructedTextBlock,list_Google);
		}

		/* Prepare Carrot2 documents */
		//final ArrayList<Document> documents = new ArrayList<Document>();
		logger.info("------------------------------");
		logger.info("-- DONE");
		logger.info("------------------------------");
	}

	private void executeNLU(String textToProcess, List<String> listToProcess) throws ConfigurationException {
		
		if (ConfigLoader.getConfig().getRunOptions().get("createNGram").equals("Y")) {
			NGramExtractor ex = new NGramExtractor();
			ex.testFromLines((Iterable<String>)listToProcess);
		}

		if (ConfigLoader.getConfig().getRunOptions().get("execWatsonNLU").equals("Y")) {
			WatsonClient wc = null;
			try {
				wc = new WatsonClient();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			AnalysisResults ar = wc.process(textToProcess);

			wc.printConfidentResults(ar);
		}

		if (ConfigLoader.getConfig().getRunOptions().get("execGoogleNLU").equals("Y")) {
			GoogleNLUClient gc = new GoogleNLUClient();
			try {
				gc.analyzeText(textToProcess);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (ConfigLoader.getConfig().getRunOptions().get("execRazorNLU").equals("Y")) {
			TextRazorClient trc = null;
			try {
				trc = new TextRazorClient();
				trc.analyzeText(textToProcess);
			} catch (ConfigurationException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}



	public static void main(String[] args)
	{
		App app = new App();
		//app.clusterDocuments("DGS/RPP DD");

		for (int i=0; i<args.length;i++) {
			long t1 = System.currentTimeMillis();
			try {
				app.process(args[i]);
			} catch (ConfigurationException | SearchException e) {
				e.printStackTrace();
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Exec Time = "+(t2-t1));
		}

	}
}


