// MarketingServiceImpl.java
package org.example.fitgymbackend.modules.marketing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.entity.Socio;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.marketing.dto.*;
import org.example.fitgymbackend.modules.marketing.entity.*;
import org.example.fitgymbackend.modules.marketing.repository.*;
import org.example.fitgymbackend.repository.ISocioRepository;
import org.example.fitgymbackend.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements IMarketingService {

    private final LeadRepository leadRepository;
    private final CampanaRepository campanaRepository;
    private final EnvioCampanaRepository envioCampanaRepository;
    private final PromocionDescuentoRepository promocionRepository;
    private final SeguimientoLeadRepository seguimientoRepository;
    private final SegmentoMarketingRepository segmentoRepository;
    private final ISocioRepository socioRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    // ══════════════════════════════════════════════════════════════
    // LEADS
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse crearLead(LeadRequest request) {
        Lead lead = new Lead();
        lead.setNombreCompleto(request.getNombreCompleto());
        lead.setEmail(request.getEmail());
        lead.setTelefono(request.getTelefono());
        lead.setFuente(request.getFuente());
        lead.setNotas(request.getNotas());
        lead.setAsignadoA(request.getAsignadoA());
        lead.setFechaVisita(request.getFechaVisita());
        lead.setEtapa("CAPTACION");
        lead.setFechaUltimoContacto(LocalDateTime.now());

        Lead guardado = leadRepository.save(lead);

        // Email de respuesta automática al lead
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            try {
                emailService.sendMarketingEmail(
                        request.getEmail(),
                        "FitGym - ¡Gracias por tu interés!",
                        buildEmailBienvenidaLead(request.getNombreCompleto()));
            } catch (Exception e) {
                // No bloqueamos si falla el email
            }
        }

        return new ApiResponse("Lead creado exitosamente", true, guardado);
    }

    @Override
    public ApiResponse obtenerLeads() {
        return new ApiResponse("Leads obtenidos", true,
                leadRepository.findAllByOrderByFechaCreacionDesc());
    }

    @Override
    public ApiResponse obtenerLeadPorId(Long id) {
        return leadRepository.findById(id)
                .map(l -> new ApiResponse("Lead encontrado", true, l))
                .orElse(new ApiResponse("Lead no encontrado", false, null));
    }

    @Override
    @Transactional
    public ApiResponse actualizarEtapaLead(Long id, ActualizarEtapaRequest request) {
        Optional<Lead> opt = leadRepository.findById(id);
        if (opt.isEmpty())
            return new ApiResponse("Lead no encontrado", false, null);

        Lead lead = opt.get();
        String etapaAnterior = lead.getEtapa();
        lead.setEtapa(request.getEtapa());
        lead.setFechaUltimoContacto(LocalDateTime.now());

        // Registrar seguimiento automático por cambio de etapa
        if (request.getNota() != null && !request.getNota().isBlank()) {
            SeguimientoLead seg = new SeguimientoLead();
            seg.setLead(lead);
            seg.setTipoContacto("NOTA");
            seg.setDescripcion("Etapa actualizada: " + etapaAnterior + " → " + request.getEtapa()
                    + ". " + request.getNota());
            seg.setEtapaEnContacto(request.getEtapa());
            seguimientoRepository.save(seg);
        }

        return new ApiResponse("Etapa actualizada", true, leadRepository.save(lead));
    }

    @Override
    @Transactional
    public ApiResponse convertirLeadASocio(Long id) {
        Optional<Lead> opt = leadRepository.findById(id);
        if (opt.isEmpty())
            return new ApiResponse("Lead no encontrado", false, null);

        Lead lead = opt.get();
        lead.setEtapa("CERRADO");
        lead.setFechaConversion(LocalDateTime.now());
        leadRepository.save(lead);

        // Registrar en seguimiento
        SeguimientoLead seg = new SeguimientoLead();
        seg.setLead(lead);
        seg.setTipoContacto("NOTA");
        seg.setDescripcion("Lead convertido a socio exitosamente.");
        seg.setEtapaEnContacto("CERRADO");
        seguimientoRepository.save(seg);

        return new ApiResponse("Lead convertido a socio", true, lead);
    }

    @Override
    @Transactional
    public ApiResponse eliminarLead(Long id) {
        if (!leadRepository.existsById(id))
            return new ApiResponse("Lead no encontrado", false, null);
        leadRepository.deleteById(id);
        return new ApiResponse("Lead eliminado", true, null);
    }

    @Override
    @Transactional
    public ApiResponse agregarSeguimiento(Long leadId, SeguimientoLeadRequest request) {
        Optional<Lead> opt = leadRepository.findById(leadId);
        if (opt.isEmpty())
            return new ApiResponse("Lead no encontrado", false, null);

        Lead lead = opt.get();
        lead.setFechaUltimoContacto(LocalDateTime.now());
        leadRepository.save(lead);

        SeguimientoLead seg = new SeguimientoLead();
        seg.setLead(lead);
        seg.setTipoContacto(request.getTipoContacto());
        seg.setDescripcion(request.getDescripcion());
        seg.setRealizadoPor(request.getRealizadoPor());
        seg.setEtapaEnContacto(lead.getEtapa());

        return new ApiResponse("Seguimiento registrado", true, seguimientoRepository.save(seg));
    }

    @Override
    public ApiResponse obtenerSeguimientosLead(Long leadId) {
        return new ApiResponse("Seguimientos obtenidos", true,
                seguimientoRepository.findByLeadIdOrderByFechaContactoDesc(leadId));
    }

    // ══════════════════════════════════════════════════════════════
    // CAMPAÑAS
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse crearCampana(CampanaRequest request) {
        Campana campana = new Campana();
        campana.setNombre(request.getNombre());
        campana.setDescripcion(request.getDescripcion());
        campana.setTipo(request.getTipo());
        campana.setAsunto(request.getAsunto());
        campana.setContenidoHtml(request.getContenidoHtml());
        campana.setSegmentoId(request.getSegmentoId());
        campana.setFechaProgramada(request.getFechaProgramada());
        campana.setCreadoPor(request.getCreadoPor());
        campana.setEstado("BORRADOR");

        return new ApiResponse("Campaña creada", true, campanaRepository.save(campana));
    }

    @Override
    public ApiResponse obtenerCampanas() {
        return new ApiResponse("Campañas obtenidas", true,
                campanaRepository.findAllByOrderByFechaCreacionDesc());
    }

    @Override
    public ApiResponse obtenerCampanaPorId(Long id) {
        return campanaRepository.findById(id)
                .map(c -> new ApiResponse("Campaña encontrada", true, c))
                .orElse(new ApiResponse("Campaña no encontrada", false, null));
    }

    @Override
    @Transactional
    public ApiResponse enviarCampana(Long id) {
        Optional<Campana> opt = campanaRepository.findById(id);
        if (opt.isEmpty())
            return new ApiResponse("Campaña no encontrada", false, null);

        Campana campana = opt.get();
        if ("FINALIZADA".equals(campana.getEstado()))
            return new ApiResponse("Esta campaña ya fue enviada", false, null);

        // Determinar destinatarios según segmento
        List<Socio> destinatarios = obtenerDestinatariosPorSegmento(campana.getSegmentoId());

        int enviados = 0;
        int fallidos = 0;

        for (Socio socio : destinatarios) {
            if (socio.getEmail() == null || socio.getEmail().isBlank())
                continue;
            if (envioCampanaRepository.existsByCampanaIdAndEmailDestinatario(id, socio.getEmail()))
                continue;

            try {
                String html = campana.getContenidoHtml() != null
                        ? personalizarHtml(campana.getContenidoHtml(), socio.getNombreCompleto())
                        : buildEmailGenerico(socio.getNombreCompleto(), campana.getNombre(), campana.getAsunto());

                emailService.sendMarketingEmail(socio.getEmail(), campana.getAsunto(), html);

                EnvioCampana envio = new EnvioCampana();
                envio.setCampana(campana);
                envio.setEmailDestinatario(socio.getEmail());
                envio.setNombreDestinatario(socio.getNombreCompleto());
                envio.setTipoDestinatario("SOCIO");
                envio.setDestinatarioId(socio.getId());
                envio.setEstado("ENVIADO");
                envioCampanaRepository.save(envio);
                enviados++;

            } catch (Exception e) {
                fallidos++;
            }
        }

        campana.setEstado("FINALIZADA");
        campana.setFechaEnvio(LocalDateTime.now());
        campana.setTotalEnviados(enviados);
        campanaRepository.save(campana);

        return new ApiResponse(
                "Campaña enviada: " + enviados + " exitosos, " + fallidos + " fallidos",
                true,
                campana);
    }

    @Override
    @Transactional
    public ApiResponse eliminarCampana(Long id) {
        if (!campanaRepository.existsById(id))
            return new ApiResponse("Campaña no encontrada", false, null);
        campanaRepository.deleteById(id);
        return new ApiResponse("Campaña eliminada", true, null);
    }

    // ══════════════════════════════════════════════════════════════
    // CAMPAÑAS AUTOMATIZADAS
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse ejecutarCampanaBienvenida(Long socioId) {
        Optional<Socio> opt = socioRepository.findById(socioId);
        if (opt.isEmpty())
            return new ApiResponse("Socio no encontrado", false, null);

        Socio socio = opt.get();
        if (socio.getEmail() == null || socio.getEmail().isBlank())
            return new ApiResponse("El socio no tiene email registrado", false, null);

        try {
            emailService.sendMarketingEmail(
                    socio.getEmail(),
                    "¡Bienvenido a FitGym, " + socio.getNombreCompleto() + "!",
                    buildEmailBienvenidaSocio(socio.getNombreCompleto()));
            return new ApiResponse("Email de bienvenida enviado a " + socio.getEmail(), true, null);
        } catch (Exception e) {
            return new ApiResponse("Error al enviar email: " + e.getMessage(), false, null);
        }
    }

    @Override
    @Transactional
    public ApiResponse ejecutarCampanaPreVencimiento() {
        LocalDate hoy = LocalDate.now();
        List<Socio> socios = socioRepository.findAll();

        int enviados = 0;
        List<String> procesados = new ArrayList<>();

        for (Socio socio : socios) {
            if (socio.getEmail() == null || socio.getFechaFin() == null)
                continue;
            if (!"ACTIVO".equalsIgnoreCase(socio.getEstatus()))
                continue;

            long diasRestantes = ChronoUnit.DAYS.between(hoy, socio.getFechaFin());

            String asunto = null;
            String html = null;

            if (diasRestantes == 15) {
                asunto = "Tu membresía vence pronto — Renueva y ahorra 10%";
                html = buildEmailPreVencimiento(socio.getNombreCompleto(), 15, 10);
            } else if (diasRestantes == 7) {
                asunto = "¡Solo 7 días! No te quedes sin gym";
                html = buildEmailPreVencimiento(socio.getNombreCompleto(), 7, 10);
            } else if (diasRestantes == 3) {
                asunto = "Última oportunidad — Precio preferencial";
                html = buildEmailPreVencimiento(socio.getNombreCompleto(), 3, 15);
            } else if (diasRestantes == -1) {
                asunto = "Tu membresía venció — Reactívala con 20% de descuento";
                html = buildEmailVencido(socio.getNombreCompleto());
            }

            if (asunto != null) {
                try {
                    emailService.sendMarketingEmail(socio.getEmail(), asunto, html);
                    enviados++;
                    procesados.add(socio.getNombreCompleto() + " (" + diasRestantes + " días)");
                } catch (Exception e) {
                    // Continuar con el siguiente
                }
            }
        }

        return new ApiResponse("Campaña pre-vencimiento ejecutada: " + enviados + " emails enviados",
                true, procesados);
    }

    @Override
    @Transactional
    public ApiResponse ejecutarCampanaRecuperacion() {
        LocalDate hoy = LocalDate.now();
        List<Socio> socios = socioRepository.findAll();

        int enviados = 0;

        for (Socio socio : socios) {
            if (socio.getEmail() == null || socio.getFechaFin() == null)
                continue;
            if (!"INACTIVO".equalsIgnoreCase(socio.getEstatus()))
                continue;

            long diasDeBaja = ChronoUnit.DAYS.between(socio.getFechaFin(), hoy);

            String asunto = null;
            String html = null;

            if (diasDeBaja >= 28 && diasDeBaja <= 32) {
                asunto = "Te extrañamos — Vuelve sin costo de inscripción";
                html = buildEmailRecuperacion(socio.getNombreCompleto(), 0);
            } else if (diasDeBaja >= 58 && diasDeBaja <= 62) {
                asunto = "Plan especial de regreso: 2x1 en tu primer mes";
                html = buildEmailRecuperacion(socio.getNombreCompleto(), 50);
            } else if (diasDeBaja >= 88 && diasDeBaja <= 92) {
                asunto = "Tus pesas te esperan — 50% en tu primer mes de regreso";
                html = buildEmailRecuperacion(socio.getNombreCompleto(), 50);
            }

            if (asunto != null) {
                try {
                    emailService.sendMarketingEmail(socio.getEmail(), asunto, html);
                    enviados++;
                } catch (Exception e) {
                    // Continuar
                }
            }
        }

        return new ApiResponse("Campaña de recuperación ejecutada: " + enviados + " emails enviados",
                true, null);
    }

    @Override
    @Transactional
    public ApiResponse ejecutarCampanaCumpleanios() {
        LocalDate hoy = LocalDate.now();
        List<Socio> socios = socioRepository.findAll();

        int enviados = 0;

        for (Socio socio : socios) {
            if (socio.getEmail() == null || socio.getFechaNacimiento() == null)
                continue;

            boolean esCumpleanios = socio.getFechaNacimiento().getMonthValue() == hoy.getMonthValue()
                    && socio.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth();

            if (esCumpleanios) {
                try {
                    emailService.sendMarketingEmail(
                            socio.getEmail(),
                            "¡Feliz cumpleaños, " + socio.getNombreCompleto() + "! 🎂",
                            buildEmailCumpleanios(socio.getNombreCompleto()));
                    enviados++;
                } catch (Exception e) {
                    // Continuar
                }
            }
        }

        return new ApiResponse("Campaña cumpleaños ejecutada: " + enviados + " emails enviados",
                true, null);
    }

    // ══════════════════════════════════════════════════════════════
    // PROMOCIONES
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse crearPromocion(PromocionRequest request) {
        if (promocionRepository.existsByCodigo(request.getCodigo().toUpperCase()))
            return new ApiResponse("Ya existe una promoción con ese código", false, null);

        PromocionDescuento promo = new PromocionDescuento();
        promo.setNombre(request.getNombre());
        promo.setCodigo(request.getCodigo().toUpperCase().trim());
        promo.setTipoDescuento(request.getTipoDescuento());
        promo.setValor(request.getValor());
        promo.setFechaInicio(request.getFechaInicio());
        promo.setFechaFin(request.getFechaFin());
        promo.setLimiteUsosTotales(request.getLimiteUsosTotales());
        promo.setUsosPorPersona(request.getUsosPorPersona() != null ? request.getUsosPorPersona() : 1);
        promo.setAplicaA(request.getAplicaA() != null ? request.getAplicaA() : "AMBOS");
        promo.setDescripcion(request.getDescripcion());
        promo.setActivo(true);

        return new ApiResponse("Promoción creada", true, promocionRepository.save(promo));
    }

    @Override
    public ApiResponse obtenerPromociones() {
        return new ApiResponse("Promociones obtenidas", true,
                promocionRepository.findAllByOrderByFechaCreacionDesc());
    }

    @Override
    public ApiResponse validarCodigo(String codigo) {
        Optional<PromocionDescuento> opt = promocionRepository.findByCodigo(codigo.toUpperCase());
        if (opt.isEmpty())
            return new ApiResponse("Código no válido", false, null);

        PromocionDescuento promo = opt.get();

        if (!promo.getActivo())
            return new ApiResponse("Este código está inactivo", false, null);

        LocalDate hoy = LocalDate.now();
        if (promo.getFechaInicio() != null && hoy.isBefore(promo.getFechaInicio()))
            return new ApiResponse("Este código aún no está vigente", false, null);
        if (promo.getFechaFin() != null && hoy.isAfter(promo.getFechaFin()))
            return new ApiResponse("Este código ha expirado", false, null);

        if (promo.getLimiteUsosTotales() != null && promo.getUsosActuales() >= promo.getLimiteUsosTotales())
            return new ApiResponse("Este código ha alcanzado su límite de usos", false, null);

        return new ApiResponse("Código válido", true, promo);
    }

    @Override
    @Transactional
    public ApiResponse usarCodigo(String codigo) {
        ApiResponse validacion = validarCodigo(codigo);
        if (!validacion.isSuccess())
            return validacion;

        PromocionDescuento promo = promocionRepository.findByCodigo(codigo.toUpperCase()).get();
        promo.setUsosActuales(promo.getUsosActuales() + 1);

        if (promo.getLimiteUsosTotales() != null && promo.getUsosActuales() >= promo.getLimiteUsosTotales()) {
            promo.setActivo(false);
        }

        return new ApiResponse("Código aplicado correctamente", true, promocionRepository.save(promo));
    }

    @Override
    @Transactional
    public ApiResponse togglePromocion(Long id) {
        Optional<PromocionDescuento> opt = promocionRepository.findById(id);
        if (opt.isEmpty())
            return new ApiResponse("Promoción no encontrada", false, null);

        PromocionDescuento promo = opt.get();
        promo.setActivo(!promo.getActivo());
        return new ApiResponse(
                promo.getActivo() ? "Promoción activada" : "Promoción desactivada",
                true,
                promocionRepository.save(promo));
    }

    @Override
    @Transactional
    public ApiResponse eliminarPromocion(Long id) {
        if (!promocionRepository.existsById(id))
            return new ApiResponse("Promoción no encontrada", false, null);
        promocionRepository.deleteById(id);
        return new ApiResponse("Promoción eliminada", true, null);
    }

    // ══════════════════════════════════════════════════════════════
    // SEGMENTOS
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse crearSegmento(SegmentoRequest request) {
        SegmentoMarketing seg = new SegmentoMarketing();
        seg.setNombre(request.getNombre());
        seg.setDescripcion(request.getDescripcion());
        seg.setCriteriosJson(request.getCriteriosJson());
        return new ApiResponse("Segmento creado", true, segmentoRepository.save(seg));
    }

    @Override
    public ApiResponse obtenerSegmentos() {
        return new ApiResponse("Segmentos obtenidos", true,
                segmentoRepository.findAllByOrderByFechaCreacionDesc());
    }

    @Override
    @Transactional
    public ApiResponse ejecutarSegmento(Long id) {
        Optional<SegmentoMarketing> opt = segmentoRepository.findById(id);
        if (opt.isEmpty())
            return new ApiResponse("Segmento no encontrado", false, null);

        SegmentoMarketing segmento = opt.get();
        List<Socio> socios = aplicarFiltrosSegmento(segmento.getCriteriosJson());

        segmento.setUltimaEjecucion(LocalDateTime.now());
        segmento.setTotalSociosMatch(socios.size());
        segmentoRepository.save(segmento);

        List<Map<String, Object>> resultado = socios.stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("nombreCompleto", s.getNombreCompleto());
            m.put("email", s.getEmail());
            m.put("tipoMembresia", s.getTipoMembresia());
            m.put("estatus", s.getEstatus());
            m.put("fechaFin", s.getFechaFin());
            return m;
        }).collect(Collectors.toList());

        return new ApiResponse("Segmento ejecutado: " + socios.size() + " socios encontrados",
                true, resultado);
    }

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════

    @Override
    public ApiResponse obtenerDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        List<Socio> todosSocios = socioRepository.findAll();
        LocalDate hoy = LocalDate.now();

        // KPIs socios
        long totalSocios = todosSocios.size();
        long sociosActivos = todosSocios.stream()
                .filter(s -> "ACTIVO".equalsIgnoreCase(s.getEstatus())).count();
        long sociosInactivos = totalSocios - sociosActivos;
        double tasaRetencion = totalSocios > 0
                ? Math.round((double) sociosActivos / totalSocios * 10000.0) / 100.0
                : 0;

        // Vencimientos próximos (7 días)
        long vencenEn7Dias = todosSocios.stream()
                .filter(s -> s.getFechaFin() != null && "ACTIVO".equalsIgnoreCase(s.getEstatus()))
                .filter(s -> {
                    long dias = ChronoUnit.DAYS.between(hoy, s.getFechaFin());
                    return dias >= 0 && dias <= 7;
                }).count();

        // En riesgo (sin asistencia registrada hace más de 14 días — usamos fechaFin
        // como proxy)
        long enRiesgo = todosSocios.stream()
                .filter(s -> "ACTIVO".equalsIgnoreCase(s.getEstatus()))
                .filter(s -> s.getFechaFin() != null
                        && ChronoUnit.DAYS.between(hoy, s.getFechaFin()) > 7
                        && ChronoUnit.DAYS.between(hoy, s.getFechaFin()) <= 30)
                .count();

        // Leads
        Map<String, Long> leadsPorEtapa = new HashMap<>();
        for (Object[] row : leadRepository.contarPorEtapa()) {
            leadsPorEtapa.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> leadsPorFuente = new HashMap<>();
        for (Object[] row : leadRepository.contarPorFuente()) {
            leadsPorFuente.put((String) row[0], (Long) row[1]);
        }

        long totalLeads = leadRepository.count();
        long leadsCerrados = leadRepository.countByEtapa("CERRADO");
        double tasaConversion = totalLeads > 0
                ? Math.round((double) leadsCerrados / totalLeads * 10000.0) / 100.0
                : 0;

        // Campañas
        long campanasActivas = campanaRepository.findByEstado("ACTIVA").size();
        long campanasTotales = campanaRepository.count();

        // Promociones activas
        long promocionesActivas = promocionRepository.findByActivoTrue().size();

        // Cumpleañeros hoy
        long cumpleaniosHoy = todosSocios.stream()
                .filter(s -> s.getFechaNacimiento() != null)
                .filter(s -> s.getFechaNacimiento().getMonthValue() == hoy.getMonthValue()
                        && s.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth())
                .count();

        // Distribución por membresía
        Map<String, Long> distribucionMembresia = todosSocios.stream()
                .filter(s -> s.getTipoMembresia() != null)
                .collect(Collectors.groupingBy(Socio::getTipoMembresia, Collectors.counting()));

        dashboard.put("totalSocios", totalSocios);
        dashboard.put("sociosActivos", sociosActivos);
        dashboard.put("sociosInactivos", sociosInactivos);
        dashboard.put("tasaRetencion", tasaRetencion);
        dashboard.put("vencenEn7Dias", vencenEn7Dias);
        dashboard.put("enRiesgo", enRiesgo);
        dashboard.put("totalLeads", totalLeads);
        dashboard.put("leadsCerrados", leadsCerrados);
        dashboard.put("tasaConversion", tasaConversion);
        dashboard.put("leadsPorEtapa", leadsPorEtapa);
        dashboard.put("leadsPorFuente", leadsPorFuente);
        dashboard.put("campanasActivas", campanasActivas);
        dashboard.put("campanasTotales", campanasTotales);
        dashboard.put("promocionesActivas", promocionesActivas);
        dashboard.put("cumpleaniosHoy", cumpleaniosHoy);
        dashboard.put("distribucionMembresia", distribucionMembresia);

        return new ApiResponse("Dashboard obtenido", true, dashboard);
    }

    // ══════════════════════════════════════════════════════════════
    // MÉTODOS PRIVADOS — HELPERS
    // ══════════════════════════════════════════════════════════════

    private List<Socio> obtenerDestinatariosPorSegmento(Long segmentoId) {
        if (segmentoId == null)
            return socioRepository.findAll();
        Optional<SegmentoMarketing> opt = segmentoRepository.findById(segmentoId);
        if (opt.isEmpty())
            return socioRepository.findAll();
        return aplicarFiltrosSegmento(opt.get().getCriteriosJson());
    }

    private List<Socio> aplicarFiltrosSegmento(String criteriosJson) {
        List<Socio> todos = socioRepository.findAll();
        if (criteriosJson == null || criteriosJson.isBlank())
            return todos;

        try {
            Map<String, Object> criterios = objectMapper.readValue(criteriosJson, Map.class);

            return todos.stream().filter(socio -> {
                // Filtro por estatus
                if (criterios.containsKey("estatusSocio")) {
                    String est = (String) criterios.get("estatusSocio");
                    if (!est.equalsIgnoreCase(socio.getEstatus()))
                        return false;
                }
                // Filtro por tipo membresía
                if (criterios.containsKey("tipoMembresia")) {
                    String tipo = (String) criterios.get("tipoMembresia");
                    if (!tipo.equalsIgnoreCase(socio.getTipoMembresia()))
                        return false;
                }
                // Filtro por sexo
                if (criterios.containsKey("sexo")) {
                    String sexo = (String) criterios.get("sexo");
                    if (!sexo.equalsIgnoreCase(socio.getSexo()))
                        return false;
                }
                // Filtro por membresía vencida en X días
                if (criterios.containsKey("venceEnDias") && socio.getFechaFin() != null) {
                    int dias = (Integer) criterios.get("venceEnDias");
                    long restantes = ChronoUnit.DAYS.between(LocalDate.now(), socio.getFechaFin());
                    if (restantes < 0 || restantes > dias)
                        return false;
                }
                // Filtro por membresía vencida hace X días (recuperación)
                if (criterios.containsKey("vencioHaceDias") && socio.getFechaFin() != null) {
                    int dias = (Integer) criterios.get("vencioHaceDias");
                    long transcurridos = ChronoUnit.DAYS.between(socio.getFechaFin(), LocalDate.now());
                    if (transcurridos < 0 || transcurridos > dias)
                        return false;
                }
                return true;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            return todos;
        }
    }

    private String personalizarHtml(String html, String nombre) {
        return html.replace("{{nombre}}", nombre != null ? nombre : "Socio");
    }

    // ══════════════════════════════════════════════════════════════
    // TEMPLATES DE EMAIL
    // ══════════════════════════════════════════════════════════════

    private String buildBase(String headerColor, String titulo, String cuerpo) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><style>"
                + "body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f4f4f4;}"
                + ".container{max-width:600px;margin:20px auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1);}"
                + ".header{background:" + headerColor + ";padding:30px;text-align:center;color:#fff;}"
                + ".content{padding:30px;}"
                + "h1{margin:0;font-size:22px;}"
                + "p{color:#666;line-height:1.6;}"
                + ".btn{display:inline-block;padding:12px 30px;background:#606de5;color:#fff;text-decoration:none;border-radius:25px;margin:20px 0;font-weight:bold;}"
                + ".footer{padding:20px;text-align:center;color:#999;font-size:12px;border-top:1px solid #eee;}"
                + "</style></head><body><div class='container'>"
                + "<div class='header'><h1>" + titulo + "</h1></div>"
                + "<div class='content'>" + cuerpo + "</div>"
                + "<div class='footer'><p>FitGym &mdash; Todos los derechos reservados.</p></div>"
                + "</div></body></html>";
    }

    private String buildEmailBienvenidaSocio(String nombre) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>, ¡bienvenido a la familia FitGym!</p>"
                + "<p>Estamos muy contentos de tenerte con nosotros. Aquí tienes lo que necesitas saber:</p>"
                + "<ul><li>🕐 Horario: Lunes a Viernes 6am–10pm | Sábados 7am–8pm</li>"
                + "<li>💪 Clases incluidas según tu plan</li>"
                + "<li>📱 Cualquier duda, contáctanos directamente</li></ul>"
                + "<p>¡Nos vemos en el gym!</p>";
        return buildBase("linear-gradient(135deg,#606de5,#8b5cf6)", "¡Bienvenido a FitGym!", cuerpo);
    }

    private String buildEmailBienvenidaLead(String nombre) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>Gracias por tu interés en FitGym. Uno de nuestros asesores se pondrá en contacto contigo muy pronto.</p>"
                + "<p>Mientras tanto, puedes conocer nuestros planes y beneficios. ¡Te esperamos!</p>";
        return buildBase("linear-gradient(135deg,#606de5,#8b5cf6)", "Gracias por contactarnos", cuerpo);
    }

    private String buildEmailPreVencimiento(String nombre, int diasRestantes, int descuento) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>Tu membresía vence en <strong>" + diasRestantes + " día(s)</strong>.</p>"
                + "<p>Renueva ahora y obtén un <strong>" + descuento
                + "% de descuento</strong>. ¡No dejes que se interrumpa tu progreso!</p>"
                + "<div style='text-align:center'><a href='#' class='btn'>Renovar mi membresía</a></div>";
        return buildBase("linear-gradient(135deg,#f59e0b,#d97706)", "Tu membresía vence pronto", cuerpo);
    }

    private String buildEmailVencido(String nombre) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>Tu membresía venció ayer. ¡No te quedes fuera del gym!</p>"
                + "<p>Reactívala hoy con un <strong>20% de descuento</strong> especial para ti.</p>"
                + "<div style='text-align:center'><a href='#' class='btn'>Reactivar ahora</a></div>";
        return buildBase("linear-gradient(135deg,#ef4444,#dc2626)", "Tu membresía venció", cuerpo);
    }

    private String buildEmailRecuperacion(String nombre, int descuento) {
        String msg = descuento > 0
                ? "Te ofrecemos un <strong>" + descuento + "% de descuento</strong> en tu primer mes de regreso."
                : "¡Vuelve sin costo de inscripción! Solo por ser parte de nuestra familia.";
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>, ¡te extrañamos!</p>"
                + "<p>" + msg + "</p>"
                + "<p>Tu esfuerzo y progreso te esperan aquí. ¡Es momento de retomar!</p>"
                + "<div style='text-align:center'><a href='#' class='btn'>Volver a FitGym</a></div>";
        return buildBase("linear-gradient(135deg,#10b981,#059669)", "¡Te extrañamos!", cuerpo);
    }

    private String buildEmailCumpleanios(String nombre) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>¡El equipo de FitGym te desea un feliz cumpleaños! 🎂</p>"
                + "<p>Como regalo, <strong>trae a un amigo gratis</strong> esta semana. Solo muestra este email en recepción.</p>"
                + "<p>¡Que lo celebres con mucha energía!</p>";
        return buildBase("linear-gradient(135deg,#ec4899,#db2777)", "¡Feliz cumpleaños!", cuerpo);
    }

    private String buildEmailGenerico(String nombre, String campana, String asunto) {
        String cuerpo = "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>" + (asunto != null ? asunto : campana) + "</p>"
                + "<p>El equipo de FitGym</p>";
        return buildBase("linear-gradient(135deg,#606de5,#8b5cf6)", campana, cuerpo);
    }
}