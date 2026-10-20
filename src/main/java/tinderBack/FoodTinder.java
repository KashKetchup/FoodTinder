// package tinderBack;

// import java.util.Set;
// import java.util.List;
// import database.DatabaseSevice;
// import tinderBack.FTException;



// public class FoodTinder {

//     private final DatabaseService m_dbService;

//     public FoodTinder(DatabaseService dbService) {
//         m_dbService = dbService;
//     }

//     public List<Ingredient> getAllIngredients() {
//         try {
//             return m_dbService.getAllIngredients();
//         } catch (DBException dbe) {
//             throw new FTException(dbe.getMessage());
//         }
        
//     }

//     public List<FoodTag> getAllTags() {
//         try {
//             return m_dbService.getAllTags();
//         } catch (DBException dbe) {
//             throw new FTException(dbe.getMessage());
//         }
//     }

//     public List<Recipe> findRecomendRecipes(
//         Set<Integer> includedTags, 
//         Set<Integer> excludedTags,
//         Set<Integer> includedIngr,
//         Set<Integer> excludedIngr
//     ) {
//         try {
//             m_dbService.getRecipesByTags(5, includedTags, includedIngr, excludedTags, excludedIngr);
//         } catch (DBException dbe) {
//             throw new FTException(dbe.getMessage());
//         }  
//     }

//     public FullRecipe getRecipe(int id) {
//         try {
//             return m_dbService.getRecipe(id);
//         } catch (DBException dbe) {
//             throw new FTException(dbe.getMessage());
//         }
//     }
    
// }