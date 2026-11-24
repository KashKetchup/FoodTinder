import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import type { RecipeShort } from '../types/Recipe.tsx';
import { Navigation } from '../components/Navigation.tsx';
import './RecipesScreen.css';
import './SearchScreen.css';
import { Filters } from '../components/Filters.tsx';

export default function RecipesScreen() {
  const navigate = useNavigate();

  const [recipes, setRecipes] = useState<RecipeShort[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterOpen, setFilterOpen] = useState(false);
  
  const [searchParams, setSearchParams] = useSearchParams();

  const fetchRecipes = async () => {
    return fetch(`/backend/recipes?in_ingr=${searchParams.get("include_ingredients") || ""}` +
                                  `&ex_ingr=${searchParams.get("exclude_ingredients") || ""}` + 
                                  `&in_tag=${searchParams.get("include_tags") || ""}` + 
                                  `&ex_tag=${searchParams.get("exclude_tags") || ""}`)
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
      setRecipes(data);
    } catch (error) {
      console.error('Ошибка загрузки рецептов:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRecipePress = (recipe: any) => {
    navigate(`/recipe?id=${recipe.id}`);
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
        forDiets={false}
        onApply={() => {
            setFilterOpen(false);
            loadData();
        }}/>;
  }

  return (
    <div className="container">
      {/* Заголовок по центру */}
      <header className="header">
        <h1 className="header-title">Вкусные Рецепты</h1>
      </header>
      <button className="btn-filter" onClick={() => setFilterOpen(true)}>
        Фильтры
      </button>

      {/* Список рецептов */}
      <main className="recipe-list">
        {recipes.length === 0 ? (
          <p>
            Нет рецептов, удовлетворяющих фильтрам
          </p>
        ) : (recipes.map((recipe) => (
          <div
            key={recipe.id}
            className="recipe-block"
            onClick={() => handleRecipePress(recipe)}
          >
            <img
              src={recipe.image_url}
              alt={recipe.title}
              className="recipe-image"
            />
            <div className="recipe-info">
              <h2 className="recipe-title">{recipe.title}</h2>
              <p className="recipe-description">{recipe.description}</p>
            </div>
          </div>
        )))}
      </main>
      <Navigation />
    </div>
  );
}