package com.iagro.pettersson.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads/profiles/");
    private final Path uploadDirFarm = Paths.get("uploads/farms/");

    public FileStorageService() throws IOException {
        // Crear carpeta si no existe
        Files.createDirectories(uploadDir);
        Files.createDirectories(uploadDirFarm);
    }

    /**
     * Guarda la foto del usuario en la carpeta uploads/profiles
     * y la renombra como {idUsuario}.jpg o {idUsuario}.png según corresponda
     *
     * @param file MultipartFile enviado desde el frontend
     * @param idUsuario Id del usuario
     * @return ruta relativa para guardar en la DB
     * @throws IOException si falla la operación de guardado
     */
    public String storeProfileImage(MultipartFile file, Long idUsuario) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se ha enviado ningún archivo");
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (!Arrays.asList("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Formato no permitido. Solo jpg y png");
        }

        // Obtener extensión según el tipo
        String extension = contentType.equals("image/png") ? ".png" : ".jpg";

        // Nombre del archivo: idUsuario.ext
        String fileName = idUsuario + extension;

        // Ruta final
        Path targetLocation = uploadDir.resolve(fileName);

        // Guardar archivo y sobrescribir si existe
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Retornar ruta relativa para la DB
        return "uploads/profiles/" + fileName;
    }

    public String eliminarFotoPerfil(Long idUser) {
        String[] posiblesExtensiones = {".jpg", ".png"};

        for (String ext : posiblesExtensiones) {
            Path filePath = uploadDir.resolve(idUser + ext);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                    return "Foto de perfil eliminada: " + filePath;
                } catch (IOException e) {
                    return "Error al eliminar la foto de perfil: " + e.getMessage();
                }
            }
        }

        return "No se encontró la foto de perfil";
    }

    public String storeFarmImage(MultipartFile file, Long idFinca) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se ha enviado ningún archivo");
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (!Arrays.asList("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Formato no permitido. Solo jpg y png");
        }

        // Obtener extensión según el tipo
        String extension = contentType.equals("image/png") ? ".png" : ".jpg";

        // Nombre del archivo: idUsuario.ext
        String fileName = idFinca + extension;

        // Ruta final
        Path targetLocation = uploadDirFarm.resolve(fileName);

        // Guardar archivo y sobrescribir si existe
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Retornar ruta relativa para la DB
        return "uploads/farms/" + fileName;
    }

    public String eliminarFotoFinca(Long idFinca) {
        String[] posiblesExtensiones = {".jpg", ".png"};

        for (String ext : posiblesExtensiones) {
            Path filePath = uploadDirFarm.resolve(idFinca + ext);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                    return "Foto de finca eliminada: " + filePath;
                } catch (IOException e) {
                    return "Error al eliminar la foto de la finca: " + e.getMessage();
                }
            }
        }

        return "No se encontró ninguna foto para la finca con ID: " + idFinca;
    }


}
