package ml.ayushdhanai.foodguide;

public class Model {

    private int image;
    private String title;
    private String ingredients;
    private String recipe;

    public Model(int image,String title,String ingredients,String recipe) {
        this.image = image;
        this.title = title;
        this.ingredients = ingredients;
        this.recipe = recipe;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getFoodName() {
        return title;
    }

    public void setFoodName(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

}
