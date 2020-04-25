package edu.brown.cs.student.database;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.brown.cs.student.food.Recipe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * This class is used to handle interactions with the Edamam api, and parse the resulting JSON
 * into Java objects.
 *
 */
public final class FieldParser {

  /**
   * APP_ID - The application id for api authentication.
   * APP_KEY - The key for api authentication.
   */
  private static final String APP_ID = "2a676518";
  private static final String APP_KEY = "158f55a83eee58aff1544072b788784f";

  private FieldParser() { }

  /**
   * This function formats an input uri so that it can be used in a get request.
   * @param uri - The uri that must be formatted
   * @return - The uri with all / and : replaced.
   */
  private static String formatURI(String uri) {
    String temp;
    temp = uri.replace(":", "%3A");
    temp = temp.replace("/", "%2F");
    temp = temp.replace("#", "%23");
    return temp;
  }

  /**
   * This function takes the json for each Recipe in an array and returns the information in
   * object form.
   * @param json - the JSON text of the recipe.
   * @return - the array of Recipe objects containing information from the JSON.
   */
  private static Recipe[] parseRecipeJSON(String json) {
    System.out.println("got here");
    GsonBuilder gsonBuilder = new GsonBuilder();
    JsonDeserializer<Recipe> recipeDeserializer = new RecipeDeserializer();
    gsonBuilder.registerTypeAdapter(Recipe.class, recipeDeserializer);
    Gson gson = gsonBuilder.create();
    JsonElement jsonTree = JsonParser.parseString(json);

    try {
      JsonObject jsonObject = jsonTree.getAsJsonObject();
      if (jsonObject.has("hits")) {
        JsonElement hits = jsonObject.get("hits");
        JsonArray hitsArray = hits.getAsJsonArray();
        Recipe[] recipes = new Recipe[hitsArray.size()];

        for (int i = 0; i < hitsArray.size(); i++) {
          JsonObject currElt = hitsArray.get(i).getAsJsonObject();
          recipes[i] = gson.fromJson(currElt.get("recipe"), Recipe.class);
        }
        return recipes;
      }
    } catch (IllegalStateException ise) {
      return gson.fromJson(json, Recipe[].class);
    }
    return null; //should never be reached.
  }

  /**
   * This function retrieves a recipe from the api that corresponds to the given uri.
   * @param uri - the uri of the desired recipe.
   * @return - the recipe object corresponding to the given uri.
   * @throws IOException - when httpClient.send fails.
   * @throws InterruptedException - when httpClient.send fails.
   */
  public static Recipe getRecipeFromURI(String uri) throws IOException, InterruptedException,
      APIException {
    String reformattedUri = formatURI(uri);

    HttpClient httpClient = HttpClient.newBuilder().build();
    HttpRequest httpRequest = HttpRequest.newBuilder().GET()
        .uri(URI.create("https://api.edamam.com/search?r=" + reformattedUri
            + "&app_id=" + APP_ID + "&app_key=" + APP_KEY)).build();

    HttpResponse<String> response = httpClient.send(httpRequest,
        HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new APIException("API returned error " + response.statusCode());
    }
    Recipe[] recipeArray = parseRecipeJSON(response.body());
    if (recipeArray == null) {
      throw new APIException("API returned malformed JSON");
    }
    return recipeArray[0];
  }

  /**
   * This function retrieves recipes that correspond to the given query in the api.
   * @param query - the desired query to search for in the api.
   * @return - an array of recipes that correspond to the given query in the api.
   */
  public static Recipe[] getRecipesFromQuery(String query) throws IOException, InterruptedException,
      APIException, SQLException {
    HttpClient httpClient = HttpClient.newBuilder().build();
    HttpRequest httpRequest = HttpRequest.newBuilder().GET()
        .uri(URI.create("https://api.edamam.com/search?q=" + query
        + "&app_id=" + APP_ID + "&app_key=" + APP_KEY)).build();

    HttpResponse<String> response = httpClient.send(httpRequest,
        HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new APIException("API returned error " + response.statusCode());
    }

    Recipe[] recipes = parseRecipeJSON(response.body());
    if (recipes == null) {
      throw new APIException("API returned malformed JSON");
    }

//    for (Recipe r : recipes) {
//      RecipeDatabase.insertRecipe(r);
//    }

    return recipes;
  }


  /**
   * This function retrieves a given number of recipes from the api.
   * @param start - the starting index to retrieve recipes from.
   * @param end - the ending index to retrieve recipes from.
   * @return - A list of Recipe objects containing data from the api.
   */
  public static List<Recipe> getRecipeSubset(int start, int end) {
    HttpClient httpClient = HttpClient.newBuilder().build();
    HttpRequest httpRequest = HttpRequest.newBuilder().GET()
        .uri(URI.create("https://api.edamam.com/search?q=")).build(); //fix this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    return null;
  }



  /**
   * Test api function.
   */
  public static String apiCall() {
    HttpClient httpClient = HttpClient.newBuilder().build();
    HttpRequest httpRequest = HttpRequest.newBuilder().GET()
        .uri(URI.create("https://api.edamam.com/search?" +
            "q=chicken" +
            "&app_id=2a676518"
            + "&app_key=" +
            "158f55a83eee58aff1544072b788784f")).build();

    try {
      HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
      System.out.println(response.statusCode());
      System.out.println(response.body());
      return response.body();
    } catch (IOException | InterruptedException ioe) {
      ioe.printStackTrace();
      return null;
    }
  }

  /**
   * Test Gson function.
   */
  public static Recipe parseJSON() {
    String json = apiCall();
    Recipe[] recipes = parseRecipeJSON(json);
    for (int i = 0; i < recipes.length; i++) {
      System.out.println(recipes[i].getUri());
      System.out.println(recipes[i].getNutrientVals("FE")[0]);
    }
    return recipes[0];
  }
}
