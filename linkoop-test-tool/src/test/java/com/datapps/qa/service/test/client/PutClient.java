package com.datapps.qa.service.test.client;

import javax.ws.rs.core.MediaType;

import com.datapps.qa.service.test.utils.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 鐢ㄤ簬鍙戣捣get璇锋眰
 * @author Matrix42
 *
 */
public class PutClient implements MyClient{
	
	private ClientResponse response ;
	private WebResource resource;
	private String RequestUrl;
	
	public PutClient(String url, Object params) {
		Client client = Client.create();
		RequestUrl = Config.BaseUrl+url;
		resource = client.resource(RequestUrl);	
		connect(params);
	}
	/**
     * 鍙戣捣杩炴帴
     * @param params 鍙傛暟
     */
	private void connect(Object params){
		response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("X-AUTH-TOKEN",Config.AuthToken)
				.put(ClientResponse.class,params);
	}
	/**
     * 鑾峰彇int鍨嬬姸鎬佺爜
     */
	@Override
    public int getStatus(){
		return response.getStatus();
	}
	/**
     * 鑾峰彇缃戦〉杩斿洖鐨勫唴瀹�
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
