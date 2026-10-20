package tinderBack;

import java.util.Set;

public record FullRecipe(
    int m_recipeId,
    String m_name,
    String instruction,
    Set<Ingredient> m_ingredients,
    Set<FoodTag> m_tags,
    String m_imagePath,
    int m_cookingTime,
    int m_rate,
    double m_calories,
    int m_difficulty 
) {}
