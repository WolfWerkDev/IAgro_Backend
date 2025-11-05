package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.SuperUsuario.*;
import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Plan;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Service.SuperUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class SuperUsuarioController {

    @Autowired
    private SuperUsuarioService superUsuarioService;

    @GetMapping("/obtener-usuarios")
    public ResponseEntity<?> obtenerUsuarios(){
        // Validar superUser con el token
        superUsuarioService.validarSuperUsuario();

        List<Usuario> lista = superUsuarioService.obtenerUsuarios();
        return ResponseEntity.ok(new SuperListaUsuarios(lista));
    }

    @GetMapping("/obtener-agrolinks")
    public ResponseEntity<?> obtenerAgrolinks(){
        superUsuarioService.validarSuperUsuario();

        List<Agrolink> listaAgrolinks = superUsuarioService.obtenerAgrolinks();
        return ResponseEntity.status(HttpStatus.OK).body(new SuperListaAgrolink(listaAgrolinks));
    }

    @GetMapping("/obtener-planes")
    public ResponseEntity<?> obtenerPlanes() {
        superUsuarioService.validarSuperUsuario();

        List<Plan> listaPlanes = superUsuarioService.obtenerPlanes();
        return ResponseEntity.status(HttpStatus.OK).body(new PlanesRegistrados(listaPlanes));
    }

    @PostMapping("/registro-superUser")
    public ResponseEntity<String> registroSuperUsuario(@Valid @RequestBody RegistroSuperUsuario dto) {
        try {
            superUsuarioService.registroCodigoSuperUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/modificar-usuario")
    public ResponseEntity<?> modificarUsuario(@Valid @RequestBody SuperActualizarUsuario dto) {
        superUsuarioService.validarSuperUsuario();

        Usuario usuario = superUsuarioService.modificarUsuario(dto);
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    @PostMapping("/registrar-agrolink")
    public ResponseEntity<?> registrarAgrolink(@Valid @RequestBody SuperRegistroAgrolink dto) {
        superUsuarioService.validarSuperUsuario();

        Agrolink agrolink = superUsuarioService.registrarAgrolink(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AgrolinkRegistrado(agrolink));
    }

    @PostMapping("/modificar-plan")
    public ResponseEntity<?> modificarPlan(@Valid @RequestBody SuperModificarPlan dto) {
        superUsuarioService.validarSuperUsuario();

        Plan plan = superUsuarioService.modificarInfoPLan(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new PlanModificado(plan));
    }
}
