package czd.lib.network;

import android.content.Context;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

public class AsyncHttpClient {
	private static final int DEFAULT_MAX_CONNECTIONS = 10;
	private static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
	private static final int DEFAULT_MAX_RETRIES = 1;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	private static int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private ThreadPoolExecutor threadPool;
	private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
	private final Map<String, String> clientHeaderMap;

	/**
	 * Creates a new AsyncHttpClient.
	 */
	public AsyncHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, socketTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, "czd_lib");

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		
		httpParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(cm, httpParams);
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(response.getEntity()));
							break;
						}
					}
				}
			}
		});

		httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_MAX_RETRIES));

		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
		clientHeaderMap = new HashMap<String, String>();
	}

	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public HttpContext getHttpContext() {
		return this.httpContext;
	}

	public void setCookieStore(CookieStore cookieStore) {
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

	public void setUserAgent(String userAgent) {
		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
	}

	public void setTimeout(int timeout) {
		final HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
	}

	public void addHeader(String header, String value) {
		clientHeaderMap.put(header, value);
	}

	public void setBasicAuth(String user, String pass) {
		AuthScope scope = AuthScope.ANY;
		setBasicAuth(user, pass, scope);
	}

	public void setBasicAuth(String user, String pass, AuthScope scope) {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
		this.httpClient.getCredentialsProvider().setCredentials(scope, credentials);
	}

	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		List<WeakReference<Future<?>>> requestList = requestMap.get(context);
		if (requestList != null) {
			for (WeakReference<Future<?>> requestRef : requestList) {
				Future<?> request = requestRef.get();
				if (request != null) {
					request.cancel(mayInterruptIfRunning);
				}
			}
		}
		requestMap.remove(context);
	}

	public void cancelAllRequests() {
		requestMap.clear();
		threadPool.shutdown();
		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	public void get(String url, AsyncHttpResponseHandler responseHandler) {
		get(null, url, null, responseHandler);
	}

	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		get(null, url, params, responseHandler);
	}

	public void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		get(context, url, null, responseHandler);
	}

	public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		sendRequest(httpClient, httpContext, new HttpGet(getUrlWithQueryString(url, params)), null, responseHandler, context);
	}

	public void get(Context context, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, null, responseHandler, context);
	}

	public void post(String url, AsyncHttpResponseHandler responseHandler) {
		post(null, url, null, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		post(null, url, params, responseHandler);
	}

	public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		try {
			post(context, url, paramsToEntity(params, responseHandler), null, responseHandler);
		} catch (IOException e) {
			responseHandler.sendFailureMessage(e, "post set responseHandler fail");
		}
	}

	public void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType, responseHandler, context);
	}

	public void post(Context context, String url, Header[] headers, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = new HttpPost(url);
		if (params != null) {
			try {
				request.setEntity(paramsToEntity(params, responseHandler));
			} catch (IOException e) {
				responseHandler.sendFailureMessage(e, "Fail set responseHandler");
			}
		}
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, responseHandler, context);
	}

	public void post(Context context, String url, Header[] headers, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, responseHandler, context);
	}

	public void put(String url, AsyncHttpResponseHandler responseHandler) {
		put(null, url, null, responseHandler);
	}

	public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		put(null, url, params, responseHandler);
	}

	public void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		try {
			put(context, url, paramsToEntity(params, responseHandler), null, responseHandler);
		} catch (IOException e) {
			responseHandler.sendFailureMessage(e, "Fail set responseHandler");
		}
	}

	public void put(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPut(url), entity), contentType, responseHandler, context);
	}

	public void put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, responseHandler, context);
	}

	public void delete(String url, AsyncHttpResponseHandler responseHandler) {
		delete(null, url, responseHandler);
	}

	public void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		final HttpDelete delete = new HttpDelete(url);
		sendRequest(httpClient, httpContext, delete, null, responseHandler, context);
	}

	public void delete(Context context, String url, Header[] headers, AsyncHttpResponseHandler responseHandler) {
		final HttpDelete delete = new HttpDelete(url);
		if (headers != null)
			delete.setHeaders(headers);
		sendRequest(httpClient, httpContext, delete, null, responseHandler, context);
	}

	protected void sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, AsyncHttpResponseHandler responseHandler, Context context) {
		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		Future<?> request = threadPool.submit(new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler));

		if (context != null) {
			// Add request to request map
			List<WeakReference<Future<?>>> requestList = requestMap.get(context);
			if (requestList == null) {
				requestList = new LinkedList<WeakReference<Future<?>>>();
				requestMap.put(context, requestList);
			}

			requestList.add(new WeakReference<Future<?>>(request));

			// TODO: Remove dead weakrefs from requestLists?
		}
	}

	public static String getUrlWithQueryString(String url, RequestParams params) {
		if (params != null) {
			String paramString = params.getParamString();
			if (url.indexOf("?") == -1) {
				url += "?" + paramString;
			}
			else {
				url += "&" + paramString;
			}
		}

		return url;
	}

	private HttpEntity paramsToEntity(RequestParams params, AsyncHttpResponseHandler responseHandler) throws IOException {
		HttpEntity entity = null;

		if (params != null) {
			entity = params.getEntity(responseHandler);
		}

		return entity;
	}

	private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
	}

	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}
}
