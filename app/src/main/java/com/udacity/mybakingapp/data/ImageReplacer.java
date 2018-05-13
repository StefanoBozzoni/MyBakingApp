package com.udacity.mybakingapp.data;

import android.util.SparseArray;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageReplacer {
   private SparseArray<String> hmap;

    public ImageReplacer()
    {
        this.hmap = new SparseArray<String>();

        hmap.put(1,"http://cooknshare.com/wp-content/uploads/2015/10/C1915293-1444846793783477large.jpg");
        hmap.put(2,"https://cafedelites.com/wp-content/uploads/2016/08/Fudgy-Cocoa-Brownies-35.jpg");
        hmap.put(3,"https://assets.epicurious.com/photos/57c5b45184c001120f616523/6:4/w_620%2Ch_413/moist-yellow-cake.jpg");
        hmap.put(4,"http://food.fnr.sndimg.com/content/dam/images/food/fullset/2013/12/9/0/FNK_Cheesecake_s4x3.jpg.rend.hgtvcom.616.462.suffix/1387411272847.jpeg");
    }

    public URL findThumbnailURLByRecipeId(int recipeId){
        String s= hmap.get(recipeId);
        URL url = null;
        try {
            return new URL(s);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public String findThumbnailByRecipeId(int recipeId){
        return hmap.get(recipeId);
    }

}
