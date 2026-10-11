package pe.edu.cibertec.pedidoservicegrupo09.service;

import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.pedidoservicegrupo09.dto.DetallePedidoDTO;
import pe.edu.cibertec.pedidoservicegrupo09.dto.PedidoResponseDTO;
import pe.edu.cibertec.pedidoservicegrupo09.dto.ResumenConsolidadoClienteDTO;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.iclient.PagoClient;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.model.PagoDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PedidoService {

    // Tolerancia para comparar montos Double (evita fallar por decimales como 0.1 + 0.2)
    private static final double TOLERANCIA = 0.01;

    private final PagoClient pagoClient;

    // Datos de ejemplo en memoria (no se usa base de datos)
    private final Map<Long, PedidoResponseDTO> pedidos = new HashMap<>();
    private final Map<Long, String> clientes = new HashMap<>();

    @PostConstruct
    public void cargarDatosDeEjemplo() {
        clientes.put(1L, "Juan Perez");
        clientes.put(2L, "Maria Lopez");

        agregarPedido(1L, "TRK-0001", 1L, List.of(
                detalle(101L, "Laptop Lenovo", 1, 2500.00),
                detalle(102L, "Mouse inalambrico", 2, 50.00)));
        agregarPedido(2L, "TRK-0002", 1L, List.of(
                detalle(103L, "Monitor 24 pulgadas", 1, 700.00)));
        agregarPedido(3L, "TRK-0003", 2L, List.of(
                detalle(104L, "Teclado mecanico", 1, 300.00),
                detalle(105L, "Audifonos", 1, 150.00)));
    }

    public PedidoResponseDTO obtenerPedido(Long idPedido) {
        PedidoResponseDTO pedido = pedidos.get(idPedido);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido " + idPedido);
        }
        pedido.setMontoTotalCalculado(calcularMontoTotal(pedido.getListaDetalles()));
        return pedido;
    }

    public PedidoResponseDTO verificarYCompletar(Long idPedido) {
        PedidoResponseDTO pedido = obtenerPedido(idPedido);
        PagoDTO pago = buscarPagoDelPedido(idPedido);

        // Se completa solo si el pago esta APROBADO y cubre exactamente el total del pedido
        if (pago != null
                && "APROBADO".equals(pago.getEstadoPago())
                && montosIguales(pago.getMontoPagado(), pedido.getMontoTotalCalculado())) {
            pedido.setEstadoPedido("COMPLETADO");
        }
        return pedido;
    }

    public ResumenConsolidadoClienteDTO consolidarCliente(Long clienteId) {
        String nombreCliente = clientes.get(clienteId);
        if (nombreCliente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + clienteId);
        }

        List<PedidoResponseDTO> pedidosCliente = new ArrayList<>();
        double montoTotalAcumulado = 0.0;
        for (PedidoResponseDTO pedido : pedidos.values()) {
            if (pedido.getClienteId().equals(clienteId)) {
                PedidoResponseDTO pedidoCalculado = obtenerPedido(pedido.getIdPedido());
                pedidosCliente.add(pedidoCalculado);
                montoTotalAcumulado += pedidoCalculado.getMontoTotalCalculado();
            }
        }

        List<PagoDTO> pagosCliente = buscarPagosDelCliente(clienteId);
        double totalPagadoAprobado = 0.0;
        for (PagoDTO pago : pagosCliente) {
            if ("APROBADO".equals(pago.getEstadoPago())) {
                totalPagadoAprobado += pago.getMontoPagado();
            }
        }

        // Si lo pagado (aprobado) no cubre lo comprado, el cliente tiene deudas
        String estadoCuenta = montoTotalAcumulado - totalPagadoAprobado > TOLERANCIA ? "CON_DEUDAS" : "AL_DIA";

        return new ResumenConsolidadoClienteDTO(
                clienteId,
                nombreCliente,
                pedidosCliente.size(),
                montoTotalAcumulado,
                pedidosCliente,
                pagosCliente,
                estadoCuenta);
    }

    private PagoDTO buscarPagoDelPedido(Long idPedido) {
        try {
            return pagoClient.obtenerPagoPorPedido(idPedido);
        } catch (FeignException.NotFound e) {
            return null; // el pedido todavia no tiene pago
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "pago-service no disponible");
        }
    }

    private List<PagoDTO> buscarPagosDelCliente(Long clienteId) {
        try {
            return pagoClient.listarPagosPorCliente(clienteId);
        } catch (FeignException.NotFound e) {
            return new ArrayList<>(); // el cliente todavia no tiene pagos
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "pago-service no disponible");
        }
    }

    private double calcularMontoTotal(List<DetallePedidoDTO> detalles) {
        double total = 0.0;
        for (DetallePedidoDTO detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }

    private boolean montosIguales(Double montoPagado, Double montoTotal) {
        return montoPagado != null && Math.abs(montoPagado - montoTotal) < TOLERANCIA;
    }

    private void agregarPedido(Long idPedido, String codigo, Long clienteId, List<DetallePedidoDTO> detalles) {
        pedidos.put(idPedido, new PedidoResponseDTO(
                idPedido, codigo, LocalDateTime.now(), clienteId, "PENDIENTE", detalles, null));
    }

    private DetallePedidoDTO detalle(Long idProducto, String nombre, Integer cantidad, Double precioUnitario) {
        return new DetallePedidoDTO(idProducto, nombre, cantidad, precioUnitario, cantidad * precioUnitario);
    }
}
