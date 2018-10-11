package cz.weissar.weartracker.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.weissar.weartracker.dto.Activity;
import cz.weissar.weartracker.dto.ContextualUserQuestionnaire;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by pweissar
 */

public class RestClient {

    private static final String ACCEPT_JSON = "Accept: application/json";
    private static final String ACCEPT_PNG = "Accept: image/png";
    private static final String ACCEPT_PDF = "Accept: application/pdf";
    private static final String AUTHORIZATION = "Authorization";

    public static final String TEST_TOKEN = "Bearer 1s1qlnvk8l284nbb744ami2dbqfcn345bntbg44ij21dccnbmtej";

    private static final String CONTENT_TYPE_JSON = "Content-Type: application/json";
    private static final String BASE_URL = "http://imitgw.uhk.cz:59782/core/rest/";

    private static RestClient instance = new RestClient();

    private static Retrofit retrofit;
    private WearTrackerService wearTrackerService;


    private RestClient() {
        retrofit = new RetrofitBuilder()
                .withBaseUrl(BASE_URL)
                .withLevel(HttpLoggingInterceptor.Level.BODY)
                .build();

        wearTrackerService = retrofit.create(WearTrackerService.class);

    }

    public static WearTrackerService get() {
        return instance.wearTrackerService;
    }

    public interface WearTrackerService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("rules")
        Call<List<ContextualUserQuestionnaire>> getRules(@Header(AUTHORIZATION) String token);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("activity")
        Call<ResponseBody> postActivity(@Header(AUTHORIZATION) String token, @Body Activity activity);

    }

    /**
     * Build a retrofit instance
     */
    private class RetrofitBuilder {
        private String baseUrl;
        private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
        private Converter.Factory converter = GsonConverterFactory.create(); //GsonConverterFactory.create(JSONUtils.getConfiguredGson());
        //private Converter.Factory scalarConverter = ScalarsConverterFactory.create();
        //private int timeout = DEFAULT_TIMEOUT;
        private List<Interceptor> interceptors = new ArrayList<>();
        private boolean crashLogging = true;

        /**
         * Sets base url. No default value, must be called!
         *
         * @param baseUrl Base url
         * @return RetrofitBuilder instance
         */
        public RetrofitBuilder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets logging level. Default value {@link HttpLoggingInterceptor.Level#NONE}
         *
         * @param level Logging level
         * @return RetrofitBuilder instance
         */
        public RetrofitBuilder withLevel(HttpLoggingInterceptor.Level level) {
            this.level = level;
            return this;
        }

        /**
         * Sets interceptors. Default empty
         *
         * @param interceptors List of interceptors
         * @return RetrofitBuilder instance
         */
        public RetrofitBuilder withInterceptors(Interceptor... interceptors) {
            this.interceptors = Arrays.asList(interceptors);
            return this;
        }


        /**
         * Builds a {@link Retrofit} instance with set parameters
         *
         * @return Retrofit instance
         */
        public Retrofit build() {
            OkHttpClient.Builder httpClient = buildClient();

            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(converter)
                    //.addConverterFactory(scalarConverter)
                    .client(httpClient.build())
                    .build();
        }

        private OkHttpClient.Builder buildClient() {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            for (Interceptor interceptor : interceptors) {
                httpClient.addInterceptor(interceptor);
            }

            /*
            httpClient.addInterceptor(new HeaderInterceptor());
            httpClient.addInterceptor(new CustomHttpLoggingInterceptor().setLevel(level));
            if (crashLogging) {
                httpClient.addInterceptor(new CrashLoggingInterceptor());
            }

            httpClient.readTimeout(timeout, TimeUnit.SECONDS);
            httpClient.connectTimeout(timeout, TimeUnit.SECONDS);
            */

            return httpClient;
        }

    }

}
