package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Agrolink.InfoAgrolink;
import com.iagro.pettersson.DTO.Agrolink.VincularAgrolink;
import com.iagro.pettersson.Service.AgrolinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agrolink")
public class AgrolinkController {

    @Autowired
    private AgrolinkService agrolinkService;

    @PostMapping("/vincular")
    public ResponseEntity<?> vincularAgrolink(@Valid @RequestBody VincularAgrolink dto) {
        Long idUser = agrolinkService.obtenerIdUsuario();
        try {
            InfoAgrolink response = agrolinkService.vincularAgrolink(dto, idUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/por-finca/{idFinca}")
    public ResponseEntity<?> obtenerAgrolinksPorFinca(@PathVariable Long idFinca) {
        try {
            List<InfoAgrolink> lista = agrolinkService.obtenerAgrolinksPorFinca(idFinca);
            return ResponseEntity.status(HttpStatus.OK).body(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/editar-agrolink")
    public ResponseEntity<?> actualizarAgrolink(@RequestBody InfoAgrolink dto) {
        try {
            InfoAgrolink info = agrolinkService.actualizarNombreAgrolink(dto);
            return ResponseEntity.status(HttpStatus.OK).body(info);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
