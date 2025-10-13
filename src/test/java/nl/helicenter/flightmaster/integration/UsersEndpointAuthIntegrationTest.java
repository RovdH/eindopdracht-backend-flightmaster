package nl.helicenter.flightmaster.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsersEndpointAuthIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void authRegister_permitAll_created_withoutAuth_andReturnsIdAndLocation() throws Exception {
        MvcResult result = this.mvc
                .perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"registreer@flightmaster.nl","password":"Banaan123!"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/users/\\d+")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
    }

    @Test
    void usersPost_withoutAuth_unauthorized401() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"guest@flightmaster.nl","password":"Appel123!","role":"USER"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void usersPost_asUser_forbidden403() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"user@flightmaster.nl","password":"Watermeloen123!","role":"USER"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void usersPost_asAdmin_ok201_andTextBody() throws Exception {
        MvcResult result = this.mvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"pilot@flightmaster.nl","password":"Ananas123!","role":"PILOT"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Gebruiker geregistreerd met id: ")))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}

