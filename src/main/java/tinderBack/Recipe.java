package tinderBack;

import java.util.List;

public record Recipe(
    int m_recipeId,
    List<Ingredient> m_ingredients,
    List<FoodTag> m_tags,
    String m_name,
    String m_imagePath,
    int m_cookingTime,
    int m_rate,
    float m_calories,
    int m_difficulty
) {}