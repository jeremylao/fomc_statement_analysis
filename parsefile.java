import java.util.*;
import java.io.*;

public class parsefile{

    public static void main(String[] args) throws IOException {
	File file = new File(args[0]);
	BufferedReader br = new BufferedReader(new FileReader(file));
	String line = "hello world";
	String line_list[] = line.split("\\s");
	int on_flag = 0;
	int off_flag = 0;

	while( (line=br.readLine()) != null){
	    if(line.isEmpty()){
		
	    }
	    else{
		line_list = line.split("[\\s]"); //.;:,]");
		int length_string=line_list.length;
		if(length_string > 0){
		    for(int i = 0; i< length_string; ++i){
			
			if(line_list[i].equals("EDT") || line_list[i].equals("EST") || line_list[i].equals("release")  ){
			    on_flag = 1;
			    
			}
			if(on_flag == 1 && line_list[i].equals("Implementation") && line_list[i+1].equals("Note") ){
			    on_flag = 0;
			}
			if(on_flag == 1 && line_list[i].equals("Last") && line_list[i+1].equals("Update:")){
			    on_flag = 0;
			}

			if(on_flag == 1 && !line_list[i].equals("") && !line.isEmpty() && !line_list[i].equals("release") && !line_list[i].equals("EST") && !line_list[i].equals("EDT") && !line_list[i].equals("Share")){
			    int string_len = line_list[i].length();
			    if(string_len > 0 && (line_list[i].charAt(string_len-1) == '.' || line_list[i].charAt(string_len-1) == ',' || line_list[i].charAt(string_len-1) == ':'  || line_list[i].charAt(string_len-1) == ';')){
				if(line_list[i].charAt(string_len-1) == '.' && string_len>2){
				    System.out.println(line_list[i].substring(0,string_len-1));
				    System.out.println(line_list[i].charAt(string_len-1));
				    System.out.println();

				}
				else{
				    System.out.println(line_list[i].substring(0,string_len-1));
				    System.out.println(line_list[i].charAt(string_len-1));
				}
			    }
			    else{
				System.out.println(line_list[i]);
			    }
			}			
		    }
		}
	    }
	}
    }
}