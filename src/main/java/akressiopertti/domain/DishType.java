package akressiopertti.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class DishType extends BaseModel {
    
    private String name;
    @OneToMany(mappedBy = "dishType")
    private List<Recipe> recipes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
    
}
