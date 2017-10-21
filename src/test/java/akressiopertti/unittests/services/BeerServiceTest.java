package akressiopertti.unittests.services;

import akressiopertti.domain.Beer;
import akressiopertti.domain.BeerType;
import akressiopertti.repository.BeerRepository;
import akressiopertti.service.BeerService;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
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
public class BeerServiceTest {
    
    @Autowired
    private BeerRepository beerRepositoryMock;
    @Autowired
    private BeerService beerService;
    
    @Before
    public void setUp() {
        Mockito.reset(beerRepositoryMock);
        
        List<Beer> beers = new ArrayList<>();
        Beer c1 = new Beer();
        c1.setId(1L);
        c1.setName("Heineken");
        c1.setBrewery("Heineken2");
        beers.add(c1);
        Beer c2 = new Beer();
        c2.setId(2L);
        c2.setName("Asahi");
        c2.setBrewery("Asahi2");
        beers.add(c2);
        Beer c3 = new Beer();
        c3.setId(3L);
        c3.setName("Paulaner");
        c3.setBrewery("Paulaner2");
        beers.add(c3);
        
        when(beerRepositoryMock.findAll()).thenReturn(beers);
        when(beerRepositoryMock.findOne(1L)).thenReturn(c1);
        when(beerRepositoryMock.findOne(2L)).thenReturn(c2);
        when(beerRepositoryMock.findOne(3L)).thenReturn(c3);
        when(beerRepositoryMock.findOne(4L)).thenReturn(null);
        when(beerRepositoryMock.findByName("Heineken")).thenReturn(c1);
        when(beerRepositoryMock.findByName("Leffe")).thenReturn(null);
        when(beerRepositoryMock.save(any(Beer.class))).thenReturn(c1);
        Mockito.doThrow(DataAccessException.class).when(beerRepositoryMock).delete(4L);
    }
    
    @Test
    public void findAllRetrievesAllBeers() {
        String[] beerNames = new String[] {"Asahi", "Paulaner", "Heineken"};
        List<Beer> retrievedBeers = beerService.findAll();
        
        verify(beerRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual beers", 3, retrievedBeers.size());
        int hits = 0;
        for(String name : beerNames) {
            for(Beer c : retrievedBeers) {
                if(name.equals(c.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some beer names missing", 3, hits);
        verifyNoMoreInteractions(beerRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneBeer() {        
        Beer retrievedBeer = beerService.findOne(2L);
        
        verify(beerRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a beer by id", retrievedBeer);
        assertEquals("Found wrong beer", "Asahi", retrievedBeer.getName());
        verifyNoMoreInteractions(beerRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Beer retrievedBeer = beerService.findOne(4L);
        
        verify(beerRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong beer by nonexisting id", retrievedBeer);
        verifyNoMoreInteractions(beerRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Beer c1 = new Beer();
        c1.setName("Hoegaarden");
        c1.setBrewery("Hoegaarden2");
        c1.setBeerType(null);
        
        beerService.save(c1);
        
        ArgumentCaptor<Beer> beerArgument = ArgumentCaptor.forClass(Beer.class);
        verify(beerRepositoryMock, times(1)).save(beerArgument.capture());
        verifyNoMoreInteractions(beerRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        Beer c = beerService.remove(2L);
        verify(beerRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", c);
        assertEquals("Remove returns wrong object", (Long)2L, c.getId());
        verify(beerRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(beerRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        beerService.remove(4L);    
        verify(beerRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(beerRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueBeer() {
        Beer c = new Beer();
        c.setName("Heineken");
        c.setBrewery("X");
        
        List<ObjectError> errors = beerService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(beerRepositoryMock, times(1)).findByName("Heineken");
        verifyNoMoreInteractions(beerRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingBeer() {
        Beer c = new Beer();
        c.setId(1L);
        c.setName("Heineken");
        c.setBrewery("X");
        
        List<ObjectError> errors = beerService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(beerRepositoryMock, times(1)).findByName("Heineken");
        verifyNoMoreInteractions(beerRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingBeerWithDifferentId() {
        Beer c = new Beer();
        c.setId(6L);
        c.setName("Heineken");
        c.setBrewery("X");
        
        List<ObjectError> errors = beerService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(beerRepositoryMock, times(1)).findByName("Heineken");
        verifyNoMoreInteractions(beerRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueBeer() {
        Beer c = new Beer();
        c.setName("Hoegaarden");
        c.setBrewery("Hoegaarden2");
        
        List<ObjectError> errors = beerService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(beerRepositoryMock, times(1)).findByName("Hoegaarden");
        verifyNoMoreInteractions(beerRepositoryMock);
    }  
}
