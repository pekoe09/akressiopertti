package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class FoodStuff extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 100, message = "Nimi voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    private String name;
    private boolean isFoodCategory;
    @OneToMany(mappedBy = "foodStuff")
    private List<Ingredient> ingredients;
    @ManyToMany(mappedBy = "foodStuffs")
    private List<Recipe> recipes;
    
    public FoodStuff(){
        this.isFoodCategory = false;
        this.ingredients = new ArrayList<>();
        this.recipes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsFoodCategory() {
        return isFoodCategory;
    }

    public void setIsFoodCategory(boolean isFoodCategory) {
        this.isFoodCategory = isFoodCategory;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
    
}
