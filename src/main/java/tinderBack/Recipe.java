package tinderBack;

public interface Recipe {
    public Ingredient[] getIngredients();
    public FoodTag[] getTags();

    public String getName();
    public String getImagePath();
    public String getCookingTime(); // Expl: "1,5 hour"
    public int getCalories();
    public float getRate();       //0-5*
    public float getDifficulty(); //0-5*

    //Mb move some to fullRec.

}