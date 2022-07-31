package Controller;

import java.util.concurrent.TimeUnit;

import APISet.apiset;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {

//    private static final String URL = "http://192.168.0.111/smart_canteen/user/";
//    private static final String URL = "http://192.168.137.1/smart_canteen/user/";
    private static final String URL = "http://192.168.72.161/smart_canteen/user/";
    private static Controller clientObj;
    private static Retrofit retrofit;

    Controller() {
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized Controller getInstance() {
        if (clientObj == null)
            clientObj = new Controller();
        return clientObj;
    }

    public apiset getAPI(){
        return retrofit.create(apiset.class);
    }

}
