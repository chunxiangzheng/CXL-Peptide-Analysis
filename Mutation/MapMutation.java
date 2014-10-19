/**
 * 
 * @author Bruce Lab
 * check known mutation for cross-linked sites
 *
 */
import java.util.*;
import java.io.*;
public class MapMutation {
	public static void main(String[] args) {
		
		
	}
	public static void mapMutationNearXLinkK(String in, String out, Map<String, ArrayList<Integer>> mutMap, int range) {
		Map<String, Set<Integer>> labeledKMap = new HashMap<String, Set<Integer>>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				String proA = arr[2];
				String proB = arr[11];
				int siteA = Integer.valueOf(arr[1]) + Integer.valueOf(arr[5]) + 1;
				int siteB = Integer.valueOf(arr[10]) + Integer.valueOf(arr[14]) + 1;
				if (labeledKMap.containsKey(proA)) {
					labeledKMap.get(proA).add(siteA);
				} else {
					Set<Integer> siteSet = new HashSet<Integer>();
					siteSet.add(siteA);
					labeledKMap.put(proA, siteSet);
				}
				if (labeledKMap.containsKey(proB)) {
					labeledKMap.get(proB).add(siteB);
				} else {
					Set<Integer> siteSet = new HashSet<Integer>();
					siteSet.add(siteB);
					labeledKMap.put(proB, siteSet);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		Set<String> proSet = labeledKMap.keySet();
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			for (String s : proSet) {
				int counter = 0;
				int matchCounter = 0;
				Set<Integer> siteSet = labeledKMap.get(s);
				Set<Integer> mutSet = new HashSet<Integer>();
				if (!mutMap.containsKey(s)) {
					ps.println(s + "\t" + counter + "\t" + matchCounter);
					continue;
				}
				ArrayList<Integer> mut_al = mutMap.get(s);
				for (Integer i : mut_al) mutSet.add(i);
				for (Integer site : siteSet) {
					counter++;
					for (int j = site - range; j <= site + range; j++) {
						if (mutSet.contains(j)) {
							matchCounter++;
							break;
						}
					}
				}
				ps.println(s + "\t" + counter + "\t" + matchCounter);
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static Map<String, ArrayList<Integer>> buildMutMap(String in) {
		Map<String, ArrayList<Integer>> mutMap = new HashMap<String, ArrayList<Integer>>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				String pname = arr[0];
				int len = arr[3].length();
				int resNum = Integer.valueOf(arr[3].substring(1, len - 1));
				if (mutMap.containsKey(pname)) {
					mutMap.get(pname).add(resNum);
				} else {
					ArrayList<Integer> al = new ArrayList<Integer>();
					al.add(resNum);
					mutMap.put(pname, al);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return mutMap;
	}
	public static void checkSeq(String fasta, String mappingtable, String mut, String out) {
		Map<String, String> seqMap = new HashMap<String, String>();
		Map<String, ArrayList<String>> idMap = new HashMap<String, ArrayList<String>>();
		try {
			FileReader fr = new FileReader(fasta);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String seq = "";
			String pname = "";
			while (line != null) {
				if (line.startsWith(">")) {
					if (!seq.equals("")) {
						seqMap.put(pname, seq);
					}
					pname = line.substring(4, 10);
					//System.out.println(pname);
					seq = "";
				} else {
					seq += line;
				}
				line = br.readLine();
			}
			seqMap.put(pname, seq);
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			FileReader fr = new FileReader(mappingtable);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("From")) {
					line = br.readLine();
					continue;
				}
				String[] arr = line.split("\t");
				String key = arr[0];
				String value = arr[1];
				if (idMap.containsKey(key)) {
					idMap.get(key).add(value);
				} else {
					ArrayList<String> al = new ArrayList<String>();
					al.add(value);
					idMap.put(key, al);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			FileReader fr = new FileReader(mut);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("gene")) {
					line = br.readLine();
					continue;
				}
				String[] arr = line.split("\t");
				String gene = arr[0];
				char res = arr[3].charAt(0);
				int resNum = Integer.valueOf(arr[3].substring(1, arr[3].length() - 1));
				ArrayList<String> al = idMap.get(gene);
				for (String s : al) {
					if (resNum > seqMap.get(s).length()) continue;
					if (seqMap.get(s).charAt(resNum - 1) == res) {
						ps.println(s + "\t" + arr[1] + "\t" + arr[2] + "\t" + arr[3] + "\t" + arr[4]);
					}
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
	public static void getGeneList(String in, String out) {
		Set<String> geneSet = new HashSet<String>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("gene")) {
					line = br.readLine();
					continue;
				}
				String[] arr = line.split("\t");
				if (!geneSet.contains(arr[0])) {
					ps.println(arr[0]);
					geneSet.add(arr[0]);
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
}
