package weatherstyle.gestioneambiente.applicationlogic.control;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import weatherstyle.gestioneambiente.applicationlogic.logic.beans.Evento;
import weatherstyle.gestioneambiente.applicationlogic.logic.service.EventoLogicImpl;
import weatherstyle.gestioneambiente.applicationlogic.logic.service.EventoLogicInterface;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "VisualizzaEventiServlet", value = "/VisualizzaEventiServlet")
public class VisualizzaEventiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EventoLogicInterface eventoLogic = new EventoLogicImpl();
        List<Evento> listaEventi = eventoLogic.ottieniListaEventiFuturi();
        request.setAttribute("listaEventi", listaEventi);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/gestioneambiente/visualizzaEventi.jsp");
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}