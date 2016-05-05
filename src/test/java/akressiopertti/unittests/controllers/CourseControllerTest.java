package akressiopertti.unittests.controllers;

import akressiopertti.Main;
import akressiopertti.domain.Course;
import akressiopertti.service.CourseService;
import akressiopertti.service.UserService;
import java.util.Arrays;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
//        DirtiesContextTestExecutionListener.class,
//        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExcecutionListener.class})
public class CourseControllerTest {

    private final String LIST_URI = "/ruokalajit";
    private final String ADD_URI = "/ruokalajit/lisaa";
    private final String EDIT_URI = "/ruokalajit/1/muokkaa";
    private final String DELETE_URI = "/ruokalajit/1/poista";
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    @Autowired
    private CourseService courseServiceMock;
    
    @Before
    public void setUp() {
        Mockito.reset(courseServiceMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andReturn(); 
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void listViewContainsCourses() throws Exception {
        Course c1 = new Course();
        c1.setName("Alkuruoka");
        when(courseServiceMock.findAll()).thenReturn(Arrays.asList(c1));
        
        MvcResult res = mockMvc.perform(get(LIST_URI))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("courses", hasSize(1)))
                .andReturn();
    }
}
