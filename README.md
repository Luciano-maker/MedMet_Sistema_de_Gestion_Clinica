# MedMet — Sistema de Gestión Clínica

Sistema integral de escritorio para la optimización de procesos asistenciales y administrativos del centro de salud ficticio **MedMet**. Permite la administración integral de pacientes, profesionales médicos, especialidades, programación y ciclo de vida de citas, cobros y el registro de atenciones en consultorio con historias clínicas y evoluciones médicas.

Desarrollado en el marco de la asignatura **Seminario de Práctica de Informática** de la carrera **Licenciatura en Informática** (Universidad Siglo 21).

##  Características Principales

El sistema se organiza en dos perfiles operativos diferenciados bajo control de acceso autenticado:

###  Módulo de Administración (Mesa de Entrada / Recepción)

* **Autenticación Segura:** Validación de credenciales con redirección según perfil asignado.

* **Gestión de Especialidades Médicas:** Alta, modificación, consulta y baja lógica con validación de turnos asociados.

* **Gestión de Profesionales Médicos:** Padrón de médicos con matrícula profesional, datos de contacto y especialidad asociada.

* **Padrón de Pacientes:** Registro y actualización de datos personales, cálculo de edad y cobertura médica (particular u obra social).

* **Agenda Diaria y Turnos:**

  * Agendamiento con validación preventiva de solapamiento de horarios por profesional.

  * Marcado de arribo a sala de espera (*"En espera"*).

  * Reprogramación y cancelación de citas médicas.

  * Filtros combinados por fecha, profesional y especialidad.

* **Caja y Cobros:** Asentamiento de transacciones por métodos de pago (efectivo, débito, transferencia) y emisión de comprobantes digitales.

###   Módulo de Consultorio Médico

* **Consola de Atención Diaria:** Agenda del día filtrada exclusivamente para el profesional autenticado, con priorización de pacientes en espera.

* **Historia Clínica y Evolución Médica:**

  * Consulta cronológica de antecedentes previos del paciente.

  * Carga de diagnóstico, hallazgos clínicos y plan terapéutico / farmacológico.

  * Cierre de atención con transición automática del turno a estado *"Finalizado"*.

* **Buscador General de Expedientes:** Consulta histórica de historias clínicas e intervenciones por DNI o apellido de paciente.

##   Arquitectura y Principios de Diseño

El proyecto implementa el patrón arquitectónico **Modelo-Vista-Controlador (MVC)**, garantizando un bajo acoplamiento y alta cohesión entre la interfaz visual y las reglas del negocio:

```
 ┌────────────────┐         Dispara Eventos         ┌────────────────────────┐
 │      VISTA     │ ──────────────────────────────> │      CONTROLADOR       │
 │    (JavaFX)    │ <────────────────────────────── │    (Coordina flujo)    │
 └────────────────┘         Actualiza Datos         └────────────────────────┘
                                                                 │
                                                       Invoca Lógica de Negocio
                                                                 ▼
 ┌────────────────┐                                 ┌────────────────────────┐
 │  BANCO DATOS   │ <────────────────────────────── │    GESTOR / MODELO     |
 │ (Repositorio)  │    Persistencia en Memoria    | (POO, Reglas y Valid) |
 └────────────────┘                                 └────────────────────────┘

```

### Aplicación de los 4 Pilares de la Programación Orientada a Objetos (POO)

1. **Abstracción**

2. **Encapsulamiento**

3. **Herencia**

4. **Polimorfismo**

### Manejo de Excepciones del Dominio

Jerarquía de excepciones personalizadas para el tratamiento de inconsistencias:

* `MedMetException`: Excepción verificada base del centro de salud.

* `TurnoInvalidoException`: Especialización que restringe superposición de franjas horarias o transiciones de estado no permitidas.

##  Tecnologías y Requisitos Previos

* **Lenguaje:** Java 17 o superior (compatible con JDK 21 y JDK 24).

* **Framework Gráfico:** JavaFX 17.0.20.

* **Gestor de Construcción:** Apache Maven 3.8+.

* **Entorno de Desarrollo (IDE recomendado):** IntelliJ IDEA (Community o Ultimate), Eclipse IDE, o Visual Studio Code.

##  Instrucciones de Instalación y Ejecución

### 1. Clonar el Repositorio

### 2. Opción A: Ejecutar mediante Maven (Recomendada)

Al contar con el archivo `pom.xml`, Maven descargará automáticamente las dependencias de JavaFX requeridas sin necesidad de configurar librerías manuales:

```
mvn clean compile javafx:run

```

### 3. Opción B: Ejecutar desde IntelliJ IDEA u otros IDEs

1. Abre IntelliJ IDEA y selecciona **File** ➔ **Open...** seleccionando la carpeta raíz del proyecto.

2. Si el IDE detecta el archivo `pom.xml`, presiona **Load Maven Project** o haz clic en el icono flotante de sincronización.

3. Navega en el árbol de archivos hacia:

   `src/com/medmet/app/LauncherMainApp.java`

4. Haz clic derecho sobre el archivo y selecciona **Run 'LauncherMainApp.main()'**.

> **Nota:** La clase `LauncherMainApp.java` inicializa el ciclo de vida de JavaFX desacoplada de la clase principal `Application`, permitiendo ejecutar la aplicación sin requerir parámetros `--module-path` ni `--add-modules` en la configuración de ejecución del IDE.

##  Credenciales de Prueba

El sistema cuenta con datos precargados para verificar el funcionamiento de los distintos módulos:

| Perfil | Nombre de Usuario | Contraseña | Rol / Profesional Asignado | 
| ----- | ----- | ----- | ----- | 
| **Recepcionista** | `recepcion_central` | `Recep2026` | Laura Gómez (Mesa de Entrada) | 
| **Médico** | `dr_fernandez` | `DocCardio26` | Dr. Marcos Fernández (Cardiología - MP-5842) | 
| **Médica** | `dra_perez` | `DocClinica26` | Dra. Valeria Pérez (Medicina General - MP-6120) | 
| **Médica** | `dra_ruiz` | `DocOdonto26` | Dra. Andrea Ruiz (Odontología - MP-4399) | 

##  Información Académica

* **Institución:** Universidad Siglo 21

* **Carrera:** Licenciatura en Informática

* **Asignatura:** Seminario de Práctica de Informática

* **Año:** 2026
