# Cincuentazo 🎴

## Descripción

**Cincuentazo** es un juego de cartas de Poker implementado en JavaFX donde los jugadores (humano y máquinas) deben sobrevivir utilizando estratégicamente sus cartas. El objetivo principal es ser el último jugador en pie, evitando que la suma de las cartas en la mesa supere el número 50.

Este proyecto fue desarrollado como Mini Proyecto #3 para aplicar principios de programación orientada a eventos, diseño de interfaces gráficas, arquitectura MVC, manejo de hilos para concurrencia, excepciones personalizadas y pruebas unitarias con JUnit 5.

### Motivación

El proyecto busca integrar conceptos avanzados de desarrollo de software en Java, incluyendo:
- Diseño de interfaces gráficas intuitivas y modernas con JavaFX
- Implementación de arquitectura Modelo-Vista-Controlador (MVC)
- Manejo de concurrencia mediante hilos para simular jugadores máquina
- Control robusto de errores mediante excepciones personalizadas
- Gestión profesional del código con Git y GitHub

---

## Características principales

✅ **Modo multijugador**: Juega contra 1, 2 o 3 jugadores controlados por IA  
✅ **Interfaz gráfica moderna**: Diseñada con JavaFX y Scene Builder  
✅ **Sistema de turnos inteligente**: Los jugadores máquina toman decisiones automáticas con delays realistas (2-4 segundos)  
✅ **Reglas del juego implementadas**:
  - Cartas del 2 al 8 y el 10 suman su valor
  - Carta 9 no suma ni resta (valor 0)
  - J, Q, K restan 10
  - As suma 1 o 10 según convenga
  - La suma de la mesa no puede exceder 50

✅ **Sistema de eliminación**: Los jugadores sin jugadas válidas son eliminados automáticamente  
✅ **Pantalla de victoria**: Animación y video de celebración al ganar  
✅ **Manejo de excepciones**: Excepciones marcadas y no marcadas personalizadas  
✅ **Hilos concurrentes**: Temporizador para turnos de jugadores máquina  
✅ **Pruebas unitarias**: Validación de lógica del juego con JUnit 5  
✅ **Documentación completa**: Javadoc en inglés para todas las clases  

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java SE** | 17+ | Lenguaje de programación principal |
| **JavaFX** | 17+ | Framework para interfaz gráfica |
| **Scene Builder** | 19.0+ | Diseño visual de interfaces FXML |
| **IntelliJ IDEA** | 2023+ | IDE de desarrollo |
| **JUnit 5** | 5.9+ | Framework de pruebas unitarias |
| **Git & GitHub** | - | Control de versiones |
| **Maven/Gradle** | - | Gestión de dependencias (opcional) |

---

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- ☑️ **Java Development Kit (JDK) 17 o superior**
  - Descargar desde: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)
  - Verificar instalación: `java -version`

