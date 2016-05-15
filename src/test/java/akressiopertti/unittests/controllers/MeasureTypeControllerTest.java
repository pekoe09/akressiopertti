package akressiopertti.unittests.controllers;

import akressiopertti.domain.MeasureType;
import akressiopertti.service.MeasureTypeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
public class MeasureTypeControllerTest {
    
    private final String LIST_URI = "/mittatyypit";
    private final String ADD_URI = "/mittatyypit/lisaa";
    private final String EDIT_URI = "/mittatyypit/1/muokkaa";
    private final String DELETE_URI = "/mittatyypit/1/poista";
    private MeasureType m1;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private MeasureTypeService measureTypeServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(measureTypeServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        m1 = new MeasureType();
        m1.setId(1L);
        m1.setName("Paino");
        when(measureTypeServiceMock.findAll()).thenReturn(Arrays.asList(m1));
        when(measureTypeServiceMock.findOne(1L)).thenReturn(m1);
        when(measureTypeServiceMock.checkUniqueness(any(MeasureType.class))).thenReturn(new ArrayList<>());
        when(measureTypeServiceMock.save(any(MeasureType.class))).thenReturn(m1);
        when(measureTypeServiceMock.remove(1L)).thenReturn(m1);
        Mockito.doThrow(IllegalArgumentException.class).when(measureTypeServiceMock).remove(4L); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measuretypes"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsMeasureTypes() throws Exception {       
        mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("measureTypes"))
                .andExpect(model().attribute("measureTypes", hasSize(1)))
                .andReturn();
        
        verify(measureTypeServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_add"))
                .andReturn();
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsMeasureType() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Paino")             
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mittatyypit"))
                .andExpect(flash().attribute("success", 
                        "Mittatyyppi " + m1.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<MeasureType> measureTypeArgument = ArgumentCaptor.forClass(MeasureType.class);
        verify(measureTypeServiceMock,times(1)).checkUniqueness(measureTypeArgument.capture());
        assertEquals("Paino", measureTypeArgument.getValue().getName());
        verify(measureTypeServiceMock, times(1)).save(measureTypeArgument.capture());
        assertEquals("Paino", measureTypeArgument.getValue().getName());
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddMeasureTypeView() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)              
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_add"))
                .andExpect(model().attributeExists("measureType"))
                .andExpect(model().attributeHasFieldErrors("measureType", "name"))
                .andReturn();
        
        verify(measureTypeServiceMock,times(1)).checkUniqueness(any(MeasureType.class));
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddMeasureTypeView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(measureTypeServiceMock.checkUniqueness(any(MeasureType.class))).thenReturn(errors);
        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Paino")              
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_add"))
                .andExpect(model().attributeExists("measureType"))
                .andExpect(model().attributeHasFieldErrors("measureType", "name"))
                .andReturn();   
         
        verify(measureTypeServiceMock,times(1)).checkUniqueness(any(MeasureType.class));
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_edit"))
                .andExpect(model().attributeExists("measureType"))
                .andReturn();
                
        verify(measureTypeServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsDishType() throws Exception {   
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Paino")
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mittatyypit"))
                .andExpect(flash().attribute("success",
                        "Mittatyypin " + m1.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<MeasureType> measureTypeArgument = ArgumentCaptor.forClass(MeasureType.class);
        verify(measureTypeServiceMock, times(1)).checkUniqueness(measureTypeArgument.capture());
        assertEquals("Paino", measureTypeArgument.getValue().getName());
        verify(measureTypeServiceMock, times(1)).save(measureTypeArgument.capture());
        assertEquals("Paino", measureTypeArgument.getValue().getName());
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditMeasureTypeView() throws Exception {           
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_edit"))
                .andExpect(model().attributeExists("measureType"))
                .andExpect(model().attributeHasFieldErrors("measureType", "name"))
                .andReturn();  
        
        verify(measureTypeServiceMock,times(1)).checkUniqueness(any(MeasureType.class));
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditMeasureTypeView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(measureTypeServiceMock.checkUniqueness(any(MeasureType.class))).thenReturn(errors);
        
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Paino")             
                .sessionAttr("measureType", m1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("measuretype_edit"))
                .andExpect(model().attributeExists("measureType"))
                .andExpect(model().attributeHasFieldErrors("measureType", "name"))
                .andReturn();   
         
        verify(measureTypeServiceMock,times(1)).checkUniqueness(any(MeasureType.class));
        verifyNoMoreInteractions(measureTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesDishType() throws Exception {
        mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mittatyypit"))
                .andReturn();
        
        verify(measureTypeServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(measureTypeServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        mockMvc.perform(post("/mittatyypit/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/mittatyypit"))
                .andExpect(flash().attribute("error", "Poistettavaa mittatyyppiä ei löydy!"))
                .andReturn();
        
        verify(measureTypeServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(measureTypeServiceMock);
    }      
}
