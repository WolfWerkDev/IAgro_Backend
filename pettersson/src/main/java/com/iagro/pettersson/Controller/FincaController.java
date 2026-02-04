package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Finca.*;
import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Enum.TipoDeCultivo;
import com.iagro.pettersson.Service.FincaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/fincas")
public class FincaController {

    @Autowired
    private FincaService fincaService;

    @GetMapping("/fincas-usuario")
    public ResponseEntity<?> obtenerFincasDelUsuario() {
        Long idUser = fincaService.obtenerIdUsuario();
        Usuario usuario = fincaService.buscarUsuarioPorId(idUser);

        List<InfoFinca> listaFincas = fincaService.obtenerInfoFincas(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(listaFincas);
    }

    @PostMapping("/registrar-finca")
    public ResponseEntity<?> registrarFinca(@Valid @RequestBody RegistroFinca dto) {
        fincaService.obtenerIdUsuario();
        Long idUser = fincaService.obtenerIdUsuario();

        try {
            fincaService.registrarFinca(idUser, dto);
            FincaCreada dtoResponse = new FincaCreada("Finca creada con éxito");
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PutMapping("/actualizar-finca")
    public ResponseEntity<?> actualizarFinca(@Valid @ModelAttribute ActualizarFinca dto) {
        try {
            InfoFinca respuesta = fincaService.actualizarFinca(dto);
            return ResponseEntity.status(HttpStatus.OK).body(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la finca. " + e.getMessage());
        }
    }

    /*
    @PutMapping("/cambiar-foto")
    public ResponseEntity<?> cambiarFotoFinca(@ModelAttribute ActualizarFinca dto) {
        try {
            String nuevaUrl = fincaService.cambiarFotoFinca(dto, dto.id());
            return ResponseEntity.status(HttpStatus.OK).body(nuevaUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }*/

    @DeleteMapping("/eliminar-foto/{idFinca}")
    public ResponseEntity<?> eliminarFotoFinca(@PathVariable Long idFinca) {
        try {
            String response = fincaService.eliminarFotoFinca(idFinca);
            return ResponseEntity.status(HttpStatus.OK).body(new FotoEliminada(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    // NO OLVIDAR EXPONER ENDPOINT CON EL ENUM TIPODECULTIVO
    @GetMapping("/cultivos")
    public ResponseEntity<?> obtenerCultivos() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(TipoDeCultivo.values());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    // En cada endpoint donde se necesite idUser se extrae del token JWT
}
