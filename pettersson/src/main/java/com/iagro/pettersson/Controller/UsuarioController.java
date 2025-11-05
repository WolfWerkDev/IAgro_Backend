package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Usuario.ActualizarUsuario;
import com.iagro.pettersson.DTO.Usuario.InformacionUsuario;
import com.iagro.pettersson.DTO.Usuario.RegistroUsuario;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/obtener-usuario")
    public ResponseEntity<?> obtenerInfoUsuario() {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            InformacionUsuario user = usuarioService.obtenerInfoDeUsuario(idUser);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody RegistroUsuario dto) {
        try {
            usuarioService.RegistrarUsuario(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/actualizar-info")
    public ResponseEntity<?> actualizarInfo(@RequestBody ActualizarUsuario dto) {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            Usuario usuario = usuarioService.actualizarInfoUsuario(dto, idUser);
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/cambiar-contraseña")
    public ResponseEntity<?> cambiarPassword(@RequestBody ActualizarUsuario dto) {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            String response = usuarioService.cambiarPassword(dto, idUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/cambiar-foto")
    public ResponseEntity<?> cambiarFotoPerfil(@ModelAttribute ActualizarUsuario dto) {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            String response = usuarioService.actualizarFotoPerfil(dto, idUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar-foto")
    public ResponseEntity<?> eliminarFotoPerfil() {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            String response = usuarioService.eliminarFotoPerfil(idUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
