import java.util.*;
import java.io.*;


public class INFLATION_TEXT_EXTRACTOR{

	public static void main(String args[]) throws IOException{

		
				
				File file = new File(args[0]);   		//Read file from command line
				
				
					BufferedReader br = new BufferedReader(new FileReader(file)); 				//Store file in buffered reader object (1)
					String line;
					int count = 0;
					
					
					while((line = br.readLine()) != null){
						if(line.isEmpty()){
							
						}
						else if(line.matches(".*-----------.*")){
							if(line.matches(".*stable.*")){
								System.out.println("Stable");
							}
							else if (line.matches(".*inflation.*")){
								System.out.println("Higher");
							}
							else if (line.matches(".*deflation.*")){
								
							}	System.out.println("Lower");
						}
						else{
							//System.out.println("hi");
						}
					}
					System.out.println(count);
			
		
		
	}
}