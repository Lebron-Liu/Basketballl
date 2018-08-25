package com.xyy.net;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsConfig {

	SSLSocketFactory sslSocketFactory;
	HostnameVerifier hostnameVerifier = DO_NOT_VERIFY;

	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	public HttpsConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
		return this;
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public HttpsConfig setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
		return this;
	}

	public static HttpsConfig createDefConfig() {
		return createConfig();
	}

	/**
	 * 单向验证
	 * 
	 * @param clientCer
	 *            服务端生成给客户端的公钥文件
	 * @return
	 */
	public static HttpsConfig createConfig(InputStream clientCer) {
		return createConfig(clientCer);
	}

	/**
	 * 双向验证
	 * 
	 * @param serverCer
	 *            服务端公钥文件
	 * @param clientBks
	 *            客户端私钥文件
	 * @param password
	 *            客户端私钥文件密码
	 * @return
	 */
	public static HttpsConfig createConfig(InputStream serverCer,
			InputStream clientBks, String password) {
		return createConfig(serverCer, clientBks, password);
	}

	private static HttpsConfig createConfig(Object... par) {
		HttpsConfig config = new HttpsConfig();
		if (par.length == 0) {
			config.sslSocketFactory = getNoSSLSocketFactory();
		} else if (par.length == 1 && par[0] instanceof InputStream) {
			config.sslSocketFactory = getSocketFactory((InputStream) par[0]);
		} else if (par.length == 3) {
			config.sslSocketFactory = getSocketFactory((InputStream) par[0],
					(InputStream) par[1], (String) par[2]);
		}
		return config;
	}

	/**
	 * 添加证书(单项认证) 把服务器颁发的证书mycer.cer放到Android的目录assets
	 * 
	 * @param is 服务端生成给客户端的公钥文件
	 */
	private static SSLSocketFactory getSocketFactory(InputStream is) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			// InputStream is = getAssets().open("mycer.cer");
			// InputStream is = new FileInputStream(clientCer);
			keyStore.setCertificateEntry("0",
					certificateFactory.generateCertificate(is));
			if (is != null) {
				is.close();
			}
			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(keyStore);
			sslContext.init(null, trustManagerFactory.getTrustManagers(),
					new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 添加证书 (双向认证) 首先对于双向证书验证，也就是说，客户端也会有个“.key文件”，服务器那边会同时有个“.cer文件”与之对应。
	 * 我们已经生成了server.key和server.cer文件。
	 * 接下来按照生成证书的方式，再生成一对这样的文件，我们命名为:client.key,client.cer. 然后配置服务器 <Connector
	 * 其他属性与前面一致 clientAuth="true" truststoreFile="client.cer" />
	 * 
	 * @param serverCer 服务端公钥
	 * @param clientBks 客户端私钥
	 * @param passwprd 客户端私钥文件密码
	 */
	private static SSLSocketFactory getSocketFactory(InputStream is,
			InputStream clientBks, String password) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			// InputStream is = getAssets().open("server.cer");
			// InputStream is = new FileInputStream(serverCer);
			keyStore.setCertificateEntry("0",
					certificateFactory.generateCertificate(is));
			if (is != null) {
				is.close();
			}
			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(keyStore);

			// 初始化keystore
			KeyStore clientKeyStore = KeyStore.getInstance("BKS");
			clientKeyStore.load(clientBks, password.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientKeyStore, password.toCharArray());

			sslContext.init(keyManagerFactory.getKeyManagers(),
					trustManagerFactory.getTrustManagers(), new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 没有安全证书的SSLContext
	private static SSLSocketFactory getNoSSLSocketFactory() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 不验证
	 */
	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	};
}
