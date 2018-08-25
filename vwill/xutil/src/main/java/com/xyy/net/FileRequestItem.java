package com.xyy.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class FileRequestItem extends StringRequestItem {
	// 文件参数
	Map<String, File> fileParam;

	public static class Builder extends StringRequestItem.Builder {
		private Map<String, File> fileParam;

		public StringRequestItem build() {
			return new FileRequestItem(this);
		}

		public Builder addFileParam(String key, File value) {
			if (fileParam == null) {
				fileParam = new HashMap<String, File>();
			}
			fileParam.put(key, value);
			return this;
		}

		@Override
		public Builder addStringParam(String key, String value) {
			super.addStringParam(key, value);
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
		public Builder addHead(String key,
				String value) {
			super.addHead(key, value);
			return this;
		}

	}

	public FileRequestItem(Builder builder) {
		super(builder);
		this.fileParam = builder.fileParam;
	}
}
