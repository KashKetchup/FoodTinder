import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Recipe } from '../types/Recipe.tsx';
import { Navigation } from '../components/Navigation.tsx';
import './RecipeDetailScreen.css';

export default function RecipeDetailScreen() {
  const navigate = useNavigate();
  
  // ТИПИЗИРУЕМ СОСТОЯНИЕ (STATE)
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchRecipe = async () => {
    fetch(`/backend/recipe?id=${new URLSearchParams(window.location.search).get("id") || "null"}`)
      .then((response) => {
        return response.json();
      })
      .then((data: Recipe) => {
        setRecipe(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Ошибка загрузки рецепта:', error);
        setLoading(false);
      });
  }

  useEffect(() => {
    fetchRecipe();
  }, []);

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
        <button className="back-button" onClick={() => navigate('/recipes')}>
          ← Назад
        </button>
        <Navigation />
      </div>
    );
  }

  // Основной рендер
  return (
    <div className="container recipe-detail">
      <button className="back-button" onClick={() => navigate('/recipes')}>
        ← Назад
      </button>
      
      <img src={recipe.image_url} alt={recipe.title} className="detail-image" />
      <h1 className="detail-title">{recipe.title}</h1>
      <p className="detail-description">{recipe.description}</p>
      <Navigation />
    </div>
  );
}