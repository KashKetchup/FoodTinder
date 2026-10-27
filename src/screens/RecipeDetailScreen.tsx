import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './RecipeDetailScreen.css';

// ИНТЕРФЕЙС
interface Recipe {
  id: number;
  title: string;
  description: string;
  image_url: string;
}

export default function RecipeDetailScreen() {
  // ТИПИЗИРУЕМ ПАРАМЕТРЫ URL
  const { id } = useParams<{ id: string }>();
  
  const navigate = useNavigate();
  
  // ТИПИЗИРУЕМ СОСТОЯНИЕ (STATE)
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        // Тип массива указан явно
        const mockRecipes: Recipe[] = [
          { id: 1, title: "Борщ классический", description: "Наваристый суп со свеклой, капустой и сметаной. Настоящая классика. Ингредиенты: свекла, капуста, мясо, морковь, лук.", image_url: "https://images.unsplash.com/photo-1547592180-85f173990554?w=500&auto=format&fit=crop&q=60" },
          { id: 2, title: "Паста Карбонара", description: "Итальянская паста с беконом, яйцом и сыром пармезан. Ингредиенты: спагетти, гуанчиале, яйца, пекорино романо.", image_url: "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500&auto=format&fit=crop&q=60" },
          { id: 3, title: "Панкейки с ягодами", description: "Пышные американские блинчики с кленовым сиропом и свежей клубникой. Ингредиенты: мука, молоко, яйца, разрыхлитель, ягоды.", image_url: "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=500&auto=format&fit=crop&q=60" }
        ];
        
        const foundRecipe = mockRecipes.find(r => r.id === Number(id)) || null;
        
        setRecipe(foundRecipe);
      } catch (error) {
        console.error('Ошибка загрузки рецепта:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRecipe();
  }, [id]);

  // Условный рендеринг (загрузка)
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  // Условный рендеринг (ошибка, если рецепт не найден)
  if (!recipe) {
    return (
      <div className="container">
        <h2>Рецепт не найден</h2>
        <button className="back-button" onClick={() => navigate(-1)}>
          ← Назад к списку
        </button>
      </div>
    );
  }

  // Основной рендер
  return (
    <div className="container recipe-detail">
      <button className="back-button" onClick={() => navigate(-1)}>
        ← Назад к списку
      </button>
      
      <img src={recipe.image_url} alt={recipe.title} className="detail-image" />
      <h1 className="detail-title">{recipe.title}</h1>
      <p className="detail-description">{recipe.description}</p>
    </div>
  );
}