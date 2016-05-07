package akressiopertti.unittests.services;

import akressiopertti.domain.Course;
import akressiopertti.repository.CourseRepository;
import akressiopertti.service.CourseService;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.ObjectError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestContext.class})
public class CourseServiceTest {
    
    @Autowired
    private CourseRepository courseRepositoryMock;
    @Autowired
    private CourseService courseService;
    
    @Before
    public void setUp() {

    }
    
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
        
        verify(courseRepositoryMock, times(2)).findOne(4L);
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
        Course c = courseService.remove(2L);
        
        assertNotNull("Remove does not return the object", c);
        assertEquals("Remove returns wrong object", (Long)2L, (Long)c.getId());
        verify(courseRepositoryMock, times(1)).delete(2L);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeThrowsExceptionForNonexistingId() {
        Course c = courseService.remove(4L);        
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueCourse() {
        Course c = new Course();
        c.setName("Alkuruoka");
        c.setOrdinality(5);
        
        List<ObjectError> errors = courseService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingCourse() {
        Course c = new Course();
        c.setId(1L);
        c.setName("Alkuruoka");
        c.setOrdinality(5);
        
        List<ObjectError> errors = courseService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());        
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingCourseWithDifferentId() {
        Course c = new Course();
        c.setId(6L);
        c.setName("Alkuruoka");
        c.setOrdinality(5);
        
        List<ObjectError> errors = courseService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());  
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueCourse() {
        Course c = new Course();
        c.setName("Primi Piatti");
        c.setOrdinality(1);
        
        List<ObjectError> errors = courseService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
    }
}
