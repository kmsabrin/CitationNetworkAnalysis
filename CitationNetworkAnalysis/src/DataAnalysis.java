import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class DataAnalysis {	
	CitationDAG citationDAG;
	Set<String> groundTruth;
	
	DataAnalysis() {
//		for the first time only
		citationDAG = new CitationDAG();
//		citationDAG.loadMetaData("titles.txt");
//		citationDAG.loadNetwork("citing_cited.csv");
		
		citationDAG.loadMetaData("patInfo.csv");
		citationDAG.loadNetwork("cite75_99.txt");
//		serializeCitationDAG(citationDAG);
		
//		for subsequent issues, load serialized object
//		deserializeAndLoadCitationDAG(); // fucked up!!!
		
//		groundTruth = new HashSet();
//		try {
//			String line = "";
//			Scanner scanner = new Scanner(new File("kauffman.txt"));	
//			while (scanner.hasNext()) {
//				line = scanner.nextLine();
//				String tokens[] = line.split("[\t]");
//				groundTruth.add(tokens[0].substring(1, tokens[0].length() - 1));
////				System.out.println(tokens[0].substring(1, tokens[0].length() - 1));
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void serializeCitationDAG(CitationDAG citationDAG) {
		try {
			FileOutputStream fileOut = new FileOutputStream("APS_Citation_DAG.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(citationDAG);
			out.close();
			fileOut.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deserializeAndLoadCitationDAG() {
		try {
			FileInputStream fileIn = new FileInputStream("APS_Citation_DAG.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			citationDAG = (CitationDAG) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double getCoCitationScore(String doiA, String doiB) {
		double sumCitations = 0;
		Set<String> mergedCitations = new HashSet();
		
		if (citationDAG.citations.get(doiA) != null) {
			sumCitations += citationDAG.citations.get(doiA).size(); 
			mergedCitations.addAll(citationDAG.citations.get(doiA));
		}
		
		if (citationDAG.citations.get(doiB) != null) {
			sumCitations += citationDAG.citations.get(doiB).size(); 		
			mergedCitations.addAll(citationDAG.citations.get(doiB));
		}
		
		double commonCitations = sumCitations - mergedCitations.size();
		
		return commonCitations / mergedCitations.size();
	}
	
	public double getBibligraphicCouplingScore(String doiA, String doiB) {
		double sumReferences = 0; 
		Set<String> mergedReferences = new HashSet();
		
		if (citationDAG.references.get(doiA) != null) {
			sumReferences += citationDAG.references.get(doiA).size();
			mergedReferences.addAll(citationDAG.references.get(doiA));
		}
		
		if (citationDAG.references.get(doiB) != null) {
			sumReferences += citationDAG.references.get(doiB).size();
			mergedReferences.addAll(citationDAG.references.get(doiB));	
		}
		
		double commonReferences = sumReferences - mergedReferences.size();
		
		return commonReferences / mergedReferences.size();
	}
	
//	pruned with co-citation and bibliographic scores
	public void getReferenceTree(String startingDoi) { 
		Set<String> referenceTree = new HashSet();
		Queue<String> q = new LinkedList();

		q.add(startingDoi);
		referenceTree.add(q.peek());
		while (!q.isEmpty()) {
			String doi = q.poll();
//			System.out.println(doi + " " + q.size());
			if (!citationDAG.references.containsKey(doi)) continue;
			for(String s: citationDAG.references.get(doi)) {
				if (!referenceTree.contains(s)) {
//					relevance filter
					double coCitationScore = getCoCitationScore(doi, s);
					double bibliographicCouplingScore = getBibligraphicCouplingScore(doi, s);
					
//					System.out.println("Cocitation Score between " + doi + " & " + s + " is " + coCitationScore);
					if (coCitationScore > 0.1 /*|| bibliographicCouplingScore > 0.2*/) {
						referenceTree.add(s);
						q.add(s);
					}
				}
			}
		}
		
		for (String s: referenceTree) {
			System.out.println(s + "  " + citationDAG.doiPaper.get(s).title);
		}
		
		System.out.println(referenceTree.size());
	}
	
//	given a set of papers how coherent/good this set is representing a single field of interest
	public void fieldScoreOfASet(Set<String> papers) { 
		int isValid = 0;		
		Set<String> notValid = new HashSet();
		
		for (String s: papers) {			
//			System.out.println("------------------------------------------------");
//			check validity with references used
			if (citationDAG.references.containsKey(s)) {
				int refCount = 0;
				for (String r : citationDAG.references.get(s)) {
					if (papers.contains(r))
						++refCount;
				}

//				System.out.println("Ref Count " + refCount + " " + references.get(s).size());
//				valid if refers at least half of the paper in the given set
				if (refCount * 2 >= citationDAG.references.get(s).size()) {
					++isValid;
					continue;
				}
			}
			
//			check validity with citations received
			if (citationDAG.citations.containsKey(s)) {
				int citCount = 0;
				for (String r : citationDAG.citations.get(s)) {
					if (papers.contains(r))
						++citCount;
				}

//				System.out.println("Cit Count " + citCount + " " + citations.get(s).size());
//				valid if cited by at least half of the paper in the given set
				if (citCount * 2 >= citationDAG.citations.get(s).size()) {
					++isValid;
					continue;
				}
			}
			
//			this paper is not supposed to be in this set
//			print the titles of references and citations to see why
			System.out.println(s + "\t" + citationDAG.doiPaper.get(s).title);
			if (citationDAG.references.containsKey(s)) {
				for (String r: citationDAG.references.get(s)) {
					System.out.println("References " + r + "\t" + citationDAG.doiPaper.get(r).title + "\t" + papers.contains(r));
				}
			}
			if (citationDAG.citations.containsKey(s)) {
				for (String r: citationDAG.citations.get(s)) {
					System.out.println("Citations " + r + "\t" + citationDAG.doiPaper.get(r).title + "\t" + papers.contains(r));
				}
			}
			notValid.add(s);
		}
		
//		validity score for the whole set
		System.out.println(isValid * 1.0 / papers.size());
	}
	
//	algorithm for extracting a field given some seeds
	public void fiedlLookUp(Set<String> seeds) {
//		print initial seeds
		System.out.println(seeds.size());
		for (String s : seeds) {
			System.out.println(s);
		}
		
		Set<String> addPaper = new HashSet();
		addPaper.addAll(seeds);
		
//		references and citation weights
		double rt = 0.4;
		double ct = 0.5;
		
		while (true) {		
//			neighbors are papers either referenced by or citing some paper added to the seed in a round
//			initially addPaper == seeds
//			note that seed keep growing and represents the whole field eventually (bad naming!)
			Set<String> neighbors = new HashSet();
			
			for (String s: addPaper) {
				if (citationDAG.citations.containsKey(s)) {
					for (String r : citationDAG.citations.get(s)) {
						if (!seeds.contains(r)) {
							neighbors.add(r);
						}
					}
				}
				if (citationDAG.references.containsKey(s)) {
					for (String r : citationDAG.references.get(s)) {
						if (!seeds.contains(r)) {
							neighbors.add(r);
						}
					}
				}
			}
			addPaper.clear();
			
//			check which papers from the neighbors can be added to the seeds
			for (String s: neighbors) {
				double csp = 0, rsp = 0, cps = 0, rps = 0;
				
				double seedCitationCount = 0;
				double seedReferenceCount = 0;
				
				if (citationDAG.citations.containsKey(s)) {
					for (String r : citationDAG.citations.get(s)) {
						if (seeds.contains(r)) {
							++seedCitationCount;
						}
					}
				}

				if (citationDAG.references.containsKey(s)) {
					for (String r : citationDAG.references.get(s)) {
						if (seeds.contains(r)) {
							++seedReferenceCount;
						}
					}
				}
				
//				csp : amount of seeds citing some paper 's'
//				rsp : amount of seeds referred by some paper 's'
//				cps : amount of paper s's citations within seeds
//				rps : amount of paper s's references within seeds
//				as the seeds get larger and larger, we shift to validity check via cps and rps from csp and rsp
				
				csp = seedCitationCount / seeds.size();
				rsp = seedReferenceCount / seeds.size(); 
				
				if (citationDAG.citations.containsKey(s))
					cps = seedCitationCount / citationDAG.citations.get(s).size();
				if (citationDAG.references.containsKey(s))
					rps = seedReferenceCount / citationDAG.references.get(s).size(); 
				
				if (Math.max(csp, cps) >= ct) addPaper.add(s);
				if (Math.max(rsp, rps) >= rt) addPaper.add(s);
			}
			
//			break condition when no new paper is added
			if (addPaper.size() <= 0) break;
			
//			update with the new set of seeds
			seeds.addAll(addPaper);
		}
		
//		print extracted field papers	
		System.out.println("Extracted Field Size: " + seeds.size());
		int c = 0, k = 0;
		for (String s: seeds) {
//			System.out.println(s);
			if (citationDAG.doiPaper.get(s).category.equals("452")) ++c;
			else ++k;
		}
		System.out.println("Positive " + c + "Negative " + k);
		
//		System.out.println("False Negatives: ");
//		int fn = 0;
//		for (String s: groundTruth) {
//			if (!seeds.contains(s)) {
//				++fn;
//				System.out.println(s + " " + citationDAG.doiPaper.get(s).title);
//			}
//		}
//		System.out.println("False Positives: ");
//		int fp = 0;
//		for (String s: seeds) {
//			if (!groundTruth.contains(s)) {
//				++fp;
//				System.out.println(s + " " + citationDAG.doiPaper.get(s).title);
//			}
//		}
//		System.out.println("Truth Size: " + groundTruth.size() + " Extracted Field Size: " + seeds.size());
//		System.out.println("FN: " + fn + " FP: " + fp);
	}
	
	public static void main(String[] args) {
		DataAnalysis dataAnalysis = new DataAnalysis();
		
//		dataAnalysis.fieldScoreOfASet(dataAnalysis.groundTruth); 
		
		Set<String> seeds = new HashSet();
		
//		for citation network
//		for (String s: papers) {
//			if (dataAnalysis.citations.containsKey(s)) {
//				int knt = dataAnalysis.citations.get(s).size();
//				System.out.println(knt + "\t" + dataAnalysis.doiTitle.get(s));
//			}
//		}
//		seeds.add("10.1103/PhysRevLett.93.048701");
//		seeds.add("10.1103/PhysRevE.65.016129");
//		seeds.add("10.1103/PhysRevLett.90.098701");
//		seeds.add("10.1103/PhysRevLett.90.068702");
		
		
//		for patent network
//		category 452
		seeds.add("3070832");
		seeds.add("3071801");
		seeds.add("3084379");
		seeds.add("3111705");
		
		dataAnalysis.fiedlLookUp(seeds);
	}
}
