package akressiopertti.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Entity
public class GrapeContent extends BaseModel {
    
    @ManyToOne
    @NotNull
    private Wine wine;
    @ManyToOne
    @NotNull
    private Grape grape;
    @Range(min = 0, max = 100)
    private int content;

    public Wine getWine() {
        return wine;
    }

    public void setWine(Wine wine) {
        this.wine = wine;
    }

    public Grape getGrape() {
        return grape;
    }

    public void setGrape(Grape grape) {
        this.grape = grape;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }    
    
}
