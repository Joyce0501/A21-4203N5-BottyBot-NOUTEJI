package nouteji.bot;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.nio.ch.ThreadPool;


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
  public static final   List<String> lesLiens = new ArrayList<String>();
  public static final HashSet<String> lesliensSet = new HashSet<>();
  public static final  List<String> emails = new ArrayList<>();
  public static boolean ArgumentErreurs;

    // private static final  Document doc = Jsoup.connect(url).get();
    public static void main( String[] args ) throws IOException, InterruptedException {

        ValiderNombreArguments(args.length);
        ValiderArgumentProfondeur(args[0]);
        ValiderArgumentUrl(args[1]);
        ValiderArgumentDossier(args[2]);
        Explorer1erUrl(args[1],args[2],Integer.parseInt(args[0]));
    }

    public static int ValiderNombreArguments(int length)
    {
        if(length != 3)
        {
            System.err.println("tu dois me fournir 03 arguements. Le premier est un entier , le deuxieme une adresse URL et le dernier une liste");
        }

        return length;
    }

    public static void ValiderArgumentProfondeur(String profondeur)
    {
        try
        {

            int LaProfondeur =  Integer.parseInt(profondeur);

            if ( LaProfondeur < 0 || LaProfondeur > 99)
            {
                System.err.println("la profondeur doit etre comprise entre 0 et 99 inclusivement");
            }
            else
            {
                System.out.println("La profondeur est correcte");
            }

        }
        catch (Exception e)
        {
            System.out.println("Ceci n'est pas un nombre");
        }


    }

    public static boolean ValiderArgumentUrl (String lien)
   {


          if (!UrlValidator.getInstance().isValid(lien))
           {
               System.err.println("url est invalide");

           }
           else
           {
               System.out.println("url est valide");
              // UrlValides.add(lien);

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

    // explorer 1ere url
    public static void Explorer1erUrl(String url,String dossier,int profondeur) throws IOException, InterruptedException {
        lesliensSet.add(url);
        lesLiens.add(url);
        Document doc = Jsoup.connect(url).get();
        ChercherCourriels(url,doc);
        Sauvegarder(dossier,url,doc);
        Explorer(dossier,profondeur,0);

    }
    // Traitement
    // gestion de la profondeur

    // liste d'url , les avoir dans l'irdre te les explorer , a chaque fois on ajoute une url tout en vérifiant qu'on l'a pas encore exploré
    public static void Explorer(String path, int profondeur,int urlvisites) throws IOException, InterruptedException {


        if(profondeur == 0)
        {
          //  List<String> emailsenordre = emails

            List<String> subList = emails.subList(0, emails.size());
            subList.sort(String.CASE_INSENSITIVE_ORDER);
            System.out.println("Nombre de pages explorées : " + lesLiens.size());
            System.out.println("Nombre de courriels extraits en ordre alphabétique : " + emails.size());
            for(int i = 0; i <= emails.size();i++)
            {
                System.out.println(emails.get(i));
            }
            return;

        }

      /*  Document doc = Jsoup.connect(lurl).get();
            Elements links = doc.select("a[href]");
            for (Element e : links) {
                lesLiens.add(e.attr("abs:href"));

            } */


        // step 1: url a partir de l'index jamais exploré
        int taille = lesLiens.size();
            for (int i = urlvisites; i <= taille; i++)
            {

                String urlCourante = lesLiens.get(i);
                Document documentUrlCourante = Jsoup.connect(urlCourante).get();
                Elements elt = documentUrlCourante.select("a");
                for(Element element : elt)

                {

                    String urlTrouvee = element.absUrl("href");
                    if(lesliensSet.contains(urlTrouvee))
                    {
                        return;
                    }

                    // l'url decouverte n'est pas valide et pas explorée
                    try{
                        new URL(urlTrouvee);
                        Document documentUrlTrouvee = Jsoup.connect(urlTrouvee).get();
                        lesLiens.add(urlTrouvee);
                        lesliensSet.add(urlTrouvee);
                        ChercherCourriels(urlTrouvee,documentUrlTrouvee);
                        Sauvegarder(path,urlTrouvee,documentUrlTrouvee);

                    }

                    //url incorrecte
                    catch (MalformedURLException e)
                    {
                      //  if(ValiderArgumentUrl(urlTrouvee) == false)
                   //     {
                            System.out.println("Url mal formée :" + urlTrouvee);
                     //   }
                    }
                    // url non joignables
                    catch (Exception e)
                    {
                     //   if(ValiderArgumentUrl(urlTrouvee) == false)
                     //   {
                            System.out.println("Url injoignable :" + urlTrouvee);
                      //  }
                    }

                    // pages web existantes mais non consultables

                }
            }

            Explorer(path,profondeur--,urlvisites);
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

         while (matcher.find()) {
             if(!emails.contains(matcher.group())) emails.add(matcher.group());
         }
         // me creer une profondeur actuelle
         System.out.println("Exploration de " + url);

     }

}
