package com.xyy.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 描述返回的内容
 */
public class ResponceItem {
	/**
	 * 返回码
	 */
	private int code;

	/**
	 * 返回内容
	 */
	private InputStream in;

	/**
	 * 头部信息
	 */
	private Map<String, List<String>> heads;

	private String content;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getString() {
		if (null == content) {
			try {
				ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int num;
				while ((num = in.read(buffer)) != -1) {
					dataOut.write(buffer, 0, num);
				}
				dataOut.flush();
				content = new String(dataOut.toByteArray());
				dataOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != in) {
					try {
						in.close();
						in = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return content;
	}

	public InputStream getBody() {
		if (null != content) {
			return null;
		}
		return in;
	}

	public void setBody(InputStream in) {
		this.in = in;
	}

	public ResponceItem(int code, InputStream in) {
		this(code, in, null);
	}

	public ResponceItem(int code, InputStream in,
			Map<String, List<String>> heads) {
		this.code = code;
		this.in = in;
		this.heads = heads;
	}

	public ResponceItem() {
	}

	public Map<String, List<String>> getHeads() {
		return heads;
	}

	public void setHeads(Map<String, List<String>> heads) {
		this.heads = heads;
	}
}
