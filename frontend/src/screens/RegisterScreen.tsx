import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './RegisterScreen.css';

export default function RegisterScreen() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Регистрация
  const handleRegister = () => {
    console.log('Register attempt:', { email, password });
    // Здесь будет логика регистрации
    alert('Функция регистрации будет реализована позже');
  };

  // Переход на страницу входа
  const handleGoToLogin = () => {
    navigate('/login');
  };

  const handleNavClick = (path: string) => {
    if (location.pathname !== path) {
      navigate(path);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h1 className="auth-title">Регистрация</h1>
        
        <div className="input-group">
          <input
            type="email"
            className="auth-input"
            placeholder="Почта"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="input-group">
          <input
            type="password"
            className="auth-input"
            placeholder="Пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <button className="auth-button register-button" onClick={handleRegister}>
          Зарегистрироваться
        </button>

        <div className="auth-links">
          <button className="auth-link" onClick={handleGoToLogin}>
            Уже есть аккаунт? Войти
          </button>
        </div>
      </div>
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