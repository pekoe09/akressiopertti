package akressiopertti.unittests.services;

import akressiopertti.domain.Wine;
import akressiopertti.repository.WineRepository;
import akressiopertti.service.WineService;
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
public class WineServiceTest {
    
    @Autowired
    private WineRepository wineRepositoryMock;
    @Autowired
    private WineService wineService;
    
    @Before
    public void setUp() {
        Mockito.reset(wineRepositoryMock);
        
        List<Wine> wines = new ArrayList<>();
        Wine c1 = new Wine();
        c1.setId(1L);
        c1.setName("Villa Cafaggio");
        c1.setRegion("Toscana");
        c1.setProducer("Cafaggio2");
        wines.add(c1);
        Wine c2 = new Wine();
        c2.setId(2L);
        c2.setName("Miss Harry");
        c2.setRegion("Australia");
        c2.setProducer("Lindemans");
        wines.add(c2);
        Wine c3 = new Wine();
        c3.setId(3L);
        c3.setName("Kung Fu Girl");
        c3.setRegion("California");
        c3.setProducer("Napa");
        wines.add(c3);
        List<Wine> nameHits = new ArrayList<>();
        nameHits.add(c2);
        
        when(wineRepositoryMock.findAll()).thenReturn(wines);
        when(wineRepositoryMock.findOne(1L)).thenReturn(c1);
        when(wineRepositoryMock.findOne(2L)).thenReturn(c2);
        when(wineRepositoryMock.findOne(3L)).thenReturn(c3);
        when(wineRepositoryMock.findOne(4L)).thenReturn(null);
        when(wineRepositoryMock.findByName("Harry")).thenReturn(nameHits);
        when(wineRepositoryMock.findByName("Jekel")).thenReturn(null);
        when(wineRepositoryMock.save(c2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(wineRepositoryMock).delete(4L);
    }
    
    @Test
    public void findAllRetrievesAllWines() {
        String[] wineNames = new String[] {"Miss Harry", "Kung Fu Girl", "Villa Cafaggio"};
        List<Wine> retrievedWines = wineService.findAll();
        
        verify(wineRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual wines", 3, retrievedWines.size());
        int hits = 0;
        for(String name : wineNames) {
            for(Wine c : retrievedWines) {
                if(name.equals(c.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some wine names missing", 3, hits);
        verifyNoMoreInteractions(wineRepositoryMock);
    }
//    
//    @Test
//    public void findOneRetrievesOneWine() {        
//        Wine retrievedWine = wineService.findOne(2L);
//        
//        verify(wineRepositoryMock, times(1)).findOne(2L);
//        assertNotNull("Didn't find a wine by id", retrievedWine);
//        assertEquals("Found wrong wine", "Miss Harry", retrievedWine.getName());
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }
//    
//    @Test
//    public void findOneReturnsNullForNonexistingId() {
//        Wine retrievedWine = wineService.findOne(4L);
//        
//        verify(wineRepositoryMock, times(1)).findOne(4L);
//        assertNull("Found wrong wine by nonexisting id", retrievedWine);
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }
//    
//    @Test
//    public void saveAttemptsToSaveToRepository() {
//        Wine c1 = new Wine();
//        c1.setName("Comte de Champagne");
//        c1.setRegion("Champagne");
//        c1.setProducer("Taittinger");
//        
//        wineService.save(c1);
//        
//        ArgumentCaptor<Wine> wineArgument = ArgumentCaptor.forClass(Wine.class);
//        verify(wineRepositoryMock, times(1)).save(wineArgument.capture());
//        verifyNoMoreInteractions(wineRepositoryMock);        
//    }
//    
//    @Test
//    public void removeAttemptsToRemoveFromRepository() {
//        Wine c = wineService.remove(2L);
//        verify(wineRepositoryMock, times(1)).findOne(2L);
//        assertNotNull("Remove does not return the object", c);
//        assertEquals("Remove returns wrong object", (Long)2L, c.getId());
//        verify(wineRepositoryMock, times(1)).delete(2L);
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }
//    
//    @Test(expected = IllegalArgumentException.class)
//    public void removeThrowsExceptionForNonexistingId() {
//        wineService.remove(4L);    
//        verify(wineRepositoryMock, times(1)).findOne(2L);
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }
//    
//    @Test
//    public void uniquenessCheckReturnsErrorForNonUniqueWine() {
//        Wine c = new Wine();
//        c.setName("Alkuruoka");
//        c.setRegion("");
//        c.setProducer("");
//        
//        List<ObjectError> errors = wineService.checkUniqueness(c);
//        assertNotNull("No error list is returned from method", errors);
//        assertEquals("Method returns wrong number of errors", 1, errors.size());
//        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
//        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
//        verify(wineRepositoryMock, times(1)).findByName("Alkuruoka");
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }
// 
//    @Test
//    public void uniquenessCheckDoesNotReturnErrorForExistingWine() {
//        Wine c = new Wine();
//        c.setId(1L);
//        c.setName("Alkuruoka");
//        c.setOrdinality(5);
//        
//        List<ObjectError> errors = wineService.checkUniqueness(c);
//        assertNotNull("No error list is returned from method", errors);
//        assertEquals("Method returns wrong number of errors", 0, errors.size());  
//        verify(wineRepositoryMock, times(1)).findByName("Alkuruoka");
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }   
//    
//    @Test
//    public void uniquenessCheckReturnsErrorForExistingWineWithDifferentId() {
//        Wine c = new Wine();
//        c.setId(6L);
//        c.setName("Alkuruoka");
//        c.setOrdinality(5);
//        
//        List<ObjectError> errors = wineService.checkUniqueness(c);
//        assertNotNull("No error list is returned from method", errors);
//        assertEquals("Method returns wrong number of errors", 1, errors.size());
//        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
//        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
//        verify(wineRepositoryMock, times(1)).findByName("Alkuruoka");
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }     
//    
//    @Test
//    public void uniquenessCheckReturnsEmptyListForUniqueWine() {
//        Wine c = new Wine();
//        c.setName("Primi Piatti");
//        c.setOrdinality(1);
//        
//        List<ObjectError> errors = wineService.checkUniqueness(c);
//        assertNotNull("No error list is returned from method", errors);
//        assertEquals("Method returns wrong number of errors", 0, errors.size());
//        verify(wineRepositoryMock, times(1)).findByName("Primi Piatti");
//        verifyNoMoreInteractions(wineRepositoryMock);
//    }  
}
