<%@ page import="tinderBack.FoodTinder" %>
<%@ page import="tinderBack.Recipe" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Главная</title>
    </head>

    <body>

        <main>

            <!-- TEMPLATE заголовок -->
            <h1>Какой-то заголовок</h1>

            <section class="recipes">

                <%
                    if (request.getAttribute("recipes") != null) {
                        List<Recipe> recipes = (List<Recipe>) request.getAttribute("recipes");
                        out.println("<ui>");
                        for (Recipe recipe : recipes) {
                            out.println("<li>");
                                out.println("<a href=\"recipe?id=" + recipe.recipeId() + "\">Ссылка</a>");
                                out.println("<img src=" + recipe.imagePath() + " alt=" + recipe.name() + ">");
                                out.println("<h2>" + recipe.name() + "</h2>");
                                out.println("<div class=\"recipe-info\">");
                                    out.println("<p>" + recipe.description() + "</p>");
                                    out.println("<span>Рейтинг: " + recipe.rate() + "</span>");
                                out.println("</div>");
                            out.println("</li>");

                        }
                        out.println("</ui>");
                    } else {
                        out.println("<h1>Нет подходящих рецептов</h1>");
                    }
                            
                %>


            </section>

        </main>

        <footer>

            <a href="/">Home</a>
            <a href="/search">Search</a>
            <a href="/bookmarks">Закладки</a>
            <a href="/profile">Profile</a>

        </footer>

    </body>
</html>