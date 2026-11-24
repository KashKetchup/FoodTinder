import { BrowserRouter, Routes, Route } from 'react-router-dom';
import HomeScreen from './screens/HomeScreen';
import RecipesScreen from './screens/RecipesScreen';
import RecipeDetailScreen from './screens/RecipeDetailScreen';
import DietsScreen from './screens/DietsScreen';
import DietDetailScreen from './screens/DietDetailScreen';
import SearchScreen from './screens/SearchScreen';
import LoginScreen from './screens/LoginScreen';
import RegisterScreen from './screens/RegisterScreen';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomeScreen />} />
        <Route path="/recipes" element={<RecipesScreen />} />
        <Route path="/recipe" element={<RecipeDetailScreen />} />
        <Route path="/diets" element={<DietsScreen />} />
        <Route path="/diet" element={<DietDetailScreen />} />
        <Route path="/search" element={<SearchScreen />} />
        <Route path="/login" element={<LoginScreen />} />
        <Route path="/register" element={<RegisterScreen />} />
        {/* Страница не найдена*/}
        <Route path="*" element={<div style={{padding: '20px', textAlign: 'center'}}>Страница не найдена (404)</div>} />
      
      </Routes>
    </BrowserRouter>
  );
}