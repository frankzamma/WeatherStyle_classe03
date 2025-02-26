package weatherstyle.gestioneutenti.applicationlogic.control;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import weatherstyle.gestioneutenti.applicationlogic.logic.beans.Admin;
import weatherstyle.gestioneutenti.applicationlogic.logic.beans.Utente;
import weatherstyle.gestioneutenti.applicationlogic.logic.service.AdminLogicImpl;
import weatherstyle.gestioneutenti.applicationlogic.logic.service.AdminLogicService;

import java.io.IOException;

/**
 * @author Francesco Giuseppe Zammarrelli
 * La classe Login admin servlet.
 */
@WebServlet(name = "LoginAdminServlet",value = "/login-admin")
public class LoginAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("login-admin-page");
    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Utente u = (Utente) session.getAttribute("utente");
        Admin admin = (Admin) session.getAttribute("admin");
        if (u == null && admin == null) {
            String email =  request.getParameter("email");
            String password =  request.getParameter("password");
            AdminLogicService service =  new AdminLogicImpl();
            try {
                admin = service.loginAdmin(email,password);

                session.setAttribute("admin",admin);

                response.sendRedirect("index.html");
            } catch (IllegalArgumentException e) {
                String message =  e.getMessage();

                request.setAttribute("error-message",message);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/gestioneUtente/admin/login_admin.jsp");

                dispatcher.forward(request,response);
            }
        } else {
            response.sendRedirect("index.html");
        }
    }
}
