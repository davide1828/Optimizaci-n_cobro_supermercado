import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Clase SimulacionSupermercado
 * 
 * Orquestador principal de la simulación.
 * Se encarga de:
 *   1. Crear el catálogo de productos disponibles.
 *   2. Generar clientes con carritos aleatorios.
 *   3. Distribuir los clientes entre las cajeras.
 *   4. Lanzar todos los hilos simultáneamente.
 *   5. Esperar a que terminen y mostrar el reporte final.
 * 
 * ──────────────────────────────────────────────────────────────────
 * Mecanismo de concurrencia utilizado:
 * 
 *   Thread + thread.join()
 *   ├── Cada Cajera se envuelve en un objeto Thread.
 *   ├── thread.start() lanza el hilo en paralelo (no bloqueante).
 *   └── thread.join() hace que el hilo principal ESPERE a que
 *       ese hilo termine antes de continuar con el reporte.
 * 
 * Resultado: las cajeras cobran en paralelo, pero el reporte
 * final solo se imprime cuando TODAS han terminado.
 * ──────────────────────────────────────────────────────────────────
 */
public class SimulacionSupermercado {

    // ─── Constantes de configuración ──────────────────────────────────────────

    /** Número de cajeras abiertas en la simulación */
    private static final int NUM_CAJERAS = 3;

    /** Número de clientes que entran al supermercado */
    private static final int NUM_CLIENTES = 6;

    /** Mínimo de productos que puede llevar un cliente */
    private static final int MIN_PRODUCTOS_POR_CLIENTE = 2;

    /** Máximo de productos que puede llevar un cliente */
    private static final int MAX_PRODUCTOS_POR_CLIENTE = 5;

    // ─── Atributos de estado ──────────────────────────────────────────────────

    /** Catálogo completo de productos disponibles en el supermercado */
    private final List<Producto> catalogo;

    /** Lista de todos los clientes generados para la simulación */
    private final List<Cliente> todosLosClientes;

    /** Lista de cajeras que atenderán en esta sesión */
    private final List<Cajera> cajeras;

    /** Lista de hilos, uno por cajera */
    private final List<Thread> hilos;

    /** Generador de números aleatorios para asignar productos */
    private final Random random;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public SimulacionSupermercado() {
        this.catalogo = new ArrayList<>();
        this.todosLosClientes = new ArrayList<>();
        this.cajeras = new ArrayList<>();
        this.hilos = new ArrayList<>();
        this.random = new Random(42); // Semilla fija para resultados reproducibles
    }

    // ─── Métodos de configuración ─────────────────────────────────────────────

    /**
     * Inicializa el catálogo de productos del supermercado.
     * Cada producto tiene: nombre, precio (COP) y tiempo de escaneo (segundos sim.).
     * El tiempo de escaneo simula la dificultad de registrar ese artículo.
     */
    private void inicializarCatalogo() {
        // Nombre                         Precio     T.Escaneo (seg sim.)
        catalogo.add(new Producto("Leche entera 1L",      3_500,  1));
        catalogo.add(new Producto("Pan tajado integral",  4_200,  1));
        catalogo.add(new Producto("Arroz 5kg",           18_900,  3));
        catalogo.add(new Producto("Aceite vegetal 1L",    9_800,  2));
        catalogo.add(new Producto("Azúcar 2kg",           7_500,  2));
        catalogo.add(new Producto("Huevos x12",          11_000,  2));
        catalogo.add(new Producto("Jabón de baño",        3_200,  1));
        catalogo.add(new Producto("Shampoo 400ml",       14_500,  2));
        catalogo.add(new Producto("Pasta dental",         6_300,  1));
        catalogo.add(new Producto("Papel higiénico x4",   8_900,  1));
        catalogo.add(new Producto("Pollo entero",        22_000,  4));
        catalogo.add(new Producto("Carne molida 500g",   16_000,  3));
        catalogo.add(new Producto("Manzanas x6",          9_500,  3));
        catalogo.add(new Producto("Bananos x10",          4_800,  2));
        catalogo.add(new Producto("Detergente 2kg",      18_000,  2));
        catalogo.add(new Producto("Gaseosa 2L",           6_500,  1));
        catalogo.add(new Producto("Café molido 250g",    12_000,  2));
        catalogo.add(new Producto("Galletas x3 paquetes", 8_200,  2));
    }

