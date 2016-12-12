package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class BeerType extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 50, message = "Nimi voi olla enintään 50 merkkiä pitkä")
    @Column(unique = true, length = 50)
    @Field(index = Index.YES)
    private String name;
    @Column(length = 5000)
    @Length(max = 5000, message = "Kuvaus voi olla enintään 5000 merkkiä pitkä")
    private String description;
    @OneToMany(mappedBy = "beerType")
    private List<Beer> beers;
    
    public BeerType() {
        this.beers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setBeers(List<Beer> beers) {
        this.beers = beers;
    }    
    
}
