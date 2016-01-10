package akressiopertti.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Measure extends BaseModel {
    
    private String name;
    @ManyToOne
    private MeasureType measureType;
    @OneToMany(mappedBy = "measure")
    private List<RecipeIngredient> recipeIngredients;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
    
}
