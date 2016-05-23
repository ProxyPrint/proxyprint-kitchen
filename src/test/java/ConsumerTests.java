
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WebAppConfig.class)
@WebIntegrationTest
public class ConsumerTests {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    private UserDAO users;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void registerUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/consumer/register")
                .param("username", "testusername")
                .param("password", "testpassword")
                .param("name", "testname")
                .param("email", "testemail@mail.pt")
                .param("latitude", "testlat")
                .param("longitude", "testlong")).andReturn();
        
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @After
    public void destroy(){
        User user = this.users.findByUsername("testusername");
        this.users.delete(user);
    }
}
