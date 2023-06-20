package com.example.boozzapp.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*



interface API {

    @GET("/api/v1/categories")
    fun homeCategories(
        @Header("Authorization") Authorization: String,
        ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/api/v1/templates")
    fun homeTemplates(
        @Field("sort_by") sort_by: String,
        @Field("page") page: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/api/v1/categoryWiseVideos")
    fun categoryWiseVideo(
        @Field("sort_by") sort_by: String,
        @Field("category_id") category_id: String,
        @Field("page") page: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun paymentAPI(
        @Url url: String,
        @Header("Content-Type") conent: String,
        @Field("language") language: String,
        @Field("device_id") device_id: String,
        @Field("signature") signature: String,
        @Field("service_command") service_command: String,
        @Field("merchant_identifier") merchant_identifier: String,
        @Field("access_code") access_code: String,
    ): Call<ResponseBody>


    @POST
    fun paymentAPI(
        @Url url: String,
        @Header("Content-Type") conent: String,
        @Body body: RequestBody
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/register")
    fun register(
        @Field("type") type: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/addInquiry")
    fun addInquiry(
        @Field("property_id") property_id: String,
        @Field("property_user_id") property_user_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("mobile_no") mobile_no: String,
        @Field("message") message: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/contactUsToAgent")
    fun contactusToAgent(
        @Field("property_user_id") property_user_id: String,
        @Field("full_name") full_name: String,
        @Field("phone_number") phone_number: String,
        @Field("email") email: String,
        @Field("message") message: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/contactUs")
    fun contactUsMain(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("mobile_no") mobile_no: String,
        @Field("message") message: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/v1/changePassword")
    fun changePassword(
        @Header("Authorization") Authorization: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String
    ): Call<ResponseBody>


    //TODO: This api is remaining as per Laravel
    @GET("homeScreen")
    fun home(): Call<ResponseBody>

    @GET("countries")
    fun getCountries(): Call<ResponseBody>

    @GET("api/v1/deletePropertyFile/{id}")
    fun deletePropertyFile(
        @Header("Authorization") Authorization:String,
        @Path("id") id: String): Call<ResponseBody>

    @GET("api/v1/page/{id}")
    fun pages(
        @Path("id") id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/getInquiry")
    fun getInquiry(
        @Header("Authorization") Authorization: String,
        @Query("page") page: String,
        @Field("type") type: String
    ): Call<ResponseBody>

    @GET("api/v1/deleteInquiry/{id}")
    fun deleteInquiry(
        @Header("Authorization") Authorization: String,
        @Path("id") id: Int
    ): Call<ResponseBody>


    @GET("api/v1/dashboard")
    fun dashBoard(
        @Header("Authorization") Authorization: String,
    ): Call<ResponseBody>


    @GET("api/v1/features")
    fun features(
    ): Call<ResponseBody>

    @GET("api/v1/tags")
    fun tags(
    ): Call<ResponseBody>


    @GET("api/v1/propertyDetails/{id}")
    fun propertyDetails(
        @Path("id") id: Int
    ): Call<ResponseBody>


    @GET("api/v1/getCategory/{type}")
    fun getCategory(
        @Path("type") type: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("events")
    fun getEvents(
        @Query("page") page: Int,
        @Field("category_id") category_id: String,
        @Field("country_id") country_id: String,
        @Field("event_date") event_date: String,
        @Field("keyword") keyword: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/v1/user")
    fun updateprofile(
        @Header("Authorization") Authorization: String,
        @Field("name") name: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("website_link") website_link: String,
        @Field("twitter_link") twitter_link: String,
        @Field("instagram_link") instagram_link: String,
        @Field("linkedin_link") linkedin_link: String,
        @Field("address") address: String,
        @Field("biography") biography: String,
        @Field("mobile_no") mobile_no: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("facebook_link") facebook_link: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("buyPackage")
    fun buyPackage(
        @Header("Authorization") Authorization: String,
        @Field("package_id") package_id: String,
        @Field("transaction_id") transaction_id: String,
        @Field("organization_id") organization_id: String,
        @Field("payment_method") payment_method: String,
        @Field("sub_total") sub_total: String,
        @Field("service_charge_percentage") service_charge_percentage: String,
        @Field("service_charge") service_charge: String,
        @Field("total") total: String,
        @Field("start_date") start_date: String,
        @Field("adult") adult: String,
        @Field("child") child: String,
        @Field("language") language: String,
        @Field("start_time") start_time: String,
        @Field("end_time") end_time: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("address") address: String,
        @Field("adult_amount") adult_amount: String,
        @Field("child_amount") child_amount: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("contactUs")
    fun contactUs(
        @Field("name") name: String,
        @Field("message") message: String,
        @Field("mobile_no") mobile_no: String,
        @Field("email") email: String,
    ): Call<ResponseBody>

    @GET("packageDetails/{id}")
    fun getDetails(
        @Path("id") id: String
    ): Call<ResponseBody>

    @POST("items")
    fun getProducts(
        @Query("page") page: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("getPackageAvailability")
    fun getPackageAvailability(
        @Field("unique_package_id") unique_package_id: String,
        @Field("package_id") package_id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("packageCheckDate")
    fun checkAvailability(
        @Field("package_id") package_id: String,
        @Field("adult") adult: String,
        @Field("child") child: String
    ): Call<ResponseBody>

    @POST("packages")
    fun getPackages(
        @Query("page") page: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/forgotPassword")
    fun forgetPassword(
        @Field("email") email: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("order")
    fun eventOrder(
        @Header("Authorization") Authorization: String,
        @Field("currency") currency: String,
        @Field("sub_total") sub_total: Int,
        @Field("total") total: Int,
        @Field("transaction_id") transaction_id: String,
        @Field("payment_method") payment_method: String,
        @Field("service_charge_percentage") service_charge_percentage: String,
        @Field("service_charge") service_charge: String,
        @Field("card_number") card_number: String,
        @Field("card_holder_name") card_holder_name: String,
        @Field("expiry_date") expiry_date: String,
        @Field("merchant_reference") merchant_reference: String,
        @Field("authorization_code") authorization_code: String,
        @Field("response_code") response_code: String,
        @Field("payment_option") payment_option: String
    ): Call<ResponseBody>


    @POST("event/get_subscribed_organizers")
    fun getSubcribedOrganizers(
        @Body body: RequestBody
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cart")
    fun addEventToCart(
        @Field("type") type: String,
        @Field("event_id") event_id: String,
        @Field("quantity") quantity: Int,
        @Field("organization_id") organization_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("googleLogin")
    fun googleLogin(
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("google_id") google_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("email") email: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("facebookLogin")
    fun facebookLogin(
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("facebook_id") facebook_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("twitterLogin")
    fun twitterLogin(
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("twitter_id") twitter_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/properties")
    fun properties(
        @Query("page") page: Int,
        @Field("keyword") keyword: String,
        @Field("category_id") category_id: String,
        @Field("address") address: String,
        @Field("for_type") for_type: String,
        @Field("bedroom") bedroom: String,
        @Field("bathroom") bathroom: String,
        @Field("price_range_min") price_range_min: String,
        @Field("price_range_max") price_range_max: String,
        @Field("is_featured") is_featured: String,
        @Field("features[]") features: ArrayList<Int>,
        @Field("sortBy") sortBy: String,
        @Field("tags") tags: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cart")
    fun addItemToCart(
        @Field("type") type: String,
        @Field("package_id") item_id: String,
        @Field("quantity") quantity: Int,
        @Field("organization_id") organization_id: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cart")
    fun addPackageToCart(
        @Field("type") type: String,
        @Field("package_id") item_id: String,
        @Field("quantity") quantity: Int,
        @Field("organization_id") organization_id: Int,
    ): Call<ResponseBody>

    @PUT("cart/{id}")
    fun updateCart(
        @Path("id") id: Int,
        @Query("quantity") quantity: Int,
        @Query("cart_id") cart_id: Int,
    ): Call<ResponseBody>


    @DELETE("cart/{cart_id}")
    fun deleteCart(
        @Header("Authorization") Authorization: String,
        @Path("cart_id") cart_id: Int
    ): Call<ResponseBody>

    @GET("cart")
    fun getCart(
        @Header("Authorization") Authorization: String,
    ): Call<ResponseBody>

    @GET("order")
    fun getOrder(
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int

    ): Call<ResponseBody>


    @GET("order/{id}")
    fun getOrderDetails(
        @Header("Authorization") Authorization: String,
        @Path("id") id: Int

    ): Call<ResponseBody>


    @GET("api/v1/user")
    fun getProfile(
        @Header("Authorization") Authorization: String
    ): Call<ResponseBody>

    @POST("event/get_user_review")
    fun getUserReview(
        @Body body: RequestBody
    ): Call<ResponseBody>

    @POST("event/get_user_order")
    fun getUserOrder(
        @Body body: RequestBody
    ): Call<ResponseBody>

    @GET("event/{id}")
    fun getEventDetail(
        @Path("id") id: String,
        @Header("Authorization") Authorization: String
    ): Call<ResponseBody>

    @POST("packageDetails/{id}")
    fun getPackageDetails(
        @Path("id") id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("addRemoveFavouriteEvent")
    fun addRemoveFavouriteEvent(
        @Field("event_id") event_id: String
    ): Call<ResponseBody>


    @POST("event/subscribe_organizer")
    fun subscribeOrganiser(
        @Body body: RequestBody
    ): Call<ResponseBody>

    @POST("event/unsubscribe_organizer")
    fun unsubscribeOrganiser(
        @Body body: RequestBody
    ): Call<ResponseBody>

    //TODO: This api is remaining as per Laravel
    @POST("authentication/reset_password")
    fun resetPassword(
        @Body body: RequestBody
    ): Call<ResponseBody>

    //TODO: This api is remaining as per Laravel
    @FormUrlEncoded
    @POST("user")
    fun editProfile(
        @Header("Authorization") Authorization: String,
        @Field("name") name: String,
        @Field("country_code") country_code: String,
        @Field("mobile_no") mobile_no: String,
        @Field("dob") dob: String,
    ): Call<ResponseBody>

    @Multipart
    @POST("api/v1/changeProfileImage")
    fun changeProfileImage(
        @Header("Authorization") Authorization: String,
        @Part("old_image") old_image: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ResponseBody>


    @Multipart
    @POST("api/v1/addPropertyImage")
    fun addPropertyImage(
        @Header("Authorization") Authorization: String,
        @Part("property_id") property_id: RequestBody,
        @Part("type") type: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>

    @Multipart
    @POST("api/v1/addUpdateProperty")
    fun addUpdateProperty(
        @Header("Authorization") Authorization: String,
        @Part("edit_value") edit_value: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part("subcategory_id") subcategory_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("deal_type") deal_type: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("price_number") price_number: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("bathroom") bathroom: RequestBody,
        @Part("bedroom") bedroom: RequestBody,
        @Part("square_fit") square_fit: RequestBody,
        @Part("garage") garage: RequestBody,
        @Part("stories") stories: RequestBody,
        @Part("price_offer") price_offer: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("address") address: RequestBody,
        @Part("featured_property") featured_property: RequestBody,
        @Part("video_url") video_url: RequestBody,
        @Part("feature[]") feature: ArrayList<RequestBody>,
        @Part("tag[]") tag: ArrayList<RequestBody>,
        @Part("faqs[]") faqs: ArrayList<RequestBody>,
        @Part("faq_description[]") faq_description: ArrayList<RequestBody>,
        @Part profile_image: MultipartBody.Part?
    ): Call<ResponseBody>

    @GET("api/v1/getSubCategory/{id}")
    fun getSubCategory(
        @Path("id") id: String
    ): Call<ResponseBody>

    //TODO: This api is remaining as per Laravel
    @POST("getCalenderEvent")
    fun getCalendarEvents(
        @Header("Authorization") Authorization: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("addRemoveCalenderEvent")
    fun addRemoveCalenderEvent(
        @Header("Authorization") Authorization: String,
        @Field("event_id") event_id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/companyAndAgent")
    fun getAgentsList(
        @Field("order_by") order_by: String,
        @Query("page") page: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/companyAndAgentProperty")
    fun getAgentsData(
        @Field("agent_id") agent_id: String,
        @Query("page") page: String
    ): Call<ResponseBody>

    @GET("api/v1/getCategory/{type}")
    fun getCategoryList(
        @Path("type") type: String
    ): Call<ResponseBody>

    @GET("api/v1/getSubCategory/{id}")
    fun getSubCategoryList(
        @Path("id") type: String
    ): Call<ResponseBody>

    @GET("api/v1/features")
    fun getFeatures(
    ): Call<ResponseBody>


    @POST("api/v1/myProperty")
    fun getMyProperty(
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @POST("api/v1/addFavouriteProperty")
    fun addtoFav(
        @Header("Authorization") Authorization: String,
        @Query("property_id") property_id: Int
    ): Call<ResponseBody>

    @GET("api/v1/removeFavourite/{id}")
    fun removetoFav(
        @Header("Authorization") Authorization: String,
        @Path("id") id: Int

    ): Call<ResponseBody>

    @GET("api/v1/getFavouriteProperty")
    fun favList(
        @Header("Authorization") Authorization: String,
    ): Call<ResponseBody>

    @POST("api/v1/statusChangeProperty")
    fun changePropertyStatus(
        @Header("Authorization") Authorization: String,
        @Query("property_id") property_id: Int
    ): Call<ResponseBody>

    @POST("api/v1/deleteProperty")
    fun deleteProperty(
        @Header("Authorization") Authorization: String,
        @Query("property_id") property_id: Int
    ): Call<ResponseBody>




    //TODO: This api is remaining as per Laravel
    @POST("getFavouriteEvent")
    fun getFavouriteEvents(
        @Header("Authorization") Authorization: String
    ): Call<ResponseBody>
}