package com.xyy.net;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class DownloadRequestItem extends RequestItem {
    int from;
    int to;
    String savePath;

    public static class Builder extends RequestItem.Builder {
        DownloadRequestItem r;

        public Builder() {
            r = new DownloadRequestItem();
        }

        @Override
        public Builder url(String url) {
            r.url = url;
            return this;
        }

        @Override
        public Builder method(String method) {
            r.method = method;
            return this;
        }

        @Override
        public Builder heads(Map<String, String> heads) {
            r.heads = heads;
            return this;
        }

        @Override
        public Builder addHead(String key, String value) {
            super.addHead(key, value);
            return this;
        }

        public Builder from(int from){
            r.from = from;
            return this;
        }

        public Builder to(int to){
            r.to = to;
            return this;
        }

        public Builder savePath(String savePath){
            r.savePath = savePath;
            return this;
        }

        @Override
        public DownloadRequestItem build() {
            return r;
        }
    }

}
