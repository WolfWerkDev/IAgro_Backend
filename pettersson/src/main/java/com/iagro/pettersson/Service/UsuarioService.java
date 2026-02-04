package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.Finca.InfoFinca;
import com.iagro.pettersson.DTO.Plan.InfoPlan;
import com.iagro.pettersson.DTO.Usuario.ActualizarUsuario;
import com.iagro.pettersson.DTO.Usuario.InformacionUsuario;
import com.iagro.pettersson.DTO.Usuario.RegistroUsuario;
import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Plan;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FileStorageService fileStorageService;

    // ---------------- Métodos de solo lectura ----------------
    @Transactional (readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con id: " + id));
    }

    // Proxy de usuario
    @Transactional(readOnly = true)
    public Usuario getUsuarioReference(Long idUsuario) {
        return entityManager.getReference(Usuario.class, idUsuario);
    }

    @Transactional(readOnly = true)
    public Long obtenerIdUsuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return Long.valueOf(auth.getName());
        }
        throw new RuntimeException("No se pudo obtener el id del usuario");
    }

    @Transactional(readOnly = true)
    public Plan obtenerPlanDelUsuario(Long idUser) {
        Usuario usuario = buscarUsuarioPorId(idUser);
        return usuario.getPlan();
    }

    @Transactional(readOnly = true)
    private boolean validarUsuarioRegistrado(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        return usuario != null;
    }

    // ---------------- Métodos de escritura ----------------
    @Transactional
    public void RegistrarUsuario(RegistroUsuario dto) {
        if (validarUsuarioRegistrado(dto.correo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.nombre())
                .fechaNacimiento(dto.fechaNacimiento())
                .telefono(dto.telefono())
                .correo(dto.correo())
                .contraseña(passwordEncoder.encode(dto.contraseña()))
                .build();

        try {
            usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario");
        }
    }

    @Transactional(readOnly = true)
    public InformacionUsuario obtenerInfoDeUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        Plan userPlan = usuario.getPlan();
        InfoPlan info = new InfoPlan(
                userPlan.getIdPlan(),
                userPlan.getTipoPlan(),
                userPlan.getDescripcion(),
                userPlan.getMaxFincas(),
                userPlan.getMaxAgrolinksPorFinca(),
                userPlan.getMaxMensajesChat(),
                userPlan.getVariablesDisponibles()
        );

        return new InformacionUsuario(usuario.getFotoPerfil(), usuario.getNombre(), usuario.getFechaNacimiento(), usuario.getTelefono(), usuario.getCorreo(), info, usuario.getFechaInicioPlan(), usuario.getFechaFinPlan());
    }

    @Transactional
    public Usuario actualizarInfoUsuario(ActualizarUsuario dto, Long idUser) {
        Usuario usuario = buscarUsuarioPorId(idUser);
        dto.telefono().ifPresent(usuario::setTelefono);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public String cambiarPassword(ActualizarUsuario dto, Long iduser) {
        Usuario usuario = buscarUsuarioPorId(iduser);
        dto.contraseñaAnterior().ifPresent(oldPass -> {
            if (!passwordEncoder.matches(oldPass, usuario.getContraseña())) {
                throw new RuntimeException("La contraseña actual es incorrecta");
            } else {
                usuario.setContraseña(passwordEncoder.encode(dto.nuevaContraseña()));
            }
        });
        usuarioRepository.save(usuario);
        return "Contraseña actualizada correctamente";
    }

    @Transactional
    public String actualizarFotoPerfil(ActualizarUsuario dto, Long idUser) throws IOException {
        Usuario usuario = buscarUsuarioPorId(idUser);
        String fotoUrl = fileStorageService.storeProfileImage(dto.fotoPerfil(), idUser);
        usuario.setFotoPerfil(fotoUrl);
        usuarioRepository.save(usuario);
        return fotoUrl;
    }

    @Transactional
    public String eliminarFotoPerfil(Long idUser) {
        Usuario usuario = buscarUsuarioPorId(idUser);
        fileStorageService.eliminarFotoPerfil(idUser);
        usuario.setFotoPerfil("images/fotoPerfilDefault.png");
        usuarioRepository.save(usuario);
        return usuario.getFotoPerfil();
    }

}
