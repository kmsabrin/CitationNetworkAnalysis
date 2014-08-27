import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class RegexTest {
	
	public static void main(String[] args) {
		
//		String myString = "my    name is cat.";
//		
//		String[] stringArray = myString.split("\\s+");
//		
//		for (String s: stringArray) {
//			System.out.println(s);
//		}
		
		Set<String> papers = new HashSet();
		papers.add("10.1103/PhysRevA.44.6399");
		papers.add("10.1103/PhysRevE.63.036204");
		papers.add("10.1103/PhysRevE.65.016129");
		papers.add("10.1103/PhysRevE.72.061901");
		papers.add("10.1103/PhysRevE.75.051108");
		papers.add("10.1103/PhysRevE.77.036119");
		papers.add("10.1103/PhysRevLett.90.098701");
		papers.add("10.1103/PhysRevLett.94.218702");
		papers.add("10.1103/PhysRevLett.94.088701");
		papers.add("10.1103/PhysRevLett.95.048701");

		papers.add("10.1103/PhysRevA.42.6210");
		papers.add("10.1103/PhysRevE.47.2962");
		papers.add("10.1103/PhysRevE.63.051913");
		papers.add("10.1103/PhysRevE.65.046128");
		papers.add("10.1103/PhysRevE.66.015103");
		papers.add("10.1103/PhysRevE.69.056214");
		papers.add("10.1103/PhysRevE.71.026232");
		papers.add("10.1103/PhysRevE.71.056116");
		papers.add("10.1103/PhysRevE.72.016110");
		papers.add("10.1103/PhysRevE.72.026137");
		papers.add("10.1103/PhysRevE.72.046112");
		papers.add("10.1103/PhysRevE.72.046124");
		papers.add("10.1103/PhysRevE.72.055101");
		papers.add("10.1103/PhysRevE.73.026118");
		papers.add("10.1103/PhysRevE.74.046101");
		papers.add("10.1103/PhysRevE.74.046104");
		papers.add("10.1103/PhysRevE.74.041910");
		papers.add("10.1103/PhysRevE.75.051907");
		papers.add("10.1103/PhysRevE.76.036115");
		papers.add("10.1103/PhysRevE.76.046122");
		papers.add("10.1103/PhysRevE.79.036108");
		papers.add("10.1103/PhysRevE.80.026102");
		papers.add("10.1103/PhysRevE.80.026122");
		papers.add("10.1103/PhysRevE.80.056102");
		papers.add("10.1103/PhysRevLett.77.1644");
		papers.add("10.1103/PhysRevLett.84.5660");
		papers.add("10.1103/PhysRevLett.90.068702");
		papers.add("10.1103/PhysRevLett.93.048701");
		papers.add("10.1103/PhysRevLett.96.018101");
		papers.add("10.1103/PhysRevLett.98.158701");
		papers.add("10.1103/PhysRevLett.101.218702");

		papers.add("10.1103/PhysRevE.79.061908");
		papers.add("10.1103/PhysRevE.78.066118");
		papers.add("10.1103/PhysRevE.77.011901");
		papers.add("10.1103/PhysRevLett.93.038101");
		papers.add("10.1103/PhysRevE.65.016129");

		
		try {
			Scanner scanner = new Scanner(new File("titles_2.txt"));
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String stringArray[] = line.split("[\t]");
				
				if (stringArray.length < 2) {
//					System.out.println(line);
					continue;
				}
				
				String s = stringArray[1].toLowerCase();
				if(s.contains("bool") && s.contains("network")) {
					if (!papers.contains(stringArray[0].substring(1, stringArray[0].length() - 1))) {
						System.out.println(line);	
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
