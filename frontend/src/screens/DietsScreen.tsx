import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Diet } from '../types/Diet.tsx';
import type { Recipe } from '../types/Recipe.tsx';
import { Navigation } from '../components/Navigation.tsx';
import './DietsScreen.css';

export default function DietsScreen() {
  const navigate = useNavigate();

  const [diets, setDiets] = useState<Diet[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchRecipes = async () => {
    return fetch(`/backend/diets`)
        .then((response) => {
          setLoading(false);
          return response.json();
        })
        .catch((error) => {
          console.error('Ошибка загрузки рецептов:', error);
          setLoading(false);
        });
  };

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const data = await fetchRecipes();
      setDiets(data);
    } catch (error) {
      console.error('Ошибка загрузки рецептов:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRecipePress = (diet: any) => {
    navigate(`/diet?id=${diet.id}`);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="container">
      {/* Заголовок по центру */}
      <header className="header">
        <h1 className="header-title">Рационы на день</h1>
      </header>

      {/* Список рецептов */}
      <main className="recipe-list">
        {diets.map((diet) => (
          <div
            key={diet.id}
            className="recipe-block"
            onClick={() => handleRecipePress(diet)}
          >
            <div className="recipe-info">
              <p className="recipe-description">{diet.description}</p>
            </div>
          </div>
        ))}
      </main>
      <Navigation />
    </div>
  );
}