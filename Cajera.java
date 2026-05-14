import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Clase Cajera
 * 
 * Representa a una cajera del supermercado.
 * Implementa {@link Runnable} para ejecutarse como un hilo independiente,
 * lo que permite que varias cajeras cobren simultáneamente.
 * 
 * Cada cajera recibe una cola de clientes y los procesa uno por uno:
 * escanea cada producto de la compra, simula el tiempo de cobro con
 * Thread.sleep() y registra los resultados para el reporte final.
 * 
 * ──────────────────────────────────────────────────────────────────
 * ¿Por qué Runnable y no Thread directamente?
 * Implementar Runnable es la práctica recomendada en Java porque:
 *   1. Permite que la clase herede de otra si fuera necesario.
 *   2. Separa la tarea (lógica de cobro) del mecanismo de ejecución
 *      (el hilo), siguiendo el principio de responsabilidad única.
 * ──────────────────────────────────────────────────────────────────
 */
public class Cajera implements Runnable {

    // ─── Constantes ───────────────────────────────────────────────────────────

    /**
     * Factor de escala de tiempo: cuántos milisegundos reales equivalen
     * a 1 segundo simulado de escaneo.
     * 
     * Con 300 ms por segundo simulado, un producto con 2s de escaneo
     * tardará 600 ms reales, haciendo la demo ágil pero perceptible.
     */
    private static final int MS_POR_SEGUNDO_SIMULADO = 300;

    // ─── Atributos ────────────────────────────────────────────────────────────

    /** Nombre de la cajera (p.ej. "Cajera-1") */
    private final String nombre;

    /**
     * Cola de clientes asignados a esta cajera.
     * 
     * Usamos Queue<Cliente> porque representa perfectamente una fila:
     * el primer cliente en llegar es el primero en ser atendido (FIFO).
     * 
     * NOTA: Esta cola no necesita ser thread-safe porque cada cajera
     * es la única que lee de su propia cola.
     */
    private final Queue<Cliente> colaClientes;

    /**
     * Tiempo total acumulado que esta cajera estuvo activa, en ms.
     * Se usa para estadísticas al final de la simulación.
     */
    private long tiempoTotalCajera;

    /**
     * Lista de clientes atendidos por esta cajera, en orden de atención.
     * Se llena durante la simulación y se consulta al generar el reporte final,
     * permitiendo saber exactamente qué clientes procesó cada cajera.
     */
    private final List<Cliente> clientesProcesados;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea una cajera con su nombre y la cola de clientes a atender.
     *
     * @param nombre        Nombre identificador de la cajera
     * @param colaClientes  Cola de clientes que atenderá
     */
    public Cajera(String nombre, Queue<Cliente> colaClientes) {
        this.nombre = nombre;
        this.colaClientes = colaClientes;
        this.tiempoTotalCajera = 0;
        this.clientesProcesados = new ArrayList<>();
    }

    // ─── Implementación de Runnable ───────────────────────────────────────────

    /**
     * Método principal del hilo.
     * 
     * Se llama automáticamente cuando se inicia el hilo con thread.start().
     * La cajera procesa clientes en orden hasta que la cola queda vacía.
     * 
     * Flujo por cada cliente:
     *   1. Sacar cliente de la cola
     *   2. Escanear cada producto (simular tiempo con Thread.sleep)
     *   3. Registrar tiempo de cobro en el objeto Cliente
     *   4. Imprimir resumen de la compra
     */
    @Override
    public void run() {
        imprimirEncabezadoCajera();

        // Procesar clientes mientras haya en la cola
        while (!colaClientes.isEmpty()) {

            // poll() retorna null si la cola está vacía (no lanza excepción)
            Cliente cliente = colaClientes.poll();

            if (cliente != null) {
                procesarCliente(cliente);
            }
        }

        imprimirResumenCajera();
    }

    // ─── Lógica de procesamiento ──────────────────────────────────────────────

