package akressiopertti.service;

public class RelationViolationException extends RuntimeException {
    
   private String message; 
   
   public RelationViolationException(String message) {
       this.message = message;
   }

    public String getMessage() {
        return message;
    }

}
