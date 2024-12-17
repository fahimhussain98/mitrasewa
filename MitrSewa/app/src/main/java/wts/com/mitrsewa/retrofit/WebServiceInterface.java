package wts.com.mitrsewa.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebServiceInterface {

    String BASE_URL = "https://fingpayap.tapits.in/fpaepsservice/api/";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();

    String Base_url2 = "https://fingpayap.tapits.in/fpaepsweb/api/";
    Retrofit retrofit2 = new Retrofit.Builder().baseUrl(Base_url2)
            .addConverterFactory(GsonConverterFactory.create()).build();

    String Base_url3 = "https://fingpayap.tapits.in/fpaepsweb/api/onboarding/get/";
    Retrofit Retrofitcompnaytype = new Retrofit.Builder().baseUrl(Base_url3)
            .addConverterFactory(GsonConverterFactory.create()).build();

    @FormUrlEncoded
    @POST("DMTCheckStatus")
    Call<JsonObject> pendingDmtCheckStatus(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("UniqueId") String UniqueId,
                                           @Field("commission") String commission,
                                           @Field("tds") String tds,
                                           @Field("surcharge") String surcharge,
                                           @Field("gst") String gst,
                                           @Field("amount") String amount);

    @FormUrlEncoded
    @POST("PendingDMTReport")
    Call<JsonObject> pendingDmtReport(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("UPICheckStatus")
    Call<JsonObject> upiCheckStatus(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("orderid") String orderid,
                                    @Field("status") String status);

    @FormUrlEncoded
    @POST("UserWalletToWalletReport")
    Call<JsonObject> walletToWalletReport(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("fromDate") String fromDate,
                                          @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("UpdateCallbackMATMFingpay")
    Call<JsonObject> checkMatmStatus(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("amount") String amount,
                                     @Field("merchantTranId") String merchantTranId,
                                     @Field("cardtype") String cardtype,
                                     @Field("transType") String transType);

    /*@FormUrlEncoded
    @POST("User-WalletToWallet")
    Call<JsonObject> doWalletToWalletTransaction(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("amount") String amount,
                                                 @Field("transferto") String transferto);*/

    @FormUrlEncoded
    @POST("WalletToWallet")
    Call<JsonObject> doWalletToWalletTransaction(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("creditUserId") String creditUserId,
                                                 @Field("fromWallet") String fromWallet,
                                                 @Field("toWallet") String toWallet,
                                                 @Field("amount") String amount,
                                                 @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("GetUserByMobileNo")
    Call<JsonObject> getUserDetails(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("MobileNo") String MobileNo);

    @FormUrlEncoded
    @POST("ExpressDmtCheckStatus")
    Call<JsonObject> expressDmtCheckStatus(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("orderid") String orderid,
                                           @Field("TxnDate") String TxnDate,
                                           @Field("transactiontype") String transactiontype);

    @FormUrlEncoded
    @POST("ComplaintStatusReport")
    Call<JsonObject> makeComplaintReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("UserUpiReport")
    Call<JsonObject> upiReport(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("fromDate") String fromDate,
                               @Field("toDate") String toDate);


    @FormUrlEncoded
    @POST("DoComplain")
    Call<JsonObject> makeComplaint(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("UniqueId") String UniqueId,
                                   @Field("remarks") String remarks,
                                   @Field("ComplainType") String ComplainType,
                                   @Field("LiveId") String LiveId,
                                   @Field("serviceName") String serviceName);

    @FormUrlEncoded
    @POST("RechargeCheckStatus")
    Call<JsonObject> rechargeCheckStatus(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("UniqueId") String UniqueId,
                                   @Field("commission") String commission,
                                   @Field("tds") String tds,
                                   @Field("surcharge") String surcharge,
                                   @Field("gst") String gst,
                                   @Field("amount") String amount
    );

    @FormUrlEncoded
    @POST("GetQrReceipt")
    Call<JsonObject> getQrReceipt(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("txnid") String txnid);

    @FormUrlEncoded
    @POST("GetSubdivionCode")
    Call<JsonObject> getSubDivisionCode(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("UPIQrCodeGenerate")
    Call<JsonObject> doUpiQrTransaction(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("Name") String Name,
                                        @Field("UPIId") String UPIId,
                                        @Field("MobileNo") String MobileNo,
                                        @Field("Amount") String Amount,
                                        @Field("Mode") String Mode);

    @FormUrlEncoded
    @POST("Generate-Security")
    Call<JsonObject> generateMpin(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("securityType") String securityType,
                                  @Field("securityPin") String securityPin);

    @FormUrlEncoded
    @POST("GetOtpSecurity")
    Call<JsonObject> getOtp(@Header("Authorization") String auth,
                            @Field("userID") String userID,
                            @Field("tokenKey") String tokenKey,
                            @Field("deviceInfo") String deviceInfo,
                            @Field("emailId") String emailId,
                            @Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("ValidateAuthentication")
    Call<JsonObject> validateAuthentication(@Header("Authorization") String auth,
                            @Field("userID") String userID,
                            @Field("tokenKey") String tokenKey,
                            @Field("deviceInfo") String deviceInfo,
                            @Field("authenticateCode") String authenticateCode);

    @FormUrlEncoded
    @POST("CheckAuthentication")
    Call<JsonObject> checkAuthentication(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo);


    @FormUrlEncoded
    @POST("DMTaccountVarify")
    Call<JsonObject> verifyAccount(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("remitterId") String remitterId,
                                   @Field("bankName") String bankName,
                                   @Field("beneFirstName") String beneFirstName,
                                   @Field("beneLastName") String beneLastName,
                                   @Field("mobileNo") String mobileNo,
                                   @Field("ifscCode") String ifscCode,
                                   @Field("accountNo") String accountNo,
                                   @Field("senderMobileNo") String senderMobileNo,
                                   @Field("address") String address,
                                   @Field("pinCode") String pinCode,
                                   @Field("otp") String otp,
                                   @Field("senderName") String senderName,
                                   @Field("transactionMode") String transactionMode);

    @FormUrlEncoded
    @POST("ExpressDmtAccountVerification")
    Call<JsonObject> verifyExpressAccount(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("remitterId") String remitterId,
                                          @Field("bankName") String bankName,
                                          @Field("mobileNo") String mobileNo,
                                          @Field("ifscCode") String ifscCode,
                                          @Field("accountNo") String accountNo,
                                          @Field("senderMobileNo") String senderMobileNo,
                                          @Field("senderName") String senderName);

    @FormUrlEncoded
    @POST("GetSender")
    Call<JsonObject> isSenderValidate(@Header("Authorization") String auth,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("userID") String userid,
                                      @Field("senderMobileNo") String senderMobileNo);
    @FormUrlEncoded
    @POST("GetSenderWithEKYC")
    Call<JsonObject> GetSenderWithEKYC(@Header("Authorization") String auth,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("userID") String userid,
                                      @Field("senderMobileNo") String senderMobileNo);

    @FormUrlEncoded
    @POST("ExpressDmtGetSender")
    Call<JsonObject> isSenderValidateExpress(@Header("Authorization") String auth,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("userID") String userID,
                                             @Field("senderMobileNo") String senderMobileNo);

    @FormUrlEncoded
    @POST("BeneRegistration")
    Call<JsonObject> addBeneficiary(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("remitterId") String remitterId,
                                    @Field("bankName") String bankName,
                                    @Field("beneFirstName") String beneFirstName,
                                    @Field("beneLastName") String beneLastName,
                                    @Field("mobileNo") String mobileNo,
                                    @Field("ifscCode") String ifscCode,
                                    @Field("accountNo") String accountNo,
                                    @Field("senderMobileNo") String senderMobileNo,
                                    @Field("address") String address,
                                    @Field("pinCode") String pinCode,
                                    @Field("otp") String otp);

    @FormUrlEncoded
    @POST("ExpressDmtBeneRegistration")
    Call<JsonObject> addBeneficiaryExpress(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("remitterId") String remitterId,
                                           @Field("bankName") String bankName,
                                           @Field("beneName") String beneName,
                                           @Field("beneLastName") String beneLastName,
                                           @Field("mobileNo") String mobileNo,
                                           @Field("ifscCode") String ifscCode,
                                           @Field("accountNo") String accountNo,
                                           @Field("senderMobileNo") String senderMobileNo,
                                           @Field("address") String address,
                                           @Field("pinCode") String pinCode,
                                           @Field("otp") String otp);

    @FormUrlEncoded
    @POST("GetBankListForDMT")
    Call<JsonObject> getBankDmt(@Header("Authorization") String auth,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("userID") String userID);

    @FormUrlEncoded
    @POST("TransferMoney")
    Call<JsonObject> payBeneficiary(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("txnAmount") String txnAmount,
                                    @Field("beneficiaryId") String beneficiaryId,
                                    @Field("outletId") String outletId,
                                    @Field("senderName") String senderName,
                                    @Field("senderMobileNo") String senderMobileNo,
                                    @Field("accountNo") String accountNo,
                                    @Field("beneficiaryName") String beneficiaryName,
                                    @Field("bankName") String bankName,
                                    @Field("ifscCode") String ifscCode,
                                    @Field("beneMobileNo") String beneMobileNo,
                                    @Field("transactionMode") String transactionMode,
                                    @Field("bankId") String bankId);

    @FormUrlEncoded
    @POST("ExpressDmtMoneyTransfer")
    Call<JsonObject> payBeneficiaryExpress(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("txnAmount") String txnAmount,
                                           @Field("beneficiaryId") String beneficiaryId,
                                           @Field("senderName") String senderName,
                                           @Field("senderMobileNo") String senderMobileNo,
                                           @Field("transactionMode") String transactionMode);

    @FormUrlEncoded
    @POST("DeleteBeneficiary")
    Call<JsonObject> deleteBeneficiary(@Header("Authorization") String auth,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("userID") String userID,
                                       @Field("beneficiaryId") String beneficiaryId,
                                       @Field("senderMobileNo") String senderMobileNo,
                                       @Field("remitterId") String remitterId);

    @FormUrlEncoded
    @POST("ExpressDmtDeleteBeneficiary")
    Call<JsonObject> deleteBeneficiaryExpress(@Header("Authorization") String auth,
                                              @Field("tokenKey") String tokenKey,
                                              @Field("deviceInfo") String deviceInfo,
                                              @Field("userID") String userID,
                                              @Field("beneid") String beneid);

    @FormUrlEncoded
    @POST("AddSender")
    Call<JsonObject> addSender(@Header("Authorization") String auth,
                               @Field("tokenKey") String token,
                               @Field("deviceInfo") String DeviceInfo,
                               @Field("userID") String userID,
                               @Field("senderMobileNo") String sendermobileno,
                               @Field("firstName") String firstname,
                               @Field("lastName") String lastname,
                               @Field("pinCode") String pincode,
                               @Field("address") String address,
                               @Field("otp") String otp);

    @FormUrlEncoded
    @POST("ExpressDmtRemitterRegistration")
    Call<JsonObject> addSenderExpress(@Header("Authorization") String auth,
                                      @Field("tokenKey") String token,
                                      @Field("deviceInfo") String DeviceInfo,
                                      @Field("userID") String userID,
                                      @Field("senderMobileNo") String senderMobileNo,
                                      @Field("Name") String Name,
                                      @Field("lastName") String lastname,
                                      @Field("pinCode") String pinCode,
                                      @Field("address") String address,
                                      @Field("otp") String otp);

    @FormUrlEncoded
    @POST("SenderOtpVarify")
    Call<JsonObject> verifySender(@Header("Authorization") String auth,
                                  @Field("tokenKey") String token,
                                  @Field("deviceInfo") String DeviceInfo,
                                  @Field("remitterId") String remitterId,
                                  @Field("userID") String userID,
                                  @Field("senderMobileNo") String senderMobileNo,
                                  @Field("otp") String otp);


    @FormUrlEncoded
    @POST("BbpsBillPay")
    Call<JsonObject> payBill(@Header("Authorization") String auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("ServiceType") String ServiceType,
                             @Field("operatorId") String operatorId,
                             @Field("consumerNumber") String consumerNumber,
                             @Field("mobileNo") String mobileNo,
                             @Field("consumerName") String consumerName,
                             @Field("billAmt") String billAmt,
                             @Field("dueDate") String dueDate,
                             @Field("serviceId") String serviceId);
    @FormUrlEncoded
    @POST("WtsBillPay")
    Call<JsonObject> WtsBillPay(@Header("Authorization") String auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("ServiceType") String ServiceType,
                             @Field("operatorId") String operatorId,
                             @Field("consumerNumber") String consumerNumber,
                             @Field("mobileNo") String mobileNo,
                             @Field("consumerName") String consumerName,
                             @Field("billAmt") String billAmt,
                             @Field("dueDate") String dueDate,
                             @Field("serviceId") String serviceId);

    @FormUrlEncoded
    @POST("UserUtilityReport")
    Call<JsonObject> getBBPSReport(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("fromDate") String fromDate,
                                   @Field("toDate") String toDate,
                                   @Field("ServiceName") String ServiceName);

    @FormUrlEncoded
    @POST("BbpsBillFetch")
    Call<JsonObject> fetchBill(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("ServiceType") String ServiceType,
                               @Field("serviceId") String serviceId,
                               @Field("operatorId") String operatorId,
                               @Field("consumerNumber") String consumerNumber,
                               @Field("mobileNo") String mobileNo,
                               @Field("subDivisionCode") String subDivisionCode);
    @FormUrlEncoded
    @POST("WtsBillFetch")
    Call<JsonObject> fetchBill_wts(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("ServiceType") String ServiceType,
                               @Field("serviceId") String serviceId,
                               @Field("operatorId") String operatorId,
                               @Field("consumerNumber") String consumerNumber,
                               @Field("mobileNo") String mobileNo,
                               @Field("subDivisionCode") String subDivisionCode);



    @FormUrlEncoded
    @POST("GetSecurityMpin")
    Call<JsonObject> checkMpinOrTPIN(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("securityType") String securityType,
                                     @Field("securityPin") String securityPin);

    @FormUrlEncoded
    @POST("FingPayUserOnBoardBioMetric")
    Call<JsonObject> doUserBioMetricVerification(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("aadharno") String aadharno,
                                                 @Field("fingerdata") String fingerdata);
    @FormUrlEncoded
    @POST("DoRemitterEKYC")
    Call<JsonObject> DoRemitterEKYC(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("senderMobileNo") String senderMobileNo,
                                                 @Field("aadhaar_number") String aadhaar_number,
                                                 @Field("pidData") String pidData,
                                                 @Field("latitude") String latitude,
                                                 @Field("longitude") String longitude
    );


    /*@FormUrlEncoded
    @POST("FingPayUserOnBoard")
    Call<JsonObject> doFingPayUserOnBoard(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude,
                                          @Field("merchantname") String merchantname,
                                          @Field("merchantmobileno") String merchantmobileno,
                                          @Field("companylegalname") String companylegalname,
                                          @Field("emailid") String emailid,
                                          @Field("merchantpincode") String merchantpincode,
                                          @Field("merchantcityname") String merchantcityname,
                                          @Field("merchantdistrictname") String merchantdistrictname,
                                          @Field("merchantaddress") String merchantaddress,
                                          @Field("userpan") String userpan,
                                          @Field("aadharno") String aadharno);*/

    @FormUrlEncoded
    @POST("FingPayUserOnBoard")
    Call<JsonObject> doFingPayUserOnBoard(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude,
                                          @Field("merchantFirstname") String merchantFirstname,
                                          @Field("merchantLastName") String merchantLastName,
                                          @Field("merchantmobileno") String mobile,
                                          @Field("companylegalname") String company,
                                          @Field("emailid") String emailId,
                                          @Field("merchantpincode") String pinCode,
                                          @Field("merchantState") String merchantstate,
                                          @Field("merchantcityname") String city,
                                          @Field("merchantdistrictname") String merchantdistrictname,
                                          @Field("merchantaddress") String merchantaddress,
                                          @Field("userpan") String userpan,
                                          @Field("aadharno") String aadharno,
                                          @Field("bankName") String bankName,
                                          @Field("accountNo") String accountNo,
                                          @Field("ifscCode") String ifscCode,
                                          @Field("companyType") String companyType,
                                          @Field("certificateOfincorporation") String certificateOfincorporation,
                                          @Field("physicalVerification") String physicalVerification,
                                          @Field("cancelChequeImages") String cancelChequeImages,
                                          @Field("termConditionsCheck") String termConditionsCheck,
                                          @Field("videoVerificationwithLatLong") String videoVerificationwithLatLong,
                                          @Field("tradeBusinessProof") String tradeBusinessProof,
                                          @Field("shopImageTrueOrFalse") String shopImageTrueOrFalse,
                                          @Field("aadharFront") String aadharFront,
                                          @Field("aadharBack") String aadharBack,
                                          @Field("passbook") String passbook,
                                          @Field("pancardImage") String pancardImage,
                                          @Field("shopImage") String shopImage);

    @FormUrlEncoded
    @POST("FingPayUserOnBoardValidateApi")
    Call<JsonObject> verifyOnboardOtp(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("primaryKeyId") String primaryKeyId,
                                      @Field("encodeFPTxnId") String encodeFPTxnId,
                                      @Field("otp") String otp);

    @FormUrlEncoded
    @POST("FingPayAePSCheckStatus")
    Call<JsonObject> checkEKycStatus(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo);


    @FormUrlEncoded
    @POST("DoFingPayAepsTransaction")
    Call<JsonObject> doAepsTransaction(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("aadharno") String aadharno,
                                       @Field("bankiinno") String bankiinno,
                                       @Field("mobileno") String mobileno,
                                       @Field("amount") String amount,
                                       @Field("txntype") String txntype,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude,
                                       @Field("fingerdata") String fingerdata,
                                       @Field("appversion") String appVersion);

    @FormUrlEncoded
    @POST("Do2FAForCashWithdrawal")
    Call<JsonObject> checkCW2FA(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("fingerData") String fingerData,
                                @Field("latitude") String latitude,
                                @Field("longitude") String longitude,
                                @Field("aadharNo") String aadharNo,
                                @Field("mobileNo") String mobileNo,
                                @Field("serviceType") String serviceType

    );


    @GET("bankdata/bank/details")
    Call<JsonObject> getFingPayBankDetails();
    @GET("onboarding/getstates")//onboarding/getstates
    Call<JsonArray> getmerchantState();
    @GET("companyType/master")//companyType/master
    Call<JsonObject> getCompnayType();



    @POST("cashWithdrawal/merchant/v2/withdrawal")
    Call<JsonObject> doAepsCashWithdrawal(@Header("trnTimestamp") String timeStamp,
                                          @Header("hash") String hash,
                                          @Header("deviceIMEI") String deviceIMEI,
                                          @Body JSONObject requestObject
    );


    @FormUrlEncoded
    @POST("GetOnboardUserDetails")
    Call<JsonObject> getUserFingPayCredentials(@Header("Authorization") String Auth,
                                               @Field("userID") String userID,
                                               @Field("tokenKey") String tokenKey,
                                               @Field("deviceInfo") String deviceInfo,
                                               @Field("action") String action
    );

    @FormUrlEncoded
    @POST("MATMReport")
    Call<JsonObject> getMatmReport(@Header("Authorization") String Auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("txnType") String txnType,
                                   @Field("fromDate") String fromDate,
                                   @Field("toDate") String toDate
    );

    @FormUrlEncoded
    @POST("MatmCallBack")
    Call<JsonObject> fingpayMatmCallback(@Header("Authorization") String Auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("response") String response,
                                         @Field("appversion") String appVersion);

    @FormUrlEncoded
    @POST("MAtmCredential")
    Call<JsonObject> getMATMCredential(@Header("Authorization") String Auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("CMSReport")
    Call<JsonObject> getCmsReport(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("fromDate") String fromDate,
                                  @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("GetSchemeList")
    Call<JsonObject> getScheme(@Header("Authorization") String Auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("action") String action,
                               @Field("userType") String userType,
                               @Field("iD") String iD);

    @FormUrlEncoded
    @POST("AddUsers")
    Call<JsonObject> addUser(@Header("Authorization") String Auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("mobileNo") String mobileNo,
                             @Field("password") String password,
                             @Field("name") String name,
                             @Field("userType") String userType,
                             @Field("emailID") String emailID,
                             @Field("dob") String dob,
                             @Field("companyName") String companyName,
                             @Field("address") String address,
                             @Field("stateID") String stateID,
                             @Field("cityID") String cityID,
                             @Field("pancardNo") String pancardNo,
                             @Field("aadharNo") String aadharNo,
                             @Field("schemeID") String schemeID,
                             @Field("capBal") String capBal);

    @Multipart
    @POST("FileUploading/UploadFile")
    Call<JsonObject> uploadfile(@Header("Authorization") String auth,
                                @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("GetBankDetails")
    Call<JsonObject> getBankList(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("FundRequest")
    Call<JsonObject> doPaymentRequest(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("requestDate") String requestDate,
                                      @Field("referenceNo") String referenceNo,
                                      @Field("bankName") String bankName,
                                      @Field("paymentMode") String paymentMode,
                                      @Field("remark") String remark,
                                      @Field("imagePath") String imagePath,
                                      @Field("requestTo") String requestTo,
                                      @Field("amount") String amount);

    @FormUrlEncoded
    @POST("CreditReport")
    Call<JsonObject> getCreditReport(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("searchBy") String searchBy,
                                     @Field("fromDate") String fromDate,
                                     @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("DebitReport")
    Call<JsonObject> getDebitReport(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("searchBy") String searchBy,
                                    @Field("fromDate") String fromDate,
                                    @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("GetChieldList")
    Call<JsonObject> getUsers(@Header("Authorization") String auth,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo,
                              @Field("userID") String userid,
                              @Field("action") String action);

    @FormUrlEncoded
    @POST("DebitAmount")
    Call<JsonObject> doDebitBalance(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("amount") String amount,
                                    @Field("chieldID") String chieldID);

    @FormUrlEncoded
    @POST("CreditAmount")
    Call<JsonObject> doCreditBalance(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("amount") String amount,
                                     @Field("chieldID") String chieldID);

    @FormUrlEncoded
    @POST("Add-Payoutbank")
    Call<JsonObject> addbankDetails(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("BankName") String BankName,
                                    @Field("AccountNo") String AccountNo,
                                    @Field("AccHolderName") String AccHolderName,
                                    @Field("IfscCode") String IfscCode);


    @POST("GetAepsBank")
    Call<JsonObject> GetAepsBank(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("MplanMobileSimplePlan")
    Call<JsonObject> getPlans(@Field("userID") String userID,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo,
                              @Field("operatorName") String operatorName,
                              @Field("circle") String circle);

    @FormUrlEncoded
    @POST("MplanGetCustomerInfo")
    Call<JsonObject> getDthUserInfo(@Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("operatorName") String operatorName,
                                    @Field("dthNo") String dthNo);

    @FormUrlEncoded
    @POST("MplanGetDthPlan")
    Call<JsonObject> getDthMplan(@Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("operatorName") String operatorName);

    @FormUrlEncoded
    @POST("MplanMobileSpecialPlan")
    Call<JsonObject> getMyPlans(@Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("operatorName") String operatorName,
                                @Field("mobileNumber") String mobileNumber);

    @FormUrlEncoded
    @POST("MPlanGetMnp")
    Call<JsonObject> getOperatorCircle(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("mobileNumber") String mobileNumber
    );

    @FormUrlEncoded
    @POST("UserMoneyTransferReport")
    Call<JsonObject> getDmtReport(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("fromDate") String fromDate,
                                  @Field("toDate") String toDate,
                                  @Field("Accountno") String Accountno);

    @FormUrlEncoded
    @POST("UserExpressDmtReport")
    Call<JsonObject> getExpressDmtReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate,
                                         @Field("Accountno") String Accountno);

    @FormUrlEncoded
    @POST("DmtRefundReport")
    Call<JsonObject> getDmtRefundReport(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("DmtRefund")
    Call<JsonObject> doDmtRefund(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("referenceid") String referenceid,
                                 @Field("ackno") String ackno,
                                 @Field("tds") String Tds);

    @FormUrlEncoded
    @POST("DmtRefundOtpValidate")
    Call<JsonObject> validateRefundOtp(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("referenceid") String referenceid,
                                       @Field("ackno") String ackno,
                                       @Field("otp") String otp,
                                       @Field("amount") String amount,
                                       @Field("surcharge") String surcharge,
                                       @Field("gst") String gst,
                                       @Field("tds") String tds
    );

    @FormUrlEncoded
    @POST("UpdateSettlementCheckStatus")
    Call<JsonObject> doSettlementCheckStatus(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("orderid") String orderid,
                                             @Field("TxnDate") String TxnDate,
                                             @Field("transactiontype") String transactiontype,
                                             @Field("apiName") String apiName
    );

    @FormUrlEncoded
    @POST("FingPayCheckStatus")
    Call<JsonObject> doAepsCheckStatus(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("orderId") String orderid,
                                             @Field("serviceType") String transactiontype
    );


    @FormUrlEncoded
    @POST("GetStateList")
    Call<JsonObject> getState(@Header("Authorization") String Auth,
                              @Field("userID") String userID,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("GetCityList")
    Call<JsonObject> getCity(@Header("Authorization") String Auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("stateID") String stateID);

    @FormUrlEncoded
    @POST("SignUp")
    Call<JsonObject> signUp(@Header("Authorization") String auth,
                            @Field("name") String name,
                            @Field("companyname") String companyname,
                            @Field("mobileno") String mobileno,
                            @Field("emailid") String emailid,
                            @Field("aadharno") String aadharno,
                            @Field("panno") String panno,
                            @Field("address") String address,
                            @Field("statename") String statename,
                            @Field("stateid") String stateid,
                            @Field("cityname") String cityname,
                            @Field("cityid") String cityid,
                            @Field("pincode") String pincode,
                            @Field("usertype") String usertype);

    @FormUrlEncoded
    @POST("ChangePassword")
    Call<JsonObject> changePassword(@Header("Authorization") String auth,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("userID") String userID,
                                    @Field("oldPassword") String oldPassword,
                                    @Field("newPassword") String newPassword);


    @FormUrlEncoded
    @POST("verifyotp1")
    Call<JsonObject> sendOtp(@Header("Authorization") String auth,
                             @Field("userid") String userid);

    @FormUrlEncoded
    @POST("myCommission")
    Call<JsonObject> getMyCommission(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo);


    @FormUrlEncoded
    @POST("ForgetPassword")
    Call<JsonObject> forgetPassword(@Header("Authorization") String auth,
                                    @Field("userName") String username,
                                    @Field("mobileNo") String mobileno);

    @FormUrlEncoded
    @POST("ForgotMpin")
    Call<JsonObject> forgetMpin(@Header("Authorization") String auth,
                                @Field("username") String username,
                                @Field("mobileno") String mobileno);

    @FormUrlEncoded
    @POST("GetWtsSettlementReport")
    Call<JsonObject> getSettlementReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("MainLedger")
    Call<JsonObject> getLedgerReport(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("Accountno") String Accountno,
                                     @Field("fromDate") String fromDate,
                                     @Field("toDate") String toDate
    );

    @FormUrlEncoded
    @POST("UserAepsLedgerReport")
    Call<JsonObject> getAepsLedgerReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("Accountno") String Accountno,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("UserCommissionLedger")
    Call<JsonObject> getCommisssionLedgerReport(@Header("Authorization") String auth,
                                                @Field("userID") String userID,
                                                @Field("tokenKey") String tokenKey,
                                                @Field("deviceInfo") String deviceInfo,
                                                @Field("Accountno") String Accountno,
                                                @Field("fromDate") String fromDate,
                                                @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("GetUserAepsReport")
    Call<JsonObject> getAepsReport(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("fromDate") String fromDate,
                                   @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("SettlementBankMaster")
    Call<JsonObject> getAccountDetails(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("Composit-Settlement")
    Call<JsonObject> moveToBank(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("WalletType") String WalletType,
                                @Field("Amount") String Amount,
                                @Field("AccountNo") String AccountNo,
                                @Field("ifscCode") String ifscCode,
                                @Field("AccountType") String AccountType,
                                @Field("TransactionType") String TransactionType,
                                @Field("AcHolderName") String AcHolderName,
                                @Field("BankName") String BankName,
                                @Field("UpiId") String UpiId);

    @FormUrlEncoded
    @POST("InstantPay-Settlement")
    Call<JsonObject> moveToBank2(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("WalletType") String WalletType,
                                @Field("Amount") String Amount,
                                @Field("AccountNo") String AccountNo,
                                @Field("ifscCode") String ifscCode,
                                @Field("AccountType") String AccountType,
                                @Field("TransactionType") String TransactionType,
                                @Field("AcHolderName") String AcHolderName,
                                @Field("BankName") String BankName,
                                @Field("UpiId") String UpiId,
                                @Field("latitude") String latitude,
                                @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("CommissionWalletWidthdraw")
    Call<JsonObject> moveToBank3(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("WalletType") String WalletType,
                                 @Field("Amount") String Amount,
                                 @Field("AccountNo") String AccountNo,
                                 @Field("ifscCode") String ifscCode,
                                 @Field("AccountType") String AccountType,
                                 @Field("TransactionType") String TransactionType,
                                 @Field("AcHolderName") String AcHolderName,
                                 @Field("BankName") String BankName,
                                 @Field("UpiId") String UpiId,
                                 @Field("latitude") String latitude,
                                 @Field("longitude") String longitude,
                                 @Field("ApiId") String ApiId
    );

    @FormUrlEncoded
    @POST("CommissionWalletWidthdraw")
    Call<JsonObject> withdrawCommission(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("WalletType") String WalletType,
                                        @Field("Amount") String Amount,
                                        @Field("AccountNo") String AccountNo,
                                        @Field("ifscCode") String ifscCode,
                                        @Field("AccountType") String AccountType,
                                        @Field("TransactionType") String TransactionType,
                                        @Field("AcHolderName") String AcHolderName,
                                        @Field("BankName") String BankName);


    @FormUrlEncoded
    @POST("RechargeReport")
    Call<JsonObject> getReport(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("userType") String userType,
            @Field("amountWise") String amountWise,
            @Field("statusWise") String statusWise,
            @Field("modeWise") String modeWise,
            @Field("mobileNo") String mobileNo,
            @Field("fromDate") String fromDate,
            @Field("toDate") String toDate,
            @Field("parentWise") String parentWise,
            @Field("operatorWise") String operatorWise);

    @FormUrlEncoded
  //  @POST("UserLogin")
    @POST("DemoLogin")
    Call<JsonObject> login(
            @Header("Authorization") String auth,
            @Field("userName") String userName,
            @Field("password") String password,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("appversion") String appVersion,
             @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("deviceName") String deviceName
    );

    @FormUrlEncoded
    @POST("CheckCredientials")
    Call<JsonObject> checkCredential(@Header("Authorization") String auth,
                                     @Field("userName") String userName,
                                     @Field("password") String password,
                                     @Field("deviceID") String deviceID,
                                     @Field("appversion") String appversion);

    @FormUrlEncoded
    @POST("GetService")
    Call<JsonObject> getServices(
            @Header("Authorization") String auth,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("userID") String userID);

    @FormUrlEncoded
    @POST("GetOperatorsList")
    Call<JsonObject> getOperators(@Header("Authorization") String auth,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("userID") String userID,
                                  @Field("serviceID") String serviceID);

    @POST("GetCircle")
    Call<JsonObject> getCircle(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("DoDTHRecharge")
    Call<JsonObject> doDthRecharge(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("MobileNo") String MobileNo,
            @Field("ServiceType") String ServiceType);

    @FormUrlEncoded
    @POST("DoRecharge")
    Call<JsonObject> doRecharge(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("MobileNo") String MobileNo,
            @Field("ServiceType") String ServiceType);


    @FormUrlEncoded
    @POST("GetUserBalance")
    Call<JsonObject> getBalance(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("action") String action,
            @Field("balUserID") String balUserID);

    @FormUrlEncoded
    @POST("GetUserAepsBalance")
    Call<JsonObject> getAepsBalance(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("action") String action,
            @Field("balUserID") String balUserID);

    @FormUrlEncoded
    @POST("GetUserAepsCommissionWallet")
    Call<JsonObject> getCommissionBalance(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("action") String action,
            @Field("balUserID") String balUserID);

    //Check Two Factor Authentication

    @FormUrlEncoded
    @POST("CheckTwofactorAuthentication")
    Call<JsonObject> checkTwoFactorAuthStatus(@Header("Authorization") String auth,
                                              @Field("userID") String userID,
                                              @Field("tokenKey") String tokenKey,
                                              @Field("deviceInfo") String deviceInfo,
                                              @Field("serviceType") String serviceType
    );

    @FormUrlEncoded
    @POST("TwoFAuthentication")
    Call<JsonObject> twoFactorAuthentication(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("aadharNo") String aadharNo,
                                             @Field("mobileNo") String mobileNo,
                                             @Field("fingerData") String fingerData,
                                             @Field("latitude") String latitude,
                                             @Field("longitude") String longitude,
                                             @Field("serviceType") String serviceType
    );

/////////////////////////////////////////////////////////

    @FormUrlEncoded
    @POST("GetOtherServices")
    Call<JsonObject> getOtherServices(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo
    );

}
