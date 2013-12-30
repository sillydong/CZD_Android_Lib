package czd.lib.network;

import android.os.Message;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
	protected static final int SUCCESS_JSON_MESSAGE = 100;

	public void onSuccess(JSONObject response) {
	}

	public void onSuccess(JSONArray response) {
	}

	public void onSuccess(int statusCode, JSONObject response) {
		onSuccess(response);
	}

	public void onSuccess(int statusCode, JSONArray response) {
		onSuccess(response);
	}

	public void onFailure(Throwable e, JSONObject errorResponse) {
	}

	public void onFailure(Throwable e, JSONArray errorResponse) {
	}

	@Override
	protected void sendSuccessMessage(int statusCode, String responseBody) {
		if (statusCode != HttpStatus.SC_NO_CONTENT)
		{
			try
			{
				Object jsonResponse = parseResponse(responseBody);
				sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, jsonResponse}));
			} catch (JSONException e)
			{
				sendFailureMessage(e, responseBody);
			}
		}
		else
		{
			sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, new JSONObject()}));
		}
	}

	@Override
	protected void handleMessage(Message msg) {
		switch (msg.what)
		{
			case SUCCESS_JSON_MESSAGE:
				Object[] response = (Object[])msg.obj;
				handleSuccessJsonMessage(((Integer)response[0]).intValue(), response[1]);
				break;
			default:
				super.handleMessage(msg);
		}
	}

	protected void handleSuccessJsonMessage(int statusCode, Object jsonResponse) {
		if (jsonResponse instanceof JSONObject)
		{
			onSuccess(statusCode, (JSONObject)jsonResponse);
		}
		else if (jsonResponse instanceof JSONArray)
		{
			onSuccess(statusCode, (JSONArray)jsonResponse);
		}
		else
		{
			onFailure(new JSONException("Unexpected type " + jsonResponse.getClass().getName()), jsonResponse.toString());
		}
	}

	protected Object parseResponse(String responseBody) throws JSONException {
		Object result = null;
		//trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
		responseBody = responseBody.trim();
		if (responseBody.startsWith("{") || responseBody.startsWith("["))
		{
			result = new JSONTokener(responseBody).nextValue();
		}
		if (result == null)
		{
			result = responseBody;
		}
		return result;
	}

	@Override
	protected void handleFailureMessage(Throwable e, String responseBody) {
		try
		{
			if (responseBody != null)
			{
				Object jsonResponse = parseResponse(responseBody);
				if (jsonResponse instanceof JSONObject)
				{
					onFailure(e, (JSONObject)jsonResponse);
				}
				else if (jsonResponse instanceof JSONArray)
				{
					onFailure(e, (JSONArray)jsonResponse);
				}
				else
				{
					onFailure(e, responseBody);
				}
			}
			else
			{
				onFailure(e, "");
			}
		} catch (JSONException ex)
		{
			onFailure(e, responseBody);
		}
	}
}
