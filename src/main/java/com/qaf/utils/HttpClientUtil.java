package com.qaf.utils;

import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient工具类
 */
public class HttpClientUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpClientUtil.class);

	static final HttpClientBuilder builder = HttpClients.custom();

	private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A";

	static class DefaultTrustStrategy implements TrustStrategy {
		@Override
		public boolean isTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			return true;
		}
	}

	static {
		try {
			HttpProcessorBuilder httpProcessorbuilder = HttpProcessorBuilder
					.create();
			List<Header> defaultHeaders = new ArrayList<Header>();
			defaultHeaders.add(new BasicHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
			httpProcessorbuilder
					.addAll(new RequestUserAgent(DEFAULT_USER_AGENT));
			httpProcessorbuilder
					.addAll(new RequestDefaultHeaders(defaultHeaders));
			httpProcessorbuilder.addAll(new RequestContent());
			httpProcessorbuilder.addAll(new RequestTargetHost());
			httpProcessorbuilder.addAll(new RequestConnControl());
			httpProcessorbuilder.addAll(new RequestExpectContinue());
			httpProcessorbuilder.add(new RequestAddCookies());
			httpProcessorbuilder.add(new RequestAcceptEncoding());
			httpProcessorbuilder.add(new RequestAuthCache());
			httpProcessorbuilder.add(new ResponseProcessCookies());
			SSLContext sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, new DefaultTrustStrategy())
					.build();
			HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory>create()
					.register("http",
							PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslsf).build();
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			connectionManager.setMaxTotal(2000);
			connectionManager.setDefaultMaxPerRoute(100);
			builder.setConnectionManager(connectionManager)
					.setHttpProcessor(httpProcessorbuilder.build());
		} catch (Exception e) {
			System.err.println("初始化httpclient连接池出错！");
		}
	}

	public static CloseableHttpClient getClient() {
		try {
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String doRequest(HttpRequestBase request) {
		String response = null;
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				httpresponse = httpclient.execute(request);
				response = EntityUtils.toString(httpresponse.getEntity(),
						"UTF-8");
			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("关闭http资源时异常：{}", e);
		}
		return response;
	}

	public static String postSendJson(String url, String jsonStr, String sign) {
		String response = null;
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				HttpPost httppost = new HttpPost(url);
				StringEntity stringentity = new StringEntity(jsonStr,
						ContentType.create("application/json", "UTF-8"));
				httppost.setEntity(stringentity);
				httppost.addHeader("OPT-SIGN", sign);
				httpresponse = httpclient.execute(httppost);
				response = EntityUtils.toString(httpresponse.getEntity(),
						"UTF-8");
			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("关闭http资源时异常：{}", e);
		}
		return response;
	}

	public static String postSendJson(String url, String jsonStr) {
		String response = null;
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				HttpPost httppost = new HttpPost(url);
				StringEntity stringentity = new StringEntity(jsonStr,
						ContentType.create("application/json", "UTF-8"));
				httppost.setEntity(stringentity);
				httpresponse = httpclient.execute(httppost);
				response = EntityUtils.toString(httpresponse.getEntity(),
						"UTF-8");
			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("关闭http资源时异常：{}", e);
		}
		return response;
	}

	public static String doPost(String url, Map<String, String> param,
			String myContentType) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (null != param) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						paramList, "UTF-8");
				httpPost.setEntity(entity);
				myContentType = StringUtils.isBlank(myContentType)
						? "application/x-www-form-urlencoded"
						: myContentType;
				httpPost.addHeader("Content-Type", myContentType);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
				// if (null != httpClient) {
				// httpClient.close();
				// }
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("关闭http资源时异常：{}", e);
			}
		}
		return resultString;
	}

	/**
	 * POST请求
	 */
	public static String doPost(String url, Map<String, String> param,
			String myContentType, int timeOut) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			if (timeOut > 0) {
				RequestConfig requestConfig = RequestConfig.custom()
						.setConnectTimeout(timeOut)
						.setConnectionRequestTimeout(timeOut)
						.setSocketTimeout(timeOut).build();
				httpPost.setConfig(requestConfig);
			}
			// 创建参数列表
			if (null != param) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						paramList, "UTF-8");
				httpPost.setEntity(entity);
				myContentType = StringUtils.isBlank(myContentType)
						? "application/x-www-form-urlencoded"
						: myContentType;
				httpPost.addHeader("Content-Type", myContentType);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
				// if (null != httpClient) {
				// httpClient.close();
				// }
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("关闭http资源时异常：{}", e);
			}
		}
		return resultString;
	}

	public static String doPost(String url, Map<String, String> param,
			Map<String, String> headers) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (null != param) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						paramList, "UTF-8");
				httpPost.setEntity(entity);
				httpPost.addHeader("Content-Type",
						"application/x-www-form-urlencoded");
				if (headers != null) {
					for (Entry<String, String> e : headers.entrySet()) {
						httpPost.addHeader(e.getKey(), e.getValue());
					}
				}
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("关闭http资源时异常：{}", e);
			}
		}
		return resultString;
	}

	/**
	 * GET请求
	 */
	public static String doGet(String url, Map<String, String> param,
			Map<String, String> headers) {
		// 创建HttpClient对象
		CloseableHttpClient httpClient = getClient();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建URI
			URIBuilder builder = new URIBuilder(url);
			if (null != param) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);
			if (!MapUtils.isEmpty(headers)) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 执行请求
			response = httpClient.execute(httpGet);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("关闭http资源时异常：{}", e);
			}
		}
		return resultString;
	}

	public static int sendNotifyGetRsCode(String url, String jsonStr) {
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				HttpPost httppost = new HttpPost(url);
				StringEntity stringentity = new StringEntity(jsonStr,
						ContentType.create("application/json", "UTF-8"));
				httppost.setEntity(stringentity);
				httpresponse = httpclient.execute(httppost);
				int code = httpresponse.getStatusLine().getStatusCode();
				return code;

			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("关闭http资源时异常：{}", e);
		}
		return 0;
	}

	public static int sendNotifyGetRsCode(String url, String jsonStr,
			Map<String, String> headers) {
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				HttpPost httppost = new HttpPost(url);
				StringEntity stringentity = new StringEntity(jsonStr,
						ContentType.create("application/json", "UTF-8"));
				if (headers != null && headers.size() > 0) {
					for (Entry<String, String> e : headers.entrySet()) {
						httppost.addHeader(e.getKey(), e.getValue());
					}
				}
				httppost.setEntity(stringentity);
				httpresponse = httpclient.execute(httppost);
				int code = httpresponse.getStatusLine().getStatusCode();
				return code;

			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("关闭http资源时异常：{}", e);
		}
		return 0;
	}

}
