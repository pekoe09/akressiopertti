package akressiopertti.unittests.services;

import akressiopertti.domain.MeasureType;
import akressiopertti.repository.MeasureTypeRepository;
import akressiopertti.service.MeasureTypeService;
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
public class MeasureTypeServiceTest {
    
    @Autowired
    private MeasureTypeRepository measureTypeRepositoryMock;
    @Autowired
    private MeasureTypeService measureTypeService;
    
    @Before
    public void setUp() {
        Mockito.reset(measureTypeRepositoryMock);
        
        List<MeasureType> measureTypes = new ArrayList<>();
        MeasureType m1 = new MeasureType();
        m1.setName("paino");
        m1.setId(1L);
        measureTypes.add(m1);
        MeasureType m2 = new MeasureType();
        m2.setId(2L);
        m2.setName("tilavuus");
        measureTypes.add(m2);
        MeasureType m3 = new MeasureType();
        m3.setId(3L);
        m3.setName("pituus");
        measureTypes.add(m3);
        
        when(measureTypeRepositoryMock.findAll()).thenReturn(measureTypes);
        when(measureTypeRepositoryMock.findOne(1L)).thenReturn(m1);
        when(measureTypeRepositoryMock.findOne(2L)).thenReturn(m2);
        when(measureTypeRepositoryMock.findOne(3L)).thenReturn(m3);
        when(measureTypeRepositoryMock.findOne(4L)).thenReturn(null);
        when(measureTypeRepositoryMock.findByName("paino")).thenReturn(m1);
        when(measureTypeRepositoryMock.findByName("lukumäärä")).thenReturn(null);
        when(measureTypeRepositoryMock.save(any(MeasureType.class))).thenReturn(m1);
        Mockito.doThrow(DataAccessException.class).when(measureTypeRepositoryMock).delete(4L); 
    }    
            
    @Test
    public void findAllRetrievesAllInredients() {
        String[] measureTypeNames = new String[] {"paino", "tilavuus", "pituus"};
        List<MeasureType> retrievedMeasureTypes = measureTypeService.findAll();
        
        verify(measureTypeRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual measureTypes", 3, retrievedMeasureTypes.size());
        int hits = 0;
        for(String name : measureTypeNames) {
            for(MeasureType m : retrievedMeasureTypes) {
                if(name.equals(m.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some measureType names missing", 3, hits);
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneMeasureType() {        
        MeasureType retrievedMeasureType = measureTypeService.findOne(2L);
        
        verify(measureTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find an measureType by id", retrievedMeasureType);
        assertEquals("Found wrong measureType", "tilavuus", retrievedMeasureType.getName());
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        MeasureType retrievedMeasureType = measureTypeService.findOne(4L);
        
        verify(measureTypeRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong measureType by nonexisting id", retrievedMeasureType);
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        MeasureType i4 = new MeasureType();
        i4.setName("lukumäärä");
        
        MeasureType savedMeasureType = measureTypeService.save(i4);
        
        assertNotNull(savedMeasureType);
        assertEquals((Long)1L, savedMeasureType.getId());
        ArgumentCaptor<MeasureType> measureTypeArgument = ArgumentCaptor.forClass(MeasureType.class);
        verify(measureTypeRepositoryMock, times(1)).save(measureTypeArgument.capture());
        verifyNoMoreInteractions(measureTypeRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        MeasureType i = measureTypeService.remove(2L);
        verify(measureTypeRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", i);
        assertEquals("Remove returns wrong object", (Long)2L, i.getId());
        verify(measureTypeRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        measureTypeService.remove(4L);    
        verify(measureTypeRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueMeasureType() {
        MeasureType i = new MeasureType();
        i.setName("paino");
        
        List<ObjectError> errors = measureTypeService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(measureTypeRepositoryMock, times(1)).findByName("paino");
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingMeasureType() {
        MeasureType i = new MeasureType();
        i.setId(1L);
        i.setName("paino");
        
        List<ObjectError> errors = measureTypeService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(measureTypeRepositoryMock, times(1)).findByName("paino");
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingMeasureTypeWithDifferentId() {
        MeasureType i = new MeasureType();
        i.setId(6L);
        i.setName("paino");
        
        List<ObjectError> errors = measureTypeService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(measureTypeRepositoryMock, times(1)).findByName("paino");
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueMeasureType() {
        MeasureType i = new MeasureType();
        i.setName("lukumäärä");
        
        List<ObjectError> errors = measureTypeService.checkUniqueness(i);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(measureTypeRepositoryMock, times(1)).findByName("lukumäärä");
        verifyNoMoreInteractions(measureTypeRepositoryMock);
    }  
}
