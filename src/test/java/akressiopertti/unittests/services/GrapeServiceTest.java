package akressiopertti.unittests.services;

import akressiopertti.domain.Grape;
import akressiopertti.repository.GrapeRepository;
import akressiopertti.service.GrapeService;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
public class GrapeServiceTest {
    
    @Autowired
    private GrapeRepository grapeRepositoryMock;
    @Autowired
    private GrapeService grapeService;
    
    @Before
    public void setUp() {
        Mockito.reset(grapeRepositoryMock);
        
        List<Grape> grapes = new ArrayList<>();
        Grape g1 = new Grape();
        g1.setId(1L);
        g1.setName("Merlot");
        grapes.add(g1);
        Grape g2 = new Grape();
        g2.setId(2L);
        g2.setName("Chardonnay");
        grapes.add(g2);
        Grape g3 = new Grape();
        g3.setId(3L);
        g3.setName("Pinot Noir");
        grapes.add(g3);
        
        when(grapeRepositoryMock.findAll()).thenReturn(grapes);
        when(grapeRepositoryMock.findOne(1L)).thenReturn(g1);
        when(grapeRepositoryMock.findOne(2L)).thenReturn(g2);
        when(grapeRepositoryMock.findOne(3L)).thenReturn(g3);
        when(grapeRepositoryMock.findOne(4L)).thenReturn(null);
        when(grapeRepositoryMock.findByName("Chardonnay")).thenReturn(g2);
        when(grapeRepositoryMock.findByName("Mourvedre")).thenReturn(null);
        when(grapeRepositoryMock.save(g2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(grapeRepositoryMock).delete(4L);
    }
    
    @Test
    public void findAllRetrievesAllGrapes() {
        String[] grapeNames = new String[] { "Chardonnay", "Pinot Noir", "Merlot" };
        List<Grape> retrievedGrapes = grapeService.findAll();
        
        verify(grapeRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual grapes", 3, retrievedGrapes.size());
        int hits = 0;
        for(String name : grapeNames) {
            for(Grape g : retrievedGrapes) {
                if(name.equals(g.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some grape names missing", 3, hits);
        verifyNoMoreInteractions(grapeRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Grape retrievedGrape = grapeService.findOne(2L);
        
        verify(grapeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a grape by id", retrievedGrape);
        assertEquals("Found wrong grape", "Chardonnay", retrievedGrape.getName());
        verifyNoMoreInteractions(grapeRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Grape g1 = new Grape();
        g1.setName("Mourvedre");
        
        grapeService.save(g1);
        
        ArgumentCaptor<Grape> grapeArgument = ArgumentCaptor.forClass(Grape.class);
        verify(grapeRepositoryMock, times(1)).save(grapeArgument.capture());
        verifyNoMoreInteractions(grapeRepositoryMock);
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        Grape g = grapeService.remove(2L);
        verify(grapeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", g);
        assertEquals("Remove returns wrong object", (Long)2L, g.getId());
        verify(grapeRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(grapeRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        grapeService.remove(4L);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueGrape() {
        Grape g = new Grape();
        g.setName("Chardonnay");
        
        List<ObjectError> errors = grapeService.checkUniqueness(g);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(grapeRepositoryMock, times(1)).findByName("Chardonnay");
        verifyNoMoreInteractions(grapeRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingGrape() {
        Grape g = new Grape();
        g.setId(1L);
        g.setName("Merlot");
        
        List<ObjectError> errors = grapeService.checkUniqueness(g);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(grapeRepositoryMock, times(1)).findByName("Merlot");
        verifyNoMoreInteractions(grapeRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingGrapeWithDifferentId() {
        Grape g = new Grape();
        g.setId(6L);
        g.setName("Chardonnay");
        
        List<ObjectError> errors = grapeService.checkUniqueness(g);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(grapeRepositoryMock, times(1)).findByName("Chardonnay");
        verifyNoMoreInteractions(grapeRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueGrape() {
        Grape g = new Grape();
        g.setName("Mourvedre");
        
        List<ObjectError> errors = grapeService.checkUniqueness(g);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(grapeRepositoryMock, times(1)).findByName("Mourvedre");
        verifyNoMoreInteractions(grapeRepositoryMock);
    }  
}
