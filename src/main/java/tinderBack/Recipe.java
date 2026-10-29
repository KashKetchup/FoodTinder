package tinderBack;

import java.util.Set;

public record Recipe(
    int recipeId,
    String name,
    String description,
    String instruction,
    Set<Ingredient> ingredients,
    Set<FoodTag> tags,
    String imagePath,
    int cookingTime,
    int rate,
    double calories,
    int difficulty
) {}
