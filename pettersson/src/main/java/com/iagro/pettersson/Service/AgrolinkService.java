package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.Agrolink.InfoAgrolink;
import com.iagro.pettersson.DTO.Agrolink.VincularAgrolink;
import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Repository.AgrolinkRepository;
import com.iagro.pettersson.Repository.FincaRepository;
import com.iagro.pettersson.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgrolinkService {

    @Autowired
    private AgrolinkRepository agrolinkRepository;

    @Autowired
    private FincaRepository fincaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ---------------- Métodos de solo lectura ----------------
    @Transactional(readOnly = true)
    public Long obtenerIdUsuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return Long.valueOf(auth.getName());
        }
        throw new RuntimeException("No se pudo obtener el id del usuario");
    }

    @Transactional(readOnly = true)
    public Agrolink buscarAgrolinkPorCodigo(String codigo) {
        return agrolinkRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("No existe Agrolink con código: " + codigo + " registrado"));
    }

    @Transactional(readOnly = true)
    public Finca buscarFincaPorId(Long idFinca) {
        return fincaRepository.findById(idFinca)
                .orElseThrow(() -> new RuntimeException("No se encontró la finca con id: " + idFinca));
    }

    @Transactional(readOnly = true)
    public boolean agrolinkEsRegistrado(Agrolink agrolink) {
        return agrolink.isEsVinculado();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(Long idUser) {
        return usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario con id: " + idUser));
    }

    // ---------------- Métodos de escritura ----------------
    @Transactional
    public InfoAgrolink vincularAgrolink(VincularAgrolink dto, Long idUser) {
        Usuario usuario = buscarUsuario(idUser);
        Finca finca = buscarFincaPorId(dto.idFinca());
        String plan = String.valueOf(usuario.getPlan().getTipoPlan());
        int maxAgrolinks = usuario.getPlan().getMaxAgrolinksPorFinca();
        int agrolinksFinca = finca.getAgrolinks().size();

        if (agrolinksFinca < maxAgrolinks || maxAgrolinks == -1) {
            Agrolink agrolink = buscarAgrolinkPorCodigo(dto.codigoAgrolink());
            if (agrolinkEsRegistrado(agrolink)) {
                throw new RuntimeException("El agrolink " + agrolink.getNombreAgrolink() + " ya está vinculado a otro usuario");
            }
            try {
                LocalDateTime fechaVinculacion = LocalDateTime.now();
                agrolink.setFinca(finca);
                agrolink.setEsVinculado(true);
                agrolink.setFechaVinculacion(fechaVinculacion);

                agrolinkRepository.save(agrolink);
                return new InfoAgrolink(agrolink.getCodigo(), agrolink.getNombreAgrolink());
            } catch (Exception e) {
                throw new RuntimeException("Error al vincular Agrolink, " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Ha alcanzado el máximo de agrolinks por finca para su plan. Su plan es: " + plan);
        }
    }

    public List<InfoAgrolink> obtenerAgrolinksPorFinca(Long idFinca) {
        Finca finca = buscarFincaPorId(idFinca);
        List<Agrolink> agrolinks = agrolinkRepository.findByFinca(finca).orElseThrow(() -> new RuntimeException("No hay Agrolinks registrados en esta finca"));

        return agrolinks.stream()
                .map(a -> new InfoAgrolink(
                        a.getCodigo(),
                        a.getNombreAgrolink()
                ))
                .toList();
    }

    public InfoAgrolink actualizarNombreAgrolink(InfoAgrolink dto) {
        Agrolink agrolink = buscarAgrolinkPorCodigo(dto.codigoAgrolink());
        agrolink.setNombreAgrolink(dto.nombre());
        agrolinkRepository.save(agrolink);
        return new InfoAgrolink(agrolink.getCodigo(), agrolink.getNombreAgrolink());
    }
}
