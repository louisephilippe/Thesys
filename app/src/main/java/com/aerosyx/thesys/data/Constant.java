package com.aerosyx.thesys.data;

import android.net.Uri;

public class Constant {

	private static String IP_ADDRESS = "thesys.esy.es";
//http://dream-space.web.id/envato/services/material_recipe/#/login

	public static String getURLimgRecipe(String file_name){
		Uri.Builder builder = new Uri.Builder();
		String URL;
		builder.scheme("http").authority(IP_ADDRESS)
				.appendPath("uploads")
				.appendPath("recipe")
				.appendPath(file_name);
		URL = builder.build().toString();
//Log.e("URL", URL);
		return URL;
	}

	public static String getURLimgCategory(String file_name){
		Uri.Builder builder = new Uri.Builder();
		String URL;
		builder.scheme("http").authority(IP_ADDRESS)
				.appendPath("uploads")
				.appendPath("category")
				.appendPath(file_name);
		URL = builder.build().toString();
//Log.e("URL", URL);
		return URL;
	}

	public static String getURLrecipes(){
/* http://localhost/material_recipe/app/services/categories */
		Uri.Builder builder = new Uri.Builder();
		String URL;
		builder.scheme("http").authority(IP_ADDRESS)
				.appendPath("app")
				.appendPath("services")
				.appendPath("recipes");
		URL = builder.build().toString();
//Log.e("URL", URL);
		return URL;
	}

	public static String getURLcategory(){
/* http://localhost/material_recipe/app/services/recipes */
		Uri.Builder builder = new Uri.Builder();
		String URL;
		builder.scheme("http").authority(IP_ADDRESS)
				.appendPath("app")
				.appendPath("services")
				.appendPath("categories");
		URL = builder.build().toString();
//Log.e("URL", URL);
		return URL;
	}

}
