package akressiopertti.unittests.controllers;

import akressiopertti.domain.Course;
import akressiopertti.domain.FoodStuff;
import akressiopertti.service.FoodStuffService;
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
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
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
        WithSecurityContextTestExcecutionListener.class})
public class FoodStuffControllerTest {

    private final String LIST_URI = "/ruoka-aineet";
    private final String ADD_URI = "/ruoka-aineet/lisaa";
    private final String EDIT_URI = "/ruoka-aineet/1/muokkaa";
    private final String DELETE_URI = "/ruoka-aineet/1/poista";
    private FoodStuff f1;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private FoodStuffService foodStuffServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(foodStuffServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        f1 = new FoodStuff();
        f1.setId(1L);
        f1.setName("Liha");
        f1.setIsFoodCategory(true);
        when(foodStuffServiceMock.findAll()).thenReturn(Arrays.asList(f1));
        when(foodStuffServiceMock.findOne(1L)).thenReturn(f1);
        when(foodStuffServiceMock.checkUniqueness(any(FoodStuff.class))).thenReturn(new ArrayList<ObjectError>());
        when(foodStuffServiceMock.save(any(FoodStuff.class))).thenReturn(f1);
        when(foodStuffServiceMock.remove(1L)).thenReturn(f1);
        Mockito.doThrow(NullPointerException.class).when(foodStuffServiceMock).remove(4L);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuffs"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsFoodStuffs() throws Exception {       
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("foodStuffs"))
                .andExpect(model().attribute("foodStuffs", hasSize(1)))
                .andReturn();
        
        verify(foodStuffServiceMock,  times(1)).findAll();
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_add"))
                .andReturn();
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsFoodStuff() throws Exception {        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Liha")
                .param("isfoodcategory", "1")                
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruoka-aineet"))
                .andExpect(flash().attribute("success", 
                        "Ruoka-aine " + f1.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<FoodStuff> foodstuffArgument = ArgumentCaptor.forClass(FoodStuff.class);
        verify(foodStuffServiceMock,times(1)).checkUniqueness(foodstuffArgument.capture());
        assertEquals("Liha", foodstuffArgument.getValue().getName());
        verify(foodStuffServiceMock, times(1)).save(foodstuffArgument.capture());
        assertEquals("Liha", foodstuffArgument.getValue().getName());
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddFoodStuffView() throws Exception {        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("isFoodCategory", "true")                
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_add"))
                .andExpect(model().attributeExists("foodStuff"))
                .andExpect(model().attributeHasFieldErrors("foodStuff", "name"))
                .andReturn();
        
        verify(foodStuffServiceMock,times(1)).checkUniqueness(any(FoodStuff.class));
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddFoodStuffView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(foodStuffServiceMock.checkUniqueness(any(FoodStuff.class))).thenReturn(errors);
        
         MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Liha")
                .param("isFoodCategory", "true")                
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_add"))
                .andExpect(model().attributeExists("foodStuff"))
                .andExpect(model().attributeHasFieldErrors("foodStuff", "name"))
                .andReturn();   
         
        verify(foodStuffServiceMock,times(1)).checkUniqueness(any(FoodStuff.class));
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_edit"))
                .andExpect(model().attributeExists("foodStuff"))
                .andReturn();
                
        verify(foodStuffServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsFoodStuff() throws Exception {   
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Liha")
                .param("isFoodCategory", "true")
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruoka-aineet"))
                .andExpect(flash().attribute("success",
                        "Ruoka-aineen " + f1.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<FoodStuff> foodStuffArgument = ArgumentCaptor.forClass(FoodStuff.class);
        verify(foodStuffServiceMock, times(1)).checkUniqueness(foodStuffArgument.capture());
        assertEquals("Liha", foodStuffArgument.getValue().getName());
        verify(foodStuffServiceMock, times(1)).save(foodStuffArgument.capture());
        assertEquals("Liha", foodStuffArgument.getValue().getName());
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditFoodStuffView() throws Exception {           
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .param("isFoodCategory", "true")
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_edit"))
                .andExpect(model().attributeExists("foodStuff"))
                .andExpect(model().attributeHasFieldErrors("foodStuff", "name"))
                .andReturn();  
        
        verify(foodStuffServiceMock,times(1)).checkUniqueness(any(FoodStuff.class));
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditFoodStuffView() throws Exception {
        List<ObjectError> errors = new ArrayList<ObjectError>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(foodStuffServiceMock.checkUniqueness(any(FoodStuff.class))).thenReturn(errors);
        
         MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Liha")
                .param("isFooodCategory", "true")                
                .sessionAttr("foodStuff", f1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("foodstuff_edit"))
                .andExpect(model().attributeExists("foodStuff"))
                .andExpect(model().attributeHasFieldErrors("foodStuff", "name"))
                .andReturn();   
         
        verify(foodStuffServiceMock,times(1)).checkUniqueness(any(FoodStuff.class));
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesCourse() throws Exception {
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruoka-aineet"))
                .andReturn();
        
        verify(foodStuffServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(foodStuffServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        MvcResult res = mockMvc.perform(post("/ruoka-aineet/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruoka-aineet"))
                .andExpect(flash().attribute("error", "Poistettavaa ruoka-ainetta ei löydy!"))
                .andReturn();
        
        verify(foodStuffServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(foodStuffServiceMock);
    }
}
