package APISet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ResponseModel.ResponseModel;
import ResponseModel.MenuResponseModel;
import ResponseModel.OrderResponseModel;
import ResponseModel.TokenRes.Token_Res;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface apiset {

    @FormUrlEncoded
    @POST("checkUser.php")
    Call<ResponseModel> checkUser(
            @Field("phoneNumber") String phone_number
    );

    @FormUrlEncoded
    @POST("newUser.php")
    Call<ResponseModel> newUser(
            @Field("phoneNumber") String phone_number ,
            @Field("username") String user_name
    );

    @FormUrlEncoded
    @POST("updateToken.php")
    Call<ResponseModel> updateToken(
            @Field("user_id") int userID ,
            @Field("fb_token") String firebaseToken
    );

    @FormUrlEncoded
    @POST("signin.php")
    Call<ResponseModel>createUser(
            @Field("username") String user_name,
            @Field("number") String phone_number ,
            @Field("fb_token") String firebase_token
    );


    @FormUrlEncoded
    @POST("totalSpent.php")
    Call<ResponseModel>totalSpent(
            @Field("user_id") int user_id
    );

    @GET("item.php")
    Call<List<MenuResponseModel>> getData();

    @FormUrlEncoded
    @POST("pOrder.php")
    Call<List<OrderResponseModel>> getPreviousOrder(
            @Field("uid") int user_id
    );

    @FormUrlEncoded
    @POST("changeNUmber.php")
    Call<ResponseModel> updateNumber(
            @Field("user_id") int uid,
            @Field("new_number") String new_number
    );

    @POST("orderId.php")
    Call<ResponseModel> getLastOrderID();

    @FormUrlEncoded
    @POST("newOrder.php")
    Call<ResponseModel> newOrder(
            @Field("user_id") int user_id ,
            @Field("order_string") String order_string ,
            @Field("amount") int amount ,
            @Field("txn_id") String txn_id
    );



}
