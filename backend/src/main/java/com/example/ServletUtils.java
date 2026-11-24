package com.example;

import tinderBack.Diet;
import tinderBack.Recipe;

public class ServletUtils {
	public static String convertRecipeToJSON(Recipe recipe) {
		return String.format(
                "{\"id\": \"%d\"" + 
                ", \"title\": \"%s\"" + 
                ", \"description\": \"%s\"" + 
                ", \"image_url\": \"%s\"" +
                ", \"cookingTime\": \"%d\"" +
                ", \"rate\": \"%d\"" +
                ", \"calories\": \"%f\"" +
                ", \"proteins\": \"%f\"" +
                ", \"fats\": \"%f\"" +
                ", \"carbs\": \"%f\"" +
                ", \"difficulty\": \"%d\"}",
                recipe.recipeId(), recipe.name(),
                recipe.description(), recipe.imagePath(),
                recipe.cookingTime(), recipe.rate(), 
                recipe.calories(), recipe.proteins(),
                recipe.fats(), recipe.carbs(), recipe.difficulty());
	}
	
	public static String convertDietToJSON(Diet diet) {
		return String.format(
				"{\"id\": \"%d\"" + 
			    ", \"description\": \"%s\"" +
			    ", \"breakfast\": %s" +
			    ", \"lunch\": %s" +
			    ", \"dinner\": %s}",
			    diet.dietId(), diet.description(),
			    ServletUtils.convertRecipeToJSON(diet.breakfast()),
			    ServletUtils.convertRecipeToJSON(diet.lunch()),
			    ServletUtils.convertRecipeToJSON(diet.dinner()));
	}
}
