package akressiopertti.unittests.controllers;

import akressiopertti.domain.Course;
import akressiopertti.domain.DishType;
import akressiopertti.service.DishTypeService;
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
public class DishTypeControllerTest {
    
    private final String LIST_URI = "/ruokatyypit";
    private final String ADD_URI = "/ruokatyypit/lisaa";
    private final String EDIT_URI = "/ruokatyypit/1/muokkaa";
    private final String DELETE_URI = "/ruokatyypit/1/poista";
    private DishType d1;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private DishTypeService dishTypeServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(dishTypeServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        d1 = new DishType();
        d1.setId(1L);
        d1.setName("Keitto");
        when(dishTypeServiceMock.findAll()).thenReturn(Arrays.asList(d1));
        when(dishTypeServiceMock.findOne(1L)).thenReturn(d1);
        when(dishTypeServiceMock.checkUniqueness(any(DishType.class))).thenReturn(new ArrayList<ObjectError>());
        when(dishTypeServiceMock.save(any(DishType.class))).thenReturn(d1);
        when(dishTypeServiceMock.remove(1L)).thenReturn(d1);
        Mockito.doThrow(NullPointerException.class).when(dishTypeServiceMock).remove(4L);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("dishtypes"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsDishTypes() throws Exception {       
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("dishTypes"))
                .andExpect(model().attribute("dishTypes", hasSize(1)))
                .andReturn();
        
        verify(dishTypeServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_add"))
                .andReturn();
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsDishType() throws Exception {        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Keitto")             
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokatyypit"))
                .andExpect(flash().attribute("success", 
                        "Ruokatyyppi " + d1.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<DishType> dishtypeArgument = ArgumentCaptor.forClass(DishType.class);
        verify(dishTypeServiceMock,times(1)).checkUniqueness(dishtypeArgument.capture());
        assertEquals("Keitto", dishtypeArgument.getValue().getName());
        verify(dishTypeServiceMock, times(1)).save(dishtypeArgument.capture());
        assertEquals("Keitto", dishtypeArgument.getValue().getName());
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddDishTypeView() throws Exception {        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)              
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_add"))
                .andExpect(model().attributeExists("dishType"))
                .andExpect(model().attributeHasFieldErrors("dishType", "name"))
                .andReturn();
        
        verify(dishTypeServiceMock,times(1)).checkUniqueness(any(DishType.class));
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddDishTypeView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(dishTypeServiceMock.checkUniqueness(any(DishType.class))).thenReturn(errors);
        
         MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Keitto")              
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_add"))
                .andExpect(model().attributeExists("dishType"))
                .andExpect(model().attributeHasFieldErrors("dishType", "name"))
                .andReturn();   
         
        verify(dishTypeServiceMock,times(1)).checkUniqueness(any(DishType.class));
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_edit"))
                .andExpect(model().attributeExists("dishType"))
                .andReturn();
                
        verify(dishTypeServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsDishType() throws Exception {   
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Keitto")
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokatyypit"))
                .andExpect(flash().attribute("success",
                        "Ruokatyypin " + d1.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<DishType> dishtypeArgument = ArgumentCaptor.forClass(DishType.class);
        verify(dishTypeServiceMock, times(1)).checkUniqueness(dishtypeArgument.capture());
        assertEquals("Keitto", dishtypeArgument.getValue().getName());
        verify(dishTypeServiceMock, times(1)).save(dishtypeArgument.capture());
        assertEquals("Keitto", dishtypeArgument.getValue().getName());
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditDishTypeView() throws Exception {           
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_edit"))
                .andExpect(model().attributeExists("dishType"))
                .andExpect(model().attributeHasFieldErrors("dishType", "name"))
                .andReturn();  
        
        verify(dishTypeServiceMock,times(1)).checkUniqueness(any(DishType.class));
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditDishTypeView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(dishTypeServiceMock.checkUniqueness(any(DishType.class))).thenReturn(errors);
        
         MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Keitto")             
                .sessionAttr("dishType", d1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("dishtype_edit"))
                .andExpect(model().attributeExists("dishType"))
                .andExpect(model().attributeHasFieldErrors("dishType", "name"))
                .andReturn();   
         
        verify(dishTypeServiceMock,times(1)).checkUniqueness(any(DishType.class));
        verifyNoMoreInteractions(dishTypeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesDishType() throws Exception {
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokatyypit"))
                .andReturn();
        
        verify(dishTypeServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(dishTypeServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        MvcResult res = mockMvc.perform(post("/ruokatyypit/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokatyypit"))
                .andExpect(flash().attribute("error", "Poistettavaa ruokatyyppiä ei löydy!"))
                .andReturn();
        
        verify(dishTypeServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(dishTypeServiceMock);
    }    
}
