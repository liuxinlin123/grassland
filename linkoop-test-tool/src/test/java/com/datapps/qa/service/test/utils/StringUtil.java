package com.datapps.qa.service.test.utils;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
import com.datapps.qa.service.test.cla.SaveNameId;
import com.datapps.qa.service.test.client.PutClient;
import com.datapps.qa.service.test.constant.ElementType;

import net.sf.json.JSONObject;

public class StringUtil {

	/**
	 * 把字符串格式化为["example"]格式
	 * 
	 * @param param 待格式化字符串
	 * @return 格式化后的字符串
	 * @author Matrix42
	 */
	public static String Formate(Object param) {
		return "[\"" + param.toString() + "\"]";
	}

	/**
	 * 生成一个随机的名字
	 * 
	 * @return 随机的名字
	 * @author xs
	 */
	public static String RandomName() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 从json中取id
	 * 
	 * @param json
	 * @return id
	 */
	public static String getIdFromJson(String json) {

		JSONObject jsonObject = JSONObject.fromObject(json);
		if (jsonObject.containsKey("id")) {
			return jsonObject.getString("id");
		} else {
			return "";
		}
	}

	/**
	 * 获取十六进制的颜色代码.例如 "#6E36B4"
	 * 
	 * @return String
	 * @author Matrix42
	 */
	public static String getRandomColor() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return "#" + r + g + b;
	}

	/**
	 * 将json文件中对应的内容替换 jsonString为创建时的模版，将其中的个别参数替换
	 */
	public static String Replace(String path, String[] args, String jsonString) {
		String name = args[1].toLowerCase();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		SaveNameId CDCsaveNameId;
		SaveNameId CDOsaveNameId;
		SaveNameId FlowsaveNameId = null;
		switch (name) {
			case ElementType.PROCESSCONFIGS:
				CDCsaveNameId = new SaveNameId(path, "processconfigs");
				break;
			case ElementType.CDC:
				CDCsaveNameId = new SaveNameId(path, "cdc");
				break;
			case ElementType.CDO:
				CDCsaveNameId = new SaveNameId(path, "cdc");
				CDOsaveNameId = new SaveNameId(path, "cdo");

				String schema = CDCsaveNameId.getValue(jsonObject.getString("schemaName"));
				jsonObject.put("schema", schema);
				break;
			case ElementType.DATAFLOW:
				CDCsaveNameId = new SaveNameId(path, "cdc");
				CDOsaveNameId = new SaveNameId(path, "cdo");

				for (int i = 2; i < args.length; i++) {
					String id = "";
					if (i % 2 == 0) {
						id = CDOsaveNameId.getValue(args[i]);
					} else {
						id = CDCsaveNameId.getValue(args[i]);
					}
					jsonString = jsonString.replace(args[i], id);
				}

				break;
			case ElementType.WORKFLOW:
				// 创建文件(最后保存的文件)
				CDOsaveNameId = new SaveNameId(path, "cdo");
				FlowsaveNameId = new SaveNameId(path, "dataflow");
				// 获得json
				for (int i = 2; i < args.length; i++) {
					String s = args[i];
					String result;
					if (s.startsWith("dataflow")) {
						result = FlowsaveNameId.getValue(s);
						jsonString = jsonString.replace(s, result);
					}
				}

				jsonObject = JSONObject.fromObject(jsonString);
				break;
			case ElementType.SCHEDULER:
				if (args[2].equals("dataflow")) {
					FlowsaveNameId = new SaveNameId(path, "dataflow");
				} else if (args[2].equals("workflow")) {
					FlowsaveNameId = new SaveNameId(path, "workflow");
				}

				// 获得flow的id
				String flowName = jsonObject.getString("flowName");
				String flowId = FlowsaveNameId.getValue(flowName);
				jsonObject.put("flowId", flowId);
				break;
			case ElementType.TAG:
			    break;
			case ElementType.USER:
				String userName = jsonObject.getString("name");
				jsonObject.put("name", userName);
				jsonObject.put("loginId", userName);
				break;
		}

		if (jsonObject == null) {
			return "";
		} else {
			return jsonObject.toString();
		}

	}

	public static void Update(String path, String api, String[] args, String jsonString, String status) {
		SaveNameId saveNameId = new SaveNameId(path, args[1]);
		String id = saveNameId.getValue(args[2]);
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		jsonObject.put("id", id);
		PutClient putClient = new PutClient(api + "/" + id, jsonObject.toString());
		assertEquals(Integer.parseInt(status), putClient.getStatus());
	}

}