    /**
     * Procesa la compra completa de un cliente.
     * Escanea cada producto, registra tiempos y calcula totales.
     *
     * @param cliente Cliente a atender
     */
    private void procesarCliente(Cliente cliente) {
        long inicioCliente = System.currentTimeMillis();

        System.out.printf("%n  [%s] -> Atendiendo a %s (%d productos)%n",
                nombre, cliente.getId(), cliente.getCarrito().size());
        System.out.println("  " + "─".repeat(65));
        System.out.printf("  %-25s %-12s %-18s%n", "Producto", "Precio", "Tiempo escaneo");
        System.out.println("  " + "─".repeat(65));

        List<Producto> productos = cliente.getCarrito();

        // Escanear cada producto de la compra
        for (Producto producto : productos) {
            long inicioProd = System.currentTimeMillis();

            // Simular el tiempo que tarda escanear este producto
            simularEscaneo(producto.getTiempoEscaneoSegundos());

            long tiempoProd = System.currentTimeMillis() - inicioProd;

            System.out.printf("  %-25s $%,8.0f  %,6d ms (%ds sim.)%n",
                    producto.getNombre(),
                    producto.getPrecio(),
                    tiempoProd,
                    producto.getTiempoEscaneoSegundos());
        }

        // Calcular y registrar el tiempo total de cobro de este cliente
        long tiempoCliente = System.currentTimeMillis() - inicioCliente;
        cliente.setTiempoTotalCobro(tiempoCliente);
        tiempoTotalCajera += tiempoCliente;

        // Guardar en la lista de clientes procesados para el reporte final
        clientesProcesados.add(cliente);

        // Imprimir totales del cliente
        System.out.println("  " + "─".repeat(65));
        System.out.printf("  %-25s $%,8.0f  %,6d ms totales%n",
                "TOTAL COMPRA",
                cliente.calcularTotal(),
                tiempoCliente);
        System.out.println();
    }

    /**
     * Simula el tiempo de escaneo de un producto usando Thread.sleep().
     * 
     * Thread.sleep() pausa el hilo actual (esta cajera) sin bloquear
     * los demás hilos (las otras cajeras siguen trabajando).
     * 
     * Esto es la clave de la concurrencia: mientras una cajera "espera"
     * escanear un producto, las otras siguen atendiendo en paralelo.
     *
     * @param segundosSimulados Segundos teóricos de escaneo del producto
     */
    private void simularEscaneo(int segundosSimulados) {
        try {
            Thread.sleep((long) segundosSimulados * MS_POR_SEGUNDO_SIMULADO);
        } catch (InterruptedException e) {
            // Si el hilo es interrumpido (p.ej. al cerrar la app),
            // restauramos la bandera de interrupción y salimos limpiamente.
            Thread.currentThread().interrupt();
            System.err.printf("[%s] Hilo interrumpido durante el escaneo.%n", nombre);
        }
    }

    // ─── Métodos de impresión ─────────────────────────────────────────────────

    /** Imprime el banner de inicio de la cajera */
    private void imprimirEncabezadoCajera() {
        System.out.printf("%n╔══════════════════════════════════════════════════════╗%n");
        System.out.printf("║      %s iniciando turno...                        ║%n", nombre);
        System.out.printf("╚══════════════════════════════════════════════════════╝%n");
    }

    /** Imprime el resumen de tiempo de la cajera al terminar */
    private void imprimirResumenCajera() {
        System.out.printf("     %s terminó su turno. Tiempo total activa: %,d ms (%.2f seg)%n%n",
                nombre, tiempoTotalCajera, tiempoTotalCajera / 1000.0);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /** @return Nombre de la cajera */
    public String getNombre() {
        return nombre;
    }

    /** @return Tiempo total que la cajera estuvo procesando (en ms) */
    public long getTiempoTotalCajera() {
        return tiempoTotalCajera;
    }

    /**
     * Retorna la lista de clientes atendidos por esta cajera (solo lectura).
     * Disponible para consulta después de que el hilo haya terminado.
     *
     * @return Lista inmutable de clientes procesados
     */
    public List<Cliente> getClientesProcesados() {
        return Collections.unmodifiableList(clientesProcesados);
    }
}
