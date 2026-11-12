package ec.intesud.catalogo;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogoController.class)
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogoService service;

    @Test
    @DisplayName("POST /api/cursos devuelve 400 ante datos inválidos")
    void devuelveBadRequestParaDatosInvalidos() throws Exception {
        when(service.registrar(anyString(), anyString(), anyInt(), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("El nombre es obligatorio"));

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":"JAV-101","nombre":"","cupo":10,"precio":10.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El nombre es obligatorio")));
    }

    @Test
    @DisplayName("GET de precio grupal devuelve 404 para curso inexistente")
    void devuelveNotFoundParaCursoInexistente() throws Exception {
        when(service.precioGrupal(eq("XXX-999"), eq(3)))
                .thenThrow(new NoSuchElementException("No existe el curso XXX-999"));

        mockMvc.perform(get("/api/cursos/XXX-999/precio-grupal")
                        .param("estudiantes", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No existe el curso XXX-999")));
    }
}
