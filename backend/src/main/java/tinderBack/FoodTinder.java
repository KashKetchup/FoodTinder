package tinderBack;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import database.*;



public class FoodTinder {

    private final DatabaseService m_dbService;
    private final ChallengePool m_challengePool;

    public FoodTinder(DatabaseService dbService, ChallengePool pool) {
        m_challengePool = pool;
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
            throw new FTException(dbe.getMessage());
        }
    }



    public List<Recipe> findRecomendRecipes (
        Set<Integer> includedTags, Set<Integer> excludedTags,
        Set<Integer> includedIngr, Set<Integer> excludedIngr) throws FTException {
        try {
            return m_dbService.getRecipesByTags
            (
                5, 
                includedTags, excludedTags, 
                includedIngr, excludedIngr
            );
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }  
    }
    
    public List<Diet> findRecomendDiets(
    		int daysCount,
            Set<Integer> includedTags, Set<Integer> excludedTags,
            Set<Integer> includedIngr, Set<Integer> excludedIngr) throws FTException {
        try {
        	List<Recipe> breakfasts = m_dbService.getBreakfasts(daysCount, 
        			includedTags, excludedTags, includedIngr, excludedIngr);
        	List<Recipe> lunches = m_dbService.getLunches(daysCount, 
        			includedTags, excludedTags, includedIngr, excludedIngr);
        	List<Recipe> dinners = m_dbService.getDinners(daysCount, 
        			includedTags, excludedTags, includedIngr, excludedIngr);
        	Set<Diet> diets = new HashSet<Diet>();
base:      	for (Recipe breakfast : breakfasts) {
        		for (Recipe lunch : lunches) {
        			for (Recipe dinner : dinners) {
                		diets.add(new Diet(0, "", breakfast, lunch, dinner));
                		if (diets.size() == daysCount) {
                			break base;
                		}
                	}
            	}
        	}
        	if (diets.size() == daysCount) {
        		return diets.stream().toList();
        	}
        	return List.of();
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }  
    }


    public Recipe getRecipe(int id) throws FTException {
        try {
            return m_dbService.getRecipe(id);

            //DBException
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }
    
    public Diet getDiet(int id) throws FTException {
        try {
            return m_dbService.getDiet(id);
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }

    public String getSalt() throws FTException {
        try {
            return m_dbService.getSalt();
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }

    public void addUserQuery(String login, String password) throws FTException {
        try {
            return m_dbService.addUser(login, password);
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }

    public String getPassByLogin(String login) throws FTException {
        try {
            return m_dbService.getPassByLogin(login);
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }

    public String addToken(String token, long currentTime) throws FTException {
        try {
            return m_dbService.addToken(token, currentTime);
        } catch (DBException dbe) {
            throw new FTException(dbe.getMessage());
        }
    }

    public String getChallenge() {
        return m_challengePool.getChallenge();
    }

    public boolean checkChallenge(String challenge) {
        return m_challengePool.checkChallenge(challenge);
    }


    
}