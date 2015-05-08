package com.smy.volley.extend;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.levelup.jiemimoshengren.R;

/**Volley错误相关帮助类*/
public class VolleyErrorHelper {
    /**
    * Returns appropriate message which is to be displayed to the user 
    * against the specified error object.
    * @param error
    * @param context
    * @return
    */
 public static String getMessage(Object error, Context context) {
     if (error instanceof TimeoutError) {
         return context.getResources().getString(R.string.generic_server_down);
     }
     else if (isServerProblem(error)) {
         return handleServerError(error, context);
     }
     else if (isNetworkProblem(error)) {
         return context.getResources().getString(R.string.no_internet);
     }
     return context.getResources().getString(R.string.generic_error);
 }
 
 /**
 * Determines whether the error is related to network
 * @param error
 * @return
 */
 private static boolean isNetworkProblem(Object error) {
     return (error instanceof NetworkError) || (error instanceof NoConnectionError);
 }
 /**
 * Determines whether the error is related to server
 * @param error
 * @return
 */
 private static boolean isServerProblem(Object error) {
     return (error instanceof ServerError) || (error instanceof AuthFailureError);
 }
 /**
 * Handles the server error, tries to determine whether to show a stock message or to 
 * show a message retrieved from the server.
 * 
 * @param err
 * @param context
 * @return
 */
 private static String handleServerError(Object err, Context context) {
	 return "serverError";
     /*VolleyError error = (VolleyError) err;
 
     NetworkResponse response = error.networkResponse;
 
     if (response != null) {
         switch (response.statusCode) {
           case 404:
           case 422:
           case 401:
               try {
                   // server might return error like this { "error": "Some error occured" }
                   // Use "Gson" to parse the result
                   HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                           new TypeToken<Map<String, String>>() {
                           }.getType());

                   if (result != null && result.containsKey("error")) {
                       return result.get("error");
                   }

               } catch (Exception e) {
                   e.printStackTrace();
               }
               // invalid request
               return error.getMessage();

           default:
               return context.getResources().getString(R.string.generic_server_down);
           }
     }
       return context.getResources().getString(R.string.generic_error);*/
 }
}