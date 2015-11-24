package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Image;

@WebServlet("/uploadServlet")
@MultipartConfig(location = "/var/www/html/image", maxFileSize = 1024 * 1024 * 20)
public class ImageSaving extends HttpServlet {

    //Database settings
    private final String dbURL = "jdbc:mysql://192.168.56.1:3306/image-sharing";
    private final String dbUser = "admin";
    private final String dbPass = "password";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream inputStream = null;
        try (PrintWriter out = response.getWriter()) {
            Part filePart = request.getPart("upload-image"); //obtains file part of multipart request
            Connection conn = null;
            String message = null;
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            try {
                // connects to the database
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

                //Upload file to DB
                filePart.write(filePart.getSubmittedFileName());

                // constructs SQL statement
                String sql = "INSERT INTO IMAGE (ImagePath, DateCreated, Title) values (?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, filePart.getSubmittedFileName());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                statement.setString(2, dateFormat.format(Calendar.getInstance().getTime()));
                statement.setString(3, filePart.getSubmittedFileName());
                // sends the statement to the database server
                int row = statement.executeUpdate();
                if (row > 0) {
                    message = "File uploaded and saved into database";
                }

            } catch (Exception e) {
                out.println("ERROR --> " + e.getMessage());
            } finally {
                response.getWriter().write(message);
                out.close();
            }
            
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
