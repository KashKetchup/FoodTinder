package tinderBack;

public interface FoodTinder {
    public Ingredient[] getAllIngredients();
    public FoodTag[] getAllTags();
    public FullRecipe getRecipe(int id);
    public Recipe[] findRecomendRecipes(
        FoodTag[] includedTags, 
        FoodTag[] excludedTags,
        Ingredient[] includedIngr,
        Ingredient[] excludedIngr
    );
    
}
