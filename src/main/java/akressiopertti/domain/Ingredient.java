package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Ingredient extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 100, message = "Nimi voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    @Field
    private String name;
    @NotBlank(message = "Partitiivimuoto on pakollinen tieto")
    @Length(max = 100, message = "Partitiivimuoto voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    private String partitive;
    @ManyToOne
    private FoodStuff foodStuff;
    @Length(max = 4000)
    @Column(length = 4000)
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

    public String getPartitive() {
        return partitive;
    }

    public void setPartitive(String partitive) {
        this.partitive = partitive;
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
