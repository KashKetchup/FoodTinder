import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginScreen.css';

export default function LoginScreen() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Вход в акаунт
  const handleLogin = () => {
    console.log('Login attempt:', { email, password });
    // Здесь будет логика входа
    alert('Функция входа будет реализована позже');
  };

  // Переход на страницу регистрации
  const handleGoToRegister = () => {
    navigate('/register');
  };

  // Восстановление пароля
  const handleForgotPassword = () => {
    alert('Функция восстановления пароля будет реализована позже');
  };

  const handleNavClick = (path: string) => {
    if (location.pathname !== path) {
      navigate(path);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h1 className="auth-title">Вход</h1>
        
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

        <button className="auth-button login-button" onClick={handleLogin}>
          Войти
        </button>

        <div className="auth-links">
          <button className="auth-link" onClick={handleGoToRegister}>
            Зарегистрироваться
          </button>
          
          <button className="auth-link forgot-password" onClick={handleForgotPassword}>
            Забыли пароль?
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