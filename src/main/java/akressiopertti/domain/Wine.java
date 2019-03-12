package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Range;

@Entity
public class Wine extends Beverage {

    @Range(min = 1900, max = 2100)
    private int vintage;
    @ManyToOne
    private Geography country;
    @ManyToOne
    private Geography region;
    private String producer;
    @ManyToOne
    private WineType wineType;
    @OneToMany(mappedBy = "wine")
    private List<GrapeContent> grapes;
    @ManyToMany(mappedBy = "wines")
    private List<Recipe> recipes;
    
    public Wine(){
        this.recipes = new ArrayList<>();
        this.grapes = new ArrayList<>();
    }

    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    public Geography getCountry() {
        return country;
    }

    public void setCountry(Geography country) {
        this.country = country;
    }

    public Geography getRegion() {
        return region;
    }

    public void setRegion(Geography region) {
        this.region = region;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public WineType getWineType() {
        return wineType;
    }

    public void setWineType(WineType wineType) {
        this.wineType = wineType;
    }

    public List<GrapeContent> getGrapes() {
        return grapes;
    }

    public void setGrapes(List<GrapeContent> grapes) {
        this.grapes = grapes;
    }  
    
    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
  
}
