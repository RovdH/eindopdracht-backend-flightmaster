package nl.helicenter.flightmaster.controller;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.security.JwtRequestFilter;
import nl.helicenter.flightmaster.security.JwtUtil;
import nl.helicenter.flightmaster.service.UserPhotoService;
import nl.helicenter.flightmaster.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerUnitTest {

    @Autowired MockMvc mvc;

    @MockitoBean JwtRequestFilter jwtRequestFilter;
    @MockitoBean JwtUtil jwtUtil;

    @MockitoBean UserService userService;
    @MockitoBean UserPhotoService userPhotoService;

    @Test
    void createUser_asAdmin_returns200_withTextMessage_andPassesRoleFromPayload() throws Exception {

        given(userService.registerUser(any(UserRequestDto.class))).willReturn(123L);

        MvcResult result = this.mvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {"email":"pilot@flightmaster.nl","password":"Bananenpannekoek1!","role":"PILOT"}
                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Gebruiker geregistreerd met id: 123"))
                .andExpect(header().string("Location", matchesPattern("/users/\\d+")))
                .andReturn();


        var captor = ArgumentCaptor.forClass(UserRequestDto.class);
        verify(userService).registerUser(captor.capture());
        var dto = captor.getValue();
        assertThat(dto.getEmail()).isEqualTo("pilot@flightmaster.nl");
        assertThat(dto.getRole()).isEqualTo("PILOT");

    }

    @Test
    void createUser_invalidPayload_returns400() throws Exception {
        MvcResult result = this.mvc
        .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {"email":"", "password":"", "role":""}
                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
