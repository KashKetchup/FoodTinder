import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Diet } from '../types/Diet.tsx';
import type { Recipe } from '../types/Recipe.tsx';
import { Navigation } from '../components/Navigation.tsx';
import './DietDetailScreen.css';

export default function DietDetailScreen() {
  const navigate = useNavigate();
  
  // ТИПИЗИРУЕМ СОСТОЯНИЕ (STATE)
  const [diet, setDiet] = useState<Diet | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchRecipe = async () => {
    fetch(`/backend/diet?id=${new URLSearchParams(window.location.search).get("id") || "null"}`)
      .then((response) => {
        return response.json();
      })
      .then((data: Diet) => {
        setDiet(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Ошибка загрузки рациона:', error);
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
  if (!diet) {
    return (
      <div className="container">
        <h2>Рацион не найден</h2>
        <button className="back-button" onClick={() => navigate('/diets')}>
          ← Назад
        </button>
        <Navigation />
      </div>
    );
  }

  // Основной рендер
  return (
    <div className="container recipe-detail">
      <button className="back-button" onClick={() => navigate('/diets')}>
        ← Назад
      </button>
      
      <p className="detail-description">{diet.description}</p>
      <Navigation />
    </div>
  );
}