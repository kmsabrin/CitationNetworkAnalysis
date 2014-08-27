import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CitationDAG {
	Map<String, Paper> doiPaper;
	Map<String, List<String>> citations;
	Map<String, List<String>> references;
	
	public CitationDAG() {
		doiPaper = new HashMap();
		citations = new HashMap();
		references = new HashMap();
	}
	
	public void loadMetaData(String fileName) {
		System.out.println("Loading Metadata");
		String line = "";
		
		try {
			Scanner scanner = new Scanner(new File(fileName));
			
//			for citation network
			/*
			while (scanner.hasNext()) {
				line = scanner.nextLine();
				String tokens[] = line.split("[\t]"); 
				if (tokens.length < 5) {
					System.out.println(line);
					continue;
				}
				
				Paper paper = new Paper();
				paper.doi = tokens[0].substring(1, tokens[0].length() - 1);
				paper.title = tokens[1].substring(1, tokens[1].length() - 1);
				paper.journal = tokens[2].substring(1, tokens[2].length() - 1);
				paper.authors = tokens[3].substring(1, tokens[3].length() - 1);
				paper.date = tokens[4].substring(1, tokens[4].length() - 1);
				doiPaper.put(paper.doi, paper);
			}
			*/
			
//			for patent network
			while (scanner.hasNext()) {
				line = scanner.nextLine();
				String tokens[] = line.split("[,]"); 
				if (tokens.length < 5) {
					System.out.println(line);
					continue;
				}
				
				Paper paper = new Paper();
				paper.doi = tokens[0];
				paper.date = tokens[1];
				paper.category = tokens[4];
				doiPaper.put(paper.doi, paper);
			}
			
			scanner.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(line);
		}
	}
	
	public void loadNetwork(String fileName) {
		System.out.println("Loading Network");
		String line = "";	
		try {
			Scanner scanner = new Scanner(new File(fileName));
			scanner.nextLine();			
			while (scanner.hasNext()) {
				line = scanner.nextLine();
//				String tokens[] = line.split("[,\\r\\n\\s]");
//				String paperDoi = tokens[0];
//				String referenceDoi = tokens[1];
				
				int i = line.indexOf(',');
				String paperDoi = line.substring(0, i).intern();
				String referenceDoi = line.substring(i, line.length()).intern();
			
				if (references.containsKey(paperDoi)) {
					references.get(paperDoi).add(referenceDoi);
				}
				else {
					List<String> refs = new ArrayList();
					refs.add(referenceDoi);
					references.put(paperDoi, refs);
				}
				
				if (citations.containsKey(referenceDoi)) {
					citations.get(referenceDoi).add(paperDoi);
				}
				else {
					List<String> cits = new ArrayList();
					cits.add(paperDoi);
					citations.put(referenceDoi, cits);
				}
			}			
			scanner.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(line);
		}
	}
	
//	public static void main(String[] args) {
//		CitationDAG citationDAG = new CitationDAG();
//		citationDAG.loadMetaData("titles.txt");
//		citationDAG.loadNetwork("citing_cited.csv");
//	}
}
