package czd.lib.network;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {
	private static String ENCODING = "UTF-8";

	protected ConcurrentHashMap<String,String> urlParams;
	protected ConcurrentHashMap<String,FileWrapper> fileParams;
	protected ConcurrentHashMap<String,StreamWrapper> streamParams;
	protected ConcurrentHashMap<String,ArrayList<String>> urlParamsWithArray;

	public RequestParams() {
		init();
	}

	public RequestParams(Map<String,String> source) {
		init();

		for (Map.Entry<String,String> entry : source.entrySet())
		{
			put(entry.getKey(), entry.getValue());
		}
	}

	public RequestParams(String key, String value) {
		init();

		put(key, value);
	}

	public RequestParams(Object... keysAndValues) {
		init();
		int len = keysAndValues.length;
		if (len % 2 != 0)
			throw new IllegalArgumentException("Supplied arguments must be even");
		for (int i = 0; i < len; i += 2)
		{
			String key = String.valueOf(keysAndValues[i]);
			String val = String.valueOf(keysAndValues[i + 1]);
			put(key, val);
		}
	}

	public void put(String key, int value) {
		if (key != null)
		{
			urlParams.put(key, String.valueOf(value));
		}
	}

	public void put(String key, String value) {
		if (key != null && value != null)
		{
			urlParams.put(key, value);
		}
	}

	public void put(String key, File file) throws FileNotFoundException {
		put(key, file, null);
	}

	public void put(String key, File file, String contentType) throws FileNotFoundException {
		if (key != null && file != null)
		{
			fileParams.put(key, new FileWrapper(file, contentType));
		}
	}

	public void put(String key, ArrayList<String> values) {
		if (key != null && values != null)
		{
			urlParamsWithArray.put(key, values);
		}
	}

	public void put(String key, InputStream stream) {
		put(key, stream, null);
	}

	public void put(String key, InputStream stream, String name) {
		put(key, stream, name, null);
	}

	public void put(String key, InputStream stream, String name, String contentType) {
		if (key != null && stream != null)
		{
			streamParams.put(key, new StreamWrapper(stream, name, contentType));
		}
	}

	public void remove(String key) {
		urlParams.remove(key);
		fileParams.remove(key);
		streamParams.remove(key);
		urlParamsWithArray.remove(key);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String,String> entry : urlParams.entrySet())
		{
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}

		for (ConcurrentHashMap.Entry<String,FileWrapper> entry : fileParams.entrySet())
		{
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append("STREAM");
		}

		for (ConcurrentHashMap.Entry<String,FileWrapper> entry : fileParams.entrySet())
		{
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append("FILE");
		}

		for (ConcurrentHashMap.Entry<String,ArrayList<String>> entry : urlParamsWithArray.entrySet())
		{
			if (result.length() > 0)
				result.append("&");

			ArrayList<String> values = entry.getValue();
			for (int i = 0; i < values.size(); i++)
			{
				if (i != 0)
					result.append("&");
				result.append(entry.getKey()).append("[]");
				result.append("=");
				result.append(values.get(i));
			}
		}

		return result.toString();
	}

	public HttpEntity getEntity(AsyncHttpResponseHandler responseHandler) throws IOException {
		if (streamParams.isEmpty() && fileParams.isEmpty())
		{
			return createFormEntity();
		}
		else
		{
			return createMultipartEntity(responseHandler);
		}
	}

	private HttpEntity createFormEntity() {
		try
		{
			return new UrlEncodedFormEntity(getParamsList(), ENCODING);
		} catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}

	private HttpEntity createMultipartEntity(AsyncHttpResponseHandler progressHandler) throws IOException {
		SimpleMultipartEntity entity = new SimpleMultipartEntity(progressHandler);

		// Add string params
		for (ConcurrentHashMap.Entry<String,String> entry : urlParams.entrySet())
		{
			entity.addPart(entry.getKey(), entry.getValue());
		}

		// Add dupe params
		for (ConcurrentHashMap.Entry<String,ArrayList<String>> entry : urlParamsWithArray.entrySet())
		{
			ArrayList<String> values = entry.getValue();
			for (String value : values)
			{
				entity.addPart(entry.getKey() + "[]", value);
			}
		}

		// Add stream params
		for (ConcurrentHashMap.Entry<String,StreamWrapper> entry : streamParams.entrySet())
		{
			StreamWrapper stream = entry.getValue();
			if (stream.inputStream != null)
			{
				entity.addPart(entry.getKey(), stream.name, stream.inputStream, stream.contentType);
			}
		}

		// Add file params
		for (ConcurrentHashMap.Entry<String,FileWrapper> entry : fileParams.entrySet())
		{
			FileWrapper fileWrapper = entry.getValue();
			entity.addPart(entry.getKey(), fileWrapper.file, fileWrapper.contentType);
		}

		return entity;
	}

	private void init() {
		urlParams = new ConcurrentHashMap<String,String>();
		streamParams = new ConcurrentHashMap<String,StreamWrapper>();
		fileParams = new ConcurrentHashMap<String,FileWrapper>();
		urlParamsWithArray = new ConcurrentHashMap<String,ArrayList<String>>();
	}

	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

		for (ConcurrentHashMap.Entry<String,String> entry : urlParams.entrySet())
		{
			lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		for (ConcurrentHashMap.Entry<String,ArrayList<String>> entry : urlParamsWithArray.entrySet())
		{
			ArrayList<String> values = entry.getValue();
			for (String value : values)
			{
				lparams.add(new BasicNameValuePair(entry.getKey() + "[]", value));
			}
		}

		return lparams;
	}

	protected String getParamString() {
		return URLEncodedUtils.format(getParamsList(), ENCODING);
	}

	private static class FileWrapper {
		public File file;
		public String contentType;

		public FileWrapper(File file, String contentType) {
			this.file = file;
			this.contentType = contentType;
		}
	}

	private static class StreamWrapper {
		public InputStream inputStream;
		public String name;
		public String contentType;

		public StreamWrapper(InputStream inputStram, String name, String contentType) {
			this.inputStream = inputStram;
			this.name = name;
			this.contentType = contentType;
		}
	}
}
