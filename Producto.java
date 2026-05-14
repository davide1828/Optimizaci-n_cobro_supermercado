/**
 * Clase Producto
 * 
 * Representa un artículo disponible en el supermercado.
 * Cada producto tiene un nombre, un precio y un tiempo de escaneo
 * que simula cuántos segundos tarda la cajera en registrarlo.
 * 
 * En la simulación, el tiempo de escaneo se traduce en milisegundos
 * mediante un factor de escala definido en SimulacionSupermercado.
 */
public class Producto {

    // ─── Atributos ────────────────────────────────────────────────────────────

    /** Nombre del producto (p.ej. "Leche", "Pan integral") */
    private final String nombre;

    /** Precio del producto en pesos colombianos */
    private final double precio;

    /**
     * Tiempo base de escaneo en segundos (simulado).
     * Productos grandes o pesados suelen tardar más en procesarse.
     */
    private final int tiempoEscaneoSegundos;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea un nuevo producto con todos sus atributos.
     *
     * @param nombre                Nombre del producto
     * @param precio                Precio unitario en COP
     * @param tiempoEscaneoSegundos Tiempo de escaneo simulado en segundos
     */
    public Producto(String nombre, double precio, int tiempoEscaneoSegundos) {
        this.nombre = nombre;
        this.precio = precio;
        this.tiempoEscaneoSegundos = tiempoEscaneoSegundos;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /** @return Nombre del producto */
    public String getNombre() {
        return nombre;
    }

    /** @return Precio unitario del producto */
    public double getPrecio() {
        return precio;
    }

    /** @return Tiempo de escaneo en segundos (escala de simulación) */
    public int getTiempoEscaneoSegundos() {
        return tiempoEscaneoSegundos;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("%-25s $%,8.0f  (%ds de escaneo)",
                nombre, precio, tiempoEscaneoSegundos);
    }
}
