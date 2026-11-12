# Catálogo de cursos

## Defectos detectados

Ejecución inicial: `mvn test` ejecutó 22 pruebas, con 6 fallos y 1 error. Las pruebas se conservaron sin modificaciones en el commit `test: pruebas de las reglas R1 a R8`.

| ID | Regla | Prueba que lo detecta | Esperado | Obtenido | Causa y corrección |
|---|---|---|---|---|---|
| D-01 | R2 | `rechazaNombreSoloEspacios` | Lanzar `IllegalArgumentException` para un nombre formado solo por espacios. | El curso se registró sin excepción. | Se valida `nombre.isEmpty()` antes de quitar espacios. Corregir validando el nombre recortado. |
| D-02 | R3 | `aceptaCupoCuarenta` | Aceptar cupo 40, porque el rango es inclusivo entre 5 y 40. | `IllegalArgumentException: Cupo fuera de rango: 40`. | La condición usa `cupo >= 40`. Corregir a `cupo > 40`. |
| D-03 | R4 | `rechazaPrecioCero` | Lanzar `IllegalArgumentException` cuando el precio es 0. | El curso se registró sin excepción. | La condición acepta cero con `precio.signum() < 0`. Corregir a `precio.signum() <= 0`. |
| D-04 | R5 | `rechazaCodigoDuplicado` | Lanzar `IllegalStateException` al registrar de nuevo `JAV-101`. | El segundo registro no lanzó excepción. | Se consulta el mapa con `codigo.toLowerCase()` pero se inserta con el código original. Usar la misma clave normalizada en ambas operaciones. |
| D-05 | R6 | `buscaSinDistinguirMayusculas` | Encontrar `Java para principiantes` al buscar `java`. | Se obtuvo una lista con 0 cursos. | `String.contains` distingue mayúsculas y minúsculas. Normalizar ambos textos antes de comparar. |
| D-06 | R7 | `impideSuperarElCupo` | Lanzar `IllegalStateException` en la sexta inscripción para cupo 5. | La sexta inscripción no lanzó excepción. | La validación usa `inscritos > cupo` antes de incrementar. Corregir a `inscritos >= cupo`. |
| D-07 | R8 | `aplicaDescuentoATresEstudiantes` | Total de 25.50 para 3 estudiantes a 10.00 cada uno. | Total de 30.00. | La condición aplica el descuento solo con `estudiantes > 3`. Corregir a `estudiantes >= 3`. |

## Evidencia de depuración

- D-01/R2: la sesión se detuvo en `CatalogoService.registrar`, línea 20. En Variables se observó `nombre = "   "`; por ello `nombre.isEmpty()` era `false` y la validación original no rechazaba el valor.
- D-02/R3: la sesión se detuvo en `CatalogoService.registrar`, línea 23. En Variables se observó `cupo = 40`; la condición original `cupo >= 40` era verdadera, aunque la regla acepta el límite superior.

Las dos capturas fueron tomadas durante la depuración en VS Code. Guárdalas como archivos de evidencia junto al repositorio antes de la entrega final.
