/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.unittests.controllers;

import akressiopertti.controller.ControllerUtilities;
import akressiopertti.domain.Recipe;
import akressiopertti.service.RecipeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class RecipeControllerTest {
    
    private final String LIST_URI = "/reseptit";
    private final String DETAILS_URI = "/reseptit/1";
    private final String ADD_URI = "/reseptit/lisaa";
    private final String EDIT_URI = "/reseptit/1/muokkaa";
    private final String DELETE_URI = "/reseptit/1/poista";
    Recipe r1;
    JSONArray ingredientArray;
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private RecipeService recipeServiceMock;
    @Autowired
    private ControllerUtilities controllerUtilitiesMock;
    
    @Before
    public void setUp() throws Exception {
        Mockito.reset(recipeServiceMock);
        Mockito.reset(controllerUtilitiesMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        r1 = new Recipe();
        r1.setId(1L);
        r1.setTitle("Soosi");  
        r1.setInstructions("");
        r1.setNeedsMarinating(false);
        r1.setRecipeIngredients(new ArrayList<>());
        r1.setSource("");
        r1.setComment("");
        r1.setDishType(null);
        r1.setFoodStuffs(new ArrayList<>());
        r1.setCourse(null);
        r1.setRelatedRecipes(new ArrayList<>());
        r1.setBeers(new ArrayList<>());
        r1.setWines(new ArrayList<>());
        r1.setPreparationTime(0);
        
        
        ingredientArray = new JSONArray();
        JSONObject ingredientObject1 = new JSONObject();
        ingredientObject1.put("name", "silakka");
        ingredientObject1.put("id", 1L);
        ingredientArray.add(ingredientObject1);
        JSONObject ingredientObject2 = new JSONObject();
        ingredientObject2.put("name", "suola");
        ingredientObject2.put("id", 2L);
        ingredientArray.add(ingredientObject2);
                
        when(recipeServiceMock.findAll()).thenReturn(Arrays.asList(r1));
        when(recipeServiceMock.findOne(1L)).thenReturn(r1);
        when(recipeServiceMock.checkUniqueness(any(Recipe.class))).thenReturn(new ArrayList<>());
        when(recipeServiceMock.getOptions()).thenReturn(new HashMap<>());
        when(recipeServiceMock.save(any(Recipe.class), any(JSONArray.class))).thenReturn(r1);
        when(recipeServiceMock.remove(1L)).thenReturn(r1);
        Mockito.doThrow(IllegalArgumentException.class).when(recipeServiceMock).remove(4L);
//        when(ControllerUtilities.getJSONArrayFromString(any(String.class))).thenReturn(new JSONArray());
        Mockito.doNothing().when(controllerUtilitiesMock).addOptionsListsToModel(any(Model.class), any(Map.class));
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsRecipes() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", hasSize(1)))
                .andReturn(); 
        
        verify(recipeServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void detailsViewOpens() throws Exception {
        mockMvc.perform(get(DETAILS_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe"))
                .andReturn();
    }    
        
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void detailsViewContainsRecipe() throws Exception {
        mockMvc.perform(get(DETAILS_URI))
                .andExpect(model().attributeExists("recipe"))
                .andReturn();
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_add"))
                .andReturn();
        verify(recipeServiceMock, times(1)).getOptions();
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsRecipe() throws Exception {
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Soosi")
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "{[]}")
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/reseptit"))
                .andExpect(flash().attribute("success",
                        "Resepti \"" + r1.getTitle() + "\" on tallennettu!"))
                .andReturn();
        
        verify(recipeServiceMock, times(1)).checkUniqueness(any(Recipe.class));
        verify(controllerUtilitiesMock, times(1)).getJSONArrayFromString(any(String.class));
        ArgumentCaptor<Recipe> recipeArgument = ArgumentCaptor.forClass(Recipe.class);
        ArgumentCaptor<JSONArray> jsonArrayArgument = ArgumentCaptor.forClass(JSONArray.class);
        verify(recipeServiceMock, times(1)).save(recipeArgument.capture(), jsonArrayArgument.capture());
        assertEquals("Soosi", recipeArgument.getValue().getTitle());
        verifyNoMoreInteractions(controllerUtilitiesMock);
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddRecipeView() throws Exception {
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "")
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_add"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasFieldErrors("recipe", "title"))
                .andReturn();
        verify(recipeServiceMock, times(1)).checkUniqueness(any(Recipe.class));
        verify(controllerUtilitiesMock, times(1)).addOptionsListsToModel(any(Model.class), any(Map.class));
        verify(recipeServiceMock, times(1)).getOptions();
        verifyNoMoreInteractions(controllerUtilitiesMock);
        verifyNoMoreInteractions(recipeServiceMock);
    }    
        
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void uniquenessFailRevertsToAddRecipeView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("title", "Nimi on jo varattu"));
        when(recipeServiceMock.checkUniqueness(any(Recipe.class))).thenReturn(errors);
        
        mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Soosi")  
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "")
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_add"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasFieldErrors("recipe", "title"))
                .andReturn();   
        
        verify(recipeServiceMock, times(1)).getOptions();
        verify(recipeServiceMock,times(1)).checkUniqueness(any(Recipe.class));
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_edit"))
                .andExpect(model().attributeExists("recipe"))
                .andReturn();
                
        verify(recipeServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsRecipe() throws Exception {   
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("title", "Soosi")
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "")
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/reseptit"))
                .andExpect(flash().attribute("success",
                        "Reseptin \"" + r1.getTitle() + "\" tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<Recipe> recipeArgument = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeServiceMock, times(1)).checkUniqueness(recipeArgument.capture());
        assertEquals("Soosi", recipeArgument.getValue().getTitle());
        verify(recipeServiceMock, times(1)).save(recipeArgument.capture(), any(JSONArray.class));
        assertEquals("Soosi", recipeArgument.getValue().getTitle());
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditRecipeView() throws Exception {           
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("title", "")
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "")
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_edit"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasFieldErrors("recipe", "title"))
                .andReturn();  
        
        verify(recipeServiceMock, times(1)).getOptions();
        verify(recipeServiceMock,times(1)).checkUniqueness(any(Recipe.class));
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void uniquenessFailRevertsToEditRecipeView() throws Exception {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("title", "Nimi on jo varattu"));
        when(recipeServiceMock.checkUniqueness(any(Recipe.class))).thenReturn(errors);
        
        mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("title", "Soosi")
                .param("preparationHours", "0")
                .param("preparationMinutes", "0")
                .param("ingredientSet", "")               
                .sessionAttr("recipe", r1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("recipe_edit"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasFieldErrors("recipe", "title"))
                .andReturn();   
        
        verify(recipeServiceMock, times(1)).getOptions();
        verify(recipeServiceMock,times(1)).checkUniqueness(any(Recipe.class));
        verifyNoMoreInteractions(recipeServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesRecipe() throws Exception {
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/reseptit"))
                .andReturn();
        
        verify(recipeServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(recipeServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {       
        mockMvc.perform(post("/reseptit/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/reseptit"))
                .andExpect(flash().attribute("error", "Poistettavaa reseptiä ei löydy!"))
                .andReturn();
        
        verify(recipeServiceMock, times(1)).remove(4L);
        verifyNoMoreInteractions(recipeServiceMock);
    }
}
