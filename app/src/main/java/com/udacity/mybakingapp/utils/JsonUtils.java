package com.udacity.mybakingapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.udacity.mybakingapp.BuildConfig;
import com.udacity.mybakingapp.data.ImageReplacer;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.model.ReviewItem;
import com.udacity.mybakingapp.model.TrailerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class JsonUtils {
    private static final String TAG = "JSonUtils";
    private static Recipe[] mRecipes= null;

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public final static String RECIPES_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String POSTER_BASE_URL  = "http://image.tmdb.org/t/p/";
    public final static String YOUTUBE_TN_URL   = "http://img.youtube.com/vi/";

    public final static String W185 ="w185";
    public final static String W500 ="w500";

    final static String PARAM_QUERY = "api_key";
    final static String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    /**
     * Builds the URL used to query MovieDB.
     *
     * @param movieDbQuery The keyword that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String movieDbQuery) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(movieDbQuery)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrl(String BASE_URL, String movieDbQuery) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .build();

        if (!movieDbQuery.isEmpty()) {
            builtUri= Uri.withAppendedPath(builtUri,Uri.encode(movieDbQuery));
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static Recipe getRecipe(int index) {
        if ((mRecipes!=null) && (index <= mRecipes.length))
            return mRecipes[index];
        else
            return null;
    }

    public static Recipe[] parseRecipesJson(String json) {
        try {
            JSONArray arrayJsonRoot        = new JSONArray(json);
            int num_recipes=arrayJsonRoot.length();


            Recipe[] recipes = new Recipe[num_recipes];
            Gson gson = new Gson();
            for (int i=0;i<num_recipes;i++) {
                String recipeString= arrayJsonRoot.getJSONObject(i).toString();
                Recipe recipe = new Recipe();
                recipe = gson.fromJson(recipeString, Recipe.class);  // Convert JSON to Java Object
                recipes[i]=recipe;
            }
            ImageReplacer ir=new ImageReplacer();
            for (Recipe recipe:recipes) {
                int numSteps=recipe.getSteps().size();
                String videoUrl=recipe.getSteps().get(numSteps-1).getVideoURL();
                Bitmap thumbnail=null;
                String altImageUrlStr=ir.findThumbnailByRecipeId(recipe.getId());
                if (altImageUrlStr==null) altImageUrlStr="";
                if (altImageUrlStr.isEmpty() && (recipe.getImage().isEmpty()) && (!videoUrl.isEmpty()))
                    thumbnail = retriveVideoFrameFromVideo(videoUrl);
                recipe.setThumbnail(thumbnail);
            }

            mRecipes=recipes;
            return recipes;
        }


        catch (JSONException e) {
            e.printStackTrace();
            //Log.d(TAG,"Couldn't parse Json Movies Object:"+json);
            return null;
        }
    }




    public static String getJsonString(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getString(propertyName);
    }

    public static int getJsonInt(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getInt(propertyName);
    }

    public static boolean getJsonBoolean(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getBoolean(propertyName);
    }

    public static float getJsonFloat(JSONObject pJson, String propertyName) throws JSONException  {
        return Float.valueOf(pJson.getString(propertyName));
    }


    public static List<Integer> getJsonIntegerList(JSONObject pJson, String propertyName) throws JSONException  {
        List<Integer> aList = new ArrayList<Integer>();
        JSONArray array = pJson.getJSONArray(propertyName);
            for (int i = 0; i < array.length(); i++) {
                aList.add(array.getInt(i));
        }
        return aList;
    }

    public static Date getJsonDate(JSONObject pJson,String propertyName) throws JSONException  {

        String release_date_str   = getJsonString(pJson      ,propertyName);
        SimpleDateFormat sdf      = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date release_date; // = null;
        try {
            release_date = sdf.parse(release_date_str);
        }
        catch (ParseException e) {
            throw new JSONException(e.toString());
        }
        return release_date;
    }

    public static Bitmap makeThumbnailURL(String path) {
        //return YOUTUBE_TN_URL.concat(key).concat("/hqdefault.jpg");
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
    {

       // Bitmap bitmap = null;
       // ThumbnailUtils.createVideoThumbnail(Uri.parse(videoPath).getEncodedPath(),
       //         MediaStore.Images.Thumbnails.MINI_KIND);
       // return bitmap;


        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();//Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        //bitmap=Bitmap.createScaledBitmap(bitmap, 1500, 500, false);
        return bitmap;
    }

    public static Bitmap getRunTimeThumbnail(Context context, String videoUrl) {
        String selectedPathVideo = ImageFilePath.getPath(context, Uri.parse(videoUrl));
        Log.i("Image File Path", "" + selectedPathVideo);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.MICRO_KIND);
        return thumb;
    }


    /*
    public static String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    */

}
