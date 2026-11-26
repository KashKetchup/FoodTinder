import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Navigation } from '../components/Navigation.tsx';
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
      <Navigation />
    </div>
  );
}