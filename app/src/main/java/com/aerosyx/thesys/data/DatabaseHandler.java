package com.aerosyx.thesys.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aerosyx.thesys.model.Category;
import com.aerosyx.thesys.model.Recipe;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muslim on 15/01/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION       = 1;
    private static final String DATABASE_NAME       = "m_recipe_db";

    public static final String TABLE_RECIPE         = "table_recipe";
    public static final String TABLE_FAVORITES      = "table_favorites";
    public static final String TABLE_CATEGORY       = "table_category";

    // Columns names TABLE_RECIPE && FAVORITES && CATEGORY
    private static final String R_ID                = "id";
    private static final String R_CONTENT           = "content";
    private static final String R_CATEGORY          = "category";

    private Gson gson = new Gson();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        Log.d("DB", "Constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "onCreate");
        createTableRecipe(db);
        createTableCategory(db);
        createTableFavorites(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
    }
    private void truncateTableRecipe(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        createTableRecipe(db);
    }
    private void truncateTableCategory(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        createTableCategory(db);
    }

    private void createTableRecipe(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECIPE + "("
                + R_ID + " TEXT PRIMARY KEY,"
                + R_CATEGORY + " TEXT,"
                + R_CONTENT + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }
    private void createTableCategory(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + R_ID + " TEXT PRIMARY KEY,"
                + R_CONTENT + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableFavorites(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + R_ID + " TEXT PRIMARY KEY,"
                + R_CONTENT + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * TRANSACTION TABLE
     */


    /**
     * Recipes
     */
    public List<Recipe> addListRecipe(List<Recipe> recipes){
        SQLiteDatabase db = this.getWritableDatabase();
        truncateTableRecipe(db);
        for (Recipe r : recipes){
            addOneRecipe(db, r);
        }
        db.close();
        return getAllRecipe();
    }

    public List<Recipe> getAllRecipe(){
        return getAll(TABLE_RECIPE);
    }

    private void addOneRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(R_ID, recipe.id+"");
        values.put(R_CATEGORY, recipe.category+"");
        values.put(R_CONTENT, gson.toJson(recipe));
        if(!isExist(db, TABLE_RECIPE, recipe.id+"")) {
            db.insert(TABLE_RECIPE, null, values);
        }else {
            db.update(TABLE_RECIPE, values, R_ID + " = ?", new String[]{recipe.id+""});
        }
    }

    public List<Recipe> getRecipesByCategoryId(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Recipe> list = new ArrayList<>();
        String q = "SELECT  * FROM " + TABLE_RECIPE +" WHERE "+R_CATEGORY+" = ?";
        Cursor cursor = db.rawQuery(q, new String[]{category.id+""});
        list = getAll(db, cursor);
        db.close();
        return list;
    }

    /**
     * Category
     */
    public List<Category> addListCategory(List<Category> categories){
        SQLiteDatabase db = this.getWritableDatabase();
        truncateTableCategory(db);
        for (Category c : categories){
            addOneCategory(db, c);
        }
        db.close();
        return getAllCategory();
    }

    public List<Category> getAllCategory(){
        return getAllcategory(TABLE_CATEGORY);
    }

    private void addOneCategory(SQLiteDatabase db, Category category) {
        ContentValues values = new ContentValues();
        values.put(R_ID, category.id);
        values.put(R_CONTENT, gson.toJson(category));
        if(!isExist(db, TABLE_CATEGORY, category.id+"")) {
            db.insert(TABLE_CATEGORY, null, values);
        }else {
            db.update(TABLE_CATEGORY, values, R_ID + " = ?", new String[]{category.id+""});
        }
    }


    /**
     * Favorites
     */
    public Recipe addOneFavorite(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(R_ID, recipe.id);
        values.put(R_CONTENT, gson.toJson(recipe));
        if(!isExist(db, TABLE_FAVORITES, recipe.id + "")) {
            db.insert(TABLE_FAVORITES, null, values);
        }else {
            db.update(TABLE_FAVORITES, values, R_ID + " = ?", new String[]{recipe.id+""});
        }
        db.close();
        return recipe;
    }
    public List<Recipe> getAllFavorites() {
        return getAll(TABLE_FAVORITES);
    }
    public void deleteFavorites(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, R_ID + " = ?", new String[] { String.valueOf(recipe.id+"") });
        db.close();
    }


    /**
     * Support Method
     */

    public boolean isExist(String table, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return isExist(db, table, id);
    }
    private boolean isExist(SQLiteDatabase db, String table, String id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + R_ID + " = ?", new String[]{id});
        int count = cursor.getCount();
        cursor.close();
        if(count>0){
            return true;
        }else{
            return false;
        }
    }
    private List<Recipe> getAll(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Recipe> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table , null);
        list = getAll(db, cursor);
        db.close();
        return list;
    }

    private List<Recipe> getAll(SQLiteDatabase db, Cursor cursor) {
        List<Recipe> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex(R_CONTENT));
                Recipe r = gson.fromJson(content, Recipe.class);
                list.add(r);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    private List<Category> getAllcategory(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table , null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex(R_CONTENT));
                Category c = gson.fromJson(content, Category.class);
                list.add(c);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

}
