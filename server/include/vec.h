#ifndef VEC_H
#define VEC_H

#include <stddef.h>

/**
 * @brief Implementación de un array dinámico
 * 
 */
struct vec
{
	void  *data;
	size_t length;
	size_t capacity;
	size_t element_size;
};


/**
 * @brief Crea e inicializa un nuevo vector
 * 
 * @param element_size Tamaño en memoria de cada elemento
 * @return struct vec Vector inicializado que almacena vectores de tamaño element_size
 */
struct vec vec_new(size_t element_size);

/**
 * @brief Elimina todos los elementos de un vector 
 * 
 * @param vec puntero al vector cuyos elementos se quieren eliminar 
 */
void vec_clear(struct vec *vec);

/**
 * @brief Obtiene el elemento ubicado en un índice dado de un vector
 * 
 * @param vec Puntero al vector en el que se encuentra el elemento
 * @param index Índice del elemento
 * @return void* puntero al vector del cual se quiere obtener el elemento
 */
void *vec_get(struct vec *vec, size_t index);

/**
 * @brief Agrega un espacio a un vector y retorna un puntero al nuevo elemento  
 * 
 * @param vec vector en el cual se quiere realizar un emplace 
 * @return void* puntero al nuevo espacio agregado al vector 
 */
void *vec_emplace(struct vec *vec);

/**
 * @brief Elimina un elemento en el índice dado de un vector
 * 
 * @param vec vector del cual se quiere eliminar el elemento
 * @param index índice del elemento
 */
void vec_delete(struct vec *vec, size_t index);

/**
 * @brief Redimensiona un vector a un nuevo tamaño dado
 * 
 * @param vec Vector a redimensionar
 * @param new_size Nuevo tamaño del vector 
 */
void vec_resize(struct vec *vec, size_t new_size);

#endif
