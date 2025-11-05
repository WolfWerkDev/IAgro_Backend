package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.SuperUsuario.RegistroSuperUsuario;
import com.iagro.pettersson.DTO.SuperUsuario.SuperActualizarUsuario;
import com.iagro.pettersson.DTO.SuperUsuario.SuperModificarPlan;
import com.iagro.pettersson.DTO.SuperUsuario.SuperRegistroAgrolink;
import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Plan;
import com.iagro.pettersson.Entity.SuperUsuario;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Repository.AgrolinkRepository;
import com.iagro.pettersson.Repository.PlanRepository;
import com.iagro.pettersson.Repository.SuperUsuarioRepository;
import com.iagro.pettersson.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuperUsuarioService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SuperUsuarioRepository superUsuarioRepository;

    @Autowired
    private AgrolinkRepository agrolinkRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    // ---------------- Métodos de validación ----------------
    @Transactional(readOnly = true)
    public void validarSuperUsuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esSuper = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SUPER_USER"));
        if (!esSuper) throw new RuntimeException("Acceso denegado: requiere permisos de superusuario.");
    }

    @Transactional(readOnly = true)
    private String encriptarContraseña(String contraseña) {
        return passwordEncoder.encode(contraseña);
    }

    @Transactional(readOnly = true)
    private boolean validarUser(String username) {
        SuperUsuario superUsuario = superUsuarioRepository.findByUsername(username);
        return superUsuario != null;
    }

    // ---------------- Métodos de escritura ----------------
    @Transactional
    public void registroCodigoSuperUser(RegistroSuperUsuario dto) {
        if (validarUser(dto.username())) throw new IllegalArgumentException("El superUsuario ya está registrado");

        if ("Desarrollo".equals(dto.caso())) {
            SuperUsuario superUsuario = SuperUsuario.builder()
                    .username(dto.username())
                    .password(encriptarContraseña(dto.contraseña()))
                    .codigo(encriptarContraseña(dto.codigo()))
                    .build();
            try { superUsuarioRepository.save(superUsuario); }
            catch (Exception e) { throw new RuntimeException("Error al registrar superUsuario en desarrollo"); }
        } else {
            SuperUsuario superUsuarioReferencia = superUsuarioRepository.findById(1L)
                    .orElseThrow(() -> new IllegalStateException("Registro de desarrollo no encontrado"));
            if (passwordEncoder.matches(dto.codigo(), superUsuarioReferencia.getCodigo())) {
                SuperUsuario nuevoSuperUsuario = SuperUsuario.builder()
                        .username(dto.username())
                        .password(encriptarContraseña(dto.contraseña()))
                        .build();
                try { superUsuarioRepository.save(nuevoSuperUsuario); }
                catch (Exception e) { throw new RuntimeException("Error al registrar nuevo superUsuario"); }
            } else throw new IllegalArgumentException("El código de autorización no coincide");
        }
    }

    // ---------------- Métodos de lectura ----------------
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con id" + id + " no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario modificarUsuario(SuperActualizarUsuario dto) {
        Usuario usuario = buscarUsuarioPorId(dto.idUsuario());
        dto.esActivo().ifPresent(usuario::setEsActivo);
        dto.idPlan().ifPresent(planId -> {
            Plan plan = buscarPlanPorId(planId);
            usuario.setPlan(plan);
        });
        dto.fechaInicioPlan().ifPresent(fechaInicio -> {
            usuario.setFechaInicioPlan(fechaInicio);
            usuario.setFechaFinPlan(fechaInicio.plusMonths(1));
        });
        return usuarioRepository.save(usuario);
    }

    // ---------------- Agrolinks ----------------
    @Transactional(readOnly = true)
    public List<Agrolink> obtenerAgrolinks() {
        return agrolinkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agrolink obtenerAgrolinkPorCodigo(String codigo) {
        return agrolinkRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("No se encontró Agrolink con código: " + codigo));
    }

    @Transactional
    public Agrolink registrarAgrolink(SuperRegistroAgrolink dto) {
        Agrolink agrolink = Agrolink.builder()
                .codigo(dto.codigo())
                .build();
        return agrolinkRepository.save(agrolink);
    }

    // ---------------- Planes ----------------
    @Transactional(readOnly = true)
    public List<Plan> obtenerPlanes() {
        return planRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Plan buscarPlanPorId(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró en plan con id: " + id));
    }

    @Transactional
    public Plan modificarInfoPLan(SuperModificarPlan dto) {
        Plan plan = buscarPlanPorId(dto.idPlan());
        dto.descripcion().ifPresent(plan::setDescripcion);
        dto.precioMensual().ifPresent(plan::setPrecioMensual);
        dto.maxFincas().ifPresent(plan::setMaxFincas);
        dto.maxAgrolinksPorFinca().ifPresent(plan::setMaxAgrolinksPorFinca);
        dto.maxMensajesPorChat().ifPresent(plan::setMaxMensajesChat);
        dto.variablesDisponibles().ifPresent(plan::setVariablesDisponibles);
        dto.esActivo().ifPresent(plan::setEsActivo);
        return planRepository.save(plan);
    }
}
