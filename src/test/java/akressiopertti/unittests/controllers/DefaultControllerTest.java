package akressiopertti.unittests.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class DefaultControllerTest {
    
    private final String RANDOM_URI= "/jotain";
    private final String LOGIN_URI = "/login";
    private final String LOGIN_ERROR_URI = "/login_error";
    private final String LOGOUT_SUCCESS_URI = "/logout_success";
    
    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    
    @Test
    public void randomUriDirectsToDefaultView() throws Exception {
        mockMvc.perform(get(RANDOM_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andReturn();
    }
    
    @Test
    public void loginUriOpensLoginView() throws Exception {
        mockMvc.perform(get(LOGIN_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andReturn();
    }
        
    @Test
    public void loginErrorUriOpensLoginView() throws Exception {
        mockMvc.perform(get(LOGIN_ERROR_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginError"))
                .andReturn();
    }
        
    @Test
    public void logoutSuccessUriOpensLogoutSuccessView() throws Exception {
        mockMvc.perform(get(LOGOUT_SUCCESS_URI))
                .andExpect(status().isOk())
                .andExpect(view().name("logout_success"))
                .andReturn();
    }
}
