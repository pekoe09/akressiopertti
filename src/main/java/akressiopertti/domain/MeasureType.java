/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class MeasureType extends BaseModel {
    
    @NotBlank(message = "Nimi on pakollinen tieto")
    @Length(max = 100, message = "Nimi voi olla enintään 100 merkkiä pitkä")
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "measureType")
    private List<Measure> measures;
    
    public MeasureType(){
        this.measures = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }
    
}
