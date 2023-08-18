package org.pseudocode.extract.pipeline.fileOperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pseudocode.extract.pipeline.Constants;
import org.pseudocode.extract.pipeline.model.Article;
import org.pseudocode.extract.pipeline.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonOperation {

    public static void writeArticlesToFile(int year, int month, List<Article> list) throws IOException {
        String yearMonthKey = Util.createYearMonthKey(year, month);
        String json = new Gson().toJson(list);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        String prettyJsonString = gson.toJson(je);

        FileUtils.write(new File(Constants.t7GResultPath + yearMonthKey + ".json"), prettyJsonString, StandardCharsets.UTF_8);
    }

    public static List<Article> readArticlesFromFile(int year, int month) throws IOException {
        String yearMonthKey = Util.createYearMonthKey(year, month);
        File jsonFile = new File(Constants.t7GResultPath + yearMonthKey + ".json");
        String content = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);

        Type articleListType = new TypeToken<ArrayList<Article>>(){}.getType();
        return new Gson().fromJson(content, articleListType);
    }
}
