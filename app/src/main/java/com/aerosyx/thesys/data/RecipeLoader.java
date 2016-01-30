package com.aerosyx.thesys.data;

import android.os.AsyncTask;

import com.aerosyx.thesys.json.JSONStream;
import com.aerosyx.thesys.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeLoader extends AsyncTask<String, String, List<Recipe>> {
    JSONStream jsonStream = new JSONStream();
    String URL = Constant.getURLrecipes();
    private Gson gson = new Gson();

    public interface TaskListener {
        public void onFinished(List<Recipe> result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    public RecipeLoader(TaskListener listener) {
        this.taskListener = listener;
    }

    @Override
    protected List<Recipe> doInBackground(String... params) {
        try {
            Thread.sleep(100);
            List<NameValuePair> param = new ArrayList<>();
            JsonReader reader = jsonStream.getJsonResult(URL, JSONStream.METHOD_GET, param);
            return getRecipeArray(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Recipe> result) {
        super.onPostExecute(result);
        // In onPostExecute we check if the listener is valid
        if (this.taskListener != null) {
            this.taskListener.onFinished(result);
        }
    }

    private List<Recipe> getRecipeArray(JsonReader reader) throws IOException {
        List<Recipe> list = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            Recipe recipe = gson.fromJson(reader, Recipe.class);
            list.add(recipe);
        }
        reader.endArray();
        reader.close();
        return list;
    }
}
