/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class WS_Calendario extends HttpServlet {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "dbcalendario";
    final private String user = "root";
    final private String password = "";
    private Connection calendario;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            calendario = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            calendario.close();
        } catch (SQLException e) {
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Calendario</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Calendario at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     *
     * examples of use: http://localhost:8080/phoneBook/gigi
     * http://localhost:8080/phoneBook/lucia?descr=si
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Attivita attivita;
        ArrayList<Attivita> listaAttivita = new ArrayList<Attivita>();

        String materia = null;
        String sezione = null;
        String titolo = null;
        String data;
        String orario = null;
        String descrizione = null;
        String svolto = null;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        
        try {
            data = request.getParameter("data");
            String sql = "SELECT idMateria,idSezione,titolo,orario,descrizione,svolto";
            sql += " FROM attivita WHERE data = '" + data + "';";
            // ricerca nominativo nel database
            Statement statement = calendario.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                while (result.next()) {
                    materia = result.getString(1);
                    if (materia.equals("1")) {
                        materia = "Tecnologie";
                    }
                    if (materia.equals("2")) {
                        materia = "Sistemi e reti";
                    }
                    sezione = result.getString(2);
                    if (sezione.equals("1")) {
                        sezione = "5^B inf";
                    }
                    if (sezione.equals("2")) {
                        sezione = "5^C inf";
                    }
                    titolo = result.getString(3);
                    orario = result.getString(4);
                    descrizione = result.getString(5);
                    svolto = result.getString(6);
                    attivita = new Attivita(materia, sezione, titolo, data, orario, descrizione, svolto);
                    listaAttivita.add(attivita);
                }

            } else {
                response.sendError(404, "Entry not found!");
                result.close();
                statement.close();
                return;
            }
            result.close();
            statement.close();
            // scrittura del body della risposta
            response.setContentType("text/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<entry>");
                for (int i = 0; i < listaAttivita.size(); i++) {
                    out.print("<materia>");
                    out.print(materia);
                    out.println("</materia>");
                    out.print("<sezione>");
                    out.print(sezione);
                    out.println("</sezione>");
                    out.print("<titolo>");
                    out.print(titolo);
                    out.println("</titolo>");
                    out.print("<data>");
                    out.print(data);
                    out.println("</data>");
                    out.print("<orario>");
                    out.print(orario);
                    out.println("</orario>");
                    out.print("<descrizione>");
                    out.print(descrizione);
                    out.println("</descrizione>");
                    out.print("<svolto>");
                    out.print(svolto);
                    out.println("</svolto>");
                }
                out.println("</entry>");
            } finally {
                out.close();
            }
            response.setStatus(200); // OK
        } catch (SQLException e) {
            response.sendError(500, "DBMS server error!");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);
        String line;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        try {
            // scrittura nel file "entry.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();
            // estrazione dei valori degli elementi "name" e "number" dal file "entry.xml"
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("entry.xml");
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("titolo");
            String materia = null;
            String sezione = null;
            String titolo = null;
            String data = null;
            String orario = null;
            String descrizione = null;
            if (list != null && list.getLength() > 0) {
                titolo = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("materia");
            materia = null;
            if (list != null && list.getLength() > 0) {
                materia = list.item(0).getFirstChild().getNodeValue();
                if (materia.equals("Tecnologie")) {
                    materia = "1";
                }
                if (materia.equals("Sistemi e reti")) {
                    materia = "2";
                }
            }

            list = root.getElementsByTagName("sezione");
            sezione = null;
            if (list != null && list.getLength() > 0) {
                sezione = list.item(0).getFirstChild().getNodeValue();
                if (sezione.equals("5^B inf")) {
                    sezione = "1";
                }
                if (sezione.equals("5^C inf")) {
                    sezione = "2";
                }
            }

            list = root.getElementsByTagName("data");
            data = null;
            if (list != null && list.getLength() > 0) {
                data = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("orario");
            orario = null;
            if (list != null && list.getLength() > 0) {
                orario = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("descrizione");
            descrizione = null;
            if (list != null && list.getLength() > 0) {
                descrizione = list.item(0).getFirstChild().getNodeValue();
            }

            if (titolo == null || materia == null || sezione == null || data == null || orario == null || descrizione == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (titolo.isEmpty() || materia.isEmpty() || data.isEmpty() || orario.isEmpty() || descrizione.isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            try {
                // aggiunta voce nel database
                Statement statement = calendario.createStatement();
                if (statement.executeUpdate("INSERT attivita(idMateria,idSezione,titolo,data,orario,descrizione) VALUES('" + materia + "', '" + sezione + "', '" + titolo + "', '" + data + "', '" + orario + "', '" + descrizione + "');") <= 0) {
                    response.sendError(403, "Name exist!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(201); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "PhonebookDB";
    }// </editor-fold>
}
