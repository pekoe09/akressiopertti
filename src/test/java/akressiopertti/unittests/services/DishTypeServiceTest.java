package akressiopertti.unittests.services;

import akressiopertti.domain.DishType;
import akressiopertti.repository.DishTypeRepository;
import akressiopertti.service.DishTypeService;
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
public class DishTypeServiceTest {
    
    @Autowired
    private DishTypeRepository dishTypeRepositoryMock;
    @Autowired
    private DishTypeService dishTypeService;
    
    @Before
    public void setUp() {
        Mockito.reset(dishTypeRepositoryMock);
        
        List<DishType> dishTypes = new ArrayList<>();
        DishType d1 = new DishType();
        d1.setId(1L);
        d1.setName("Keitto");
        dishTypes.add(d1);
        DishType d2 = new DishType();
        d2.setId(2L);
        d2.setName("Kastike");
        dishTypes.add(d2);
        DishType d3 = new DishType();
        d3.setId(3L);
        d3.setName("Vanukas");
        dishTypes.add(d3);
        
        when(dishTypeRepositoryMock.findAll()).thenReturn(dishTypes);
        when(dishTypeRepositoryMock.findOne(1L)).thenReturn(d1);
        when(dishTypeRepositoryMock.findOne(2L)).thenReturn(d2);
        when(dishTypeRepositoryMock.findOne(3L)).thenReturn(d3);
        when(dishTypeRepositoryMock.findOne(4L)).thenReturn(null);
        when(dishTypeRepositoryMock.findByName("Keitto")).thenReturn(d1);
        when(dishTypeRepositoryMock.findByName("Pata")).thenReturn(null);
        when(dishTypeRepositoryMock.save(any(DishType.class))).thenReturn(d1);
        Mockito.doThrow(DataAccessException.class).when(dishTypeRepositoryMock).delete(4L);
    }    
        
    @Test
    public void findAllRetrievesAllDishTypes() {
        String[] dishTypeNames = new String[] {"Keitto", "Kastike", "Vanukas"};
        List<DishType> retrievedDishTypes = dishTypeService.findAll();
        
        verify(dishTypeRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual dish types", 3, retrievedDishTypes.size());
        int hits = 0;
        for(String name : dishTypeNames) {
            for(DishType d : retrievedDishTypes) {
                if(name.equals(d.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some dish type names missing", 3, hits);
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneDishType() {        
        DishType retrievedDishType = dishTypeService.findOne(2L);
        
        verify(dishTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a dish type by id", retrievedDishType);
        assertEquals("Found wrong dish  type", "Kastike", retrievedDishType.getName());
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        DishType retrievedDishType = dishTypeService.findOne(4L);
        
        verify(dishTypeRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong dish type by nonexisting id", retrievedDishType);
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        DishType d4 = new DishType();
        d4.setName("Paisti");
        
        DishType savedDishType = dishTypeRepositoryMock.save(d4);
        
        assertNotNull(savedDishType);
        assertEquals((Long)1L, savedDishType.getId());
        ArgumentCaptor<DishType> dishTypeArgument = ArgumentCaptor.forClass(DishType.class);
        verify(dishTypeRepositoryMock, times(1)).save(dishTypeArgument.capture());
        verifyNoMoreInteractions(dishTypeRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        DishType d = dishTypeService.remove(2L);
        verify(dishTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", d);
        assertEquals("Remove returns wrong object", (Long)2L, d.getId());
        verify(dishTypeRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        dishTypeService.remove(4L);    
        verify(dishTypeRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueDishType() {
        DishType d = new DishType();
        d.setName("Keitto");
        
        List<ObjectError> errors = dishTypeService.checkUniqueness(d);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(dishTypeRepositoryMock, times(1)).findByName("Keitto");
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingDishType() {
        DishType d = new DishType();
        d.setId(1L);
        d.setName("Keitto");
        
        List<ObjectError> errors = dishTypeService.checkUniqueness(d);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(dishTypeRepositoryMock, times(1)).findByName("Keitto");
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingDishTypeWithDifferentId() {
        DishType d = new DishType();
        d.setId(6L);
        d.setName("Keitto");
        
        List<ObjectError> errors = dishTypeService.checkUniqueness(d);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(dishTypeRepositoryMock, times(1)).findByName("Keitto");
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueDishType() {
        DishType d = new DishType();
        d.setName("Soppa");
        
        List<ObjectError> errors = dishTypeService.checkUniqueness(d);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(dishTypeRepositoryMock, times(1)).findByName("Soppa");
        verifyNoMoreInteractions(dishTypeRepositoryMock);
    }  
}
