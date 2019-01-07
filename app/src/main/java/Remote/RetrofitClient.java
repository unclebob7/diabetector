package Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 制造和发送POST请求实现类
 * */

public class RetrofitClient {
    private static Retrofit retrofitClient = null;

    public static Retrofit getClient(String baseUrl) {
        if(retrofitClient == null){
            retrofitClient = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }

}
