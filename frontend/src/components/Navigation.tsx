import { useNavigate, useLocation } from 'react-router-dom';
import './Navigation.css';

export function Navigation() {
    const location = useLocation();
    const navigate = useNavigate();

    // Универсальный обработчик навигации с проверкой текущего пути
    const handleNavClick = (path: string) => {
        if (location.pathname !== path) {
            navigate(path);
        }
    };
    return (
      <nav className="bottom-nav">
        <button 
          className={`nav-btn ${location.pathname === '/' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/')}
        >
          <span className="nav-icon">🏠</span>
          <span>Home</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/recipes' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/recipes')}
        >
          <span className="nav-icon">🍕</span>
          <span>Search</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/diets' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/diets')}
        >
          <span className="nav-icon">🍽️</span>
          <span>Search</span>
        </button>

        <button 
          className={`nav-btn ${location.pathname === '/favorites' ? 'active' : ''}`} 
          onClick={() => handleNavClick('/login')}
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
    );
}