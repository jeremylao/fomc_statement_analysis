
import java.util.*;
import java.io.*;
import opennlp.maxent.*;


public class PROJECT_CHUNKER{

	public static class Functions{
		
		public Functions(){}
		
		public String getFileExtension(File file){
			
			String name = file.getName();
			
			try{
				return name.substring(name.lastIndexOf(".") +1);
			}
			catch (Exception e){
				return "";
			}
		}
		
		public int bioFlagFunc(File file){
			if(getFileExtension(file).equals("pos-tag")){
				return 1;
			}
			else{
				return 0;
			}
		}
		
		
	}

	public static void main (String[] args) throws IOException {
			
			File file = new File(args[0]);                                            	//Read file from command line
			File output = new File(args[1]);
			String output_name = args[1];
			Functions help = new Functions();		
			BufferedReader br = new BufferedReader(new FileReader(file)); 				//Store file in buffered reader object (1)
			PrintWriter pw = new PrintWriter(output_name);
			
			String line;
			int sos_flag=0;
			String line_list[];
			String prev_BIO_tag = "";
			String prior_pos = "";
			int bio_flag;
			int counter = 0;

			bio_flag = help.bioFlagFunc(file);
			
			while((line = br.readLine()) != null){
				if(line.isEmpty()){
					prev_BIO_tag = "";
					prior_pos = "";
					sos_flag=1;
					pw.print("\n");
					//counter = 0;
				}
				else{
					line_list = line.split("\\s");
					pw.print(line_list[0] + "\t");
					
					//part of speech
					//System.out.println(line_list[0]+" "+line_list[1]);
					pw.print("pos="+line_list[1]+"\t");
					
					//first character
					//pw.print("firstChar="+line_list[0].charAt(0)+"\t");
					
					//increase
					//pw.print("lastChar="+line_list[0].charAt(line_list[0].length()-1)+"\t");
					
					if(line_list[0].contains("/")){
						pw.print("fraction=y"+"\t");
					}
					
					if(line_list[0].matches(".*[0-9]+.*")){
						pw.print("isNumber=y"+"\t");
					}
					
					if(line_list[0].matches("maintain") || line_list[0].matches("keep")){
						pw.print("maintain_OR_keep=y"+"\t");
					}
					if(line_list[0].matches("discount")){
						pw.print("disco_rate=y"+"\t");
					}
					if(line_list[0].matches("lower") || line_list[0].matches("rais")){
						pw.print("lower_or_raise=y"+"\t");
					}
					if(line_list[0].toLowerCase().matches("treasury")){
						pw.print("treasury=y"+"\t");
					}
					if(line_list[0].toLowerCase().matches("reinvest")){
						pw.print("reinvest=y"+"\t");
					}
					if(line_list[0].matches("unemployment") || line_list[0].matches("inflation")){
						pw.print("mandate=y"+"\t");
					}
					//SoS
					//SoS
					if(prior_pos.equals("")){
						pw.print("sos=y"+"\t");
					}
					if(line_list[0].contains("increas") || line_list[0].contains("decreas")){
						pw.print("inc_or_dec=y"+"\t");
					}
			
					if(Character.isUpperCase(line_list[0].charAt(0))){
						pw.print("uppercase=y"+"\t");
					}					//Previos BIO Tag
					if(bio_flag == 1 && !prev_BIO_tag.equals("")){
						if(prev_BIO_tag.equals("A"))
							pw.print("priorBIOTag=A"+"\t");
						if(prev_BIO_tag.equals("U"))
							pw.print("priorBIOTag=U"+"\t");
						if(prev_BIO_tag.equals("T"))
							pw.print("priorBIOTag=T"+"\t");
						if(prev_BIO_tag.equals("O"))
							pw.print("priorBIOTag=O"+"\t");
						if(prev_BIO_tag.equals("N"))
							pw.print("priorBIOTag=N"+"\t");
						if(prev_BIO_tag.equals("D"))
							pw.print("priorBIOTag=D"+"\t");
					}
					
					//Prior part of speech
					if(!prior_pos.equals("")){
						pw.print("priorPOS="+prior_pos+"\t");
					}
					
					if(bio_flag == 0 && !prev_BIO_tag.equals("")){
						pw.print("priorBIOTag=@@"+"\t");
					}
					
					//BIO TAG & BIO Tag of previous line
					if(bio_flag == 1){
						//System.out.println(line_list[0]+ " "+line_list[1]+" "+counter);
						pw.print(line_list[2]+"\n");
						prev_BIO_tag=line_list[2];
						prior_pos = line_list[1];
					}
					else{
						if(bio_flag == 1){
							prev_BIO_tag = line_list[2];
						}
						prior_pos = line_list[1];
						prev_BIO_tag = "-";
						pw.print("\n");
					}
					
					++counter;
					
				}
			}
			
			
			pw.close();
	}
		
}