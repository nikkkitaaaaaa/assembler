import java.io.*;
import java.util.*;

class Pass2{
	public static void main(String[] args){
		HashMap<String,Integer> Symh=new HashMap<String,Integer>();
		HashMap<Integer,Integer> Lith=new HashMap<Integer,Integer>();
		String line;
		try(BufferedReader br1=new BufferedReader(new FileReader("Symtab.txt"))){
			while((line=br1.readLine())!=null){
				String[] words=line.split(" ");
				Symh.put(words[0],Integer.parseInt(words[1]));
			}
			br1.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		try(BufferedReader br2=new BufferedReader(new FileReader("Litab.txt"))){
			int i=0;
			while((line=br2.readLine())!=null){
				String[] words=line.split(" ");
				Lith.put(i,Integer.parseInt(words[1]));
				
				i+=1;
			}
			br2.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		try(BufferedReader br=new BufferedReader(new FileReader("InterCode.txt"))){
			BufferedWriter bw=new BufferedWriter(new FileWriter("MachCode.txt"));
			int loc_cntr=0;
			String otpt="";
			while((line=br.readLine())!=null){
				otpt="";
				if(line.contains("(AD")){
					if(line.contains("00")){
						//no need
					}			
				}else if(line.contains("IS")){
					String x="",reg="";
					if(line.contains("06") || line.contains("07")){
						if(line.contains("S"))
							x=""+Symh.get(line.substring(11,12));
						else
							x=""+Lith.get(Integer.parseInt(line.substring(11,12)));							
												
					}else{
						if(line.contains("00"))
							x="";
						else{
							
							if(line.contains("(0)") || line.contains("(1)") || line.contains("2") || line.contains("3")){
								reg=""+line.substring(9,10)+" ";
								if(line.contains("(S"))
									x=""+Symh.get(line.substring(14,15));
								else
									x=""+Lith.get(Integer.parseInt(line.substring(14,15)));	
							}else{
								if(line.contains("L")){
									x=""+Symh.get(line.substring(11,12))+" "+Lith.get(Integer.parseInt(line.substring(16,17)));
								}else{
									x=""+Symh.get(line.substring(11,12))+" "+Symh.get(line.substring(16,17));									
								}							
							}
						}
					}
					otpt="+"+line.substring(4,6)+" "+reg+x;
				}else{
					if(line.contains("01"))
						otpt="[ "+"01 0 "+line.substring(11,12)+" ]";
					
				}
				bw.write(otpt+"\n");
			}
			bw.close();
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
