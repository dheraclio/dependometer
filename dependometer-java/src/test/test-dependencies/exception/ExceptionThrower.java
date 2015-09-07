import java.io.IOException;

public class ExceptionThrower
{
   public void doSomething(int s) throws IOException, CheckedException
   {
      switch (s)
      {
         case 1:
            throw new IOException("io exception");
         case 2:
            throw new CheckedException();
         case 3:
            throw new UncheckedException();
      }
   }
}
