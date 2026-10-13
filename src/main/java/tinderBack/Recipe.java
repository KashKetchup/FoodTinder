package tinderBack;

class Recipe {
    private final int m_recipeId;
    private final Ingredient[] m_ingredients;
    private final FoodTag[] m_tags;
    private final String m_name;
    private final String m_imagePath;
    private final int m_cookingTime;
    private final int m_rate;
    private final float m_calories;
    private final int m_difficulty;

    public Recipe(
        int id,
        String name, 
        Ingredient[] ingredients, 
        FoodTag[] tags, 
        String imagePath,
        int cookingTime,
        int rate,
        float calories,
        int difficulty
    ) {
        m_recipeId = id;
        m_ingredients = ingredients;
        m_tags = tags;
        m_name = name;
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
    public String getImagePath() {
        return m_imagePath;
    }
    public int getCookingTime() {
        return m_cookingTime;
    }
    public float getCalories() {
        return m_calories;
    }
    public int getRate() {
        return m_rate;
    } 
    public float getDifficulty() {
        return m_difficulty;
    }

    public Ingredient[] getIngredients() {
        return m_ingredients;
    }
    public FoodTag[] getTags() {
        return m_tags;
    }
}