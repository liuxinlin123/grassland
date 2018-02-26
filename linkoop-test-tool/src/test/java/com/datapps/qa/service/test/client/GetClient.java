package com.datapps.qa.service.test.client;

import javax.ws.rs.core.MediaType;

import com.datapps.qa.service.test.utils.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class GetClient implements MyClient{
	
	private WebResource resource;
	private ClientResponse response;
	private String RequestUrl;
	
	public GetClient(String url, MultivaluedMapImpl params) {
		RequestUrl = Config.BaseUrl + url;
		Client client = Client.create();
		resource = client.resource(RequestUrl);
		connect(params);
	}
	/**
	 * 鍙戣捣杩炴帴
	 */
	private void connect(MultivaluedMapImpl params){
		response = resource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON)
				.header("X-AUTH-TOKEN", Config.AuthToken)
				.get(ClientResponse.class);	
	}
	/**
	 * 鑾峰彇int鍨嬬姸鎬佺爜
	 */
	@Override
    public int getStatus(){
		return response.getStatus();
	}
	/**
	 * 鑾峰彇杩斿洖鐨勫唴瀹�
	 */
	@Override
    public String getContent(){
		return response.getEntity(String.class);
	}
	/**
	 * 鑾峰彇TOKEN瀛楃涓�
	 */
	public String getAuthToken(){
		return response.getHeaders()
				.get("X-AUTH-TOKEN")
				.get(0)
				.toString();
	}
	
	
}
