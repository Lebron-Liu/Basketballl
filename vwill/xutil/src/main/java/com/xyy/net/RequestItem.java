package com.xyy.net;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求的参数和地址信息
 */
public class RequestItem {
	// 额外的头部信息
	Map<String, String> heads;

	// 请求地址
	String url;

	// 请求方式
	String method = "get";

	public static class Builder {
		private Map<String, String> heads;
		private String url;
		private String method = "get";

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder method(String method) {
			this.method = method;
			return this;
		}

		public Builder heads(Map<String, String> heads) {
			this.heads = heads;
			return this;
		}

		public Builder addHead(String key, String value) {
			if (null == heads) {
				heads = new HashMap<String, String>();
			}
			heads.put(key, value);
			return this;
		}

		public RequestItem build() {
			return new RequestItem(this);
		}
	}

	protected RequestItem(Builder builder) {
		this.url = builder.url;
		this.method = builder.method;
		this.heads = builder.heads;
	}
	
	public RequestItem(){}

}
