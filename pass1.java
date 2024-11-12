
import java.io.*;

class Optab{
	String name;
	String opcode;
	Optab(String name,String opcode){
		this.name=name;
		this.opcode=opcode;
	}
}

class Symtab{
	String name;
	int addr;
	Symtab(){
		this.name=null;
		addr=0;
	}
}

class Litab{
	String name;
	int addr;
	Litab(){
		this.name=null;
		addr=0;	
	}
}

class Analysis{	
	String isopc,adop,dlop,regno;
	Analysis(){
		
		Pass1.setup();
	}
	boolean isop(String x){
		for(int i=0;i<Pass1.ot.length;i++){
			if(x.equals(Pass1.ot[i].name)){
				isopc=Pass1.ot[i].opcode;
				return true;
			}
		}
		return false;
	}
	boolean  isadr(String x){
		for(int i=0;i<Pass1.adr.length;i++){
			if(x.equals(Pass1.adr[i])){
				adop="0"+i;
				
				return true;
			}
		}
		return false;
		
	}
	boolean isdcl(String x){
		for(int i=0;i<Pass1.dl.length;i++){
			if(x.equals(Pass1.dl[i])){
				dlop="0"+i;
				return true;
			}
		}
		return false;
		
	}
	boolean isreg(String x){
		for(int i=0;i<Pass1.reg.length;i++){
			if(x.equals(Pass1.reg[i])){
				regno=""+i;
				return true;
			}
		}
		return false;
		
	}
	
	void writeic(String x) throws IOException,FileNotFoundException{
		BufferedWriter bw=new BufferedWriter(new FileWriter("InterCode.txt",true));
		bw.write(x+"\n");
		bw.close();
		
	}
	void writeSNL() throws IOException,FileNotFoundException{
		BufferedWriter bw1=new BufferedWriter(new FileWriter("Symtab.txt"));
		BufferedWriter bw2=new BufferedWriter(new FileWriter("Litab.txt"));
		for(int i=0;i<Pass1.st.length;i++){
			if(Pass1.st[i].name==null)
				break;
			bw1.write(Pass1.st[i].name+" "+Pass1.st[i].addr+"\n");
			
		}
		for(int i=0;i<Pass1.lt.length;i++){
			if(Pass1.lt[i].name==null)
				break;
			bw2.write(Pass1.lt[i].name+" "+Pass1.lt[i].addr+"\n");
			
		}
		bw1.close();
		bw2.close();
		
		
		
	}
	void process(){
		
		String line;
		boolean end=false;
		String symbff[]=new String[2];
		
		try(BufferedReader br=new BufferedReader(new FileReader("input.txt"))){
			int lit_itr=0,sym_itr=0;
			int loc_cntr=0;			
			while((line=br.readLine())!=null && !end){
				String ics="";
				String type="";
				boolean regexists=false;
				int constants=0;
				int sym_cnt=0,lit_cnt=0,opr_count=0;
				//process words and identify type of statement
				line=line.replaceAll(","," ");
				String words[]=line.split(" ");
				
				for(int i=0;i<words.length;i++){				
					if(isop(words[i])){
						type="IS";						
					}else if(isadr(words[i])){
						type="AD";
					}else if(isreg(words[i])){
						regexists=true;
						opr_count+=1;
						
						//already processed in operands 
					}else if(isdcl(words[i])){
						type="DL";
					}else if(words[i].matches("^'.*'$")){
						if(type.equals("DL")){
							//constants process of DL
							constants=Integer.parseInt(words[i].substring(1,words[i].length()-1));
						}else{
							Pass1.lt[lit_itr++].name=words[i];
							lit_cnt+=1;
							opr_count+=1;
							
							
										
						}
					}else{
						if(i==0){
							Pass1.st[sym_itr].name=words[i];
							Pass1.st[sym_itr].addr=loc_cntr++;
								sym_itr+=1;
								
						}else{
							
							if(type.equals("DL") || type.equals("AD")){
								constants=Integer.parseInt(words[i]);
								
							}else{
								opr_count+=1;
								symbff[sym_cnt]=words[i];
								sym_cnt+=1;
							}
						}						
					}
					
				}
				String opp="";
				if(type.equals("IS")){
				        loc_cntr+=1;
					if(opr_count==0)
						opp="";
					else if(opr_count==1){
						if(lit_cnt==1)
							opp="(L,"+(lit_itr-1)+")";
						else
							opp="(S,"+symbff[sym_cnt-1]+")";
							
					}else{
						if(regexists==true){
						 	if(lit_cnt==1)
								opp="("+regno+")"+"(L,"+(lit_itr-1)+")";
							else
								opp="("+regno+")"+"(S,"+symbff[sym_cnt-1]+")";
						}else{
							if(sym_cnt==2)
								opp="(S,"+symbff[sym_cnt-2]+")"+"(S,"+symbff[sym_cnt-1]+")";
							else{
								opp="(S,"+symbff[sym_cnt-1]+")"+"(L,"+(lit_itr-1)+")";								
							}
						}
					}
						
					ics="(IS,"+isopc+") "+opp;
				}else if(type.equals("DL")){
					opp="(C,"+constants+")";
					ics="(DL,"+dlop+") "+opp;
					if(dlop.equals("00"))
						loc_cntr+=constants;
				}else{
					switch(adop){
						case("00"):
							opp="(C,"+constants+")";
							loc_cntr+=constants;
							break;
						case("01"):
							opp="";
							end=true;
							for(int i=0;i<lit_itr;i++){
								if(Pass1.lt[i].name==null)
									break;
								if(Pass1.lt[i].addr==0){
									Pass1.lt[i].addr=loc_cntr++;
								}
							}
							break;
						case("02"):
							opp="";
							
							for(int i=0;i<lit_itr;i++){
								if(Pass1.lt[i].name==null)
									break;
								if(Pass1.lt[i].addr==0){
									Pass1.lt[i].addr=loc_cntr++;
								}
							}
							break;						
					}
					ics="(AD,"+adop+") "+opp;
					
					
				}
				writeic(ics);
			}
			writeSNL();
			br.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}


class Pass1{
	static Optab[] ot=new Optab[9];
	static Symtab[] st=new Symtab[20];
	static Litab[] lt=new Litab[15];
	static String[] reg={"AREG","BREG","CREG","DREG"};
	static String[] adr={"START","END","LTORG"};
	static String[] dl={"DS","DC"};
	
	
	static void setup(){
		String line;
		for(int i=0;i<st.length;i++){
			st[i]=new Symtab();
		}
		for(int i=0;i<lt.length;i++){
			lt[i]=new Litab();
		}
		int i=0;
		try(BufferedReader br=new BufferedReader(new FileReader("/home/nikita/Desktop/Assembler/optab.txt"))){
			while((line=br.readLine())!=null){
				
					String words[]=line.split(" ");
					ot[i++]=new Optab(words[0],words[1]);
					
				
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args){
		Analysis a=new Analysis();
		a.process();
		System.out.println("Pass1 Completed Succesfully.");		
	}
}


