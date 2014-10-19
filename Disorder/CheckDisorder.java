/**
 * 
 * @author Bruce Lab
 *
 */
import java.io.*;
import java.util.*;

public class CheckDisorder {
	public static void main(String[] args) {
		//checkDisorder("ControlPairs.txt", "ControlDisorder");
		//System.out.println(getAvgDisorder("predict/P11940.pred", 157, 7));
		
	}
	public static void checkDisorder(String in, String out) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				String predA = "predict/" + arr[0] + ".pred";
				String predB = "predict/" + arr[2] + ".pred";
				int resA = Integer.valueOf(arr[1].trim());
				int resB = Integer.valueOf(arr[3].trim());
				ps.println(arr[0] + "\t" + arr[1] + "\t" + getAvgDisorder(predA, resA, 7) + "\t" 
				+ arr[2] + "\t" + arr[3] + "\t" + getAvgDisorder(predB, resB, 7));
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
	public static double getAvgDisorder(String pred, int residue, int window) {
		double avgDisorder = -1;
		int lb = residue - window;
		if (lb < 0) lb = 0;
		int ub = residue + window;
		ArrayList<Double> disorder = new ArrayList<Double>();
		try {
			FileReader fr = new FileReader(pred);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 4) {
					line = br.readLine();
					continue;
				}
				int n = Integer.valueOf(arr[0].trim());
				if (n <= ub && n >= lb) {
					disorder.add(Double.valueOf(arr[2]));
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		double counter = 0;
		double sum = 0;
		for (double d : disorder) {
			counter++;
			sum += d;
		}
		avgDisorder = sum / counter;
		return avgDisorder;
	}
}
