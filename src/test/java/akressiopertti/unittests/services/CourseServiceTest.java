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
import static org.mockito.Mockito.when;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
        Mockito.reset(courseRepositoryMock);
        
        List<Course> courses = new ArrayList<>();
        Course c1 = new Course();
        c1.setId(1L);
        c1.setName("Alkuruoka");
        c1.setOrdinality(1);
        courses.add(c1);
        Course c2 = new Course();
        c2.setId(2L);
        c2.setName("Pääruoka");
        c2.setOrdinality(2);
        courses.add(c2);
        Course c3 = new Course();
        c3.setId(3L);
        c3.setName("Jälkiruoka");
        c3.setOrdinality(3);
        courses.add(c3);
        
        when(courseRepositoryMock.findAll()).thenReturn(courses);
        when(courseRepositoryMock.findOne(1L)).thenReturn(c1);
        when(courseRepositoryMock.findOne(2L)).thenReturn(c2);
        when(courseRepositoryMock.findOne(3L)).thenReturn(c3);
        when(courseRepositoryMock.findOne(4L)).thenReturn(null);
        when(courseRepositoryMock.findByName("Alkuruoka")).thenReturn(c1);
        when(courseRepositoryMock.findByName("Primi Piatti")).thenReturn(null);
//        when(mockRepo.save((Course)anyObject())).thenReturn((Course)returnsFirstArg());
        when(courseRepositoryMock.save(c2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(courseRepositoryMock).delete(4L);
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
        verifyNoMoreInteractions(courseRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneCourse() {        
        Course retrievedCourse = courseService.findOne(2L);
        
        verify(courseRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a course by id", retrievedCourse);
        assertEquals("Found wrong course", "Pääruoka", retrievedCourse.getName());
        verifyNoMoreInteractions(courseRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Course retrievedCourse = courseService.findOne(4L);
        
        verify(courseRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong course by nonexisting id", retrievedCourse);
        verifyNoMoreInteractions(courseRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Course c1 = new Course();
        c1.setName("Juustolautanen");
        c1.setOrdinality(4);
        
        courseService.save(c1);
        
        ArgumentCaptor<Course> courseArgument = ArgumentCaptor.forClass(Course.class);
        verify(courseRepositoryMock, times(1)).save(courseArgument.capture());
        verifyNoMoreInteractions(courseRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        Course c = courseService.remove(2L);
        verify(courseRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", c);
        assertEquals("Remove returns wrong object", (Long)2L, c.getId());
        verify(courseRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(courseRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        courseService.remove(4L);    
        verify(courseRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(courseRepositoryMock);
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
        verify(courseRepositoryMock, times(1)).findByName("Alkuruoka");
        verifyNoMoreInteractions(courseRepositoryMock);
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
        verify(courseRepositoryMock, times(1)).findByName("Alkuruoka");
        verifyNoMoreInteractions(courseRepositoryMock);
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
        verify(courseRepositoryMock, times(1)).findByName("Alkuruoka");
        verifyNoMoreInteractions(courseRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueCourse() {
        Course c = new Course();
        c.setName("Primi Piatti");
        c.setOrdinality(1);
        
        List<ObjectError> errors = courseService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(courseRepositoryMock, times(1)).findByName("Primi Piatti");
        verifyNoMoreInteractions(courseRepositoryMock);
    }  
}
