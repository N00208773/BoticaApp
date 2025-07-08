package com.example.boticaapp.api;

import android.content.Context;
import android.util.Log;
import com.example.boticaapp.utils.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.Header;
import java.io.UnsupportedEncodingException;

public class ApiClient {
    public static final AsyncHttpClient client = new AsyncHttpClient();
    static {
        client.addHeader("Accept", "application/json");
        client.setTimeout(30_000);
    }

    private static final String AUTH_URL       = Constants.API_BASE_URL + "auth.php";
    private static final String USERS_URL      = Constants.API_BASE_URL + "users.php";
    private static final String PHARMACIES_URL = Constants.API_BASE_URL + "pharmacies.php";
    public  static final String MEDICINES_URL  = Constants.API_BASE_URL + "medicines.php";
    private static final String ORDERS_URL     = Constants.API_BASE_URL + "orders.php";

    // ---------- AUTH ----------
    public static void login(Context ctx, String email, String pass, JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        String url = AUTH_URL + "?action=login";
        Log.d("ApiClient", "POST JSON " + url + " → " + body);
        client.post(ctx, url, entity, "application/json", h);
    }

    public static void register(Context ctx,
                                String email,
                                String pass,
                                String name,
                                String role,
                                JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", pass);
            body.put("name", name);
            body.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        String url = AUTH_URL + "?action=register";
        Log.d("ApiClient", "POST JSON " + url + " → " + body);
        client.post(ctx, url, entity, "application/json", h);
    }

    // ---------- USERS (ADMIN) ----------
    public static void getAllUsers(Context ctx, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "list");
        client.get(ctx, USERS_URL, p, h);
    }
    public static void updateUser(
            Context ctx,
            int userId,
            String name,
            String email,
            JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("action", "update");
            body.put("id",       userId);
            body.put("name",     name);
            body.put("email",    email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        client.post(
                ctx,
                Constants.API_BASE_URL + "users.php?action=update",
                entity,
                "application/json",
                h
        );
    }

    // ---------- PHARMACIES ----------
    public static void getPharmacies(Context ctx, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "list");
        client.get(ctx, PHARMACIES_URL, p, h);
    }
    public static void getPharmacy(Context ctx, int id, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams();
        p.put("action", "get");
        p.put("id", id);
        client.get(ctx, PHARMACIES_URL, p, h);
    }
    public static void createPharmacy(Context ctx,
                                      String name,
                                      String address,
                                      String phone,
                                      JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("action", "create");
            body.put("name", name);
            body.put("address", address);
            body.put("phone", phone);
            StringEntity e = new StringEntity(body.toString(), "UTF-8");
            client.post(ctx, PHARMACIES_URL, e, "application/json", h);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public static void updatePharmacy(Context ctx,
                                      int id,
                                      String name,
                                      String address,
                                      String phone,
                                      JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams();
        p.put("action", "update");
        p.put("id", id);
        p.put("name", name);
        p.put("address", address);
        p.put("phone", phone);
        client.post(ctx, PHARMACIES_URL, p, h);
    }
    public static void deletePharmacy(Context ctx, int id, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "delete");
        p.put("id", id);
        client.post(ctx, PHARMACIES_URL, p, h);
    }

    // ---------- MEDICINES ----------
    public static void getMedicines(Context ctx, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "list");
        client.get(ctx, MEDICINES_URL, p, h);
    }
    public static void getMedicinesByPharmacy(Context ctx, int pharmacyId, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "list");
        p.put("pharmacy_id", pharmacyId);
        client.get(ctx, MEDICINES_URL, p, h);
    }
    public static void searchMedicines(Context ctx, String name, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "search");
        p.put("name", name);
        client.get(ctx, MEDICINES_URL, p, h);
    }
    public static void createMedicine(Context ctx,
                                      int pharmacyId,
                                      String name,
                                      String desc,
                                      double price,
                                      int stock,
                                      JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("pharmacy_id", pharmacyId);
            body.put("name", name);
            body.put("description", desc);
            body.put("price", price);
            body.put("stock", stock);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        String url = MEDICINES_URL + "?action=create";
        Log.d("ApiClient", "POST JSON " + url + " → " + body);
        client.post(ctx, url, entity, "application/json", h);
    }
    public static void updateMedicine(Context ctx,
                                      int id,
                                      String name,
                                      String desc,
                                      double price,
                                      int stock,
                                      JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("id", id);
            body.put("name", name);
            body.put("description", desc);
            body.put("price", price);
            body.put("stock", stock);
            StringEntity entity = new StringEntity(body.toString(), "UTF-8");
            client.post(ctx, MEDICINES_URL + "?action=update", entity, "application/json", h);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void deleteMedicine(Context ctx, int id, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "delete");
        p.put("id", id);
        client.post(ctx, MEDICINES_URL, p, h);
    }

    // ---------- ORDERS ----------
    public static void getOrdersByUser(Context ctx, int userId, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "by_user");
        p.put("user_id", userId);
        client.get(ctx, ORDERS_URL, p, h);
    }
    public static void getOrdersByPharmacy(Context ctx, int pharmacyId, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "by_pharmacy");
        p.put("pharmacy_id", pharmacyId);
        client.get(ctx, ORDERS_URL, p, h);
    }
    public static void createOrder(Context ctx,
                                   int userId,
                                   int pharmacyId,
                                   JSONArray itemsArray,
                                   JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("user_id", userId);
            body.put("pharmacy_id", pharmacyId);
            body.put("items", itemsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        String url = ORDERS_URL + "?action=create";
        Log.d("ApiClient", "POST JSON " + url + " → " + body);
        client.post(ctx, url, entity, "application/json", h);
    }
    public static void updateOrderStatus(Context ctx,
                                         int orderId,
                                         String status,
                                         JsonHttpResponseHandler h) {
        JSONObject body = new JSONObject();
        try {
            body.put("id", orderId);
            body.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(body.toString(), "UTF-8");
        client.post(ctx, ORDERS_URL + "?action=update_status", entity, "application/json", h);
    }

    // ---------- ADMIN EXTRA ----------
    public static void getTopPharmacy(Context ctx, JsonHttpResponseHandler h) {
        RequestParams p = new RequestParams("action", "top");
        client.get(ctx, ORDERS_URL, p, h);
    }
}

