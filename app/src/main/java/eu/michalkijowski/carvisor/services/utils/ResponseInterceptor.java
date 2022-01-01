package eu.michalkijowski.carvisor.services.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Response modified = response.newBuilder()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .build();

        return modified;
    }
}