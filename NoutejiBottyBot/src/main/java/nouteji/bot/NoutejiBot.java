package nouteji.bot;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.print.Doc;
import java.io.*;

import java.lang.annotation.Documented;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoutejiBot {

  private static final   Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
    private static final  Document doc = Jsoup.connect(url).get();
    public static void main( String[] args )
    {

        ValiderNombreArguments(args.length);
        ValiderArgumentProfondeur(Integer.parseInt(args[0]));
        ValiderArgumentUrl(args[1]);
        ValiderArgumentDossier(args[2]);
     //   Explorer("https://departement-info-cem.github.io/3N5-Prog3/testbot/",1);

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
        else
        {
            System.out.println("La profondeur est correcte");
        }
        return profondeur;
    }

    public static Boolean ValiderArgumentUrl (String lien)
   {
           if (!UrlValidator.getInstance().isValid(lien))
           {
               System.err.println("url est invalide");

           }
           else
           {
               System.out.println("url est valide");

           }
        return UrlValidator.getInstance().isValid(lien);
   }

    // le répertoire où écrire les copies locales des fichiers explorés. Le dossier doit être accessible et on doit pouvoir y écrire. Ecriture seulement
    // est accessible en lecture

    public static Boolean ValiderArgumentDossier (String Dossier)
    {

        File monfichier = new File(Dossier);
        if(!monfichier.exists())
        {
            System.err.println("Dossier n'existe pas");
            return false;
        }
        else
        {
            System.out.println("c'est good");
            return true;
        }

    }

    // Traitement
    // gestion de la profondeur

    // liste d'url , les avoir dans l'irdre te les explorer , a chaque fois on ajoute une url tout en vérifiant qu'on l'a pas encore exploré
    public static void Explorer(String path, int profondeur,int urlvisites)

    {

        if(profondeur == 0)
        return;

            profondeur--;

    }

    public static void Sauvegarder(String Dossier, String url, Document fichier) throws IOException {
      // le but est d'enregistrer le fichier dans e dossier
        URL unlien = new URL(url);

        // remplacer les adresses courriels par le mien
        String remplacement = fichier.toString().replaceAll(String.valueOf(p),"1983276@cegepmontpetit.ca");

        // lieu de fichier
        File newfile = new File(Dossier + unlien.getPath().replaceAll("[\\\\:*?<>|]","-"));

        // cree le dossier parent
        Files.createDirectories(Paths.get(newfile.getParent()));

        // cree le fichier
        FileWriter ecriture = new FileWriter(newfile.getPath());

        // ecrire mon courriel
        ecriture.write(remplacement);

        ecriture.close();

    }

    //extraire mes courriels

     public static void  ChercherCourriels(String url, Document docourriel)
     {
         Matcher matcher = p.matcher(docourriel.text());
         List<String> emails = new ArrayList<>();
         while (matcher.find()) {
             if(!emails.contains(matcher.group())) emails.add(matcher.group());
         }
         // me creer une profondeur actuelle
         System.out.println("Exploration de " + url);

     }

}
