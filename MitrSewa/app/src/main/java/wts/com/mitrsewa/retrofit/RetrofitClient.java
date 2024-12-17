package wts.com.mitrsewa.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient
{
    static {
        System.loadLibrary("native-lib");
    }
    public native static String getAPIHost();
    public native static String getAPI();
    public native static String getAuthKey();
    public native static String getAPIHostPlans();

    public static final String BASE_URL = getAPIHost();
    public static final String BASE_URL_GET = getAPIHostPlans();
    public static final String BASE_URL_WITHOUT_ATTRIBUTE = getAPI();
    public static final String AUTH_KEY = getAuthKey();
    private static RetrofitClient mInstance;
    private final retrofit2.Retrofit retrofit;
    private final retrofit2.Retrofit retrofitGet;
    private final retrofit2.Retrofit retrofitWithoutAttribute;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(7000, TimeUnit.SECONDS)
                .readTimeout(7000, TimeUnit.SECONDS).build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitGet = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL_GET).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitWithoutAttribute = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL_WITHOUT_ATTRIBUTE).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public WebServiceInterface getApi() {

        return retrofit.create(WebServiceInterface.class);
    }

    public WebServiceInterface getApiSecond() {

        return retrofitGet.create(WebServiceInterface.class);
    }

    public WebServiceInterface getApiWithoutAttribute() {

        return retrofitWithoutAttribute.create(WebServiceInterface.class);
    }
}
