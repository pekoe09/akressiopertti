package akressiopertti.unittests.services;

import akressiopertti.domain.WineType;
import akressiopertti.repository.WineTypeRepository;
import akressiopertti.service.WineTypeService;
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
public class WineTypeServiceTest {
    
    @Autowired
    private WineTypeRepository wineTypeRepositoryMock;
    @Autowired
    private WineTypeService wineTypeService;
    
    @Before
    public void setUp() {
        Mockito.reset(wineTypeRepositoryMock);
        
        List<WineType> wineTypes = new ArrayList<>();
        WineType c1 = new WineType();
        c1.setId(1L);
        c1.setName("Valkoviini");
        wineTypes.add(c1);
        WineType c2 = new WineType();
        c2.setId(2L);
        c2.setName("Punaviini");
        wineTypes.add(c2);
        WineType c3 = new WineType();
        c3.setId(3L);
        c3.setName("Shampanja");
        wineTypes.add(c3);
        
        when(wineTypeRepositoryMock.findAll()).thenReturn(wineTypes);
        when(wineTypeRepositoryMock.findOne(1L)).thenReturn(c1);
        when(wineTypeRepositoryMock.findOne(2L)).thenReturn(c2);
        when(wineTypeRepositoryMock.findOne(3L)).thenReturn(c3);
        when(wineTypeRepositoryMock.findOne(4L)).thenReturn(null);
        when(wineTypeRepositoryMock.findByName("Punaviini")).thenReturn(c2);
        when(wineTypeRepositoryMock.findByName("Sherry")).thenReturn(null);
        when(wineTypeRepositoryMock.save(c2)).thenReturn(null);
        Mockito.doThrow(DataAccessException.class).when(wineTypeRepositoryMock).delete(4L);
    }
    
    @Test
    public void findAllRetrievesAllWineTypes() {
        String[] wineTypeNames = new String[] {"Shampanja", "Valkoviini", "Punaviini"};
        List<WineType> retrievedWineTypes = wineTypeService.findAll();
        
        verify(wineTypeRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual wineTypes", 3, retrievedWineTypes.size());
        int hits = 0;
        for(String name : wineTypeNames) {
            for(WineType c : retrievedWineTypes) {
                if(name.equals(c.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some wineType names missing", 3, hits);
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneWineType() {        
        WineType retrievedWineType = wineTypeService.findOne(2L);
        
        verify(wineTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a wineType by id", retrievedWineType);
        assertEquals("Found wrong wineType", "Punaviini", retrievedWineType.getName());
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        WineType retrievedWineType = wineTypeService.findOne(4L);
        
        verify(wineTypeRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong wineType by nonexisting id", retrievedWineType);
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        WineType c1 = new WineType();
        c1.setName("Sherry");
        
        wineTypeService.save(c1);
        
        ArgumentCaptor<WineType> wineTypeArgument = ArgumentCaptor.forClass(WineType.class);
        verify(wineTypeRepositoryMock, times(1)).save(wineTypeArgument.capture());
        verifyNoMoreInteractions(wineTypeRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        WineType c = wineTypeService.remove(2L);
        verify(wineTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", c);
        assertEquals("Remove returns wrong object", (Long)2L, c.getId());
        verify(wineTypeRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        wineTypeService.remove(4L);    
        verify(wineTypeRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueWineType() {
        WineType c = new WineType();
        c.setName("Punaviini");
        
        List<ObjectError> errors = wineTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(wineTypeRepositoryMock, times(1)).findByName("Punaviini");
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingWineType() {
        WineType c = new WineType();
        c.setId(2L);
        c.setName("Punaviini");
        
        List<ObjectError> errors = wineTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(wineTypeRepositoryMock, times(1)).findByName("Punaviini");
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingWineTypeWithDifferentId() {
        WineType c = new WineType();
        c.setId(6L);
        c.setName("Punaviini");
        
        List<ObjectError> errors = wineTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(wineTypeRepositoryMock, times(1)).findByName("Punaviini");
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueWineType() {
        WineType c = new WineType();
        c.setName("Sherry");

        List<ObjectError> errors = wineTypeService.checkUniqueness(c);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(wineTypeRepositoryMock, times(1)).findByName("Sherry");
        verifyNoMoreInteractions(wineTypeRepositoryMock);
    }  
}
