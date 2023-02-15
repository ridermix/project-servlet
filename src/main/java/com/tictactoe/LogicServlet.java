package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends javax.servlet.http.HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession();
        Field field = extractField(currentSession);
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);
        if(Sign.EMPTY != currentSign){
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        field.getField().put(index,Sign.CROSS);
        if(checkWin(resp, currentSession, field)){
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if(emptyFieldIndex >= 0){
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if(checkWin(resp, currentSession, field)){
                return;
            }
        } else {
            currentSession.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }
        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);
        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request){
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession){
        Object fieldAttribute = currentSession.getAttribute("field");
            if(Field.class != fieldAttribute.getClass()){
                currentSession.invalidate();
                throw new RuntimeException("Session is broken, try one more time");
            }
        return (Field) fieldAttribute;
    }
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if(Sign.CROSS == winner || Sign.NOUGHT == winner){
            currentSession.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
