package nl.helicenter.flightmaster.controller;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.security.JwtUtil;
import nl.helicenter.flightmaster.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AuthenticationManager authenticationManager;
    @MockitoBean
    UserDetailsService userDetailsService;
    @MockitoBean
    JwtUtil jwt;

    @MockitoBean
    UserService userService;

    @Test
    void register_forcesRoleUSER_andReturns201EmptyBody() throws Exception {
        willReturn(1L).given(userService).registerUser(any(UserRequestDto.class));

        MvcResult result = this.mvc
                .perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"rody@banaan.com","password":"Geheimpje123!","role":"ADMIN"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        var captor = ArgumentCaptor.forClass(UserRequestDto.class);
        verify(userService).registerUser(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("USER");
        assertThat(captor.getValue().getEmail()).isEqualTo("rody@banaan.com")
        ;

    }

    @Test
    void register_invalidPayload_returns400() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"email":"rodybanaan","password":"!","role":""}
                                """))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

}
