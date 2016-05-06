package akressiopertti.unittests.controllers;

import akressiopertti.domain.Course;
import akressiopertti.service.CourseService;
import java.util.ArrayList;
import java.util.Arrays;
import static junit.framework.Assert.assertEquals;
import org.hamcrest.Matchers;
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
import org.springframework.dao.DataAccessException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.ObjectError;
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
        
        verify(courseServiceMock,  times(1)).findAll();
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewOpens() throws Exception {
        MvcResult res = mockMvc.perform(get(ADD_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("course_add"))
                .andReturn();
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void addViewAddsCourse() throws Exception {
        Course course = new Course();
        when(courseServiceMock.checkUniqueness(any(Course.class))).thenReturn(new ArrayList<ObjectError>());
        when(courseServiceMock.save(any(Course.class))).thenReturn(course);
        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Alkuruoka")
                .param("ordinality", "1")                
                .sessionAttr("course", course)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokalajit"))
                .andExpect(flash().attribute("success", 
                        "Ruokalaji " + course.getName() + " tallennettu!"))
                .andReturn();
        
        ArgumentCaptor<Course> courseArgument = ArgumentCaptor.forClass(Course.class);
        verify(courseServiceMock,times(1)).checkUniqueness(courseArgument.capture());
        assertEquals("Alkuruoka", courseArgument.getValue().getName());
        verify(courseServiceMock, times(1)).save(courseArgument.capture());
        assertEquals("Alkuruoka", courseArgument.getValue().getName());
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidAddRevertsToAddCourseView() throws Exception {
        Course course = new Course();
        when(courseServiceMock.checkUniqueness(any(Course.class))).thenReturn(new ArrayList<ObjectError>());
        when(courseServiceMock.save(any(Course.class))).thenReturn(course);
        
        MvcResult res = mockMvc.perform(post(ADD_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("ordinality", "1")                
                .sessionAttr("course", course)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("course_add"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeHasFieldErrors("course", "name"))
                .andReturn();
        
        verify(courseServiceMock,times(1)).checkUniqueness(any(Course.class));
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void editViewOpens() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("Alkuruoka");
        course.setOrdinality(1);
        when(courseServiceMock.checkUniqueness(any(Course.class))).thenReturn(new ArrayList<ObjectError>());
        when(courseServiceMock.findOne(1L)).thenReturn(course);
        
        MvcResult res = mockMvc.perform(get(EDIT_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("course_edit"))
                .andExpect(model().attributeExists("course"))
                .andReturn();
                
        verify(courseServiceMock, times(1)).findOne(1L);
    }
    
    @Test
    @WithMockUser(username = "a",roles = {"ADMIN"})
    public void editViewEditsCourse() throws Exception {
        Course course = new Course();
        when(courseServiceMock.checkUniqueness(any(Course.class))).thenReturn(new ArrayList<ObjectError>());
        when(courseServiceMock.save(any(Course.class))).thenReturn(course);
        
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "Alkuruoka")
                .param("ordinality", "1")
                .sessionAttr("course", course)
        )
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokalajit"))
                .andExpect(flash().attribute("success",
                        "Ruokalajin " + course.getName() + " tiedot päivitetty!"))
                .andReturn();
                
        ArgumentCaptor<Course> courseArgument = ArgumentCaptor.forClass(Course.class);
        verify(courseServiceMock, times(1)).checkUniqueness(courseArgument.capture());
        assertEquals("Alkuruoka", courseArgument.getValue().getName());
        verify(courseServiceMock, times(1)).save(courseArgument.capture());
        assertEquals("Alkuruoka", courseArgument.getValue().getName());
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void invalidUpdateRevertsToEditCourseView() throws Exception {
        Course course = new Course();
        when(courseServiceMock.checkUniqueness(any(Course.class))).thenReturn(new ArrayList<ObjectError>());        
         
        MvcResult res = mockMvc.perform(post(EDIT_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name", "")
                .param("ordinality", "1")
                .sessionAttr("course", course)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("course_edit"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeHasFieldErrors("course", "name"))
                .andReturn();  
        
        verify(courseServiceMock,times(1)).checkUniqueness(any(Course.class));
        verifyNoMoreInteractions(courseServiceMock);
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deleteRemovesCourse() throws Exception {
        Course course = new Course();
        course.setId(1L);
        when(courseServiceMock.findOne(1L)).thenReturn(course);
        when(courseServiceMock.remove(1L)).thenReturn(course);
        
        MvcResult res = mockMvc.perform(post(DELETE_URI))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokalajit"))
                .andReturn();
        
        verify(courseServiceMock, times(1)).remove(1L);
        verifyNoMoreInteractions(courseServiceMock);                
    }
    
    @Test
    @WithMockUser(username = "a", roles = {"ADMIN"})
    public void deletingInvalidIdShowsErrorMessage() throws Exception {
        Mockito.doThrow(NullPointerException.class).when(courseServiceMock).remove(4L);
        
        MvcResult res = mockMvc.perform(post("/ruokalajit/4/poista"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/ruokalajit"))
                .andExpect(flash().attribute("error", "Poistettavaa ruokalajia ei löydy!"))
                .andReturn();
        
        
    }
}
