package com.datapps.qa.service.test.utils;

import java.io.IOException;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.datapps.qa.service.test.cla.Is_Json_File_Util;

/**
 * 匹配两个json文件中的不同的地方
 * 
 * @author Administrator
 *
 */
public class JsonDiff {

	private String result = ""; // 保存不匹配的信息，作为最后的返回值
	private String tag = ""; // 保存标记每一层的最外边的key值，"."代表最顶层
	private Is_Json_File_Util is_json = new Is_Json_File_Util();

	public JsonDiff() {

	}

	/**
	 * 比对两个json字符串的入口
	 * 
	 * @param expect_json测试人员给定的期望json字符串
	 * @param get_json从服务器端获取的json字符串
	 * @return 返回不匹配的地方，返回空串为完全匹配，返回"-1"为某一字符串不是Json格式
	 * @throws IOException
	 */
	public String Diff(String expect_json, String get_json) throws IOException {
		//System.out.println(get_json);
		if (is_json.test_Is_Json(expect_json) && is_json.test_Is_Json(get_json)) {
			JSONObject json1 = JSONObject.fromObject(expect_json);
			JSONObject json2 = JSONObject.fromObject(get_json);
			try {
				this.compareJson(json1, json2, null);
				return result;
			} catch (Exception e) {
				throw new RuntimeException("json文件格式不匹配,请检查期望的json文件与创建json文件格式是否相同");
			}
		} else {
			return "error json format";
		}
	}

	private void compareJson(JSONObject json1, JSONObject json2, String key) {
		@SuppressWarnings("unchecked")
        Iterator<String> i = json1.keys(); // 保存json对象json1的所有key
		while (i.hasNext()) {
			key = i.next();
			if (isContainKey(json2, key) == false) {
				result += tag + "." + key + "^" + json1.get(key).toString() + "^" + "null^null^";
			} else if ("null".equals(json1.get(key).toString()) || "null".equals(json2.get(key).toString())) {
				compareJson(json1.get(key).toString(), json2.get(key).toString(), key);
			} else {
				compareJson(json1.get(key), json2.get(key), key);
			}
		}
	}

	private void compareJson(Object json1, Object json2, String key) {

		if (json1 instanceof JSONObject) {
			compareJson((JSONObject) json1, (JSONObject) json2, key);
		} else if (json1 instanceof JSONArray) {
			compareJson((JSONArray) json1, (JSONArray) json2, key);
		} else if (json1 instanceof String) {
			compareJson((String) json1, (String) json2, key);
		} else {
			compareJson(json1.toString(), json2.toString(), key);
		}
	}

	private void compareJson(String str1, String str2, String key) {
		if (!str1.equals(str2)) {
			result += tag + "." + key + "^" + str1 + "^" + tag + "." + key + "^" + str2 + "^";
		}
	}

	private void compareJson(JSONArray json1, JSONArray json2, String key) {
		if (!json2.containsAll(json1) || (json1.size() > json2.size())) {
			if(json1.size() > json2.size()){
				this.compareJson(json1, json2);
			}else{
			tag += "." + key;
			Iterator<?> i1 = json1.iterator();
			Iterator<?> i2 = json2.iterator();

			while (i1.hasNext()) {

				compareJson(i1.next(), i2.next(), key);
			}
			tag = "";
			}
		}
	}

	/**
	 * 判断json2文件从当前开始到最后是否包含某一指定的Key
	 * 
	 * @param arr
	 *            json2文件从当前开始到最后的所有key
	 * @param key
	 *            需要查找的被包含的key
	 * @return 返回true代表json2中包含key，false代表不包含
	 */
	private static boolean isContainKey(JSONObject js, String key) {
		@SuppressWarnings("unchecked")
        Iterator<String> arr = js.keys();
		boolean f = false;
		while (arr.hasNext()) {
			if (arr.next().equals(key)) {
				f = true;
				break;
			}
		}
		return f;
	}

	
	/**
	 * 找到JSONArray1中不被包含在JSONArray2中的那一项
	 * @param json1
	 * @param json2
	 * @return
	 */
	private void compareJson(JSONArray json1, JSONArray json2) {
		boolean k = false;

		for (int i = 0; i < json1.size(); i++) {
			for (int j = 0; j < json2.size(); j++) {
				if (json1.get(i).equals(json2.get(j))) {
					k = true;
					break;
				}
			}
			if (!k) {
			result += tag + "."  +json1.get(i).toString()  + "^" +"null"+"^" + "null^null^";
			}else{
				k=false;
			}
		}
	}

	


}