    /**
     * Genera los clientes y les asigna carritos aleatorios del catálogo.
     * 
     * Cada cliente recibe entre MIN y MAX productos escogidos al azar,
     * permitiendo repetición (un cliente puede llevar 2 leches, etc.).
     */
    private void generarClientes() {
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            Cliente cliente = new Cliente("Cliente-" + i);

            // Decidir cuántos productos lleva este cliente
            int numProductos = MIN_PRODUCTOS_POR_CLIENTE +
                    random.nextInt(MAX_PRODUCTOS_POR_CLIENTE - MIN_PRODUCTOS_POR_CLIENTE + 1);

            // Escoger productos aleatorios del catálogo
            for (int j = 0; j < numProductos; j++) {
                Producto productoAleatorio = catalogo.get(random.nextInt(catalogo.size()));
                cliente.agregarProducto(productoAleatorio);
            }

            todosLosClientes.add(cliente);
        }
    }

    /**
     * Crea las cajeras y distribuye los clientes entre ellas.
     * 
     * Estrategia de distribución: round-robin (alternancia cíclica).
     * El Cliente-1 va a Cajera-1, Cliente-2 a Cajera-2, Cliente-3 a Cajera-3,
     * Cliente-4 vuelve a Cajera-1, y así sucesivamente.
     * 
     * Esto balancea la carga de manera uniforme entre las cajeras.
     */
    private void distribuirClientesEntreCajeras() {
        // Crear una cola de clientes por cada cajera
        List<Queue<Cliente>> colasPorCajera = new ArrayList<>();
        for (int i = 0; i < NUM_CAJERAS; i++) {
            colasPorCajera.add(new LinkedList<>());
        }

        // Asignar clientes en round-robin
        for (int i = 0; i < todosLosClientes.size(); i++) {
            int cajeraAsignada = i % NUM_CAJERAS;
            colasPorCajera.get(cajeraAsignada).add(todosLosClientes.get(i));
        }

        // Crear las cajeras con sus respectivas colas
        for (int i = 0; i < NUM_CAJERAS; i++) {
            Cajera cajera = new Cajera("Cajera-" + (i + 1), colasPorCajera.get(i));
            cajeras.add(cajera);
        }
    }

    // ─── Métodos de ejecución ─────────────────────────────────────────────────

    /**
     * Lanza todos los hilos de cajeras simultáneamente.
     * 
     * thread.start() → El hilo entra al planificador del S.O.
     * Cada hilo ejecutará el método run() de su Cajera en paralelo.
     */
    private void lanzarHilos() {
        for (Cajera cajera : cajeras) {
            Thread hilo = new Thread(cajera);
            // Nombre descriptivo para depuración (visible en herramientas como jstack)
            hilo.setName("Hilo-" + cajera.getNombre());
            hilos.add(hilo);
        }

        System.out.println("Lanzando " + NUM_CAJERAS +
                " cajeras en paralelo para atender " + NUM_CLIENTES + " clientes...");

        // Iniciar TODOS los hilos antes de hacer join (para verdadera concurrencia)
        for (Thread hilo : hilos) {
            hilo.start();
        }
    }

    /**
     * Espera a que todos los hilos terminen.
     * 
     * thread.join() bloquea el hilo principal (main) hasta que el hilo
     * en cuestión termine su ejecución. Llamamos join() para cada hilo
     * para garantizar que el reporte final se imprima con TODOS los datos.
     */
    private void esperarFinalizacion() {
        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Hilo principal interrumpido: " + e.getMessage());
            }
        }
    }

    // ─── Reporte final ────────────────────────────────────────────────────────

    /**
     * Imprime el reporte consolidado de la simulación.
     * 
     * Estructura del reporte:
     *   1. Una sección por cada cajera:
     *      - Clientes que atendió, productos y costo de cada uno
     *      - Tiempo individual de cada cliente
     *      - Tiempo total de esa cajera
     *   2. Sección de totales globales (suma de todas las cajeras)
     * 
     * Este método se llama DESPUÉS de que todos los hilos terminaron (join),
     * por eso los datos de tiempos ya están completos y son seguros de leer.
     */
    private void imprimirReporteFinal() {
        System.out.println("\n\n");
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  REPORTE FINAL DE LA SIMULACIÓN                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");

        long tiempoGlobalTotal = 0;
        double costoGlobalTotal = 0;
        int clientesGlobalTotal = 0;

        // ── Sección por cajera ──────────────────────────────────────────────
        for (Cajera cajera : cajeras) {
            List<Cliente> clientesDeCajera = cajera.getClientesProcesados();

            System.out.println();
            System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
            System.out.printf ("  │  %-59s│%n", cajera.getNombre());
            System.out.println("  ├──────────────────────┬────────────┬──────────┬──────────────┤");
            System.out.printf ("  │  %-20s  │  %-9s │ %-8s │  %-11s │%n",
                    "Cliente", "Productos", "Costo", "Tiempo");
            System.out.println("  ├──────────────────────┼────────────┼──────────┼──────────────┤");

            double costoCajera = 0;

            for (Cliente cliente : clientesDeCajera) {
                System.out.printf("  │  %-20s  │  %-9d │ $%,6.0f │  %,7d ms  │%n",
                        cliente.getId(),
                        cliente.getCarrito().size(),
                        cliente.calcularTotal(),
                        cliente.getTiempoTotalCobro());
                costoCajera += cliente.calcularTotal();
            }

            System.out.println("  ├──────────────────────┴────────────┴──────────┴──────────────┤");
            System.out.printf ("  │  %-20s   Clientes: %-3d   Ventas: $%,7.0f             │%n",
                    "Subtotal " + cajera.getNombre(),
                    clientesDeCajera.size(),
                    costoCajera);
            System.out.printf ("  │     Tiempo total de %s: %,d ms  (%.2f seg)%s│%n",
                    cajera.getNombre(),
                    cajera.getTiempoTotalCajera(),
                    cajera.getTiempoTotalCajera() / 1000.0,
                    " ".repeat(Math.max(0, 19 - String.valueOf(cajera.getTiempoTotalCajera()).length())));
            System.out.println("  └─────────────────────────────────────────────────────────────┘");

            // Acumular para el total global
            tiempoGlobalTotal  += cajera.getTiempoTotalCajera();
            costoGlobalTotal   += costoCajera;
            clientesGlobalTotal += clientesDeCajera.size();
        }

        // ── Totales globales ────────────────────────────────────────────────
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       TOTALES GLOBALES                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║  Cajeras activas:       %-3d                                      ║%n",
                cajeras.size());
        System.out.printf ("║  Clientes atendidos:    %-3d                                      ║%n",
                clientesGlobalTotal);
        System.out.printf ("║  Total en ventas:       $%,10.0f                              ║%n",
                costoGlobalTotal);
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");

        // Desglose de tiempo por cajera para comparación rápida
        System.out.println("║     Tiempo por cajera:                                           ║");
        for (Cajera cajera : cajeras) {
            System.out.printf("║     %-12s  -> %,6d ms  (%.2f seg)                        ║%n",
                    cajera.getNombre(),
                    cajera.getTiempoTotalCajera(),
                    cajera.getTiempoTotalCajera() / 1000.0);
        }

        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║     Tiempo total (suma de todas las cajeras): %,7d ms         ║%n",
                tiempoGlobalTotal);
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
    }

    // ─── Método principal de simulación ──────────────────────────────────────

    /**
     * Ejecuta la simulación completa de principio a fin.
     * Llama a los pasos en orden: configurar → distribuir → ejecutar → reportar.
     */
    public void ejecutar() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║             SIMULACIÓN DE COBRO EN SUPERMERCADO                  ║");
        System.out.println("║         IU Digital de Antioquia — Concurrencia en Java            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");

        // Paso 1: Preparar datos
        System.out.println("\nInicializando catálogo de productos...");
        inicializarCatalogo();
        System.out.println("   " + catalogo.size() + " productos disponibles.");

        // Paso 2: Generar clientes
        System.out.println("\nGenerando clientes con carritos aleatorios...");
        generarClientes();
        for (Cliente c : todosLosClientes) {
            System.out.println("   " + c);
        }

        // Paso 3: Distribuir entre cajeras
        System.out.println("\nAsignando clientes a cajeras (round-robin)...");
        distribuirClientesEntreCajeras();
        for (int i = 0; i < cajeras.size(); i++) {
            System.out.printf("   %s -> atiende %d cliente(s)%n",
                    cajeras.get(i).getNombre(),
                    todosLosClientes.size() / NUM_CAJERAS +
                    (i < todosLosClientes.size() % NUM_CAJERAS ? 1 : 0));
        }

        // Paso 4: Lanzar hilos (concurrencia real)
        System.out.println();
        long inicioGlobal = System.currentTimeMillis();
        lanzarHilos();

        // Paso 5: Esperar que todos terminen
        esperarFinalizacion();
        long tiempoRealTotal = System.currentTimeMillis() - inicioGlobal;

        // Paso 6: Reporte final
        imprimirReporteFinal();

        System.out.printf("%nTiempo REAL de pared (wall-clock): %,d ms (%.2f seg)%n",
                tiempoRealTotal, tiempoRealTotal / 1000.0);
        System.out.println("   (Menor que la suma de tiempos individuales = concurrencia real)");
    }
}
