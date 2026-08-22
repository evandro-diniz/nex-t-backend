package com.nex_t.config;

import com.nex_t.entity.Usuario;
import com.nex_t.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Após o login Google bem-sucedido: cria o Usuario se for a primeira vez,
 * gera um JWT e redireciona de volta para o frontend com o token na URL.
 * O React deve capturar o token da query string e guardá-lo (ex: em memória/cookie).
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Value("${app.cors.allowed-origins}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String nome = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");

        Usuario usuario = usuarioRepository.findByGoogleId(googleId)
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.builder().email(email).nome(nome).googleId(googleId).build()
                ));

        String token = jwtService.gerarToken(usuario.getEmail());
        response.sendRedirect(frontendUrl + "/auth/callback?token=" + token);
    }
}
