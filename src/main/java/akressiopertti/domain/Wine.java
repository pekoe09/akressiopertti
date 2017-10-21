package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Entity
public class Wine extends Beverage {

    @Range(min = 1900, max = 2100)
    private int vintage;
    private String country;
    @NotBlank(message = "Alue on pakollinen tieto")
    @Length(max = 100, message = "Alue voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true, length = 100)
    private String region;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
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
