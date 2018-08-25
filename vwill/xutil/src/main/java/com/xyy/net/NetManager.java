package com.xyy.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.os.Handler;
import android.os.Looper;

import com.xyy.net.imp.Callback;
import com.xyy.net.imp.ProgressCallback;

/**
 * Created by Administrator on 2016/12/27.
 */
public class NetManager {
	// 线程池对像
	private ExecutorService mExecutorService;
	private Map<String, Future<?>> futureMap;
	// 接收任务中传递回来的内容
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private static NetManager instance;
	// 网络请求对象
	private HttpUtil httpServer;

	private NetManager() {
		mExecutorService = Executors.newFixedThreadPool(10);
		futureMap = new HashMap<String, Future<?>>();
		httpServer = new HttpUtil();
	}

	public static NetManager getInstance() {
		if (null == instance) {
			instance = new NetManager();
		}
		return instance;
	}

	/**
	 * 设置Https请求的配置
	 * 
	 * @param config
	 * @return
	 */
	public NetManager setHttpsConfig(HttpsConfig config) {
		httpServer.setHttpsConfig(config);
		return instance;
	}

	public <T> void execute(RequestItem request, Callback<T> callback) {
		if (!futureMap.containsKey(request.url)) {
			Future<?> f = mExecutorService.submit(new NetRunnable<T>(request,
					callback));
			futureMap.put(request.url, f);
		}
	}

	public void cancel(String url) {
		Future<?> f = futureMap.get(url);
		if (f != null) {
			if (!f.isDone() && !f.isCancelled()) {
				f.cancel(true);
			}
			futureMap.remove(url);
		}
	}

	public void cancelAll() {
		Iterator<Map.Entry<String, Future<?>>> it = futureMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Future<?>> entry = it.next();
			cancel(entry.getKey());
		}
	}

	public boolean isTaskRunning(String url){
		return futureMap.containsKey(url);
	}

	/**
	 * 处理网络请求的任务
	 */
	class NetRunnable<T> implements Runnable {
		private RequestItem request;
		private Callback<T> callback;

		public NetRunnable(RequestItem request, Callback<T> callback) {
			this.request = request;
			this.callback = callback;
		}

		@Override
		public void run() {
			T t = null;
			// get请求
			if (request.method.toLowerCase().equals("get")) {
				// 是否是断点下载的参数
				if (request instanceof DownloadRequestItem) {
					// 使用断点续传下载
					DownloadRequestItem d = (DownloadRequestItem) request;
					if (callback instanceof ProgressCallback) {
						t = httpServer.download(d.url, d.savePath, d.from,
								d.to, callback,
								new HttpUtil.OnProgressListener() {

									@Override
									public void onProgress(int current,
											int total) {
										((ProgressCallback) callback)
												.onProgress(current, total);
									}
								});
					} else {
						t = httpServer.download(d.url, d.savePath, d.from,
								d.to, callback, null);
					}
				} else {
					t = httpServer.get(request.url, request.heads, callback);
				}
			} else {
				// post请求
				// 有参数
				if (request instanceof FileRequestItem) {
					// 文件参数
					FileRequestItem file = (FileRequestItem) request;
					if (callback instanceof ProgressCallback) {
						t = httpServer.post(file.url, file.strParam,
								file.fileParam, request.heads, callback,
								new HttpUtil.OnProgressListener() {
									@Override
									public void onProgress(int current,
											int total) {
										((ProgressCallback) callback)
												.onProgress(current, total);
									}
								});
					} else {
						t = httpServer.post(file.url, file.strParam,
								file.fileParam, request.heads, callback, null);
					}
				} else if (request instanceof StringRequestItem) {
					// 只有字符串参数
					StringRequestItem str = (StringRequestItem) request;
					t = httpServer.post(str.url, str.strParam, request.heads,
							callback);
				} else {
					// 无参数
					t = httpServer.post(request.url, null, request.heads,
							callback);
				}
			}
			futureMap.remove(request.url);
			handleResult(t, callback);
		}
	}

	private <T> void handleResult(final T t, final Callback<T> callback) {
		// 提交到主线程
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				callback.onResult(t);
			}
		});
	}

	public void releas() {
		mExecutorService = null;
		instance = null;
	}
}
