package tinderBack;

import java.util.Set;

public class Recipe {
    private final int m_recipeId;
    private final Set<Ingredient> m_ingredients;
    private final Set<FoodTag> m_tags;
    private final String m_name;
    private final String m_desc;
    private final String m_imagePath;
    private final int m_cookingTime;
    private final int m_rate;
    private final double m_calories;
    private final int m_difficulty;

    public Recipe(
        int id,
        String name, 
        String description,
        Set<Ingredient> ingredients, 
        Set<FoodTag> tags, 
        String imagePath,
        int cookingTime,
        int rate,
        double calories,
        int difficulty
    ) {
        m_recipeId = id;
        m_ingredients = ingredients;
        m_tags = tags;
        m_name = name;
        m_desc = description;
        m_imagePath = imagePath;
        m_cookingTime = cookingTime;
        m_rate = rate;
        m_calories = calories;
        m_difficulty = difficulty;
    }

    public int getId() {
        return m_recipeId;
    }
    public String getName() {
        return m_name;
    }
    public String getDescription() {
        return m_desc;
    }
    public String getImagePath() {
        return m_imagePath;
    }
    public int getCookingTime() {
        return m_cookingTime;
    }
    public double getCalories() {
        return m_calories;
    }
    public int getRate() {
        return m_rate;
    } 
    public float getDifficulty() {
        return m_difficulty;
    }

    public Set<Ingredient> getIngredients() {
        return m_ingredients;
    }
    public Set<FoodTag> getTags() {
        return m_tags;
    }
}