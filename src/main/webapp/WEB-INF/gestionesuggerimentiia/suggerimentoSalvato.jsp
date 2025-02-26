<%--
  Created by IntelliJ IDEA.
  User: raffaele aurucci
--%>
<%@ page import="weatherstyle.gestionesuggerimentiia.applicationlogic.logic.beans.Outfit" %>
<%@ page import="weatherstyle.gestioneguardaroba.applicationlogic.logic.beans.Maglia" %>
<%@ page import="weatherstyle.gestioneguardaroba.applicationlogic.logic.beans.Pantaloni" %>
<%@ page import="weatherstyle.gestioneguardaroba.applicationlogic.logic.beans.Scarpe" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Suggerimento salvato</title>
    <%@include file="../links.jsp"%>
</head>
<body>
<%@include file="../navbar.jsp"%>
<br>
<% String suggerimento = (String) request.getAttribute("suggerimentoSalvato");
   Outfit outfit = (Outfit) request.getAttribute("outfit");
   Maglia maglia = outfit.getMaglia();
   Pantaloni pantaloni = outfit.getPantaloni();
   Scarpe scarpe = outfit.getScarpe();
%>
<div class="container">
    <div class="alert alert-success" role="alert">
        <%=suggerimento%><br>
    </div>

    <br>

    <h4 style="text-align: center"><%=outfit.getNome()%></h4>

    <div class="row justify-content-center">
        <div class="card col-align-self-center" style="width: 18rem;">
            <img src="<%=maglia.getDirImmagine()%>" class="card-img-top rounded mx-auto d-block vestiti">
        </div>

        <div class="card col-align-self-center" style="width: 18rem;">
            <img src="<%=pantaloni.getDirImmagine()%>" class="card-img-top rounded mx-auto d-block vestiti">
        </div>

        <div class="card col-align-self-center" style="width: 18rem;">
            <img src="<%=scarpe.getDirImmagine()%>" class="card-img-top rounded mx-auto d-block vestiti">
        </div>
    </div>

</div>
<%@include file="../footer.jsp"%>
</body>
</html>
