/**
 * 
 * @author Bruce Lab
 *
 */
import java.io.*;
//import java.util.*;

public class DisorderScript {
	public static void main(String[] args) {
		breakFasta("2013080860WLIC52BD.fasta");
	}
	public static void breakFasta(String in) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String fileName = "";
			String sequence = "";
			String line = br.readLine();
			while (line != null) {
				if (line.charAt(0) == '>') {
					if (!sequence.equals("")) {
						FileOutputStream fout = new FileOutputStream("flat/" + fileName);
						PrintStream ps = new PrintStream(fout);
						ps.println(sequence);
						ps.close();
						fout.close();
					}
					sequence = "";
					fileName = line.split("\\|")[1];
				} else {
					sequence += line;
				}
				line = br.readLine();
			}
			FileOutputStream fout = new FileOutputStream("flat/" + fileName);
			PrintStream ps = new PrintStream(fout);
			ps.println(sequence);
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
