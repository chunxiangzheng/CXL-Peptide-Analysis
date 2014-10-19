import acm.program.*;
import acm.util.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Secondary_Struct_Anal extends ConsoleProgram {
	//public String ln = "start";
	public SecStruct_DB[] db; //secondary structure database
	public int dblength = -1; // the number of protein entries in the db
	public void init() {
		gff_read();
		//file_write();
	}
	public void gff_read() {
		String ln = "start";  
		int currentdblength = -1; // the position of current db
		this.db = new SecStruct_DB[500]; // init the db
		try {
			FileReader fr = new FileReader("1.gff");
			BufferedReader br = new BufferedReader (fr);
			
			while (ln != null) {
				ln = br.readLine();
				//ps.println(ln);
				//StringTokenizer st = new StringTokenizer(ln, "\t");
				if (ln!= null){
					String [] st = ln.split("\t");
					//print(st.length);
					//println(st.length);
					switch (st.length) {
					case 1 :
						currentdblength ++;
						String [] st1 = ln.split(" ");
						//println(this.db);
						//println(this.db[currentdblength]);
						this.db[currentdblength] = new SecStruct_DB();
						//println (st1[1]);
						//println (st1[3]);
						this.db[currentdblength].proteinAccession = st1 [1];
						this.db[currentdblength].proteinLength = Integer.parseInt(st1 [3]);
						//println(this.db[currentdblength].structure_feature[0]);
						
						
						break;
					case 9 :
						//println(st[0]);
						//println(st[1]);
						//println(st[2]);
						//println(st[3]);
						if ((st[2].equals("Helix")) || (st[2].equals("Beta strand")) || (st[2].equals("Turn"))) {
							for (int i = Integer.parseInt(st [3]) - 1; i < Integer.parseInt(st[4]); i++){
									if (st[2]== "Helix") {
										this.db[currentdblength].structure_feature[i] = 'H';
									}	
									else {
										if (st[2] == "Beta strand") {
											this.db[currentdblength].structure_feature[i] = 'S';
										} else {
											this.db[currentdblength].structure_feature[i] = 'T';
										}
									}
							}
						}
						break;
					}
				}

				
			}
			this.dblength = currentdblength;
			println(this.dblength);
			//ps.print(this.db);
			fr.close();
			//fout.close();
		} catch (IOException ex){
			throw new ErrorException(ex); 
		}
		
	}
	
	public void file_write() {
		try {
			FileOutputStream fout = new FileOutputStream ("2.txt");
			PrintStream ps = new PrintStream(fout);
			for (int i = 0; i<= this.dblength; i++) {
				for (int j = 0; j<this.db[i].proteinLength; j++) {
					ps.print(db[i].proteinAccession + "\t"+ convertInteger(i) + "\t");
					ps.println(db[i].structure_feature[j]);
				}
			}
			fout.close();
		} catch (IOException ex) {
			throw new ErrorException(ex);
		}	

	}
	public static String convertInteger(int i) {
	    return Integer.toString(i);
	}
	public void saDB_read() {
		FileReader fr1 = new FileReader("");
	}
}
