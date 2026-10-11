package pe.edu.cibertec.pedidoservicegrupo09.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.cibertec.pedidoservicegrupo09.dto.PedidoResponseDTO;
import pe.edu.cibertec.pedidoservicegrupo09.dto.ResumenConsolidadoClienteDTO;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.iclient.PagoClient;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.model.PagoDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private PagoClient pagoClient;
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pagoClient = mock(PagoClient.class);
        pedidoService = new PedidoService(pagoClient);
        pedidoService.cargarDatosDeEjemplo();
    }

    @Test
    void obtenerPedido_calculaElMontoTotalConLosSubtotales() {
        // Pedido 1: 1 x 2500 + 2 x 50
        PedidoResponseDTO pedido = pedidoService.obtenerPedido(1L);
        assertEquals(2600.00, pedido.getMontoTotalCalculado(), 0.001);
    }

    @Test
    void verificarYCompletar_completaSiElPagoEstaAprobadoYCoincideElMonto() {
        when(pagoClient.obtenerPagoPorPedido(1L)).thenReturn(pago(1L, 2600.00, "APROBADO"));
        assertEquals("COMPLETADO", pedidoService.verificarYCompletar(1L).getEstadoPedido());
    }

    @Test
    void verificarYCompletar_noCompletaSiElMontoNoCoincide() {
        when(pagoClient.obtenerPagoPorPedido(1L)).thenReturn(pago(1L, 1000.00, "APROBADO"));
        assertEquals("PENDIENTE", pedidoService.verificarYCompletar(1L).getEstadoPedido());
    }

    @Test
    void verificarYCompletar_noCompletaSiElPagoFueRechazado() {
        when(pagoClient.obtenerPagoPorPedido(1L)).thenReturn(pago(1L, 2600.00, "RECHAZADO"));
        assertEquals("PENDIENTE", pedidoService.verificarYCompletar(1L).getEstadoPedido());
    }

    @Test
    void consolidarCliente_alDiaSiLosPagosAprobadosCubrenLosPedidos() {
        // Cliente 1: pedidos de 2600 + 700 = 3300
        when(pagoClient.listarPagosPorCliente(1L)).thenReturn(List.of(
                pago(1L, 2600.00, "APROBADO"),
                pago(2L, 700.00, "APROBADO")));

        ResumenConsolidadoClienteDTO resumen = pedidoService.consolidarCliente(1L);

        assertEquals(2, resumen.getTotalPedidosRealizados());
        assertEquals(3300.00, resumen.getMontoTotalAcumulado(), 0.001);
        assertEquals("AL_DIA", resumen.getEstadoCuenta());
    }

    @Test
    void consolidarCliente_conDeudasSiFaltaPagar() {
        when(pagoClient.listarPagosPorCliente(1L)).thenReturn(List.of(
                pago(1L, 2600.00, "APROBADO"),
                pago(2L, 700.00, "RECHAZADO")));

        assertEquals("CON_DEUDAS", pedidoService.consolidarCliente(1L).getEstadoCuenta());
    }

    private PagoDTO pago(Long idPedido, Double monto, String estado) {
        return new PagoDTO(1L, idPedido, "TARJETA_CREDITO", monto, LocalDateTime.now(), "AUT-001", estado);
    }
}
