
package com.lizhming.OneStoreIAP;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ObjectAlreadyConsumedException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.onestore.iap.api.IapEnum;
import com.onestore.iap.api.IapResult;
import com.onestore.iap.api.ProductDetail;
import com.onestore.iap.api.PurchaseClient;
import com.onestore.iap.api.PurchaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RNOneStoreIapModule extends ReactContextBaseJavaModule implements ActivityEventListener {
  private static final String TAG = RNOneStoreIapModule.class.getSimpleName();
  private static final int PURCHASE_REQUEST_CODE = 1022;
  private static final int LOGIN_REQUEST_CODE = 1023;
  private final ReactApplicationContext reactContext;
  private PurchaseClient mPurchaseClient;

  // 원스토어 인앱결제 API 버전
  private static final int IAP_API_VERSION = 5;

  public RNOneStoreIapModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
    return "RNOneStoreIap";
  }

  @ReactMethod
  public void loadPurchases(String type, final Promise promise) {
    /*
     * PurchaseClient의 queryPurchasesAsync API (구매내역조회) 콜백 리스너
     */
    PurchaseClient.QueryPurchaseListener mQueryPurchaseListener = new PurchaseClient.QueryPurchaseListener() {
      @Override
      public void onSuccess(List<PurchaseData> purchaseDataList, String productType) {
        Log.d(TAG, "queryPurchasesAsync onSuccess, " + purchaseDataList.toString());

        WritableNativeArray items = new WritableNativeArray();
        if (IapEnum.ProductType.IN_APP.getType().equalsIgnoreCase(productType)) {
          for (PurchaseData purchaseData : purchaseDataList) {
            WritableMap item = Arguments.createMap();
            item.putString("productId", purchaseData.getProductId());
            item.putString("DeveloperPayload", purchaseData.getDeveloperPayload());
            item.putString("OrderId", purchaseData.getOrderId());
            item.putString("PackageName", purchaseData.getPackageName());
            item.putString("PurchaseData", purchaseData.getPurchaseData());
            item.putString("PurchaseId", purchaseData.getPurchaseId());
            item.putString("Signature", purchaseData.getSignature());
            item.putInt("PurchaseState", purchaseData.getPurchaseState());
            item.putString("PurchaseTime", "" + purchaseData.getPurchaseTime());
            item.putInt("RecurringState", purchaseData.getRecurringState());
            item.putString("ProductType", "inapp");
            items.pushMap(item);
          }

        } else if (IapEnum.ProductType.AUTO.getType().equalsIgnoreCase(productType)) {
          for (PurchaseData purchaseData : purchaseDataList) {
            WritableMap item = Arguments.createMap();
            item.putString("productId", purchaseData.getProductId());
            item.putString("DeveloperPayload", purchaseData.getDeveloperPayload());
            item.putString("OrderId", purchaseData.getOrderId());
            item.putString("PackageName", purchaseData.getPackageName());
            item.putString("PurchaseData", purchaseData.getPurchaseData());
            item.putString("PurchaseId", purchaseData.getPurchaseId());
            item.putString("Signature", purchaseData.getSignature());
            item.putInt("PurchaseState", purchaseData.getPurchaseState());
            item.putString("PurchaseTime", "" + purchaseData.getPurchaseTime());
            item.putInt("RecurringState", purchaseData.getRecurringState());
            item.putString("ProductType", "auto");
            items.pushMap(item);
          }
        }

        try {
          promise.resolve(items);
        } catch (ObjectAlreadyConsumedException oce) {
          Log.e(TAG, oce.getMessage());
        }
      }

      @Override
      public void onErrorRemoteException() {
        Log.e(TAG, "queryPurchasesAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
        promise.reject("queryPurchasesAsync", "queryPurchasesAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
      }

      @Override
      public void onErrorSecurityException() {
        Log.e(TAG, "queryPurchasesAsync onError, 비정상 앱에서 결제가 요청되었습니다");
        promise.reject("queryPurchasesAsync", "queryPurchasesAsync onError, 비정상 앱에서 결제가 요청되었습니다");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "queryPurchasesAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        promise.reject("queryPurchasesAsync", "queryPurchasesAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }

      @Override
      public void onError(IapResult result) {
        Log.e(TAG, "queryPurchasesAsync onError, " + result.toString());
        promise.reject("queryPurchasesAsync", "queryPurchasesAsync onError, " + result.toString());
      }
    };
    String productType = IapEnum.ProductType.IN_APP.getType(); // "inapp"
    if (type.equalsIgnoreCase("inapp")) {
      productType = IapEnum.ProductType.IN_APP.getType();
    }
    else if (type.equalsIgnoreCase("auto")) {
      productType = IapEnum.ProductType.AUTO.getType();
    }
    else if (type.equalsIgnoreCase("all")) {
//      productType = IapEnum.ProductType.ALL.getType();
    }
    mPurchaseClient.queryPurchasesAsync(IAP_API_VERSION, productType, mQueryPurchaseListener);
  }
  private void checkBilling(final Promise promise) {
    /*
     * PurchaseClient의 isBillingSupportedAsync (지원여부조회) API 콜백 리스너
     */
    PurchaseClient.BillingSupportedListener mBillingSupportedListener = new PurchaseClient.BillingSupportedListener() {

      @Override
      public void onSuccess() {
        Log.d(TAG, "isBillingSupportedAsync onSuccess");
        promise.resolve(true);
//        loadPurchases(promise);
      }

      @Override
      public void onError(final IapResult result) {
        Log.e(TAG, "isBillingSupportedAsync onError, " + result.toString());
        if (IapResult.RESULT_NEED_LOGIN == result) {
          PurchaseClient.LoginFlowListener mLoginFlowListener = new PurchaseClient.LoginFlowListener() {
            @Override
            public void onSuccess() {
              Log.d(TAG, "launchLoginFlowAsync onSuccess");
              // 개발사에서는 로그인 성공시에 대한 이후 시나리오를 지정하여야 합니다.
              promise.resolve(true);
//              loadPurchases(promise);
            }

            @Override
            public void onError(IapResult result) {
              Log.e(TAG, "launchLoginFlowAsync onError, " + result.toString());
              promise.reject("launchLogin", "isBillingSupportedAsync onError, " + result.toString());
            }

            @Override
            public void onErrorRemoteException() {
              Log.e(TAG, "launchLoginFlowAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
              promise.reject("launchLogin", "isBillingSupportedAsync onError, " + result.toString());
            }

            @Override
            public void onErrorSecurityException() {
              Log.e(TAG, "launchLoginFlowAsync onError, 비정상 앱에서 결제가 요청되었습니다");
              promise.reject("launchLogin", "isBillingSupportedAsync onError, " + result.toString());
            }

            @Override
            public void onErrorNeedUpdateException() {
              Log.e(TAG, "launchLoginFlowAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
              promise.reject("launchLogin", "isBillingSupportedAsync onError, " + result.toString());
            }

          };
          mPurchaseClient.launchLoginFlowAsync(IAP_API_VERSION, getCurrentActivity(), LOGIN_REQUEST_CODE, mLoginFlowListener);
          return;
        }
        promise.reject("billing", "isBillingSupportedAsync onError, " + result.toString());
      }

      @Override
      public void onErrorRemoteException() {
        Log.e(TAG, "isBillingSupportedAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
        promise.reject("billing", "isBillingSupportedAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
      }

      @Override
      public void onErrorSecurityException() {
        Log.e(TAG, "isBillingSupportedAsync onError, 비정상 앱에서 결제가 요청되었습니다");
        promise.reject("billing", "isBillingSupportedAsync onError, 비정상 앱에서 결제가 요청되었습니다");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "isBillingSupportedAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        promise.reject("billing", "isBillingSupportedAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }
    };

    mPurchaseClient.isBillingSupportedAsync(IAP_API_VERSION, mBillingSupportedListener);
  }
  @ReactMethod
  public void initConnection(String publicKey,final Promise promise) {
    final Activity activity = getCurrentActivity();
    /*
     * PurchaseClient의 connect API 콜백 리스너
     * 바인딩 성공/실패 및 원스토어 서비스 업데이트가 필요한지에 대한 응답을 넘겨줍니다.
     */
    PurchaseClient.ServiceConnectionListener mServiceConnectionListener = new PurchaseClient.ServiceConnectionListener() {
      @Override
      public void onConnected() {
        Log.d(TAG, "Service connected");
        checkBilling(promise);
//        promise.resolve(true);
      }

      @Override
      public void onDisconnected() {
        Log.d(TAG, "Service disconnected");
        promise.reject("initConnection", "Service disconnected");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "connect onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        PurchaseClient.launchUpdateOrInstallFlow(activity);
        promise.reject("initConnection", "connect onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }
    };
    // PurchaseClient 초기화 - context 와 Signature 체크를 위한 public key 를 파라미터로 넘겨줍니다.
    if (mPurchaseClient == null)
      mPurchaseClient = new PurchaseClient(activity, publicKey);

    // 원스토어 서비스로 인앱결제를 위한 서비스 바인딩을 요청합니다.
    mPurchaseClient.connect(mServiceConnectionListener);

  }
  public void terminate() {
    if (mPurchaseClient == null) {
      Log.d(TAG, "PurchaseClient is not initialized");
      return;
    }

    // 앱 종료시 PurchaseClient를 이용하여 서비스를 terminate 시킵니다.
    mPurchaseClient.terminate();
  }

  @ReactMethod
  public  void getProducts( String type, final ReadableArray products,final Promise promise) {
    /*
     * PurchaseClient의 queryProductsAsync API (상품정보조회) 콜백 리스너
     */
    PurchaseClient.QueryProductsListener mQueryProductsListener = new PurchaseClient.QueryProductsListener() {
      @Override
      public void onSuccess(List<ProductDetail> productDetails) {
        Log.d(TAG, "queryProductsAsync onSuccess, " + productDetails.toString());
        WritableNativeArray items = new WritableNativeArray();
        for (ProductDetail product : productDetails) {
          WritableMap item = Arguments.createMap();
          item.putString("productId", product.getProductId());
          item.putString("title", product.getTitle());
          item.putString("price", product.getPrice());
          item.putString("type", product.getType());
          items.pushMap(item);
        }
        try {
          promise.resolve(items);
        } catch (ObjectAlreadyConsumedException oce) {
          Log.e(TAG, oce.getMessage());
        }
      }

      @Override
      public void onErrorRemoteException() {
        Log.e(TAG, "queryProductsAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
        promise.reject("productListener", "queryProductsAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
      }

      @Override
      public void onErrorSecurityException() {
        Log.e(TAG, "queryProductsAsync onError, 비정상 앱에서 결제가 요청되었습니다");
        promise.reject("productListener", "queryProductsAsync onError, 비정상 앱에서 결제가 요청되었습니다");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "queryProductsAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        promise.reject("productListener", "queryProductsAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }

      @Override
      public void onError(IapResult result) {
        Log.e(TAG, "queryProductsAsync onError, " + result.toString());
        promise.reject("productListener", "queryProductsAsync onError, " + result.toString());
      }
    };

    String productType = IapEnum.ProductType.IN_APP.getType(); // "inapp"
    if (type.equalsIgnoreCase("inapp")) {
      productType = IapEnum.ProductType.IN_APP.getType();
    }
    else if (type.equalsIgnoreCase("auto")) {
      productType = IapEnum.ProductType.AUTO.getType();
    }
    else if (type.equalsIgnoreCase("all")) {
      productType = IapEnum.ProductType.ALL.getType();
    }
    Log.d(TAG, type);
    Log.d(TAG, "ss "+products.size());
    ArrayList<String> productCodes = new ArrayList<>();
    for (int i = 0; i < products.size(); i++) {
      productCodes.add(products.getString(i));
    }
    mPurchaseClient.queryProductsAsync(IAP_API_VERSION, productCodes, productType, mQueryProductsListener);
  }

  @ReactMethod
  public void requestPurchase(String productId,String payload, final Promise promise) {
    /*
     * PurchaseClient의 launchPurchaseFlowAsync API (구매) 콜백 리스너
     */
    PurchaseClient.PurchaseFlowListener mPurchaseFlowListener = new PurchaseClient.PurchaseFlowListener() {
      @Override
      public void onSuccess(PurchaseData purchaseData) {
        Log.d(TAG, "launchPurchaseFlowAsync onSuccess, " + purchaseData.toString());
        WritableMap item = Arguments.createMap();
        item.putString("productId", purchaseData.getProductId());
        item.putString("DeveloperPayload", purchaseData.getDeveloperPayload());
        item.putString("OrderId", purchaseData.getOrderId());
        item.putString("PackageName", purchaseData.getPackageName());
        item.putString("PurchaseData", purchaseData.getPurchaseData());
        item.putString("PurchaseId", purchaseData.getPurchaseId());
        item.putString("Signature", purchaseData.getSignature());
        item.putInt("PurchaseState", purchaseData.getPurchaseState());
        item.putString("PurchaseTime", "" + purchaseData.getPurchaseTime());
        item.putInt("RecurringState", purchaseData.getRecurringState());
        promise.resolve(item);
        // 구매완료 후 developer payload 검증을 수해한다.
//        if (!isValidPayload(purchaseData.getDeveloperPayload())) {
//          Log.d(TAG, "launchPurchaseFlowAsync onSuccess, Payload is not valid.");
//          return;
//        }
//
//        // 구매완료 후 signature 검증을 수행한다.
//        boolean validPurchase = AppSecurity.isValidPurchase(purchaseData.getPurchaseData(), purchaseData.getSignature());
//        if (validPurchase) {
//          if (product5000.equals(purchaseData.getProductId())) {
//            // 관리형상품(inapp)은 구매 완료 후 소비를 수행한다.
//            consumeItem(purchaseData);
//          }
//        } else {
//          Log.d(TAG, "launchPurchaseFlowAsync onSuccess, Signature is not valid.");
//          return;
//        }
      }
      @Override
      public void onError(IapResult result) {
        Log.e(TAG, "launchPurchaseFlowAsync onError, " + result.toString());
        promise.reject("purchase", "launchPurchaseFlowAsync onError, " + result.toString());
      }

      @Override
      public void onErrorRemoteException() {
        Log.e(TAG, "launchPurchaseFlowAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
        promise.reject("purchase", "launchPurchaseFlowAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
      }

      @Override
      public void onErrorSecurityException() {
        Log.e(TAG, "launchPurchaseFlowAsync onError, 비정상 앱에서 결제가 요청되었습니다");
        promise.reject("purchase", "launchPurchaseFlowAsync onError, 비정상 앱에서 결제가 요청되었습니다");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "launchPurchaseFlowAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        promise.reject("purchase", "launchPurchaseFlowAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }
    };

    String productName = ""; // "" 일때는 개발자센터에 등록된 상품명 노출
    String productType = IapEnum.ProductType.IN_APP.getType(); // "inapp"
    String devPayload = payload; //generatePayload();
    String gameUserId = ""; // 디폴트 ""
    boolean promotionApplicable = false;
    final Activity activity = getCurrentActivity();
    try {
      Log.d(TAG, "" + mPurchaseClient.launchPurchaseFlowAsync(IAP_API_VERSION, activity, PURCHASE_REQUEST_CODE, productId, productName, productType, devPayload, gameUserId, promotionApplicable, mPurchaseFlowListener));
    }catch (Exception e){
      promise.reject("purchase", e);
    }
  }

  @ReactMethod
  public void finishPurchase(ReadableMap purchase,final Promise promise) {
    /*
     * PurchaseClient의 consumeAsync API (상품소비) 콜백 리스너
     */
    PurchaseClient.ConsumeListener mConsumeListener = new PurchaseClient.ConsumeListener() {
      @Override
      public void onSuccess(PurchaseData purchaseData) {
        Log.d(TAG, "consumeAsync onSuccess, " + purchaseData.toString());
        // 상품소비 성공, 이후 시나리오는 각 개발사의 구매완료 시나리오를 진행합니다.
        promise.resolve(true);
      }

      @Override
      public void onErrorRemoteException() {
        Log.e(TAG, "consumeAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
        promise.reject("consume","consumeAsync onError, 원스토어 서비스와 연결을 할 수 없습니다");
      }

      @Override
      public void onErrorSecurityException() {
        Log.e(TAG, "consumeAsync onError, 비정상 앱에서 결제가 요청되었습니다");
        promise.reject("consume","consumeAsync onError,  비정상 앱에서 결제가 요청되었습니다");
      }

      @Override
      public void onErrorNeedUpdateException() {
        Log.e(TAG, "consumeAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
        promise.reject("consume","consumeAsync onError, 원스토어 서비스앱의 업데이트가 필요합니다");
      }

      @Override
      public void onError(IapResult result) {
        Log.e(TAG, "consumeAsync onError, " + result.toString());
        promise.reject("consume","consumeAsync onError, " + result.toString());
      }
    };

    PurchaseData purchaseData = PurchaseData.builder()
            .productId(purchase.getString("productId"))
            .developerPayload(purchase.getString("DeveloperPayload"))
            .orderId(purchase.getString("OrderId"))
            .packageName(purchase.getString("PackageName"))
            .purchaseData(purchase.getString("PurchaseData"))
            .purchaseId(purchase.getString("PurchaseId"))
            .signature(purchase.getString("Signature"))
            .purchaseState(purchase.getInt("PurchaseState"))
            .purchaseTime(Long.parseLong(purchase.getString("PurchaseTime")))
            .recurringState(purchase.getInt("RecurringState"))
            .developerPayload("")
            .build(); // 구매내역조회 및 구매요청 후 전달받은 PurchaseData

    mPurchaseClient.consumeAsync(IAP_API_VERSION, purchaseData, mConsumeListener);
  }
  public static String generatePayload() {
    char[] payload;
    final char[] specials = {'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '{', '}', '|', '\\', '/', '.',
            '.', '=', '[', ']', '?', '<', '>'};
    StringBuilder buffer = new StringBuilder();
    for (char ch = '0'; ch <= '9'; ++ch) {
      buffer.append(ch);
    }
    for (char ch = 'a'; ch <= 'z'; ++ch) {
      buffer.append(ch);
    }
    for (char ch = 'A'; ch <= 'Z'; ++ch) {
      buffer.append(ch);
    }

    for (char ch : specials) {
      buffer.append(ch);
    }

    payload = buffer.toString().toCharArray();

    StringBuilder randomString = new StringBuilder();
    Random random = new Random();

    //length : 20자
    for (int i = 0; i < 20; i++) {
      randomString.append(payload[random.nextInt(payload.length)]);
    }

    return randomString.toString();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PURCHASE_REQUEST_CODE) {
      /*
       * launchPurchaseFlowAsync API 호출 시 전달받은 intent 데이터를 handlePurchaseData를 통하여 응답값을 파싱합니다.
       * 파싱 이후 응답 결과를 launchPurchaseFlowAsync 호출 시 넘겨준 PurchaseFlowListener 를 통하여 전달합니다.
       */
      if (resultCode == Activity.RESULT_OK) {
        if (mPurchaseClient.handlePurchaseData(data) == false) {
          Log.e(TAG, "onActivityResult handlePurchaseData false ");
          // listener is null
        }
      } else {
        Log.e(TAG, "onActivityResult user canceled");
        // user canceled , do nothing..
      }
    }
    else if (requestCode == LOGIN_REQUEST_CODE) {
      /*
       * launchLoginFlowAsync API 호출 시 전달받은 intent 데이터를 handleLoginData를 통하여 응답값을 파싱합니다.
       * 파싱 이후 응답 결과를 launchLoginFlowAsync 호출 시 넘겨준 LoginFlowListener 를 통하여 전달합니다.
       */

      if (resultCode == Activity.RESULT_OK) {
        if (mPurchaseClient.handleLoginData(data) == false) {
          Log.e(TAG, "onActivityResult handleLoginData false ");
          // listener is null
        }
      } else {
        Log.e(TAG, "onActivityResult user canceled");

        // user canceled , do nothing..
      }
    }
  }
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    onActivityResult(requestCode, resultCode, data);
  }
  
  @Override
  public void onNewIntent(Intent intent) { }
}