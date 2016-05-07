package akressiopertti.unittests.controllers;

import akressiopertti.domain.Ingredient;
import akressiopertti.domain.Measure;
import akressiopertti.domain.MeasureType;
import akressiopertti.service.MeasureService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.hasSize;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class MeasureControllerTest {
    
    private final String LIST_URI = "/mitat";
    private final String ADD_URI = "/mitat/lisaa";
    private final String EDIT_URI = "/mitat/1/muokkaa";
    private final String DELETE_URI = "/mitat/1/poista";
    private Measure m1;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private MeasureService measureServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(measureServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        m1 = new Measure();
        m1.setId(1L);
        m1.setName("Litra");
        m1.setPartitive("Litraa");
        m1.setAbbreviation("l");
        m1.setMeasureType(new MeasureType());
        
        when(measureServiceMock.findAll()).thenReturn(Arrays.asList(m1));
        when(measureServiceMock.findOne(1L)).thenReturn(m1);
        when(measureServiceMock.checkUniqueness(any(Measure.class))).thenReturn(new ArrayList<ObjectError>());
        when(measureServiceMock.save(any(Measure.class))).thenReturn(m1);
        when(measureServiceMock.remove(1L)).thenReturn(m1);
        Mockito.doThrow(NullPointerException.class).when(measureServiceMock).remove(4L);   
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measures"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsMeasures() throws Exception {       
        mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("measures"))
                .andExpect(model().attribute("measures", hasSize(1)))
                .andReturn();
        
        verify(measureServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measure_add"))
                .andReturn();
        verify(measureServiceMock, times(1)).getOptions();
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsMeasure() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Litra")
                .param("partitive", "Litraa")    
                .param("abbreviation", "l")
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mitat"))
                .andExpect(flash().attribute("success", 
                        "Mitta " + m1.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<Measure> measureArgument = ArgumentCaptor.forClass(Measure.class);
        verify(measureServiceMock,times(1)).checkUniqueness(measureArgument.capture());
        assertEquals("Litra", measureArgument.getValue().getName());
        verify(measureServiceMock, times(1)).save(measureArgument.capture());
        assertEquals("Litra", measureArgument.getValue().getName());
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddMeasureView() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("partitive", "litraa")    
                .param("abbreviation", "l")
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measure_add"))
                .andExpect(model().attributeExists("measure"))
                .andExpect(model().attributeHasFieldErrors("measure", "name"))
                .andReturn();
        
        verify(measureServiceMock, times(1)).getOptions();
        verify(measureServiceMock,times(1)).checkUniqueness(any(Measure.class));
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddMeasureView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(measureServiceMock.checkUniqueness(any(Measure.class))).thenReturn(errors);
        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Litra")
                .param("partitive", "litraa") 
                .param("abbreviation", "l")
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measure_add"))
                .andExpect(model().attributeExists("measure"))
                .andExpect(model().attributeHasFieldErrors("measure", "name"))
                .andReturn();   
        
        verify(measureServiceMock, times(1)).getOptions();
        verify(measureServiceMock,times(1)).checkUniqueness(any(Measure.class));
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measure_edit"))
                .andExpect(model().attributeExists("measure"))
                .andReturn();
                
        verify(measureServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsMeasure() throws Exception {   
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Litra")
                .param("partitive", "litraa")
                .param("abbreviation", "l")
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mitat"))
                .andExpect(flash().attribute("success",
                        "Mitan " + m1.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<Measure> measureArgument = ArgumentCaptor.forClass(Measure.class);
        verify(measureServiceMock, times(1)).checkUniqueness(measureArgument.capture());
        assertEquals("Litra", measureArgument.getValue().getName());
        verify(measureServiceMock, times(1)).save(measureArgument.capture());
        assertEquals("Litra", measureArgument.getValue().getName());
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditMeasureView() throws Exception {           
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .param("partitive", "litraa")
                .param("abbreviation", "l")
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measure_edit"))
                .andExpect(model().attributeExists("measure"))
                .andExpect(model().attributeHasFieldErrors("measure", "name"))
                .andReturn();  
        
        verify(measureServiceMock, times(1)).getOptions();
        verify(measureServiceMock,times(1)).checkUniqueness(any(Measure.class));
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditMeasureView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(measureServiceMock.checkUniqueness(any(Measure.class))).thenReturn(errors);
        
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Litra")
                .param("partitive", "litraa")                
                .sessionAttr("measure", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measure_edit"))
                .andExpect(model().attributeExists("measure"))
                .andExpect(model().attributeHasFieldErrors("measure", "name"))
                .andReturn();   
        
        verify(measureServiceMock, times(1)).getOptions();
        verify(measureServiceMock,times(1)).checkUniqueness(any(Measure.class));
        verifyNoMoreInteractions(measureServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesMeasure() throws Exception {
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mitat"))
                .andReturn();
        
        verify(measureServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(measureServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        mockMvc.perform(post("/mitat/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mitat"))
                .andExpect(flash().attribute("error", "Poistettavaa mittaa ei löydy!"))
                .andReturn();
        
        verify(measureServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(measureServiceMock);
    }    
}
