package com.egg.libreria2;

import com.egg.libreria2.controller.*;
import com.egg.libreria2.entidades.*;
import com.egg.libreria2.repositorios.*;
import com.egg.libreria2.services.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.DispatcherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PortalController.class, AutorController.class,
        EditorialController.class, LoginController.class, UserController.class,
        ErrorsController.class}, properties = {
        "logging.level.root=WARN", "spring.main.banner-mode=off"
})
@Import(Security.class)
class CatalogSecurityWebTest {

    @Autowired MockMvc mvc;
    @MockBean LibroService books;
    @MockBean AutorService authors;
    @MockBean EditorialService publishers;
    @MockBean UserService users;
    @MockBean LibroRepositorio bookRepository;
    @MockBean AutorRepositorio authorRepository;
    @MockBean EditorialRepositorio publisherRepository;

    @BeforeEach
    void sampleCatalog() {
        Autor author = new Autor();
        author.setId("author-1");
        author.setNombre("Example author");
        author.setAlta(true);
        Editorial publisher = new Editorial();
        publisher.setId("publisher-1");
        publisher.setNombre("Example publisher");
        publisher.setAlta(true);
        Libro book = new Libro();
        book.setId("book-1");
        book.setTitulo("Example book");
        book.setAlta(true);
        book.setAutor(author);
        book.setEditorial(publisher);
        when(books.listAll()).thenReturn(Collections.singletonList(book));
        when(authors.listAll()).thenReturn(Collections.singletonList(author));
        when(publishers.listAll()).thenReturn(Collections.singletonList(publisher));
    }

    static Stream<String> mutationRoutes() {
        return Stream.of("/cargar", "/save", "/cargarautor", "/cargareditorial",
                "/lista-autores/editando-autor", "/lista-editoriales/save-editorial",
                "/disponibilidad-libro", "/lista-autores/disponibilidad-autor",
                "/lista-editoriales/disponibilidad-editorial");
    }

    private MockHttpServletRequestBuilder mutation(String route) {
        return post(route).param("id", "item-1").param("isbn", "123")
                .param("titulo", "Example book").param("anio", "2020")
                .param("ejemplares", "5").param("ejemplaresPrestados", "0")
                .param("ejemplaresRestantes", "5").param("nombre", "Example")
                .param("nombre1", "Example publisher");
    }

