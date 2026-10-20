package tinderBack;

import java.util.Set;

public record Recipe(
    int m_recipeId,
    String m_name,
    String m_description,
    Set<Ingredient> m_ingredients,
    Set<FoodTag> m_tags,
    String m_imagePath,
    int m_cookingTime,
    int m_rate,
    double m_calories,
    int m_difficulty
) {}
