package vstore.framework.communication.access;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import vstore.framework.access.FileAccess;
import vstore.framework.db.table_helper.FileAccessDBHelper;
import vstore.framework.utils.JsonUtils;

/**
 * Responsible for Uploading all FileAccess objects to the corresponding storage nodes.
 *
 */
public class InsertAccessThread extends Thread {

	private OkHttpClient httpClient;

	public InsertAccessThread() {
		httpClient = new OkHttpClient();
	}
	
	
	@Override
	public void run() {
		// TODO: get FileAccess from local DB that have not been uploaded yet
		// --> "order by nodeId" o.ä.?
		
		List<FileAccess> faList = null;
		
		try {
			faList = FileAccessDBHelper.getNotUploadedFileAccesses();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		insertAtRemote(faList);
		
		
		
	}
	
	private boolean insertAtRemote(List<FileAccess> faList) {

		// TODO
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		JSONObject jsonObject = new JSONObject();
		   try {
		       jsonObject.put("uuid", "123");
		       jsonObject.put("file", "myFile");
		       jsonObject.put("geohash", "hashy");
		       jsonObject.put("timeOfWeek", "now");
		       jsonObject.put("totalMinutes", "998");

		   } catch (Exception e) {
		       e.printStackTrace();
		   }
		
		RequestBody body = RequestBody.create(JSON, jsonObject.toString());
				
		//TODO: replace with node uri
		String url = "http://localhost:50001/fileAccess/insert";
		
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
				
		try (Response response = httpClient.newCall(request).execute())
		{
			ResponseBody resBody = response.body();
			if(resBody == null) return false;
			String responseBody = resBody.string();
			JSONParser jP = new JSONParser();
			JSONObject result = (JSONObject)jP.parse(responseBody);

			// TODO: on successful insert, switch "isUploaded" flag for fileAccess objects to true
			
			if(result.containsKey("error") && (JsonUtils.getIntFromJson("error", result, 1) != 1)
					|| response.code() == 404)
			{
				return true;
			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
}
