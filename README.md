# Simulador de Supermercado - Concurrencia en Java 🛒

Este proyecto es una solución diseñada para el Departamento de Tecnología en Desarrollo de Software de la **IUDigital de Antioquia**. Su objetivo es simular el proceso de cobro en cajas registradoras de un supermercado, aplicando conceptos avanzados de **concurrencia e hilos (Threads)** en Java.

## 📝 Descripción del Desafío

El sistema simula el proceso de cobro cliente a cliente. Si el supermercado cuenta con varias cajas abiertas, estas deben procesar a sus respectivos clientes en paralelo.
El objetivo principal es demostrar cómo la implementación de hilos agiliza drásticamente el proceso de cobro frente a un sistema secuencial tradicional.

## 🚀 Conceptos Aplicados

- **Concurrencia:** Uso de múltiples hilos trabajando simultáneamente.
- **Runnable y Threads:** Separación de la lógica de negocio (Runnable) y el mecanismo de ejecución (Thread).
- **Sincronización:** Uso de `hilo.join()` para esperar a que todas las cajeras terminen antes de emitir el reporte final.
- **Colas (Queue):** Gestión de filas de clientes bajo el principio FIFO (First In, First Out).

## 📂 Estructura del Proyecto

El código está estructurado bajo principios de Programación Orientada a Objetos (POO):

- `Producto.java`: Entidad que representa un artículo con su nombre, precio y tiempo teórico que toma escanearlo.
- `Cliente.java`: Entidad que agrupa una lista de productos (carrito) y almacena el tiempo total que tomó su atención.
- `Cajera.java`: Implementa `Runnable`. Contiene la lógica para procesar la cola de clientes y simular el tiempo de escaneo mediante pausas no bloqueantes (`Thread.sleep()`).
- `SimulacionSupermercado.java`: Orquestador del sistema. Genera clientes aleatorios, los distribuye en las cajas (estrategia round-robin), lanza los hilos y genera el reporte consolidado.
- `Main.java`: Punto de entrada de la aplicación.

## 🛠️ Cómo compilar y ejecutar

Para correr este proyecto desde la terminal o consola de comandos, sigue estos pasos:

1. Abre tu terminal y navega hasta el directorio raíz del proyecto.
2. Compila todos los archivos `.java`:
   ```bash
   javac *.java
   ```
3. Ejecuta la clase principal:
   ```bash
   java Main
   ```

## 📊 Reporte Final

Al terminar la ejecución, el sistema imprimirá en la consola un desglose completo indicando:
- Qué clientes atendió cada cajera.
- Cuánto dinero procesó y cuánto tiempo invirtió.
- El tiempo total (suma de todas las cajeras) vs el tiempo REAL de pared (wall-clock time), demostrando la eficiencia ganada gracias a la concurrencia.
