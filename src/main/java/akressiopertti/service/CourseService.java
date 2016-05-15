package akressiopertti.service;

import akressiopertti.domain.Course;
import akressiopertti.repository.CourseRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findOne(Long id) {
        return courseRepository.findOne(id);
    }
    
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Course remove(Long id) {
        Course course = courseRepository.findOne(id);
        if(course == null) {
            throw new IllegalArgumentException("Cannot remove object with id " + id.toString());
        }
        courseRepository.delete(id);
        return course;
    }

    public List<ObjectError> checkUniqueness(Course course) {
        List<ObjectError> errors = new ArrayList<>();
        Course anotherCourse = courseRepository.findByName(course.getName());
        if(anotherCourse != null && (course.getId() == null || !anotherCourse.getId().equals(course.getId()))){
            errors.add(new ObjectError("name", "Nimi on jo varattu"));
        }
        return errors;
    }

}
