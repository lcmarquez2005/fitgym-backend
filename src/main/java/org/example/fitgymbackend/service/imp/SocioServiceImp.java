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

    // ── Mappers ──────────────────────────────────────────
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