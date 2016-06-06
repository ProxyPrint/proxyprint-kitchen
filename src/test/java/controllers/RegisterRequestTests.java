package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WebAppConfig.class)
@WebIntegrationTest
public class RegisterRequestTests {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    private MockMvc mockMvc;

    @Autowired
    private AdminDAO admins;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private Gson GSON;

    private RegisterRequest exampleRR = new RegisterRequest("manager", "manager", "manager@email", "1234", "address", 45d, 32d, "NIF", "name", false);

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    private RegisterRequest addRegisterRequest() throws Exception {
        String body = GSON.toJson(exampleRR);

        MvcResult mvcResult = this.mockMvc.perform(post("/request/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String responseText = mvcResult.getResponse().getContentAsString();

        RegisterRequest response = GSON.fromJson(responseText, RegisterRequest.class);

        //set ids to be equal
        exampleRR.setId(response.getId());

        assert (exampleRR.equals(response));

        return response;
    }

    //testar se o servidor regista pedido reprografia
    @Test
    public void registerRequestTest() throws Exception {
        addRegisterRequest();
    }

    @Test
    public void acceptRegisterRequest() throws Exception {
        //test admin
        Admin admin = admins.save(new Admin("admin", "admin", "admin@mail.pt"));

        //nr of pshops before
        int pshopsBefore = (int) this.printshops.count();

        RegisterRequest rr = addRegisterRequest();

        MvcResult mvcResult = this.mockMvc.perform(post("/request/accept/" + (int) rr.getId())
                .with(httpBasic("admin", "admin"))).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String response = mvcResult.getResponse().getContentAsString();

        JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();

        boolean status = jsonObject.get("success").getAsBoolean();

        assert (status);

        int pshopsAfter = (int) this.printshops.count();

        assert (pshopsBefore == pshopsAfter - 1);
        
        Manager m = this.managers.findByUsername("manager");
        assert(m.getPassword().equals("1234"));
    }
}
