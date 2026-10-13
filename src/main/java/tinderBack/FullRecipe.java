package tinderBack;

public record FullRecipe(
    int m_recipeId,
    Ingredient[] m_ingredients,
    FoodTag[] m_tags,
    String m_name,
    String m_imagePath,
    int m_cookingTime,
    int m_rate,
    float m_calories,
    int m_difficulty,
    String instruction
) {}