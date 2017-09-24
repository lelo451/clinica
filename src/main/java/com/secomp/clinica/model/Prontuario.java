package com.secomp.clinica.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "prontuario_id")
    private Integer id;

    @Column(name = "prontuario_medico")
    private String medico;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "prontuario_data_consulta")
    private Date data_consulta;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "prontuario_sintomas")
    private String sintomas;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "prontuario_diagnostico")
    private String diagnostico;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "prontuario_receita")
    private String receita;

    @Column(name = "prontuario_assinatura")
    private String assinatura;

    @ManyToOne
    @JoinColumn(name = "prontuario_usuario_id")
    private Usuario usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public Date getData_consulta() {
        return data_consulta;
    }

    public void setData_consulta(Date data_consulta) {
        this.data_consulta = data_consulta;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getReceita() {
        return receita;
    }

    public void setReceita(String receita) {
        this.receita = receita;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prontuario that = (Prontuario) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}