    private void noCatalogSideEffects() {
        verifyNoInteractions(books, authors, publishers, bookRepository,
                authorRepository, publisherRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/listaDeAutores", "/lista-editoriales", "/login", "/registro"})
    void publicPagesRemainReadable(String route) throws Exception {
        mvc.perform(get(route)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("mutationRoutes")
    void anonymousMutationWithCsrfRequiresLogin(String route) throws Exception {
        mvc.perform(mutation(route).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        noCatalogSideEffects();
    }

    @ParameterizedTest
    @MethodSource("mutationRoutes")
    void readerCannotMutateEvenWithValidCsrf(String route) throws Exception {
        mvc.perform(mutation(route).with(user("reader").roles("USER")).with(csrf()))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @ParameterizedTest
    @MethodSource("mutationRoutes")
    void administratorWithoutCsrfCannotMutate(String route) throws Exception {
        mvc.perform(mutation(route).with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @ParameterizedTest
    @MethodSource("mutationRoutes")
    void administratorWithCsrfReachesExpectedOperation(String route) throws Exception {
        mvc.perform(mutation(route).with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection());
        switch (route) {
            case "/cargar":
                verify(books).AgregarLibro(isNull(), eq(123L), eq("Example book"),
                        eq(2020), eq(5), eq(0), eq(5), isNull(), isNull());
                break;
            case "/save": verify(books).Save(any(Libro.class)); break;
            case "/cargarautor": verify(authors).AgregarAutor(null, "Example"); break;
            case "/cargareditorial": verify(publishers).AgregarEditorial(null, "Example"); break;
            case "/lista-autores/editando-autor": verify(authors).guardarAutor(any(Autor.class)); break;
            case "/lista-editoriales/save-editorial": verify(publishers).guardarEditorial(any(Editorial.class)); break;
            case "/disponibilidad-libro": verify(books).eliminarLibro("item-1"); break;
            case "/lista-autores/disponibilidad-autor": verify(authors).EliminarAutor("item-1"); break;
            case "/lista-editoriales/disponibilidad-editorial": verify(publishers).eliminarEditorial("item-1"); break;
            default: throw new AssertionError("Unmapped test operation: " + route);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/disponibilidad-libro", "/lista-autores/disponibilidad-autor",
            "/lista-editoriales/disponibilidad-editorial"})
    void formerDestructiveGetsDoNotReachTheService(String route) throws Exception {
        mvc.perform(get(route).param("id", "item-1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/nuevoLibro", "/nuevoAutor", "/nuevaEditorial", "/editar-libro",
            "/lista-autores/editar-autor", "/lista-editoriales/editar-editorial"})
    void administratorFormsRenderCsrfToken(String route) throws Exception {
        mvc.perform(get(route).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));
    }


    @ParameterizedTest
    @ValueSource(strings = {"/nuevoLibro", "/nuevoAutor", "/nuevaEditorial", "/editar-libro",
            "/lista-autores/editar-autor", "/lista-editoriales/editar-editorial"})
    void readerCannotOpenAdministratorForms(String route) throws Exception {
        mvc.perform(get(route).with(user("reader").roles("USER")))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @Test
    void invalidCsrfCannotDeleteEvenForAdministrator() throws Exception {
        mvc.perform(post("/disponibilidad-libro").param("id", "book-1")
                .with(user("admin").roles("ADMIN")).with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/listaDeAutores", "/lista-editoriales"})
    void renderedDeleteFormSubmitsItsOwnCsrfTokenAndSession(String page) throws Exception {
        MvcResult result = mvc.perform(get(page).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andReturn();
        String route = page.equals("/") ? "/disponibilidad-libro"
                : page.equals("/listaDeAutores") ? "/lista-autores/disponibilidad-autor"
                : "/lista-editoriales/disponibilidad-editorial";
        String form = formFor(result, route);
        assertThat(form).contains("method=\"post\"");
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        mvc.perform(post(route).session(session).param("id", "item-1")
                .param("_csrf", csrfFrom(form))).andExpect(status().is3xxRedirection());
        if (page.equals("/")) verify(books).eliminarLibro("item-1");
        else if (page.equals("/listaDeAutores")) verify(authors).EliminarAutor("item-1");
        else verify(publishers).eliminarEditorial("item-1");
    }

    @Test
    void readerPageDoesNotAdvertiseAdministratorControls() throws Exception {
        mvc.perform(get("/").with(user("reader").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("action=\"/disponibilidad-libro\""))))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("href=\"/nuevoLibro\""))));
    }

    @Test
    void registrationWithoutCsrfIsRejected() throws Exception {
        mvc.perform(post("/registro").param("username", "new-reader")
                .param("password", "local-test-fixture").param("password2", "local-test-fixture"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(users);
    }

    @Test
    void registrationUsesRenderedTokenWithoutAuthentication() throws Exception {
        MvcResult result = mvc.perform(get("/registro")).andExpect(status().isOk()).andReturn();
        mvc.perform(post("/registro")
                .session((MockHttpSession) result.getRequest().getSession(false))
                .param("_csrf", csrfFrom(formFor(result, "/registro")))
                .param("username", "new-reader").param("password", "local-test-fixture")
                .param("password2", "local-test-fixture"))
                .andExpect(redirectedUrl("/"));
        verify(users).save("new-reader", "local-test-fixture", "local-test-fixture");
    }

    @Test
    void loginUsesRenderedTokenAndAuthenticatesThroughTheFilterChain() throws Exception {
        when(users.loadUserByUsername("reader")).thenReturn(User.withUsername("reader")
                .password(new BCryptPasswordEncoder().encode("local-test-fixture"))
                .roles("USER").build());
        MvcResult page = mvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        MvcResult login = mvc.perform(post("/logincheck")
                .session((MockHttpSession) page.getRequest().getSession(false))
                .param("_csrf", csrfFrom(formFor(page, "/logincheck")))
                .param("username", "reader").param("password", "local-test-fixture"))
                .andExpect(redirectedUrl("/")).andReturn();
        SecurityContextImpl context = (SecurityContextImpl) login.getRequest().getSession(false)
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(context.getAuthentication().getName()).isEqualTo("reader");
    }

    @Test
    void loginWithoutCsrfDoesNotAuthenticate() throws Exception {
        mvc.perform(post("/logincheck").param("username", "reader")
                .param("password", "local-test-fixture")).andExpect(status().isForbidden());
        verifyNoInteractions(users);
    }

    private MockHttpSession authenticatedSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken("reader", "unused",
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))));
        return session;
    }

    @Test
    void logoutGetDoesNotEndTheSession() throws Exception {
        MockHttpSession session = authenticatedSession();
        mvc.perform(get("/logout").session(session));
        assertThat(session.isInvalid()).isFalse();
        assertThat(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
                .isNotNull();
    }

    @Test
    void logoutWithoutCsrfIsRejectedAndPreservesSession() throws Exception {
        MockHttpSession session = authenticatedSession();
        mvc.perform(post("/logout").session(session)).andExpect(status().isForbidden());
        assertThat(session.isInvalid()).isFalse();
    }

    @Test
    void renderedLogoutFormEndsTheSessionWithCsrf() throws Exception {
        MockHttpSession session = authenticatedSession();
        MvcResult page = mvc.perform(get("/").session(session))
                .andExpect(status().isOk()).andReturn();
        mvc.perform(post("/logout").session(session)
                .param("_csrf", csrfFrom(formFor(page, "/logout"))))
                .andExpect(redirectedUrl("/login?logout"));
        assertThat(session.isInvalid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void unlistedRoutesAreDeniedEvenToAdministrators(String method) throws Exception {
        MockHttpServletRequestBuilder request = method.equals("GET")
                ? get("/unlisted") : post("/unlisted").with(csrf());
        mvc.perform(request.with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
        noCatalogSideEffects();
    }

    @Test
    void errorDispatchCanRenderErrorPageWithoutRequiringLogin() throws Exception {
        mvc.perform(get("/error").with(request -> { request.setDispatcherType(DispatcherType.ERROR); return request; })
                .requestAttr("javax.servlet.error.status_code", 404))
                .andExpect(status().isOk()).andExpect(view().name("error"))
                .andExpect(model().attribute("codigo", 404));
    }

    private String formFor(MvcResult page, String action) throws Exception {
        Pattern pattern = Pattern.compile("<form\\b[^>]*action=\"" + Pattern.quote(action)
                + "\"[^>]*>([\\s\\S]*?)</form>");
        Matcher matcher = pattern.matcher(page.getResponse().getContentAsString());
        assertThat(matcher.find()).as("Rendered form for %s", action).isTrue();
        return matcher.group();
    }

    private String csrfFrom(String form) {
        Matcher matcher = Pattern.compile("<input[^>]*name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(form);
        assertThat(matcher.find()).as("CSRF token rendered inside the form").isTrue();
        return matcher.group(1);
    }
}
