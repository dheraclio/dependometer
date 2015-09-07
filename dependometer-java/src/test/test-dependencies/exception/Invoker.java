import java.io.IOException;

public class Invoker
{
   public void invoke() throws IOException, CheckedException
   {
      try
      {
         new ExceptionThrower().doSomething(1);
      }
      catch (CheckedException e)
      {
         throw e;
      }
   }
}
