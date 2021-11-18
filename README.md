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
u
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

# Comunicación JSON
{"init": "player"} -> lo envía el cliente como primer mensaje para conectarse como un nuevo jugador, máximo de 2 jugadores

{"init": "observer", "observee": n} -> lo envía el cliente como primer mensaje para conectarse como un nuevo observador que observa al jugador n (0-1)

{"Error": "msg"} -> el servidor retorna un mensaje de error al cliente cuando algo sale mal antes de cerrar la conexión

{"ghost": "name"} -> el servidor le indica al cliente que spawnee el fantasma con nombre 'name'

{"pastillas": "size"} -> el servidor le indica al cliente que resetee las pastillas del tamaño indicado por 'size', puede ser "grandes" o "peques"

{"fruta": n} -> servidor le indica al cliente que cree una fruta de n puntos

{"aumentar": n} -> servidor le indica al cliente que aumente la velocidad en n pasos

{"disminuir": n} -> servidor le indica al cliente que disminuya la velocidad en n pasos
