package akressiopertti.unittests.controllers;

import akressiopertti.domain.Ingredient;
import akressiopertti.service.IngredientService;
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
public class IngredientControllerTest {
    
    private final String LIST_URI = "/ainekset";
    private final String ADD_URI = "/ainekset/lisaa";
    private final String EDIT_URI = "/ainekset/1/muokkaa";
    private final String DELETE_URI = "/ainekset/1/poista";
    private Ingredient i1;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private IngredientService ingredientServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(ingredientServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        i1 = new Ingredient();
        i1.setId(1L);
        i1.setName("Tilli");
        i1.setPartitive("tilliä");
        
        JSONArray ingredientArray = new JSONArray();
        JSONObject ingredientObject = new JSONObject();
        ingredientObject.put("name", i1.getName());
        ingredientObject.put("id", i1.getId());
        ingredientArray.add(ingredientObject);        
        
        when(ingredientServiceMock.findAll()).thenReturn(Arrays.asList(i1));
        when(ingredientServiceMock.findOne(1L)).thenReturn(i1);
        when(ingredientServiceMock.getIngredientsArray()).thenReturn(ingredientArray.toJSONString());
        when(ingredientServiceMock.checkUniqueness(any(Ingredient.class))).thenReturn(new ArrayList<>());
        when(ingredientServiceMock.save(any(Ingredient.class))).thenReturn(i1);
        when(ingredientServiceMock.remove(1L)).thenReturn(i1);
        Mockito.doThrow(IllegalArgumentException.class).when(ingredientServiceMock).remove(4L);        
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsCourses() throws Exception {       
        mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("ingredients"))
                .andExpect(model().attribute("ingredients", hasSize(1)))
                .andReturn();
        
        verify(ingredientServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listJSONRetrievesIngredientsJSON() throws Exception {
        JSONArray ingredientArray = new JSONArray();
        JSONObject ingredientObject = new JSONObject();
        ingredientObject.put("name", i1.getName());
        ingredientObject.put("id", i1.getId());
        ingredientArray.add(ingredientObject);  
        
        MvcResult res = mockMvc.perform(get("/ainekset/lista")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("")
                .session(new MockHttpSession())
                .accept("application/json"))
                .andReturn();
        
        assertEquals(ingredientArray.toJSONString(), res.getResponse().getContentAsString());
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_add"))
                .andReturn();
        verify(ingredientServiceMock, times(1)).getOptions();
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsIngredient() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Tilli")
                .param("partitive", "Tilliä")                
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ainekset"))
                .andExpect(flash().attribute("success", 
                        "Aines " + i1.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<Ingredient> ingredientArgument = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientServiceMock,times(1)).checkUniqueness(ingredientArgument.capture());
        assertEquals("Tilli", ingredientArgument.getValue().getName());
        verify(ingredientServiceMock, times(1)).save(ingredientArgument.capture());
        assertEquals("Tilli", ingredientArgument.getValue().getName());
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddIngredientView() throws Exception {        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("partitive", "tilliä")                
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_add"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeHasFieldErrors("ingredient", "name"))
                .andReturn();
        
        verify(ingredientServiceMock, times(1)).getOptions();
        verify(ingredientServiceMock,times(1)).checkUniqueness(any(Ingredient.class));
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddIngredientView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(ingredientServiceMock.checkUniqueness(any(Ingredient.class))).thenReturn(errors);
        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Tilli")
                .param("partitive", "tilliä")                
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_add"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeHasFieldErrors("ingredient", "name"))
                .andReturn();   
        
        verify(ingredientServiceMock, times(1)).getOptions();
        verify(ingredientServiceMock,times(1)).checkUniqueness(any(Ingredient.class));
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_edit"))
                .andExpect(model().attributeExists("ingredient"))
                .andReturn();
                
        verify(ingredientServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsIngredient() throws Exception {   
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Tilli")
                .param("partitive", "tilliä")
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ainekset"))
                .andExpect(flash().attribute("success",
                        "Aineksen " + i1.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<Ingredient> ingredientArgument = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientServiceMock, times(1)).checkUniqueness(ingredientArgument.capture());
        assertEquals("Tilli", ingredientArgument.getValue().getName());
        verify(ingredientServiceMock, times(1)).save(ingredientArgument.capture());
        assertEquals("Tilli", ingredientArgument.getValue().getName());
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditIngredientView() throws Exception {           
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .param("partitive", "tilliä")
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_edit"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeHasFieldErrors("ingredient", "name"))
                .andReturn();  
        
        verify(ingredientServiceMock, times(1)).getOptions();
        verify(ingredientServiceMock,times(1)).checkUniqueness(any(Ingredient.class));
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditIngredientView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("name", "Nimi on jo varattu"));
        when(ingredientServiceMock.checkUniqueness(any(Ingredient.class))).thenReturn(errors);
        
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Tilli")
                .param("partitive", "tilliä")                
                .sessionAttr("ingredient", i1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient_edit"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeHasFieldErrors("ingredient", "name"))
                .andReturn();   
        
        verify(ingredientServiceMock, times(1)).getOptions();
        verify(ingredientServiceMock,times(1)).checkUniqueness(any(Ingredient.class));
        verifyNoMoreInteractions(ingredientServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesIngredient() throws Exception {
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ainekset"))
                .andReturn();
        
        verify(ingredientServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(ingredientServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        mockMvc.perform(post("/ainekset/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ainekset"))
                .andExpect(flash().attribute("error", "Poistettavaa ainesta ei löydy!"))
                .andReturn();
        
        verify(ingredientServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(ingredientServiceMock);
    }
}
