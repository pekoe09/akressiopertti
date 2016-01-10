package akressiopertti.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RecipeIngredient extends BaseModel{
    
    @ManyToOne
    private Recipe recipe;
    @ManyToOne
    private Ingredient ingredient;
    private float amountFloat;
    private int amountInteger;
    @ManyToOne
    private Measure measure;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public float getAmountFloat() {
        return amountFloat;
    }

    public void setAmountFloat(float amountFloat) {
        this.amountFloat = amountFloat;
    }

    public int getAmountInteger() {
        return amountInteger;
    }

    public void setAmountInteger(int amountInteger) {
        this.amountInteger = amountInteger;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    
}
