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
  public static boolean ArgumentErreurs = false;
  public static   String instructions =
            "La commande prend 3 arguments :\n " +
                    "- une profondeur entre 0 et 99\n " +
                    "- une URL de site à explorer\n " +
                    "- un chemin où sauvegarder les résultats";

    // private static final  Document doc = Jsoup.connect(url).get();
    public static void main( String[] args ) throws IOException, InterruptedException {

        System.out.println("Bonjour, Joyce");
        ValiderArguments(args.length,args[0],args[1],args[2]);
        if(ArgumentErreurs)
        {
            System.out.println(instructions);
            return;
        }
        System.out.println("Nous pouvons explorer");
        Explorer1erUrl(args[1],args[2],Integer.parseInt(args[0]));

    }

    public static void ValiderArguments(int length, String Profondeur, String Url , String Dossier)
    {
        if(length != 3)
        {
            ArgumentErreurs = true;
            System.err.println("Nombre d'arguments incorrecte");
        }
        else
        {
            ValiderArgumentUrl(Url);
            ValiderArgumentProfondeur(Profondeur);
            ValiderArgumentDossier(Dossier);
        }
    }

    public static void ValiderArgumentProfondeur(String profondeur)
    {
        try
        {

            int LaProfondeur =  Integer.parseInt(profondeur);

            if ( LaProfondeur < 0 || LaProfondeur > 99)
            {
                ArgumentErreurs = true;
                System.err.println("la profondeur doit etre comprise entre 0 et 99 inclusivement");
            }

        }
        catch (Exception e)
        {
            ArgumentErreurs = true;
            System.out.println("Ceci n'est pas un nombre");
        }


    }

    public static void ValiderArgumentUrl (String lien)
   {

       try {
           new URL(lien);
           Document doc = Jsoup.connect(lien).get();
       }
       catch (MalformedURLException e)
       {
           ArgumentErreurs = true;
           System.err.println("Url pas valide");
       }
       catch (IOException e) {
           ArgumentErreurs = true;
           System.err.println("Impossible de se connecter");

       }


   }

    // le répertoire où écrire les copies locales des fichiers explorés. Le dossier doit être accessible et on doit pouvoir y écrire. Ecriture seulement
    // est accessible en lecture

    public static void ValiderArgumentDossier (String Dossier)
    {

        File monfichier = new File(Dossier);
        if(!monfichier.exists())
        {
            ArgumentErreurs = true;
            System.err.println("Dossier n'existe pas");

        }
        else if(!Files.isWritable(Paths.get(Dossier)))
        {
            ArgumentErreurs = true;
            System.err.println("Impossible d'écrire dans ce dossier");
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
            for(int i = 0; i < emails.size();i++)
            {
                System.out.println(emails.get(i));
            }
            return;

        }

        // step 1: url a partir de l'index jamais exploré
        int taille = lesLiens.size();
            for (int i = urlvisites; i < taille; i++)
            {
                String urlCourante = lesLiens.get(i);
                Document documentUrlCourante = Jsoup.connect(urlCourante).get();
                Elements elt = documentUrlCourante.select("a");
                for(Element element : elt)

                {

                    String urlTrouvee = element.absUrl("href");
                    if(!lesliensSet.contains(urlTrouvee))
                    {
                        lesliensSet.add(urlTrouvee);
                        try{
                            new URL(urlTrouvee);
                            Document documentUrlTrouvee = Jsoup.connect(urlTrouvee).get();
                            ChercherCourriels(urlTrouvee,documentUrlTrouvee);
                            Sauvegarder(path,urlTrouvee,documentUrlTrouvee);
                            lesLiens.add(urlTrouvee);
                        }
                        // l'url decouverte n'est pas valide et pas explorée


                        catch (FileNotFoundException e)
                        {

                        }
                        //url incorrecte
                        catch (MalformedURLException e)
                        {
                            System.err.println("Url mal formée : " + urlTrouvee);
                        }
                        // url non joignables
                        catch (Exception e)
                        {
                            System.err.println("Url injoignable : " + urlTrouvee);


                        }
                    }
                }
            }
            Explorer(path,--profondeur,urlvisites);
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
