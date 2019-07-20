package vstore.framework.communication.access;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vstore.framework.access.FileAccess;
import vstore.framework.db.table_helper.FileAccessDBHelper;
import vstore.framework.db.table_helper.NodeDBHelper;
import vstore.framework.node.NodeInfo;
import vstore.framework.utils.IdentifierUtils;

/**
 * Responsible for Uploading all FileAccess objects to the corresponding storage nodes.
 *
 */
public class InsertAccessThread extends Thread {

	private OkHttpClient httpClient;
	List<FileAccess> faList;

	public InsertAccessThread(List<FileAccess> faList) {
		this.httpClient = new OkHttpClient();
		this.faList = faList;
	}
	
	
	@Override
	public void run() {
		
		insertAtRemote(faList);
				
	}
	
	private void insertAtRemote(List<FileAccess> faList) {
		boolean success = false;
		
		if (faList == null || faList.size() == 0) {
			return;
		}
		
		
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");		
		JSONArray jsonArray = new JSONArray();
		
		for (FileAccess fa: faList) {
			JSONObject jsonFA = new JSONObject();
			jsonFA.put("uuid", fa.getUuid());
			jsonFA.put("file", fa.getFileUuid());
			jsonFA.put("geohash", fa.getGeohash().toBase32());
			jsonFA.put("timeOfWeek", fa.getTimeOfWeek().toString());
			jsonFA.put("totalMinutes", fa.getTimeOfWeek().getTotalMinutes());
			jsonFA.put("deviceId", IdentifierUtils.getDeviceIdentifier());
			
			jsonArray.add(jsonFA);
		}
		
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fileAccesses", jsonArray);

		RequestBody body = RequestBody.create(JSON, jsonObject.toString());
				
		// get nodeAddress from (random) FileAccess obj from list
		NodeInfo ni = null;
		try {
			ni = NodeDBHelper.getNode(faList.get(0).getNodeId());
			
			if (ni == null) {
				return;
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String url = ni.getInsertAccessUri(); //"http://localhost:50001/fileAccess/insert";
		
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
				
		try (Response response = httpClient.newCall(request).execute())
		{
//			ResponseBody resBody = response.body();
//			if(resBody == null) return;
//			String responseBody = resBody.string();
//			JSONParser jP = new JSONParser();
//			JSONObject result = (JSONObject)jP.parse(responseBody);

			if (response.code() >= 200 && response.code() < 300) {
				// remote insert was successful, switch flag  
				success = true;
			}
			
//			if(result.containsKey("error") && (JsonUtils.getIntFromJson("error", result, 1) != 1)
//					|| response.code() == 404)
//			{
//				success = false;
//			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// on successful insert, switch "isUploaded" flag for fileAccess objects to true
		if (success) {
			try {
				FileAccessDBHelper.setAsUploaded(faList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
