package akressiopertti.unittests.services;

import akressiopertti.domain.Ingredient;
import akressiopertti.repository.IngredientRepository;
import akressiopertti.service.IngredientService;
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
public class IngredientServiceTest {
    
    @Autowired
    private IngredientRepository ingredientRepositoryMock;
    @Autowired
    private IngredientService ingredientService;
    
    @Before
    public void setUp() {
        Mockito.reset(ingredientRepositoryMock);
        
        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient i1 = new Ingredient();
        i1.setId(1L);
        i1.setName("Silakka");
        ingredients.add(i1);
        Ingredient i2 = new Ingredient();
        i2.setId(2L);
        i2.setName("Kuha");
        ingredients.add(i2);
        Ingredient i3 = new Ingredient();
        i3.setId(3L);
        i3.setName("Lohi");
        ingredients.add(i3);
        
        when(ingredientRepositoryMock.findAll()).thenReturn(ingredients);
        when(ingredientRepositoryMock.findOne(1L)).thenReturn(i1);
        when(ingredientRepositoryMock.findOne(2L)).thenReturn(i2);
        when(ingredientRepositoryMock.findOne(3L)).thenReturn(i3);
        when(ingredientRepositoryMock.findOne(4L)).thenReturn(null);
        when(ingredientRepositoryMock.findByName("Silakka")).thenReturn(i1);
        when(ingredientRepositoryMock.findByName("Kampela")).thenReturn(null);
        when(ingredientRepositoryMock.save(any(Ingredient.class))).thenReturn(i1);
        Mockito.doThrow(DataAccessException.class).when(ingredientRepositoryMock).delete(4L);
    }
            
    @Test
    public void findAllRetrievesAllInredients() {
        String[] ingredientNames = new String[] {"Silakka", "Kuha", "Lohi"};
        List<Ingredient> retrievedIngredients = ingredientService.findAll();
        
        verify(ingredientRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual ingredients", 3, retrievedIngredients.size());
        int hits = 0;
        for(String name : ingredientNames) {
            for(Ingredient i : retrievedIngredients) {
                if(name.equals(i.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some ingredient names missing", 3, hits);
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneIngredient() {        
        Ingredient retrievedIngredient = ingredientService.findOne(2L);
        
        verify(ingredientRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a dish type by id", retrievedIngredient);
        assertEquals("Found wrong dish  type", "Kuha", retrievedIngredient.getName());
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Ingredient retrievedIngredient = ingredientService.findOne(4L);
        
        verify(ingredientRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong ingredient by nonexisting id", retrievedIngredient);
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Ingredient i4 = new Ingredient();
        i4.setName("Kampela");
        
        Ingredient savedIngredient = ingredientRepositoryMock.save(i4);
        
        assertNotNull(savedIngredient);
        assertEquals((Long)1L, savedIngredient.getId());
        ArgumentCaptor<Ingredient> ingredientArgument = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientRepositoryMock, times(1)).save(ingredientArgument.capture());
        verifyNoMoreInteractions(ingredientRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        Ingredient i = ingredientService.remove(2L);
        verify(ingredientRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", i);
        assertEquals("Remove returns wrong object", (Long)2L, i.getId());
        verify(ingredientRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        ingredientService.remove(4L);    
        verify(ingredientRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueIngredient() {
        Ingredient i = new Ingredient();
        i.setName("Silakka");
        i.setPartitive("silakkaa");
        
        List<ObjectError> errors = ingredientService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(ingredientRepositoryMock, times(1)).findByName("Silakka");
        verify(ingredientRepositoryMock, times(1)).findByPartitive("silakkaa");
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingIngredient() {
        Ingredient i = new Ingredient();
        i.setId(1L);
        i.setName("Silakka");
        i.setPartitive("silakkaa");
        
        List<ObjectError> errors = ingredientService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(ingredientRepositoryMock, times(1)).findByName("Silakka");
        verify(ingredientRepositoryMock, times(1)).findByPartitive("silakkaa");
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingIngredientWithDifferentId() {
        Ingredient i = new Ingredient();
        i.setId(6L);
        i.setName("Silakka");
        i.setPartitive("silakkaa");
        
        List<ObjectError> errors = ingredientService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(ingredientRepositoryMock, times(1)).findByName("Silakka");
        verify(ingredientRepositoryMock, times(1)).findByPartitive("silakkaa");
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueIngredient() {
        Ingredient i = new Ingredient();
        i.setName("Kampela");
        i.setPartitive("kampelaa");
        
        List<ObjectError> errors = ingredientService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(ingredientRepositoryMock, times(1)).findByName("Kampela");
        verify(ingredientRepositoryMock, times(1)).findByPartitive("kampelaa");
        verifyNoMoreInteractions(ingredientRepositoryMock);
    }  
}
