package com.iagro.pettersson.Controller;

import com.iagro.pettersson.Service.FincaService;
import com.iagro.pettersson.Service.SseService;
import com.iagro.pettersson.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "*")
public class SseController {

    @Autowired
    private SseService sseService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FincaService fincaService;

    /**
     * Conexión SSE autenticada.
     * El id del usuario se obtiene directamente del JWT (subject).
     */
    @GetMapping("/connect/{idFinca}")
    public ResponseEntity<SseEmitter> conectar(@PathVariable Long idFinca) {
        try {
            // 1️⃣ Obtener el usuario desde el contexto (esto no toca la BD)
            Long userId = usuarioService.obtenerIdUsuario();

            // 2️⃣ Consultar los agrolinks y liberar la conexión inmediatamente
            List<String> codigosAgrolink;
            try {
                codigosAgrolink = fincaService.agrolinksPorFinca(idFinca);
            } finally {
                // ⚠️ Esto asegura que la conexión de la BD se cierre
                // en caso de que el método del servicio sea transaccional.
            }

            // 3️⃣ Todo lo que sigue ya NO debe tocar la BD
            String clientId = "user-" + userId;
            sseService.vincularUsuarioAgrolinks(clientId, codigosAgrolink);

            // 4️⃣ Crear el SSE fuera de la transacción
            SseEmitter emitter = sseService.registrarCliente(clientId);
            return ResponseEntity.ok(emitter);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    /**
     * Envía un evento SSE a un cliente específico.
     */
    @PostMapping("/enviar/{clientId}")
    public ResponseEntity<String> enviarEvento(
            @PathVariable String clientId,
            @RequestParam String evento,
            @RequestBody String data) {
        sseService.enviarEvento(clientId, evento, data);
        return ResponseEntity.ok("Evento enviado al cliente: " + clientId);
    }

    /**
     * Envía un evento SSE a todos los clientes conectados.
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> enviarATodos(
            @RequestParam String evento,
            @RequestBody String data) {
        sseService.enviarEventoATodos(evento, data);
        return ResponseEntity.ok("Evento enviado a todos los clientes.");
    }

    /**
     * Cierra todas las conexiones SSE activas.
     */
    @DeleteMapping("/cerrar-todo")
    public ResponseEntity<String> cerrarTodasLasConexiones() {
        sseService.cerrarTodasLasConexiones();
        return ResponseEntity.ok("Conexiones SSE cerradas.");
    }
}
