package akressiopertti.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.joda.time.DateTime;

@Entity
public class Recipe extends BaseModel {
    
    private String title;
    private String instructions;
    private DateTime preparationTime;
    private boolean needsMarinating;
    @OneToMany(mappedBy = "recipe")
    private List<RecipeIngredient> recipeIngredients;
    private String source;
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

    public DateTime getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(DateTime preparationTime) {
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
