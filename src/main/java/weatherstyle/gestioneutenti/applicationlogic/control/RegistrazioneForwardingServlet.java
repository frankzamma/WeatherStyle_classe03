package weatherstyle.gestioneutenti.applicationlogic.control;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import weatherstyle.gestioneutenti.applicationlogic.logic.beans.Utente;

import java.io.IOException;

/**
 * @author Francesco Giuseppe Zammarrelli
 * La classe Registrazione forwarding servlet.
 */
@WebServlet(name = "RegistrazioneForwardingServlet",value = "/registrazione-utente")
public class RegistrazioneForwardingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/gestioneUtente/utente/registrazione.jsp");
            dispatcher.forward(request,response);
        } else {
            response.sendRedirect("index.html");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
