package nouteji.bot;

import java.net.URL;

public class NoutejiBot {

    public static void main( String[] args )
    {

        // System.out.println( "Hello Joyce" );
        ValiderNombreArguments(args.length);
        ValiderArgumentProfondeur(Integer.parseInt(args[0]));
       // ValiderUrl(args[1]);


    }

    public static int ValiderNombreArguments(int length)
    {
        if(length != 3)
        {
            System.err.println("tu dois me fournir 03 arguements. Le premier est un entier , le deuxieme une adresse URL et le dernier une liste");
        }

        return length;
    }

    public static int ValiderArgumentProfondeur(int profondeur)
    {
        if (profondeur < 0)
        {
            System.err.println("la profondeur ne peut etre négative");
        }

        return profondeur;

    }


    public static class Test {

        public static boolean isValidURL(String url){
            try {
                URL u = new URL(url);
                u.openConnection().connect();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }


}
