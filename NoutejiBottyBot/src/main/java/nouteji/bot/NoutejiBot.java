package nouteji.bot;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;

import java.lang.annotation.Documented;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoutejiBot {

    public static void main( String[] args )
    {

        ValiderNombreArguments(args.length);
        ValiderArgumentProfondeur(Integer.parseInt(args[0]));
        ValiderArgumentUrl(args[1]);
        ValiderArgumentDossier(args[2]);
        Explorer("https://departement-info-cem.github.io/3N5-Prog3/testbot/",1);

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

    public static void Explorer(String url, int profondeur)
    {

        if(profondeur > 0)
        {
            try{
                Document doc = Jsoup.connect(url).get();

                Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                Matcher matcher = p.matcher(doc.text());
                Set<String> emails = new HashSet<String>();
                while (matcher.find()) {
                    emails.add(matcher.group());
                }                                                        // extraire couriel
                System.out.println(emails);


//                Element link = doc.select("a").first();
//                String relHref = link.attr("href");
//                String absHref = link.attr("abs:href");




                Set<String> lesLiens = new HashSet<String>();
                Elements links = doc.select("a[href]");
                for (Element e : links) {
                    lesLiens.add(e.attr("abs:href"));
                }
                System.out.println(lesLiens);

                for (String elt : lesLiens) {

                  //  String contenu = lesLiens.

                }
                // extraire les liens

                Element link = doc.select("a").first();
                String relHref = link.attr("href");
                Path file = Paths.get(relHref);
                PrintWriter ecriture = new PrintWriter(new FileOutputStream(relHref,true));
                if(!Files.exists(file))
                {
                    Files.createFile(file);
                }
                ecriture.println("abs:href");                                                                    // ecrire fichier



//                System.out.println(absHref);
//                System.out.println(relHref); // pour mettre dans un fichier portant ce nom. (1.html)
            }

            catch (IOException e)
            {
                System.err.println("Error");
            }

            profondeur--;
            //  }

        }

       }

}
