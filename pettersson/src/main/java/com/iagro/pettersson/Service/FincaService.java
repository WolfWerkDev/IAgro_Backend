package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.Finca.ActualizarFinca;
import com.iagro.pettersson.DTO.Finca.RegistroFinca;
import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Repository.FincaRepository;
import com.iagro.pettersson.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FincaService {

    @Autowired
    private FincaRepository fincaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // ---------------- Lectura ----------------
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El usuario con id " + id + " no fué encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Finca> fincasDelUsuario(Usuario user) {
        return fincaRepository.findByUsuario(user);
    }

    @Transactional(readOnly = true)
    private int numeroFincasUsuario(Usuario user) {
        return fincaRepository.findByUsuario(user).size();
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
    public Finca buscarFincaPorId(Long idFinca) {
        return fincaRepository.findById(idFinca)
                .orElseThrow(() -> new RuntimeException("No se encontró la finca con el id: " + idFinca));
    }

    @Transactional(readOnly = true)
    public List<String> agrolinksPorFinca(Long idFinca) {
        Finca finca = fincaRepository.findById(idFinca)
                .orElseThrow(() -> new RuntimeException("No se encontró la finca"));
        return finca.getAgrolinks().stream()
                .map(Agrolink::getCodigo)
                .collect(Collectors.toList());
    }

    // ---------------- Escritura ----------------
    @Transactional
    public void registrarFinca(Long idUser, RegistroFinca dto) {
        Usuario usuario = buscarUsuarioPorId(idUser);
        int maxFincas = usuario.getPlan().getMaxFincas();
        int numeroFincas = numeroFincasUsuario(usuario);

        if (numeroFincas < maxFincas || maxFincas == -1) {
            Finca finca = Finca.builder()
                    .nombreFinca(dto.nombreFinca())
                    .ubicacion(dto.ubicacion())
                    .fotoFinca(asignarFotoAleatoria(null, "Registro"))
                    .tiposDeCultivo(dto.tiposDeCultivo())
                    .usuario(usuario)
                    .build();

            fincaRepository.save(finca);
        } else {
            throw new RuntimeException("El usuario ha alcanzado el número máximo de fincas permitido por su plan.");
        }
    }

    @Transactional
    public void actualizarFinca(ActualizarFinca dto) {
        Finca finca = buscarFincaPorId(dto.id());

        dto.nombre().ifPresent(finca::setNombreFinca);
        dto.tipoDeCultivo().ifPresent(tipos -> {
            finca.getTiposDeCultivo().clear();
            finca.getTiposDeCultivo().addAll(tipos);
        });

        fincaRepository.save(finca);
    }

    @Transactional
    public String cambiarFotoFinca(ActualizarFinca dto, Long idFinca) throws IOException {
        Finca finca = buscarFincaPorId(idFinca);
        String urlFoto = fileStorageService.storeFarmImage(dto.fotoFinca(), idFinca);
        finca.setFotoFinca(urlFoto);
        fincaRepository.save(finca);
        return urlFoto;
    }

    @Transactional
    public String eliminarFotoFinca(Long idFinca) {
        Finca finca = buscarFincaPorId(idFinca);
        fileStorageService.eliminarFotoFinca(idFinca);
        asignarFotoAleatoria(idFinca, "Eliminar");
        fincaRepository.save(finca);
        return finca.getFotoFinca();
    }

    private static final int NUM_FOTOS = 4;
    // Método para asignar foto aleatoria solo al crear
    public String asignarFotoAleatoria(Long idFinca, String caso) {
        int numero = (int) (Math.random() * NUM_FOTOS) + 1;
        if ("Eliminar".equals(caso)) {
            Finca finca = buscarFincaPorId(idFinca);
            finca.setFotoFinca("images/farms/" + numero + ".png");
            fincaRepository.save(finca);
            return finca.getFotoFinca();
        } else {
            return "images/farms/" + numero + ".png";
        }

    }
}
