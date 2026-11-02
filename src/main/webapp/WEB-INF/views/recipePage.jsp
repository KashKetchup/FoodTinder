<%@ page import="tinderBack.FoodTinder" %>
<%@ page import="tinderBack.Recipe" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
    <head>
        <meta charset="UTF-8">
        <title>Рецепт</title>
    </head>

    <body>

        <article class="recipe1">
            
            <% 
                if (request.getAttribute("recipe") != null) {
                    Recipe recipe = (Recipe)request.getAttribute("recipe");
                    out.println("<h1>" + recipe.name() + "</h1>");
                    
                }
            %>


            <!-- фото блюда -->
    
            <img
                src=${recipe.imagePath()}
                alt="${recipe.name()} - img"
            >


            <!-- текст рецепта -->
            <section class="recipe-text">

                <p>
                    ${recipe.instruction()}
                </p>

            </section>


            <!-- оценки/лайки -->
            <div class="rating">
                Оценки/лайки: ${recipe.rate()}
            </div>


        </article>

    </body>
</html>