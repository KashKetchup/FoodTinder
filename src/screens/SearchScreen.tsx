import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './SearchScreen.css';

// При добавлении базы реализовать Автодополнение с задержкой (Debounced Autocomplete) для выпадающих тегов/ингридиентов

export default function SearchScreen() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  
  // Состояния для полей ввода
  const [blackInput, setBlackInput] = useState('');
  const [whiteInput, setWhiteInput] = useState('');
  
  // Состояния для списков (инициализируем из URL параметров)
  const [blackList, setBlackList] = useState<string[]>(() => {
    const black = searchParams.get('black');
    return black ? black.split(',').filter(Boolean) : [];
  });
  
  const [whiteList, setWhiteList] = useState<string[]>(() => {
    const white = searchParams.get('white');
    return white ? white.split(',').filter(Boolean) : [];
  });

  // Обновляем URL параметры при изменении списков
  useEffect(() => {
    const params = new URLSearchParams();
    if (blackList.length > 0) params.set('black', blackList.join(','));
    if (whiteList.length > 0) params.set('white', whiteList.join(','));
    setSearchParams(params);
  }, [blackList, whiteList, setSearchParams]);

  // Добавление тега в black list
  const handleBlackAdd = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && blackInput.trim()) {
      const tag = blackInput.trim().toLowerCase();
      if (!blackList.includes(tag)) {
        setBlackList([...blackList, tag]);
      }
      setBlackInput('');
    }
  };

  // Добавление тега в white list
  const handleWhiteAdd = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && whiteInput.trim()) {
      const tag = whiteInput.trim().toLowerCase();
      if (!whiteList.includes(tag)) {
        setWhiteList([...whiteList, tag]);
      }
      setWhiteInput('');
    }
  };

  // Удаление тега из black list
  const handleBlackRemove = (tagToRemove: string) => {
    setBlackList(blackList.filter(tag => tag !== tagToRemove));
  };

  // Удаление тега из white list
  const handleWhiteRemove = (tagToRemove: string) => {
    setWhiteList(whiteList.filter(tag => tag !== tagToRemove));
  };

  // Кнопка поиска — переход на главную с фильтрами
  const handleSearch = () => {
    navigate('/');
  };

  // Очистка всех фильтров
  const handleClearAll = () => {
    setBlackList([]);
    setWhiteList([]);
    setSearchParams({}); // Очищаем URL
  };

  return (
    <div className="search-container">
      {/* Заголовок */}
      <header className="search-header">
        <h1>Фильтр рецептов</h1>
      </header>

      {/* BLACK LIST */}
      <section className="filter-section black-list-section">
        <h2 className="section-title">Black List</h2>
        <p className="section-description">
          Ингредиенты, которые НЕ должны быть в рецепте
        </p>
        
        <input
          type="text"
          className="filter-input"
          placeholder="Введите ингредиент и нажмите Enter"
          value={blackInput}
          onChange={(e) => setBlackInput(e.target.value)}
          onKeyDown={handleBlackAdd}
        />
        
        <div className="tags-container">
          {blackList.map((tag) => (
            <span key={tag} className="tag tag-black">
              {tag}
              <button 
                className="tag-remove" 
                onClick={() => handleBlackRemove(tag)}
              >
                ×
              </button>
            </span>
          ))}
          {blackList.length === 0 && (
            <span className="empty-message">Нет запрещенных ингредиентов</span>
          )}
        </div>
      </section>

      {/* WHITE LIST */}
      <section className="filter-section white-list-section">
        <h2 className="section-title">White List</h2>
        <p className="section-description">
          Обязательные ингредиенты (хотя бы один должен быть)
        </p>
        
        <input
          type="text"
          className="filter-input"
          placeholder="Введите ингредиент и нажмите Enter"
          value={whiteInput}
          onChange={(e) => setWhiteInput(e.target.value)}
          onKeyDown={handleWhiteAdd}
        />
        
        <div className="tags-container">
          {whiteList.map((tag) => (
            <span key={tag} className="tag tag-white">
              {tag}
              <button 
                className="tag-remove" 
                onClick={() => handleWhiteRemove(tag)}
              >
                ×
              </button>
            </span>
          ))}
          {whiteList.length === 0 && (
            <span className="empty-message">Нет обязательных ингредиентов</span>
          )}
        </div>
      </section>

      
      <div className="action-buttons">
        <button className="btn-clear" onClick={handleClearAll}>
          🗑️ Очистить всё
        </button>
        
        <button className="btn-search" onClick={handleSearch}>
          🔍 Поиск рецептов
        </button>
      </div>

      {/* Нижняя навигация */}
      <nav className="bottom-nav">
        <button className="nav-btn" onClick={() => navigate('/')}>
          <span className="nav-icon">🏠</span>
          <span>Home</span>
        </button>
        <button className="nav-btn active" onClick={() => navigate('/search')}>
          <span className="nav-icon">🔍</span>
          <span>Search</span>
        </button>
        <button className="nav-btn" onClick={() => navigate('/favorites')}>
          <span className="nav-icon">❤️</span>
          <span>Favorites</span>
        </button>
        <button className="nav-btn" onClick={() => navigate('/login')}>
          <span className="nav-icon">👤</span>
          <span>Login</span>
        </button>
      </nav>
    </div>
  );
}