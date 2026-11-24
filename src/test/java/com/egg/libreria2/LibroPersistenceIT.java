package com.egg.libreria2;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.entidades.Libro;
import com.egg.libreria2.repositorios.AutorRepositorio;
import com.egg.libreria2.repositorios.EditorialRepositorio;
import com.egg.libreria2.repositorios.LibroRepositorio;
import com.egg.libreria2.services.AutorService;
import com.egg.libreria2.services.EditorialService;
import com.egg.libreria2.services.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({LibroService.class, AutorService.class, EditorialService.class})
@Testcontainers
class LibroPersistenceIT {
    @Container
    static final MySQLContainer<?> DATABASE = new MySQLContainer<>("mysql:8.4.11");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
    }

    @Autowired LibroService service;
    @Autowired LibroRepositorio libros;
    @Autowired AutorRepositorio autores;
    @Autowired EditorialRepositorio editoriales;
    @Autowired EntityManager entityManager;

    private Autor autor;
    private Editorial editorial;

    @BeforeEach void seed() {
        autor = autores.save(new Autor(null, "Synthetic author", true));
        editorial = editoriales.save(new Editorial(null, "Synthetic publisher", true));
    }

    @Test void rejectsInconsistentOrNegativeCopiesBeforeWriting() {
        assertThatThrownBy(() -> service.Save(draft(4, 3, 2))).isInstanceOf(ErrorLibro.class);
        assertThatThrownBy(() -> service.Save(draft(4, -1, 5))).isInstanceOf(ErrorLibro.class);
        assertThatThrownBy(() -> service.AgregarLibro(null, 10L, "Fixture", 2020, 4, 5, 0, autor, editorial))
                .isInstanceOf(ErrorLibro.class);
        assertThat(libros.count()).isZero();
    }

    @Test void persistedCountsStayConsistentAndMissingBooksGiveADomainError() throws Exception {
        Libro created = service.Save(draft(4, 1, 3));
        String id = created.getId();
        entityManager.flush();
        entityManager.clear();
        Libro stored = libros.findById(id).orElseThrow(AssertionError::new);
        assertThat(stored.getEjemplares() - stored.getEjemplaresPrestados()).isEqualTo(stored.getEjemplaresRestantes());

        service.bajaOAltaEditorial(id);
        entityManager.flush();
        entityManager.clear();
        assertThat(libros.findById(id).orElseThrow(AssertionError::new).getAlta()).isFalse();

        assertThatThrownBy(() -> service.bajaOAltaEditorial("missing-book")).isInstanceOf(ErrorLibro.class);
        assertThatThrownBy(() -> service.eliminarLibro("missing-book")).isInstanceOf(ErrorLibro.class);
        assertThat(libros.count()).isEqualTo(1);
    }

    private Libro draft(int total, int borrowed, int available) {
        return new Libro(null, 10L, "Synthetic book", 2020, total, borrowed, available, true, autor, editorial);
    }
}
