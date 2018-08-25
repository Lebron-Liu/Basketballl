package com.xyy.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class StringRequestItem extends RequestItem {
	// 文本参数
	Map<String, String> strParam;

	public static class Builder extends RequestItem.Builder {
		Map<String, String> strParam;

		public StringRequestItem build() {
			return new StringRequestItem(this);
		}

		public Builder addStringParam(String key, String value) {
			if (strParam == null) {
				strParam = new HashMap<String, String>();
			}
			strParam.put(key, value);
			return this;
		}

		@Override
		public Builder url(String url) {
			super.url(url);
			return this;
		}

		@Override
		public Builder method(String method) {
			super.method(method);
			return this;
		}

		@Override
		public Builder heads(Map<String, String> heads) {
			super.heads(heads);
			return this;
		}

		@Override
		public Builder addHead(String key, String value) {
			super.addHead(key, value);
			return this;
		}

	}

	public StringRequestItem(Builder builder) {
		super(builder);
		this.strParam = builder.strParam;
		this.method = "post";
	}

}
