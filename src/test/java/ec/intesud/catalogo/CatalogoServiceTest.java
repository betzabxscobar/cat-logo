package ec.intesud.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CatalogoServiceTest {

    @ParameterizedTest
    @CsvSource({"jav-101", "JAVA-101", "JAV101", "JAV-10A"})
    @DisplayName("R1: rechaza códigos que no tienen tres mayúsculas, guion y tres dígitos")
    void rechazaCodigosConFormatoInvalido(String codigo) {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(codigo, "Java", 10, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R1: acepta un código con el formato válido")
    void aceptaCodigoConFormatoValido() {
        CatalogoService service = new CatalogoService();

        Curso curso = service.registrar("JAV-101", "Java", 10, new BigDecimal("10.00"));

        assertEquals("JAV-101", curso.getCodigo());
    }

    @Test
    @DisplayName("R2: rechaza un nombre nulo")
    void rechazaNombreNulo() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", null, 10, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R2: rechaza un nombre vacío")
    void rechazaNombreVacio() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "", 10, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R2: rechaza un nombre compuesto solo por espacios")
    void rechazaNombreSoloEspacios() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "   ", 10, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R3: rechaza cupo de 4, por debajo del mínimo")
    void rechazaCupoCuatro() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "Java", 4, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R3: acepta cupo de 5, el mínimo permitido")
    void aceptaCupoCinco() {
        CatalogoService service = new CatalogoService();

        Curso curso = service.registrar("JAV-101", "Java", 5, new BigDecimal("10.00"));

        assertEquals(5, curso.getCupo());
    }

    @Test
    @DisplayName("R3: acepta cupo de 40, el máximo permitido")
    void aceptaCupoCuarenta() {
        CatalogoService service = new CatalogoService();

        Curso curso = service.registrar("JAV-101", "Java", 40, new BigDecimal("10.00"));

        assertEquals(40, curso.getCupo());
    }

    @Test
    @DisplayName("R3: rechaza cupo de 41, por encima del máximo")
    void rechazaCupoCuarentaYUno() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "Java", 41, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("R4: rechaza precio cero")
    void rechazaPrecioCero() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "Java", 10, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("R4: rechaza precio negativo")
    void rechazaPrecioNegativo() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("JAV-101", "Java", 10, new BigDecimal("-0.01")));
    }

    @Test
    @DisplayName("R5: rechaza registrar dos cursos con el mismo código")
    void rechazaCodigoDuplicado() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java inicial", 10, new BigDecimal("10.00"));

        assertThrows(IllegalStateException.class,
                () -> service.registrar("JAV-101", "Java avanzado", 10, new BigDecimal("20.00")));
    }

    @Test
    @DisplayName("R6: encuentra cursos sin distinguir mayúsculas de minúsculas")
    void buscaSinDistinguirMayusculas() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java para principiantes", 10, new BigDecimal("10.00"));

        assertEquals(1, service.buscar("java").size());
    }

    @Test
    @DisplayName("R7: impide superar el cupo del curso")
    void impideSuperarElCupo() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java", 5, new BigDecimal("10.00"));

        for (int i = 0; i < 5; i++) {
            service.inscribir("JAV-101");
        }

        assertThrows(IllegalStateException.class, () -> service.inscribir("JAV-101"));
    }

    @Test
    @DisplayName("R8: no descuenta una inscripción grupal de 2 estudiantes")
    void noAplicaDescuentoADosEstudiantes() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java", 10, new BigDecimal("10.00"));

        assertEquals(new BigDecimal("20.00"), service.precioGrupal("JAV-101", 2));
    }

    @Test
    @DisplayName("R8: aplica 15 por ciento de descuento a 3 estudiantes")
    void aplicaDescuentoATresEstudiantes() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java", 10, new BigDecimal("10.00"));

        assertEquals(new BigDecimal("25.50"), service.precioGrupal("JAV-101", 3));
    }

    @Test
    @DisplayName("R8: aplica 15 por ciento de descuento a más de 3 estudiantes")
    void aplicaDescuentoACuatroEstudiantes() {
        CatalogoService service = new CatalogoService();
        service.registrar("JAV-101", "Java", 10, new BigDecimal("10.00"));

        assertEquals(new BigDecimal("34.00"), service.precioGrupal("JAV-101", 4));
    }

    @Test
    @DisplayName("R8: rechaza una cantidad grupal no positiva")
    void rechazaCantidadGrupalNoPositiva() {
        CatalogoService service = new CatalogoService();

        assertThrows(IllegalArgumentException.class, () -> service.precioGrupal("JAV-101", 0));
    }

    @Test
    @DisplayName("R5: conserva el curso registrado cuando su código es único")
    void conservaCursoConCodigoUnico() {
        CatalogoService service = new CatalogoService();

        Curso curso = service.registrar("JAV-101", "Java", 10, new BigDecimal("10.00"));

        assertNotNull(service.obtener(curso.getCodigo()));
    }
}
