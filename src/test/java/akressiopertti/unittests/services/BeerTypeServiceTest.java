package akressiopertti.unittests.services;

import akressiopertti.domain.BeerType;
import akressiopertti.repository.BeerTypeRepository;
import akressiopertti.service.BeerTypeService;
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
public class BeerTypeServiceTest {
    
    @Autowired
    private BeerTypeRepository beerTypeRepositoryMock;
    @Autowired
    private BeerTypeService beerTypeService;
    
    @Before
    public void setUp() {
        Mockito.reset(beerTypeRepositoryMock);
        
        List<BeerType> beerTypes = new ArrayList<>();
        BeerType b1 = new BeerType();
        b1.setId(1L);
        b1.setName("Lager");
        beerTypes.add(b1);
        BeerType b2 = new BeerType();
        b2.setId(2L);
        b2.setName("Pils");
        beerTypes.add(b2);
        BeerType b3 = new BeerType();
        b3.setId(3L);
        b3.setName("Kriek");
        beerTypes.add(b3);
        
        when(beerTypeRepositoryMock.findAll()).thenReturn(beerTypes);
        when(beerTypeRepositoryMock.findOne(1L)).thenReturn(b1);
        when(beerTypeRepositoryMock.findOne(2L)).thenReturn(b2);
        when(beerTypeRepositoryMock.findOne(3L)).thenReturn(b3);
        when(beerTypeRepositoryMock.findOne(4L)).thenReturn(null);
        when(beerTypeRepositoryMock.findByName("Pils")).thenReturn(b2);
        when(beerTypeRepositoryMock.findByName("Indian Pale Ale")).thenReturn(null);
        when(beerTypeRepositoryMock.save(b2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(beerTypeRepositoryMock).delete(4L);
    }
    
    @Test
    public void findAllRetrievesAllBeerTypes() {
        String[] beerTypeNames = new String[] {"Pils", "Kriek", "Lager"};
        List<BeerType> retrievedBeerTypes = beerTypeService.findAll();
        
        verify(beerTypeRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual beer types", 3, retrievedBeerTypes.size());
        int hits = 0;
        for(String name : beerTypeNames) {
            for(BeerType b : retrievedBeerTypes) {
                if(name.equals(b.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some beer type names missing", 3, hits);
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneBeerType() {        
        BeerType retrievedBeerType = beerTypeService.findOne(2L);
        
        verify(beerTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a beer type by id", retrievedBeerType);
        assertEquals("Found wrong beer type", "Pils", retrievedBeerType.getName());
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        BeerType retrievedBeerType = beerTypeService.findOne(4L);
        
        verify(beerTypeRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong beer type by nonexisting id", retrievedBeerType);
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        BeerType c1 = new BeerType();
        c1.setName("Indian Pale Ale");

        beerTypeService.save(c1);
        
        ArgumentCaptor<BeerType> beerTypeArgument = ArgumentCaptor.forClass(BeerType.class);
        verify(beerTypeRepositoryMock, times(1)).save(beerTypeArgument.capture());
        verifyNoMoreInteractions(beerTypeRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        BeerType c = beerTypeService.remove(2L);
        verify(beerTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", c);
        assertEquals("Remove returns wrong object", (Long)2L, c.getId());
        verify(beerTypeRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        beerTypeService.remove(4L);    
        verify(beerTypeRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueBeerType() {
        BeerType c = new BeerType();
        c.setName("Pils");
        
        List<ObjectError> errors = beerTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(beerTypeRepositoryMock, times(1)).findByName("Pils");
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingBeerType() {
        BeerType c = new BeerType();
        c.setId(2L);
        c.setName("Pils");
        
        List<ObjectError> errors = beerTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(beerTypeRepositoryMock, times(1)).findByName("Pils");
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingBeerTypeWithDifferentId() {
        BeerType c = new BeerType();
        c.setId(6L);
        c.setName("Pils");
        
        List<ObjectError> errors = beerTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(beerTypeRepositoryMock, times(1)).findByName("Pils");
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueBeerType() {
        BeerType c = new BeerType();
        c.setName("Indian Pale Ale");

        List<ObjectError> errors = beerTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(beerTypeRepositoryMock, times(1)).findByName("Indian Pale Ale");
        verifyNoMoreInteractions(beerTypeRepositoryMock);
    }  
}
