package br.com.conexaoautolib.autoconfigure.properties;

/**
 * Propriedades de configuração de logging.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class LoggingProperties {
    
    /**
     * Habilita/desabilita logging de requisições/respostas.
     */
    private boolean enabled = true;
    
    /**
     * Nível de logging para requisições.
     */
    private String level = "INFO";
    
    /**
     * Loga corpo da requisição (pode conter dados sensíveis).
     */
    private boolean logRequestBody = false;
    
    /**
     * Loga corpo da resposta (pode conter dados sensíveis).
     */
    private boolean logResponseBody = false;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public boolean isLogRequestBody() {
        return logRequestBody;
    }
    
    public void setLogRequestBody(boolean logRequestBody) {
        this.logRequestBody = logRequestBody;
    }
    
    public boolean isLogResponseBody() {
        return logResponseBody;
    }
    
    public void setLogResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody;
    }
}