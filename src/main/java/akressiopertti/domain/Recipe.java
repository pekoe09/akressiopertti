package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Recipe extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 200, message = "Nimi voi olla enintään 200 merkkiä pitkä")
    @Column(unique = true)
    private String title;
    @Column(length = 5000)
    @Length(max = 5000, message = "Ohjeteksti voi olla enintään 5000 merkkiä pitkä")
    private String instructions;
    @Min(0)
    private int preparationTime;
    private boolean needsMarinating;
    @OneToMany(mappedBy = "recipe")
    private List<RecipeIngredient> recipeIngredients;
    @Length(max = 300, message = "Lähde voi olla enintään 300 merkkiä pitkä")
    @Column(length = 300)
    private String source;
    @Column(length = 5000)
    @Length(max = 5000, message = "Kommentti voi olla enintään 5000 merkkiä pitkä")
    private String comment;
    @ManyToOne
    private DishType dishType;
    @ManyToMany
    private List<FoodStuff> foodStuffs;
    @ManyToOne
    private Course course;
    @ManyToMany
    private List<Recipe> relatedRecipes;
    @ManyToMany
    private List<Beer> beers;
    @ManyToMany
    private List<Wine> wines;
    
    public Recipe(){
        this.needsMarinating = false;
        this.recipeIngredients = new ArrayList<>();
        this.foodStuffs = new ArrayList<>();
        this.relatedRecipes = new ArrayList<>();
        this.beers = new ArrayList<>();
        this.wines = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public boolean isNeedsMarinating() {
        return needsMarinating;
    }

    public void setNeedsMarinating(boolean needsMarinating) {
        this.needsMarinating = needsMarinating;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DishType getDishType() {
        return dishType;
    }

    public void setDishType(DishType dishType) {
        this.dishType = dishType;
    }

    public List<FoodStuff> getFoodStuffs() {
        return foodStuffs;
    }

    public void setFoodStuffs(List<FoodStuff> foodStuffs) {
        this.foodStuffs = foodStuffs;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Recipe> getRelatedRecipes() {
        return relatedRecipes;
    }

    public void setRelatedRecipes(List<Recipe> relatedRecipes) {
        this.relatedRecipes = relatedRecipes;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setBeers(List<Beer> beers) {
        this.beers = beers;
    }

    public List<Wine> getWines() {
        return wines;
    }

    public void setWines(List<Wine> wines) {
        this.wines = wines;
    }
    
}
