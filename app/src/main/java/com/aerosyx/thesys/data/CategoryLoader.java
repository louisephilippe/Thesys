package com.aerosyx.thesys.data;

import android.os.AsyncTask;

import com.aerosyx.thesys.json.JSONStream;
import com.aerosyx.thesys.model.Category;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryLoader extends AsyncTask<String, String, List<Category>> {
    JSONStream jsonStream = new JSONStream();
    String URL = Constant.getURLcategory();
    private Gson gson = new Gson();

    public interface TaskListener {
        public void onFinished(List<Category> result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    public CategoryLoader(TaskListener listener) {
        this.taskListener = listener;
    }

    @Override
    protected List<Category> doInBackground(String... params) {
        try {
            Thread.sleep(100);
            List<NameValuePair> param = new ArrayList<>();
            JsonReader reader = jsonStream.getJsonResult(URL, JSONStream.METHOD_GET, param);
            return getCategoryArray(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Category> result) {
        super.onPostExecute(result);
        // In onPostExecute we check if the listener is valid
        if (this.taskListener != null) {
            this.taskListener.onFinished(result);
        }
    }

    private List<Category> getCategoryArray(JsonReader reader) throws IOException {
        List<Category> list = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            Category category = gson.fromJson(reader, Category.class);
            list.add(category);
        }
        reader.endArray();
        reader.close();
        return list;
    }
}
