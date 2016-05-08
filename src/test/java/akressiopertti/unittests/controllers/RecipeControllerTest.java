/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package akressiopertti.unittests.controllers;

import akressiopertti.domain.Recipe;
import akressiopertti.service.RecipeService;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    
    @Before
    public void setUp() throws Exception {
        Mockito.reset(recipeServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        
        r1 = new Recipe();
        r1.setId(1L);
        r1.setTitle("Soosi");   
        
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
        when(recipeServiceMock.save(any(Recipe.class), any(JSONArray.class))).thenReturn(r1);
        when(recipeServiceMock.remove(1L)).thenReturn(r1);
        Mockito.doThrow(NullPointerException.class).when(recipeServiceMock).remove(4L);   
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"))
                .andReturn(); 
    }
}
