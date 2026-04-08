package com.ejemplo.servlet;

import com.ejemplo.model.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet principal que maneja las peticiones GET y POST
 * para la gestión de tareas en memoria.
 * Implementa el patrón Post/Redirect/Get (PRG).
 */
@WebServlet(name = "TareasServlet", urlPatterns = { "/tareas" })
public class TareasServlet extends HttpServlet {

    // Lista en memoria (persiste mientras Tomcat esté corriendo)
    private final List<Tarea> tareas = new ArrayList<>();
    private int contadorId = 1;

    @Override
    public void init() throws ServletException {
        // Datos de ejemplo al iniciar la aplicación
        tareas.add(new Tarea(contadorId++, "Leer documentación de Servlets"));
        tareas.add(new Tarea(contadorId++, "Implementar ciclo GET/POST"));
    }

    /**
     * GET /tareas — carga la lista y la envía a la vista JSP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("tareas", tareas);
        req.getRequestDispatcher("/WEB-INF/views/tareas.jsp")
                .forward(req, resp);
    }

    /**
     * POST /tareas — procesa agregar o eliminar una tarea
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        if ("agregar".equals(accion)) {
            String titulo = req.getParameter("titulo");
            // Validación en el servidor
            if (titulo == null || titulo.isBlank()) {
                req.setAttribute("error", "El título no puede estar vacío");
                req.setAttribute("tareas", tareas);
                req.getRequestDispatcher("/WEB-INF/views/tareas.jsp")
                        .forward(req, resp);
                return;
            }
            tareas.add(new Tarea(contadorId++, titulo.trim()));

        } else if ("eliminar".equals(accion)) {
            int id = Integer.parseInt(req.getParameter("id"));
            tareas.removeIf(t -> t.getId() == id);
        }

        // Patrón PRG: siempre redirigir después de un POST exitoso
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }
}