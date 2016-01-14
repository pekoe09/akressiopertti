package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Ingredient extends BaseModel {
    
    private String name;
    @ManyToOne
    private FoodStuff foodStuff;
    private String comment;
    @OneToMany(mappedBy = "ingredient")
    private List<RecipeIngredient> recipeIngredients;
    
    public Ingredient(){
        this.recipeIngredients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FoodStuff getFoodStuff() {
        return foodStuff;
    }

    public void setFoodStuff(FoodStuff foodStuff) {
        this.foodStuff = foodStuff;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
    
}
