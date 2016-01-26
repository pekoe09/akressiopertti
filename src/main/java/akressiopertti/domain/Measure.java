package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Measure extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 100, message = "Nimi voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    private String name;
    @NotBlank(message = "Partitiivimuoto on pakollinen tieto")
    @Length(max = 100, message = "Partitiivimuoto voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    private String partitive;
    @NotBlank(message = "Lyhenne on pakollinen tieto")
    @Length(max = 5, message = "Lyhenne voi olla enintään 5 merkkiä pitkä")
    @Column(unique = true)
    private String abbreviation;
    @ManyToOne
    private MeasureType measureType;
    @OneToMany(mappedBy = "measure")
    private List<RecipeIngredient> recipeIngredients;
    
    public Measure(){
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
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
