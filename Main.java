import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

/**
 * ShopNest - Tarea 2 (Java)
 * Librerías usadas:
 *  - java.util (List, ArrayList, Optional)
 *  - java.time (LocalDateTime, DateTimeFormatter)
 *  - java.net.http (HttpClient, HttpRequest, HttpResponse)
 */
public class Main {

    // ====== Clase Producto ======
    static class Producto {
        private final String id;
        private final String nombre;
        private final double precio;

        public Producto(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }

        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }

        @Override
        public String toString() {
            return nombre + " ($" + precio + ")";
        }
    }

    // ====== Clase Carrito ======
    static class Carrito {
        private final List<Producto> items = new ArrayList<>();

        public void agregar(Producto p) { items.add(p); }

        public double total() {
            double t = 0.0;
            for (Producto p : items) t += p.getPrecio();
            return t;
        }

        public List<Producto> getItems() { return items; }
    }

    // ====== Clase Pedido ======
    static class Pedido {
        private final List<Producto> productos;
        private final LocalDateTime creadoEn;
        private final double total;

        public Pedido(List<Producto> productos) {
            this.productos = productos;
            this.creadoEn = LocalDateTime.now();
            this.total = productos.stream().mapToDouble(Producto::getPrecio).sum();
        }

        public String resumen() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            sb.append("=== RESUMEN PEDIDO ShopNest ===\n");
            sb.append("Fecha/Hora: ").append(creadoEn.format(fmt)).append("\n");
            sb.append("Productos:\n");
            for (Producto p : productos) {
                sb.append(" - ").append(p.getNombre()).append(" $").append(p.getPrecio()).append("\n");
            }
            sb.append("TOTAL: $").append(total).append("\n");
            return sb.toString();
        }
    }

    // ====== Clase ApiCliente (simula integración con API externa) ======
    static class ApiCliente {
        private final HttpClient client = HttpClient.newHttpClient();

        public String obtenerProductoEjemplo() throws Exception {
            // Endpoint público de ejemplo
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://dummyjson.com/products/1"))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return "HTTP " + resp.statusCode() + " | Body (primeros 120 chars): " +
                    resp.body().substring(0, Math.min(120, resp.body().length())) + "...";
        }
    }

    // ====== Método principal ======
    public static void main(String[] args) {
        // 1️⃣ java.util: catálogo y carrito
        List<Producto> catalogo = new ArrayList<>();
        catalogo.add(new Producto("P001", "Pulsera artesanal", 120.0));
        catalogo.add(new Producto("P002", "Playera local", 250.0));
        catalogo.add(new Producto("P003", "Taza ShopNest", 99.0));

        // Buscar producto
        Optional<Producto> buscado = catalogo.stream()
                .filter(p -> p.getId().equals("P002"))
                .findFirst();

        // Crear carrito
        Carrito carrito = new Carrito();
        buscado.ifPresent(carrito::agregar);
        carrito.agregar(catalogo.get(0));

        System.out.println("Carrito actual: " + carrito.getItems());
        System.out.println("Total carrito: $" + carrito.total());

        // 2️⃣ java.time: crear pedido con fecha/hora
        Pedido pedido = new Pedido(carrito.getItems());
        System.out.println(pedido.resumen());

        // 3️⃣ java.net.http: consumir API externa
        try {
            ApiCliente api = new ApiCliente();
            String resultado = api.obtenerProductoEjemplo();
            System.out.println("Integración API (demo): " + resultado);
        } catch (Exception e) {
            System.out.println("No se pudo llamar a la API (posible falta de red). Detalle: " + e.getMessage());
        }
    }
}
