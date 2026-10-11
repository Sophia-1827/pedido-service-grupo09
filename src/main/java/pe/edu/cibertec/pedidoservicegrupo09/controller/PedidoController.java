package pe.edu.cibertec.pedidoservicegrupo09.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.pedidoservicegrupo09.dto.PedidoResponseDTO;
import pe.edu.cibertec.pedidoservicegrupo09.dto.ResumenConsolidadoClienteDTO;
import pe.edu.cibertec.pedidoservicegrupo09.service.PedidoService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
@RestController
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(idPedido));
    }

    @GetMapping("/verificar-y-completar/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> verificarYCompletar(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.verificarYCompletar(idPedido));
    }

    @GetMapping("/consolidado-cliente/{clienteId}")
    public ResponseEntity<ResumenConsolidadoClienteDTO> consolidarCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.consolidarCliente(clienteId));
    }
}
