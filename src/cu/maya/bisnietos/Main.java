package cu.maya.bisnietos;

import cu.maya.bisnietos.core.SSLHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class Main {

    private static final String[] titles = {"Apellidos", "Nombre", "Año de Nacimiento", "Lugar de nacimiento", "Lugar de salida", "Lugar de entrada", "Datos", "Imagenes"};
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36";
    public static String NEXT_PAGE = "https://pares.mcu.es/MovimientosMigratorios/";
    public static String nextPage = "buscadorRaw.form;jsessionid=744B6972EC89E034FFB99BD73590BD95?d-3602157-p=2635&objectsPerPage=25";

    public static void main(String[] args) {

        File file = new File("D://Movimientos Migratorios Iberoamericanos.txt");
        Elements navigation = null;
        boolean error;
        do {
            try {
                Document document = SSLHelper.getConnection(NEXT_PAGE + nextPage).userAgent(USER_AGENT).timeout(0).get();
                error = false;
                extractInfo(document, file);
//            next page
                navigation = document.select("div.pagResultados a[href]:contains(Sig.)");
                nextPage = navigation.first().attr("href");
                appendToFileFileOutputStream(file, NEXT_PAGE + nextPage);
                System.out.println(new Date() + " " + NEXT_PAGE + nextPage);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                error = true;
            }
        } while (navigation.size() > 0 || error);

    }


    public static void extractInfo(Document document, File file) throws IOException {
//            table#coleccion tr    contenido
        Elements elements = document.select("table#coleccion tr.odd,tr.even");

        for (Element el : elements) {
            String row = "";
            Elements tds = el.select("td");
            for (int i = 0; i < tds.size(); i++) {
                if (i < 6) row += (tds.get(i).text()) + ", ";
                else {
                    Element link = tds.get(i).select("a").first();
                    if (link != null) {
                        String linkHref = link.attr("href");
                        row += linkHref;
                    }
                    if (i < 7) row += ", ";
                }
            }
            appendToFileFileOutputStream(file, row);
        }
    }

    private static void appendToFileFileOutputStream(File file, String content) throws IOException {
        // append mode
        content += "\n";
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

}
