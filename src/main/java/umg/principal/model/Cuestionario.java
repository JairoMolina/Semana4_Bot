package umg.principal.model;

public class Cuestionario {
    private String seccion;
    private long telegramid;
    private int preguntaid;
    private String response;

    // Getters y setters
    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public long getTelegramid() { return telegramid; }
    public void setTelegramid(long telegramid) { this.telegramid = telegramid; }

    public int getPreguntaid() { return preguntaid; }
    public void setPreguntaid(int preguntaid) { this.preguntaid = preguntaid; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
