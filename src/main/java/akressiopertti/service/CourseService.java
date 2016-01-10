package akressiopertti.service;

import akressiopertti.domain.Course;
import akressiopertti.repository.CourseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        courseRepository.delete(id);
        return course;
    }

}
