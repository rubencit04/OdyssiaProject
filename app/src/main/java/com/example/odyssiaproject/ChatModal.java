package com.example.odyssiaproject;

public class ChatModal {
    private String mensaje;
    private String sender;  // puede ser "user" o "bot"

    public ChatModal(String mensaje, String sender) {
        this.mensaje = mensaje;
        this.sender = sender;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
