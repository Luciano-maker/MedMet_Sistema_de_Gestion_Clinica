# MedMet - Sistema de Gestión Clínica 🏥

Sistema de escritorio transaccional orientado a la optimización, digitalización y administración integral de turnos médicos, registro de pacientes, cobro de aranceles e historias clínicas para el centro de salud ficticio **MedMet** (San José de Metán, Salta).

Desarrollado en **Java** bajo el paradigma de Programación Orientada a Objetos (POO), con interfaz gráfica construida en **JavaFX** y persistencia relacional en **MySQL** mediante conectividad **JDBC**, implementando los patrones de diseño **Modelo-Vista-Controlador (MVC)**.

##  Tabla de Contenidos

 1. [Descripción General](#-descripción-general)

 2. [Tecnologías y Herramientas](#-tecnologías-y-herramientas)

 3. [Arquitectura del Software](#-arquitectura-del-software)

 4. [Estructura del Proyecto](#-estructura-del-proyecto)

 5. [Requisitos Previos](#-requisitos-previos)

 6. [Instalación y Base de Datos](#-instalación-y-base-de-datos)

 7. [Configuración Dinámica de Conexión](#-configuración-dinámica-de-conexión)

 8. [Credenciales para Evaluación](#-credenciales-para-evaluación)

 9. [Instrucciones de Ejecución](#-instrucciones-de-ejecución)

10. [Módulos y Casos de Uso Implementados](#-módulos-y-casos-de-uso-implementados)

11. [Autor y Datos Académicos](#-autor-y-datos-académicos)

## Descripción General

El centro de salud MedMet presentaba dificultades operativas derivadas de la gestión manual en papel: solapamiento de agendas médicas, extravío de antecedentes clínicos, retrasos en la mesa de entrada y falta de trazabilidad en los cobros de consultas.

Este software reemplaza los procesos tradicionales mediante un sistema transaccional de escritorio que centraliza las operaciones del personal administrativo y médico, garantizando:

* Control estricto de concurrencia y solapamiento horario en agendas de profesionales.

* Integridad referencial y transaccionalidad atómica en los pagos de consultas.

* Trazabilidad completa de historias clínicas y evoluciones médicas por paciente.

* Control de acceso basado en perfiles (Recepcionista y Médico).

## Tecnologías y Herramientas

* **Lenguaje de Programación:** Java (OpenJDK 17+)

* **Interfaz Gráfica de Usuario (GUI):** JavaFX 17+ (programático sin FXML)

* **Gestor de Dependencias y Construcción:** Apache Maven

* **Sistema de Gestión de Bases de Datos:** MySQL Server 8.0+

* **Conectividad a Datos:** JDBC (`mysql-connector-j` 8.3+)

* **Entorno de Desarrollo (IDE):** IntelliJ IDEA

* **Control de Versiones:** Git y GitHub

## Arquitectura del Software

El sistema está estructurado respetando la separación de responsabilidades y el desacoplamiento de capas:

* **Modelo-Vista-Controlador (MVC):**

  * **Modelo (`medmet.modelo`):** Contiene la lógica de negocio y las entidades del dominio clínico.

  * **Vista (`medmet.vistas`):** Componentes gráficos en JavaFX encargados del renderizado y captura de eventos.

  * **Controlador (`medmet.controlador`):** Intermediarios que coordinan las acciones del usuario, validan reglas y comandan a las vistas y DAOs.

* **Patrón DAO (Data Access Object):** Centraliza la persistencia relacional en sentencias parametrizadas (`PreparedStatement`) mediante JDBC, aislando el motor SQL del resto del código.

* **Pilares de POO Aplicados:**

  * **Abstracción:** Clase abstracta `Persona` que centraliza atributos filiatorios y define contratos de visualización polimórfica (`mostrarDetalle()`).

  * **Encapsulamiento:** Modificadores de visibilidad (`private`/`protected`) con acceso mediante métodos de lectura/escritura y validaciones de invariantes.

  * **Herencia:** Subclases `Paciente`, `Medico` y `Recepcionista` que extienden de `Persona` reutilizando comportamiento e invocando a la superclase mediante `super(...)`.

  * **Polimorfismo:** Despacho dinámico de métodos sobrescritos (`@Override`) y tratamiento polimórfico en colecciones dinámicas (`List<Turno>`, `List<EvolucionMedica>`).

## Estructura del Proyecto

```
MedMet_Sistema_de_Gestion_Clinica/
│
├── sistemamedmetdb.sql               # Script DDL/DML con esquema y datos semilla de MySQL
├── pom.xml                           # Descriptor Maven de dependencias y plugins                      
│
└── src/
    └── medmet/
        ├── app/                      # Clases de arranque (AppMedMet, LauncherMainApp)
        ├── conexion/                 # Conector JDBC y gestor dinámico de propiedades (ConexionBD)
        ├── controlador/              # Controladores MVC (LoginControlador, RecepcionistaControlador, MedicoControlador)
        ├── dao/                      # Capa DAO con consultas SQL (TurnoDAO, PacienteDAO, MedicoDAO, PagoDAO, HistoriaClinicaDAO)
        ├── excepciones/              # Excepciones personalizadas del dominio (MedMetException, TurnoInvalidoException)
        ├── gestor/                   # Clases de servicio y reglas de negocio (GestorTurnos, GestorPagos, etc.)
        ├── modelo/                   # Entidades de dominio (Persona, Paciente, Medico, Turno, Pago, etc.)
        └── vistas/                   # Formularios, grillas y cuadros de diálogo modales de JavaFX

```

##  Requisitos Previos

1. **Java Development Kit (JDK):** Versión 17 o superior correctamente instalada y configurada en las variables de entorno (`JAVA_HOME`).

2. **MySQL Server:** Versión 8.0 o superior en ejecución en el puerto estándar `3306`.

3. **Apache Maven:** Versión 3.8+ (opcional si se ejecuta mediante el wrapper del IDE).

4. **Git:** Para clonar el repositorio localmente.

##  Instalación y Base de Datos

1. Abra una consola o cliente de MySQL (MySQL Workbench, DBeaver o línea de comandos).

2. Ejecute el script `SistemaMedMetDB.sql` provisto en la raíz del proyecto para crear la base de datos `sistemamedmetdb` e insertar los registros iniciales:

```
SOURCE /ruta/del/proyecto/SistemaMedMetDB.sql;

```

El script configurará:

* Esquema relacional con tablas: `persona`, `paciente`, `medico`, `especialidad`, `usuario`, `turno`, `pago`, `historia_clinica` y `evolucion_medica`.

* Claves foráneas con integridad referencial y restricciones de unicidad.

* Carga de profesionales, pacientes, credenciales de usuarios y turnos de demostración.

##  Configuración Dinámica de Conexión

Para facilitar la corrección docente sin exigir modificaciones en el código fuente:

* **Detección Automática:** Al ejecutar la aplicación, el sistema intenta conectarse con los parámetros locales predeterminados. Si la conexión falla, se abre automáticamente la ventana modal **"MedMet | Configuración de Base de Datos"**.

* **Persistencia sin recompilación:** Al ingresar el usuario y contraseña del evaluador y presionar **Guardar y Continuar**, los parámetros se almacenan en un archivo local `db_config.properties` para futuros arranques.

* **Acceso Manual:** La pantalla de inicio de sesión dispone de un enlace inferior directo **"⚙ Configurar BD"** para alterar los parámetros de red o credenciales en cualquier momento.

##  Credenciales para Evaluación

El acceso al sistema está protegido según el rol operativo:

| Rol | Usuario | Contraseña | Nombre y Contexto | 
| ----- | ----- | ----- | ----- | 
| **Recepcionista** | `recepcion_central` | `Admin2026*` | Laura Gómez — Mesa de Entrada | 
| **Médico** | `dr_fernandez` | `DocCardio26` | Dr. Marcos Fernández — Cardiología | 
| **Médico** | `dra_perez` | `DocGen2026` | Dra. Valeria Pérez — Medicina General | 
| **Médico** | `dra_ruiz` | `DocOdonto26` | Dra. Andrea Ruiz — Odontología | 

##  Instrucciones de Ejecución

### Opción A: Desde IntelliJ IDEA (Recomendado)

1. Clone el repositorio:

   ```
   git clone https://github.com/Luciano-maker/MedMet_Sistema_de_Gestion_Clinica.git
   
   ```

2. Abra la carpeta clonada en IntelliJ IDEA (`File -> Open...`).

3. Aguarde a que Maven sincronice las dependencias del archivo `pom.xml`.

4. Ubique la clase de entrada en `src/medmet/app/LauncherMainApp.java` (o `AppMedMet.java`).

5. Haga clic derecho y seleccione **Run 'LauncherMainApp.main()'**.

### Opción B: Mediante Maven por Consola

Abra una terminal en el directorio raíz del proyecto y ejecute:

```
mvn clean compile javafx:run

```

##  Módulos y Casos de Uso Implementados

### 1. Acceso y Seguridad 

* Autenticación basada en roles mediante `RadioButtons`.

* Encriptación visual del campo de contraseña y control de sesiones activas.

### 2. Módulo de Recepción y Mesa de Entrada

* **Agenda Médica Diaria:** Visualización de citas en grilla cronológica con filtros por fecha, médico y especialidad.

* **Confirmación de Asistencia:** Transición del estado de cita a *"En espera"* al presentarse el paciente en recepción.

* **Agendamiento Inteligente :** Selector de turnos en bloques clínicos de 30 minutos con validación contra solapamientos de horario en MySQL.

* **Reprogramación y Cancelación :** Modificación de fechas/horas de citas activas y liberación de cupos en agenda.

* **Cobro de Consultas:** Diálogo de facturación con transacción atómica en base de datos: inserción en tabla `pago` y actualización del estado del turno a `PAGADO`.

* **Padrón de Pacientes:** Búsqueda en tiempo real por DNI o apellido, altas y edición de coberturas de salud.

### 3. Módulo de Consultorio Médico 

* **Llamada de Pacientes:** Consulta de la lista de espera del profesional autenticado.

* **Ficha Médica:** Visualización del historial de antecedentes y consultas previas asociadas a la historia clínica del paciente.

* **Evolución Clínica:** Asentamiento de motivo de consulta, diagnóstico y plan de tratamiento, registrando la atención en `evolucion_medica` y marcando el turno como `Atendido`.

##  Autor y Datos Académicos

* **Estudiante:** Luciano Iván Ruiz Jiménez

* **Carrera:** Licenciatura en Informática

* **Materia:** Seminario de Práctica

* **Institución:** Universidad Siglo 21

* **Año Académico:** 2026