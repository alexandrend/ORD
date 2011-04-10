import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class LuceneCDA {
	
	public static final String FILES_TO_INDEX_DIRECTORY = "/Users/alexandre/Dropbox/Documentos/workspace/ORd/CDA";
	public static final String INDEX_FILE = "/Users/alexandre/Dropbox/Documentos/workspace/ORd/indice";

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";
	
	public static void main( String args[] ) throws Exception{
	
		createIndex();
		String query;
		Scanner s = new Scanner(System.in);
		
		while( !( query=s.nextLine()).equalsIgnoreCase("Sair")) {
			
			searchIndex(query);
		} 
	
	}
	
	
	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
		
		File indice = new File(INDEX_FILE);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_31,analyzer);
		conf.setOpenMode(OpenMode.CREATE);
		
		IndexWriter indexWriter = new IndexWriter(new SimpleFSDirectory(indice), conf );  
		
		File dir = new File(FILES_TO_INDEX_DIRECTORY);
		File[] files = dir.listFiles();
		for (File file : files) {
			Document document = new Document();
			
			String path = file.getCanonicalPath();
			document.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));

			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_CONTENTS, reader));

			indexWriter.addDocument(document);
		}
		indexWriter.optimize();
		indexWriter.close();
	}
	
	public static void searchIndex(String searchString) throws IOException, ParseException {
		System.out.println("Procurando por '" + searchString + "'");
		
		IndexReader indexReader = IndexReader.open(new SimpleFSDirectory(new File(INDEX_FILE)));
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
		QueryParser queryParser = new QueryParser(Version.LUCENE_31,FIELD_CONTENTS, analyzer);
		Query query = queryParser.parse(searchString);
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
        indexSearcher.search(query, collector);
				
        System.out.println("Nœmero de documentos =" + collector.getTotalHits());
        
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        for (int i = 0; i < hits.length; i++) {
        	Document hitDoc = indexSearcher.doc(hits[i].doc);
        	String values[] = hitDoc.getValues("path");
        	System.out.println( "Arquivo: " + values[0]);
        	
        }

      indexSearcher.close();
      
	}
	
	
	
}