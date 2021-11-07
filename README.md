# Especificaciones / Diseño

## Servidor

Servidor hecho en C

- Va a tener un prompt donde el admin puede insertar comandos para controlar el juego
- El juego no "corre solo" sino que el admin controla los eventos importantes del juego

- Crear fantasmas con un comando
- Poner pastillas (resetear con un comando)
- Frutas con valor aleatorio (admin decide cuando y el valor)
- Resetear el nivel cuando se acaben los puntitos pequeños
- Control de velocidad de fantasmas (admin puede variar)
- Controlar puntuación (puntitos pequeños dan 10pts)

El servidor puede aceptar múltiples clientes al mismo tiempo (aunque solo 2 máximos jugadores).
Se va a usar un thread por cliente y un thread para el prompt de comandos.

### Comandos

- help

Muestra los comandos disponibles

- quit

Se sale de la sesión

- Blinky/Pinky/Inky/Clyde [jugador]

Envía al fantasma correspondiente

- pastillas [jugador]

Resetea las pastillas grandes del juego

- pasti [jugador]

Resetea las pastillas pequeñas del juego

- fruta <valor> [jugador]

Crea una fruta con un valor dado

- velocidad +/- [n] [jugador]

Cambia la velocidad de los fantasmas, aumentando con un + y disminuyendo con un -. Se puede especificar un 'n' de qué tanto variar la velocidad, si se omite se toma como 1.


**Nota:** El campo opcional [jugador] es para escoger a cuál jugador enviarle el comando, si no se incluye por default es 0 (el primer jugador).


## Cliente jugador

Cliente hecho en Java
- Corre el jueguito de pacman y se comunica con el servidor

## Cliente observador

- Copia del cliente jugador pero solo puede observar y no realizar acciones


# Ganar puntos

## C

- Constantes en su propio archivo
- Usar structs

## Java

- Clases
- Paquetes
- 3 patrones de diseño

