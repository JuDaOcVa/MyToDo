package com.dev.Models;

public class ListItem {
    private int id;
    private String title;
    private String description;
    private String emoji;
    private int check;
    private String fecha;
    private String hora;

    public ListItem(int id, String title, String description, String emoji, int check, String fecha, String hora) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.emoji = emoji;
        this.check = check;
        this.fecha = fecha;
        this.hora = hora;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheck() {
        return check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
