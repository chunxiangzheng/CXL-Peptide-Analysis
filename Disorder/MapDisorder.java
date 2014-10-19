/**
 * map disorder with cross-linking data
 * @author Bruce Lab
 *
 */
import java.io.*;
import java.util.*;

import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

public class MapDisorder {
	public static void main(String[] args) {
	}
	
	public static void checkSimResults(String disorderDir, String out) {
		File fdir = new File(disorderDir);
		File[] flist = fdir.listFiles();
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			for (File f : flist) {
				ps.println(calculateDisorderAverage(f.getAbsolutePath()));
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static double calculateDisorderAverage(String in) {
		double avg = 0;
		double sum = 0;
		double n = 0;
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				sum += Math.abs(Double.valueOf(arr[0]) - Double.valueOf(arr[1]));
				n++;
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		avg = sum / n;
		return avg;
	}
	public static void simDisorderPair(int repeat, String disorderdir, String lysinepool, String outdir, int numPair, int flag) {//flag 0 = both, 1=hetero 2= homo
		ArrayList<String> al_lysine = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(lysinepool);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				al_lysine.add(line);
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		int size = al_lysine.size();
		for (int i = 0; i < repeat; i++) {
			try {
				FileOutputStream fout = new FileOutputStream(outdir + "/" + i);
				PrintStream ps = new PrintStream(fout);
				for (int j = 0; j < numPair; j++) {
					int indexA = (int) (Math.random() * size);
					int indexB = (int) (Math.random() * size);
					String sA = al_lysine.get(indexA);
					String sB = al_lysine.get(indexB);
					String proA = sA.split("\t")[0];
					String proB = sB.split("\t")[0];
					if (flag == 1) {
						while (proA.equals(proB)) {
							indexB = (int) (Math.random() * size);
							sB = al_lysine.get(indexB);
							proB = sB.split("\t")[0];
						}
					} else {
						if (flag == 2) {
							while (!proA.equals(proB)) {
								indexB = (int) (Math.random() * size);
								sB = al_lysine.get(indexB);
								proB = sB.split("\t")[0];
							}
						}
					}
					int siteA = Integer.valueOf(sA.split("\t")[1]);
					int siteB = Integer.valueOf(sB.split("\t")[1]);
					double disorderA = getDisorder(disorderdir + "/" + proA + ".pred", siteA);
					double disorderB = getDisorder(disorderdir + "/" + proB + ".pred", siteB);
					ps.println(disorderA + "\t" + disorderB);
				}
				ps.close();
				fout.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	public static void printDisorder(String disorderdir, String in, String out, int flag) {// organize disorder by pairs.  flag 0 = both, 1 = hetero, 2 = homo
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				String proA = arr[2];
				String proB = arr[11];
				if (flag == 1) {
					if (proA.equals(proB)) {
						line = br.readLine();
						continue;
					}
				} else {
					if (flag == 2) {
						if (!proA.equals(proB)) {
							line = br.readLine();
							continue;
						}
					}
				}
				int siteA = Integer.valueOf(arr[1].trim()) + Integer.valueOf(arr[5].trim()) + 1;
				int siteB = Integer.valueOf(arr[10].trim()) + Integer.valueOf(arr[14].trim()) + 1;
				double disorderA = getDisorder(disorderdir + "/" + proA + ".pred", siteA);
				double disorderB = getDisorder(disorderdir + "/" + proB + ".pred", siteB);
				ps.println(disorderA + "\t" + disorderB);
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			
		}
	}
	public static double getDisorder(String disorderIn, int site) {
		double disorder = 0;
		try {
			FileReader fr = new FileReader(disorderIn);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length != 4) {
					line = br.readLine();
					continue;
				}
				int resNum = Integer.valueOf(arr[0].trim());
				if (resNum == site) {
					disorder = Double.valueOf(arr[2]);
					break;
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return disorder;
	}
	public static void printDisorder(String disorderdir, Map<String, ArrayList<Integer>> siteMap, String out) {// pname\tnumK\disorderK\tcrosslinkK\tmatched
		Set<String> keySet = siteMap.keySet();
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			for (String s : keySet) {
				FileReader fr = new FileReader(disorderdir + "/" + s + ".pred");
				BufferedReader br = new BufferedReader(fr);
				ArrayList<Integer> al_site = siteMap.get(s);
				Collections.sort(al_site);
				int numK = 0;
				int numXLinkK = al_site.size();
				int numDisorderK = 0;
				int numMatched = 0;
				int pointer = 0;
				String line = br.readLine();
				while (line != null) {
					String[] arr = line.split("\t");
					if (arr.length != 4) {
						line = br.readLine();
						continue;
					}
					int resNum = Integer.valueOf(arr[0].trim());
					if (pointer < numXLinkK) {
						if (resNum == al_site.get(pointer)) {
							if (arr[3].equals("D")) {
								numMatched ++;
							}
							pointer++;
						}
					}
					if (arr[1].equals("K")) {
						numK++;
						if (arr[3].equals("D")) numDisorderK++;
					}
					
					line = br.readLine();
				}
				ps.println(s + "\t" + numK + "\t" + numDisorderK + "\t" + numXLinkK + "\t" + numMatched);
				br.close();
				fr.close();
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void writeBATFileAndProList(String in, String out1, String out2) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout1 = new FileOutputStream(out1);
			PrintStream ps1 = new PrintStream(fout1);
			FileOutputStream fout2 = new FileOutputStream(out2);
			PrintStream ps2 = new PrintStream(fout2);
			Set<String> proSet = new HashSet<String>();
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (!proSet.contains(arr[2])) {
					proSet.add(arr[2]);
					ps1.println(arr[2]);
					ps2.println("java -jar VSL2.jar -s:flat/" + arr[2] + " >predict/" + arr[2] + ".pred");
				}
				if (!proSet.contains(arr[11])) {
					proSet.add(arr[11]);
					ps1.println(arr[11]);
					ps2.println("java -jar VSL2.jar -s:flat/" + arr[11] + " >predict/" + arr[11] + ".pred");
				}
				line = br.readLine();
			}
			ps2.close();
			fout2.close();
			ps1.close();
			fout1.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void printGenome(String in, String out) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			Set<String> genomeSet = new HashSet<String>();
			String line = br.readLine();
			while (line != null) {				
				String[] arr = line.split("\t");
				if (arr.length < 2) {
					line = br.readLine();
					continue;
				}
				if (!genomeSet.contains(arr[0])) {
					ps.println(arr[0] + "\t" + arr[1]);
					genomeSet.add(arr[0]);
				}
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void printMap(String out, Map<String, String> idMap) {
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			Set<String> idSet = idMap.keySet();
			for (String s : idSet) {
				ps.println(s + "\t" + idMap.get(s));
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static Map<String, String> buildMappingTable(String in1, String in2) {
		Map<String, String> idMap = new HashMap<String, String>();
		try {
			FileReader fr = new FileReader(in1);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				System.out.println(line);
				String[] arr = line.split("\t");
				if (arr.length < 3) {
					line = br.readLine();
					continue;
				}
				String ipi = getIPINumber(arr[1]).equals("") ? arr[1] : getIPINumber(arr[1]);
				idMap.put(arr[2], ipi);
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			FileReader fr = new FileReader(in1);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 3) {
					line = br.readLine();
					continue;
				}
				String ipi = getIPINumber(arr[1]).equals("") ? arr[1] : getIPINumber(arr[1]);
				idMap.put(arr[2], ipi);
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return idMap;
	}
	public static String getIPINumber(String ipi) {
		String[] arr = ipi.split("\\|");
		if (arr.length < 4) {
			return "";
		}
		return arr[1];
	}
	public static void extractOrganName(String in, String out) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 4) {
					line = br.readLine();
					continue;
				}
				if (arr[3].indexOf('[') == -1) {
					line = br.readLine();
					continue;
				}
				if (arr[3].indexOf(']') == -1) {
					line = br.readLine();
					continue;
				}
				String organ = arr[3].substring(arr[3].indexOf('[') + 1, arr[3].indexOf(']'));
				ps.println(arr[0] + "\t" + organ);
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void printAllOrgan(String in1, String in2, String out) {
		Set<String> organSet = new HashSet<String>();
		try {
			FileReader fr = new FileReader(in1);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (!organSet.contains(arr[0])) {
					ps.println(line);
					organSet.add(arr[0]);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
			fr = new FileReader(in2);
			br = new BufferedReader(fr);
			line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (!organSet.contains(arr[0])) {
					ps.println(line);
					organSet.add(arr[0]);
				}
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void guessColumn(String in, String phrase) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 3) {
					line = br.readLine();
					continue;
				}
				if (arr[2].trim().equals(phrase)) {
					System.out.println(line);
					System.out.println(arr[1]);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void fileReader(String in, int lines, String out) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine(); 
			for (int i = 0; i < lines; i++) {
				ps.println(line);
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
