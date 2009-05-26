package examples.categorias;

public class DeepTwitterException extends Exception{
	
	private int code;
	
	public DeepTwitterException(String msg) {
        super(msg);
    }
	
	public DeepTwitterException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public DeepTwitterException(String msg, Exception cause) {
        super(msg, cause);
    }
    
    public int getCode(){
    	return code;
    }
    
    public static final int NENHUM_USUARIO = 1;
    public static final int NENHUMA_MENSAGEM = 2;
    
    //code = 1 --> nenhum usuario selecionado
    //code = 2 --> nenhuma palavra nas categorias

}
