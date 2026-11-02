package tinderBack;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import database.*;



public class FoodTinder {

    private final DatabaseService m_dbService;

    public FoodTinder(DatabaseService dbService) {
        m_dbService = dbService;
    }

    public List<Ingredient> getAllIngredients() throws FTException {
        try {
            return m_dbService.getAllIngredients();
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
        
    }

    public List<FoodTag> getAllTags() throws FTException {
        try {
            return m_dbService.getAllTags();
        } catch (DBException dbe) {
            throw new FTException("DB err");
        }
    }

    //TEST EXAMPLE

    public List<Recipe> findRecomendRecipes (
        Set<Integer> includedTags, 
        Set<Integer> excludedTags,
        Set<Integer> includedIngr,
        Set<Integer> excludedIngr
    ) throws FTException {
        try {

            Set<Ingredient> ingr = new HashSet<>();
            Set<FoodTag> tags = new HashSet<>();
            Recipe rec1 = new Recipe(1, "Plov", "Plov - plov", "Do some thing", ingr, tags, "./home/al6ed0/Изображения/Emote_60000398.png", 1000, 1, 10.2, 100);
            Recipe rec2 = new Recipe(2, "Oleg", "Oleg - Olga", "Do some thing", ingr, tags, "./home/al6ed0/Изображения/Emote_60000398.png", 1000, 1, 10.2, 100);
            
            return List.of(rec1, rec2); 

            // return m_dbService.getRecipesByTags
            // (
            //     5, 
            //     includedTags, 
            //     includedIngr, 
            //     excludedTags, 
            //     excludedIngr
            // );

            //DBException
        } catch (Exception dbe) {
            throw new FTException(dbe.getMessage());
        }  
    }

    //TEST EXAMPLE

    public Recipe getRecipe(int id) throws FTException {
        try {
            if (id == 1) {
                Set<Ingredient> ingr = new HashSet<>();
                Set<FoodTag> tags = new HashSet<>();
                return new Recipe(1, "Plov", "Plov - plov", "Do some thing", ingr, tags, "./home/al6ed0/Изображения/Emote_60000398.png", 1000, 1, 10.2, 100);
            } 
            if (id == 2) {
                Set<Ingredient> ingr = new HashSet<>();
                Set<FoodTag> tags = new HashSet<>();
                return new Recipe(2, "Oleg", "Oleg - Olga", "Do some thing", ingr, tags, "./home/al6ed0/Изображения/Emote_60000398.png", 1000, 1, 10.2, 100);
            } 
            return null;
            //DBException
        } catch (Exception dbe) {
            throw new FTException(dbe.getMessage());
        }
    }
    
}