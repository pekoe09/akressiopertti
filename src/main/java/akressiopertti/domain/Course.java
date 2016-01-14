package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Course extends BaseModel {
    
    private String name;
    private int ordinality;
    @OneToMany(mappedBy = "course")
    private List<Recipe> recipes;
    
    public Course(){
        this.recipes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrdinality() {
        return ordinality;
    }

    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
    
}
