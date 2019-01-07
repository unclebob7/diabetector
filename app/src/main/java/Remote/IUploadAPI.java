package Remote;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 制造和发送POST请求抽象方法 （服务）
 * */

public interface IUploadAPI {
    @Multipart
    @POST("upload/upload.php")      /** Make a POST request. */
    Call<String> uploadFile(@Part MultipartBody.Part file);     /** Send a POST request. */
}
