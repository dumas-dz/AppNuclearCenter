package com.dumas.pedestal.common.util.http;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HTTPClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HTTPClientUtil.class);
	private static CloseableHttpClient httpclient = null;
	private static PoolingHttpClientConnectionManager connManager = null;
	private static final int connectTimeout = 5000;
	private static final int socketTimeout = 10000;
	private static final int connectionRequestTimeout = 1000;
	private static final int DEFAULT_INITIAL_BUFFER_SIZE = 4096;

	/**
	 * json请求
	 *
	 * @param strURL
	 * @param params
	 * @return
	 * @throws Exception 
	 */

	public static String postJSON(String strURL, JSONObject params) throws Exception {
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");// 设置请求方式
			connection.setRequestProperty("Accept", "application/json");// 设置接收数据的格式
			connection.setRequestProperty("Content-Type", "application/json");// 设置发送数据的格式
			connection.connect();
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");// utf-8编码
			out.append(params.toString());
			out.flush();
			out.close();
			// 读取响应
			int length = (int) connection.getContentLength();// 获取长度
			InputStream is = connection.getInputStream();
			if (length != -1) {
				byte[] data = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, data, destPos, readLen);
					destPos += readLen;
				}
				String result = new String(data, "UTF-8");
				return result;
			}
			return null;
		} catch (Exception e) {
		   throw e;
		}
	}

	public static String doPost(String urlPath, Map<String, String> paramMap, RequestConfig requestConfig,
			Charset encode) throws IOException {
		String result = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		HttpPost httpPost = new HttpPost(urlPath);
		if (null == requestConfig) {
			requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(5000)
					.setConnectionRequestTimeout(1000).build();
		}
		encode = null == encode ? Consts.UTF_8 : encode;
		ArrayList postParams = new ArrayList();
		Iterator e;
		if (null != paramMap) {
			e = paramMap.keySet().iterator();

			while (e.hasNext()) {
				String e1 = (String) e.next();
				postParams.add(new BasicNameValuePair(e1, (String) paramMap.get(e1)));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(postParams, encode));
		httpPost.setConfig(requestConfig);
		try {
			response = httpclient.execute(httpPost);
			entity = response.getEntity();
			result = null == entity ? null : EntityUtils.toString(entity, encode);
			if (response.getStatusLine().getStatusCode() == 200) {

				String e2 = result;
				return e2;
			}

			e = null;
		} catch (ClientProtocolException var22) {
			throw var22;
		} catch (IOException var23) {
			throw var23;
		} finally {
			try {
				if (null != entity) {
					entity.getContent().close();
				}
				if (null != response) {
					response.close();
				}
			} catch (IOException var21) {
				throw var21;
			}
			httpPost.releaseConnection();
		}
		return result;
	}

	static {
		try {
			SSLContext e = SSLContexts.custom().useTLS().build();
			e.init((KeyManager[]) null, new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, (SecureRandom) null);
			Registry socketFactoryRegistry = RegistryBuilder.create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(e)).build();
			connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
			connManager.setDefaultSocketConfig(socketConfig);
			MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
					.setMaxLineLength(2000).build();
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(200);
			connManager.setDefaultMaxPerRoute(20);
			httpclient = HttpClients.custom().setConnectionManager(connManager).build();
		} catch (KeyManagementException var5) {
			logger.error("KeyManagementException", var5);
		} catch (NoSuchAlgorithmException var6) {
			logger.error("NoSuchAlgorithmException", var6);
		}

	}
}
