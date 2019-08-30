

package com.co.novaip.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.json.JSONObject;

/**
 *
 * @author desarrollo7
 */

@Entity
@Table(name = "question")
public class Question implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "parent", nullable = true, unique = false)
    private Long parent;
    
    @Column(name = "empresa_slug", nullable = true, unique = false)
    private String empresaSlug;
    
    @Column(name = "pregunta", nullable = true, unique = false)
    private String pregunta;
    
    @Column(name = "key", nullable = true, unique = false)
    private String key;
    
    @Column(name = "oneWay", nullable = true, unique = false)
    private Boolean oneWay;
    
    @Column(name = "endPoint", nullable = true, unique = false)
    private String endPoint;
    
    @Column(name = "respuesta_posible", nullable = true, unique = false)
    private String respuestaPosible;
    
    @Column(name = "fecha_creacion",nullable = true, unique = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion",nullable = true, unique = false)
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "eliminado",nullable = true, unique = false)
    private Boolean eliminado;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getEmpresaSlug() {
        return empresaSlug;
    }

    public void setEmpresaSlug(String empresaSlug) {
        this.empresaSlug = empresaSlug;
    }

    public String getRespuestaPosible() {
        return respuestaPosible;
    }

    public void setRespuestaPosible(String respuestaPosible) {
        this.respuestaPosible = respuestaPosible;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    
    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getOneWay() {
        return oneWay;
    }

    public void setOneWay(Boolean oneWay) {
        this.oneWay = oneWay;
    }
    
    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + id + ", parent=" + parent + ", empresaSlug=" + empresaSlug + ", pregunta=" + pregunta + ", key=" + key + ", oneWay=" + oneWay + ", endPoint=" + endPoint + ", respuestaPosible=" + respuestaPosible + ", fechaCreacion=" + fechaCreacion + ", fechaActualizacion=" + fechaActualizacion + ", eliminado=" + eliminado + '}';
    }

    
}
