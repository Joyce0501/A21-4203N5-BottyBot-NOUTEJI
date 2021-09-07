package nouteji.bot;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;

import java.net.URL;

public class NoutejiBot {

    public static void main( String[] args )
    {

        ValiderNombreArguments(args.length);
        ValiderArgumentProfondeur(Integer.parseInt(args[0]));
        ValiderArgumentUrl(args[1]);
        ValiderArgumentDossier(args[2]);
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

    public static boolean ValiderArgumentUrl (String lien)
   {
           if (!UrlValidator.getInstance().isValid(lien))
           {
               System.out.println("url est invalide");
           }
           else
           {
               System.out.println("url est valide");
           }

          return UrlValidator.getInstance().isValid(lien);

   }

    // le répertoire où écrire les copies locales des fichiers explorés. Le dossier doit être accessible et on doit pouvoir y écrire. Ecriture seulement
    // est accessible en lecture

    public static File ValiderArgumentDossier (String Dossier)
    {
//        File Directory = new File(Dossier);
//
//        try{
//            if(Directory.mkdir()) {
//                System.out.println("Dossier crée");
//            } else {
//                System.out.println("Dossier existe déja");
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//        }

        // création du dossier
        new File(Dossier).mkdirs();

        // je vais essayer de creer un fichier dans le dossier
        File Lefichier = new File(Dossier,"monFichier");

        // ca marche donc le dossier existe et je peux ecrire, vite je detruis le fichier que je viens de creer
        if(Lefichier.exists())
        {
            Lefichier.delete();

        }
        // ca marche pas > messa ge erreur
        else
        {
            System.out.println("le dossier n'existe pas");
        }

        return Lefichier;
    }

}
