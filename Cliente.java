import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase Cliente
 * 
 * Representa a un comprador en el supermercado.
 * Cada cliente lleva una lista de productos que desea pagar y,
 * una vez procesado, guarda el tiempo total que le tomó su compra.
 * 
 * Esta clase es inmutable respecto al carrito: los productos se agregan
 * antes de la simulación y no se modifican durante ella.
 */
public class Cliente {

    // ─── Atributos ────────────────────────────────────────────────────────────

    /** Identificador único del cliente (p.ej. "Cliente-1") */
    private final String id;

    /** Lista de productos que el cliente desea comprar */
    private final List<Producto> carrito;

    /**
     * Tiempo total que tardó el cobro de este cliente en milisegundos.
     * Se calcula durante la simulación y se almacena aquí para reportes.
     * 
     * Usamos volatile porque este valor puede ser escrito por el hilo
     * de la cajera y leído por el hilo principal al generar el reporte.
     */
    private volatile long tiempoTotalCobro;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea un cliente con su identificador.
     * El carrito comienza vacío; se llenan con {@link #agregarProducto}.
     *
     * @param id Identificador legible del cliente
     */
    public Cliente(String id) {
        this.id = id;
        this.carrito = new ArrayList<>();
        this.tiempoTotalCobro = 0;
    }

    // ─── Métodos de negocio ───────────────────────────────────────────────────

    /**
     * Agrega un producto al carrito del cliente.
     *
     * @param producto Producto a añadir
     */
    public void agregarProducto(Producto producto) {
        carrito.add(producto);
    }

    /**
     * Calcula el costo total de todos los productos en el carrito.
     *
     * @return Suma de los precios de todos los productos
     */
    public double calcularTotal() {
        double total = 0;
        for (Producto p : carrito) {
            total += p.getPrecio();
        }
        return total;
    }

    /**
     * Calcula el tiempo total teórico de escaneo sumando los tiempos
     * individuales de cada producto (en segundos simulados).
     *
     * @return Suma de tiempos de escaneo de todos los productos
     */
    public int calcularTiempoEscaneoTotal() {
        int total = 0;
        for (Producto p : carrito) {
            total += p.getTiempoEscaneoSegundos();
        }
        return total;
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    /** @return Identificador del cliente */
    public String getId() {
        return id;
    }

    /**
     * Retorna una vista no modificable del carrito.
     * Esto protege la lista interna de modificaciones externas.
     *
     * @return Lista de productos (solo lectura)
     */
    public List<Producto> getCarrito() {
        return Collections.unmodifiableList(carrito);
    }

    /** @return Tiempo real que tardó el cobro (en ms), 0 si aún no se procesó */
    public long getTiempoTotalCobro() {
        return tiempoTotalCobro;
    }

    /**
     * Establece el tiempo real de cobro una vez que la cajera termina.
     * Solo la cajera asignada debe llamar este método.
     *
     * @param tiempoTotalCobro Tiempo en milisegundos
     */
    public void setTiempoTotalCobro(long tiempoTotalCobro) {
        this.tiempoTotalCobro = tiempoTotalCobro;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("%s [%d producto(s), total: $%,.0f]",
                id, carrito.size(), calcularTotal());
    }
}
