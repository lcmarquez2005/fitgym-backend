package org.example.fitgymbackend.service.imp;

import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.entity.Socio;
import org.example.fitgymbackend.model.request.SocioRequest;
import org.example.fitgymbackend.model.response.SocioResponse;
import org.example.fitgymbackend.repository.ISocioRepository;
import org.example.fitgymbackend.service.ISocioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocioServiceImp implements ISocioService {

    private final ISocioRepository socioRepository;

    @Override
    public List<SocioResponse> buscar(String q) {
        return socioRepository.buscar(q)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SocioResponse registrar(SocioRequest request) {
        Socio socio = toEntity(request);
        Socio guardado = socioRepository.save(socio);
        return toResponse(guardado);
    }

    @Override
    public SocioResponse actualizar(Long id, SocioRequest request) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + id));
        socio.setNombreCompleto(request.getNombreCompleto());
        socio.setTelefono(request.getTelefono());
        socio.setEmail(request.getEmail());
        socio.setFechaNacimiento(request.getFechaNacimiento());
        socio.setSexo(request.getSexo());
        socio.setContactoEmergencia(request.getContactoEmergencia());
        socio.setTelefonoEmergencia(request.getTelefonoEmergencia());
        socio.setIdSocio(request.getIdSocio());
        socio.setFechaRegistro(request.getFechaRegistro());
        socio.setEstatus(request.getEstatus());
        socio.setTipoMembresia(request.getTipoMembresia());
        socio.setDescuento(request.getDescuento());
        socio.setCostoMensual(request.getCostoMensual());
        socio.setFechaInicio(request.getFechaInicio());
        socio.setFechaFin(request.getFechaFin());
        socio.setLesiones(request.getLesiones());
        socio.setAlergias(request.getAlergias());
        socio.setExtras(request.getExtras());
        socio.setFoto(request.getFoto());
        return toResponse(socioRepository.save(socio));
    }

    @Override
    public void eliminar(Long id) {
        if (!socioRepository.existsById(id)) {
            throw new RuntimeException("Socio no encontrado con id: " + id);
        }
        socioRepository.deleteById(id);
    }

    private Socio toEntity(SocioRequest r) {
        Socio s = new Socio();
        s.setNombreCompleto(r.getNombreCompleto());
        s.setTelefono(r.getTelefono());
        s.setEmail(r.getEmail());
        s.setFechaNacimiento(r.getFechaNacimiento());
        s.setSexo(r.getSexo());
        s.setContactoEmergencia(r.getContactoEmergencia());
        s.setTelefonoEmergencia(r.getTelefonoEmergencia());
        s.setIdSocio(r.getIdSocio());
        s.setFechaRegistro(r.getFechaRegistro());
        s.setEstatus(r.getEstatus());
        s.setTipoMembresia(r.getTipoMembresia());
        s.setDescuento(r.getDescuento());
        s.setCostoMensual(r.getCostoMensual());
        s.setFechaInicio(r.getFechaInicio());
        s.setFechaFin(r.getFechaFin());
        s.setLesiones(r.getLesiones());
        s.setAlergias(r.getAlergias());
        s.setExtras(r.getExtras());
        s.setFoto(r.getFoto());
        return s;
    }

    private SocioResponse toResponse(Socio s) {
        SocioResponse r = new SocioResponse();
        r.setId(s.getId());
        r.setNombreCompleto(s.getNombreCompleto());
        r.setTelefono(s.getTelefono());
        r.setEmail(s.getEmail());
        r.setFechaNacimiento(s.getFechaNacimiento());
        r.setSexo(s.getSexo());
        r.setContactoEmergencia(s.getContactoEmergencia());
        r.setTelefonoEmergencia(s.getTelefonoEmergencia());
        r.setIdSocio(s.getIdSocio());
        r.setFechaRegistro(s.getFechaRegistro());
        r.setEstatus(s.getEstatus());
        r.setTipoMembresia(s.getTipoMembresia());
        r.setDescuento(s.getDescuento());
        r.setCostoMensual(s.getCostoMensual());
        r.setFechaInicio(s.getFechaInicio());
        r.setFechaFin(s.getFechaFin());
        r.setLesiones(s.getLesiones());
        r.setAlergias(s.getAlergias());
        r.setExtras(s.getExtras());
        r.setFoto(s.getFoto());
        return r;
    }
}