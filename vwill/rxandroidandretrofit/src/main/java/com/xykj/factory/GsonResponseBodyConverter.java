package com.xykj.factory;

import com.google.gson.Gson;
import com.xykj.model.ErrorResult;
import com.xykj.model.ErrorResultException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 返回数据解析器
 * @param <T>
 */
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    /**
     * 针对数据返回成功、错误不同类型字段处理
     */
    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            if (null != response) {
                System.out.println("type:"+type.toString());
                if (!response.startsWith("{result:") && !type.getClass().getName().equals(ErrorResult.class.getClass().getName())) {
                    return gson.fromJson(response, type);
                } else {
                    ErrorResult err = gson.fromJson(response, ErrorResult.class);
                    int code = err.getResult();
                    if (code == -1) {
                        throw new ErrorResultException("", code);
                    } else {
                        throw new ErrorResultException(err.getExtras(), code);
                    }
                }
            }
        } finally {
            value.close();
        }
        return null;
    }
}
