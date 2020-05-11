package com.aks.covidtracker.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

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


}
