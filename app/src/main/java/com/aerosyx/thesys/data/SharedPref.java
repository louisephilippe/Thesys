package com.aerosyx.thesys.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import java.util.Set;

public class SharedPref {

	private static String KEY_LIS_ID = "KEY_LIS_ID";

	public static void addFavoriteId(Context ctx, String id){
		Set<String> val = getStringListPref(KEY_LIS_ID, ctx);
		if(!val.contains(id)) {
			val.add(id);
			setStringListPref(KEY_LIS_ID, val, ctx);
		}
	}
	public static void delFavoriteId(Context ctx, String id){
		Set<String> val = getStringListPref(KEY_LIS_ID, ctx);
		if(val.contains(id)) {
			val.remove(id);
			setStringListPref(KEY_LIS_ID, val, ctx);
		}
	}

	public static boolean isIdExist(Context ctx, String id){
		Set<String> val = getStringListPref(KEY_LIS_ID, ctx);
		return val.contains(id);
	}

	public static Set<String> getFavorites(Context ctx){
		return getStringListPref(KEY_LIS_ID, ctx);
	}

	/**
	 * Universal shared preference
	 * for string
	 */
	public static String getStringPref(String key_val, String def_val, Context context) {
		SharedPreferences pref = context.getSharedPreferences("pref_" + key_val, context.MODE_PRIVATE);
		return pref.getString(key_val, def_val);
	}
	
	public static void setStringPref(String key_val, String val, Context context) {
		SharedPreferences pref = context.getSharedPreferences("pref_"+key_val,context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.clear();
		prefEditor.putString(key_val, val);
		prefEditor.commit();
	}
	public static Set<String> getStringListPref(String key_val, Context context) {
		Set<String> val = new ArraySet<>();
		SharedPreferences pref = context.getSharedPreferences("pref_"+key_val,context.MODE_PRIVATE);
		return pref.getStringSet(key_val,val);
	}

	public static void setStringListPref(String key_val, Set<String> val, Context context) {
		SharedPreferences pref = context.getSharedPreferences("pref_"+key_val,context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.clear();
		prefEditor.putStringSet(key_val, val);
		prefEditor.commit();
	}


}
