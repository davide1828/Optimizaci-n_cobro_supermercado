

/**
 * ═══════════════════════════════════════════════════════════════════
 *  SIMULACIÓN DE COBRO EN SUPERMERCADO — Concurrencia con Hilos Java
 * ═══════════════════════════════════════════════════════════════════
 *
 * Caso de estudio: IU Digital de Antioquia
 * Asignatura: Tecnología en Desarrollo de Software
 *
 * DESCRIPCIÓN DEL SISTEMA
 * ─────────────────────────────────────────────────────────────────
 * Este programa simula el proceso de cobro en cajas registradoras
 * de un supermercado usando concurrencia de hilos (Thread) en Java.
 *
 * El objetivo es demostrar que múltiples cajeras pueden cobrar
 * a sus respectivos clientes en PARALELO, reduciendo el tiempo
 * total de atención en comparación con un sistema secuencial.
 *
 * ESTRUCTURA DEL PROYECTO
 * ─────────────────────────────────────────────────────────────────
 *   Producto.java              → Entidad: artículo con precio y tiempo de escaneo
 *   Cliente.java               → Entidad: comprador con carrito de productos
 *   Cajera.java                → Hilo: procesa la cola de clientes asignada
 *   SimulacionSupermercado.java → Orquestador: configura y ejecuta la simulación
 *   Main.java                  → Punto de entrada del programa
 *
 * CONCEPTOS DE CONCURRENCIA APLICADOS
 * ─────────────────────────────────────────────────────────────────
 *   • Runnable            : interfaz que convierte Cajera en tarea ejecutable
 *   • Thread              : envuelve cada Cajera para ejecución en paralelo
 *   • thread.start()      : lanza el hilo al planificador del S.O.
 *   • Thread.sleep()      : simula tiempo de escaneo sin bloquear otros hilos
 *   • thread.join()       : sincroniza el hilo principal con los hilos hijos
 *   • volatile            : garantiza visibilidad del tiempo de cobro entre hilos
 *
 * CÓMO EJECUTAR (línea de comandos)
 * ─────────────────────────────────────────────────────────────────
 *   javac *.java      → Compilar todas las clases
 *   java Main         → Ejecutar la simulación
 */
public class Main {

    /**
     * Punto de entrada del programa.
     * Crea la simulación y la inicia.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) throws Exception {

        // Crear el orquestador de la simulación
        SimulacionSupermercado simulacion = new SimulacionSupermercado();

        // Ejecutar: inicializar → generar datos → lanzar hilos → reportar
        simulacion.ejecutar();
    }
}
