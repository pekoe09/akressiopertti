package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Grape extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 50, message = "Nimi voi olla enintään 50 merkkiä pitkä")
    @Column(unique = true, length = 50)
    @Field(index = Index.YES)
    private String name;
    @Column(length = 5000)
    @Length(max = 5000, message = "Kuvaus voi olla enintään 5000 merkkiä pitkä")
    private String description;
    @ManyToMany
    private List<Wine> wines;
    
    public Grape() {
        this.wines = new ArrayList<>();
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

    public List<Wine> getWines() {
        return wines;
    }

    public void setWines(List<Wine> wines) {
        this.wines = wines;
    }    
}
