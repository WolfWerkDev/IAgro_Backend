package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Finca.ActualizarFinca;
import com.iagro.pettersson.DTO.Finca.FincasPorUsuario;
import com.iagro.pettersson.DTO.Finca.RegistroFinca;
import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Service.FincaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("fincas")
public class FincaController {

    @Autowired
    private FincaService fincaService;

    @GetMapping("/fincas-usuario")
    public ResponseEntity<FincasPorUsuario> obtenerFincasDelUsuario() {
        Long idUser = fincaService.obtenerIdUsuario();
        Usuario usuario = fincaService.buscarUsuarioPorId(idUser);

        List<Finca> listaFincas = fincaService.fincasDelUsuario(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(new FincasPorUsuario(listaFincas));
    }

    @PostMapping("/registrar-finca")
    public ResponseEntity<?> registrarFinca(@Valid @RequestBody RegistroFinca dto) {
        fincaService.obtenerIdUsuario();
        Long idUser = fincaService.obtenerIdUsuario();

        try {
            fincaService.registrarFinca(idUser, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Finca creada con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PutMapping("/actualizar-finca")
    public ResponseEntity<?> actualizarFinca(@Valid @RequestBody ActualizarFinca dto) {
        try {
            fincaService.actualizarFinca(dto);
            return ResponseEntity.status(HttpStatus.OK).body("Finca actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la finca. " + e.getMessage());
        }
    }

    @PutMapping("/cambiar-foto")
    public ResponseEntity<?> cambiarFotoFinca(@ModelAttribute ActualizarFinca dto) {
        try {
            String nuevaUrl = fincaService.cambiarFotoFinca(dto, dto.id());
            return ResponseEntity.status(HttpStatus.OK).body(nuevaUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar-foto/{idFinca}")
    public ResponseEntity<?> eliminarFotoFinca(@RequestParam Long idFinca) {
        try {
            String response = fincaService.eliminarFotoFinca(idFinca);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    // NO OLVIDAR EXPONER ENDPOINT CON EL ENUM TIPODECULTIVO
    // En cada endpoint donde se necesite idUser se extrae del token JWT
}
