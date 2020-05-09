/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Christian
 */
public class Attivita {
    private String materia;
    private String classe;
    private String titolo;
     private String data;
    private String orario;
    private String descrizione;
    private String svolto;

    public Attivita(String materia, String classe, String titolo,String data, String orario, String descrizione, String svolto) {
        this.materia = materia;
        this.classe = classe;
        this.titolo = titolo;
        this.orario = orario;
        this.descrizione = descrizione;
        this.svolto = svolto;
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
    
    

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setSvolto(String svolto) {
        this.svolto = svolto;
    }

    public String getMateria() {
        return materia;
    }

    public String getClasse() {
        return classe;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getOrario() {
        return orario;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getSvolto() {
        return svolto;
    }
    
}
