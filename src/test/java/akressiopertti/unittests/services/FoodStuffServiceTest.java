package akressiopertti.unittests.services;

import akressiopertti.domain.FoodStuff;
import akressiopertti.repository.FoodStuffRepository;
import akressiopertti.service.FoodStuffService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.ObjectError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestContext.class})
public class FoodStuffServiceTest {
    
    @Autowired
    private FoodStuffRepository foodStuffRepositoryMock;
    @Autowired
    private FoodStuffService foodStuffService;
    
    @Before
    public void setUp() {
        Mockito.reset(foodStuffRepositoryMock);
        
        List<FoodStuff> foodStuffs = new ArrayList<>();
        FoodStuff f1 = new FoodStuff();
        f1.setId(1L);
        f1.setName("Liha");
        foodStuffs.add(f1);
        FoodStuff f2 = new FoodStuff();
        f2.setId(2L);
        f2.setName("Kala");
        foodStuffs.add(f2);
        FoodStuff f3 = new FoodStuff();
        f3.setId(3L);
        f3.setName("Kana");
        foodStuffs.add(f3);
        
        when(foodStuffRepositoryMock.findAll()).thenReturn(foodStuffs);
        when(foodStuffRepositoryMock.findOne(1L)).thenReturn(f1);
        when(foodStuffRepositoryMock.findOne(2L)).thenReturn(f2);
        when(foodStuffRepositoryMock.findOne(3L)).thenReturn(f3);
        when(foodStuffRepositoryMock.findOne(4L)).thenReturn(null);
        when(foodStuffRepositoryMock.findByName("Liha")).thenReturn(f1);
        when(foodStuffRepositoryMock.findByName("Yrtti")).thenReturn(null);
        when(foodStuffRepositoryMock.save(any(FoodStuff.class))).thenReturn(f1);
        Mockito.doThrow(DataAccessException.class).when(foodStuffRepositoryMock).delete(4L);        
    }
       
    @Test
    public void findAllRetrievesAllFoodStuffs() {
        String[] foodStuffNames = new String[] {"Liha", "Kala", "Kana"};
        List<FoodStuff> retrievedFoodStuffs = foodStuffService.findAll();
        
        verify(foodStuffRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual foodstuffs", 3, retrievedFoodStuffs.size());
        int hits = 0;
        for(String name : foodStuffNames) {
            for(FoodStuff f : retrievedFoodStuffs) {
                if(name.equals(f.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some foodstuff names missing", 3, hits);
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneFoodStuff() {        
        FoodStuff retrievedFoodStuff = foodStuffService.findOne(2L);
        
        verify(foodStuffRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a foodstuff by id", retrievedFoodStuff);
        assertEquals("Found wrong foodstuff", "Kala", retrievedFoodStuff.getName());
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        FoodStuff retrievedFoodStuff = foodStuffService.findOne(4L);
        
        verify(foodStuffRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong foodstuff by nonexisting id", retrievedFoodStuff);
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        FoodStuff f4 = new FoodStuff();
        f4.setName("Yrtti");
        
        FoodStuff savedFoodStuff = foodStuffRepositoryMock.save(f4);
        
        assertNotNull(savedFoodStuff);
        assertEquals((Long)1L, savedFoodStuff.getId());
        ArgumentCaptor<FoodStuff> foodStuffArgument = ArgumentCaptor.forClass(FoodStuff.class);
        verify(foodStuffRepositoryMock, times(1)).save(foodStuffArgument.capture());
        verifyNoMoreInteractions(foodStuffRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        FoodStuff d = foodStuffService.remove(2L);
        verify(foodStuffRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", d);
        assertEquals("Remove returns wrong object", (Long)2L, d.getId());
        verify(foodStuffRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        foodStuffService.remove(4L);    
        verify(foodStuffRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueFoodStuff() {
        FoodStuff f = new FoodStuff();
        f.setName("Liha");
        
        List<ObjectError> errors = foodStuffService.checkUniqueness(f);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(foodStuffRepositoryMock, times(1)).findByName("Liha");
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingFoodStuff() {
        FoodStuff f = new FoodStuff();
        f.setId(1L);
        f.setName("Liha");
        
        List<ObjectError> errors = foodStuffService.checkUniqueness(f);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(foodStuffRepositoryMock, times(1)).findByName("Liha");
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingFoodStuffWithDifferentId() {
        FoodStuff f = new FoodStuff();
        f.setId(6L);
        f.setName("Liha");
        
        List<ObjectError> errors = foodStuffService.checkUniqueness(f);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(foodStuffRepositoryMock, times(1)).findByName("Liha");
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueDishType() {
        FoodStuff f = new FoodStuff();
        f.setName("Yrtti");
        
        List<ObjectError> errors = foodStuffService.checkUniqueness(f);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(foodStuffRepositoryMock, times(1)).findByName("Yrtti");
        verifyNoMoreInteractions(foodStuffRepositoryMock);
    }      
}
