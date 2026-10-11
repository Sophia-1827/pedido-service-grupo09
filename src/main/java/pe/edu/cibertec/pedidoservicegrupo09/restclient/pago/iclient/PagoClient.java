package pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.iclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.model.PagoDTO;

import java.util.List;

// name = spring.application.name de pago-service: Feign lo busca en Eureka
@FeignClient(name = "pago-service-grupo09")
public interface PagoClient {

    @GetMapping("/api/v1/pagos/pedido/{idPedido}")
    PagoDTO obtenerPagoPorPedido(@PathVariable("idPedido") Long idPedido);

    @GetMapping("/api/v1/pagos/cliente/{clienteId}")
    List<PagoDTO> listarPagosPorCliente(@PathVariable("clienteId") Long clienteId);
}
