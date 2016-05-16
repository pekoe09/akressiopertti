package akressiopertti.unittests.services;

import akressiopertti.domain.Measure;
import akressiopertti.repository.MeasureRepository;
import akressiopertti.service.MeasureService;
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
public class MeasureServiceTest {
    
    @Autowired
    private MeasureRepository measureRepositoryMock;
    @Autowired
    private MeasureService measureService;
    
    @Before
    public void setUp()  {
        Mockito.reset(measureRepositoryMock);
        
        List<Measure> measures = new ArrayList<>();
        Measure m1 = new Measure();
        m1.setId(1L);
        m1.setName("Litra");
        m1.setPartitive("litraa");
        m1.setAbbreviation("l");
        measures.add(m1);
        Measure m2 = new Measure();
        m2.setId(2L);
        m2.setName("Desi");
        m2.setPartitive("desiä");
        m2.setAbbreviation("dl");
        measures.add(m2);
        Measure m3 = new Measure();
        m3.setId(3L);
        m3.setName("Milli");
        m3.setPartitive("milliä");
        m3.setAbbreviation("ml");
        measures.add(m3);
                       
        when(measureRepositoryMock.findAll()).thenReturn(measures);
        when(measureRepositoryMock.findOne(1L)).thenReturn(m1);
        when(measureRepositoryMock.findOne(2L)).thenReturn(m2);
        when(measureRepositoryMock.findOne(3L)).thenReturn(m3);
        when(measureRepositoryMock.findOne(4L)).thenReturn(null);
        when(measureRepositoryMock.findByName("Litra")).thenReturn(m1);
        when(measureRepositoryMock.findByName("Kilo")).thenReturn(null);
        when(measureRepositoryMock.save(any(Measure.class))).thenReturn(m1);
        Mockito.doThrow(DataAccessException.class).when(measureRepositoryMock).delete(4L);
    }
            
    @Test
    public void findAllRetrievesAllMeasures() {
        String[] measureNames = new String[] {"Litra", "Desi", "Milli"};
        List<Measure> retrievedMeasures = measureService.findAll();
        
        verify(measureRepositoryMock, times(1)).findAll();
        assertEquals("Mismatch between number of retrieved and actual ingredients", 3, retrievedMeasures.size());
        int hits = 0;
        for(String name : measureNames) {
            for(Measure m : retrievedMeasures) {
                if(name.equals(m.getName())) {
                    hits++;
                    break;
                }
            }
        }
        assertEquals("Some measure names missing", 3, hits);
        verifyNoMoreInteractions(measureRepositoryMock);
    }
    
    @Test
    public void findOneRetrievesOneMeasure() {        
        Measure retrievedMeasure = measureService.findOne(2L);
        
        verify(measureRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Didn't find a measure by id", retrievedMeasure);
        assertEquals("Found wrong measure", "Desi", retrievedMeasure.getName());
        verifyNoMoreInteractions(measureRepositoryMock);
    }
    
    @Test
    public void findOneReturnsNullForNonexistingId() {
        Measure retrievedMeasure = measureService.findOne(4L);
        
        verify(measureRepositoryMock, times(1)).findOne(4L);
        assertNull("Found wrong measure by nonexisting id", retrievedMeasure);
        verifyNoMoreInteractions(measureRepositoryMock);
    }
    
    @Test
    public void saveAttemptsToSaveToRepository() {
        Measure m = new Measure();
        m.setName("Kilo");
        m.setPartitive("kiloa");
        m.setAbbreviation("k");
        
        Measure savedMeasure = measureService.save(m);
        
        assertNotNull(savedMeasure);
        assertEquals((Long)1L, savedMeasure.getId());
        ArgumentCaptor<Measure> ingredientArgument = ArgumentCaptor.forClass(Measure.class);
        verify(measureRepositoryMock, times(1)).save(ingredientArgument.capture());
        verifyNoMoreInteractions(measureRepositoryMock);        
    }
    
    @Test
    public void removeAttemptsToRemoveFromRepository() {
        Measure i = measureService.remove(2L);
        verify(measureRepositoryMock, times(1)).findOne(2L);
        assertNotNull("Remove does not return the object", i);
        assertEquals("Remove returns wrong object", (Long)2L, i.getId());
        verify(measureRepositoryMock, times(1)).delete(2L);
        verifyNoMoreInteractions(measureRepositoryMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeThrowsExceptionForNonexistingId() {
        measureService.remove(4L);    
        verify(measureRepositoryMock, times(1)).findOne(2L);
        verifyNoMoreInteractions(measureRepositoryMock);
    }
    
    @Test
    public void uniquenessCheckReturnsErrorForNonUniqueMeasure() {
        Measure m = new Measure();
        m.setName("Litra");
        m.setPartitive("litraa");
        m.setAbbreviation("l");
        
        List<ObjectError> errors = measureService.checkUniqueness(m);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(measureRepositoryMock, times(1)).findByName("Litra");
        verify(measureRepositoryMock, times(1)).findByPartitive("litraa");
        verify(measureRepositoryMock, times(1)).findByAbbreviation("l");
        verifyNoMoreInteractions(measureRepositoryMock);
    }
 
    @Test
    public void uniquenessCheckDoesNotReturnErrorForExistingMeasure() {
        Measure m = new Measure();
        m.setId(1L);
        m.setName("Litra");
        m.setPartitive("litraa");
        m.setAbbreviation("l");
        
        List<ObjectError> errors = measureService.checkUniqueness(m);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());  
        verify(measureRepositoryMock, times(1)).findByName("Litra");
        verify(measureRepositoryMock, times(1)).findByPartitive("litraa");
        verify(measureRepositoryMock, times(1)).findByAbbreviation("l");
        verifyNoMoreInteractions(measureRepositoryMock);
    }   
    
    @Test
    public void uniquenessCheckReturnsErrorForExistingMeasureWithDifferentId() {
        Measure m = new Measure();
        m.setId(6L);
        m.setName("Litra");
        m.setPartitive("litraa");
        m.setAbbreviation("l");
        
        List<ObjectError> errors = measureService.checkUniqueness(m);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 1, errors.size());
        assertEquals("Error message is incorrect", "Nimi on jo varattu", errors.get(0).getDefaultMessage());
        assertEquals("Error field name is incorrect", "name", errors.get(0).getObjectName());
        verify(measureRepositoryMock, times(1)).findByName("Litra");
        verify(measureRepositoryMock, times(1)).findByPartitive("litraa");
        verify(measureRepositoryMock, times(1)).findByAbbreviation("l");
        verifyNoMoreInteractions(measureRepositoryMock);
    }     
    
    @Test
    public void uniquenessCheckReturnsEmptyListForUniqueMeasure() {
        Measure m = new Measure();
        m.setName("Kilo");
        m.setPartitive("kiloa");
        m.setAbbreviation("k");
        
        List<ObjectError> errors = measureService.checkUniqueness(m);
        assertNotNull("No error list is returned from method", errors);
        assertEquals("Method returns wrong number of errors", 0, errors.size());
        verify(measureRepositoryMock, times(1)).findByName("Kilo");
        verify(measureRepositoryMock, times(1)).findByPartitive("kiloa");
        verify(measureRepositoryMock, times(1)).findByAbbreviation("k");
        verifyNoMoreInteractions(measureRepositoryMock);
    }      
    
}
