package nl.helicenter.flightmaster.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegisterLoginIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void registerThenLogin_returnsTokens() throws Exception {
        String email = "novi@hogeschool.nl";
        String password = "Banaan123!";

        MvcResult resultRegister = this.mvc
                .perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {"email":"%s","password":"%s"}
                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MvcResult resultLogin = this.mvc
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {"email":"%s","password":"%s"}
                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken", not(emptyOrNullString())))
                .andExpect(jsonPath("$.refreshToken", not(emptyOrNullString())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}