- ☑️ **JavaFX SDK 17 o superior** (si no está incluido en tu JDK)
  - Descargar desde: [Gluon JavaFX](https://gluonhq.com/products/javafx/)

- ☑️ **IntelliJ IDEA** (Community o Ultimate)
  - Descargar desde: [JetBrains](https://www.jetbrains.com/idea/download/)

- ☑️ **Git** (para clonar el repositorio)
  - Descargar desde: [git-scm.com](https://git-scm.com/)

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/cincuentazo.git
cd cincuentazo
```

### 2. Abrir el proyecto en IntelliJ IDEA

1. Abre IntelliJ IDEA
2. Selecciona `File > Open`
3. Navega hasta la carpeta del proyecto y ábrela
4. Espera a que IntelliJ indexe el proyecto

### 3. Configurar JavaFX (si es necesario)

Si JavaFX no está incluido en tu JDK:

1. Ve a `File > Project Structure > Libraries`
2. Haz clic en `+` y selecciona `Java`
3. Navega hasta la carpeta `lib` de tu JavaFX SDK
4. Selecciona todos los archivos `.jar`
5. Aplica los cambios

### 4. Configurar VM Options

1. Ve a `Run > Edit Configurations`
2. Agrega las siguientes VM options (ajusta la ruta según tu instalación):

```bash
--module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.media
```

---

## Uso / Ejecución

### Ejecutar desde IntelliJ IDEA

1. Localiza la clase principal con el método `main` (por ejemplo, `Main.java` o `CincuentazoApplication.java`)
2. Haz clic derecho sobre la clase
3. Selecciona `Run 'Main.main()'`

### Compilar y ejecutar desde línea de comandos

```bash
# Compilar
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.media -d out src/main/java/com/cincuentazo/**/*.java

# Ejecutar
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.media -cp out com.cincuentazo.Main
```

### Controles del juego

- **Seleccionar carta**: Haz clic sobre una carta en tu mano
- **Terminar turno**: Haz clic en el botón "End Turn" o presiona `ESPACIO`
- **Menú de pausa**: Presiona `ESC`
- **Ayuda**: Presiona `H`
- **Salir**: Presiona `ESC` y selecciona "Salir al Menú Principal"

---

## Estructura del proyecto

```
cincuentazo/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── cincuentazo/
│       │           ├── controller/          # Controladores MVC
│       │           │   ├── GameController.java
│       │           │   ├── VictoryController.java
│       │           │   └── WelcomeViewController.java
│       │           │
│       │           ├── model/               # Modelos de datos
│       │           │   ├── Card.java
│       │           │   ├── Deck.java
│       │           │   ├── GameModel.java
│       │           │   ├── Player.java
│       │           │   ├── HumanPlayer.java
│       │           │   └── MachinePlayer.java
│       │           │
│       │           ├── view/                # Vistas (Stages)
│       │           │   ├── GameStage.java
│       │           │   ├── VictoryStage.java
│       │           │   └── WelcomeStage.java
│       │           │
│       │           ├── interfaces/          # Interfaces del proyecto
│       │           │   ├── CardSelectionListener.java
│       │           │   ├── GameEventListener.java
│       │           │   ├── Playable.java
│       │           │   ├── TurnCallback.java
│       │           │   └── UIUpdateListener.java
│       │           │
│       │           ├── exception/           # Excepciones personalizadas
│       │           │   ├── GameException.java         (checked)
│       │           │   ├── EmptyDeckException.java    (checked)
│       │           │   ├── InvalidCardException.java  (unchecked)
│       │           │   └── InvalidPlayerActionException.java (unchecked)
│       │           │
│       │           └── Main.java            # Clase principal
│       │
│       └── resources/
│           ├── GameView.fxml                # Vista principal del juego
│           ├── VictoryView.fxml             # Vista de victoria
│           ├── WelcomeView.fxml             # Vista de bienvenida
│           └── com/
│               └── cincuentazo/
│                   └── images/              # Recursos gráficos
│                       ├── cartas/          # Imágenes de cartas
│                       ├── back.png         # Reverso de carta
│                       ├── favicon.png      # Ícono de aplicación
│                       └── VictoryVideo.mp4 # Video de victoria
│
├── test/
│   └── java/
│       └── com/
│           └── cincuentazo/                 # Pruebas unitarias (JUnit 5)
│               ├── CardTest.java
│               ├── DeckTest.java
│               └── GameModelTest.java
│
├── docs/                                    # Documentación Javadoc
├── .gitignore
├── README.md
└── pom.xml / build.gradle                   # Configuración de Maven/Gradle
```

### Descripción de paquetes principales

- **`controller/`**: Controladores que manejan la lógica de interacción entre la vista y el modelo
- **`model/`**: Clases del dominio del juego (cartas, mazo, jugadores, lógica del juego)
- **`view/`**: Stages de JavaFX que representan las diferentes ventanas del juego
- **`interfaces/`**: Interfaces para listeners de eventos y comportamientos compartidos
- **`exception/`**: Excepciones personalizadas marcadas (checked) y no marcadas (unchecked)

---

## Arquitectura

El proyecto implementa el patrón **Modelo-Vista-Controlador (MVC)**:

- **Modelo**: `GameModel`, `Player`, `Card`, `Deck` - Lógica del juego
- **Vista**: Archivos FXML + clases Stage - Presentación visual
- **Controlador**: `GameController`, `WelcomeViewController`, `VictoryController` - Coordinación entre modelo y vista

### Características técnicas

- **Hilos concurrentes**: `MachinePlayerThread` simula el tiempo de pensamiento de la IA
- **Listeners e interfaces**: Desacoplamiento mediante `GameEventListener`, `UIUpdateListener`, `CardSelectionListener`
- **Adaptadores**: `KeyboardAdapter` para manejo de eventos de teclado
- **Estructura de datos**: `LinkedList` para gestión de turnos rotativos, `Stack` para el mazo de cartas
- **Excepciones**: Marcadas (`GameException`, `EmptyDeckException`) y no marcadas (`InvalidCardException`)

---

## Contribuciones

¡Las contribuciones son bienvenidas! Si deseas colaborar:

1. **Fork** el repositorio
2. Crea una **rama** para tu feature: `git checkout -b feature/nueva-funcionalidad`
3. **Commit** tus cambios: `git commit -m 'Añadir nueva funcionalidad'`
4. **Push** a la rama: `git push origin feature/nueva-funcionalidad`
5. Abre un **Pull Request** describiendo tus cambios

### Guías de contribución

- Mantén el código documentado en inglés (Javadoc)
- Sigue las convenciones de nombres de Java
- Escribe pruebas unitarias para nuevas funcionalidades
- Asegúrate de que el código compile sin errores antes de hacer commit

---

## Autores

👨‍💻 **Miguel Angel Martinez Eraso**  
👨‍💻 **William May Barreto**

**Equipo Cincuentazo** - Mini Proyecto #3  
Universidad del Valle - Ingeniería de Sistemas

---

## Licencia

Este proyecto está desarrollado con fines académicos para el curso de Programación Orientada a Objetos.

---

## Notas adicionales

### Problemas conocidos

- ⚠️ En algunos sistemas, el video de victoria puede no reproducirse correctamente si faltan codecs de medios
- ⚠️ La aplicación requiere permisos de lectura en la carpeta de recursos para cargar imágenes



---

## Documentación adicional

Para más detalles sobre la implementación, consulta:

- 📚 **Javadoc**: Abre `docs/index.html` después de generar la documentación
- 📋 **Enunciado del proyecto**: Consulta la rúbrica original del Mini Proyecto #3
- 🧪 **Pruebas unitarias**: Revisa la carpeta `test/` para ejemplos de testing con JUnit 5

---

**¿Preguntas o sugerencias?** Abre un [issue](https://github.com/tu-usuario/cincuentazo/issues) en GitHub.
