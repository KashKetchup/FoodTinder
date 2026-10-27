import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom'; // Добавили useLocation
import './HomeScreen.css';

const fetchRecipes = async () => {
  return [
    {
      id: 1,
      title: "Борщ классический",
      description: "Наваристый суп со свеклой, капустой и сметаной. Настоящая классика.",
      image_url: "https://images.unsplash.com/photo-1547592180-85f173990554?w=500&auto=format&fit=crop&q=60"
    },
    {
      id: 2,
      title: "Паста Карбонара",
      description: "Итальянская паста с беконом, яйцом и сыром пармезан. Готовится за 15 минут.",
      image_url: "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500&auto=format&fit=crop&q=60"
    },
    {
      id: 3,
      title: "Панкейки с ягодами",
      description: "Пышные американские блинчики с кленовым сиропом и свежей клубникой.",
      image_url: "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=500&auto=format&fit=crop&q=60"
    }
  ];
};

export default function HomeScreen() {
  const [recipes, setRecipes] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  
  const navigate = useNavigate();
  const location = useLocation(); // Получаем текущий путь (например, '/')

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
    navigate(`/recipe/${recipe.id}`);
  };

  // Универсальный обработчик навигации с проверкой текущего пути
  const handleNavClick = (path: string) => {
    if (location.pathname !== path) {
      navigate(path);
    }
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
        <h1 className="header-title">Вкусные Рецепты</h1>
      </header>

      {/* Список рецептов */}
      <main className="recipe-list">
        {recipes.map((recipe) => (
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
        ))}
      </main>

      {/* Нижняя панель навигации (Absolute/Fixed) */}
      <nav className="bottom-nav">
        <button 
          className={`nav-btn ${location.pathname === '/' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/')}
        >
          <span className="nav-icon">🏠</span>
          <span>Home</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/search' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/search')}
        >
          <span className="nav-icon">🔍</span>
          <span>Search</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/favorites' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/favorites')}
        >
          <span className="nav-icon">❤️</span>
          <span>Favorites</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/login' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/login')}
        >
          <span className="nav-icon">👤</span>
          <span>Login</span>
        </button>
      </nav>
    </div>
  );
}