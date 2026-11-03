import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom'; // Добавили useLocation
import './HomeScreen.css';

// ИНТЕРФЕЙС
interface Recipe {
  id: number;
  name: string;
  description: string;
  instruction: string;
  ingredients: Object;
  tags: Object;
  image_url: string;
  cookingTime: number;
  rate: number;
  calories: number;
  difficulty: number;
}

export default function HomeScreen() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchRecipes = async () => {
    return fetch(`/backend/main`)
        .then((response) => {
          setLoading(false);
          return response.json();
        })
        .catch((error) => {
          console.error('Ошибка загрузки рецептов:', error);
          setLoading(false);
        });
  };

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
    navigate(`/recipe?id=${recipe.id}`);
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
              alt={recipe.name}
              className="recipe-image"
            />
            <div className="recipe-info">
              <h2 className="recipe-title">{recipe.name}</h2>
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