package akressiopertti.unittests.services;

import akressiopertti.domain.Course;
import akressiopertti.repository.CourseRepository;
import akressiopertti.service.CourseService;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestContext.class})
public class CourseServiceTest {
    
    @Autowired
    private CourseRepository courseRepositoryMock;
    @Autowired
    private CourseService courseService;
    
    @Test
    public void findAllRetrievesAllCourses() {
        String[] courseNames = new String[] {"Alkuruoka", "Pääruoka", "Jälkiruoka"};
        List<Course> retrievedCourses = courseService.findAll();
        
        verify(courseRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual courses", 3, retrievedCourses.size());
        int hits = 0;
        for(String name : courseNames) {
            for(Course c : retrievedCourses) {
                if(name.equals(c.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some course names missing", 3, hits);
    }
    
    @Test
    public void findOneRetrievesOneCourse() {
        
        Course retrievedCourse = courseService.findOne(2L);
        
        verify(courseRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a course by id", retrievedCourse);
        assertEquals("Found wrong course", "Pääruoka", retrievedCourse.getName());
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Course retrievedCourse = courseService.findOne(4L);
        
        verify(courseRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong course by nonexisting id", retrievedCourse);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Course c1 = new Course();
        c1.setName("Juustolautanen");
        c1.setOrdinality(4);
        
        Course savedCourse = courseService.save(c1);
        
        ArgumentCaptor<Course> courseArgument = ArgumentCaptor.forClass(Course.class);
        verify(courseRepositoryMock, times(1)).save(courseArgument.capture());
        
        
    }
    
    @Test
    public void saveThrowsExceptionWhenCannotSave() {
        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        
    }
    
    @Test
    public void removeThrowsExceptionForNonexistingId() {
        
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueCourse() {
        
    }
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueCourse() {
        
    }
}
