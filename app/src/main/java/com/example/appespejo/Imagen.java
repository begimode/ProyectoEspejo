package com.example.appespejo;

public class Imagen {
    private String titulo;
    private String url;
    private long tiempo;

    public Imagen(String titulo, String url, long tiempo) {
        this.titulo = titulo;
        this.url = url;
        this.tiempo = tiempo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }
}
