package com.datapps.qa.service.test.utils;

import com.datapps.qa.service.test.cla.Error_Massage_String;


public class Result_Error_out_Main {
	public static void result_Error_Output_Main(String errorMassage) {
		// TODO Auto-generated method stub
		Error_Massage_String errror_String = new Error_Massage_String();
		String str = errror_String.string_To_ArrayList(errorMassage);
		if(!str.isEmpty()){
			throw new RuntimeException(str);
		}
	}
}
