package nl.kolkos.cryptoManagerBot.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class ApiRequestController {
	private static final Logger LOG = LogManager.getLogger(ApiRequestController.class);
	
	private String apiBaseUrl;
	
	
	public String getApiBaseUrl() {
		return apiBaseUrl;
	}


	public void setApiBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}


	public void loadApiConfig() throws IOException {
		LOG.trace("Load API configuration");
		
		ConfigService configService = new ConfigService();
		Properties properties = configService.loadConfig();
		
		String host = properties.getProperty("cryptoManager.host");
		String port = properties.getProperty("cryptoManager.port");
		String protocol = properties.getProperty("cryptoManager.protocol");

		// form the base url
		String url = String.format("%s://%s:%s/api/request/", protocol, host, port);
		
		this.setApiBaseUrl(url);
		
		LOG.trace("API Base URL: {}", this.getApiBaseUrl());
		LOG.trace("Finished loading API configuration");		
	}
		
	// source: http://literatejava.com/networks/ignore-ssl-certificate-errors-apache-httpclient-4-4/
	public HttpClient createHttpClient_AcceptsUntrustedCerts() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
	    HttpClientBuilder b = HttpClientBuilder.create();
	 
	    // setup a Trust Strategy that allows all certificates.
	    //
	    SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
	        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	            return true;
	        }
	    }).build();
	    b.setSslcontext( sslContext);
	 
	    // don't check Hostnames, either.
	    //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
	    HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
	 
	    // here's the special part:
	    //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
	    //      -- and create a Registry, to register it.
	    //
	    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();
	 
	    // now, we create connection-manager using our Registry.
	    //      -- allows multi-threaded use
	    PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
	    b.setConnectionManager( connMgr);
	 
	    // finally, build the HttpClient;
	    //      -- done!
	    HttpClient client = b.build();
	    return client;
	}
	
	public String doHttpGetRequest(String url) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException {
		HttpClient client = this.createHttpClient_AcceptsUntrustedCerts();
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
		    result.append(line);
		}
		
		return result.toString();
	}
	
	
	public JSONObject jsonObjectRequest(String url) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException  {
		String response = this.doHttpGetRequest(url);
		
		JSONObject jsonObject = new JSONObject(response);
		
		return jsonObject;
	}
	
	/**
	 * Requests a JSON array object
	 * @param urlString the api url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public JSONArray jsonArrayRequest(String url) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		String response = this.doHttpGetRequest(url);
	    
	    JSONArray jsonArray = new JSONArray(response);
	    
	    return jsonArray;
	}
	
	
}
