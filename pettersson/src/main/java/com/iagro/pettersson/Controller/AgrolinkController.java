package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Agrolink.VincularAgrolink;
import com.iagro.pettersson.Service.AgrolinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agrolink")
public class AgrolinkController {

    @Autowired
    private AgrolinkService agrolinkService;

    @PostMapping("/vincular")
    public ResponseEntity<?> vincularAgrolink(@Valid @RequestBody VincularAgrolink dto) {
        Long idUser = agrolinkService.obtenerIdUsuario();
        try {
            agrolinkService.vincularAgrolink(dto, idUser);
            return ResponseEntity.status(HttpStatus.OK).body("Agrolink registrado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
