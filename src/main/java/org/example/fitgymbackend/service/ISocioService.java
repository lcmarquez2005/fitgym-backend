package org.example.fitgymbackend.service;

import org.example.fitgymbackend.model.request.SocioRequest;
import org.example.fitgymbackend.model.response.SocioResponse;

import java.util.List;

public interface ISocioService {
    List<SocioResponse> buscar(String q);
    SocioResponse registrar(SocioRequest request);
}