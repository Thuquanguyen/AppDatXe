package com.example.ibct.appdatxe.network.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public class Http implements Serializable {

    private static final String TAG = "Http";

    private static final int TIME_CACHE_DEFAULT = 60;
    private static final int NUMBER_RETRY_DEFAULT = 3;
    private static final int TIME_OUT_DEFAULT = 60;

    private Map<String, String> body = new HashMap<>();
    private Map<String, String> header = new HashMap<>();
    private Map<String, String> files = new HashMap<>();
    private String baseUrl;
    private String url;
    private String tag;
    private CallBack callBack;
    private Method method;
    private boolean retry = true;
    private boolean cache = false;
    private int timeCache = TIME_CACHE_DEFAULT;
    private int numberRetry = NUMBER_RETRY_DEFAULT;
    private long timeOut = TIME_OUT_DEFAULT;

    private CompositeDisposable mCompositeDisposable;
    private Context context;

    public static Builder get() {
        return new Builder(null, Method.GET, null);
    }

    public static Builder post() {
        return new Builder(null, Method.POST, null);
    }

    public static Builder upload() {
        return new Builder(null, Method.UPLOAD, null);
    }

    public static Builder get(String url) {
        return new Builder(url, Method.GET, null);
    }

    public static Builder post(String url) {
        return new Builder(url, Method.POST, null);
    }

    public static Builder upload(String url) {
        return new Builder(url, Method.UPLOAD, null);
    }

    public static Builder create(Context context) {
        return new Builder(null, null, context);
    }

    public static void cancel(String tag) {
        Disposable disposable = Utils.getInstance().get(tag);
        if (disposable != null) disposable.dispose();
    }

    private Interceptor provideInterceptorCache() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                if (isNetworkAvailable(context)) {
                    int maxAge = timeCache;// lưu request trong vòng 1 phut
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // lưu request trong vòng 1 tuấn
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            }
        };
    }

    private Interceptor provideInterceptorParam() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                HttpUrl.Builder urlBuilder = originalRequest.url().newBuilder();
                List<String> segments = originalRequest.url().pathSegments();
                for (String s : body.keySet()) {
                    if (s == null) s = "";
                    String mKey = String.format("{%s}", s);
                    String mValue = body.get(s);

                    for (int i = 0; i < segments.size(); i++) {
                        if (mKey.equalsIgnoreCase(segments.get(i))) {
                            urlBuilder.setPathSegment(i, mValue);
                        }
                    }
                }
                Request request = originalRequest.newBuilder()
                        .url(urlBuilder.build())
                        .build();
                Log.i(TAG, "intercept: " + urlBuilder.build().toString());
                return chain.proceed(request);
            }
        };
    }

    private Service providerService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        /*
         * thiết lập cache cho api
         */
        if (cache && context != null) {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            builder.addInterceptor(provideInterceptorCache());

            builder.cache(cache);
        }

        /**
         * thay thế path băng param
         */
        builder.addInterceptor(provideInterceptorParam());

        /*
         * thiết lập time out cho request
         */
        OkHttpClient okHttpClient = builder
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(Service.class);
    }

    private Observable<ResponseBody> providerObservable() {
        Service service = providerService();
        Observable<ResponseBody> observable;
        switch (method) {
            case GET:
                observable = service.get(url, header, body);
                break;
            case POST:
                observable = service.post(url, header, body);
                break;
            case UPLOAD:
                List<MultipartBody.Part> fileList = new ArrayList<>();
                Set keyFile = files.keySet();
                int position = 0;
                int sum = fileList.size();
                for (Object key1 : keyFile) {
                    String name = (String) key1;
                    String path = files.get(name);
                    fileList.add(prepareFilePart(name, path, sum, position, callBack));
                }

                Map<String, RequestBody> desMap = new HashMap<>();
                Set keyDes = body.keySet();
                for (Object key1 : keyDes) {
                    String key = (String) key1;
                    String value = body.get(key);
                    desMap.put(key, createPartFromString(value));
                }

                observable = service.file(url, header, fileList, desMap);
                break;
            default:
                observable = service.get(url, header, body);
        }
        return observable;
    }
    private void execute() {
        Observable<ResponseBody> observable = providerObservable();
        if (retry) observable.retry(numberRetry);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                        if (!TextUtils.isEmpty(tag)) {
                            Utils.getInstance().put(tag, d);
                        }
                        if (mCompositeDisposable != null) {
                            mCompositeDisposable.add(d);
                        }
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            Log.i(TAG, "onNext: " + data);
                            if (callBack != null) {
                                callBack.onSuccess(data);
                            }
                        } catch (Exception e) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError Thai: ", e);
                        if (callBack != null) callBack.onFailure(e.toString());
                        onComplete();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        if (callBack != null) callBack.onComplete();
                        if (!TextUtils.isEmpty(tag)) {
                            Utils.getInstance().remove(tag);
                        }
                    }
                });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String name, String path, int sum, int position, CallBack callBack) {
        File file = new File(path);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(file, callBack, position, sum);
        return MultipartBody.Part.createFormData(name, file.getName(), progressRequestBody);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

    boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static enum Method {
        GET, POST, UPLOAD
    }

    interface Service {

        @POST
        @FormUrlEncoded
        Observable<ResponseBody> post(@Url String url,
                                      @HeaderMap Map<String, String> header,
                                      @FieldMap Map<String, String> body);

        @Multipart
        @POST
        Observable<ResponseBody> file(@Url String url,
                                      @HeaderMap Map<String, String> header,
                                      @Part List<MultipartBody.Part> files,
                                      @PartMap() Map<String, RequestBody> descriptions);

        @GET
        Observable<ResponseBody> get(@Url String url,
                                     @HeaderMap Map<String, String> header,
                                     @QueryMap Map<String, String> body);
    }

    public static class Builder implements Serializable {

        private Http http;

        Builder(String url, Method method, Context context) {
            http = new Http();
            try {
                URL link = new URL(url);
                http.baseUrl = link.getProtocol() + "://" + link.getHost() + "/";
                http.url = url.replace(http.baseUrl, "");
                http.method = method;
                http.context = context;
            } catch (Exception e) {
                Log.e(TAG, "Builder", e);
            }
        }

        public void execute() {
            http.execute();
        }

        public Builder setContext(Context context) {
            http.context = context;
            return this;
        }

        public Builder setTag(String tag) {
            http.tag = tag;
            return this;
        }

        public Builder setTimeOut(long timeOut) {
            http.timeOut = timeOut;
            return this;
        }

        public Builder setTimeCache(int timeCache) {
            http.timeCache = timeCache;
            http.cache = true;
            return this;
        }

        public Builder setNumberRetry(int numberRetry) {
            http.numberRetry = numberRetry;
            http.retry = true;
            return this;
        }

        public Builder setCompositeDisposable(CompositeDisposable mCompositeDisposable) {
            http.mCompositeDisposable = mCompositeDisposable;
            return this;
        }

        public Builder setRetry(boolean retry) {
            if (http.numberRetry != NUMBER_RETRY_DEFAULT) return this;
            http.retry = retry;
            return this;
        }

        public Builder setCache(boolean cache) {
            if (http.timeCache != TIME_CACHE_DEFAULT) return this;
            http.cache = cache;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            if (TextUtils.isEmpty(http.baseUrl)) http.baseUrl = baseUrl;
            return this;
        }

        public Builder setUrl(String url) {
            if (TextUtils.isEmpty(http.url)) http.url = url;
            return this;
        }

        public Builder setMethod(Method method) {
            if (http.method == null) http.method = method;
            return this;
        }

        public Builder putHeader(String key, String value) {
            http.header.put(key, value);
            return this;
        }

        public Builder putParameter(String key, String value) {
            http.body.put(key, value);
            return this;
        }

        public Builder putFile(String key, String filepath) {
            http.files.put(key, filepath);
            return this;
        }

        public Builder withCallBack(CallBack callBack) {
            http.callBack = callBack;
            return this;
        }
    }

    public static abstract class CallBack {
        public void onSuccess(String data) throws Exception {
        }

        public void onFailure(String error) {
        }

        public void onComplete() {
        }
    }

    public static abstract class ProgressCallBack extends CallBack {
        public void onProgressUpdate(float percentage) {
        }
    }

    static class Utils {
        private static Utils ourInstance;
        private Map<String, Disposable> disposableMap;

        private Utils() {
            disposableMap = new HashMap<>();
        }

        public static Utils getInstance() {
            if (ourInstance == null) ourInstance = new Utils();
            return ourInstance;
        }

        void put(String tag, Disposable mDisposable) {
            disposableMap.put(tag, mDisposable);
        }

        Disposable get(String tag) {
            return disposableMap.get(tag);
        }

        void remove(String tag) {
            Disposable disposable = disposableMap.get(tag);
            if (disposable != null) {
                disposable.dispose();
                disposableMap.remove(tag);
            }
        }

    }

    class ProgressRequestBody extends RequestBody {

        private static final int DEFAULT_BUFFER_SIZE = 2048;
        private File mFile;
        private CallBack mListener;
        private int position;
        private int sum;

        ProgressRequestBody(File mFile, CallBack mListener, int position, int sum) {
            this.mFile = mFile;
            this.mListener = mListener;
            this.position = position;
            this.sum = sum;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("image/*");
        }

        @Override
        public long contentLength() {
            return mFile.length();
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;
            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } catch (Exception e) {
                Log.e(TAG, "writeTo: ", e);
            } finally {
                in.close();
            }
        }

        class ProgressUpdater implements Runnable {
            private long mUploaded;
            private long mTotal;

            ProgressUpdater(long uploaded, long total) {
                mUploaded = uploaded;
                mTotal = total;
            }

            @Override
            public void run() {
                if (mListener instanceof ProgressCallBack) {
                    // TODO: 5/8/2018
                    ((ProgressCallBack) mListener).onProgressUpdate((float) (100 * mUploaded / mTotal));
                }
            }
        }
    }
}

