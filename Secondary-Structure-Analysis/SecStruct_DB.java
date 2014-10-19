//import acm.util.*;
//import java.lang.*;

public class SecStruct_DB {
	public char[] structure_feature;
	public String proteinAccession;
	public int proteinLength;
	
	public SecStruct_DB(){
		//this.proteinLength = a;
		this.structure_feature = new char[2000];
		for (int i=0; i<2000; i++) {
			this.structure_feature[i] = 'U';
		}
		
	}

}
