package com.example.odyssiaproject.entidad;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
   private int id;
   private String usuario,nacionalidad, contrasenia, correo;

   private List<String> favoritosCiudades;

   private Boolean tema;

   public Usuario(int id, Boolean tema, String usuario, String nacionalidad, String contrasenia, String correo, List<String> favoritosCiudades, List<Actividad> favoritosActividades) {
      this.id = id;
      this.tema = tema;
      this.usuario = usuario;
      this.nacionalidad = nacionalidad;
      this.contrasenia = contrasenia;
      this.correo = correo;
      this.favoritosCiudades = favoritosCiudades;
   }

   public Usuario(String correo, String contrasenia) {
      this.contrasenia = contrasenia;
      this.correo = correo;
   }

   public Usuario() {

   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsuario() {
      return usuario;
   }

   public void setUsuario(String usuario) {
      this.usuario = usuario;
   }

   public String getContrasenia() {
      return contrasenia;
   }

   public void setContrasenia(String contrasenia) {
      this.contrasenia = contrasenia;
   }

   public String getCorreo() {
      return correo;
   }

   public void setCorreo(String correo) {
      this.correo = correo;
   }


   public String getNacionalidad() {
      return nacionalidad;
   }

   public void setNacionalidad(String nacionalidad) {
      this.nacionalidad = nacionalidad;
   }

   public List<String> getFavoritosCiudades() {
      return favoritosCiudades;
   }

   public void setFavoritosCiudades(List<String> favoritosCiudades) {
      this.favoritosCiudades = favoritosCiudades;
   }

   public Boolean getTema() {
      return tema;
   }

   public void setTema(Boolean tema) {
      this.tema = tema;
   }

   @Override
   public String toString() {
      return "Usuario{" +
              "id=" + id +
              ", usuario='" + usuario + '\'' +
              ", nacionalidad='" + nacionalidad + '\'' +
              ", contrasenia='" + contrasenia + '\'' +
              ", correo='" + correo + '\'' +
              ", favoritosCiudades=" + favoritosCiudades +
              ", tema=" + tema +
              '}';
   }
}
