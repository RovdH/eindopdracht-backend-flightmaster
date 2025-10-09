package nl.helicenter.flightmaster.controller;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.security.JwtRequestFilter;
import nl.helicenter.flightmaster.security.SecurityConfig;
import nl.helicenter.flightmaster.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    SecurityConfig securityConfig;

    @MockitoBean
    UserService userService;

    @Test
    void register_setsRoleUser_callsService_returns201_withEmptyBody() throws Exception {
        willReturn(42L).given(userService).registerUser(any(UserRequestDto.class));

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {"email":"rody@banaan.com","password":"Geheimpje123!","role":"IGNORED_BY_CONTROLLER"}
                """))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        ArgumentCaptor<UserRequestDto> captor = ArgumentCaptor.forClass(UserRequestDto.class);
        verify(userService).registerUser(captor.capture());
        UserRequestDto passed = captor.getValue();
        assertThat(passed.getEmail()).isEqualTo("rody@banaan.com");
        assertThat(passed.getRole()).isEqualTo("USER");
    }
}
