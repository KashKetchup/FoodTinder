import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import type { Diet } from '../types/Diet.tsx';
import { Navigation } from '../components/Navigation.tsx';
import './DietsScreen.css';
import './SearchScreen.css';
import { Filters } from '../components/Filters.tsx';

export default function RecipesScreen() {
  const navigate = useNavigate();

  const [diet, setDiet] = useState<Diet | null>(null);
  const [loading, setLoading] = useState(false);
  const [filterOpen, setFilterOpen] = useState(true);
  
  const [searchParams, setSearchParams] = useSearchParams();

  const fetchDiet = async () => {
    return fetch(`/backend/diets?in_ingr=${searchParams.get("include_ingredients") || ""}` +
                                  `&ex_ingr=${searchParams.get("exclude_ingredients") || ""}` + 
                                  `&in_tag=${searchParams.get("include_tags") || ""}` + 
                                  `&ex_tag=${searchParams.get("exclude_tags") || "" }` +
                                  `&days_count=${searchParams.get("days_count") || "1"}`)
        .then((response) => {
          setLoading(false);
          return response.json();
        })
        .catch((error) => {
          console.error('Ошибка загрузки рецептов:', error);
          setLoading(false);
        });
  };

  const loadData = async () => {
    try {
      const data = await fetchDiet();
      setDiet(data);
    } catch (error) {
      console.error('Ошибка загрузки рецептов:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  if (filterOpen) {
      return <Filters
        forDiets={true}
        onApply={() => {
            setFilterOpen(false);
            loadData();
        }}/>;
  }

  return (
    <div className="container">
      {/* Заголовок по центру */}
      <header className="header">
        <h1 className="header-title">Меню</h1>
      </header>
      <button className="btn-filter" onClick={() => setFilterOpen(true)}>
        Фильтры
      </button>

      {/* Список рецептов */}
      <main className="recipe-list">
        {diet === null ? (
          <p>
            Нет меню, удовлетворяющих фильтрам
          </p>
        ) : (
          <div>
            <img
              src={diet.breakfast.image_url}
              alt={diet.breakfast.title}
              className="recipe-image"
            />
            <div className="recipe-info">
              <h2 className="recipe-title">{diet.breakfast.title}</h2>
              <p className="recipe-description">{diet.breakfast.description}</p>
            </div>
            <img
              src={diet.lunch.image_url}
              alt={diet.lunch.title}
              className="recipe-image"
            />
            <div className="recipe-info">
              <h2 className="recipe-title">{diet.lunch.title}</h2>
              <p className="recipe-description">{diet.lunch.description}</p>
            </div>
            <img
              src={diet.dinner.image_url}
              alt={diet.dinner.title}
              className="recipe-image"
            />
            <div className="recipe-info">
              <h2 className="recipe-title">{diet.dinner.title}</h2>
              <p className="recipe-description">{diet.dinner.description}</p>
            </div>
          </div>
        )}
      </main>
      <Navigation />
    </div>
  );
}