package com.datapps.qa.service.test.cla;

import java.util.ArrayList;

public class Error_Massage_String {
	public String  string_To_ArrayList(String errormassage) {
		ArrayList<String> list = new ArrayList<String>();
		String[] s = errormassage.split("\\^");
		String str = new String();
		if("error json format".equals(s[0]))
			list.add(s[0]);
		else{
			for(int i = 0;i<s.length-3;i+=4){
				if(s[i].equals(s[i+2])){
					str = "in "+s[i]+" except = "+s[i+1]+" but = "+s[i+3];
				}else{
					str = "returned key "+s[i]+" is null";
				}
				list.add(str);
			}
		}
		String ss = new String();
		for(int i = 0 ;i<list.size();i++){
			ss += ("\n"+list.get(i));
		}
		return ss;
	}
	
}
