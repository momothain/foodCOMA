package edu.brown.cs.student.foodCOMA.database;

import edu.brown.cs.student.database.APIException;
import edu.brown.cs.student.database.FieldParser;
import edu.brown.cs.student.database.RecipeDatabase;
import edu.brown.cs.student.food.NutrientInfo;
import edu.brown.cs.student.food.Recipe;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the methods contained within the FieldParser class.
 *
 * Lots of things are commented out in this class -- running each test every time will use up our
 * API queries per minute.
 */
public class FieldParserTest {
//  @Test
//  public void testGetRecipesFromQuery() {
//    NutrientInfo.createNutrientsList();
//    try {
//      RecipeDatabase.loadDatabase("data/recipeDatabase.sqlite3");
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//    try {
//      Recipe[] recipes = new Recipe[0];
//      try {
//        recipes = FieldParser.getRecipesFromQuery("chicken");
//      } catch (SQLException e) {
//        System.out.println("SQL ERROR IN TEST");
//      }
//      assertNotNull(recipes);
//      for (Recipe r : recipes) {
//        assertNotNull(r);
//        assertNotNull(r.getUri());
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (InterruptedException | APIException e) {
//      e.printStackTrace();
//    }
//  }
}