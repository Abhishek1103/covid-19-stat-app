package com.aks.covidtracker.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.aks.covidtracker.Constants;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;

import org.xml.sax.helpers.AttributesImpl;

import java.util.Locale;

import okhttp3.OkHttpClient;

public class QueryUtils {

    private static final String TAG = "QueryUtils";

//    public static String readFromStream(InputStream inputStream) throws IOException {
//        StringBuilder output = new StringBuilder();
//        if(inputStream!=null){
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            String line = reader.readLine();
//            while(line!=null){
//                output.append(line);
//                line = reader.readLine();
//            }
//        }
//        return output.toString();
//    }

//    public static String makeHttpRequest(URL url) throws IOException {
//        String jsonResponse = null;
//        HttpURLConnection urlConnection = null;
//        InputStream inputStream = null;
//
//        try{
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setReadTimeout(10000);
//            urlConnection.setConnectTimeout(15000);
//            urlConnection.connect();
//
//            if(urlConnection.getResponseCode() != 200){
//                Log.e(LOG_TAG, urlConnection.getResponseCode()+"");
//                throw new Exception();
//            }
//            inputStream = urlConnection.getInputStream();
//            jsonResponse = readFromStream(inputStream);
//        }catch (IOException e){
//            Log.e(LOG_TAG, "Error in making request",e);
//        }
//        catch (Exception e){
//            Log.e(LOG_TAG,"Exception", e);
//        } finally {
//            if(urlConnection!=null){
//                urlConnection.disconnect();
//            }
//            if(inputStream!=null){
//                inputStream.close();
//            }
//        }
//
//        return jsonResponse;
//    }

    public static boolean checkInternetConnectivity(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String formatNumbers(String num){
        StringBuilder stringBuffer = new StringBuilder(num);
        try {
            long n = Long.parseLong(num);
            if(n < 1000)
                return num;
            else if(n < 100000){
                int len = num.length();
                stringBuffer.insert(len-3, ',');
                return stringBuffer.toString();
            }else if(n < 10000000){
                int len = num.length();
                stringBuffer.insert(len-5, ',');
                return stringBuffer.toString();
            }
        }catch(Exception e){
            return num;
        }
        return num;
    }

    // Create an OkHttpClient
    public static OkHttpClient initOkHttpClient(){
        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = new OkHttpClient.Builder().build();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return okHttpClient;
        }
    }

    // create an Apollo client with an OkHTTPClient and DiskLRUCache
    public static ApolloClient buildApolloClient(OkHttpClient okHttpClient, DiskLruHttpCacheStore cacheStore){
               ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.BASE_URL)
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(okHttpClient)
                .build();

               return apolloClient;
    }

    public static String getMonth(int num){
        switch(num){
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
        }
        return ""+num;
    }

    public static String calcRecoveryRate(Integer confirmed, Integer recovered){
        Double ans = (((double)recovered)/((double)confirmed)) * 100.0;
        if(ans.isNaN() || ans.isInfinite())
            return "NA";
        return String.format(Locale.US,"%.1f",ans);
    }

    public static String calcMortalityRate(Integer confirmed, Integer deaths){
        Double ans = (((double)deaths)/((double)confirmed)) * 100.0;
        if(ans.isNaN() || ans.isInfinite())
            return "NA";
        return String.format(Locale.US,"%.1f",ans);
    }

    public static String calcRate(Integer start, Integer end, Integer timePeriod){
        Double s = (double)start;
        Double e = (double)end;
        Double time = timePeriod*1.0;
        Double ans = 100 * (Math.pow(e/s, 1.0/time) -1);
        if(ans.isNaN() || ans.isInfinite())
            return "NA";
        return String.format(Locale.US,"%.1f",ans);
    }

    public static String calcDoublingRate(Integer start, Integer end, Integer timePeriod){
        try {
            Double rate = Double.parseDouble(calcRate(start, end, timePeriod));
            Double ans = Math.log10(2) / Math.log10((100.0 + rate) / 100);
            ans = (double)Math.round(ans);

            if (ans.isNaN() || ans.isInfinite())
                return "NA";
            return String.format(Locale.US, "%.0f", ans);
        }catch (Exception e){
            Log.e(TAG, "calcDoublingRate: Could not calculate doubling rate",e );
            return "NA";
        }
    }

}
