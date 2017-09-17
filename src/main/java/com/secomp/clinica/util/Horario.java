package com.secomp.clinica.util;

import com.secomp.clinica.model.Consulta;
import com.secomp.clinica.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class Horario {

    private final ConsultaRepository consultaRepository;

    @Autowired
    public Horario(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    private ArrayList<LocalTime> todosHorarios() {
        ArrayList<LocalTime> listaHoras = new ArrayList<>();
        LocalTime l;
        for(int i = 8; i < 18; i++) {
            l = LocalTime.of(i, 0, 0);
            listaHoras.add(l);
            l = LocalTime.of(i, 30, 0);
            listaHoras.add(l);
        }
        return listaHoras;
    }

    public ArrayList<LocalTime> horariosDisponiveis(Date data, String tipo, String medico, LocalTime time) {

        boolean limitaHorario = false;

        LocalDate date = LocalDate.now();
        System.out.println("Agora: " + date + "\nData da consulta: " + data);

        if(date.equals(data)) {
            limitaHorario = true;
        }

        ArrayList<LocalTime> horas = todosHorarios();

        List<Consulta> listaConsultas = consultaRepository.findAllByDataConsulta(data);

        for(int i = 0; i < listaConsultas.size(); i++) {
            if(medico.equals(listaConsultas.get(i).getMedico())) {
                if(listaConsultas.get(i).getTipoConsulta().equals("NORMAL")) {
                    LocalTime local = LocalTime.parse(listaConsultas.get(i).getHora());
                    horas.remove(local);
                    for(LocalTime lt : horas) {
                        if(lt.isBefore(time))
                            horas.remove(lt);
                    }
                    try {
                        LocalTime local2 = LocalTime.parse(listaConsultas.get(i + 1).getHora());
                        horas.remove(local2);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Não há mais consultas");
                    }
                } else {
                    LocalTime local = LocalTime.parse(listaConsultas.get(i).getHora());
                    horas.remove(local);
                    if(limitaHorario) {
                        for (LocalTime lt : horas) {
                            if (lt.isBefore(time))
                                horas.remove(lt);
                        }
                    }
                }
            }
        }

        if(tipo.equals("NORMAL")) {
            LocalTime cal;
            ArrayList<LocalTime> maisHorasPraExcluir = new ArrayList<>();
            maisHorasPraExcluir.add(horas.get(horas.size() - 1));
            for (int i = 0; i<horas.size()-1; i++){
                if(horas.get(i).getMinute() == 30)
                    cal = LocalTime.of(horas.get(i).getHour()+1, 0, 0);
                else
                    cal = LocalTime.of(horas.get(i).getHour(), 30, 0);
                if (!horas.get(i+1).equals(cal)){
                    maisHorasPraExcluir.add(horas.get(i));
                }
                if(limitaHorario) {
                    for (LocalTime lt : horas) {
                        if (lt.isBefore(time))
                            maisHorasPraExcluir.add(lt);
                    }
                }
            }
            horas.removeAll(maisHorasPraExcluir);
        }
        return horas;
    }



}
