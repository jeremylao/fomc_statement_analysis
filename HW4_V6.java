import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.math.*;

public class HW4_V6{

	public static class Functions{
		
		public Functions(){}
		
		public int[][] sum_row(int[][] array, int row_count, int col_count){
			int running_sum = 0;
			for(int i = 0; i< row_count; ++i){
				for(int j = 0; j< col_count; ++j){
					running_sum += array[i][j]; 
				}
			array[i][col_count] = running_sum;
			running_sum = 0;
			}
			return array;
		}
		
		public int[][] sum_col(int[][] array, int row_count, int col_count){
			int running_sum = 0;
			for(int i = 0; i< col_count; ++i){
				for(int j = 0; j< row_count; ++j){
					running_sum += array[j][i]; 
				}
			array[row_count][i] = running_sum;
			running_sum = 0;
			}
			
			return array;
		}
		
		public void print_array(int[][] array, int row, int col){
			for(int i = 0; i<row; ++i){
				for(int j = 0; j<col; ++j){ 
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
			}
		}
		
		public void print_array_boundary(int[][] array, int row, int col){
			for(int i = 0; i<=row; ++i){
				for(int j = 0; j<=col; ++j){ 
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
			}
		}
		
		public void print_vector(Vector vec){
			for(int i = 0; i<vec.size(); ++i){
				System.out.print(vec.elementAt(i)+" ");
			}
		}
	}
	
	public static class Viterbi{
		
		double[][] transition_probabilities; 
		double[][] observation_probabilities; 
		double[][] viterbi;
		int[][] backtrace;
		Vector<String> local_pos_vector;
	    int[] local_POS_vec_int;
		Functions func_v;
		
		public Viterbi(){
			transition_probabilities = new double[60][60];
			observation_probabilities = new double [60][200];
			viterbi = new double [60][200];
		  
			backtrace = new int [60][200];
			local_pos_vector = new Vector<String>(200);
			func_v = new Functions();
			local_POS_vec_int = new int[200];
		}
		
	    public int[] viterbi_algo(Vector sentence, Vector global_pos_vector){
			//step 1 of viterbi, initialize viterbi with starting position, remove 2 count from Part Of Speech Vector Size
			for(int s = 2; s<global_pos_vector.size()-2; ++s){
				viterbi[s-2][0] = transition_probabilities[1][s] * observation_probabilities[s][0];
				backtrace[s-2][0] = 0;
			}
			
			double temp;
			double current = -100000.0;
			//Recursion Step of Viterbi - State/Observations for outerloop
			//Transition probabilities for inner loop
			//Third inner loop to test the previous viterbi value
			for(int t = 1; t<sentence.size(); ++t){
				//inner loop to check all the parts of speech, the first two parts of speech are END and START, 
				//the loop needs to start at index 2 for row, but can check for transitions to ALL the parts of speech
				//this FOR LOOP iterates through the columns, the ARC TO STATES
			    current = -100000.0;
			    for(int s = 0; s<global_pos_vector.size()-2; ++s){
					
					//THIS INNERMOST FOR LOOP WILL ITERATE THROUGH THE STARTING POINT POS (ROWS)
					for(int s_prime = 0; s_prime < global_pos_vector.size() -2; ++s_prime){
						//IMPLEMENTING LOGS!!!
						temp = viterbi[s_prime][t-1] *transition_probabilities[s_prime+2][s+2]* observation_probabilities[s+2][t];
						if(temp > current){
							viterbi[s][t] = temp;
							current = temp;
						}
					}
				}
			}
			
			for(int s = 2; s<global_pos_vector.size()-2; ++s){
				viterbi[s-2][sentence.size()] = transition_probabilities[0][s] * viterbi[s-2][sentence.size()-1];
			}
			
			BigDecimal bd = new BigDecimal(1.0);
			BigDecimal temp_bd = new BigDecimal(-2.0);
			BigDecimal current_bd = new BigDecimal(-1000000.0);
			BigDecimal current_const = new BigDecimal(-100000.0);
			
			/*for(int i = 0; i<global_pos_vector.size()-2; ++i){
				for(int j = 0; j<sentence.size(); ++j){
					System.out.print(viterbi[i][j]+" ");
				}
				System.out.println();
			}*/
			
			for(int s = 0; s<sentence.size(); ++s){
				for(int t = 0; t<global_pos_vector.size(); ++t){					
					temp_bd = bd.valueOf(viterbi[t][s]);
					if( temp_bd.compareTo(current_bd) > 0){
						local_POS_vec_int[s+1]=t;
						current_bd = temp_bd;
					}
				}
				current_bd = current_const;
			}

			/*for(int s=0; s<=sentence.size(); ++s){
			    System.out.print( local_POS_vec_int[s]+" ");
			}
			System.out.println();
			for(int s=0; s<sentence.size(); ++s){
			    System.out.print( sentence.elementAt(s)+" ");
			}
			System.out.println();*/
			
			return local_POS_vec_int;
			
		}
		
		public void observational_prob(int[][] arc_table, int[][] word_table, Vector sentence, Vector global_pos_vector, Vector global_word_vector){
			
			//set up the observation matrix
			for(int i = 0; i<global_pos_vector.size(); ++i){
				for(int j = 0; j<sentence.size(); ++j){
					String test = sentence.elementAt(j).toString();
					try{
						int word_position = global_word_vector.indexOf(sentence.elementAt(j));
						//System.out.println("Word: "+sentence.elementAt(j)+" word_position: "+ word_position + " "+"Global POS vector: "+ global_pos_vector.size());
						observation_probabilities[i][j] = (double) word_table[i][word_position] / (double) word_table[global_pos_vector.size()][word_position];
					}
					catch(IndexOutOfBoundsException e){
						if(test.trim().isEmpty()){}
						else if(!test.trim().isEmpty()){
							int word_position = global_word_vector.indexOf(sentence.elementAt(j));
							//System.out.println("Word: "+sentence.elementAt(j)+" word_position: "+ word_position + " "+"Global POS vector: "+ global_pos_vector.size());
							if(j<2 && i<2){
								observation_probabilities[i][j] = 0.0;
							}
							else{
								observation_probabilities[i][j] =  1.0 /  (double) (global_pos_vector.size()-2);
							}
						}
					}
			
				}
			}
			//setup transition_probabilities
			for(int i = 0; i<global_pos_vector.size(); ++i){
				for(int j = 0; j<global_pos_vector.size(); ++j){
					transition_probabilities[i][j] = (double) arc_table[i][j] / (double) arc_table[i][global_pos_vector.size()];
				}
			}
			/*for(int i = 0; i<global_pos_vector.size(); ++i){
				for(int j = 0; j<global_pos_vector.size(); ++j){
					System.out.print(transition_probabilities[i][j]+" "); 
				}
				System.out.println();
			}
			for(int i = 0; i<global_pos_vector.size(); ++i){
				for(int j = 0; j<sentence.size(); ++j){
					System.out.print(observation_probabilities[i][j]+" "); 
				}
				System.out.println();
			}*/
		}
		
		public void clear_observational(){
			observation_probabilities = null;
		}
		
		public void print_trans(Vector global_pos_vector){
			for(int i = 0; i<global_pos_vector.size(); ++i){
				for(int j = 0; j<global_pos_vector.size(); ++j){
					System.out.print(transition_probabilities[i][j]+" ");
				}
				System.out.println();
			}
		}
	}

    public static void main (String[] args) throws IOException {
		
		File file = new File(args[0]);                                            	//Read file from command line
		File file_untag = new File(args[1]);
		BufferedReader br = new BufferedReader(new FileReader(file)); 				//Store file in buffered reader object (1)
		BufferedReader br2 = new BufferedReader(new FileReader(file));				//Store file in buffered reader object (2)
		BufferedReader br_untag = new BufferedReader(new FileReader(file_untag));
		Map<String, Integer> POS_MAP = new HashMap<String,Integer>();				//Map of the Parts of Speech and the count for each POS
		Vector<String> POS_VECTOR = new Vector<String>();							//Part of Speech vector, will be used in helping to construct the POS / Word Emit matrix, and POS ARC from / ARC to matrix
		Vector<String> WORD_EMITTED = new Vector<String>();							//Vector of words emitted, only unique words will be stored in here
		int[][] ARC_TABLE = new int[60][60];										//Arc to, Arc from table 
		int[][] POS_WORD_TABLE = new int[60][60000];								//Word Emitted , POS table (very large)
		Vector<String> obs_vec = new Vector<String>(200);
		Functions func = new Functions();
		
		String line;
		String prev_pos="start";
		POS_MAP.put("start",1);
		POS_MAP.put("end",1);
		POS_VECTOR.add("end");
		POS_VECTOR.add("start");
		int line_count = 0;
		System.out.println();

		while((line = br.readLine()) != null){
			if (line.isEmpty() && !prev_pos.equals("start") ){
				++ARC_TABLE[POS_VECTOR.indexOf(prev_pos)][POS_VECTOR.indexOf("end")];
				prev_pos = "start";
			}
			
			else{
				++line_count;
				String[] line_list = line.split("\\s");
				if(POS_MAP.containsKey(line_list[1])){
				
					POS_MAP.put(line_list[1], POS_MAP.get(line_list[1])+1);
				
					++ARC_TABLE[POS_VECTOR.indexOf(prev_pos)][POS_VECTOR.indexOf(line_list[1])];
					prev_pos = line_list[1];
				
					if(line_list[1].equals(".")){
						POS_MAP.put("start", POS_MAP.get("start")+1);
						prev_pos = ".";
					}
				
					if(!WORD_EMITTED.contains(line_list[0])){
						WORD_EMITTED.add(line_list[0]);
						++POS_WORD_TABLE[POS_VECTOR.indexOf(line_list[1])][WORD_EMITTED.indexOf(line_list[0])];
					}
				
					else{
						++POS_WORD_TABLE[POS_VECTOR.indexOf(line_list[1])][WORD_EMITTED.indexOf(line_list[0])];
					}
				}
			
				else{
					POS_MAP.put(line_list[1],1);
					POS_VECTOR.add(line_list[1]);
				
					if(prev_pos.equals(line_list[1]) && prev_pos.equals(".")){
						System.out.println("period period: "+line_count);
					}//trying to find period followed by period
				 
					++ARC_TABLE[POS_VECTOR.indexOf(prev_pos)][POS_VECTOR.indexOf(line_list[1])];
					prev_pos = line_list[1];
				
					if(line_list[1].equals(".")){
						POS_MAP.put("start", POS_MAP.get("start")+1);
						prev_pos = ".";
					}
				
					if(!WORD_EMITTED.contains(line_list[0])){
						WORD_EMITTED.add(line_list[0]);
						++POS_WORD_TABLE[POS_VECTOR.indexOf(line_list[1])][WORD_EMITTED.indexOf(line_list[0])];
					}
				
					else{
						++POS_WORD_TABLE[POS_VECTOR.indexOf(line_list[1])][WORD_EMITTED.indexOf(line_list[0])];
					}
				}
			}
		}
		
		POS_WORD_TABLE = func.sum_row(POS_WORD_TABLE, POS_VECTOR.size(), WORD_EMITTED.size());
		POS_WORD_TABLE = func.sum_col(POS_WORD_TABLE, POS_VECTOR.size(), WORD_EMITTED.size());
		ARC_TABLE = func.sum_row(ARC_TABLE, POS_VECTOR.size(), POS_VECTOR.size());
		ARC_TABLE = func.sum_col(ARC_TABLE, POS_VECTOR.size(), POS_VECTOR.size());
		
		Viterbi vit = new Viterbi();
		int[] pos_tagger = new int[200];
		int counter = 0;
		while((line = br_untag.readLine()) != null){
		//Viterbi 
		
			if(line.isEmpty()){	
				//run Viterbi algorith
				vit.observational_prob(ARC_TABLE, POS_WORD_TABLE, obs_vec, POS_VECTOR, WORD_EMITTED);	
				pos_tagger = vit.viterbi_algo(obs_vec, POS_VECTOR);
				for(int i = 0; i<obs_vec.size(); ++i){
					System.out.println(obs_vec.elementAt(i)+"\t"+POS_VECTOR.elementAt(pos_tagger[i+1]+2));
				}
				System.out.println();
				obs_vec.clear();
				vit = new Viterbi();
				pos_tagger = new int[200];
				pos_tagger = null;
			}
			String word = line.trim();
			if(!word.isEmpty()){
				obs_vec.add(word);
			}
		}
		
		
		//System.out.println();
		
		//func.print_array_boundary(ARC_TABLE, POS_VECTOR.size(), POS_VECTOR.size());
		//func.print_array_boundary(POS_WORD_TABLE, POS_VECTOR.size(), WORD_EMITTED.size());
		
		//func.print_vector(WORD_EMITTED);
		//System.out.println();
		//func.print_vector(POS_VECTOR);	
	}
	
	
}


/* Utilities for Checking Code
for(int i = 0 ; i<POS_VECTOR.size(); ++i){
	    System.out.println(POS_VECTOR.get(i));
	}
	Iterator it = POS_MAP.entrySet().iterator();
	while(it.hasNext()){
	    Map.Entry pair = (Map.Entry) it.next();
	    System.out.println(POS_VECTOR.indexOf(pair.getKey()));
	    it.remove();
	}
	for(int i = 0; i<WORD_EMITTED.size(); ++i){

	    for(int j = 0 ; j<POS_VECTOR.size(); ++j){
		System.out.print(POS_WORD_TABLE[i][j]);
	    }
	    System.out.println();
	}

	
		for(int i = 0; i<POS_VECTOR.size(); ++i){
	    for(int j = 0; j<POS_VECTOR.size(); ++j){ 
		System.out.print(ARC_TABLE[i][j]+" ");
	    }
	    System.out.println();
	}


*/
